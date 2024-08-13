package com.ayeshascode.notification.repository;

import com.ayeshascode.notification.model.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, String> {
    boolean existsByKey(String key);
}

