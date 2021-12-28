package com.ensias.socialgraph.domain;

import com.ensias.socialgraph.domain.enumeration.Following;
import java.io.Serializable;
import javax.persistence.*;

/**
 * A Follow.
 */
@Entity
@Table(name = "follow")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "follow")
public class Follow implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "follow")
    private Following follow;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Follow id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Following getFollow() {
        return this.follow;
    }

    public Follow follow(Following follow) {
        this.setFollow(follow);
        return this;
    }

    public void setFollow(Following follow) {
        this.follow = follow;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Follow)) {
            return false;
        }
        return id != null && id.equals(((Follow) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Follow{" +
            "id=" + getId() +
            ", follow='" + getFollow() + "'" +
            "}";
    }
}
