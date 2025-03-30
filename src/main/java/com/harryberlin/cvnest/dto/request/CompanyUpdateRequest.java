package com.harryberlin.cvnest.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyUpdateRequest {
    String id;
    String name;
    String address;
    String website;
    String avatar;
    String description;
    String industry;
}
