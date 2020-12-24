package com.ooad.xproject.service.impl;

import com.ooad.xproject.bo.SvResult;
import com.ooad.xproject.service.MailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")       // username is from email address
    private String from;

    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public SvResult<Boolean> sendSimpleMail(String to, String subject, String content) {
        if (to == null || "".equals(to)) {
            System.out.println("Null mail");
            return new SvResult<>("Null mail", false);
        }
        if (isFakeMail(to)) {
            return new SvResult<>("Fake mail", false);
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        try {
            mailSender.send(message);
        } catch (MailException e) {
            e.printStackTrace();
            return new SvResult<>(e.getMessage(), false);
        }

        return new SvResult<>("Send success", true);
    }

    @Override
    public SvResult<Boolean> sendSimpleMail(String to, String subject, String content, String... cc) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setCc(cc);
        message.setSubject(subject);
        message.setText(content);

        try {
            mailSender.send(message);
        } catch (MailException e) {
            e.printStackTrace();
            return new SvResult<>(e.getMessage(), false);
        }

        return new SvResult<>("Send success", true);
    }

    @Override
    public SvResult<Integer> sendMailToStudent(List<String> mailList, String subject, String content) {
        int successCnt = 0;
        for (String to : mailList) {
            if (to == null || "".equals(to)) {
                System.out.println("Null mail");
                continue;
            }
            if (isFakeMail(to)) {
                continue;
            }
            SvResult<Boolean> svResult = sendSimpleMail(to, subject, content);
            if (svResult.getData()) {
                successCnt += 1;
            } else {
                System.out.println("Send mail failed. " + svResult.getMsg());
            }
        }
        return new SvResult<>("Send to all", successCnt);
    }

    private boolean isFakeMail(String mail) {
        return mail.contains(".fake.");
    }
}
