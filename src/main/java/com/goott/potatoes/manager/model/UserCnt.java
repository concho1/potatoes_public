package com.goott.potatoes.manager.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserCnt {
    private Timestamp date;
    private int userCount;
    private int guestCount;
    private int joinCount;
}
