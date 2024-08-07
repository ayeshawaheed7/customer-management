package com.ayeshascode.fraud.service;

import com.ayeshascode.fraud.model.FraudCheckHistory;
import com.ayeshascode.fraud.repository.FraudCheckHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FraudCheckHistoryServiceTest {

    @Mock
    private FraudCheckHistoryRepository fraudCheckHistoryRepository;

    @InjectMocks
    private FraudCheckHistoryService underTest;

    @Nested
    @DisplayName("saveAndCheckFraud")
    class saveAndCheckFraud {

        @Nested
        @DisplayName("customer Id is provided")
        class customerIdIsProvided {


            @Test
            @DisplayName("then should return customer fraud check")
            void shouldReturnCustomerFraudCheck() {
                UUID customerId = UUID.randomUUID();
                FraudCheckHistory fraudCheckHistory = new FraudCheckHistory(
                        UUID.randomUUID(),
                        customerId,
                        false,
                        LocalDateTime.now()
                );

                when(fraudCheckHistoryRepository.save(any())).thenReturn(fraudCheckHistory);

                underTest.saveAndCheckFraud(customerId);

                verify(fraudCheckHistoryRepository).save(argThat(cu ->
                        cu.getCustomerId().equals(fraudCheckHistory.getCustomerId()) &&
                                cu.getIsFraudster().equals(fraudCheckHistory.getIsFraudster())
                ));
            }
        }
    }
}