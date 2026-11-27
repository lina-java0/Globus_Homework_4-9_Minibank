package com.minibank.controller;

import com.minibank.dto.account.request.OpenAccountRequest;
import com.minibank.dto.account.response.AccountResponse;
import com.minibank.model.Account;
import com.minibank.service.AccountService;
import com.minibank.validator.account.AccountValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class
AccountController {

    private final AccountService accountService;
    private final List<AccountValidator> accountValidatorList;

    public AccountController(
            AccountService accountService,
            List<AccountValidator> accountValidatorList
    ) {
        this.accountService = accountService;
        this.accountValidatorList = accountValidatorList;
    }

    // GET /accounts/{id}
    // Получение аккаунта по id
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(
            @PathVariable Long id
    ) {
        AccountResponse response = accountService.getAccount(id);

        return ResponseEntity.ok(response);
    }

    // POST /accounts
    // Открытие аккаунта (счета)
    @PostMapping
    public ResponseEntity<AccountResponse> openAccount(
            @RequestBody(required = false) OpenAccountRequest request
    ) {
        // Валидация запроса
        for (AccountValidator validator : accountValidatorList) {
            validator.validateRequest(request);
        }

        // Вызов сервиса
        Account account = accountService.openAccount(
                request.clientId(),
                request.accountNumber()
        );

        AccountResponse response = new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
