package com.minibank.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "account_number", nullable = false, unique = true, length = 34)
    private String accountNumber;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @OneToMany(
            mappedBy = "account",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    private List<Transaction> transactions = new ArrayList<>();

    protected Account() {
    }

    public Account(Client client, String accountNumber, BigDecimal balance) {
        this.client = client;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        transaction.setAccount(this);
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Long getId() { return id; }
    public Client getClient() { return client; }
    public String getAccountNumber() { return accountNumber; }
    public BigDecimal getBalance() { return balance; }
    public List<Transaction> getTransactions() {
        return transactions;
    }

    // Временная совместимость с кодом, который использовал clientId
    public Long getClientId() {
        return client.getId();
    }
}
