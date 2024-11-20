package com.mysite.sbb.mail;

import com.mysite.sbb.user.UserRole;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class TempPasswordMail {

    private final JavaMailSender javaMailSender;

    private String ePw;

    public MimeMessage createMessage(String to)
            throws UnsupportedEncodingException, MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject("조환열게시판 임시 비밀번호");

        String msgg = "";
        msgg += "<div style='margin:100px;'>";
        msgg +=
                "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg += "<h3 style='color:blue;'>임시 비밀번호입니다.</h3>";
        msgg += "<div style='font-size:130%'>";
        msgg += "CODE : <strong>";
        msgg += ePw + "</strong><div><br/> ";
        msgg += "</div>";
        message.setText(msgg, "utf-8", "html");
        message.setFrom(new InternetAddress("drcho9709@gmail.com", "조환열게시판_Admin"));

        return message;
    }



    public MimeMessage createNewDeviceMessage(String to)
            throws UnsupportedEncodingException, MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject("조환열게시판 새로운 기기로 로그인");

        String msgg = "";
        msgg += "<div style='margin:100px;'>";
        msgg +=
                "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg += "<h3 style='color:blue;'>새로운 기기로 로그인을 하였습니다 본인 확인을 해주세요.</h3>";
        msgg += "<div style='font-size:130%'>";
        msgg += "<strong>";
        msgg += ePw.replace(",","<br/>") + "</strong><div><br/> ";
        msgg += "</div>";
        message.setText(msgg, "utf-8", "html");
        message.setFrom(new InternetAddress("drcho9709@gmail.com", "조환열게시판_Admin"));

        return message;
    }



    public void sendSimpleMessage(String to, String pw, String Role) {
        this.ePw = pw;

        MimeMessage message;
        try {
            if (Role.equals(MailRole.PASSWORD.getValue())){
                message = createMessage(to);
            }
            else{
                message = createNewDeviceMessage(to);
            }

        } catch (UnsupportedEncodingException | MessagingException e) {
            e.printStackTrace();
            throw new EmailException("이메일 생성 에러");
        }
        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            e.printStackTrace();
            throw new EmailException("이메일 전송 에러");
        }
    }







}

