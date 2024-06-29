package com.goott.potatoes.Hamster.mapper;

import com.goott.potatoes.Hamster.model.userHam;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;

@Mapper
public interface userMapperHam {

    public Boolean checkDupEmail(String email);

    public Boolean checkDupNickname(String nickname);

    public Boolean checkDupPwd(String pwd);

    public Boolean checkPwdInfo(HashMap<String,String> map);

    public int changePwd(HashMap<String,String> map);

    public int joinUser(userHam user);

    public void joinKakaoUser(userHam user);

    public userHam Login(String id);

    public void joinCnt();

    public void loginCnt();
}
