package com.goott.potatoes.concho.service.search;

import com.goott.potatoes.common.service.ImageService;
import com.goott.potatoes.concho.mapper.PublicLostItemsMapper;
import com.goott.potatoes.concho.mapper.SeoulMapper;
import com.goott.potatoes.concho.model.PublicLost;
import com.goott.potatoes.concho.model.TotalLost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SearchInfoService {
    @Autowired
    private SeoulMapper seoulMapper;
    @Autowired
    private PublicLostItemsMapper publicLostItemsMapper;
    @Autowired
    private ImageService imageService;
    public Optional<TotalLost> getInfoBySectorAndIdPlus(String sector, String idPlus){
        String baseImgKey = "potatoes/8b63935f-0157-4f69-bbbd-2ffa1a485081";
        String baseImgUrl = imageService.findImageByKey(baseImgKey).get().getUrl();
        TotalLost totalLost;
        try{
            if(sector.equals("seoul")){
                totalLost = new TotalLost(seoulMapper.findSeoulByIdPlus(idPlus), baseImgUrl);
            }else if(sector.equals("public")){
                Optional<PublicLost> op = Optional.ofNullable(publicLostItemsMapper.getItemByPlusId(idPlus));
                if(op.isEmpty()){
                    totalLost = new TotalLost(publicLostItemsMapper.getPoliceItemByPlusId(idPlus), baseImgUrl, "police");
                }else{
                    totalLost = new TotalLost(op.get(), baseImgUrl, "public");
                }
            }else {
                Optional<PublicLost> op = Optional.ofNullable(publicLostItemsMapper.getPoliceItemByPlusId(idPlus));
                if(op.isEmpty()){
                    totalLost = new TotalLost(publicLostItemsMapper.getItemByPlusId(idPlus), baseImgUrl, "public");
                }else{
                    totalLost = new TotalLost(op.get(), baseImgUrl, "police");
                }

            }
        }catch (Exception e){
            totalLost = null;
        }

        return Optional.ofNullable(totalLost);
    }
}
