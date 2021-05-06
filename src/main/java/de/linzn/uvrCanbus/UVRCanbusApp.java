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

import de.linzn.simplyLogger.LogSystem;
import de.linzn.simplyLogger.Logger;
import de.linzn.uvrCanbus.appConfiguration.AppConfigurationModule;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class UVRCanbusApp {
    public static UVRCanbusApp UVRCanbusApp;
    public static AppConfigurationModule appConfigurationModule;
    public static Logger LOGGER;
    public static LogSystem logSystem;

    public MqttClient mqttClient;

    public UVRCanbusApp() {
        while (!this.connectingBroker()) {
            LOGGER.ERROR("IOBroker connection failed! Trying again.");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }
        LOGGER.INFO("Starting canbus data parser!");
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(new CanRunnable(), 10, 180, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
        logSystem = new LogSystem("UVRCANBUS");
        //logSystem.setFileLogger(new File("logs"));
        LOGGER = logSystem.getLogger();
        LOGGER.INFO(UVRCanbusApp.class.getSimpleName() + " load app...");
        appConfigurationModule = new AppConfigurationModule();
        UVRCanbusApp = new UVRCanbusApp();
    }

    private boolean connectingBroker() {
        MemoryPersistence persistence = new MemoryPersistence();
        String broker = appConfigurationModule.mqttBroker;
        String clientId = appConfigurationModule.clientId;
        String user = appConfigurationModule.username;
        String password = appConfigurationModule.password;
        try {
            this.mqttClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setUserName(user);
            connOpts.setPassword(password.toCharArray());
            connOpts.setCleanSession(true);
            connOpts.setAutomaticReconnect(true);
            LOGGER.INFO("Connecting to IOBroker " + broker + "...");
            mqttClient.connect(connOpts);
            LOGGER.INFO("Connection to IOBroker is valid!");
            return true;
        } catch (MqttException e) {
            LOGGER.ERROR(e);
            return false;
        }
    }

}
