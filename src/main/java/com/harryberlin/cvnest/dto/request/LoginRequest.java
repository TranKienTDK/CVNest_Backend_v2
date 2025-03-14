package com.harryberlin.cvnest.dto.request;

import com.harryberlin.cvnest.util.annotation.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {
    @Email
    String email;

    @Size(min = 8, message = "Password must be at least 8 characters")
    String password;
}
