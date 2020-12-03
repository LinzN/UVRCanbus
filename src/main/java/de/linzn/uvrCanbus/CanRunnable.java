package de.linzn.uvrCanbus;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CanRunnable implements Runnable {


    @Override
    public void run() {
        System.out.println("Start reading canbus data...");
        ReadCanData readCanData = new ReadCanData();

        JSONObject jsonObject = readCanData.readGoApplication();
        if (jsonObject == null) {
            int retries = 0;

            while (jsonObject == null && retries <= 5) {
                jsonObject = readCanData.readGoApplication();
                retries++;
            }
            if (jsonObject == null) {
                System.out.println("Error after 5 retries to read canbus data. Aborting!");
                return;
            }
        }
        try {
            System.out.println("Finish reading canbus data!");
            sendData(jsonObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendData(JSONObject jsonObject) throws IOException {
        URL url = new URL("http://" + UVRCanbusApp.appConfigurationModule.stemHost + ":" + UVRCanbusApp.appConfigurationModule.stemPort + "/post_heater-canbus-data");
        System.out.println("Pushing data to " + url.toString());
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);

        byte[] out = jsonObject.toString().getBytes(StandardCharsets.UTF_8);
        int length = out.length;
        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        http.connect();
        try (OutputStream os = http.getOutputStream()) {
            os.write(out);
        }

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        JSONObject jsonResponse = new JSONObject(response.toString());
        boolean isValid = jsonResponse.getBoolean("valid");
        String data = new SimpleDateFormat("yyyyy.MMMMM.dd hh:mm aaa").format(new Date());
        System.out.println("Valid push: " + isValid);
        System.out.println("Date: " + data);
        System.out.println();
    }
}
