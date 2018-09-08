package com.sfirsov.sytac.iot.slab.device;

import com.pi4j.io.gpio.*;
import com.sfirsov.sytac.iot.slab.model.MotorState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.pi4j.io.gpio.RaspiPin.getPinByAddress;

public class Motor {
    private final List<Integer> ALL_PINS = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
    private List<GpioPinDigitalOutput> provisionedPins = new ArrayList<>();
    private GpioPinDigitalOutput pin1;
    private GpioPinDigitalOutput pin2;

    public Motor() {
        final GpioController gpio = GpioFactory.getInstance();
        ALL_PINS.forEach(Motor::unprovisionPinIfProvisioned);
        provisionedPins = ALL_PINS.stream()
                .map(p -> gpio.provisionDigitalOutputPin(getPinByAddress(p), "Motor pin " + p, PinState.LOW))
                .collect(Collectors.toList());

        provisionedPins.forEach(p -> p.setMode(PinMode.DIGITAL_OUTPUT));
    }

    private static void unprovisionPinIfProvisioned(int pinNumber) {
        final GpioController gpio = GpioFactory.getInstance();
        GpioPin provisionedPin = gpio.getProvisionedPin(getPinByAddress(pinNumber));
        if (provisionedPin != null) {
            gpio.unprovisionPin(provisionedPin);
        }
    }

    public void go(MotorState motorState) {
//        if (pin1 == null || pin2 == null) {
//            System.out.println("Raspberry Pi pins was not initialized.");
//            return;
//        }

        if (motorState.getEngaged() == 2) {
            provisionedPins.get(0).low();
            provisionedPins.get(1).high();
            provisionedPins.get(2).low();
            provisionedPins.get(3).high();

            provisionedPins.get(4).low();
            provisionedPins.get(5).high();
            provisionedPins.get(7).low();
            provisionedPins.get(6).high();


            if (motorState.getDirection() == 0) {
                provisionedPins.get(0).low();
                provisionedPins.get(1).high();
                provisionedPins.get(2).low();
                provisionedPins.get(3).low();

            } else if (motorState.getDirection() == 2) {
                provisionedPins.get(0).low();
                provisionedPins.get(1).low();
                provisionedPins.get(2).low();
                provisionedPins.get(3).high();
            } else {
                provisionedPins.get(0).low();
                provisionedPins.get(1).high();
                provisionedPins.get(2).low();
                provisionedPins.get(3).high();

                provisionedPins.get(4).low();
                provisionedPins.get(5).high();
                provisionedPins.get(7).low();
                provisionedPins.get(6).high();
            }

        } else if (motorState.getEngaged() == 0) {
            provisionedPins.get(0).high();
            provisionedPins.get(1).low();
            provisionedPins.get(2).high();
            provisionedPins.get(3).low();

            provisionedPins.get(4).high();
            provisionedPins.get(5).low();
            provisionedPins.get(7).high();
            provisionedPins.get(6).low();
        } else {
            provisionedPins.forEach(GpioPinDigitalOutput::low);
        }
    }
}
