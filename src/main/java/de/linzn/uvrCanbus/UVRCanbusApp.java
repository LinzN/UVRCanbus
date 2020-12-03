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

import de.linzn.uvrCanbus.appConfiguration.AppConfigurationModule;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class UVRCanbusApp {
    public static UVRCanbusApp UVRCanbusApp;
    public static AppConfigurationModule appConfigurationModule;

    public UVRCanbusApp() {
        System.out.println("Starting canbus data parser!");
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new CanRunnable(), 10, 180, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
        appConfigurationModule = new AppConfigurationModule();
        UVRCanbusApp = new UVRCanbusApp();
    }

}
