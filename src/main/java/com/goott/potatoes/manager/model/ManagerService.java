package com.goott.potatoes.manager.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;

@Service
public class ManagerService {

    @Autowired
    private ManagerMapper mapper;

    @Scheduled(cron = "0 0 0 * * ?")
    public void crtCol() {

        LocalDateTime base = LocalDateTime.now();
        Month mon = base.getMonth();
        LocalDateTime ldt = LocalDateTime.of(base.getYear(), mon.getValue(), base.getDayOfMonth(), 0, 0, 0);

        System.out.println(ldt+" 컬럼 생성 개시");
        this.mapper.crtCol();
        System.out.println("컬럼 생성 완료");
        System.out.println("========================================");
    }

}
