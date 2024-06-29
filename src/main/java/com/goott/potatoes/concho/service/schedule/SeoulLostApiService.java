package com.goott.potatoes.concho.service.schedule;

import com.goott.potatoes.concho.mapper.SeoulMapper;
import com.goott.potatoes.concho.model.Seoul;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class SeoulLostApiService {
    @Autowired
    private XmlToMapListService xmlToMapListService;
    @Autowired
    private ApiToStringService seoulApiService;
    @Autowired
    private SeoulMapper seoulMapper;
    @Value("${concho.seoul.key}")
    private String seoulKey;

    // 24 시간 마다 실행
    //@Scheduled(fixedDelay = 3600000*24)
    public void seoulLostSchedule(LocalDate startDate, LocalDate endDate){
        // 가져올 데이터 목록
        String[] columnName = {"ID", "STATUS", "REG_DATE", "GET_THING","TAKE_PLACE", "GET_NAME", "CATE", "GET_POSITION"};
        var seoulList = new ArrayList<Seoul>();
        try{
            // 날짜 형식을 정의
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // 시작 날짜부터 현재 날짜까지 반복
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                // 날짜를 원하는 형식으로 변환하여 문자열로 저장
                String formattedDate = date.format(formatter);
                String urlBuilder =
                        "http://openapi.seoul.go.kr:8088" +
                        "/" + URLEncoder.encode(seoulKey, StandardCharsets.UTF_8) + /*인증키 (sample사용시에는 호출시 제한됩니다.)*/
                        "/" + URLEncoder.encode("xml", StandardCharsets.UTF_8) + /*요청파일타입 (xml,xmlf,xls,json) */
                        "/" + URLEncoder.encode("lostArticleInfo", StandardCharsets.UTF_8) + /*서비스명 (대소문자 구분 필수입니다.)*/
                        "/" + URLEncoder.encode("1", StandardCharsets.UTF_8) + /*요청시작위치 (sample인증키 사용시 5이내 숫자)*/
                        "/" + URLEncoder.encode("1000", StandardCharsets.UTF_8) + "/%20/" + date;

                // 서울시 api 이용해서 xml 페이지 가져오기
                Optional<String> xmlStrOp = seoulApiService.getApiToXmlString(urlBuilder);
                if(xmlStrOp.isEmpty()){
                    System.out.println("해당 날짜의 데이터가 없습니다." + formattedDate);
                    return;
                }
                // String 형태의 xml 을 map 형식의 List 로 바꿔서 넘겨주는 서비스
                var mapList = xmlToMapListService.xmlStrToStringList(xmlStrOp.get(), columnName);
                // mapList 를 seoul List 로 만드는 메서드
                mapToList(mapList, seoulList);
            }

            // 객체를 리스트를 DB에 저장
            if(seoulList.isEmpty()){
                System.out.println("서울시 데이터가 없습니다.");
            }else{
                int scCnt = seoulMapper.insertSeoulList(seoulList);
                System.out.println("서울시에서 받아온 데이터 수 : " +seoulList.size());
                System.out.println("DB 저장 성공한 데이터 수 : " +scCnt);
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }



    public void mapToList(List<HashMap<String, String>> mapList, ArrayList<Seoul> seoulList){
        for(HashMap<String, String> map : mapList){
            // 만약 받아온 map 에 id 가 없다면 넘어가기
            if(!map.containsKey("ID")) continue;
            Seoul seoul = new Seoul();
            for(String colName : map.keySet()){
                String value = map.get(colName);
                if (value != null && value.isEmpty()) {
                    value = null;
                }else if(value != null){
                    value = value.replaceAll("'","");
                }
                switch (colName){
                    case "GET_THING" :
                        seoul.setCont(value);
                        break;
                    case "CATE" :
                        seoul.setCategory(value);
                        break;
                    case "STATUS" :
                        seoul.setStatus(value);
                        break;
                    case "GET_NAME" :
                        seoul.setProductName(value);
                        break;
                    case "REG_DATE" :
                        // 입력 문자열이 yyyy-MM-dd 형식인 경우 시간 부분을 추가하여 변환
                        LocalDate localDate = LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        LocalDateTime localDateTimeValue = localDate.atStartOfDay(); // 00:00:00 시간을 추가
                        seoul.setDate(Timestamp.valueOf(localDateTimeValue));
                        break;
                    case "TAKE_PLACE" :
                        seoul.setTakePlace(value);
                        break;
                    case "ID" :
                        seoul.setActId(value);
                        break;
                    case "GET_POSITION" :
                        seoul.setOrgName(value);
                        break;
                }
            }
            seoulList.add(seoul);
        }
    }
}
