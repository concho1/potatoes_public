package com.goott.potatoes.concho.service.schedule;

import com.goott.potatoes.concho.mapper.PublicLostItemsMapper;
import com.goott.potatoes.concho.model.PublicLost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@EnableAsync
public class PublicLostApiService {
    @Autowired
    private XmlToMapListService xmlToMapListService;
    @Autowired
    public PublicLostItemsMapper publicLostItemsMapper;
    @Autowired
    private ApiToStringService ApiToStringService;
    @Autowired
    public PublicLostInfoApiService publicLostInfoApiService;

    public Queue<HashMap<String, String>> processQueue = new ConcurrentLinkedQueue<>();
    public Queue<HashMap<String, String>> processResultQueue = new ConcurrentLinkedQueue<>();
    public ArrayList<PublicLost> publicLostList = new ArrayList<>();
    public AtomicInteger dayStartCnt = new AtomicInteger(0);
    public int infoDataCnt = 0;
    public int processStartCnt = 0;
    private AtomicInteger threadStartCount = new AtomicInteger(0);
    private AtomicInteger threadEndCount = new AtomicInteger(0);
    private boolean isPolice;

    public void PublicLostSchedule(boolean isPolice, LocalDate startDate, LocalDate endDate, String inputUrl, String[] columnName) {
        this.isPolice = isPolice;
        try {
            dayStartCnt.set(Period.between(startDate, endDate).getDays() + 1);
            System.out.println("받아와야 할 총 날짜 수 : " + dayStartCnt.get());
            // 날짜 형식을 정의
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

            // 시작 날짜부터 현재 날짜까지 반복
            for (LocalDate date = endDate; !date.isBefore(startDate); date = date.minusDays(1)) {
                // 날짜를 원하는 형식으로 변환하여 문자열로 저장
                String formattedDate = date.format(formatter);
                String urlBuilder = inputUrl + (
                        "&" + URLEncoder.encode("START_YMD", StandardCharsets.UTF_8) +      /*검색시작일*/
                                "=" + URLEncoder.encode(formattedDate, StandardCharsets.UTF_8) +
                                "&" + URLEncoder.encode("END_YMD", StandardCharsets.UTF_8) +        /*검색종료일*/
                                "=" + URLEncoder.encode(formattedDate, StandardCharsets.UTF_8)
                );
                // api 이용해서 xml 페이지 가져오기
                Optional<String> xmlStrOp = ApiToStringService.getApiToXmlString(urlBuilder);
                if (xmlStrOp.isPresent()) {
                    // String 형태의 xml 을 map 형식의 List 로 바꿔서 넘겨주는 서비스
                    List<HashMap<String, String>> mapList = xmlToMapListService.xmlStrToStringList(xmlStrOp.get(), columnName);
                    // 모든 정보들을 큐에 저장
                    for(HashMap<String, String> map : mapList){
                        infoDataCnt++;
                        if(infoDataCnt > 99000){
                            System.out.println("종료된 날짜 : " + map.get("fdYmd"));
                            break;
                        }
                        processQueue.add(map);
                    }
                }
                System.out.println("받아온 날짜 : " + date);
            }
            System.out.println("상세정보 받아오기 시작...");
            // 멀티스레드로 processQueue 처리
            ExecutorService executorService = Executors.newFixedThreadPool(20); // 스레드 풀 크기 조정 가능
            List<Future<?>> futures = new ArrayList<>();
            while (!processQueue.isEmpty()) {
                HashMap<String, String> map = processQueue.poll();
                if (map != null && map.containsKey("atcId") && map.containsKey("fdSn")) {
                    processStartCnt++;
                    threadStartCount.incrementAndGet();
                    futures.add(executorService.submit(() -> {
                        processMap(map);
                        threadEndCount.incrementAndGet();
                    }));
                }
            }

            // 모든 스레드가 종료될 때까지 대기
            for (Future<?> future : futures) {
                future.get();
            }

            executorService.shutdown();

            // 모든 프로세스가 종료되면 실행
            System.out.println("마지막 프로세스");
            Thread.sleep(300);
            // ArrayList<PublicLost> 만들기
            mapToListPublicLost();

            // 객체를 리스트를 DB에 저장 => 경찰청, 포탈데이터 테이블 다르게 저장
            int scCnt = 0;
            if(isPolice){
                if(publicLostList.isEmpty()){
                    System.out.println("데이터가 없습니다. ");
                }else {
                    scCnt = publicLostItemsMapper.insertPoliceLostList(publicLostList);
                }
            }else{
                if(publicLostList.isEmpty()){
                    System.out.println("데이터가 없습니다. ");
                }else{
                    scCnt= publicLostItemsMapper.insertPublicLostList(publicLostList);
                }
            }

            System.out.println("받아온 총 데이터 수 : " + publicLostList.size());
            System.out.println("상세정보 있는 데이터 수 : " + processStartCnt);
            System.out.println("DB 저장 성공한 데이터 수 : " + scCnt);
            System.out.println("실행된 스레드 수 : " + threadStartCount.get());
            System.out.println("종료된 스레드 수 : " + threadEndCount.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void processMap(HashMap<String, String> map) {
        try {
            String[] result = publicLostInfoApiService.getInfoProcess(this.isPolice, map.get("atcId"), map.get("fdSn"));
            map.put("tel", result[0]);
            map.put("uniq", result[1]);
            processResultQueue.add(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mapToListPublicLost() {
        while (!processResultQueue.isEmpty()) {
            HashMap<String, String> map = processResultQueue.poll();
            // 만약 받아온 map 에 id 가 없다면 넘어가기
            if (!map.containsKey("atcId")) continue;
            PublicLost publicLost = new PublicLost();
            for (String colName : map.keySet()) {
                String value = map.get(colName);
                if (value != null && value.isEmpty()){
                    value = null;
                }else if(value != null){
                    value = value.replaceAll("'","");
                }
                switch (colName) {
                    case "atcId":
                        publicLost.setActId(value);
                        break;
                    case "depPlace":
                        publicLost.setDept(value);
                        break;
                    case "fdFilePathImg":
                        publicLost.setImgUrl(value);
                        break;
                    case "fdPrdtNm":
                        publicLost.setName(value);
                        break;
                    case "fdYmd":
                        // 입력 문자열이 yyyy-MM-dd 형식인 경우 시간 부분을 추가하여 변환
                        LocalDate localDate = LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        LocalDateTime localDateTimeValue = localDate.atStartOfDay(); // 00:00:00 시간을 추가
                        publicLost.setDate(Timestamp.valueOf(localDateTimeValue));
                        break;
                    case "prdtClNm":
                        // 분류 있으면
                        if (value.contains("&gt;")) {
                            String value1 = value.replaceAll("\\s", "");
                            String[] tmStrArr = value1.split("&gt;");
                            publicLost.setMajorCategory(tmStrArr[0]);
                            publicLost.setMinorCategory(tmStrArr[1]);
                            // 없으면
                        } else {
                            publicLost.setMajorCategory(value);
                            publicLost.setMinorCategory(value);
                        }
                        break;
                    case "rnum":
                        publicLost.setGainNum(value);
                        break;
                    case "tel":
                        publicLost.setTel(value);
                        if (value != null && !value.isEmpty()) {
                            String[] values = value.split("-");
                            publicLost.setFrontTel(values[0]);
                        } else {
                            publicLost.setFrontTel(null);
                        }
                        break;
                    case "uniq":
                        publicLost.setContent(value);
                        break;
                }
            }
            publicLost.setIdPlus(publicLost.getActId() + publicLost.getGainNum());
            publicLostList.add(publicLost);
        }
    }
}
