package com.goott.potatoes.concho.service.pageLogic;

import com.goott.potatoes.concho.model.Pagination;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class PageLogicService {
    /*
     * 전체 데이터 수 rowCnt, 표시할 번호 블럭 수(홀수만 가능) viewBlocksCnt,
     * 현재 가야할 페이지 nowPageNum, 표시할 데이터 수 limit
     * rowCnt, PageBlocksCnt, nowPageNum, limit 를 입력받아 return limit, offset, pageBlocks
     */
    public Pagination getPagesBlock(int rowCnt, int limit, int viewBlocksCnt , int nowPageNum){
        Pagination pagination = new Pagination();
        if(viewBlocksCnt%2 == 0) viewBlocksCnt++;

        int surplus = 0;
        int maxPageNum = (rowCnt%limit == 0) ? rowCnt/limit : (rowCnt/limit + 1);

        if(maxPageNum < nowPageNum) {       //현재 가려는 페이지가 max 를 넘어갈때
            nowPageNum = maxPageNum;
        } else if (nowPageNum < 1) {        //현재 가려는 페이지가 min 보다 작을때
            nowPageNum = 1;
        }
        pagination.setEndPageNum(maxPageNum);
        pagination.setNowPageNum(nowPageNum);

        int startViewBlock = nowPageNum - viewBlocksCnt/2;
        int endViewBlockCnt = nowPageNum + viewBlocksCnt/2;
        if(startViewBlock < 1){                     // 왼쪽이 넘음
            surplus = 1 - startViewBlock;
            endViewBlockCnt += surplus;
            startViewBlock = 1;
        }else if(endViewBlockCnt > maxPageNum){     // 오른쪽이 넘음
            surplus = endViewBlockCnt - maxPageNum;
            startViewBlock -= surplus;
            endViewBlockCnt = maxPageNum;
        }
        if(maxPageNum < viewBlocksCnt){     //현재 표시할 블럭이 부족하다면
            var pageBlocks = new ArrayList<Integer>();
            for(int i=1; i<=maxPageNum; i++){
                pageBlocks.add(i);
            }
            pagination.setPageBlocks(pageBlocks);
            pagination.setOffset((nowPageNum-1)*limit);
            pagination.setLimit(limit);
            return pagination;
        }

        //현제 표시할 블럭이 충분
        var pageBlocks = new ArrayList<Integer>();
        for(int i=startViewBlock; i<=endViewBlockCnt; i++){
            pageBlocks.add(i);
        }
        pagination.setPageBlocks(pageBlocks);
        pagination.setOffset((nowPageNum-1)*limit);
        pagination.setLimit(limit);
        return pagination;
    }
}
