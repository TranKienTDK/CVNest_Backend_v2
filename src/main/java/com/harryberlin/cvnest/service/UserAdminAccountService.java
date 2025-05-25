package com.harryberlin.cvnest.service;

import com.harryberlin.cvnest.domain.User;
import com.harryberlin.cvnest.repository.UserRepository;
import com.harryberlin.cvnest.util.constant.RoleEnum;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAdminAccountService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void createAdminAccountIfNotExists() {
        User existingUser = this.userRepository.findByEmail("admin@gmail.com");

        if (existingUser == null) {
            User adminUser = User.builder()
                    .email("admin@gmail.com")
                    .username("admin")
                    .password(passwordEncoder.encode("12345678"))
                    .role(RoleEnum.ADMIN)
                    .build();

            this.userRepository.save(adminUser);
        }
    }
}
