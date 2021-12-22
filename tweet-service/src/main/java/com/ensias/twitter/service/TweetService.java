package com.ensias.twitter.service;

import com.ensias.twitter.service.dto.TweetDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.ensias.twitter.domain.Tweet}.
 */
public interface TweetService {
    /**
     * Save a tweet.
     *
     * @param tweetDTO the entity to save.
     * @return the persisted entity.
     */
    TweetDTO save(TweetDTO tweetDTO);

    /**
     * Partially updates a tweet.
     *
     * @param tweetDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TweetDTO> partialUpdate(TweetDTO tweetDTO);

    /**
     * Get all the tweets.
     *
     * @return the list of entities.
     */
    List<TweetDTO> findAll();

    /**
     * Get the "id" tweet.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TweetDTO> findOne(Long id);

    /**
     * Delete the "id" tweet.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
