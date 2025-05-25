package com.harryberlin.cvnest.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileResponse {
    private String id;
    private String username;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String avatar;
}
