package com.ensias.twitter.service.mapper;

import com.ensias.twitter.domain.Tweet;
import com.ensias.twitter.service.dto.TweetDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Tweet} and its DTO {@link TweetDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface TweetMapper extends EntityMapper<TweetDTO, Tweet> {}
