package com.minibank;

import com.minibank.httpHandler.TransferHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args) throws IOException {

        BankService bank = new BankService();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/transfer", new TransferHandler(bank));

        server.start();

        System.out.println("MiniBank server started on port 8080");
    }
}
