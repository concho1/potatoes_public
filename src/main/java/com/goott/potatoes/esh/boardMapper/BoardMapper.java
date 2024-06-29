package com.goott.potatoes.esh.boardMapper;

import com.goott.potatoes.esh.boardModel.Board;
import com.goott.potatoes.esh.boardModel.BoardReply;
import com.goott.potatoes.esh.boardModel.Page;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface BoardMapper {
    int getListCount();
    List<Board> decoGetBoardList();
    int insertBoard(Board board);
    Board boardContent(int no);
    void viewCount(int no);
    int updateBoard(Board board);
    int deleteBoard(int no);
    void updateSeq(int no);
    int searchBoardCount(Map<String, String> map);
    List<Board> searchBoardList(Page pdto);
    int insertReply(BoardReply reply);
    List<BoardReply> getBoardReplies(int boardNum);
    int countReply(int boardNum);
    int deleteReply(int replyNum);
}
