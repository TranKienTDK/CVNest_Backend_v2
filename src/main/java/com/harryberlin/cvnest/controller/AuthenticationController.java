package com.harryberlin.cvnest.controller;

import com.harryberlin.cvnest.dto.request.LoginRequest;
import com.harryberlin.cvnest.dto.request.RegisterRequest;
import com.harryberlin.cvnest.dto.request.ResetPasswordRequest;
import com.harryberlin.cvnest.dto.request.VerifyCodeRequest;
import com.harryberlin.cvnest.dto.response.ApiResponse;
import com.harryberlin.cvnest.dto.response.LoginResponse;
import com.harryberlin.cvnest.dto.response.RegisterResponse;
import com.harryberlin.cvnest.service.AuthenticationService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/register")
    public ApiResponse<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ApiResponse.<RegisterResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Đăng ký tài khoản thành công")
                .data(authenticationService.register(registerRequest))
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authenticationService.login(loginRequest);
        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", response.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(864000)
                .build();

        ApiResponse<LoginResponse> apiResponse = ApiResponse.<LoginResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Đăng nhập thành công")
                .data(response)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(apiResponse);
    }

    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(@RequestParam String email) throws MessagingException {
        authenticationService.forgotPassword(email);
        return ApiResponse.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Một email đã được gửi đến " + email + " để khôi phục mật khẩu")
                .build();
    }

    @PostMapping("/verify-forgot-password")
    public ApiResponse<String> verifyForgotPassword(@RequestBody VerifyCodeRequest request) {
        boolean isValid = this.authenticationService.verifyCodeForgotPassword(request);
        if (isValid) {
            return ApiResponse.<String>builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Mã xác thực hợp lệ. Vui lòng nhập mật khẩu mới.")
                    .build();
        } else {
            return ApiResponse.<String>builder()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .error("Mã xác thực không hợp lệ hoặc đã hết hạn.")
                    .build();
        }
    }

    @PostMapping("/reset-password")
    public ApiResponse<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        this.authenticationService.resetPassword(request);
        return ApiResponse.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Mật khẩu đã được thay đổi thành công")
                .build();
    }

    @GetMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            @CookieValue(name = "refresh_token") String refreshToken) {
        LoginResponse response = this.authenticationService.refreshToken(refreshToken);
        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", response.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(864000)
                .build();

        ApiResponse<LoginResponse> apiResponse = ApiResponse.<LoginResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Refresh token thành công")
                .data(response)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(apiResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        this.authenticationService.logout();

        ResponseCookie deleteSpringCookie = ResponseCookie
                .from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                .build();
    }
}
