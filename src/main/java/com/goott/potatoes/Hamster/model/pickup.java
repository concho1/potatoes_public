package com.goott.potatoes.Hamster.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class pickup {
    private int pickNum;
    private String userId;
    private int categoryId;
    private String majorCategory;
    private String minorCategory;
    private String imgKey;
    private String title;
    private String cont;
    private String location;
    private int viewCnt;
    private Timestamp createdAt;
    private Timestamp updatedAt;

}
