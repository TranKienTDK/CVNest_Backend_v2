package com.harryberlin.cvnest.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailPreviewResponse {
    private String to;
    private String subject;
    private String body;
}
