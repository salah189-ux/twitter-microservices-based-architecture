package com.ensias.socialgraph.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.ensias.socialgraph.domain.Follow;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Follow} entity.
 */
public interface FollowSearchRepository extends ElasticsearchRepository<Follow, Long>, FollowSearchRepositoryInternal {}

interface FollowSearchRepositoryInternal {
    Stream<Follow> search(String query);
}

class FollowSearchRepositoryInternalImpl implements FollowSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    FollowSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Stream<Follow> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return elasticsearchTemplate.search(nativeSearchQuery, Follow.class).map(SearchHit::getContent).stream();
    }
}
