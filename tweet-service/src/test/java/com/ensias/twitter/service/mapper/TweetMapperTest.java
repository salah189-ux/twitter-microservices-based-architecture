package com.ensias.twitter.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TweetMapperTest {

    private TweetMapper tweetMapper;

    @BeforeEach
    public void setUp() {
        tweetMapper = new TweetMapperImpl();
    }
}
