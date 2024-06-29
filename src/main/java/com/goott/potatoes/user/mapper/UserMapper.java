package com.goott.potatoes.user.mapper;

import com.goott.potatoes.user.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User contUser(String id); // 상세정보(마이페이지)
    int updateUser(User user); // 회원 수정
    int updatePwd(String id, String pwd); // 비밀번호 변경
    boolean checkPwd(String id, String pwd); // 비밀번호 확인
    int deleteUser(String id); // 회원 삭제(탈퇴)

    User findUserByNickname(@Param("nickname") String nickname);
}
