package com.goott.potatoes.police.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Police {

    private String policeKey;
    private String atcId;
    private String title;
    private String filePathImg;
    private String place;
    private Timestamp date;
    private String orgName;
    private String orgTel;
    private String productName;
    private String cont;
    private String fdSn;
    private String status;
    private String addr;
}
