package com.ooad.xproject.service;

public interface MailService {
    void sendSimpleMail(String to, String subject, String content);

    void sendSimpleMail(String to, String subject, String content, String... cc);
}
