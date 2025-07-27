package com.Project1.demo.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.Project1.demo.dto.response.ResponseData;
import com.Project1.demo.dto.response.ResponseError;
import com.Project1.demo.sevice.impl.MailService;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/common")
@RequiredArgsConstructor
public class CommonColtroller {
	
	private final MailService mailService;
	
	@PostMapping("/send-email")
    public ResponseData<?> sendEmail(@RequestParam String recipients, @RequestParam String subject,
                                     @RequestParam String content, @RequestParam(required = false) MultipartFile[] files) {
        log.info("Request GET /common/send-email");
        try {
            return new ResponseData(HttpStatus.ACCEPTED.value(), mailService.sendEmail(recipients, subject, content, files));
        } catch (UnsupportedEncodingException | MessagingException e) {
            log.error("Sending email was failure, message={}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Sending email was failure");
        }
    }
}
