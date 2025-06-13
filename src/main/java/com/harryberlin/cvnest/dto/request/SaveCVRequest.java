package com.harryberlin.cvnest.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveCVRequest {
    @NotNull(message = "hrId không được để trống")
    private String hrId;

    @NotNull(message = "cvId không được để trống")
    private String cvId;
}
