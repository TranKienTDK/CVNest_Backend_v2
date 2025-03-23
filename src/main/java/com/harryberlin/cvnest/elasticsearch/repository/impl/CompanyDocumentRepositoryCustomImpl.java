package com.harryberlin.cvnest.elasticsearch.repository.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.WildcardQuery;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.harryberlin.cvnest.elasticsearch.document.CompanyDocument;
import com.harryberlin.cvnest.elasticsearch.repository.CompanyDocumentRepositoryCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

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
    public Page<CompanyDocument> findCompanyDocumentByQuery(String name, String address, String industry, Pageable pageable) {
        List<Query> mustQueries = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            WildcardQuery nameQuery = new WildcardQuery.Builder()
                    .field("name.keyword")
                    .value("*" + name + "*")
                    .build();
            mustQueries.add(new Query.Builder().wildcard(nameQuery).build());
        }

        if (address != null && !address.isEmpty()) {
            WildcardQuery addressQuery = new WildcardQuery.Builder()
                    .field("address.keyword")
                    .value("*" + address + "*")
                    .build();
            mustQueries.add(new Query.Builder().wildcard(addressQuery).build());
        }

        if (industry != null && !industry.isEmpty()) {
            MatchQuery industryQuery = new MatchQuery.Builder()
                    .field("industry.keyword")
                    .query(industry)
                    .build();
            mustQueries.add(new Query.Builder().match(industryQuery).build());
        }

        BoolQuery boolQuery = new BoolQuery.Builder()
                .must(mustQueries)
                .build();

        try {
            var searchResponse = this.elasticsearchClient.search(s -> s
                    .query(q -> q.bool(boolQuery))
                    .from(pageable.getPageNumber() * pageable.getPageSize())
                    .size(pageable.getPageSize())
                    .index("companies"), CompanyDocument.class);

            List<CompanyDocument> documents = searchResponse.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());

            return new PageImpl<>(documents, pageable, searchResponse.hits().total().value());
        } catch (IOException e) {
            log.error(e.getMessage());
            return Page.empty();
        }
    }
}
