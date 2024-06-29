package com.goott.potatoes.Hamster.service;

import com.goott.potatoes.Hamster.mapper.userMapperHam;
import com.goott.potatoes.Hamster.model.userHam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class userService {

    @Autowired
    private userMapperHam mapper;

    public int joinUser(userHam user) {
        return this.mapper.joinUser(user);
    }

    public void joinKakaoUser(userHam user) {this.mapper.joinKakaoUser(user);}

    public Boolean checkDupEmail(String email){return this.mapper.checkDupEmail(email);}

    public Boolean checkDupNickname(String nickname){return this.mapper.checkDupNickname(nickname); }

    public Boolean checkDupPwd(String pwd){return this.mapper.checkDupPwd(pwd);}

    public Boolean checkPwdInfo(HashMap<String,String> map){return this.mapper.checkPwdInfo(map);}

    public userHam Login(String id){return this.mapper.Login(id); }

    public void joinCnt(){this.mapper.joinCnt();}

    public void loginCnt(){this.mapper.loginCnt();}

    public int changePwd(HashMap<String,String> map){return this.mapper.changePwd(map);};
}
