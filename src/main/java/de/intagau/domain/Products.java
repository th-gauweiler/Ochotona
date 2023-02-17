package de.intagau.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Products.
 */
@Entity
@Table(name = "products")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Products implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "url")
    private String url;

    @Column(name = "ean")
    private String ean;

    @Column(name = "tags")
    private String tags;

    @OneToMany(mappedBy = "products")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "inherit", "contains", "products" }, allowSetters = true)
    private Set<StorageRoom> storeds = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Products id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Products name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return this.url;
    }

    public Products url(String url) {
        this.setUrl(url);
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEan() {
        return this.ean;
    }

    public Products ean(String ean) {
        this.setEan(ean);
        return this;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getTags() {
        return this.tags;
    }

    public Products tags(String tags) {
        this.setTags(tags);
        return this;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Set<StorageRoom> getStoreds() {
        return this.storeds;
    }

    public void setStoreds(Set<StorageRoom> storageRooms) {
        if (this.storeds != null) {
            this.storeds.forEach(i -> i.setProducts(null));
        }
        if (storageRooms != null) {
            storageRooms.forEach(i -> i.setProducts(this));
        }
        this.storeds = storageRooms;
    }

    public Products storeds(Set<StorageRoom> storageRooms) {
        this.setStoreds(storageRooms);
        return this;
    }

    public Products addStored(StorageRoom storageRoom) {
        this.storeds.add(storageRoom);
        storageRoom.setProducts(this);
        return this;
    }

    public Products removeStored(StorageRoom storageRoom) {
        this.storeds.remove(storageRoom);
        storageRoom.setProducts(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Products)) {
            return false;
        }
        return id != null && id.equals(((Products) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Products{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", url='" + getUrl() + "'" +
            ", ean='" + getEan() + "'" +
            ", tags='" + getTags() + "'" +
            "}";
    }
}
