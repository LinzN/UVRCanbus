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
        UVRCanbusApp.LOGGER.INFO("Start reading canbus data...");
        UVRCanbusApp.LOGGER.INFO("Global error counts: " + this.readCanData.errorCounts);

        JSONObject jsonObject = readCanData.readGoApplication();
        if (jsonObject == null) {
            int retries = 0;

            while (jsonObject == null && retries <= 5) {
                jsonObject = readCanData.readGoApplication();
                retries++;
            }
            if (jsonObject == null) {
                UVRCanbusApp.LOGGER.ERROR("Error after 5 retries to read canbus data. Aborting!");
                return;
            }
        }
        try {
            UVRCanbusApp.LOGGER.INFO("Finish reading canbus data!");
            sendDataMqtt(jsonObject);
        } catch (MqttException e) {
            UVRCanbusApp.LOGGER.ERROR(e);
        }
    }

    private void sendDataMqtt(JSONObject jsonObject) throws MqttException {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(jsonObject.toString().getBytes());
        mqttMessage.setQos(2);
        publishMQTT("uvr/canbus/data", mqttMessage);
        ;
    }

    private void publishMQTT(String topic, MqttMessage mqttMessage) {
        try {
            UVRCanbusApp.LOGGER.INFO("MQTT send data to uvr/canbus/data");
            if (!UVRCanbusApp.UVRCanbusApp.mqttClient.isConnected()) {
                UVRCanbusApp.LOGGER.ERROR("IOBroker not connected. Trying to reconnect...");
                UVRCanbusApp.UVRCanbusApp.mqttClient.reconnect();
            }
            UVRCanbusApp.UVRCanbusApp.mqttClient.publish(topic, mqttMessage);
            UVRCanbusApp.LOGGER.INFO("MQTT published!");
        } catch (MqttException e) {
            UVRCanbusApp.LOGGER.ERROR(e);
            UVRCanbusApp.LOGGER.ERROR("Error while sending MQTT data!");
        }
        UVRCanbusApp.LOGGER.INFO("");
    }

}
