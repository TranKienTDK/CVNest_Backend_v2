package com.harryberlin.cvnest.controller;

import com.harryberlin.cvnest.dto.request.PasswordRequest;
import com.harryberlin.cvnest.dto.request.UserRequest;
import com.harryberlin.cvnest.dto.response.ApiResponse;
import com.harryberlin.cvnest.dto.response.ProfileResponse;
import com.harryberlin.cvnest.dto.response.UserResponse;
import com.harryberlin.cvnest.service.CloudinaryService;
import com.harryberlin.cvnest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    public ApiResponse<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            String avatarUrl = this.cloudinaryService.uploadAvatar(file);
            return ApiResponse.<String>builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Upload avatar successful")
                    .data(avatarUrl)
                    .build();
        } catch (IOException e) {
            return ApiResponse.<String>builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @PutMapping("/{userId}/profile")
    public ApiResponse<UserResponse> updateProfile(@PathVariable("userId") String userId,
                                                   @RequestBody UserRequest userRequest) {
        userRequest.setId(userId);
        return ApiResponse.<UserResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Update profile successful")
                .data(this.userService.updateProfile(userRequest))
                .build();
    }

    @PutMapping("/{userId}/change-password")
    public ApiResponse<Void> changePassword(@PathVariable("userId") String userId,
                                            @RequestBody PasswordRequest request) {
        this.userService.changePassword(userId, request);
        return ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Change password successful")
                .data(null)
                .build();
    }

    @GetMapping("/{userId}/profile")
    public ApiResponse<ProfileResponse> getProfile(@PathVariable("userId") String userId) {
        return ApiResponse.<ProfileResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Get profile successful")
                .data(this.userService.getProfile(userId))
                .build();
    }
}
