package com.ayeshascode.customer.service;

import com.ayeshascode.customer.model.IdempotencyKey;
import com.ayeshascode.customer.repository.IdempotencyKeyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdempotencyKeyServiceTest {

    @Mock
    private IdempotencyKeyRepository idempotencyKeyRepository;

    @InjectMocks
    private IdempotencyKeyService underTest;

    private final String xIdempotencyKey = UUID.randomUUID().toString();

    @Nested
    @DisplayName("hasBeenAlreadyProcessed")
    class hasBeenAlreadyProcessed {

        @Nested
        @DisplayName("when entry already exists in DB")
        class EntryAlreadyExistsInDB {

            @Test
            @DisplayName("then returns true")
            void returnsTrue() {
                when(idempotencyKeyRepository.existsByKey(any())).thenReturn(true);

                boolean response = underTest.hasBeenAlreadyProcessed(xIdempotencyKey);

                assertThat(response).isTrue();

                verify(idempotencyKeyRepository).existsByKey(xIdempotencyKey);
            }
        }

        @Nested
        @DisplayName("when entry does NOT exist in DB")
        class EntryDoesNotExistInDB {

            @Test
            @DisplayName("then returns false")
            void returnsFalse() {
                when(idempotencyKeyRepository.existsByKey(any())).thenReturn(false);

                boolean response = underTest.hasBeenAlreadyProcessed(xIdempotencyKey);

                assertThat(response).isFalse();

                verify(idempotencyKeyRepository).existsByKey(xIdempotencyKey);
            }
        }
    }

    @Nested
    @DisplayName("save")
    class save {

        @Test
        @DisplayName("persists data in DB")
        void persistsDataInDB() {
            when(idempotencyKeyRepository.save(any())).thenReturn(any());

            underTest.save(xIdempotencyKey);

            verify(idempotencyKeyRepository).save(new IdempotencyKey(xIdempotencyKey));
        }
    }

    @Nested
    @DisplayName("generateKey")
    class generateKey {

        @Test
        @DisplayName("should return key")
        void shouldReturnKey() {
            String key = underTest.generateKey();

            assertDoesNotThrow(() -> UUID.fromString(key), "Generated key is not a valid UUID");}
    }
}