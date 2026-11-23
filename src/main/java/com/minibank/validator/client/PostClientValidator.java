package com.minibank.validator.client;

import com.minibank.dto.client.request.RegisterClientRequest;

public interface PostClientValidator {

    void validateRequest(RegisterClientRequest request);
}
