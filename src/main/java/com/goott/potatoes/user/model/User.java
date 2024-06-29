package com.goott.potatoes.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String id;
    private String imgKey;
    private int oauthId;
    private String nickname;
    private String location;
    private String mannerRank;
    private int mannerPoint;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private int message;
    private String pwd;

}
