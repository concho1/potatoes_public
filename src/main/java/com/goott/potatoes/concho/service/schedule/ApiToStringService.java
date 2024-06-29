package com.goott.potatoes.concho.service.schedule;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

@Service
public class ApiToStringService {
    public Optional<String> getApiToXmlString(String urlBuilder) {
        Optional<String> resultOptionalString = Optional.empty();
        try {
            URL url = new URL(urlBuilder);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/xml");

            // 타임아웃 설정 (밀리초 단위)
            conn.setConnectTimeout(600000); // 10분 연결 타임아웃
            conn.setReadTimeout(600000); // 10분 읽기 타임아웃

            BufferedReader rd;

            // 서비스코드가 정상이면 200~300사이의 숫자가 나옵니다.
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();

            resultOptionalString = Optional.of(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultOptionalString;
    }
}
