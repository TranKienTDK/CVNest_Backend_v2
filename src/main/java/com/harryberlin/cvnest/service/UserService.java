package com.harryberlin.cvnest.service;

import com.harryberlin.cvnest.domain.User;
import com.harryberlin.cvnest.exception.BaseException;
import com.harryberlin.cvnest.repository.UserRepository;
import com.harryberlin.cvnest.util.constant.Error;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;

    public User handleGetUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void updateUserToken(String email, String token) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new BaseException(Error.USER_NOT_FOUND);
        }
        user.setRefreshToken(token);
        userRepository.save(user);
    }

    public User getUserByRefreshTokenAndEmail(String refreshToken, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(refreshToken, email);
    }

}
