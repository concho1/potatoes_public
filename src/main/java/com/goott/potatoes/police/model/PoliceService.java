package com.goott.potatoes.police.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PoliceService {

    @Autowired
    private PoliceListService listService;

    @Autowired
    private PoliceContService contService;

    @Autowired
    private PoliceMapper mapper;

    // @Scheduled(fixedDelay = 1000 * 3600 * 24)
    public void policeSchedule() {
        var pList = this.listService.policeList();
        List<Police> policeList = new ArrayList<>();
        Police dto1 = null;
        Police dto2 = null;

        for(int i=0; i<pList.size(); i++) {
            String actId = pList.get(i).get("atcId");
            String title = pList.get(i).get("fdSbjt");
            String fdSn = pList.get(i).get("fdSn");
            String policeKey = actId + fdSn;

            dto1 = new Police();

            dto1.setPoliceKey(policeKey);
            dto1.setAtcId(actId);
            dto1.setTitle(title);
            dto1.setFdSn(fdSn);

            policeList.add(dto1);
        }

        this.mapper.add(policeList);

        for(int i=0; i<policeList.size(); i++) {
            dto2 = this.contService.policeCont(policeList.get(i));
            String addr = this.mapper.findAddr(dto2.getOrgTel());
            dto2.setAddr(addr);
            this.mapper.update_police(dto2);

        }

    }



}
