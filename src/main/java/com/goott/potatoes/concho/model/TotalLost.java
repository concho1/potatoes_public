package com.goott.potatoes.concho.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TotalLost {
    private String idPlus;
    private String dept;
    private String imgUrl;
    private String name;
    private String content;
    private Timestamp date;
    private String tel;
    private String sector;

    public TotalLost(Seoul seoul, String baseUrl){
        this.idPlus = seoul.getActId();
        this.dept = seoul.getOrgName();
        this.imgUrl = baseUrl;
        this.name = seoul.getProductName();
        this.content = seoul.getCont().replaceAll("&#xD;", "<br/>");
        this.date = seoul.getDate();
        this.tel = "해당 회사 문의";
        this.sector = "seoul";
    }

    public TotalLost(PublicLost publicLost, String baseUrl, String sector){
        this.idPlus = publicLost.getIdPlus();
        this.dept = publicLost.getDept();
        if(publicLost.getImgUrl().equals("https://www.lost112.go.kr/lostnfs/images/sub/img02_no_img.gif")){
            this.imgUrl = baseUrl;
        }else{
            this.imgUrl = publicLost.getImgUrl();
        }
        this.name = publicLost.getName();
        this.content = publicLost.getContent();
        this.date = publicLost.getDate();
        this.tel = publicLost.getTel();
        this.sector = sector;
    }
}
