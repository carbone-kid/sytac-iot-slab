package com.sfirsov.sytac.iot.slab.device;

import com.pi4j.io.gpio.*;

import static com.pi4j.io.gpio.RaspiPin.getPinByAddress;

public class LED {
    private GpioPinDigitalOutput pin1;

    public LED(int pin1) {
        final GpioController gpio = GpioFactory.getInstance();

        unprovisionPinIfProvisioned(pin1);

        this.pin1 = gpio.provisionDigitalOutputPin(getPinByAddress(pin1), "LED pin " + pin1, PinState.LOW);
        this.pin1.setMode(PinMode.DIGITAL_OUTPUT);
    }

    private static void unprovisionPinIfProvisioned(int pinNumber) {
        final GpioController gpio = GpioFactory.getInstance();
        GpioPin provisionedPin = gpio.getProvisionedPin(getPinByAddress(pinNumber));
        if(provisionedPin != null) {
            gpio.unprovisionPin(provisionedPin);
        }
    }

    public void go(String state) {
        if(pin1 == null) {
            System.out.println("Raspberry Pi pins was not initialized.");
            return;
        }
        System.out.println("State: " + state);
        if(state.indexOf("On")!=-1){
            pin1.high();
        }else{
            pin1.low();
        }

    }
}
