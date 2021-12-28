package com.ensias.socialgraph.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ensias.socialgraph.IntegrationTest;
import com.ensias.socialgraph.domain.Follow;
import com.ensias.socialgraph.domain.enumeration.Following;
import com.ensias.socialgraph.repository.FollowRepository;
import com.ensias.socialgraph.repository.search.FollowSearchRepository;
import com.ensias.socialgraph.service.dto.FollowDTO;
import com.ensias.socialgraph.service.mapper.FollowMapper;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link FollowResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class FollowResourceIT {

    private static final Following DEFAULT_FOLLOW = Following.FOLLOW;
    private static final Following UPDATED_FOLLOW = Following.UNFOLLOW;

    private static final String ENTITY_API_URL = "/api/follows";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/follows";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private FollowMapper followMapper;

    /**
     * This repository is mocked in the com.ensias.socialgraph.repository.search test package.
     *
     * @see com.ensias.socialgraph.repository.search.FollowSearchRepositoryMockConfiguration
     */
    @Autowired
    private FollowSearchRepository mockFollowSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFollowMockMvc;

    private Follow follow;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Follow createEntity(EntityManager em) {
        Follow follow = new Follow().follow(DEFAULT_FOLLOW);
        return follow;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Follow createUpdatedEntity(EntityManager em) {
        Follow follow = new Follow().follow(UPDATED_FOLLOW);
        return follow;
    }

    @BeforeEach
    public void initTest() {
        follow = createEntity(em);
    }

    @Test
    @Transactional
    void createFollow() throws Exception {
        int databaseSizeBeforeCreate = followRepository.findAll().size();
        // Create the Follow
        FollowDTO followDTO = followMapper.toDto(follow);
        restFollowMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(followDTO)))
            .andExpect(status().isCreated());

        // Validate the Follow in the database
        List<Follow> followList = followRepository.findAll();
        assertThat(followList).hasSize(databaseSizeBeforeCreate + 1);
        Follow testFollow = followList.get(followList.size() - 1);
        assertThat(testFollow.getFollow()).isEqualTo(DEFAULT_FOLLOW);

        // Validate the Follow in Elasticsearch
        verify(mockFollowSearchRepository, times(1)).save(testFollow);
    }

    @Test
    @Transactional
    void createFollowWithExistingId() throws Exception {
        // Create the Follow with an existing ID
        follow.setId(1L);
        FollowDTO followDTO = followMapper.toDto(follow);

        int databaseSizeBeforeCreate = followRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFollowMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(followDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Follow in the database
        List<Follow> followList = followRepository.findAll();
        assertThat(followList).hasSize(databaseSizeBeforeCreate);

        // Validate the Follow in Elasticsearch
        verify(mockFollowSearchRepository, times(0)).save(follow);
    }

    @Test
    @Transactional
    void getAllFollows() throws Exception {
        // Initialize the database
        followRepository.saveAndFlush(follow);

        // Get all the followList
        restFollowMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(follow.getId().intValue())))
            .andExpect(jsonPath("$.[*].follow").value(hasItem(DEFAULT_FOLLOW.toString())));
    }

    @Test
    @Transactional
    void getFollow() throws Exception {
        // Initialize the database
        followRepository.saveAndFlush(follow);

        // Get the follow
        restFollowMockMvc
            .perform(get(ENTITY_API_URL_ID, follow.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(follow.getId().intValue()))
            .andExpect(jsonPath("$.follow").value(DEFAULT_FOLLOW.toString()));
    }

    @Test
    @Transactional
    void getNonExistingFollow() throws Exception {
        // Get the follow
        restFollowMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewFollow() throws Exception {
        // Initialize the database
        followRepository.saveAndFlush(follow);

        int databaseSizeBeforeUpdate = followRepository.findAll().size();

        // Update the follow
        Follow updatedFollow = followRepository.findById(follow.getId()).get();
        // Disconnect from session so that the updates on updatedFollow are not directly saved in db
        em.detach(updatedFollow);
        updatedFollow.follow(UPDATED_FOLLOW);
        FollowDTO followDTO = followMapper.toDto(updatedFollow);

        restFollowMockMvc
            .perform(
                put(ENTITY_API_URL_ID, followDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(followDTO))
            )
            .andExpect(status().isOk());

        // Validate the Follow in the database
        List<Follow> followList = followRepository.findAll();
        assertThat(followList).hasSize(databaseSizeBeforeUpdate);
        Follow testFollow = followList.get(followList.size() - 1);
        assertThat(testFollow.getFollow()).isEqualTo(UPDATED_FOLLOW);

        // Validate the Follow in Elasticsearch
        verify(mockFollowSearchRepository).save(testFollow);
    }

    @Test
    @Transactional
    void putNonExistingFollow() throws Exception {
        int databaseSizeBeforeUpdate = followRepository.findAll().size();
        follow.setId(count.incrementAndGet());

        // Create the Follow
        FollowDTO followDTO = followMapper.toDto(follow);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFollowMockMvc
            .perform(
                put(ENTITY_API_URL_ID, followDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(followDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Follow in the database
        List<Follow> followList = followRepository.findAll();
        assertThat(followList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Follow in Elasticsearch
        verify(mockFollowSearchRepository, times(0)).save(follow);
    }

    @Test
    @Transactional
    void putWithIdMismatchFollow() throws Exception {
        int databaseSizeBeforeUpdate = followRepository.findAll().size();
        follow.setId(count.incrementAndGet());

        // Create the Follow
        FollowDTO followDTO = followMapper.toDto(follow);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFollowMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(followDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Follow in the database
        List<Follow> followList = followRepository.findAll();
        assertThat(followList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Follow in Elasticsearch
        verify(mockFollowSearchRepository, times(0)).save(follow);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFollow() throws Exception {
        int databaseSizeBeforeUpdate = followRepository.findAll().size();
        follow.setId(count.incrementAndGet());

        // Create the Follow
        FollowDTO followDTO = followMapper.toDto(follow);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFollowMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(followDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Follow in the database
        List<Follow> followList = followRepository.findAll();
        assertThat(followList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Follow in Elasticsearch
        verify(mockFollowSearchRepository, times(0)).save(follow);
    }

    @Test
    @Transactional
    void partialUpdateFollowWithPatch() throws Exception {
        // Initialize the database
        followRepository.saveAndFlush(follow);

        int databaseSizeBeforeUpdate = followRepository.findAll().size();

        // Update the follow using partial update
        Follow partialUpdatedFollow = new Follow();
        partialUpdatedFollow.setId(follow.getId());

        restFollowMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFollow.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFollow))
            )
            .andExpect(status().isOk());

        // Validate the Follow in the database
        List<Follow> followList = followRepository.findAll();
        assertThat(followList).hasSize(databaseSizeBeforeUpdate);
        Follow testFollow = followList.get(followList.size() - 1);
        assertThat(testFollow.getFollow()).isEqualTo(DEFAULT_FOLLOW);
    }

    @Test
    @Transactional
    void fullUpdateFollowWithPatch() throws Exception {
        // Initialize the database
        followRepository.saveAndFlush(follow);

        int databaseSizeBeforeUpdate = followRepository.findAll().size();

        // Update the follow using partial update
        Follow partialUpdatedFollow = new Follow();
        partialUpdatedFollow.setId(follow.getId());

        partialUpdatedFollow.follow(UPDATED_FOLLOW);

        restFollowMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFollow.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFollow))
            )
            .andExpect(status().isOk());

        // Validate the Follow in the database
        List<Follow> followList = followRepository.findAll();
        assertThat(followList).hasSize(databaseSizeBeforeUpdate);
        Follow testFollow = followList.get(followList.size() - 1);
        assertThat(testFollow.getFollow()).isEqualTo(UPDATED_FOLLOW);
    }

    @Test
    @Transactional
    void patchNonExistingFollow() throws Exception {
        int databaseSizeBeforeUpdate = followRepository.findAll().size();
        follow.setId(count.incrementAndGet());

        // Create the Follow
        FollowDTO followDTO = followMapper.toDto(follow);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFollowMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, followDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(followDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Follow in the database
        List<Follow> followList = followRepository.findAll();
        assertThat(followList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Follow in Elasticsearch
        verify(mockFollowSearchRepository, times(0)).save(follow);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFollow() throws Exception {
        int databaseSizeBeforeUpdate = followRepository.findAll().size();
        follow.setId(count.incrementAndGet());

        // Create the Follow
        FollowDTO followDTO = followMapper.toDto(follow);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFollowMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(followDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Follow in the database
        List<Follow> followList = followRepository.findAll();
        assertThat(followList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Follow in Elasticsearch
        verify(mockFollowSearchRepository, times(0)).save(follow);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFollow() throws Exception {
        int databaseSizeBeforeUpdate = followRepository.findAll().size();
        follow.setId(count.incrementAndGet());

        // Create the Follow
        FollowDTO followDTO = followMapper.toDto(follow);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFollowMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(followDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Follow in the database
        List<Follow> followList = followRepository.findAll();
        assertThat(followList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Follow in Elasticsearch
        verify(mockFollowSearchRepository, times(0)).save(follow);
    }

    @Test
    @Transactional
    void deleteFollow() throws Exception {
        // Initialize the database
        followRepository.saveAndFlush(follow);

        int databaseSizeBeforeDelete = followRepository.findAll().size();

        // Delete the follow
        restFollowMockMvc
            .perform(delete(ENTITY_API_URL_ID, follow.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Follow> followList = followRepository.findAll();
        assertThat(followList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Follow in Elasticsearch
        verify(mockFollowSearchRepository, times(1)).deleteById(follow.getId());
    }

    @Test
    @Transactional
    void searchFollow() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        followRepository.saveAndFlush(follow);
        when(mockFollowSearchRepository.search("id:" + follow.getId())).thenReturn(Stream.of(follow));

        // Search the follow
        restFollowMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + follow.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(follow.getId().intValue())))
            .andExpect(jsonPath("$.[*].follow").value(hasItem(DEFAULT_FOLLOW.toString())));
    }
}
