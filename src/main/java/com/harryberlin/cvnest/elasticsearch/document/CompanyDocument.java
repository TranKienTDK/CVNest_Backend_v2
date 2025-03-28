package com.harryberlin.cvnest.elasticsearch.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.harryberlin.cvnest.util.constant.IndustryEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(indexName = "companies")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompanyDocument {
    @Id
    String id;
    String name;
    String address;

    @Enumerated(EnumType.STRING)
    IndustryEnum industry;

    List<String> jobIds;
}
