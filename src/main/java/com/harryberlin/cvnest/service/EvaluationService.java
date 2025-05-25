package com.harryberlin.cvnest.service;

import com.harryberlin.cvnest.domain.entity.Evaluation;
import com.harryberlin.cvnest.dto.request.FeedbackRequest;
import com.harryberlin.cvnest.exception.BaseException;
import com.harryberlin.cvnest.repository.CVRepository;
import com.harryberlin.cvnest.repository.EvaluationRepository;
import com.harryberlin.cvnest.repository.JobRepository;
import com.harryberlin.cvnest.util.constant.Error;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final CVRepository cvRepository;
    private final JobRepository jobRepository;
    private final RestTemplate restTemplate;

    private static final String FASTAPI_URL = "http://localhost:8000/sync_feedback";

    public Evaluation createEvaluation(Evaluation evaluation) {
        if (!cvRepository.existsById(evaluation.getCvId())) {
            throw new BaseException(Error.CV_NOT_FOUND);
        }
        if (!jobRepository.existsById(evaluation.getJobId())) {
            throw new BaseException(Error.JOB_NOT_FOUND);
        }
        return evaluationRepository.save(evaluation);
    }

    public List<Evaluation> getAllEvaluations() {
        return evaluationRepository.findAll();
    }

    public List<Evaluation> getEvaluationsByJob(String jobId) {
        if (!jobRepository.existsById(jobId)) {
            throw new BaseException(Error.JOB_NOT_FOUND);
        }
        return evaluationRepository.findAll().stream()
                .filter(e -> e.getJobId().equals(jobId))
                .sorted((e1, e2) -> Double.compare(e2.getScore(), e1.getScore()))
                .collect(Collectors.toList());
    }

    public List<Evaluation> getEvaluationsByCv(String cvId) {
        if (!cvRepository.existsById(cvId)) {
            throw new BaseException(Error.CV_NOT_FOUND);
        }
        return evaluationRepository.findAll().stream()
                .filter(e -> e.getCvId().equals(cvId))
                .collect(Collectors.toList());
    }

    public Evaluation updateFeedback(String id, String feedback) {
        if (!"approved".equals(feedback) && !"rejected".equals(feedback)) {
            throw new BaseException(Error.EVALUATION_FEEDBACK);
        }
        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new BaseException(Error.EVALUATION_NOT_FOUND));
        evaluation.setFeedback(feedback);
        evaluationRepository.save(evaluation);

        // Gửi feedback đến FastAPI
        try {
            restTemplate.postForObject(
                    FASTAPI_URL + "?evaluation_id=" + id,
                    new FeedbackRequest(feedback),
                    String.class
            );
        } catch (Exception e) {
            log.error("Failed to send feedback to FastAPI: {}", e.getMessage());
        }

        return evaluation;
    }

    public Evaluation approveEvaluation(String id) {
        return updateFeedback(id, "approved");
    }

    public Evaluation rejectEvaluation(String id) {
        return updateFeedback(id, "rejected");
    }
}
