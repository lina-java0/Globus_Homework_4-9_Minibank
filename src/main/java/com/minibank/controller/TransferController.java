package com.minibank.controller;

import com.minibank.dto.transfer.TransferRequest;
import com.minibank.dto.transfer.TransferResponse;
import com.minibank.service.TransferService;
import com.minibank.validator.transfer.TransferValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transfer")
public class TransferController {

    private final TransferService transferService;
    private final List<TransferValidator> transferValidatorList;

    public TransferController(
            TransferService transferService,
            List<TransferValidator> transferValidatorList
    ) {
        this.transferService = transferService;
        this.transferValidatorList = transferValidatorList;
    }

    // POST /transfer
    // Перевод средств между аккаунтами
    @PostMapping
    public ResponseEntity<TransferResponse> transfer(
            @RequestBody(required = false) TransferRequest request
    ) {
        // Валидация запроса
        for (TransferValidator validator : transferValidatorList) {
            validator.validateRequest(request);
        }

        // Вызов сервиса
        transferService.transfer(
                request.fromAccountId(),
                request.toAccountId(),
                request.amount()
        );

        TransferResponse response = new TransferResponse("Transfer successful");

        return ResponseEntity.ok(response);
    }
}
