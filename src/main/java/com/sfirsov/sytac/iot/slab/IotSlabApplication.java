package com.sfirsov.sytac.iot.slab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IotSlabApplication {

    public static void main(String[] args) {
        SpringApplication.run(IotSlabApplication.class, args);
    }
}
