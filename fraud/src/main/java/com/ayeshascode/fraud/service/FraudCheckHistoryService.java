package com.ayeshascode.fraud.service;

import com.ayeshascode.fraud.model.FraudCheckHistory;
import com.ayeshascode.fraud.repository.FraudCheckHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class FraudCheckHistoryService {

    @Autowired
    private FraudCheckHistoryRepository fraudCheckHistoryRepository;

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
