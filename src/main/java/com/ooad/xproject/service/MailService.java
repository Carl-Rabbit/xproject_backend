package com.ooad.xproject.service;

import com.ooad.xproject.bo.SvResult;

public interface MailService {
    SvResult<Boolean> sendSimpleMail(String to, String subject, String content);

    SvResult<Boolean> sendSimpleMail(String to, String subject, String content, String... cc);
}
