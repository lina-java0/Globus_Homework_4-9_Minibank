package com.minibank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minibank.exception.HttpExceptionMapper;
import com.minibank.exception.custom_exceptions.AccountNotFoundException;
import com.minibank.httpHandler.account.GetAccountHandler;
import com.minibank.httpHandler.account.PostAccountHandler;
import com.minibank.httpHandler.account.router.AccountRouter;
import com.minibank.httpHandler.client.GetClientHandler;
import com.minibank.httpHandler.client.PostClientHandler;
import com.minibank.httpHandler.client.router.ClientRouter;
import com.minibank.httpHandler.transfer.TransferHandler;
import com.minibank.model.Account;
import com.minibank.model.Client;
import com.minibank.service.BankService;
import com.sun.net.httpserver.HttpServer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

public class Main {

    private static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory("minibank");

    public static void main(String[] args) throws IOException {

        BankService bankService = new BankService();
        ObjectMapper objectMapper = new ObjectMapper();
        HttpExceptionMapper exceptionMapper = new HttpExceptionMapper();

        GetClientHandler getClientHandler = new GetClientHandler(
                bankService,
                objectMapper,
                exceptionMapper
        );

        PostClientHandler postClientHandler = new PostClientHandler(
                bankService,
                objectMapper,
                exceptionMapper
        );

        GetAccountHandler getAccountHandler = new GetAccountHandler(
                bankService,
                objectMapper,
                exceptionMapper
        );

        PostAccountHandler postAccountHandler = new PostAccountHandler(
                bankService,
                objectMapper,
                exceptionMapper
        );

        TransferHandler transferHandler = new TransferHandler(
                bankService,
                objectMapper,
                exceptionMapper
        );

        ClientRouter clientRouter = new ClientRouter(
                getClientHandler,
                postClientHandler,
                objectMapper,
                exceptionMapper
        );

        AccountRouter accountRouter = new AccountRouter(
                getAccountHandler,
                postAccountHandler,
                objectMapper,
                exceptionMapper
        );

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/clients", clientRouter);
        server.createContext("/accounts", accountRouter);
        server.createContext("/transfer", transferHandler);

        // Удаление аккаунта из коллекции клиента
//        testOrphanRemoval();

        // Проверка equals() и hashCode()
//        testEqualsAndHashCode();

        server.start();

        System.out.println("MiniBank server started on port 8080");
    }

    private static void testOrphanRemoval() {
        Long clientId = 1L;
        Long accountId = 113L;

        // Удаление аккаунта из коллекции клиента
        EntityManager entityManager = EMF.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try (entityManager) {
            transaction.begin();

            Client client = entityManager.find(Client.class, clientId);
            System.out.println("1. Client loaded");

            Account account = client.getAccounts().stream()
                    .filter(a -> a.getId().equals(accountId))
                    .findFirst()
                    .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountId));
            System.out.println("2. Account found");

            client.getAccounts().remove(account);
            System.out.println("3. Account removed from collection");

            transaction.commit();
            System.out.println("4. Transaction committed");
        } catch (RuntimeException ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw ex;
        }

        try (EntityManager verificationEntityManager = EMF.createEntityManager()) {
            Account deletedAccount = verificationEntityManager.find(Account.class, accountId);

            System.out.println("Account after deletion: " + deletedAccount);
        }
    }

    private static void testEqualsAndHashCode() {
        Client client1 = new Client("Colobok", "colobok@mail.ru");
        Client client2 = new Client("Colobok", "colobok@mail.ru");

        System.out.println("Client: ");
        System.out.println("client1 == client2: " + (client1 == client2));
        System.out.println("client1.equals(client2): " + client1.equals(client2));
        System.out.println("client1.hashCode() == client2.hashCode()): " + (client1.hashCode() == client2.hashCode()));

        Set<Client> clients = new HashSet<>();
        clients.add(client1);
        clients.add(client2);

        System.out.println("HashSet size: " + clients.size());

        Account account1 = new Account(client1, "ACC-001", BigDecimal.ZERO);
        Account account2 = new Account(client1, "ACC-001", new BigDecimal("500.00"));

        System.out.println();
        System.out.println("Account:");
        System.out.println("account1 == account2: " + (account1 == account2));
        System.out.println("account1.equals(account2): " + account1.equals(account2));
        System.out.println("account1.hashCode() == account2.hashCode()): " + (account1.hashCode() == account2.hashCode()));

        Set<Account> accounts = new HashSet<>();
        accounts.add(account1);
        accounts.add(account2);

        System.out.println("HashSet size: " + accounts.size());
    }
}
