package com.goott.potatoes.qna.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class QNA {
    private int num;
    private String userNickname;
    private String title;
    private String cont;
    private Timestamp qnaDate;
    private Timestamp qnaUpdate;
    private int qnaStatus;
    private int qnaGroup;
    private int qnaStep;
    private int qnaIndent;
}
