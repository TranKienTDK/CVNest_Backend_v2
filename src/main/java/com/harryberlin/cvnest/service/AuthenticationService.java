package com.harryberlin.cvnest.service;

import com.harryberlin.cvnest.domain.User;
import com.harryberlin.cvnest.dto.request.LoginRequest;
import com.harryberlin.cvnest.dto.request.RegisterRequest;
import com.harryberlin.cvnest.dto.request.ResetPasswordRequest;
import com.harryberlin.cvnest.dto.request.VerifyCodeRequest;
import com.harryberlin.cvnest.dto.response.LoginResponse;
import com.harryberlin.cvnest.dto.response.RegisterResponse;
import com.harryberlin.cvnest.exception.BaseException;
import com.harryberlin.cvnest.jwt.JwtService;
import com.harryberlin.cvnest.mapper.register.RegisterMapper;
import com.harryberlin.cvnest.repository.UserRepository;
import com.harryberlin.cvnest.util.constant.Error;
import com.harryberlin.cvnest.util.constant.TokenEnum;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    JwtService jwtService;
    UserService userService;
    EmailService emailService;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    RegisterMapper registerMapper;
    RedisTemplate<String, Object> redisTemplate;
    AuthenticationManagerBuilder authenticationManagerBuilder;

    public RegisterResponse register(RegisterRequest request) {
        log.info("register request: {}", request);
        User user = userRepository.findByEmail(request.getEmail());
        if (user != null) {
            throw new BaseException(Error.USER_ALREADY_EXISTS);
        }
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        return registerMapper.toResponse(userRepository.save(registerMapper.toEntity(request)));
    }

    public LoginResponse login(LoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getEmail(), request.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User currentUserDB = this.userService.handleGetUserByEmail(request.getEmail());
        if (currentUserDB == null) {
            throw new BaseException(Error.USER_NOT_FOUND);
        }
        LoginResponse.UserLogin userLogin = new LoginResponse.UserLogin(
                currentUserDB.getId(),
                currentUserDB.getUsername(),
                currentUserDB.getEmail(),
                currentUserDB.getRole().name()
        );

        String accessToken = this.jwtService.generateToken(currentUserDB, TokenEnum.ACCESS_TOKEN);
        String refreshToken = this.jwtService.generateToken(currentUserDB, TokenEnum.REFRESH_TOKEN);
        this.userService.updateUserToken(request.getEmail(), refreshToken);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userLogin)
                .build();
    }

    public void forgotPassword(String email) throws MessagingException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new BaseException(Error.USER_NOT_FOUND);
        }
        this.emailService.sendResetPasswordEmail(user);
    }

    public boolean verifyCodeForgotPassword(VerifyCodeRequest request) {
        validateVerifyCode(request.getEmail(), request.getCode());
        return true;
    }

    public void resetPassword(ResetPasswordRequest request) {
        validateVerifyCode(request.getEmail(), request.getCode());
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new BaseException(Error.USER_NOT_FOUND);
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        redisTemplate.delete("verify:email:changePass:" + request.getEmail());
    }

    private void validateVerifyCode(String email, String code) {
        String key = "verify:email:changePass:" + email;
        String storedCode = (String) redisTemplate.opsForValue().get(key);
        if (storedCode == null || !storedCode.equals(code)) {
            throw new BaseException(Error.CODE_INVALID);
        }
    }

    public LoginResponse refreshToken(String refreshToken) {
        Jwt decodeToken = this.jwtService.checkValidRefreshToken(refreshToken);
        String email = decodeToken.getSubject();

        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refreshToken, email);
        if (currentUser == null) {
            throw new BaseException(Error.REFRESH_TOKEN_INVALID);
        }

        User currentUserDB = this.userService.handleGetUserByEmail(email);
        if (currentUserDB == null) {
            throw new BaseException(Error.USER_NOT_FOUND);
        }
        LoginResponse.UserLogin userLogin = new LoginResponse.UserLogin(
                currentUserDB.getId(),
                currentUserDB.getEmail(),
                currentUserDB.getUsername(),
                currentUserDB.getRole().name()
        );

        String accessToken = this.jwtService.generateToken(currentUserDB, TokenEnum.ACCESS_TOKEN);
        String newRefreshToken = this.jwtService.generateToken(currentUserDB, TokenEnum.REFRESH_TOKEN);
        this.userService.updateUserToken(email, refreshToken);
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .user(userLogin)
                .build();
    }

    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new BaseException(Error.USER_NOT_FOUND);
        }
        user.setRefreshToken(null);
        this.userService.updateUserToken(email, null);
    }

}
