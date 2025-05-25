package com.harryberlin.cvnest.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplyRequest {
    private String userId;
    private String jobId;
    private String cvId;
}
