package com.goott.potatoes.user.service;

import com.goott.potatoes.common.mapper.ConchoMessageMapper;
import com.goott.potatoes.user.mapper.UserMapper;
import com.goott.potatoes.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyPageService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ConchoMessageMapper messageMapper;

    // 상세 정보
    public User contUser(String id) {
       return this.userMapper.contUser(id);
    }
    // 수정
    public int updateUser(User user) {
        User findById = this.userMapper.contUser(user.getId());
        String oldNick = findById.getNickname();
        if (!oldNick.equals(user.getNickname())) {
            this.messageMapper.updateSendNicknameMessages(oldNick, user.getNickname());
            this.messageMapper.updateRecNicknameMessages(oldNick, user.getNickname());
            this.messageMapper.updateRoomName(oldNick, user.getNickname());
        }
        return this.userMapper.updateUser(user);
    }
    // 비밀번호 변경
    public int updatePwd(String id, String pwd){ return this.userMapper.updatePwd(id, pwd); }
    // 비밀번호 확인
    public boolean checkPwd(String id, String pwd){ return this.userMapper.checkPwd(id, pwd); }
    // 회원 탈퇴
    public int deleteUser(String id) {// 사용자와 관련된 메시지 삭제
        User user = this.userMapper.contUser(id);// id로 사용자 정보 조회
        if (user != null) {
            this.messageMapper.deleteUserMessages(user.getNickname()); // nickname을 사용하여 관련된 메시지 삭제
            return this.userMapper.deleteUser(id); // id를 사용하여 사용자 삭제
        } else {
            return 0; // 사용자가 존재하지 않는 경우 0 반환
        }
    }

}
