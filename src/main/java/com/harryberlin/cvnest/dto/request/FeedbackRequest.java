package com.harryberlin.cvnest.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackRequest {
    @NotBlank(message = "Feedback must be either 'approved' or 'rejected'")
    private String feedback;
}
