package de.linzn.uvrCanbus;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

public class CanRunnable implements Runnable {
    ReadCanData readCanData;

    public CanRunnable() {
        readCanData = new ReadCanData();
    }

    @Override
    public void run() {
        System.out.println("Start reading canbus data...");
        System.out.println("Global error counts: " + this.readCanData.errorCounts);

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
            sendDataMqtt(jsonObject);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void sendDataMqtt(JSONObject jsonObject) throws MqttException {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(jsonObject.toString().getBytes());
        mqttMessage.setQos(2);
        System.out.println("MQTT send data to uvr/canbus/data");
        UVRCanbusApp.UVRCanbusApp.mqttClient.publish("uvr/canbus/data", mqttMessage);
        System.out.println("MQTT published!");
        System.out.println("");
    }

}
