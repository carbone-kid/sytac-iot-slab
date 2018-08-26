package com.sfirsov.sytac.iot.slab.service;

import com.sfirsov.sytac.iot.slab.device.LED;
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

    @Value("${rpi.pin.led}")
    private int ledPin;

    private String ubidotsToken;

    private RestTemplate restTemplate = new RestTemplate();

    private LED LED;

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

        if(LED == null) {
            try {
                LED = new LED(ledPin);
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

    private String getLedState() {
        String retval = null;

        String getMotorEngagedUrl = "http://things.ubidots.com/api/v1.6/devices/LED/state/lv?token=" + ubidotsToken;
        HttpEntity<Integer> motorEngaged = restTemplate.getForEntity(getMotorEngagedUrl, Integer.class);
        System.out.println(motorEngaged.getBody());
        //motorState.setEngaged(motorEngaged.getBody() > 0);
        retval = motorEngaged.getBody()>0? "On" : "Off";
        return retval;
    }

    @Scheduled(initialDelay = 10000, fixedRate = 500)
    private void fetchData() {
        try {
            initIfNot();

            String ledState = getLedState();
            System.out.println("LED 1 state is: " + ledState);

            LED.go(ledState);
        }
        catch (Exception e) {
            // If something went wrong - reset Ubidots token and init RPi pins again
            ubidotsToken = null;
            LED = null;
            System.out.println(e.getMessage());
        }
    }
}
