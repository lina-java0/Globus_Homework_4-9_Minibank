package com.minibank.controller;

import com.minibank.dto.transaction.TransferRequest;
import com.minibank.dto.transaction.TransferResponse;
import com.minibank.service.TransactionService;
import com.minibank.validator.transaction.TransferValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transfer")
public class TransactionController {

    private final TransactionService transactionService;
    private final List<TransferValidator> transferValidatorList;

    public TransactionController(
            TransactionService transactionService,
            List<TransferValidator> transferValidatorList
    ) {
        this.transactionService = transactionService;
        this.transferValidatorList = transferValidatorList;
    }

    // POST /transfer
    // Перевод средств между аккаунтами
    @PostMapping
    public ResponseEntity<TransferResponse> transfer(
            @RequestBody TransferRequest request
    ) {
        // Валидация запроса
        for (TransferValidator validator : transferValidatorList) {
            validator.validateRequest(request);
        }

        // Вызов сервиса
        transactionService.transfer(
                request.fromAccountId(),
                request.toAccountId(),
                request.amount()
        );

        TransferResponse response = new TransferResponse("Transfer successful");

        return ResponseEntity.ok(response);
    }
}
