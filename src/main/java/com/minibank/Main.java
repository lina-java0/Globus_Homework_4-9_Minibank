package com.minibank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minibank.exception.HttpExceptionMapper;
import com.minibank.httpHandler.account.GetAccountHandler;
import com.minibank.httpHandler.account.PostAccountHandler;
import com.minibank.httpHandler.account.router.AccountRouter;
import com.minibank.httpHandler.client.GetClientHandler;
import com.minibank.httpHandler.client.PostClientHandler;
import com.minibank.httpHandler.client.router.ClientRouter;
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

        server.start();

        System.out.println("MiniBank server started on port 8080");
    }
}
