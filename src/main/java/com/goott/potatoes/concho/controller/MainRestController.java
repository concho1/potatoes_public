package com.goott.potatoes.concho.controller;

import com.goott.potatoes.common.model.Alarm;
import com.goott.potatoes.concho.model.Pagination;
import com.goott.potatoes.concho.model.TotalLost;
import com.goott.potatoes.concho.service.cate.CateService;
import com.goott.potatoes.concho.service.search.SearchAllService;
import com.goott.potatoes.concho.service.search.SearchByNameService;
import com.goott.potatoes.concho.service.search.SearchInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("concho")
@RequiredArgsConstructor
public class MainRestController {
    private final CateService cateService;
    private final SearchAllService searchAllService;
    private final SearchInfoService searchInfoService;
    private final SearchByNameService searchByNameService;

    @GetMapping(value = {"main/{num}", "main"})
    public ModelAndView getMainPages(
            ModelAndView modelAndView,
            @PathVariable(required = false) Integer num,
            @RequestParam Map<String, String> params){
        switch (num == null ? 1 : num){
            case 2 -> {
                modelAndView.setViewName("concho/concho-main2");
                modelAndView.addObject("selectedLoc", params.get("selectedLoc"));
                modelAndView.addObject("cateMapList", cateService.getCateListMap());
            }
            case 3 -> {
                modelAndView.setViewName("concho/concho-main3");
                modelAndView.addObject(
                    "totalLostList",
                    searchAllService.getPublicListByLocAndCate(
                        params.get("location"),params.get("major"),params.get("minor")
                    )
                );
            }
            default -> modelAndView.setViewName("concho/concho-main1");
        }
        return modelAndView;
    }
    @GetMapping("info")
    public ModelAndView getInfoPage(
            Model model,
            ModelAndView modelAndView,
            @RequestParam("sector") String sector,
            @RequestParam("idPlus") String idPlus){
        Alarm alarm = new Alarm(model);
        Optional<TotalLost> totalLostOp
                = searchInfoService.getInfoBySectorAndIdPlus(sector, idPlus);
        if(totalLostOp.isPresent()){
            modelAndView.setViewName("concho/concho-info");
            modelAndView.addObject("totalLost", totalLostOp.get());
        }else{
            alarm.setMessageAndRedirect("상세정보가 없습니다.", "");
            modelAndView.setViewName(alarm.getMessagePage());
        }
        return modelAndView;
    }
    @GetMapping("search/{sector}")
    public ModelAndView getSearchListPage(
            Model model,
            ModelAndView modelAndView,
            @PathVariable(required = false) String sector,
            @RequestParam Map<String, String> params){

        modelAndView.addObject("isSearchPresent", true);
        modelAndView.addObject("sector", sector);
        String search = params.getOrDefault("search", null);
        if(search == null || search.trim().equals("null")){
            modelAndView.addObject("totalLostList", new ArrayList<>());
            modelAndView.setViewName("concho/concho-search-form");
            return modelAndView;
        }
        int limit = Integer.parseInt(params.getOrDefault("limit", "5"));
        Integer nowPageNum = Integer.parseInt(params.getOrDefault("nowPageNum", "1"));
        Pagination pagination = new Pagination();
        Optional<List<TotalLost>> totalLostListOp =
                searchByNameService.getTotalListBySearchAndSector(
                        search.trim(), sector, limit, nowPageNum, pagination, params
                );
        List<TotalLost> totalLostList;
        if(totalLostListOp.isEmpty() || totalLostListOp.get().isEmpty()){
            Alarm alarm = new Alarm(model);
            alarm.setMessageAndRedirect("검색 결과가 없습니다...", "");
            modelAndView.setViewName(alarm.getMessagePage());
            return modelAndView;
        }else{
            totalLostList = totalLostListOp.get();
        }
        modelAndView.addObject("totalLostList", totalLostList);
        modelAndView.addObject("rowCnt", params.get("rowCnt"));
        modelAndView.addObject("nowPageNum", nowPageNum);
        modelAndView.addObject("limit", limit);
        modelAndView.addObject("search", search);
        modelAndView.addObject("pagination", pagination);
        modelAndView.setViewName("concho/concho-search-form");

        return modelAndView;
    }

}
