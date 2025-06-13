package com.harryberlin.cvnest.controller;

import com.harryberlin.cvnest.domain.entity.Evaluation;
import com.harryberlin.cvnest.dto.request.FeedbackRequest;
import com.harryberlin.cvnest.dto.response.ApiResponse;
import com.harryberlin.cvnest.service.EvaluationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evaluations")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;

    @PostMapping
    public ApiResponse<Evaluation> createEvaluation(@RequestBody Evaluation evaluation) {
        Evaluation savedEvaluation = evaluationService.createEvaluation(evaluation);
        return ApiResponse.<Evaluation>builder()
                .statusCode(201)
                .message("Evaluation created successfully")
                .data(savedEvaluation)
                .build();
    }

    @GetMapping
    public ApiResponse<List<Evaluation>> getAllEvaluations() {
        List<Evaluation> evaluations = evaluationService.getAllEvaluations();
        return ApiResponse.<List<Evaluation>>builder()
                .statusCode(200)
                .message("Get all evaluations successfully")
                .data(evaluations)
                .build();
    }

    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasAuthority('HR') or hasAuthority('ADMIN')")
    public ApiResponse<List<Evaluation>> getEvaluationsByJob(@PathVariable String jobId) {
        List<Evaluation> evaluations = evaluationService.getEvaluationsByJob(jobId);
        return ApiResponse.<List<Evaluation>>builder()
                .statusCode(200)
                .message("Get evaluations by job successfully")
                .data(evaluations)
                .build();
    }

    @GetMapping("/cv/{cvId}")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ApiResponse<List<Evaluation>> getEvaluationsByCv(@PathVariable String cvId) {
        List<Evaluation> evaluations = evaluationService.getEvaluationsByCv(cvId);
        return ApiResponse.<List<Evaluation>>builder()
                .statusCode(200)
                .message("Get evaluations by CV successfully")
                .data(evaluations)
                .build();
    }

    @PostMapping("/{id}/feedback")
    public ApiResponse<Evaluation> updateFeedback(
            @PathVariable String id,
            @Valid @RequestBody FeedbackRequest feedbackRequest) {
        Evaluation updatedEvaluation = evaluationService.updateFeedback(id, feedbackRequest.getFeedback());
        return ApiResponse.<Evaluation>builder()
                .statusCode(200)
                .message("Feedback updated successfully")
                .data(updatedEvaluation)
                .build();
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ApiResponse<Evaluation> approveEvaluation(@PathVariable String id) {
        Evaluation updatedEvaluation = evaluationService.approveEvaluation(id);
        return ApiResponse.<Evaluation>builder()
                .statusCode(200)
                .message("Evaluation approved successfully")
                .data(updatedEvaluation)
                .build();
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ApiResponse<Evaluation> rejectEvaluation(@PathVariable String id) {
        Evaluation updatedEvaluation = evaluationService.rejectEvaluation(id);
        return ApiResponse.<Evaluation>builder()
                .statusCode(200)
                .message("Evaluation rejected successfully")
                .data(updatedEvaluation)
                .build();
    }
}
