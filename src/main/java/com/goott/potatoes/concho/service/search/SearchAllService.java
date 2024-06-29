package com.goott.potatoes.concho.service.search;

import com.goott.potatoes.common.service.ImageService;
import com.goott.potatoes.concho.mapper.PublicLostItemsMapper;
import com.goott.potatoes.concho.mapper.SeoulMapper;
import com.goott.potatoes.concho.model.PublicLost;
import com.goott.potatoes.concho.model.RegionAndPhoneMap;
import com.goott.potatoes.concho.model.Seoul;
import com.goott.potatoes.concho.model.TotalLost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class SearchAllService {
    @Autowired
    private PublicLostItemsMapper publicLostItemsMapper;
    @Autowired
    private SeoulMapper seoulMapper;
    @Autowired
    private ImageService imageService;
    public List<TotalLost> getPublicListByLocAndCate(String location, String majorCate, String minorCate){
        RegionAndPhoneMap regionAndPhoneMap = new RegionAndPhoneMap();
        String phoneCode = regionAndPhoneMap.getPhoneCode(location);

        List<PublicLost> publicPoliceLostList = publicLostItemsMapper.getPoliceItemsByTelAndCategory(phoneCode, majorCate, minorCate);
        List<PublicLost> publicLostList = publicLostItemsMapper.getItemsByTelAndCategory(phoneCode, majorCate, minorCate);
        List<Seoul> seoulList = seoulMapper.getSeoulListByCate(majorCate, minorCate);

        String baseImgKey = "potatoes/8b63935f-0157-4f69-bbbd-2ffa1a485081";
        String baseImgUrl = imageService.findImageByKey(baseImgKey).get().getUrl();

        List<TotalLost> totalLostList = new ArrayList<>();
        for(PublicLost publicLost : publicLostList){
            totalLostList.add(new TotalLost(publicLost, baseImgUrl, "public"));
        }
        for(PublicLost publicLost : publicPoliceLostList){
            totalLostList.add(new TotalLost(publicLost, baseImgUrl, "police"));
        }
        for(Seoul seoul : seoulList){
            totalLostList.add(new TotalLost(seoul, baseImgUrl));
        }
        // date 필드를 기준으로 정렬합니다.
        totalLostList.sort(Comparator.comparing(TotalLost::getDate).reversed());

        return totalLostList;
    }
}
