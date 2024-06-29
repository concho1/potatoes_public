package com.goott.potatoes.concho.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicLost {
    private String idPlus;
    private String actId;
    private String gainNum;
    private String dept;
    private String imgUrl;
    private String name;
    private String content;
    private Timestamp date;
    private String majorCategory;
    private String minorCategory;
    private String tel;
    private String frontTel;
}
