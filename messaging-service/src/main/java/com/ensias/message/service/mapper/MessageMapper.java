package com.ensias.message.service.mapper;

import com.ensias.message.domain.Message;
import com.ensias.message.service.dto.MessageDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Message} and its DTO {@link MessageDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface MessageMapper extends EntityMapper<MessageDTO, Message> {}
