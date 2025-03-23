package com.harryberlin.cvnest.dto.request;

import com.harryberlin.cvnest.util.constant.IndustryEnum;
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
    IndustryEnum industry;
}
