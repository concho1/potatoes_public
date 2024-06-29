package com.goott.potatoes.Hamster.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailService {
    @Autowired
    JavaMailSender javaMailSender;

    public static final String code = createcode();

    public String sendEmail(String email, HttpSession session) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.addRecipients(Message.RecipientType.TO,email);
        message.setSubject("[찾아감 이메일 인증 코드입니다.]");

        String msgg="";
        msgg+= "<div style='margin:100px;'>";
        msgg+= "<h1> 안녕하세요 찾아감입니다 </h1>";
        msgg+= "<br>";
        msgg+= "<p>아래 코드를 회원가입 창으로 돌아가 입력해주세요<p>";
        msgg+= "<br>";
        msgg+= "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg+= "<h3 style='color:blue;'>회원가입 코드입니다.</h3>";
        msgg+= "<div style='font-size:130%'>";
        msgg+= "CODE : <strong>";
        msgg+= code + "</strong><div><br/> ";
        msgg+= "</div>";
        session.setAttribute("sCode", code);
        message.setText(msgg,"UTF-8","HTML");
        javaMailSender.send(message);

        return msgg;
    }

    public String sendPwdEmail(String email, HttpSession session) throws MessagingException {
        System.out.println(email);
        MimeMessage message = javaMailSender.createMimeMessage();
        message.addRecipients(Message.RecipientType.TO,email);
        message.setSubject("[찾아감 비밀번호 찾기 인증 코드입니다.]");

        String msgg="";
        msgg+= "<div style='margin:100px;'>";
        msgg+= "<h1> 안녕하세요 찾아감입니다 </h1>";
        msgg+= "<br>";
        msgg+= "<p>아래 코드를 회원가입 창으로 돌아가 입력해주세요<p>";
        msgg+= "<br>";
        msgg+= "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg+= "<h3 style='color:blue;'>비밀번호찾기 코드입니다.</h3>";
        msgg+= "<div style='font-size:130%'>";
        msgg+= "CODE : <strong>";
        msgg+= code + "</strong><div><br/> ";
        msgg+= "</div>";
        session.setAttribute("sCode", code);
        message.setText(msgg,"UTF-8","HTML");
        javaMailSender.send(message);

        return msgg;
    }

    public static String createcode() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 4; i++) {
            key.append(rnd.nextInt(10));
        }

        return key.toString();
    }
}
