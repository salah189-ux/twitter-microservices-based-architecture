package com.ensias.socialgraph.service.mapper;

import com.ensias.socialgraph.domain.Follow;
import com.ensias.socialgraph.service.dto.FollowDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Follow} and its DTO {@link FollowDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface FollowMapper extends EntityMapper<FollowDTO, Follow> {}
