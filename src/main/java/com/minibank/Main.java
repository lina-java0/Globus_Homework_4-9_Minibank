package com.minibank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minibank.exception.HttpExceptionMapper;
import com.minibank.httpHandler.account.AccountHandler;
import com.minibank.httpHandler.client.ClientHandler;
import com.minibank.httpHandler.transfer.TransferHandler;
import com.minibank.service.BankService;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args) throws IOException {

        BankService bankService = new BankService();
        ObjectMapper objectMapper = new ObjectMapper();
        HttpExceptionMapper exceptionMapper = new HttpExceptionMapper();

        ClientHandler clientHandler = new ClientHandler(
                bankService,
                objectMapper,
                exceptionMapper
        );

        TransferHandler transferHandler = new TransferHandler(
                bankService,
                objectMapper,
                exceptionMapper
        );

        AccountHandler accountHandler = new AccountHandler(
                bankService,
                objectMapper,
                exceptionMapper
        );

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/transfer", transferHandler);
        server.createContext("/clients", clientHandler);
        server.createContext("/accounts", accountHandler);

        server.start();

        System.out.println("MiniBank server started on port 8080");
    }
}
