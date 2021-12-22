package com.ensias.twitter.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.ensias.twitter.domain.Tweet} entity.
 */
public class TweetDTO implements Serializable {

    private Long id;

    @NotNull
    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TweetDTO)) {
            return false;
        }

        TweetDTO tweetDTO = (TweetDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, tweetDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TweetDTO{" +
            "id=" + getId() +
            ", content='" + getContent() + "'" +
            "}";
    }
}
