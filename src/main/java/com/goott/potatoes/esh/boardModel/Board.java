package com.goott.potatoes.esh.boardModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Board {
    private int boardNum;
    private String userId;
    private String userNickname;
    private String imgKey;
    private String title;
    private String cont;
    private int viewCnt;
    private int commentCount;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
