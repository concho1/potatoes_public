package com.goott.potatoes.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConchoMessage {
    private int msgNo;
    private String roomName;
    private String sendUserNickname;
    private String recUserNickname;
    private String cont;
    private Timestamp createdAt;
    private Boolean isRead;
}
