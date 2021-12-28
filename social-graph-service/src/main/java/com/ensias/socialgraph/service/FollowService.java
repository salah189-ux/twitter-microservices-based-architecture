package com.ensias.socialgraph.service;

import com.ensias.socialgraph.service.dto.FollowDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.ensias.socialgraph.domain.Follow}.
 */
public interface FollowService {
    /**
     * Save a follow.
     *
     * @param followDTO the entity to save.
     * @return the persisted entity.
     */
    FollowDTO save(FollowDTO followDTO);

    /**
     * Partially updates a follow.
     *
     * @param followDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<FollowDTO> partialUpdate(FollowDTO followDTO);

    /**
     * Get all the follows.
     *
     * @return the list of entities.
     */
    List<FollowDTO> findAll();

    /**
     * Get the "id" follow.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<FollowDTO> findOne(Long id);

    /**
     * Delete the "id" follow.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the follow corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    List<FollowDTO> search(String query);
}
