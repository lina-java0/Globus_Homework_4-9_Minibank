package com.minibank.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "clients")
@NamedEntityGraph(
        name = "Client.withAccounts",
        attributeNodes = {
                @NamedAttributeNode("accounts")
        }
)
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(
            mappedBy = "client",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Account> accounts = new ArrayList<>();

    protected Client() {
    }

    public Client(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public void addAccount(Account account) {
        accounts.add(account);
        account.setClient(this);
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public List<Account> getAccounts() { return accounts; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client client)) return false;
        return Objects.equals(email, client.email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }
}
