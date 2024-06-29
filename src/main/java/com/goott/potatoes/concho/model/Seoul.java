package com.goott.potatoes.concho.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Seoul {
    private String actId;
    private String status;
    private String productName;
    private String category;
    private Timestamp date;
    private String orgName;
    private String takePlace;
    private String cont;
}
