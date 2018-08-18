package com.sfirsov.sytac.iot.slab.model;

import lombok.Data;

@Data
public class MotorState {
    private boolean engaged;
    private int direction;
}
