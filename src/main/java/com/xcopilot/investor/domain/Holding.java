package com.xcopilot.investor.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Holding.
 */
@Entity
@Table(name = "holding")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Holding implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "symbol", nullable = false)
    private String symbol;

    @NotNull
    @Column(name = "quantity", precision = 21, scale = 2, nullable = false)
    private BigDecimal quantity;

    @NotNull
    @Column(name = "average_cost", precision = 21, scale = 2, nullable = false)
    private BigDecimal averageCost;

    @Column(name = "current_price", precision = 21, scale = 2)
    private BigDecimal currentPrice;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "holding")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "holding" }, allowSetters = true)
    private Set<Transaction> transactions = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "holdings", "owner" }, allowSetters = true)
    private Portfolio portfolio;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Holding id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public Holding symbol(String symbol) {
        this.setSymbol(symbol);
        return this;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getQuantity() {
        return this.quantity;
    }

    public Holding quantity(BigDecimal quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAverageCost() {
        return this.averageCost;
    }

    public Holding averageCost(BigDecimal averageCost) {
        this.setAverageCost(averageCost);
        return this;
    }

    public void setAverageCost(BigDecimal averageCost) {
        this.averageCost = averageCost;
    }

    public BigDecimal getCurrentPrice() {
        return this.currentPrice;
    }

    public Holding currentPrice(BigDecimal currentPrice) {
        this.setCurrentPrice(currentPrice);
        return this;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Set<Transaction> getTransactions() {
        return this.transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        if (this.transactions != null) {
            this.transactions.forEach(i -> i.setHolding(null));
        }
        if (transactions != null) {
            transactions.forEach(i -> i.setHolding(this));
        }
        this.transactions = transactions;
    }

    public Holding transactions(Set<Transaction> transactions) {
        this.setTransactions(transactions);
        return this;
    }

    public Holding addTransactions(Transaction transaction) {
        this.transactions.add(transaction);
        transaction.setHolding(this);
        return this;
    }

    public Holding removeTransactions(Transaction transaction) {
        this.transactions.remove(transaction);
        transaction.setHolding(null);
        return this;
    }

    public Portfolio getPortfolio() {
        return this.portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public Holding portfolio(Portfolio portfolio) {
        this.setPortfolio(portfolio);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Holding)) {
            return false;
        }
        return getId() != null && getId().equals(((Holding) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Holding{" +
            "id=" + getId() +
            ", symbol='" + getSymbol() + "'" +
            ", quantity=" + getQuantity() +
            ", averageCost=" + getAverageCost() +
            ", currentPrice=" + getCurrentPrice() +
            "}";
    }
}
