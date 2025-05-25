package com.harryberlin.cvnest.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordRequest {
    private String oldPassword;
    private String newPassword;
}
