package com.harryberlin.cvnest.controller;

import com.harryberlin.cvnest.domain.User;
import com.harryberlin.cvnest.dto.request.CandidateOutreachRequest;
import com.harryberlin.cvnest.dto.request.SendOutreachEmailActualRequest;
import com.harryberlin.cvnest.dto.response.ApiResponse;
import com.harryberlin.cvnest.dto.response.EmailPreviewResponse;
import com.harryberlin.cvnest.exception.BaseException;
import com.harryberlin.cvnest.service.EmailService;
import com.harryberlin.cvnest.service.UserService;
import com.harryberlin.cvnest.util.constant.Error;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recruitment")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final UserService userService;

    @PostMapping("/preview")
    public ApiResponse<?> preview(@Valid @RequestBody CandidateOutreachRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User hrUser = this.userService.handleGetUserByEmail(email);
        if (hrUser == null) {
            throw new BaseException(Error.USER_NOT_FOUND);
        }

        try {
            EmailPreviewResponse previewResponse = this.emailService.prepareEmailPreview(request, hrUser.getEmail());
            return ApiResponse.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Prepare email successfully")
                    .data(previewResponse)
                    .build();
        } catch (MessagingException e) {
            throw new BaseException(e.getMessage());
        }
    }

    @PostMapping("/send")
    public ApiResponse<?> sendEmail(@Valid @RequestBody SendOutreachEmailActualRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User hrUser = this.userService.handleGetUserByEmail(email);
        if (hrUser == null) {
            throw new BaseException(Error.USER_NOT_FOUND);
        }

        try {
            this.emailService.sendPreparedOutreachEmail(request);
            return ApiResponse.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Send email successfully")
                    .data(null)
                    .build();
        } catch (MessagingException e) {
            throw new BaseException(e.getMessage());
        }
    }
}
