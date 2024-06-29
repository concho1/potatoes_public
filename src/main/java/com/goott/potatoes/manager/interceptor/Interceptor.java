package com.goott.potatoes.manager.interceptor;

import com.goott.potatoes.common.model.ConchoMessage;
import com.goott.potatoes.common.model.Image;
import com.goott.potatoes.common.service.ConchoMessageService;
import com.goott.potatoes.common.service.ImageService;
import com.goott.potatoes.manager.model.ManagerMapper;
import com.goott.potatoes.user.mapper.UserMapper;
import com.goott.potatoes.user.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class Interceptor implements HandlerInterceptor {

    @Autowired
    private ManagerMapper mapper;
    @Autowired
    private ImageService imageService;
    @Autowired
    private ConchoMessageService messageService;
    @Autowired
    private UserMapper userMapper;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String uri = request.getRequestURI();

        if(request.getSession().getAttribute("guest") == null) {
            System.out.println("비회원 + 1");
            System.out.println(request.getSession().getId());
            this.mapper.updateGuestCnt();
            request.getSession().setAttribute("guest", "guest");
        }
        request.getSession().setAttribute("guest", "guest");

        if(request.getSession().getAttribute("userImgKey") != null){
            Optional<String> imgKey = Optional.ofNullable(request.getSession().getAttribute("userImgKey").toString());
            if(imgKey.isPresent()){
                Optional<Image> image = imageService.findImageByKey(imgKey.get());
                if(image.isPresent()){
                    request.getSession().setAttribute("userImgUrl", image.get().getUrl());
                }
            }
        }
        if(request.getSession().getAttribute("userNickname") != null){
            String userNickname = (String) request.getSession().getAttribute("userNickname");
            List<ConchoMessage> messageList = messageService.findMessageListByRecUserNickname(userNickname);
            HashMap<String, ConchoMessage> roomMap = new HashMap<>();
            HashMap<String, String> userImgMap = new HashMap<>();
            for(ConchoMessage message : messageList){
                if(message.getIsRead()){
                    //System.out.println(message.getCreateAt());
                    roomMap.put(message.getRoomName(), message);
                    User user = userMapper.findUserByNickname(message.getSendUserNickname());
                    Optional<Image> imageOp = imageService.findImageByKey(user.getImgKey());
                    if(imageOp.isPresent()){
                        userImgMap.put(message.getRoomName(), imageOp.get().getUrl());
                    }else{
                        userImgMap.put(message.getRoomName(), "/assets/img/profile-img.jpg");
                    }
                }
            }
            request.getSession().setAttribute("userImgRoomNameMap", userImgMap);
            request.getSession().setAttribute("roomMap", roomMap);
        }

        return true;
    }
}