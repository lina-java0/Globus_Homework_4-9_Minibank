package com.minibank.validator.client;

import com.minibank.dto.client.RegisterClientRequest;

public interface ClientValidator {

    void validateRequest(RegisterClientRequest request);
}
