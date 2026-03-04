package com.cosam.project01.mail.controller;

import com.cosam.project01.mail.MailService;
import com.cosam.project01.mail.dto.MailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mail")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVO','SUPERVISOR')")
public class MailController {
    private final MailService mailService;

    @PostMapping("/send")
    public String sendMail(@RequestBody MailRequest request){

        mailService.sendMail(
                request.getTo(),
                request.getSubject(),
                request.getMessage()
        );

        return "Correo enviado correctamente";
    }
}
