package com.ensias.socialgraph.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ensias.socialgraph.domain.Follow;
import com.ensias.socialgraph.repository.FollowRepository;
import com.ensias.socialgraph.repository.search.FollowSearchRepository;
import com.ensias.socialgraph.service.FollowService;
import com.ensias.socialgraph.service.dto.FollowDTO;
import com.ensias.socialgraph.service.mapper.FollowMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Follow}.
 */
@Service
@Transactional
public class FollowServiceImpl implements FollowService {

    private final Logger log = LoggerFactory.getLogger(FollowServiceImpl.class);

    private final FollowRepository followRepository;

    private final FollowMapper followMapper;

    private final FollowSearchRepository followSearchRepository;

    public FollowServiceImpl(FollowRepository followRepository, FollowMapper followMapper, FollowSearchRepository followSearchRepository) {
        this.followRepository = followRepository;
        this.followMapper = followMapper;
        this.followSearchRepository = followSearchRepository;
    }

    @Override
    public FollowDTO save(FollowDTO followDTO) {
        log.debug("Request to save Follow : {}", followDTO);
        Follow follow = followMapper.toEntity(followDTO);
        follow = followRepository.save(follow);
        FollowDTO result = followMapper.toDto(follow);
        followSearchRepository.save(follow);
        return result;
    }

    @Override
    public Optional<FollowDTO> partialUpdate(FollowDTO followDTO) {
        log.debug("Request to partially update Follow : {}", followDTO);

        return followRepository
            .findById(followDTO.getId())
            .map(existingFollow -> {
                followMapper.partialUpdate(existingFollow, followDTO);

                return existingFollow;
            })
            .map(followRepository::save)
            .map(savedFollow -> {
                followSearchRepository.save(savedFollow);

                return savedFollow;
            })
            .map(followMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FollowDTO> findAll() {
        log.debug("Request to get all Follows");
        return followRepository.findAll().stream().map(followMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FollowDTO> findOne(Long id) {
        log.debug("Request to get Follow : {}", id);
        return followRepository.findById(id).map(followMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Follow : {}", id);
        followRepository.deleteById(id);
        followSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FollowDTO> search(String query) {
        log.debug("Request to search Follows for query {}", query);
        return StreamSupport
            .stream(followSearchRepository.search(query).spliterator(), false)
            .map(followMapper::toDto)
            .collect(Collectors.toList());
    }
}
