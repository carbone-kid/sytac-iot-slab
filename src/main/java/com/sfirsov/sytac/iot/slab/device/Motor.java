package com.sfirsov.sytac.iot.slab.device;

import com.pi4j.io.gpio.*;
import com.sfirsov.sytac.iot.slab.model.MotorState;

import static com.pi4j.io.gpio.RaspiPin.getPinByAddress;

public class Motor {
    private GpioPinDigitalOutput engagePin;
    private GpioPinDigitalOutput directionPin;

    public Motor(int pinEngageNumber, int pinDirectionNumber) {
        final GpioController gpio = GpioFactory.getInstance();

        unprovisionPinIfProvisioned(pinEngageNumber);
        unprovisionPinIfProvisioned(pinDirectionNumber);

        engagePin = gpio.provisionDigitalOutputPin(getPinByAddress(pinEngageNumber), "Engage motor", PinState.LOW);
        engagePin.setMode(PinMode.DIGITAL_OUTPUT);

        directionPin = gpio.provisionDigitalOutputPin(getPinByAddress(pinDirectionNumber), "Motor direction", PinState.LOW);
        directionPin.setMode(PinMode.DIGITAL_OUTPUT);
    }

    private static void unprovisionPinIfProvisioned(int pinNumber) {
        final GpioController gpio = GpioFactory.getInstance();
        GpioPin provisionedPin = gpio.getProvisionedPin(getPinByAddress(pinNumber));
        if(provisionedPin != null) {
            gpio.unprovisionPin(provisionedPin);
        }
    }

    public void go(MotorState motorState) {
        if(engagePin == null || directionPin == null) {
            System.out.println("Raspberry Pi pins was not initialized.");
            return;
        }

        if(!motorState.isEngaged()) {
            engagePin.low();
        }
        else if(motorState.getDirection() <= 0) {
            engagePin.high();
            directionPin.low();
        }
        else {
            engagePin.high();
            directionPin.high();
        }
    }
}
