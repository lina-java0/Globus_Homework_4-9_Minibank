package com.minibank.controller;

import com.minibank.dto.client.request.RegisterClientRequest;
import com.minibank.dto.client.response.ClientResponse;
import com.minibank.model.Client;
import com.minibank.service.ClientService;
import com.minibank.validator.client.PostClientValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;
    private final List<PostClientValidator> clientValidatorList;

    public ClientController(
            ClientService clientService,
            List<PostClientValidator> clientValidatorList
    ) {
        this.clientService = clientService;
        this.clientValidatorList = clientValidatorList;
    }

    // GET /clients/{id}
    // Получение клиента по id
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> getClient(
            @PathVariable Long id
    ) {
        ClientResponse response = clientService.getClient(id);

        return ResponseEntity.ok(response);
    }

    // POST /clients
    // Регистрация клиента
    @PostMapping
    public ResponseEntity<ClientResponse> registerClient(
            @RequestBody RegisterClientRequest request
    ) {
        // Валидация запроса
        for (PostClientValidator validator : clientValidatorList) {
            validator.validateRequest(request);
        }

        // Вызов сервиса
        Client client = clientService.registerClient(
                request.name(),
                request.email()
        );

        ClientResponse response = new ClientResponse(
                client.getId(),
                client.getName(),
                client.getEmail(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
