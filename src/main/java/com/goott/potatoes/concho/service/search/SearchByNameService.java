package com.goott.potatoes.concho.service.search;

import com.goott.potatoes.common.service.ImageService;
import com.goott.potatoes.concho.mapper.PublicLostItemsMapper;
import com.goott.potatoes.concho.mapper.SeoulMapper;
import com.goott.potatoes.concho.model.Pagination;
import com.goott.potatoes.concho.model.PublicLost;
import com.goott.potatoes.concho.model.Seoul;
import com.goott.potatoes.concho.model.TotalLost;
import com.goott.potatoes.concho.service.pageLogic.PageLogicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SearchByNameService {
    private final SeoulMapper seoulMapper;
    private final PublicLostItemsMapper publicLostItemsMapper;
    private final ImageService imageService;
    private final PageLogicService pageLogicService;

    public Optional<List<TotalLost>> getTotalListBySearchAndSector(
            String search, String sector,
            int limit, Integer nowPageNum,
            Pagination pagination, Map<String, String> params) {
        int rowCnt = 0;
        List<TotalLost> totalLostList = new ArrayList<>();
        Pagination tempPagination;
        try {
            String baseImgKey = "potatoes/8b63935f-0157-4f69-bbbd-2ffa1a485081";
            String baseImgUrl = imageService.findImageByKey(baseImgKey).get().getUrl();

            switch (sector) {
                case "seoul" -> {
                    rowCnt = seoulMapper.getSeoulListBySearchCnt(search);
                    tempPagination = pageLogicService.getPagesBlock(rowCnt, limit, 10, nowPageNum);
                    handleSeoul(search, baseImgUrl, totalLostList, tempPagination.getLimit(), tempPagination.getOffset() < 1 ? 0 : tempPagination.getOffset());
                }
                case "police" -> {
                    rowCnt = publicLostItemsMapper.getPoliceItemsBySearchCnt(search);
                    tempPagination = pageLogicService.getPagesBlock(rowCnt, limit, 10, nowPageNum);
                    handlePolice(search, baseImgUrl, totalLostList, tempPagination.getLimit(), tempPagination.getOffset() < 1 ? 0 : tempPagination.getOffset());
                }
                case "public" -> {
                    rowCnt = publicLostItemsMapper.getItemsBySearchCnt(search);
                    tempPagination = pageLogicService.getPagesBlock(rowCnt, limit, 10, nowPageNum);
                    handlePublic(search, baseImgUrl, totalLostList, tempPagination.getLimit(), tempPagination.getOffset() < 1 ? 0 : tempPagination.getOffset());
                }
                default -> {
                    rowCnt = publicLostItemsMapper.getCombinedItemsBySearchCnt(search);
                    tempPagination = pageLogicService.getPagesBlock(rowCnt, limit, 10, nowPageNum);
                    handleAll(search, baseImgUrl, totalLostList, tempPagination.getLimit(), tempPagination.getOffset() < 1 ? 0 : tempPagination.getOffset());
                }
            }
            // date 필드를 기준으로 정렬합니다.
            totalLostList.sort(Comparator.comparing(TotalLost::getDate).reversed());
            params.put("rowCnt", String.valueOf(rowCnt));

            pagination.setLimit(tempPagination.getLimit());
            pagination.setOffset(tempPagination.getOffset());
            pagination.setPageBlocks(tempPagination.getPageBlocks());
            pagination.setNowPageNum(tempPagination.getNowPageNum());
            pagination.setEndPageNum(tempPagination.getEndPageNum());
        } catch (Exception e) {
            totalLostList = null;
            e.printStackTrace();
        }


        return Optional.ofNullable(totalLostList);
    }

    private void handleAll(String search, String baseImgUrl, List<TotalLost> totalLostList, Integer limit, Integer offset) {
        List<PublicLost> publicLostList = publicLostItemsMapper.getCombinedItemsBySearch(search, limit, offset);
        for (PublicLost publicLost : publicLostList) {
            totalLostList.add(new TotalLost(publicLost, baseImgUrl, "police"));
        }
    }

    private void handleSeoul(String search, String baseImgUrl, List<TotalLost> totalLostList, Integer limit, Integer offset) {
        List<Seoul> seoulList = seoulMapper.getSeoulListBySearch(search, limit, offset);
        for (Seoul seoul : seoulList) {
            totalLostList.add(new TotalLost(seoul, baseImgUrl));
        }
    }

    private void handlePolice(String search, String baseImgUrl, List<TotalLost> totalLostList, Integer limit, Integer offset) {
        List<PublicLost> publicLostList = publicLostItemsMapper.getPoliceItemsBySearch(search, limit, offset);
        for (PublicLost publicLost : publicLostList) {
            totalLostList.add(new TotalLost(publicLost, baseImgUrl, "police"));
        }
    }

    private void handlePublic(String search, String baseImgUrl, List<TotalLost> totalLostList, Integer limit, Integer offset) {
        List<PublicLost> publicLostList = publicLostItemsMapper.getItemsBySearch(search, limit, offset);
        for (PublicLost publicLost : publicLostList) {
            totalLostList.add(new TotalLost(publicLost, baseImgUrl, "public"));
        }
    }
}
