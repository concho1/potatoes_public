package com.goott.potatoes.common.mapper;

import com.goott.potatoes.common.model.ConchoMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ConchoMessageMapper {
    void insertMessage(ConchoMessage message);
    List<ConchoMessage> findMessageListByRoomName(@Param("roomName") String roomName);
    void updateIsReadToZero(@Param("roomName") String roomNum, @Param("sendUserNickname") String sendUserNickname);
    List<ConchoMessage> findRoomByRecUserNickname(@Param("recUserNickname")String recUserNickname);
    void updateSendNicknameMessages(@Param("oldNickname") String oldNickname, @Param("newNickname") String newNickname); // 보내는 닉네임 수정
    void updateRecNicknameMessages(@Param("oldNickname") String oldNickname, @Param("newNickname") String newNickname); // 받는 닉네임 수정
    void updateRoomName(@Param("oldNickname") String oldNickname, @Param("newNickname") String newNickname); // roomName 변겨
    void deleteUserMessages(String nickname); // 메시지 삭제
}
