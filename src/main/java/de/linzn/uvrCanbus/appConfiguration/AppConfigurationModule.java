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

package de.linzn.uvrCanbus.appConfiguration;


import java.io.*;
import java.util.Properties;

public class AppConfigurationModule {

    /* Variables */
    public String stemHost;
    public int stemPort;
    public String mqttBroker;
    public String username;
    public String password;
    public String clientId;

    private String fileName = "settings.properties";

    /* Create class instance */
    public AppConfigurationModule() {
        this.init();
    }

    private static byte[] toByteArray(String string) {
        String[] strings = string.replace("[", "").replace("]", "").split(", ");
        byte[] result = new byte[strings.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = Byte.parseByte(strings[i]);
        }
        return result;
    }

    /* Create folders and files*/
    public void init() {
        File file = new File(this.fileName);
        if (!file.exists()) {
            create();
        }
        this.load();

    }

    /* Setup properies file with values */
    public void create() {

        Properties prop = new Properties();
        OutputStream output;

        try {

            output = new FileOutputStream(this.fileName);
            // set the properties value
            prop.setProperty("stemHost", "10.40.0.40");
            prop.setProperty("stemPort", "8081");
            prop.setProperty("mqttBroker", "tcp://10.50.0.1:1883");
            prop.setProperty("clientId", "canbus-uvr");
            prop.setProperty("username", "eveline");
            prop.setProperty("password", "test1234");

            // save properties to project root folder
            prop.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    /* Load the file in the memory */
    public void load() {

        Properties prop = new Properties();
        InputStream input;

        try {
            input = new FileInputStream(this.fileName);

            prop.load(input);

            this.stemHost = prop.getProperty("stemHost");
            this.stemPort = Integer.parseInt(prop.getProperty("stemPort"));

            this.mqttBroker = prop.getProperty("mqttBroker");
            this.clientId = prop.getProperty("clientId");
            this.username = prop.getProperty("username");
            this.password = prop.getProperty("password");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

}
