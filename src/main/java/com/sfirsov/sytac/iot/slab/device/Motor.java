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
        if (pin1 == null || pin2 == null) {
            System.out.println("Raspberry Pi pins was not initialized.");
            return;
        }

        if (!motorState.isEngaged()) {
            pin1.low();
            pin2.low();
        } else {
            pin1.high();
            pin2.low();
        }
    }
}
