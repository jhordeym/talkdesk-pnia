package com.talkdesk.pnia.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class BusinessSectorResponse implements Serializable {
    private String number;
    private String sector;
}
