package com.goott.potatoes.qna.model;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QNAMapper {
    public int getListCount();
    public List<QNA> getList();
    public List<FAQ> getFAQ();
    public int getNum();
    public int insertQuestion(QNA qdto);
    public QNA getQNA(int no);
    public QNA getAnswer(int no);
    public int updateQNA(QNA qdto);
    public int deleteQNA(int no);
    public void deleteAnswer(int no);
}
