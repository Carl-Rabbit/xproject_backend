package com.ooad.xproject.service;

import com.ooad.xproject.bo.SvResult;

import java.util.List;

public interface MailService {
    SvResult<Boolean> sendSimpleMail(String to, String subject, String content);

    SvResult<Boolean> sendSimpleMail(String to, String subject, String content, String... cc);

    SvResult<Integer> sendMailToStudent(List<String> mailList, String subject, String content);
}
