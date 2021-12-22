package com.ensias.twitter.repository;

import com.ensias.twitter.domain.Tweet;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Tweet entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {}
