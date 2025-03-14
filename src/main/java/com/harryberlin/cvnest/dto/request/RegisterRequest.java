package com.harryberlin.cvnest.dto.request;

import com.harryberlin.cvnest.util.annotation.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {
    @NotNull(message = "Username is required")
    String username;

    @Email
    @NotNull(message = "Email is required")
    String email;

    @NotNull(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password;
}
