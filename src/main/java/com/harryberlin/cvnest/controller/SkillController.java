package com.harryberlin.cvnest.controller;

import com.harryberlin.cvnest.dto.request.SkillCreateRequest;
import com.harryberlin.cvnest.dto.request.SkillUpdateRequest;
import com.harryberlin.cvnest.dto.response.ApiResponse;
import com.harryberlin.cvnest.dto.response.SkillResponse;
import com.harryberlin.cvnest.service.SkillService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/skill")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SkillController {
    SkillService skillService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<SkillResponse> createSkill(@Valid @RequestBody SkillCreateRequest request) {
        return ApiResponse.<SkillResponse>builder()
                .statusCode(201)
                .message("Tạo mới kỹ năng thành công")
                .data(this.skillService.createSkill(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<SkillResponse>> getAllSkills() {
        return ApiResponse.<List<SkillResponse>>builder()
                .statusCode(200)
                .message("Lấy danh sách kỹ năng thành công")
                .data(this.skillService.getAllSkills())
                .build();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<SkillResponse> updateSkill(@RequestBody SkillUpdateRequest request) {
        return ApiResponse.<SkillResponse>builder()
                .statusCode(200)
                .message("Cập nhật kỹ năng thành công")
                .data(this.skillService.updateSkill(request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<Void> deleteSkill(@PathVariable String id) {
        this.skillService.deleteSkill(id);
        return ApiResponse.<Void>builder()
                .statusCode(200)
                .message("Xóa kỹ năng thành công")
                .data(null)
                .build();
    }
}
