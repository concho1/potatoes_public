package com.goott.potatoes.common.testController;

import com.goott.potatoes.common.model.Image;
import com.goott.potatoes.common.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/test-api")
public class TestRestController {
    @Autowired
    private ImageService imageService;
    @PostMapping("/image")
    public Map<String, String> uploadImgApi(
            @RequestParam("nickname") String nickname,
            @RequestPart("file") MultipartFile multipartFile){
        System.out.println(nickname);
        // 응답 map
        var responseMap = new HashMap<String, String>();

        // 이미지 업로드
        Optional<Image> imageOp = imageService.insertFileAndGetImage(multipartFile);

        if(imageOp.isEmpty()){
            responseMap.put("isOk", "no");  // Optional empty 면 no
        }else {
            Image image = imageOp.get();    // Optional 풀기
            responseMap.put("isOk", "ok");
            responseMap.put("imageUrl", image.getUrl());
        }
        return responseMap;
    }


}
