package com.ensias.twitter.service.impl;

import com.ensias.twitter.domain.Tweet;
import com.ensias.twitter.repository.TweetRepository;
import com.ensias.twitter.service.TweetService;
import com.ensias.twitter.service.dto.TweetDTO;
import com.ensias.twitter.service.mapper.TweetMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Tweet}.
 */
@Service
@Transactional
public class TweetServiceImpl implements TweetService {

    private final Logger log = LoggerFactory.getLogger(TweetServiceImpl.class);

    private final TweetRepository tweetRepository;

    private final TweetMapper tweetMapper;

    public TweetServiceImpl(TweetRepository tweetRepository, TweetMapper tweetMapper) {
        this.tweetRepository = tweetRepository;
        this.tweetMapper = tweetMapper;
    }

    @Override
    public TweetDTO save(TweetDTO tweetDTO) {
        log.debug("Request to save Tweet : {}", tweetDTO);
        Tweet tweet = tweetMapper.toEntity(tweetDTO);
        tweet = tweetRepository.save(tweet);
        return tweetMapper.toDto(tweet);
    }

    @Override
    public Optional<TweetDTO> partialUpdate(TweetDTO tweetDTO) {
        log.debug("Request to partially update Tweet : {}", tweetDTO);

        return tweetRepository
            .findById(tweetDTO.getId())
            .map(existingTweet -> {
                tweetMapper.partialUpdate(existingTweet, tweetDTO);

                return existingTweet;
            })
            .map(tweetRepository::save)
            .map(tweetMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TweetDTO> findAll() {
        log.debug("Request to get all Tweets");
        return tweetRepository.findAll().stream().map(tweetMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TweetDTO> findOne(Long id) {
        log.debug("Request to get Tweet : {}", id);
        return tweetRepository.findById(id).map(tweetMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Tweet : {}", id);
        tweetRepository.deleteById(id);
    }
}
