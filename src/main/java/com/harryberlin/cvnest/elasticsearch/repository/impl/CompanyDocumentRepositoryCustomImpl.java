package com.harryberlin.cvnest.elasticsearch.repository.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.harryberlin.cvnest.elasticsearch.repository.CompanyDocumentRepositoryCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
public class CompanyDocumentRepositoryCustomImpl implements CompanyDocumentRepositoryCustom {

    private final ElasticsearchClient elasticsearchClient;

    @Override
    public Page<String> findCompanyDocumentByQuery(String name, String address, String industry, Pageable pageable) {
        List<Query> mustQueries = new ArrayList<>();

        if (StringUtils.hasLength(name)) {
            mustQueries.add(WildcardQuery.of(w -> w
                    .field("name.keyword")
                    .value("*" + name + "*"))._toQuery());
        }

        if (StringUtils.hasLength(address)) {
            mustQueries.add(MatchQuery.of(m -> m
                    .field("address")
                    .query(address)
                    .operator(Operator.And))._toQuery());
        }

        if (StringUtils.hasLength(industry)) {
            mustQueries.add(TermQuery.of(t -> t
                    .field("industry.keyword")
                    .value(industry))._toQuery());
        }

        BoolQuery boolQuery = new BoolQuery.Builder()
                .must(mustQueries)
                .build();

        try {
            var searchResponse = this.elasticsearchClient.search(s -> s
                            .query(q -> q.bool(boolQuery))
                            .from(pageable.getPageNumber() * pageable.getPageSize())
                            .size(pageable.getPageSize())
                            .index("companies"),
                    com.harryberlin.cvnest.elasticsearch.document.CompanyDocument.class);

            List<String> companyIds = searchResponse.hits().hits().stream()
                    .map(Hit::id)
                    .collect(Collectors.toList());

            return new PageImpl<>(companyIds, pageable, searchResponse.hits().total().value());
        } catch (IOException e) {
            log.error("Error when searching companies: {}", e.getMessage(), e);
            return Page.empty(pageable);
        }
    }
}
