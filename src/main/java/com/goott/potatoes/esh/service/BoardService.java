package com.goott.potatoes.esh.service;

import com.goott.potatoes.esh.boardMapper.BoardMapper;
import com.goott.potatoes.esh.boardModel.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardService {
    @Autowired
    private BoardMapper boardMapper;

    public List<Board> decoGetBoardList() {
        return this.boardMapper.decoGetBoardList();
    }

    public int insertBoard(Board board) {
        return this.boardMapper.insertBoard(board);
    }

    public Board boardContent(int no){
        return this.boardMapper.boardContent(no);
    }

    public void viewCount(int no){
        this.boardMapper.viewCount(no);
    }

    public int updateBoard(Board board) {
        return this.boardMapper.updateBoard(board);
    }

    public int deleteBoard(int no) {
        return this.boardMapper.deleteBoard(no);
    }

    public void updateSeq(int no) {
        this.boardMapper.updateSeq(no);
    }


}
