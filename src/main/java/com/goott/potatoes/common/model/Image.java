package com.goott.potatoes.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class Image {
    private String imgKey;
    private Timestamp createdAt;
    private String url;
}
