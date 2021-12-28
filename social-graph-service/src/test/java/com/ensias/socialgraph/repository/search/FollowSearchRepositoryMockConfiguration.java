package com.ensias.socialgraph.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link FollowSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class FollowSearchRepositoryMockConfiguration {

    @MockBean
    private FollowSearchRepository mockFollowSearchRepository;
}
