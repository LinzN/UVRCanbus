/*
 * Copyright (C) 2020. Niklas Linz - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the LGPLv3 license, which unfortunately won't be
 * written for another century.
 *
 * You should have received a copy of the LGPLv3 license with
 * this file. If not, please write to: niklas.linz@enigmar.de
 *
 */

package de.linzn.uvrCanbus;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class ReadCanData {

    public int errorCounts = 0;

    public JSONObject readGoApplication() {

        ProcessBuilder processBuilder = new ProcessBuilder();
        File uvrjson = new File("uvrjson_armv6");
        String[] cmd = {"/bin/sh", "-c", uvrjson.getAbsolutePath()};

        processBuilder.command(cmd);

        try {
            Process process = processBuilder.start();

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            if(!process.waitFor(40, TimeUnit.SECONDS)){
                System.out.println("Killing process");
                process.destroyForcibly();
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                System.out.println("Can error! No valid output.");
                errorCounts++;
                return null;
            } else {
                return new JSONObject(stringBuilder.toString());
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            errorCounts++;
            return null;
        }
    }

}
