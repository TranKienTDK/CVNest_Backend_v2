package com.harryberlin.cvnest.controller;

import com.harryberlin.cvnest.domain.CV;
import com.harryberlin.cvnest.domain.User;
import com.harryberlin.cvnest.dto.request.CVRequest;
import com.harryberlin.cvnest.dto.request.CVSetDefaultRequest;
import com.harryberlin.cvnest.dto.request.CVUpdateRequest;
import com.harryberlin.cvnest.dto.request.SaveCVRequest;
import com.harryberlin.cvnest.dto.response.ApiResponse;
import com.harryberlin.cvnest.exception.BaseException;
import com.harryberlin.cvnest.service.CVService;
import com.harryberlin.cvnest.service.UserService;
import com.harryberlin.cvnest.util.constant.Error;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cv")
@RequiredArgsConstructor
public class CVController {
    private final CVService cvService;
    private final UserService userService;

    @PostMapping
    public ApiResponse<CV> createCV(@RequestBody CVRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = this.userService.handleGetUserByEmail(email);
        if (user == null) {
            throw new BaseException(Error.USER_NOT_FOUND);
        }
        CV cv = cvService.createCV(request, user);
        return ApiResponse.<CV>builder()
                .statusCode(201)
                .message("CV created successfully")
                .data(cv)
                .build();
    }
    
    @PutMapping("/{id}")
    public ApiResponse<CV> updateCV(@PathVariable("id") String cvId, @RequestBody CVUpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = this.userService.handleGetUserByEmail(email);
        if (user == null) {
            throw new BaseException(Error.USER_NOT_FOUND);
        }
        
        CV updatedCV = cvService.updateCV(cvId, request, user);
        return ApiResponse.<CV>builder()
                .statusCode(200)
                .message("CV updated successfully")
                .data(updatedCV)
                .build();
    }

    @GetMapping
    public ApiResponse<Page<CV>> getAllCVsByUser(
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = this.userService.handleGetUserByEmail(email);
        if (user == null) {
            throw new BaseException(Error.USER_NOT_FOUND);
        }
        Page<CV> cvs = this.cvService.getAllCVsByUser(user, pageable);
        return ApiResponse.<Page<CV>>builder()
                .statusCode(200)
                .message("Get all CVs successfully")
                .data(cvs)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CV> getCVDetailById(@PathVariable("id") String cvId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = this.userService.handleGetUserByEmail(email);
        if (user == null) {
            throw new BaseException(Error.USER_NOT_FOUND);
        }
        CV cv = this.cvService.getCVByIdAndUser(cvId, user);
        return ApiResponse.<CV>builder()
                .statusCode(200)
                .message("Get CV detail successfully")
                .data(cv)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCV(@PathVariable("id") String cvId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = this.userService.handleGetUserByEmail(email);
        if (user == null) {
            throw new BaseException(Error.USER_NOT_FOUND);
        }
        this.cvService.deleteCVById(cvId);
        return ApiResponse.<Void>builder()
                .statusCode(200)
                .message("CV deleted successfully")
                .build();
    }

    @GetMapping("/all")
    public ApiResponse<List<CV>> getAllCVs(@RequestParam String jobId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = this.userService.handleGetUserByEmail(email);
        if (user == null) {
            throw new BaseException(Error.USER_NOT_FOUND);
        }
        List<CV> cvs = this.cvService.getAllCVs(jobId);
        return ApiResponse.<List<CV>>builder()
                .statusCode(200)
                .message("Get all CVs successfully")
                .data(cvs)
                .build();
    }

    @PutMapping("/set-default")
    public ApiResponse<Void> setDefaultCV(@RequestBody CVSetDefaultRequest request) {
        this.cvService.setDefaultCV(request);
        return ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("CV set default successfully")
                .data(null)
                .build();
    }

    @PostMapping("/saved-cv")
    public ApiResponse<Void> savedCVByHR(@Valid @RequestBody SaveCVRequest request) {
        this.cvService.saveCVByHR(request);
        return ApiResponse.<Void>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("CV saved successfully")
                .data(null)
                .build();
    }

    @GetMapping("/{hrId}/saved-cv")
    public ApiResponse<List<CV>> getSavedCVs(@PathVariable("hrId") String hrId) {
        return ApiResponse.<List<CV>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Get saved CVs successfully")
                .data(this.cvService.getSavedCVByHr(hrId))
                .build();
    }

}
