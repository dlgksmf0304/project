package org.mbc.czo.function.member.email.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.log4j.Log4j2;

import org.mbc.czo.function.member.email.emailData.DataResponse;
import org.mbc.czo.function.member.email.emailData.DataResponseCode;
import org.mbc.czo.function.member.email.emailData.UserSignUpResponseCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.Random;

@Log4j2
@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final String fromEmail;
    private String emailCode;

    private final RedisService redisService;

    public EmailService(JavaMailSender javaMailSender,
                        RedisService redisService,
                        @Value("${spring.mail.username}") String fromEmail) {
        this.javaMailSender = javaMailSender;
        this.fromEmail = fromEmail;
        this.redisService = redisService;
    }

    @Transactional(readOnly = false)
    public DataResponse<DataResponseCode> sendMail(String receiver) {
        log.debug("sendMail 호출됨, receiver={}", receiver);
        try {
            log.debug("send 작동 ");
            emailCode = createKey();
            MimeMessage message = createMessage(receiver);

            javaMailSender.send(message);

            return new DataResponse<>(UserSignUpResponseCode.SUCCESS);
        } catch (MessagingException | UnsupportedEncodingException | MailException e) {
            e.printStackTrace();
            return new DataResponse<>(UserSignUpResponseCode.MAIL_SEND_FAILED);
        }
    }

    /**
     *  메일 내용 작성
     */
    private MimeMessage createMessage(String receiver) throws MessagingException, UnsupportedEncodingException {

        redisService.setValue("email:auth:" + receiver, emailCode, 3 * 60); // TTL 3분

        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, receiver);
        message.setSubject("인증코드 안내");

        StringBuilder mailMsg2 = new StringBuilder();
        mailMsg2.append("<div>");
        mailMsg2.append("인증코드를 확인해주세요.<br><strong style=\"font-size: 30px;\">");
        mailMsg2.append(emailCode);
        mailMsg2.append("</strong><br>이메일 인증 절차에 따라 이메일 인증코드를 발급해드립니다.<br>인증코드는 이메일 발송 시점으로부터 3분동안 유효합니다.</div>");

        message.setText(mailMsg2.toString(), "utf-8", "html");
        message.setFrom(new InternetAddress(fromEmail, "Re:LIFE 인증 메일"));

        return message;
    }

    /**
     *  메일 인증 코드 생성
     */
    private static String createKey() {
        StringBuilder key = new StringBuilder();

        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(10);
            key.append(index);
        }

        return key.toString();
    }

    // 인증 코드 검증
    public boolean verifyCode(String receiver, String code) {

        Long ttl = redisService.getTTL("email:auth:" + receiver);
        log.info("verifyCode.redisService: " + ttl);

        String savedCode = redisService.getValue("email:auth:" + receiver);
        if (savedCode != null && savedCode.equals(code)) {
            redisService.deleteValue("email:auth:" + receiver); // 검증 성공 시 삭제
            return true;
        }
        return false;
    }
}