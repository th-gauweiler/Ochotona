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
 * A StorageRoom.
 */
@Entity
@Table(name = "storage_room")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StorageRoom implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @JsonIgnoreProperties(value = { "storageRoom" }, allowSetters = true)
    @OneToOne(optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private Storage inherit;

    @OneToMany(mappedBy = "storageRoom")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "storageRoom" }, allowSetters = true)
    private Set<Storage> contains = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "storeds" }, allowSetters = true)
    private Products products;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StorageRoom id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public StorageRoom name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Storage getInherit() {
        return this.inherit;
    }

    public void setInherit(Storage storage) {
        this.inherit = storage;
    }

    public StorageRoom inherit(Storage storage) {
        this.setInherit(storage);
        return this;
    }

    public Set<Storage> getContains() {
        return this.contains;
    }

    public void setContains(Set<Storage> storages) {
        if (this.contains != null) {
            this.contains.forEach(i -> i.setStorageRoom(null));
        }
        if (storages != null) {
            storages.forEach(i -> i.setStorageRoom(this));
        }
        this.contains = storages;
    }

    public StorageRoom contains(Set<Storage> storages) {
        this.setContains(storages);
        return this;
    }

    public StorageRoom addContains(Storage storage) {
        this.contains.add(storage);
        storage.setStorageRoom(this);
        return this;
    }

    public StorageRoom removeContains(Storage storage) {
        this.contains.remove(storage);
        storage.setStorageRoom(null);
        return this;
    }

    public Products getProducts() {
        return this.products;
    }

    public void setProducts(Products products) {
        this.products = products;
    }

    public StorageRoom products(Products products) {
        this.setProducts(products);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StorageRoom)) {
            return false;
        }
        return id != null && id.equals(((StorageRoom) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StorageRoom{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
