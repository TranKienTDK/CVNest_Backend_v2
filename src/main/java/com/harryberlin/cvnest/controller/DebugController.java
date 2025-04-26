package com.harryberlin.cvnest.controller;

import com.harryberlin.cvnest.dto.response.ApiResponse;
import com.harryberlin.cvnest.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/debug")
@RequiredArgsConstructor
public class DebugController {
    private final JobService jobService;

    @PostMapping("/clean-description")
    public ApiResponse<Object> cleanDescription() {
        this.jobService.cleanDuplicateDescription();
        return ApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Cleaned duplicate description")
                .data(null)
                .build();
    }
}
