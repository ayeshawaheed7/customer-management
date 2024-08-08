package com.ayeshascode.fraud.service;

import com.ayeshascode.fraud.model.FraudCheckHistory;
import com.ayeshascode.fraud.repository.FraudCheckHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FraudCheckHistoryService {

    private final FraudCheckHistoryRepository fraudCheckHistoryRepository;

    @Transactional
    public boolean saveAndCheckFraud(UUID customerId) {
        FraudCheckHistory fraudCheckHistory = new FraudCheckHistory(
                UUID.randomUUID(),
                customerId,
                false,
                LocalDateTime.now());

        fraudCheckHistoryRepository.save(fraudCheckHistory);

        return fraudCheckHistory.getIsFraudster();
    }
}
