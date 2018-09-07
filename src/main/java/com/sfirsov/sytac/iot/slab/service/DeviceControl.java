package com.sfirsov.sytac.iot.slab.service;

import com.sfirsov.sytac.iot.slab.device.Motor;
import com.sfirsov.sytac.iot.slab.model.MotorState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class DeviceControl {

    @Value("${ubidots.api.key}")
    private String ubidotsApiKey;

    @Value("${rpi.pin.motor1.pin1}")
    private int motor1Pin1;

    @Value("${rpi.pin.motor1.pin2}")
    private int motor1Pin2;

    @Value("${rpi.pin.motor2.pin1}")
    private int motor2Pin1;

    @Value("${rpi.pin.motor2.pin2}")
    private int motor2Pin2;

    private String ubidotsToken;

    private RestTemplate restTemplate = new RestTemplate();

    private Motor motor1;
    private Motor motor2;

    private void initIfNot() {
        if(ubidotsToken == null) {
            try {
                ubidotsToken = getUbidotsToken();
            }
            catch (Exception e) {
                System.out.println("Cant obtain Ubidots token");
                return;
            }
        }

        if(motor1 == null || motor2 == null) {
            try {
                motor1 = new Motor(motor1Pin1, motor1Pin2);
                motor2 = new Motor(motor2Pin1, motor2Pin2);
            }
            catch (UnsatisfiedLinkError e) {
                System.out.println("Cant initialize Raspberry Pi GPIO");
            }
        }
    }

    private String getUbidotsToken() {
        String getTockenUrl = "http://things.ubidots.com/api/v1.6/auth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Ubidots-ApiKey", ubidotsApiKey);

        HttpEntity<String> request = new HttpEntity("", headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(getTockenUrl, request, Map.class);
        Map<String, String> body = response.getBody();
        return body.get("token");
    }

    private MotorState getMotorState(int motorNumber) {
        MotorState motorState = new MotorState();
        String getMotorEngagedUrl = "http://things.ubidots.com/api/v1.6/devices/wheel/wheel-" + motorNumber + "-engaged/lv?token=" + ubidotsToken;
        HttpEntity<Integer> motorEngaged = restTemplate.getForEntity(getMotorEngagedUrl, Integer.class);
        motorState.setEngaged(motorEngaged.getBody() > 0);

        return motorState;
    }

    @Scheduled(initialDelay = 10000, fixedRate = 500)
    private void fetchData() {
        try {
            initIfNot();

            MotorState motor1State = getMotorState(1);
            MotorState motor2State = getMotorState(2);
            System.out.println("Motor 1 engaged: " + motor1State.isEngaged());
            System.out.println("Motor 2 engaged: " + motor2State.isEngaged());

            motor1.go(motor1State);
            motor2.go(motor2State);
        }
        catch (Exception e) {
            // If something went wrong - reset Ubidots token and init RPi pins again
            ubidotsToken = null;
            motor1 = null;
            motor2 = null;
            System.out.println(e.getMessage());
        }
    }
}
