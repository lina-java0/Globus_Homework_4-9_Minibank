package com.minibank.repository;

import com.minibank.model.Client;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    @EntityGraph(value = "Client.withAccounts")
    Optional<Client> findWithAccountsById(Long id);
}
