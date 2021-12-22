package com.ensias.twitter.web.rest;

import com.ensias.twitter.repository.TweetRepository;
import com.ensias.twitter.service.TweetService;
import com.ensias.twitter.service.dto.TweetDTO;
import com.ensias.twitter.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ensias.twitter.domain.Tweet}.
 */
@RestController
@RequestMapping("/api")
public class TweetResource {

    private final Logger log = LoggerFactory.getLogger(TweetResource.class);

    private static final String ENTITY_NAME = "tweetTweet";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TweetService tweetService;

    private final TweetRepository tweetRepository;

    public TweetResource(TweetService tweetService, TweetRepository tweetRepository) {
        this.tweetService = tweetService;
        this.tweetRepository = tweetRepository;
    }

    /**
     * {@code POST  /tweets} : Create a new tweet.
     *
     * @param tweetDTO the tweetDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tweetDTO, or with status {@code 400 (Bad Request)} if the tweet has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tweets")
    public ResponseEntity<TweetDTO> createTweet(@Valid @RequestBody TweetDTO tweetDTO) throws URISyntaxException {
        log.debug("REST request to save Tweet : {}", tweetDTO);
        if (tweetDTO.getId() != null) {
            throw new BadRequestAlertException("A new tweet cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TweetDTO result = tweetService.save(tweetDTO);
        return ResponseEntity
            .created(new URI("/api/tweets/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /tweets/:id} : Updates an existing tweet.
     *
     * @param id the id of the tweetDTO to save.
     * @param tweetDTO the tweetDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tweetDTO,
     * or with status {@code 400 (Bad Request)} if the tweetDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tweetDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tweets/{id}")
    public ResponseEntity<TweetDTO> updateTweet(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TweetDTO tweetDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Tweet : {}, {}", id, tweetDTO);
        if (tweetDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tweetDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tweetRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TweetDTO result = tweetService.save(tweetDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, tweetDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /tweets/:id} : Partial updates given fields of an existing tweet, field will ignore if it is null
     *
     * @param id the id of the tweetDTO to save.
     * @param tweetDTO the tweetDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tweetDTO,
     * or with status {@code 400 (Bad Request)} if the tweetDTO is not valid,
     * or with status {@code 404 (Not Found)} if the tweetDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the tweetDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/tweets/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TweetDTO> partialUpdateTweet(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TweetDTO tweetDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Tweet partially : {}, {}", id, tweetDTO);
        if (tweetDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tweetDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tweetRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TweetDTO> result = tweetService.partialUpdate(tweetDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, tweetDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /tweets} : get all the tweets.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tweets in body.
     */
    @GetMapping("/tweets")
    public List<TweetDTO> getAllTweets() {
        log.debug("REST request to get all Tweets");
        return tweetService.findAll();
    }

    /**
     * {@code GET  /tweets/:id} : get the "id" tweet.
     *
     * @param id the id of the tweetDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tweetDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tweets/{id}")
    public ResponseEntity<TweetDTO> getTweet(@PathVariable Long id) {
        log.debug("REST request to get Tweet : {}", id);
        Optional<TweetDTO> tweetDTO = tweetService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tweetDTO);
    }

    /**
     * {@code DELETE  /tweets/:id} : delete the "id" tweet.
     *
     * @param id the id of the tweetDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tweets/{id}")
    public ResponseEntity<Void> deleteTweet(@PathVariable Long id) {
        log.debug("REST request to delete Tweet : {}", id);
        tweetService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
