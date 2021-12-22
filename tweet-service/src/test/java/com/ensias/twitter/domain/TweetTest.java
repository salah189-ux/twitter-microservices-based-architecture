package com.ensias.twitter.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ensias.twitter.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TweetTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tweet.class);
        Tweet tweet1 = new Tweet();
        tweet1.setId(1L);
        Tweet tweet2 = new Tweet();
        tweet2.setId(tweet1.getId());
        assertThat(tweet1).isEqualTo(tweet2);
        tweet2.setId(2L);
        assertThat(tweet1).isNotEqualTo(tweet2);
        tweet1.setId(null);
        assertThat(tweet1).isNotEqualTo(tweet2);
    }
}
