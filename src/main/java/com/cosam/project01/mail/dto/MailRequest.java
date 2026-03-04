package com.cosam.project01.mail.dto;

import lombok.Data;

@Data
public class MailRequest {

    private String to;
    private String subject;
    private String message;

}
