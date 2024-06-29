package com.goott.potatoes.concho.service.schedule;

import com.goott.potatoes.concho.mapper.PublicLostItemsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
@Service
public class PublicLostInfoApiService {
    @Autowired
    private XmlToMapListService xmlToMapListService;
    @Autowired
    private PublicLostItemsMapper publicLostItemsMapper;
    @Autowired
    private ApiToStringService ApiToStringService;
    @Value("${concho.potal.key}")
    private String publicLostKey;

    public String[] getInfoProcess(boolean isPolice, String atcId, String fdSn){
        String tel = null;
        String uniq = null;
        String[] result = {null, null};
        try{
            // url 만들고
            String publicLostInfoUrl =
                    (   (isPolice) ?
                        "http://apis.data.go.kr/1320000/LosfundInfoInqireService/getLosfundDetailInfo" :
                        "http://apis.data.go.kr/1320000/LosPtfundInfoInqireService/getPtLosfundDetailInfo"
                    ) +
                    "?" + URLEncoder.encode("serviceKey", StandardCharsets.UTF_8) +
                    "=" + publicLostKey + (
                        (isPolice) ?
                        (
                                "&" + URLEncoder.encode("pageNo", StandardCharsets.UTF_8) +
                                "=" + URLEncoder.encode("1", StandardCharsets.UTF_8) +
                                "&" + URLEncoder.encode("numOfRows", StandardCharsets.UTF_8) +
                                "=" + URLEncoder.encode("10", StandardCharsets.UTF_8)
                        ): ""
                    ) +
                    "&" + URLEncoder.encode("ATC_ID", StandardCharsets.UTF_8) +
                    "=" + URLEncoder.encode(atcId, StandardCharsets.UTF_8) +             /* act_id */
                    "&" + URLEncoder.encode("FD_SN", StandardCharsets.UTF_8) +
                    "=" + URLEncoder.encode(fdSn, StandardCharsets.UTF_8);              /* gain_num */

            // api 이용해서 xml 페이지 가져오기
            Optional<String> infoXmlStrOp = ApiToStringService.getApiToXmlString(publicLostInfoUrl);

            String[] columnNames = {"tel", "uniq"};
            if(infoXmlStrOp.isPresent()) {
                // String 형태의 xml 을 map 형식의 List 로 바꿔서 넘겨주는 서비스
                List<HashMap<String, String>> infoMapList = xmlToMapListService.xmlStrToStringList(infoXmlStrOp.get(), columnNames);
                for(HashMap<String, String> infoMap : infoMapList){
                    tel = infoMap.get("tel");
                    uniq = infoMap.get("uniq");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return result;
        }
        result[0] = tel;
        result[1] = uniq;
        return result;
    }
}
