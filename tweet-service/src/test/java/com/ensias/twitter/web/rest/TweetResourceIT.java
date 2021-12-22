package com.ensias.twitter.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ensias.twitter.IntegrationTest;
import com.ensias.twitter.domain.Tweet;
import com.ensias.twitter.repository.TweetRepository;
import com.ensias.twitter.service.dto.TweetDTO;
import com.ensias.twitter.service.mapper.TweetMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TweetResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TweetResourceIT {

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/tweets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private TweetMapper tweetMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTweetMockMvc;

    private Tweet tweet;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tweet createEntity(EntityManager em) {
        Tweet tweet = new Tweet().content(DEFAULT_CONTENT);
        return tweet;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tweet createUpdatedEntity(EntityManager em) {
        Tweet tweet = new Tweet().content(UPDATED_CONTENT);
        return tweet;
    }

    @BeforeEach
    public void initTest() {
        tweet = createEntity(em);
    }

    @Test
    @Transactional
    void createTweet() throws Exception {
        int databaseSizeBeforeCreate = tweetRepository.findAll().size();
        // Create the Tweet
        TweetDTO tweetDTO = tweetMapper.toDto(tweet);
        restTweetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tweetDTO)))
            .andExpect(status().isCreated());

        // Validate the Tweet in the database
        List<Tweet> tweetList = tweetRepository.findAll();
        assertThat(tweetList).hasSize(databaseSizeBeforeCreate + 1);
        Tweet testTweet = tweetList.get(tweetList.size() - 1);
        assertThat(testTweet.getContent()).isEqualTo(DEFAULT_CONTENT);
    }

    @Test
    @Transactional
    void createTweetWithExistingId() throws Exception {
        // Create the Tweet with an existing ID
        tweet.setId(1L);
        TweetDTO tweetDTO = tweetMapper.toDto(tweet);

        int databaseSizeBeforeCreate = tweetRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTweetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tweetDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Tweet in the database
        List<Tweet> tweetList = tweetRepository.findAll();
        assertThat(tweetList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkContentIsRequired() throws Exception {
        int databaseSizeBeforeTest = tweetRepository.findAll().size();
        // set the field null
        tweet.setContent(null);

        // Create the Tweet, which fails.
        TweetDTO tweetDTO = tweetMapper.toDto(tweet);

        restTweetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tweetDTO)))
            .andExpect(status().isBadRequest());

        List<Tweet> tweetList = tweetRepository.findAll();
        assertThat(tweetList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTweets() throws Exception {
        // Initialize the database
        tweetRepository.saveAndFlush(tweet);

        // Get all the tweetList
        restTweetMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tweet.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)));
    }

    @Test
    @Transactional
    void getTweet() throws Exception {
        // Initialize the database
        tweetRepository.saveAndFlush(tweet);

        // Get the tweet
        restTweetMockMvc
            .perform(get(ENTITY_API_URL_ID, tweet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tweet.getId().intValue()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT));
    }

    @Test
    @Transactional
    void getNonExistingTweet() throws Exception {
        // Get the tweet
        restTweetMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTweet() throws Exception {
        // Initialize the database
        tweetRepository.saveAndFlush(tweet);

        int databaseSizeBeforeUpdate = tweetRepository.findAll().size();

        // Update the tweet
        Tweet updatedTweet = tweetRepository.findById(tweet.getId()).get();
        // Disconnect from session so that the updates on updatedTweet are not directly saved in db
        em.detach(updatedTweet);
        updatedTweet.content(UPDATED_CONTENT);
        TweetDTO tweetDTO = tweetMapper.toDto(updatedTweet);

        restTweetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tweetDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tweetDTO))
            )
            .andExpect(status().isOk());

        // Validate the Tweet in the database
        List<Tweet> tweetList = tweetRepository.findAll();
        assertThat(tweetList).hasSize(databaseSizeBeforeUpdate);
        Tweet testTweet = tweetList.get(tweetList.size() - 1);
        assertThat(testTweet.getContent()).isEqualTo(UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void putNonExistingTweet() throws Exception {
        int databaseSizeBeforeUpdate = tweetRepository.findAll().size();
        tweet.setId(count.incrementAndGet());

        // Create the Tweet
        TweetDTO tweetDTO = tweetMapper.toDto(tweet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTweetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tweetDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tweetDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tweet in the database
        List<Tweet> tweetList = tweetRepository.findAll();
        assertThat(tweetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTweet() throws Exception {
        int databaseSizeBeforeUpdate = tweetRepository.findAll().size();
        tweet.setId(count.incrementAndGet());

        // Create the Tweet
        TweetDTO tweetDTO = tweetMapper.toDto(tweet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTweetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tweetDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tweet in the database
        List<Tweet> tweetList = tweetRepository.findAll();
        assertThat(tweetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTweet() throws Exception {
        int databaseSizeBeforeUpdate = tweetRepository.findAll().size();
        tweet.setId(count.incrementAndGet());

        // Create the Tweet
        TweetDTO tweetDTO = tweetMapper.toDto(tweet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTweetMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tweetDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tweet in the database
        List<Tweet> tweetList = tweetRepository.findAll();
        assertThat(tweetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTweetWithPatch() throws Exception {
        // Initialize the database
        tweetRepository.saveAndFlush(tweet);

        int databaseSizeBeforeUpdate = tweetRepository.findAll().size();

        // Update the tweet using partial update
        Tweet partialUpdatedTweet = new Tweet();
        partialUpdatedTweet.setId(tweet.getId());

        partialUpdatedTweet.content(UPDATED_CONTENT);

        restTweetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTweet.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTweet))
            )
            .andExpect(status().isOk());

        // Validate the Tweet in the database
        List<Tweet> tweetList = tweetRepository.findAll();
        assertThat(tweetList).hasSize(databaseSizeBeforeUpdate);
        Tweet testTweet = tweetList.get(tweetList.size() - 1);
        assertThat(testTweet.getContent()).isEqualTo(UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void fullUpdateTweetWithPatch() throws Exception {
        // Initialize the database
        tweetRepository.saveAndFlush(tweet);

        int databaseSizeBeforeUpdate = tweetRepository.findAll().size();

        // Update the tweet using partial update
        Tweet partialUpdatedTweet = new Tweet();
        partialUpdatedTweet.setId(tweet.getId());

        partialUpdatedTweet.content(UPDATED_CONTENT);

        restTweetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTweet.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTweet))
            )
            .andExpect(status().isOk());

        // Validate the Tweet in the database
        List<Tweet> tweetList = tweetRepository.findAll();
        assertThat(tweetList).hasSize(databaseSizeBeforeUpdate);
        Tweet testTweet = tweetList.get(tweetList.size() - 1);
        assertThat(testTweet.getContent()).isEqualTo(UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void patchNonExistingTweet() throws Exception {
        int databaseSizeBeforeUpdate = tweetRepository.findAll().size();
        tweet.setId(count.incrementAndGet());

        // Create the Tweet
        TweetDTO tweetDTO = tweetMapper.toDto(tweet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTweetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tweetDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tweetDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tweet in the database
        List<Tweet> tweetList = tweetRepository.findAll();
        assertThat(tweetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTweet() throws Exception {
        int databaseSizeBeforeUpdate = tweetRepository.findAll().size();
        tweet.setId(count.incrementAndGet());

        // Create the Tweet
        TweetDTO tweetDTO = tweetMapper.toDto(tweet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTweetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tweetDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tweet in the database
        List<Tweet> tweetList = tweetRepository.findAll();
        assertThat(tweetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTweet() throws Exception {
        int databaseSizeBeforeUpdate = tweetRepository.findAll().size();
        tweet.setId(count.incrementAndGet());

        // Create the Tweet
        TweetDTO tweetDTO = tweetMapper.toDto(tweet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTweetMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(tweetDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tweet in the database
        List<Tweet> tweetList = tweetRepository.findAll();
        assertThat(tweetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTweet() throws Exception {
        // Initialize the database
        tweetRepository.saveAndFlush(tweet);

        int databaseSizeBeforeDelete = tweetRepository.findAll().size();

        // Delete the tweet
        restTweetMockMvc
            .perform(delete(ENTITY_API_URL_ID, tweet.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Tweet> tweetList = tweetRepository.findAll();
        assertThat(tweetList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
