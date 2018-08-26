package com.sfirsov.sytac.iot.slab.device;

import com.pi4j.io.gpio.*;
import com.sfirsov.sytac.iot.slab.model.MotorState;

import static com.pi4j.io.gpio.RaspiPin.getPinByAddress;

public class Motor {
    private GpioPinDigitalOutput pin1;
    private GpioPinDigitalOutput pin2;

    public Motor(int pin1, int pin2) {
        final GpioController gpio = GpioFactory.getInstance();

        unprovisionPinIfProvisioned(pin1);
        unprovisionPinIfProvisioned(pin2);

        this.pin1 = gpio.provisionDigitalOutputPin(getPinByAddress(pin1), "Motor pin " + pin1, PinState.LOW);
        this.pin1.setMode(PinMode.DIGITAL_OUTPUT);

        this.pin2 = gpio.provisionDigitalOutputPin(getPinByAddress(pin2), "Motor pin " + pin2, PinState.LOW);
        this.pin2.setMode(PinMode.DIGITAL_OUTPUT);
    }

    private static void unprovisionPinIfProvisioned(int pinNumber) {
        final GpioController gpio = GpioFactory.getInstance();
        GpioPin provisionedPin = gpio.getProvisionedPin(getPinByAddress(pinNumber));
        if(provisionedPin != null) {
            gpio.unprovisionPin(provisionedPin);
        }
    }

    public void go(MotorState motorState) {
        if(pin1 == null || pin2 == null) {
            System.out.println("Raspberry Pi pins was not initialized.");
            return;
        }

        if(!motorState.isEngaged()) {
            pin1.low();
            pin2.low();
        }
        else {
            pin1.high();
            pin2.low();
        }
    }
}
