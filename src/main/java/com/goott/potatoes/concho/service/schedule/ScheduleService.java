package com.goott.potatoes.concho.service.schedule;

import com.goott.potatoes.concho.mapper.PublicLostItemsMapper;
import com.goott.potatoes.concho.mapper.SeoulMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class ScheduleService {
    @Autowired
    private PublicLostApiService publicLostApiService;
    @Autowired
    private SeoulLostApiService seoulLostApiService;
    @Autowired
    private SeoulMapper seoulMapper;
    @Autowired
    private PublicLostItemsMapper publicLostItemsMapper;


    @Value("${concho.potal.key}")
    private String publicLostKey;

    @Scheduled(fixedDelay = 3600000*12)
    public void scheduleService() {

        // 가져올 데이터 목록
        String[] publicLostColumnName = {"atcId", "depPlace", "fdFilePathImg", "fdPrdtNm", "fdSbjt", "fdSn", "fdYmd", "prdtClNm", "rnum"};

        String publicLostUrl = "http://apis.data.go.kr/1320000/LosPtfundInfoInqireService/getPtLosfundInfoAccToClAreaPd" +
                "?" + URLEncoder.encode("serviceKey", StandardCharsets.UTF_8) +
                "=" + publicLostKey +                                                  /*Service Key*/
                "&" + URLEncoder.encode("pageNo", StandardCharsets.UTF_8) +
                "=" + URLEncoder.encode("1", StandardCharsets.UTF_8) +              /*페이지번호*/
                "&" + URLEncoder.encode("numOfRows", StandardCharsets.UTF_8) +
                "=" + URLEncoder.encode("100000", StandardCharsets.UTF_8);          /*한 페이지 결과 수*/

        String policeLostUrl = "http://apis.data.go.kr/1320000/LosfundInfoInqireService/getLosfundInfoAccToClAreaPd" +
                "?" + URLEncoder.encode("serviceKey", StandardCharsets.UTF_8) +
                "=" + publicLostKey +                                                  /*Service Key*/
                "&" + URLEncoder.encode("pageNo", StandardCharsets.UTF_8) +
                "=" + URLEncoder.encode("1", StandardCharsets.UTF_8) +              /*페이지번호*/
                "&" + URLEncoder.encode("numOfRows", StandardCharsets.UTF_8) +
                "=" + URLEncoder.encode("100000", StandardCharsets.UTF_8);          /*한 페이지 결과 수*/

        int minusDayStart = 2;     // - 일 전부터 => 이게 더 커야함
        int minusDayEnd = 0;       // - 일 전 까지
        LocalDate startDate = LocalDate.now().minusDays(minusDayStart); // 시작 날짜를 정의 (현제날짜 -1일)
        LocalDate endDate = LocalDate.now().minusDays(minusDayEnd);     // 시작 날짜와 현재 날짜를 정의
        //LocalDate startDate = LocalDate.of(2024, 4, 6); // 시작 날짜
        //LocalDate endDate = LocalDate.of(2024, 4, 14);  // 종료 날짜

        // 시작 날짜부터 현재 날짜까지 반복
        // 4시간(14400000 밀리초) 이하인지 확인
        long fourHoursInMillis = TimeUnit.HOURS.toMillis(3);
        // 현재 시간
        Timestamp currentTimestamp = Timestamp.valueOf(LocalDateTime.now());
        if(Math.abs(currentTimestamp.getTime() - seoulMapper.getLastTime().getTime()) < fourHoursInMillis ){
            System.out.println("=================================================");
            System.out.println("서울 데이터는 이미 최신 데이터입니다.");
        }else{
            System.out.println("=================================================");
            System.out.println("서울 데이터 가져오기 시작");
            seoulLostApiService.seoulLostSchedule(startDate, endDate);
            seoulMapper.updateLastTime(Timestamp.valueOf(LocalDateTime.now()));
        }
        System.out.println("=================================================");
        if(Math.abs(currentTimestamp.getTime() - publicLostItemsMapper.getLastTimePortal().getTime()) < fourHoursInMillis){
            System.out.println("포털 데이터는 이미 최신 상태입니다.");
        }else{
            System.out.println("포털데이터 가져오기 시작");
            publicLostApiService.PublicLostSchedule(false, startDate, endDate, publicLostUrl, publicLostColumnName);
            publicLostItemsMapper.updateLastTimePortal(Timestamp.valueOf(LocalDateTime.now()));
        }
        System.out.println("=================================================");
        if(Math.abs(currentTimestamp.getTime() - publicLostItemsMapper.getLastTimePolice().getTime()) < fourHoursInMillis){
            System.out.println("경찰청 데이터는 이미 최신 상태입니다.");
        }else{
            System.out.println("경찰청 데이터 가져오기 시작");
            publicLostApiService.PublicLostSchedule(true, startDate, endDate, policeLostUrl, publicLostColumnName);
            publicLostItemsMapper.updateLastTimePolice(Timestamp.valueOf(LocalDateTime.now()));
        }
        System.out.println("=================================================");
    }
}
