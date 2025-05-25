package com.harryberlin.cvnest.controller;

import com.harryberlin.cvnest.domain.Apply;
import com.harryberlin.cvnest.dto.request.ApplyRequest;
import com.harryberlin.cvnest.dto.response.ApiResponse;
import com.harryberlin.cvnest.service.ApplyService;
import com.harryberlin.cvnest.util.constant.CVStatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apply")
@RequiredArgsConstructor
public class ApplyController {
    private final ApplyService applyService;

    @PostMapping
    public ApiResponse<String> applyCV(@RequestBody ApplyRequest request) {
        this.applyService.applyCVForJob(request.getUserId(), request.getJobId(), request.getCvId());
        return ApiResponse.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Apply CV thành công")
                .data(null)
                .build();
    }
    
    @GetMapping("/job/{jobId}")
    public ApiResponse<List<Apply>> getApplicationsByJob(@PathVariable("jobId") String jobId) {
        List<Apply> applications = this.applyService.getApplicationsByJob(jobId);
        return ApiResponse.<List<Apply>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Lấy danh sách ứng tuyển thành công")
                .data(applications)
                .build();
    }

    // GET APPLICATIONS BY HR
    @GetMapping("/hr/{hrId}")
    public ApiResponse<List<Apply>> getApplicationsByHR(@PathVariable("hrId") String hrId) {
        List<Apply> applications = this.applyService.getApplicationsByHR(hrId);
        return ApiResponse.<List<Apply>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Lấy danh sách ứng tuyển thành công")
                .data(applications)
                .build();
    }
    
    @GetMapping("/user/{userId}")
    public ApiResponse<List<Apply>> getApplicationsByUser(@PathVariable("userId") String userId) {
        List<Apply> applications = this.applyService.getApplicationsByUser(userId);
        return ApiResponse.<List<Apply>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Lấy danh sách ứng tuyển thành công")
                .data(applications)
                .build();
    }
    
    @PutMapping("/{id}/approve")
    public ApiResponse<String> approveApplication(@PathVariable("id") String applyId) {
        this.applyService.updateApplicationStatus(applyId, CVStatusEnum.APPROVED);
        return ApiResponse.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Đã duyệt đơn ứng tuyển")
                .data(null)
                .build();
    }
    
    @PutMapping("/{id}/reject")
    public ApiResponse<String> rejectApplication(@PathVariable("id") String applyId) {
        this.applyService.updateApplicationStatus(applyId, CVStatusEnum.REJECTED);
        return ApiResponse.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Đã từ chối đơn ứng tuyển")
                .data(null)
                .build();
    }
}
