package com.goott.potatoes.Hamster.service;

import com.goott.potatoes.Hamster.mapper.pickupMapper;
import com.goott.potatoes.Hamster.model.missing;
import com.goott.potatoes.Hamster.model.pickup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class pickupService {

    @Autowired
    private pickupMapper mapper;

    public int pickupboard_write(missing miss){return this.mapper.pickupboard_write(miss);}

    public List<pickup> pickBoardList(){return this.mapper.pickBoardList();}

    public pickup pBoardCont(int no){return this.mapper.pBoardCont(no);}

    public int pBoardModify(pickup pick){return this.mapper.pBoardModify(pick);}

    public int pBoardDelete(int no){return this.mapper.pBoardDelete(no);}

    public void updateSequence(int no){this.mapper.updateSequence(no);}

    public void pboard_hit(int no){this.mapper.pboard_hit(no);}
}
