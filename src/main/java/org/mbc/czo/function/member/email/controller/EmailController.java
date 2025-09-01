package org.mbc.czo.function.member.email.controller;

import lombok.extern.log4j.Log4j2;

import org.mbc.czo.function.member.email.emailData.DataResponse;
import org.mbc.czo.function.member.email.emailData.DataResponseCode;
import org.mbc.czo.function.member.email.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/api/mail")
public class EmailController {

    @Autowired
    private final StringRedisTemplate redisTemplate;

    private final EmailService emailService;

    public EmailController(EmailService emailService, StringRedisTemplate redisTemplate) {
        this.emailService = emailService;
        this.redisTemplate = redisTemplate;
    }


    @GetMapping("/send")
    public DataResponse<DataResponseCode> sendMail(@RequestParam("receiver") String receiver) {
        log.info("EmailController sendMail");
        DataResponse<DataResponseCode> response = emailService.sendMail(receiver);

        return response;
    }

    // 인증 코드 검증
    @PostMapping("/verify")
    public Map<String, Object> verifyCode(@RequestParam String receiver, @RequestParam String code) {
        boolean result = emailService.verifyCode(receiver, code);
        log.info("EmailController verifyCode: {}", result ? "인증 성공" : "인증 실패");

        //json반환을 위한 코드
        //join.html js 참고
        Map<String, Object> response = new HashMap<>();
        response.put("verified", result);

        return response;
    }


    @GetMapping("/redis-test")
    public String testRedis() {
        redisTemplate.opsForValue().set("springTest", "HelloSpringRedis");
        return redisTemplate.opsForValue().get("springTest");
    }

}