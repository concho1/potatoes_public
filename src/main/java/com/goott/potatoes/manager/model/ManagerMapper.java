package com.goott.potatoes.manager.model;

import com.goott.potatoes.Hamster.model.missing;
import com.goott.potatoes.Hamster.model.pickup;
import com.goott.potatoes.esh.boardModel.Board;
import com.goott.potatoes.esh.boardModel.BoardReply;
import com.goott.potatoes.qna.model.FAQ;
import com.goott.potatoes.qna.model.QNA;
import com.goott.potatoes.user.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ManagerMapper {
    public void crtCol();
    public void updateGuestCnt();
    public UserCnt getUserCnt(String date);
    public int countMissing();
    public List<User> getUserListForMain(int size);
    public int deleteUser(String id);
    public List<missing> getMissingListForMain(int size);
    public List<Board> getBoardListForMain(int size);
    public List<pickup> getPickUpListForMain(int size);
    public List<FAQ> getFAQListForMain();
    public List<QNA> getQNAList();
    public QNA getQNA(int no);
    public int answerQNA(QNA qdto);
    public void updateQNAStatus(int no);
    public List<User> getAllUserList();
    public List<Board> getAllBoard();
    public Board getBoard(int no);
    public List<BoardReply> getBoardReplyList(int no);
    public int getBoardReplyCount(int no);
    public int deleteBoardReply(int no);
    public int modBoard(Board bdto);
    public int deleteBoard(int no);
    public void updateBoardSequence(int no);
    public void updateBoardReplySequence(int no);
    public List<missing> getAllMissing();
    public List<pickup> getAllPickUp();
    public missing getMissing(int no);
    public int modMissing(missing mdto);
    public int deleteMissing(int no);
    public void updateMissingSequence(int no);
    public pickup getPickUp(int no);
    public int modPickUp(pickup pudto);
    public int deletePickUp(int no);
    public void updatePickUpSequence(int no);
    public int updateFAQ(FAQ fdto);
    public int insertFAQ(FAQ fdto);
}
