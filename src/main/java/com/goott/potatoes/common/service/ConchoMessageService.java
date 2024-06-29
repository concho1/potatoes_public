package com.goott.potatoes.common.service;

import com.goott.potatoes.common.mapper.ConchoMessageMapper;
import com.goott.potatoes.common.model.ConchoMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConchoMessageService {
    @Autowired
    private ConchoMessageMapper messageMapper;

    public static String combineStrings(String str1, String str2) {
        // 두 문자열을 알파벳 순서로 정렬하여 합친다.
        if (str1.compareTo(str2) < 0) {
            return str1 + "-" + str2;
        } else {
            return str2 + "-" + str1;
        }
    }
    public List<ConchoMessage> findMessageListBySenderAndReceiver(String sender, String receiver){
        String roomName = combineStrings(sender,receiver);
        List<ConchoMessage> messageList = messageMapper.findMessageListByRoomName(roomName);
        return messageList;
    }
    public void insertMessageBySenderAndRoomNameAndContent(String sender, String receiver ,String roomName, String content){
        ConchoMessage message = new ConchoMessage();
        message.setSendUserNickname(sender);
        message.setRoomName(roomName);
        message.setCont(content);
        message.setRecUserNickname(receiver);
        messageMapper.insertMessage(message);
    }
    public void updateIsReadToZero(String sender, String receiver){
        String roomName = combineStrings(sender,receiver);
        messageMapper.updateIsReadToZero(roomName, sender);
    }

    public List<ConchoMessage> findMessageListByRecUserNickname(String recUserNickname){
        return messageMapper.findRoomByRecUserNickname(recUserNickname);
    }

    public List<ConchoMessage> findMessageByRoomName(String roomName){
        return messageMapper.findMessageListByRoomName(roomName);
    }

}
