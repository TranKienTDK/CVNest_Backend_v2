package com.harryberlin.cvnest.controller;

import com.harryberlin.cvnest.dto.request.JobCreateRequest;
import com.harryberlin.cvnest.dto.request.JobUpdateRequest;
import com.harryberlin.cvnest.dto.response.ApiResponse;
import com.harryberlin.cvnest.dto.response.JobResponse;
import com.harryberlin.cvnest.service.JobService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/job")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobController {
    JobService jobService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<JobResponse> createJob(@RequestBody JobCreateRequest request) {
        return ApiResponse.<JobResponse>builder()
                .statusCode(201)
                .message("Tạo mới công việc thành công")
                .data(this.jobService.createJob(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<JobResponse>> getAllJobs() {
        return ApiResponse.<List<JobResponse>>builder()
                .statusCode(200)
                .message("Lấy danh sách công việc thành công")
                .data(this.jobService.getAllJob())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<JobResponse> getJobById(@PathVariable String id) {
        return ApiResponse.<JobResponse>builder()
                .statusCode(200)
                .message("Lấy thông tin công việc thành công")
                .data(this.jobService.getJobById(id))
                .build();
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<JobResponse> updateJob(@RequestBody JobUpdateRequest request) {
        return ApiResponse.<JobResponse>builder()
                .statusCode(200)
                .message("Cập nhật công việc thành công")
                .data(this.jobService.updateJob(request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteJob(@PathVariable String id) {
        this.jobService.deleteJob(id);
        return ApiResponse.<Void>builder()
                .statusCode(200)
                .message("Xóa công việc thành công")
                .data(null)
                .build();
    }
}
