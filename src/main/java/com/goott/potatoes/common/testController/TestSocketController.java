package com.goott.potatoes.common.testController;

import com.goott.potatoes.Hamster.mapper.userMapperHam;
import com.goott.potatoes.common.model.Alarm;
import com.goott.potatoes.common.model.ConchoMessage;
import com.goott.potatoes.common.model.Image;
import com.goott.potatoes.common.service.ConchoMessageService;
import com.goott.potatoes.common.service.ImageService;
import com.goott.potatoes.user.mapper.UserMapper;
import com.goott.potatoes.user.model.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class TestSocketController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ConchoMessageService messageService;
    private final userMapperHam userMapperHam;
    private final UserMapper userMapper;
    private final ImageService imageService;

    @GetMapping("/chat-to/{receiver}")
    public String getChatPage(
            @PathVariable(required = false) String receiver,
            HttpSession session,
            Model model){
        String sender = (String) session.getAttribute("userNickname");
        Alarm alarm = new Alarm(model);
        if(sender == null || sender.isEmpty()){
            alarm.setMessageAndRedirect("회원만 가능한 서비스입니다.", "/login");
            return alarm.getMessagePage();
        } else if (receiver == null || receiver.isEmpty()) {
            alarm.setMessageAndRedirect("메세지를 받을 회원 닉네임을 입력해주세요..", "");
            return alarm.getMessagePage();
        } else if (!userMapperHam.checkDupNickname(receiver)) {
            alarm.setMessageAndRedirect("없는 회원입니다..", "");
            return alarm.getMessagePage();
        }

        User user = userMapper.findUserByNickname(receiver);
        Optional<Image> imageOp = imageService.findImageByKey(user.getImgKey());
        String receiverImgUrl = null;
        if(imageOp.isPresent()){
            receiverImgUrl = imageOp.get().getUrl();
        }
        // 채팅방 페이지 줄 때 해당 채팅방에 들어온 사람(sender) 가 receiver 인 메시지가 있으면 1로 바꿔주기
        messageService.updateIsReadToZero(sender, receiver);

        if(session.getAttribute("userNickname") != null){
            String userNickname = (String) session.getAttribute("userNickname");
            List<ConchoMessage> messageList = messageService.findMessageListByRecUserNickname(userNickname);
            HashMap<String, ConchoMessage> roomMap = new HashMap<>();
            HashMap<String, String> userImgMap = new HashMap<>();
            for(ConchoMessage message : messageList){
                if(message.getIsRead()){
                    //System.out.println(message.getCreateAt());
                    roomMap.put(message.getRoomName(), message);
                    User user1 = userMapper.findUserByNickname(message.getSendUserNickname());
                    Optional<Image> imageOp1 = imageService.findImageByKey(user1.getImgKey());
                    if(imageOp1.isPresent()){
                        userImgMap.put(message.getRoomName(), imageOp1.get().getUrl());
                    }else{
                        userImgMap.put(message.getRoomName(), "/assets/img/profile-img.jpg");
                    }
                }
            }
            session.setAttribute("userImgRoomNameMap", userImgMap);
            session.setAttribute("roomMap", roomMap);
        }

        List<ConchoMessage> messageList
                = messageService.findMessageListBySenderAndReceiver(sender, receiver);
        model.addAttribute("receiver", receiver);
        model.addAttribute("messageList",messageList);
        model.addAttribute("receiverImgUrl", receiverImgUrl);
        return "concho/concho-chat";
    }

    @MessageMapping("/chat/send/{roomName}")
    public void sendMsg(
            @Payload Map<String,String> data,
            @DestinationVariable String roomName){
        String userNickname = data.get("sender");
        String content = data.get("content");
        String receiver = data.get("receiver");

        System.out.println("userNickname : " + userNickname);
        System.out.println("content : " + content);
        // 채팅방 페이지 줄 때 해당 채팅방에 들어온 사람(sender) 가 receiver 인 메시지가 있으면 1로 바꿔주기
        messageService.updateIsReadToZero(userNickname, receiver);
        messageService.insertMessageBySenderAndRoomNameAndContent(
                userNickname, receiver, roomName, content
        );
        simpMessagingTemplate.convertAndSend("/room/" + roomName, data);
    }
}
