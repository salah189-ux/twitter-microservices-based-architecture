package com.ensias.twitter.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ensias.twitter.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TweetDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TweetDTO.class);
        TweetDTO tweetDTO1 = new TweetDTO();
        tweetDTO1.setId(1L);
        TweetDTO tweetDTO2 = new TweetDTO();
        assertThat(tweetDTO1).isNotEqualTo(tweetDTO2);
        tweetDTO2.setId(tweetDTO1.getId());
        assertThat(tweetDTO1).isEqualTo(tweetDTO2);
        tweetDTO2.setId(2L);
        assertThat(tweetDTO1).isNotEqualTo(tweetDTO2);
        tweetDTO1.setId(null);
        assertThat(tweetDTO1).isNotEqualTo(tweetDTO2);
    }
}
