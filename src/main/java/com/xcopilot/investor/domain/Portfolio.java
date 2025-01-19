package com.xcopilot.investor.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Portfolio.
 */
@Entity
@Table(name = "portfolio")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Portfolio implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "total_value", precision = 21, scale = 2)
    private BigDecimal totalValue;

    @Column(name = "last_updated")
    private Instant lastUpdated;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "portfolio")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "transactions", "portfolio" }, allowSetters = true)
    private Set<Holding> holdings = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "portfolios" }, allowSetters = true)
    private UserExtra owner;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Portfolio id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Portfolio name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Portfolio description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getTotalValue() {
        return this.totalValue;
    }

    public Portfolio totalValue(BigDecimal totalValue) {
        this.setTotalValue(totalValue);
        return this;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public Instant getLastUpdated() {
        return this.lastUpdated;
    }

    public Portfolio lastUpdated(Instant lastUpdated) {
        this.setLastUpdated(lastUpdated);
        return this;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Set<Holding> getHoldings() {
        return this.holdings;
    }

    public void setHoldings(Set<Holding> holdings) {
        if (this.holdings != null) {
            this.holdings.forEach(i -> i.setPortfolio(null));
        }
        if (holdings != null) {
            holdings.forEach(i -> i.setPortfolio(this));
        }
        this.holdings = holdings;
    }

    public Portfolio holdings(Set<Holding> holdings) {
        this.setHoldings(holdings);
        return this;
    }

    public Portfolio addHoldings(Holding holding) {
        this.holdings.add(holding);
        holding.setPortfolio(this);
        return this;
    }

    public Portfolio removeHoldings(Holding holding) {
        this.holdings.remove(holding);
        holding.setPortfolio(null);
        return this;
    }

    public UserExtra getOwner() {
        return this.owner;
    }

    public void setOwner(UserExtra userExtra) {
        this.owner = userExtra;
    }

    public Portfolio owner(UserExtra userExtra) {
        this.setOwner(userExtra);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Portfolio)) {
            return false;
        }
        return getId() != null && getId().equals(((Portfolio) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Portfolio{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", totalValue=" + getTotalValue() +
            ", lastUpdated='" + getLastUpdated() + "'" +
            "}";
    }
}
