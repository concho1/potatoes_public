package com.goott.potatoes.police.model;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

@Service
public class PoliceListService {

    public ArrayList<HashMap<String, String>> policeList() {

        ArrayList<HashMap<String, String>> list1 = new ArrayList<>();
        String[] tag = {"atcId", "depPlace", "fdFilePathImg", "fdPrdtNm", "fdSbjt", "fdSn", "fdYmd", "prdtClNm", "rnum"};

        try {
            String urlBuilder1 = "http://apis.data.go.kr/1320000/LosfundInfoInqireService/getLosfundInfoAccToClAreaPd" +
                    "?" + URLEncoder.encode("serviceKey", StandardCharsets.UTF_8) + "=" + "ZJ6F1o%2Fl5RvjflG9i8CfzcC0mhU0buJCcjB5u33RhMaj1u0Ep0aOG2FVTrSmLXesFpS0hXNDj%2Fu3QVmDj5Pe4Q%3D%3D" +
                    "&" + URLEncoder.encode("START_YMD", StandardCharsets.UTF_8) + "=" + "20240301" +
                    "&" + URLEncoder.encode("pageNo", StandardCharsets.UTF_8) + "=" + "1" +
                    "&" + URLEncoder.encode("numOfRows", StandardCharsets.UTF_8) + "=" + "1000";



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

            list1 = xmlStrToStringList1(sb1.toString(), tag);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list1;
    }

    public ArrayList<HashMap<String, String>> xmlStrToStringList1(String xmlStr, String[] tags){
        var columnSet = new HashSet<>(Arrays.asList(tags));
        var mapList = new ArrayList<HashMap<String, String>>();
        int mapSize = columnSet.size();
        try {
            var columnMap1 = new HashMap<String, String>();
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
                    if(columnSet.contains(tag)) {
                        cnt++;
                        columnMap1.put(tag, value);
                        if(cnt >= mapSize){
                            mapList.add(columnMap1);
                            columnMap1 = new HashMap<>();
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
        return mapList;
    }
}
