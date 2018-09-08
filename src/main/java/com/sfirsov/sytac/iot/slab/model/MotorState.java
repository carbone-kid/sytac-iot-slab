package com.sfirsov.sytac.iot.slab.model;

import lombok.Data;

@Data
public class MotorState {
    private int engaged;
    private int direction;
}
