package com.harryberlin.cvnest.elasticsearch.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    String industry;

    List<String> jobIds;
}
