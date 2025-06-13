package com.harryberlin.cvnest.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CandidateOutreachRequest {
    private String jobId;

    @NotBlank(message = "Email ứng viên không được để trống")
    @Email(message = "Email ứng viên không hợp lệ")
    private String candidateEmail;

    private String candidateName;
}
