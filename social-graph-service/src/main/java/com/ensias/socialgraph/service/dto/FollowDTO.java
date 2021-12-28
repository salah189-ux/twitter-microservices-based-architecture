package com.ensias.socialgraph.service.dto;

import com.ensias.socialgraph.domain.enumeration.Following;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.ensias.socialgraph.domain.Follow} entity.
 */
public class FollowDTO implements Serializable {

    private Long id;

    private Following follow;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Following getFollow() {
        return follow;
    }

    public void setFollow(Following follow) {
        this.follow = follow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FollowDTO)) {
            return false;
        }

        FollowDTO followDTO = (FollowDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, followDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FollowDTO{" +
            "id=" + getId() +
            ", follow='" + getFollow() + "'" +
            "}";
    }
}
