package com.harryberlin.cvnest.elasticsearch.repository.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.harryberlin.cvnest.elasticsearch.document.JobDocument;
import com.harryberlin.cvnest.elasticsearch.repository.JobDocumentRepositoryCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
public class JobDocumentRepositoryCustomImpl implements JobDocumentRepositoryCustom {

    private final ElasticsearchClient elasticsearchClient;

    @Override
    public Page<String> searchJobs(String title, String contract, String experienceYear,
                                   String salaryRange, String jobType, String level,
                                   List<String> skillIds, Pageable pageable) {
        List<Query> mustQueries = new ArrayList<>();

        // Handle param name
        if (StringUtils.hasLength(title)) {
            mustQueries.add(MatchQuery.of(m -> m
                    .field("title")
                    .query(title))._toQuery());
        }

        // Handle param experienceYear
        if (StringUtils.hasLength(experienceYear)) {
            mustQueries.add(TermQuery.of(t -> t
                    .field("experienceYear.keyword")
                    .value(experienceYear))._toQuery());
        }

        // Handle param salaryRange
        if (StringUtils.hasLength(salaryRange)) {
            BigDecimal minSalary = getSalaryFromRange(salaryRange);
            if (minSalary != null) {
                if (salaryRange.equals("Từ 1.000.000 đ đến 3.000.000 đ")) {
                    mustQueries.add(RangeQuery.of(r -> r
                            .number(n -> n
                                    .field("salary")
                                    .gte(minSalary.doubleValue())
                                    .lt(3000000.0)))._toQuery());
                } else if (salaryRange.equals("Dưới 1.000.000 đ")) {
                    mustQueries.add(RangeQuery.of(r -> r
                            .number(n -> n
                                    .field("salary")
                                    .lt(1000000.0)))._toQuery());
                } else {
                    mustQueries.add(RangeQuery.of(r -> r
                            .number(n -> n
                                    .field("salary")
                                    .gte(minSalary.doubleValue())))._toQuery());
                }
            }
        }

        // Handle param contract
        if (StringUtils.hasLength(contract)) {
            mustQueries.add(TermQuery.of(t -> t
                    .field("contract.keyword")
                    .value(contract))._toQuery());
        }

        // Handle param jobType
        if (StringUtils.hasLength(jobType)) {
            mustQueries.add(TermQuery.of(t -> t
                    .field("jobType.keyword")
                    .value(jobType))._toQuery());
        }

        // Handle param level
        if (StringUtils.hasLength(level)) {
            mustQueries.add(TermQuery.of(t -> t
                    .field("level.keyword")
                    .value(level))._toQuery());
        }

        // Handle param skillIds
        if (skillIds != null && !skillIds.isEmpty()) {
            mustQueries.add(TermsQuery.of(t -> t
                    .field("skillIds.keyword")
                    .terms(ts -> ts.value(skillIds.stream()
                            .map(FieldValue::of)
                            .collect(Collectors.toList()))))._toQuery());
        }

        BoolQuery boolQuery = new BoolQuery.Builder()
                .must(mustQueries)
                .build();

        try {
            var searchResponse = this.elasticsearchClient.search(s -> s
                    .query(q -> q.bool(boolQuery))
                    .from(pageable.getPageNumber() * pageable.getPageSize())
                    .size(pageable.getPageSize())
                    .index("jobs"), JobDocument.class);

            List<String> jobIds = searchResponse.hits().hits().stream()
                    .map(Hit::id)
                    .collect(Collectors.toList());

            return new PageImpl<>(jobIds, pageable, searchResponse.hits().total().value());
        } catch (IOException e) {
            log.error("Error when search jobs with pagination: {}", e.getMessage(), e);
            return Page.empty(pageable);
        }
    }

    private BigDecimal getSalaryFromRange(String range) {
        return switch (range) {
            case "Dưới 1.000.000 đ" -> new BigDecimal(0);
            case "Từ 1.000.000 đ đến 3.000.000 đ" -> new BigDecimal(1000000);
            case "Từ 3.000.000 đ" -> new BigDecimal(3000000);
            case "Từ 5.000.000 đ" -> new BigDecimal(5000000);
            case "Từ 7.000.000 đ" -> new BigDecimal(7000000);
            case "Từ 10.000.000 đ" -> new BigDecimal(10000000);
            case "Từ 15.000.000 đ" -> new BigDecimal(15000000);
            default -> null;
        };
    }
}
