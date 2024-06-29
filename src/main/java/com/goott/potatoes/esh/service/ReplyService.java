package com.goott.potatoes.esh.service;

import com.goott.potatoes.esh.boardMapper.BoardMapper;
import com.goott.potatoes.esh.boardModel.BoardReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ReplyService {
    /*public List<String> setReply() {
        return null;
    }*/

    @Autowired
    private BoardMapper replyMapper;

    public int insertReply(BoardReply reply) {
        return this.replyMapper.insertReply(reply);
    }

    public List<BoardReply> getBoardReplies(int boardNum) {
        return this.replyMapper.getBoardReplies(boardNum);
    }

    public int countReply(int boardNum) {
        return this.replyMapper.countReply(boardNum);
    }

    public int deleteReply(int replyNum) {
        return this.replyMapper.deleteReply(replyNum);
    }
}
