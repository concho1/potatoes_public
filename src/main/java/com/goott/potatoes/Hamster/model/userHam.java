package com.goott.potatoes.Hamster.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class userHam {
    private String id;
    private String imgKey;
    private int oauthId;
    private String nickname;
    private String location;
    private String mannerRank;
    private int mannerPoint;
    private Timestamp created_at;
    private Timestamp updated_at;
    private int message;
    private String pwd;

    public userHam(String email, String nickname, String img_key) {
        this.nickname = nickname;
        this.id = email;
        this.imgKey = img_key;
    }
}
