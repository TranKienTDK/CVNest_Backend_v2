package com.harryberlin.cvnest.service;

import com.harryberlin.cvnest.domain.User;
import com.harryberlin.cvnest.util.helper.CodeGenerator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    @Value("${spring.mail.username}")
    private String fromAddress;

    private final RedisTemplate<String, Object> redisTemplate;
    private final JavaMailSender mailSender;

    @Async
    public void sendResetPasswordEmail(User user) throws MessagingException {
        String toAddress = user.getEmail();
        String subject = "CVNest Password Reset Verification";
        String content = "<html>"
                + "<body style='font-family: Arial, sans-serif; background-color: #f1eae0b9; padding: 20px;'>"
                + "<div style='max-width: 600px; margin: auto; background-color: #fff; padding: 20px; border-radius: 10px;'>"
                + "<h1 style='text-align: center; color: #AD7D59;'>CVNest Password Reset Request</h1>"
                + "<p>Hi [[name]],</p>"
                + "<p>We received a request to reset your password for your CVNest account. If this was you, please use the code below to verify your request and proceed with resetting your password:</p>"
                + "<div style='text-align: center; margin: 20px 0;'>"
                + "<p style='font-weight: bold; font-size: 20px;'>[[code]]</p>"
                + "</div>"
                + "<p>This code will expire in 5 minutes for security reasons.</p>"
                + "<p>If you did not request a password reset, please ignore this email. Your account remains secure.</p>"
                + "<p>Thank you,<br>The CVNest Team</p>"
                + "<div style='text-align: right;'>"
                + "<img src='cid:image_logo' alt='CVNest Logo' style='width: 100px; height: auto;'>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromAddress);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getUsername());
        String code = CodeGenerator.generateVerificationCode();
        content = content.replace("[[code]]", code);
        log.info("Reset password code: {}", code);

        String key = "verify:email:changePass:" + user.getEmail();
        if (redisTemplate.hasKey(key)) {
            redisTemplate.delete(key);
        }
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);

        helper.setText(content, true);

        try {
            Resource res = new ClassPathResource("static/images/CVNest_logo.jpg");
            helper.addInline("image_logo", res);
        } catch (Exception e) {
            log.error("Error while adding image to email", e);
        }

        mailSender.send(message);
    }

}
