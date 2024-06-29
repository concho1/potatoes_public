package com.goott.potatoes.user.controller;

import com.goott.potatoes.common.model.Alarm;
import com.goott.potatoes.common.model.Image;
import com.goott.potatoes.common.service.ImageService;
import com.goott.potatoes.user.model.User;
import com.goott.potatoes.user.service.MyPageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
@RequestMapping
public class MyPageController {

    @Autowired
    private MyPageService myPageService;
    @Autowired
    private ImageService imageService;

    // 이미지 갱신
    public void refreshImgUrl(HttpServletRequest request, String k){
        Optional<Image> imageOp = imageService.findImageByKey(k);
        if(imageOp.isPresent()) {
            request.getSession().setAttribute("userImgUrl", imageOp.get().getUrl());
        }else{
            System.out.println("이미지 키 없음");
        }
    }

    // myPage, 수정 , 비밀번호 변경, 탈퇴 view
    @GetMapping("myPage")
    public ModelAndView content(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();

        // 세션 가져옴
        HttpSession session = request.getSession();
        // 세션에서 로그인된 사용자 정보
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            modelAndView.setViewName("redirect:/login");
            return modelAndView;
        }

        String baseImgKey = "potatoes/57ce4371-553e-4b2a-bab7-a478f8294021";
        Optional<Image> imageOp = imageService.findImageByKey(baseImgKey);

        User cont = this.myPageService.contUser(userId);
        modelAndView.addObject("dto", cont);
        modelAndView.addObject("baseUrl", imageOp.get().getUrl());
        modelAndView.setViewName("user/myPage");

        return modelAndView;
    }


    // 회원 정보 수정
    @PostMapping("/modifyOk")
    public ModelAndView modifyOk(User dto, HttpServletRequest request,
                         @RequestParam("locA") String locA, @RequestParam("locB") String locB, @RequestParam("locC") String locC,
                         @RequestPart(value = "file", required = false) MultipartFile file,
                         @RequestParam(value = "removeImage", required = false) String removeImage, Model model) {

        Alarm alarm = new Alarm(model);
        HttpSession session = request.getSession();
        String userId = (String)session.getAttribute("userId");

        if (userId == null) {
            alarm.setRedirectTo("/login");
            return new ModelAndView(alarm.getRedirectTo());
        }

        // 기본 이미지 키 설정
        String baseImgKey = "potatoes/57ce4371-553e-4b2a-bab7-a478f8294021";

        if ("true".equals(removeImage)) {
            // 이미지 삭제 요청이 있을 때
            dto.setImgKey(baseImgKey);
        } else {
            // 새 이미지 업로드 시도
            Optional<Image> uploadedImage = imageService.insertFileAndGetImage(file);

            if (uploadedImage.isPresent()) {
                // 새 이미지가 성공적으로 업로드된 경우
                dto.setImgKey(uploadedImage.get().getImgKey());
            } else {// 새 이미지 업로드 실패 시
                 // 기존 프로필 사진이 있는지 확인
                User existingUser = myPageService.contUser(userId);
                String existingImgKey = (existingUser != null) ? existingUser.getImgKey() : null;

                if (existingImgKey != null && !existingImgKey.isEmpty()) {
                    // 기존 프로필 사진이 있을 경우
                    dto.setImgKey(existingImgKey);
                } else {
                    // 기존 프로필 사진이 없을 경우
                    dto.setImgKey(baseImgKey);
                }
            }
        }
        //사용자의 변경된 정보 업데이트
        dto.setId(userId);

        String location = locA + " / " + locB + " / " + locC;
        dto.setLocation(location);

        int check = this.myPageService.updateUser(dto);

        // 변경된 사용자 정보를 세션에 업데이트
        session.setAttribute("user", dto);
        session.setAttribute("userNickname", dto.getNickname());

        if(check > 0){
            // 이미지 갱신 메서드 호출
            refreshImgUrl(request, dto.getImgKey());
            session.setAttribute("userImgKey", dto.getImgKey());
            alarm.setMessageAndRedirect("수정되었습니다.","myPage");
        }else {
            alarm.setMessageAndRedirect("수정에 실패하였습니다","");
        }

        return new ModelAndView(alarm.getMessagePage());

    }

    // 비밀번호 변경
    @PostMapping("/changePwdOk")
    public ModelAndView changePwd(@RequestParam("pwd") String currentPwd, @RequestParam("newPwd") String newPwd,
                          User dto, HttpServletRequest request,Model model) {
        Alarm alarm = new Alarm(model);
        HttpSession session = request.getSession();
        String userId = (String)session.getAttribute("userId");
        if (userId == null) {
            alarm.setRedirectTo("/login");
            return new ModelAndView(alarm.getRedirectTo());
        }

        // 사용자가 입력한 기존 비밀번호와 DB에 저장된 비밀번호가 일치하는지 확인
        if(myPageService.checkPwd(userId, currentPwd)){
            // 세션에서 가져온 사용자 ID를 사용하여 사용자의 변경된 정보 업데이트
            dto.setId(userId);
            // 비밀번호 업데이트
            int check = this.myPageService.updatePwd(userId,newPwd);
            // 변경된 사용자 정보를 세션에 업데이트
            session.setAttribute("user", dto);
            if(check > 0){
                alarm.setMessageAndRedirect("비밀번호가 변경되었습니다.","myPage");
            }else {
                alarm.setMessageAndRedirect("비밀번호 변경 실패","");
            }
            return new ModelAndView(alarm.getMessagePage());
        }else {
            alarm.setMessageAndRedirect("기존 비밀번호가 일치하지 않습니다.","");
        }
        return new ModelAndView(alarm.getMessagePage());
    }

    // 회원 탈퇴
    @PostMapping("/deleteOk")
    public ModelAndView delOk(@RequestParam("pwd") String pwd, HttpServletRequest request, Model model) {
        Alarm alarm = new Alarm(model);
        HttpSession session = request.getSession();
        String userId = (String)session.getAttribute("userId");
        if (userId == null) {
            alarm.setRedirectTo("/login");
            return new ModelAndView(alarm.getRedirectTo());
        }
        User cont = this.myPageService.contUser(userId);
        model.addAttribute("dto", cont);

        if(pwd.equals(cont.getPwd())){
            int check = this.myPageService.deleteUser(userId);
            if(check > 0){
                alarm.setMessageAndRedirect("탈퇴 성공했습니다.","/logout");
            }else{
                alarm.setMessageAndRedirect("탈퇴 실패, 다시 시도해주세요.","");
            }
            return new ModelAndView(alarm.getMessagePage());
        }else {
            alarm.setMessageAndRedirect("비밀번호가 틀렸습니다.","");
        }
        return new ModelAndView(alarm.getMessagePage());
    }

    // 카카오 회원 탈퇴
    @PostMapping("/deleteOkNull")
    public ModelAndView delOkNull(HttpServletRequest request, Model model){
        Alarm alarm = new Alarm(model);
        HttpSession session = request.getSession();
        String userId = (String)session.getAttribute("userId");
        if (userId == null) {
            alarm.setRedirectTo("/login");
            return new ModelAndView(alarm.getRedirectTo());
        }
        User cont = this.myPageService.contUser(userId);
        model.addAttribute("dto", cont);
        int check = this.myPageService.deleteUser(userId);
        if(check > 0){
            alarm.setMessageAndRedirect("탈퇴 성공했습니다.","/logout");
        }else{
            alarm.setMessageAndRedirect("탈퇴 실패, 다시 시도해주세요.","");
        }
        return new ModelAndView(alarm.getMessagePage());
    }
}