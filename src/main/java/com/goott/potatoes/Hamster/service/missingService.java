package com.goott.potatoes.Hamster.service;

import com.goott.potatoes.Hamster.mapper.missingMapper;
import com.goott.potatoes.Hamster.model.missing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class missingService {

    @Autowired
    private missingMapper mapper;

    public int missingboard_write(missing miss){return this.mapper.missingboard_write(miss);}

    public List<missing> missingBoardList(){return this.mapper.missingBoardList();}

    public missing mBoardCont(int no){return this.mapper.mBoardCont(no);}

    public int mBoardModify(missing miss){return this.mapper.mBoardModify(miss);}

    public int mBoardDelete(int no){return this.mapper.mBoardDelete(no);}

    public void updateSequence(int no){this.mapper.updateSequence(no);}

    public void mboard_hit(int no){this.mapper.mboard_hit(no);}
}
