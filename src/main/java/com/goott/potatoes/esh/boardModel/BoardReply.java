package com.goott.potatoes.esh.boardModel;

import lombok.Data;

@Data

public class BoardReply {
    private int replyNum;
    private int boardNum;
    private String id;
    private int depth;
    private int step;
    private String date;
    private String cont;
}
