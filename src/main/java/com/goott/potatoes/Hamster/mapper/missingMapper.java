package com.goott.potatoes.Hamster.mapper;

import com.goott.potatoes.Hamster.model.missing;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface missingMapper {

    public int missingboard_write(missing miss);

    public List<missing> missingBoardList();

    public missing mBoardCont(int no);

    public int mBoardModify(missing miss);

    public int mBoardDelete(int no);

    public void updateSequence(int no);

    public void mboard_hit(int no);
}
