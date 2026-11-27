package com.minibank.service;

import com.minibank.dto.client.response.ClientResponse;
import com.minibank.exception.custom_exceptions.ClientNotFoundException;
import com.minibank.exception.custom_exceptions.EmailAlreadyExistsException;
import com.minibank.mapper.AccountMapper;
import com.minibank.mapper.ClientMapper;
import com.minibank.model.Client;
import com.minibank.repository.ClientRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    private final AccountMapper accountMapper = new AccountMapper();
    private final ClientMapper clientMapper = new ClientMapper(accountMapper);

    public ClientService(
            ClientRepository clientRepository
    ) {
        this.clientRepository = clientRepository;
    }

    // Регистрация клиента
    public Client registerClient(String name, String email) {
        Client client = new Client(name, email);

        try {
            return clientRepository.save(client);
        } catch (DataIntegrityViolationException ex) {
            throw new EmailAlreadyExistsException("Client with this email already exists: " + email);
        }
    }

    // Получение клиента по id вместе с его счетами
    public ClientResponse getClient(Long clientId) {
        Client client = clientRepository.findWithAccountsById(clientId)
                .orElseThrow(() -> new ClientNotFoundException("Client not found: " + clientId));

        return clientMapper.toResponse(
                client,
                client.getAccounts().stream()
                        .map(accountMapper::toResponse)
                        .toList()
        );
    }
}
