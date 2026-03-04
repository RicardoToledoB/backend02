package com.cosam.project01.mail.controller;

import com.cosam.project01.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mail")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVO','SUPERVISOR')")
public class MailController {
    private final MailService mailService;

    @PostMapping("/send")
    public String sendMail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String message
    ){

        mailService.sendMail(to, subject, message);

        return "Correo enviado correctamente";
    }
}
