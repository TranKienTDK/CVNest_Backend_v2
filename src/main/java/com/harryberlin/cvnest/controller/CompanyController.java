package com.harryberlin.cvnest.controller;

import com.harryberlin.cvnest.dto.request.CompanyCreateRequest;
import com.harryberlin.cvnest.dto.request.CompanyUpdateRequest;
import com.harryberlin.cvnest.dto.response.ApiResponse;
import com.harryberlin.cvnest.dto.response.CompanyResponse;
import com.harryberlin.cvnest.service.CompanyService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompanyController {
    CompanyService companyService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CompanyResponse> createCompany(@RequestBody CompanyCreateRequest request) {
        return ApiResponse.<CompanyResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Tạo mới công ty thành công")
                .data(this.companyService.createCompany(request))
                .build();
    }

    @GetMapping
    public ApiResponse<PagedModel<EntityModel<CompanyResponse>>> getAllCompany(Pageable pageable,
                                                                               PagedResourcesAssembler<CompanyResponse> assembler) {
        Page<CompanyResponse> companiesPage = this.companyService.getAllCompanies(pageable);
        PagedModel<EntityModel<CompanyResponse>> pagedModel = assembler.toModel(companiesPage);
        return ApiResponse.<PagedModel<EntityModel<CompanyResponse>>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Lấy danh sách công ty thành công")
                .data(pagedModel)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CompanyResponse> getCompanyById(@PathVariable String id) {
        return ApiResponse.<CompanyResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Lấy thông tin công ty thành công")
                .data(this.companyService.getCompanyById(id))
                .build();
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CompanyResponse> updateCompany(@RequestBody CompanyUpdateRequest request) {
        return ApiResponse.<CompanyResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Cập nhật thông tin công ty thành công")
                .data(this.companyService.updateCompany(request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteCompany(@PathVariable String id) {
        this.companyService.deleteCompany(id);
        return ApiResponse.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Xóa công ty thành công")
                .data(null)
                .build();
    }

    @GetMapping("/search")
    public ApiResponse<PagedModel<EntityModel<CompanyResponse>>> searchCompany(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "address", required = false) String address,
            @RequestParam(name = "industry", required = false) String industry,
            Pageable pageable, PagedResourcesAssembler<CompanyResponse> assembler) {
        Page<CompanyResponse> companiesPage = this.companyService.searchCompany(name, address, industry, pageable);
        PagedModel<EntityModel<CompanyResponse>> pagedModel = assembler.toModel(companiesPage);
        return ApiResponse.<PagedModel<EntityModel<CompanyResponse>>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Tìm kiếm công ty thành công")
                .data(pagedModel)
                .build();
    }
}
