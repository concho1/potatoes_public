package com.goott.potatoes.police.model;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class PoliceContService {

    public Police policeCont(Police pdto) {

        Police cont = new Police();
        String id = pdto.getAtcId();
        String fdSn = pdto.getFdSn();

        try {
            String urlBuilder1 = "http://apis.data.go.kr/1320000/LosfundInfoInqireService/getLosfundDetailInfo" +
                    "?" + URLEncoder.encode("serviceKey", StandardCharsets.UTF_8) + "=" + "ZJ6F1o%2Fl5RvjflG9i8CfzcC0mhU0buJCcjB5u33RhMaj1u0Ep0aOG2FVTrSmLXesFpS0hXNDj%2Fu3QVmDj5Pe4Q%3D%3D" +
                    "&" + URLEncoder.encode("ATC_ID", StandardCharsets.UTF_8) + "=" + id +
                    "&" + URLEncoder.encode("FD_SN", StandardCharsets.UTF_8) + "=" + fdSn;


            URL url1 = new URL(urlBuilder1);

            HttpURLConnection conn1 = (HttpURLConnection) url1.openConnection();

            conn1.setRequestMethod("GET");
            conn1.setRequestProperty("Content-Type", "application/xml");

            BufferedReader rd1;

            if(conn1.getResponseCode() >= 200 && conn1.getResponseCode() < 300) {
                rd1 = new BufferedReader(new InputStreamReader(conn1.getInputStream()));
            }else {
                rd1 = new BufferedReader(new InputStreamReader(conn1.getErrorStream()));
            }

            StringBuilder sb1 = new StringBuilder();
            String line1;

            while((line1 = rd1.readLine()) != null) {
                sb1.append(line1);
            }

            rd1.close();
            conn1.disconnect();

            cont = xmlStrToStringList2(sb1.toString());

            String key = cont.getAtcId() + cont.getFdSn();
            cont.setPoliceKey(key);

            String new_pName = cont.getProductName().replaceAll("&gt;", ">");
            cont.setProductName(new_pName);

            String new_cont = cont.getCont().replaceAll("&#xD;", " ");
            cont.setCont(new_cont);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cont;
    }

    public Police xmlStrToStringList2(String xmlStr){
        var columnMap2 = new HashMap<String, String>(Map.of(
                "atcId", "atcId",
                "csteSteNm", "csteSteNm",
                "fdFilePathImg", "fdFilePathImg",
                "fdPlace", "fdPlace",
                "fdSn", "fdSn",
                "fdYmd", "fdYmd",
                "orgNm", "orgNm",
                "prdtClNm", "prdtClNm",
                "tel", "tel",
                "uniq", "uniq"
        ));

        Police dto = new Police();

        int mapSize = columnMap2.size();
        Set<String> keys = columnMap2.keySet();
        try {
            char[] xmlCharArr = xmlStr.toCharArray();

            StringBuilder tagBuilder = new StringBuilder();
            StringBuilder valueBuilder = new StringBuilder();
            boolean isTagCh = false;
            int cnt = 0;
            String tag = "";
            String value = "";
            for(char ch : xmlCharArr){

                if(ch == '<'){
                    isTagCh = true;
                    value = valueBuilder.toString();
                    if(columnMap2.containsKey(tag)){
                        cnt++;
                        if(tag.equals("atcId")) {
                            dto.setAtcId(value);
                        }else if(tag.equals("csteSteNm")){
                            dto.setStatus(value);
                        }else if(tag.equals("fdFilePathImg")){
                            dto.setFilePathImg(value);
                        }else if(tag.equals("fdPlace")){
                            dto.setPlace(value);
                        }else if(tag.equals("fdSn")){
                            dto.setFdSn(value);
                        }else if(tag.equals("fdYmd")){
                            // 입력 문자열이 yyyy-MM-dd 형식인 경우 시간 부분을 추가하여 변환
                            LocalDate localDate = LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                            LocalDateTime localDateTimeValue = localDate.atStartOfDay(); // 00:00:00 시간을 추가
                            dto.setDate(Timestamp.valueOf(localDateTimeValue));
                        }else if(tag.equals("orgNm")){
                            dto.setOrgName(value);
                        }else if(tag.equals("prdtClNm")){
                            dto.setProductName(value);
                        }else if(tag.equals("tel")){
                            dto.setOrgTel(value);
                        }else {
                            dto.setCont(value);
                        }

                        if(cnt >= mapSize){
                            cnt = 0;
                        }

                    }

                    valueBuilder.setLength(0);
                } else if (ch == '>') {
                    isTagCh = false;
                    if(!tagBuilder.toString().isEmpty()){
                        tag = tagBuilder.toString();
                    }
                    tagBuilder.setLength(0);
                }else if(isTagCh){
                    tagBuilder.append(ch);
                }else{
                    valueBuilder.append(ch);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return dto;
    }

}
