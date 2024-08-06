package com.ayeshascode.customer.service;

import com.ayeshascode.customer.model.IdempotencyKey;
import com.ayeshascode.customer.repository.IdempotencyKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IdempotencyKeyService {
    private final IdempotencyKeyRepository idempotencyKeyRepository;

    public boolean hasBeenAlreadyProcessed(String xIdempotencyKey) {
        return idempotencyKeyRepository.existsByKey(xIdempotencyKey);
    }

    public void save(String xIdempotencyKey) {
        idempotencyKeyRepository.save(new IdempotencyKey(xIdempotencyKey));
    }
}
