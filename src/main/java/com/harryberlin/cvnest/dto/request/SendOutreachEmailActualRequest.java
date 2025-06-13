package com.harryberlin.cvnest.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendOutreachEmailActualRequest {
    @NotBlank(message = "Email người nhận không được để trống")
    @Email(message = "Email người nhận không hợp lệ")
    private String to;

    @NotBlank(message = "Chủ đề email không được để trống")
    private String subject;

    @NotBlank(message = "Nội dung email không được để trống")
    private String htmlBody;
}
