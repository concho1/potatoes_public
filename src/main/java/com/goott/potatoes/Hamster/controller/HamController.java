package com.goott.potatoes.Hamster.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.goott.potatoes.Hamster.model.missing;
import com.goott.potatoes.Hamster.model.pickup;
import com.goott.potatoes.Hamster.model.userHam;
import com.goott.potatoes.Hamster.service.*;
import com.goott.potatoes.common.model.Alarm;
import com.goott.potatoes.common.model.Image;
import com.goott.potatoes.common.service.ImageService;
import com.goott.potatoes.concho.model.UniqueCategories;
import com.goott.potatoes.concho.service.cate.CateService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@RestController
public class HamController {
    @Autowired
    private KakaoApi kakaoApi;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private userService userService;
    @Autowired
    private missingService missingService;
    @Autowired
    private pickupService pickupService;
    @Autowired
    private CateService cateService;

    @GetMapping("/join")
    public ModelAndView join(){
        ModelAndView modelAndView = new ModelAndView("join/join");
        String baseImgKey = "potatoes/57ce4371-553e-4b2a-bab7-a478f8294021";
        Optional<Image> imageOp = imageService.findImageByKey(baseImgKey);
        if(imageOp.isPresent()){
            modelAndView.addObject("baseImgUrl", imageOp.get().getUrl());
        }

        return modelAndView;
    }

    @PostMapping("/sendEmail")
    public Map<String, String> sendEmail(@RequestParam("email") String email, HttpServletRequest httpServletRequest) throws MessagingException {
        HttpSession session = httpServletRequest.getSession();

        emailService.sendEmail(email, session);
        return new HashMap<>(Map.of("message", "이메일 전송 성공"));
    }

    @PostMapping("/sendPwdEmail")
    public Map<String, String> sendPwdEmail(@RequestParam("email") String email, HttpServletRequest httpServletRequest) throws MessagingException {
        HttpSession session = httpServletRequest.getSession();
        emailService.sendPwdEmail(email, session);
        return new HashMap<>(Map.of("message", "이메일 전송 성공"));
    }

    @PostMapping("/checkPwdInfo")
    public Boolean checkPwdInfo(@RequestParam("email") String email, @RequestParam("nickname") String nickname) {
        HashMap<String,String> map = new HashMap<>();
        map.put("nickname",nickname);
        map.put("email",email);
        Boolean result = this.userService.checkPwdInfo(map);
        return result;
    }

    @PostMapping("/changePwd")
    public ModelAndView changePwd(@RequestParam("nickname")String nickname){

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("nickname",nickname);
        modelAndView.setViewName("join/changePWD");
        return modelAndView;
    }

    @PostMapping("/changePwd_ok")
    public ModelAndView changePwdOk(@RequestParam("nickname")String nickname,
                                   @RequestParam("pwd")String pwd,
                                   Model model){
        Alarm alarm = new Alarm(model);
        HashMap<String,String> map = new HashMap<>();
        map.put("nickname",nickname);
        map.put("pwd",pwd);
        int check = this.userService.changePwd(map);

        if(check > 0){
            alarm.setMessageAndRedirect("비밀번호가 변경되었습니다","/login");
        }else{
            alarm.setMessageAndRedirect("비밀번호가 변경 실패","");
        }

        return new ModelAndView(alarm.getMessagePage());
    }

    @PostMapping("/codeCatch")
    public Map<String, String> codeCatcher(@RequestParam("code") String inCode, HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession();
        System.out.println("사용자가 입력한 Code: " + inCode);

        if (inCode.equals(session.getAttribute("sCode"))) {
            return new HashMap<>(Map.of("good", "이메일 인증이 완료되었습니다."));
        } else {
            return new HashMap<>(Map.of("fail", "인증코드가 일치하지않습니다."));
        }

    }

    @PostMapping("/checkDupEmail")
    public Boolean checkDupEmail(@RequestParam("email") String email) {
        Boolean result = this.userService.checkDupEmail(email);
        return result;
    }

    @PostMapping("/checkDupNickname")
    public Boolean checkDupNickname(@RequestParam("nickname") String nickname) {
        Boolean result = this.userService.checkDupNickname(nickname);
        return result;
    }

    @PostMapping("/pwdDupPwd")
    public Boolean checkDupPwd(@RequestParam("pwd") String pwd) {
        Boolean result = this.userService.checkDupPwd(pwd);
        return result;
    }

    @PostMapping("/joinOk")
    public ModelAndView join(@RequestParam("locA") String loca,
                             @RequestParam("locB") String locb,
                             @RequestParam("locC") String locc,
                             @RequestPart("file") MultipartFile file,
                             userHam user,
                             HttpServletRequest request,
                             Model model) throws IOException {
        HttpSession session = request.getSession();
        Alarm alarm = new Alarm(model);

        Optional<Image> imageOp = imageService.insertFileAndGetImage(file);
        if (imageOp.isEmpty()) {
            String baseImgKey = "potatoes/57ce4371-553e-4b2a-bab7-a478f8294021";
            user.setImgKey(baseImgKey);
        } else {
            user.setImgKey(imageOp.get().getImgKey());
        }
        user.setLocation(loca + " / " + locb + " / " + locc);
        int check = this.userService.joinUser(user);
        if (check > 0) {
            alarm.setMessageAndRedirect("회원가입이 완료되었습니다.", "concho/main");
        } else {
            alarm.setMessageAndRedirect("회원가입에 오류가 발생되었습니다.", "");
        }
        session.setAttribute("userId", user.getId());
        session.setAttribute("userNickname", user.getNickname());
        session.setAttribute("userImgKey", user.getImgKey());
        session.setAttribute("userRank", user.getMannerRank());
        session.setAttribute("userImgUrl",imageService.findImageByKey(user.getImgKey()).get().getUrl());

        this.userService.joinCnt();
        return new ModelAndView(alarm.getMessagePage());
    }

    @GetMapping("/login")
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("join/login");

        return modelAndView;
    }

    @GetMapping("/login/kakao")
    public RedirectView kakao(HttpServletRequest request){
        String fullURL = request.getRequestURL().toString();
        String extractedURL = fullURL.substring(0, fullURL.indexOf("/login"));
        System.out.println(extractedURL);

        String kakaoApiKey = kakaoApi.getKakaoApiKey();
        String redirectUri = extractedURL+kakaoApi.getKakaoRedirectUri();
        String authUrl = "https://kauth.kakao.com/oauth/authorize?client_id=" + kakaoApiKey + "&redirect_uri=" + redirectUri + "&response_type=code";

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(authUrl);
        return redirectView;
    }

    @PostMapping("/loginOk")
    public ModelAndView loginOk(userHam user, Model model, HttpServletRequest request) {

        HttpSession session = request.getSession();
        Alarm alarm = new Alarm(model);
        userHam db = this.userService.Login(user.getId());

        if (db == null) {
            alarm.setMessageAndRedirect("아이디가 틀렸거나 존재하지 않는 아이디입니다", "");
            return new ModelAndView(alarm.getMessagePage());
        }
        if (!user.getPwd().equals(db.getPwd())) {
            alarm.setMessageAndRedirect("비밀번호가 틀렸습니다", "");
            return new ModelAndView(alarm.getMessagePage());
        }
        session.setAttribute("userId", db.getId());
        session.setAttribute("userNickname", db.getNickname());
        session.setAttribute("userImgKey", db.getImgKey());
        session.setAttribute("userImgUrl", imageService.findImageByKey(db.getImgKey()).get().getUrl());
        if(db.getMannerRank() == null) {
            session.setAttribute("userRank", "");
        }else {
            session.setAttribute("userRank", db.getMannerRank());
        }

        alarm.setMessageAndRedirect("","/concho/main");
        this.userService.loginCnt();
        return new ModelAndView(alarm.getMessagePage());
    }

    @GetMapping("/searchPWD")
    public ModelAndView searchPWD() {return new ModelAndView("join/searchPWD");}

    @RequestMapping("/login/oauth2/code/kakao")
    public ModelAndView KaKaoLogin(@RequestParam(value = "code", required = false) String code, HttpServletRequest request, Model model) throws JsonProcessingException, MalformedURLException {

        Alarm alarm = new Alarm(model);
        if(code == null){
            alarm.setMessageAndRedirect("동의항목에 동의를 하셔야 카카오 로그인이 가능합니다.","/login");
            return new ModelAndView(alarm.getMessagePage());
        }
        String fullURL = request.getRequestURL().toString();
        String baseURL = fullURL.substring(0, fullURL.indexOf("/login"));
        System.out.println(baseURL);

        System.out.println("코드 : " + code);
        String Tok = this.kakaoApi.getAccessToken(code, baseURL);
        System.out.println("토큰 : " + Tok);
        userHam dto = this.kakaoApi.getKakaoInfo(Tok);

        System.out.println(dto.getId());
        System.out.println(dto.getNickname());
        System.out.println(dto.getImgKey());

        // 이미 DB에 있는 이메일인지 확인
        // 이미 있으면
        // DB 에서 해당하는 이메일의 데이터를 꺼내와서 세션에 넣어주고 페이지 반환
        Boolean check = this.userService.checkDupEmail(dto.getId());
        if (check) {
            // 기존에 저장되있는 정보 가져오기
            userHam userHamInstance = this.userService.Login(dto.getId());
            HttpSession session = request.getSession();
            session.setAttribute("userId", userHamInstance.getId());
            session.setAttribute("userNickname", userHamInstance.getNickname());
            session.setAttribute("userImgKey", userHamInstance.getImgKey());
            session.setAttribute("userImgUrl", imageService.findImageByKey(userHamInstance.getImgKey()).get().getUrl());
            this.userService.loginCnt();
            alarm.setMessageAndRedirect("","/concho/main");
            return new ModelAndView(alarm.getMessagePage());
        }

        MultipartFile multipartFile = null;
        URL url = new URL(dto.getImgKey());
        try (InputStream in = url.openStream()) {
            byte[] imageBytes = IOUtils.toByteArray(in);

            // MultipartFile로 변환
            multipartFile = new MockMultipartFile("file", "image.jpg", MediaType.IMAGE_JPEG_VALUE, imageBytes);

            String baseImgKey = "potatoes/57ce4371-553e-4b2a-bab7-a478f8294021";
            Optional<Image> imageOp = imageService.insertFileAndGetImage(multipartFile);
            if (imageOp.isPresent()) {
                dto.setImgKey(imageOp.get().getImgKey());
            } else {
                dto.setImgKey(baseImgKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // db 저장
        this.userService.joinKakaoUser(dto);

        // 세션 저장
        HttpSession session = request.getSession();
        session.setAttribute("userId", dto.getId());
        session.setAttribute("userNickname", dto.getNickname());
        session.setAttribute("userImgKey", dto.getImgKey());
        session.setAttribute("userImgUrl", imageService.findImageByKey(dto.getImgKey()).get().getUrl());
        this.userService.joinCnt();
          alarm.setMessageAndRedirect("","/concho/main");
          return new ModelAndView(alarm.getMessagePage());
      }

      @GetMapping("/logout")
      public ModelAndView logout(HttpServletRequest request,Model model){
          Alarm alarm = new Alarm(model);
          HttpSession session = request.getSession(false);
          if (session != null) {
              session.invalidate();
          }
          alarm.setMessageAndRedirect("","/concho/main");
          return new ModelAndView(alarm.getMessagePage());
  }

  @GetMapping("/missingBoard_write_fix")
  public ModelAndView pickUpBoard_write(@RequestParam("no")int no) {

      HashMap<String, ArrayList<String>> catelist = cateService.getCateListMap();

      ModelAndView modelAndView = new ModelAndView("missingBoard/missingBoard_write");

      String baseImgKey = "potatoes/b61d87ce-b7a4-42a2-a839-bcc3cf8b4ea8";

      List<String> cityList = new ArrayList<>();

      // 도시명 추가
      cityList.add("서울특별시");
      cityList.add("인천광역시");
      cityList.add("대전광역시");
      cityList.add("부산광역시");
      cityList.add("울산광역시");
      cityList.add("대구광역시");
      cityList.add("광주광역시");
      cityList.add("제주특별자치도");
      cityList.add("경기도");
      cityList.add("강원도");
      cityList.add("충청남도");
      cityList.add("충청북도");
      cityList.add("경상북도");
      cityList.add("경상남도");
      cityList.add("전라남도");
      cityList.add("전라북도");

      Optional<Image> imageOp = imageService.findImageByKey(baseImgKey);
      if (imageOp.isPresent()) {
          modelAndView.addObject("noneImg", imageOp.get().getUrl());
      }

      modelAndView.addObject("list", cityList).addObject("catelist", catelist)
              .addObject("side_chk", true).addObject("fix",no);
      return modelAndView;
  }

    @GetMapping("/missingBoard_write")
    public ModelAndView missingBoard_write(){

     HashMap<String,ArrayList<String>> catelist = cateService.getCateListMap();

      ModelAndView modelAndView = new ModelAndView("missingBoard/missingBoard_write");

      String baseImgKey = "potatoes/b61d87ce-b7a4-42a2-a839-bcc3cf8b4ea8";

      List<String> cityList = new ArrayList<>();

      // 도시명 추가r
      cityList.add("서울특별시");
      cityList.add("인천광역시");
      cityList.add("대전광역시");
      cityList.add("부산광역시");
      cityList.add("울산광역시");
      cityList.add("대구광역시");
      cityList.add("광주광역시");
      cityList.add("제주특별자치도");
      cityList.add("경기도");
      cityList.add("강원도");
      cityList.add("충청남도");
      cityList.add("충청북도");
      cityList.add("경상북도");
      cityList.add("경상남도");
      cityList.add("전라남도");
      cityList.add("전라북도");

      Optional<Image> imageOp = imageService.findImageByKey(baseImgKey);
      if(imageOp.isPresent()){
          modelAndView.addObject("noneImg", imageOp.get().getUrl());
      }

      modelAndView.addObject("list",cityList).addObject("catelist",catelist).addObject("side_chk",true);
    return modelAndView;
}

    @PostMapping("/missingBoard_write_ok")
    public ModelAndView pickUpBoard_write_ok(@RequestPart("file") MultipartFile file, missing miss, Model model, HttpServletRequest request){
        Alarm alarm = new Alarm(model);
        HttpSession session = request.getSession();

        Optional<Image> image = imageService.insertFileAndGetImage(file);
        if(image.isEmpty()){
            String baseImgKey = "potatoes/a4cf8e39-f82b-4fdc-8047-dc4a71cb2460";
            miss.setImgKey(baseImgKey);
        }else{
            miss.setImgKey(image.get().getImgKey());
        }

        String userNickname = (String) session.getAttribute("userNickname");
        miss.setUserId(userNickname);

        int check = 0;
        if(miss.getCategoryId() == 1){
            check = this.pickupService.pickupboard_write(miss);
            if(check > 0){
                alarm.setMessageAndRedirect("게시글이 작성되었습니다.","/pickup_Board_list");
            }else{
                alarm.setMessageAndRedirect("게시글 작성 실패.","");
            }

            return new ModelAndView(alarm.getMessagePage());

        }else{
            check = this.missingService.missingboard_write(miss);

            if(check > 0){
                alarm.setMessageAndRedirect("게시글이 작성되었습니다.","/missing_Board_list");
            }else{
                alarm.setMessageAndRedirect("게시글 작성 실패.","");
            }

            return new ModelAndView(alarm.getMessagePage());
        }
    }

    @GetMapping("/missing_Board_list")
    public ModelAndView mBoard_list(){

       List<missing> list = this.missingService.missingBoardList();
        ModelAndView modelAndView = new ModelAndView("missingBoard/missingBoard_list");

        for(missing miss : list){
            miss.setImgKey(imageService.findImageByKey(miss.getImgKey()).get().getUrl());
        }

        modelAndView.addObject("list",list).addObject("side_chk",true);
        return modelAndView;
    }

    @GetMapping("/missingBoard_cont")
    public ModelAndView mBoard_cont(@RequestParam("no")int no){
         missing cont = this.missingService.mBoardCont(no);
         this.missingService.mboard_hit(no);
         cont.setImgKey(imageService.findImageByKey(cont.getImgKey()).get().getUrl());
         return new ModelAndView("missingBoard/missingBoard_cont").addObject("cont",cont).addObject("side_chk",true);
    }

    @GetMapping("/mboard_modify")
    public ModelAndView mBoardModify(@RequestParam("no")int no,HttpServletRequest request,Model model){

        Alarm alarm = new Alarm(model);
        missing cont = this.missingService.mBoardCont(no);
        HttpSession session = request.getSession();
        String nickname = (String) session.getAttribute("userNickname");

        if(!nickname.equals(cont.getUserId())){
            alarm.setMessageAndRedirect("잘못된 접근입니다.","");
            return new ModelAndView(alarm.getMessagePage());
        }
        ModelAndView modelAndView = new ModelAndView("missingBoard/missingBoard_modify");

        HashMap<String,ArrayList<String>> catelist = cateService.getCateListMap();

        String baseImgKey = "potatoes/b61d87ce-b7a4-42a2-a839-bcc3cf8b4ea8";

        List<String> cityList = new ArrayList<>();

        // 도시명 추가
        cityList.add("서울특별시");
        cityList.add("인천광역시");
        cityList.add("대전광역시");
        cityList.add("부산광역시");
        cityList.add("울산광역시");
        cityList.add("대구광역시");
        cityList.add("광주광역시");
        cityList.add("제주특별자치도");
        cityList.add("경기도");
        cityList.add("강원도");
        cityList.add("충청남도");
        cityList.add("충청북도");
        cityList.add("경상북도");
        cityList.add("경상남도");
        cityList.add("전라남도");
        cityList.add("전라북도");

        Optional<Image> imageOp = imageService.findImageByKey(baseImgKey);
        if(imageOp.isPresent()){
            modelAndView.addObject("noneImg", imageOp.get().getUrl());
        }

        cont.setImgKey(imageService.findImageByKey(cont.getImgKey()).get().getUrl());
        return modelAndView.addObject("cont",cont).addObject("list",cityList).addObject("catelist",catelist);
    }

    @PostMapping("/missingBoard_modify_ok")
    public ModelAndView mBoardModify_ok(@RequestParam("no")int no,@RequestPart("file")MultipartFile file,missing miss,Model model){
        Alarm alarm = new Alarm(model);

        missing dto = this.missingService.mBoardCont(no);

        Optional<Image> image = imageService.insertFileAndGetImage(file);
        if(image.isEmpty()){
            miss.setImgKey(dto.getImgKey());
        }

        miss.setMisNum(no);
        int check = this.missingService.mBoardModify(miss);

        if(check > 0){
            alarm.setMessageAndRedirect("게시글이 수정되었습니다.","/missing_Board_list");
        }else{
            alarm.setMessageAndRedirect("게시글 수정 실패.","");
        }

        return new ModelAndView(alarm.getMessagePage());
    }

    @GetMapping("mboard_delete")
    public ModelAndView mBoardDelete(@RequestParam("no")int no,Model model){
        Alarm alarm = new Alarm(model);

        int check = this.missingService.mBoardDelete(no);

        if(check > 0){
            this.missingService.updateSequence(no);
            alarm.setMessageAndRedirect("게시글이 삭제되었습니다.","/missing_Board_list");
        }else{
            alarm.setMessageAndRedirect("게시글 삭제 실패","");
        }

        return new ModelAndView(alarm.getMessagePage());
    }

    @GetMapping("/pickup_Board_list")
    public ModelAndView pickupBoard_list(){
        List<pickup> list = this.pickupService.pickBoardList();
        ModelAndView modelAndView = new ModelAndView("pickupBoard/pickBoard_list");

        for(pickup pick : list){
            pick.setImgKey(imageService.findImageByKey(pick.getImgKey()).get().getUrl());
        }

        modelAndView.addObject("list",list).addObject("side_chk",true);
        return modelAndView;
    }

    @GetMapping("/pickBoard_cont")
    public ModelAndView pickBoard_cont(@RequestParam("no")int no){
        pickup cont = this.pickupService.pBoardCont(no);
        this.pickupService.pboard_hit(no);
        cont.setImgKey(imageService.findImageByKey(cont.getImgKey()).get().getUrl());
        return new ModelAndView("pickupBoard/pickBoard_cont").addObject("cont",cont).addObject("side_chk",true);
    }

    @GetMapping("/pboard_modify")
    public ModelAndView pBoardModify(@RequestParam("no")int no,HttpServletRequest request,Model model){
        ModelAndView modelAndView = new ModelAndView("pickupBoard/pickupBoard_modify");
        Alarm alarm = new Alarm(model);
        pickup cont = this.pickupService.pBoardCont(no);
        HttpSession session = request.getSession();
        String nickname = (String) session.getAttribute("userNickname");

        if(!nickname.equals(cont.getUserId())){
            alarm.setMessageAndRedirect("잘못된 접근입니다.","");
            return new ModelAndView(alarm.getMessagePage());
        }
        HashMap<String,ArrayList<String>> catelist = cateService.getCateListMap();

        String baseImgKey = "potatoes/b61d87ce-b7a4-42a2-a839-bcc3cf8b4ea8";

        List<String> cityList = new ArrayList<>();

        // 도시명 추가
        cityList.add("서울특별시");
        cityList.add("인천광역시");
        cityList.add("대전광역시");
        cityList.add("부산광역시");
        cityList.add("울산광역시");
        cityList.add("대구광역시");
        cityList.add("광주광역시");
        cityList.add("제주특별자치도");
        cityList.add("경기도");
        cityList.add("강원도");
        cityList.add("충청남도");
        cityList.add("충청북도");
        cityList.add("경상북도");
        cityList.add("경상남도");
        cityList.add("전라남도");
        cityList.add("전라북도");

        Optional<Image> imageOp = imageService.findImageByKey(baseImgKey);
        if(imageOp.isPresent()){
            modelAndView.addObject("noneImg", imageOp.get().getUrl());
        }

        cont.setImgKey(imageService.findImageByKey(cont.getImgKey()).get().getUrl());
        return modelAndView.addObject("cont",cont).addObject("list",cityList).addObject("catelist",catelist);
    }

    @PostMapping("/pickBoard_modify_ok")
    public ModelAndView pBoardmodify_ok(@RequestParam("no")int no,@RequestPart("file")MultipartFile file,pickup pick,Model model){
        Alarm alarm = new Alarm(model);

        Optional<Image> image = imageService.insertFileAndGetImage(file);
        if(image.isEmpty()){
            String baseImgKey = "potatoes/57ce4371-553e-4b2a-bab7-a478f8294021";
            pick.setImgKey(baseImgKey);
        }else{
            pick.setImgKey(image.get().getImgKey());
        }

        pick.setPickNum(no);

        int check = this.pickupService.pBoardModify(pick);

        if(check > 0){
            alarm.setMessageAndRedirect("게시글이 수정되었습니다.","/pickup_Board_list");
        }else{
            alarm.setMessageAndRedirect("게시글 수정 실패.","");
        }

        return new ModelAndView(alarm.getMessagePage());
    }

    @GetMapping("/pboard_delete")
    public ModelAndView pBoard_delete(@RequestParam("no")int no,Model model){

        Alarm alarm = new Alarm(model);

        int check = this.pickupService.pBoardDelete(no);

        if(check > 0){
            this.pickupService.updateSequence(no);
            alarm.setMessageAndRedirect("게시글이 삭제되었습니다.","/pickup_Board_list");
        }else{
            alarm.setMessageAndRedirect("게시글 삭제 실패","");
        }

        return new ModelAndView(alarm.getMessagePage());
    }

    @PostMapping("cateUpdate")
    public List<String> cateupdate(@RequestParam("catemajor")String catemajor){

        List<UniqueCategories> minorCatrgory = this.cateService.updateCate(catemajor);

        List<String> minorCategoryList = new ArrayList<>();

        for (UniqueCategories category : minorCatrgory) {
            minorCategoryList.add(category.getMinorCategory());
        }

       return minorCategoryList;
    }
}

