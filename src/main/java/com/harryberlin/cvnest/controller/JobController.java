package com.harryberlin.cvnest.controller;

import com.harryberlin.cvnest.dto.request.JobCreateRequest;
import com.harryberlin.cvnest.dto.request.JobUpdateRequest;
import com.harryberlin.cvnest.dto.response.ApiResponse;
import com.harryberlin.cvnest.dto.response.JobResponse;
import com.harryberlin.cvnest.service.JobService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/job")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobController {
    JobService jobService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HR')")
    public ApiResponse<JobResponse> createJob(@RequestBody JobCreateRequest request) {
        return ApiResponse.<JobResponse>builder()
                .statusCode(201)
                .message("Tạo mới công việc thành công")
                .data(this.jobService.createJob(request))
                .build();
    }

    @GetMapping
    public ApiResponse<PagedModel<EntityModel<JobResponse>>> getAllJobs(Pageable pageable,
                                                                        PagedResourcesAssembler<JobResponse> assembler) {
        Page<JobResponse> jobsPage = this.jobService.getAllJob(pageable);
        PagedModel<EntityModel<JobResponse>> pageModel = assembler.toModel(jobsPage);
        return ApiResponse.<PagedModel<EntityModel<JobResponse>>>builder()
                .statusCode(200)
                .message("Lấy danh sách công việc thành công")
                .data(pageModel)
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
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HR')")
    public ApiResponse<JobResponse> updateJob(@RequestBody JobUpdateRequest request) {
        return ApiResponse.<JobResponse>builder()
                .statusCode(200)
                .message("Cập nhật công việc thành công")
                .data(this.jobService.updateJob(request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<Void> deleteJob(@PathVariable String id) {
        this.jobService.deleteJob(id);
        return ApiResponse.<Void>builder()
                .statusCode(200)
                .message("Xóa công việc thành công")
                .data(null)
                .build();
    }

    @GetMapping("/search")
    public ApiResponse<PagedModel<EntityModel<JobResponse>>> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String contract,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) String salaryRange,
            @RequestParam(required = false) String experienceYear,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) List<String> skillIds,
            Pageable pageable, PagedResourcesAssembler<JobResponse> assembler) {
        Page<JobResponse> jobsPage = this.jobService.searchJobs(
                title, contract, jobType, salaryRange, experienceYear, level, skillIds, pageable);

        PagedModel<EntityModel<JobResponse>> pageModel = assembler.toModel(jobsPage);

        return ApiResponse.<PagedModel<EntityModel<JobResponse>>>builder()
                .statusCode(200)
                .message("Tìm kiếm công việc thành công")
                .data(pageModel)
                .build();
    }

    // IMPORT DATA FROM JSON
    @GetMapping("/import")
    public ApiResponse<String> importData() throws IOException {
        this.jobService.importJobsFromJson();
        return ApiResponse.<String>builder()
                .statusCode(200)
                .message("Import dữ liệu công việc thành công")
                .data(null)
                .build();
    }

    @GetMapping("/{companyId}/jobs")
    public ApiResponse<List<JobResponse>> getJobsByCompanyId(@PathVariable String companyId) {
        return ApiResponse.<List<JobResponse>>builder()
                .statusCode(200)
                .message("Get list of jobs by company id")
                .data(this.jobService.getListJobByCompanyId(companyId))
                .build();
    }

    @GetMapping("hr/jobs/{hrId}")
    public ApiResponse<List<JobResponse>> getJobByHrId(@PathVariable String hrId) {
        return ApiResponse.<List<JobResponse>>builder()
                .statusCode(200)
                .message("Get job by HR id")
                .data(this.jobService.getListJobByHR(hrId))
                .build();
    }
}
