package com.harryberlin.cvnest.dto.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {
    private String id;
    private String username;
    private String phone;
    private LocalDate dateOfBirth;
    private String avatar;
}
