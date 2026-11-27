package com.minibank;

import com.minibank.model.Account;
import com.minibank.model.Client;
import com.minibank.repository.*;
import com.minibank.service.BankService;

import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        ClientRepository clientRepository = new ClientRepository(connectionFactory);
        AccountRepository accountRepository = new AccountRepository(connectionFactory);
        TransferRepository transactionRepository = new TransferRepository(connectionFactory);

        BankService bank = new BankService(
                clientRepository,
                accountRepository,
                transactionRepository
        );

        Client client = bank.registerClient("Polina Petrova", "polina_lina@example.com");
        Account account1 = bank.openAccount(client.getId(), "ACC-110001");
        Account account2 = bank.openAccount(client.getId(), "ACC-110002");

        // Пополняем первый счёт перед демонстрацией переводов
        bank.deposit(account1.getId(), new BigDecimal("1000.00"));

        System.out.println("=== Обычный перевод ===");
        bank.transfer(account1.getId(), account2.getId(), new BigDecimal("100.00"));

        System.out.println("account1 balance: " + bank.getAccount(account1.getId()).getBalance());
        System.out.println("account2 balance: " + bank.getAccount(account2.getId()).getBalance());

        System.out.println();
        System.out.println("=== Перевод на несуществующий счёт (баг) ===");

        BigDecimal balanceBefore = bank.getAccount(account1.getId()).getBalance();

        try {
            bank.transfer(account1.getId(), 999L, new BigDecimal("50.00"));
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка (как и ожидалось): " + e.getMessage());
        }

        BigDecimal balanceAfter = bank.getAccount(account1.getId()).getBalance();

        System.out.println("Баланс до попытки перевода: " + balanceBefore);
        System.out.println("Баланс после попытки перевода: " + balanceAfter);
        System.out.println("Деньги пропали: " + (balanceAfter.compareTo(balanceBefore) < 0));
    }
}
