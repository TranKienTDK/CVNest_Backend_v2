package com.harryberlin.cvnest.service;

import com.harryberlin.cvnest.domain.User;
import com.harryberlin.cvnest.dto.request.PasswordRequest;
import com.harryberlin.cvnest.dto.request.UserRequest;
import com.harryberlin.cvnest.dto.response.ProfileResponse;
import com.harryberlin.cvnest.dto.response.UserResponse;
import com.harryberlin.cvnest.exception.BaseException;
import com.harryberlin.cvnest.repository.UserRepository;
import com.harryberlin.cvnest.util.constant.Error;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

    public UserResponse updateProfile(UserRequest request) {
        String userId = request.getId();
        if (!this.userRepository.existsById(userId)) {
            throw new BaseException(Error.USER_NOT_FOUND);
        }
        User currentUser = this.userRepository.findById(userId).get();
        currentUser.setUsername(request.getUsername());
        currentUser.setPhone(request.getPhone());
        currentUser.setAvatar(request.getAvatar());
        currentUser.setDateOfBirth(request.getDateOfBirth());

        currentUser = this.userRepository.save(currentUser);
        return UserResponse.builder()
                .id(currentUser.getId())
                .username(currentUser.getUsername())
                .phone(currentUser.getPhone())
                .avatar(currentUser.getAvatar())
                .dateOfBirth(currentUser.getDateOfBirth())
                .build();
    }

    public void changePassword(String userId, PasswordRequest request) {
        if (!this.userRepository.existsById(userId)) {
            throw new BaseException(Error.USER_NOT_FOUND);
        }
        User currentUser = this.userRepository.findById(userId).get();
        if (!passwordEncoder.matches(request.getOldPassword(), currentUser.getPassword())) {
            throw new BaseException(Error.PASSWORD_INVALID);
        }

        if (request.getNewPassword().equals(request.getOldPassword())) {
            throw new BaseException(Error.PASSWORD_SAME_AS_OLD);
        }

        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        this.userRepository.save(currentUser);
    }

    public ProfileResponse getProfile(String userId) {
        User user = this.userRepository.findById(userId).orElseThrow(
                () -> new BaseException(Error.USER_NOT_FOUND)
        );
        return ProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .dateOfBirth(user.getDateOfBirth())
                .email(user.getEmail())
                .build();
    }

}
