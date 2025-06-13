package com.harryberlin.cvnest.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CVSetDefaultRequest {
    private String cvId;
    private String userId;
}
