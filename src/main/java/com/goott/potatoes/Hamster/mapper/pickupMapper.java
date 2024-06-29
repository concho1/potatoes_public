package com.goott.potatoes.Hamster.mapper;

import com.goott.potatoes.Hamster.model.missing;
import com.goott.potatoes.Hamster.model.pickup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface pickupMapper {

    public int pickupboard_write(missing miss);

    public List<pickup> pickBoardList();

    public pickup pBoardCont(int no);

    public int pBoardModify(pickup pick);

    public int pBoardDelete(int no);

    public void updateSequence(int no);

    public void pboard_hit(int no);
}
