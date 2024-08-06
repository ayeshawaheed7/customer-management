package com.ayeshascode.customer.repository;

import com.ayeshascode.customer.model.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, String> {
   boolean existsByKey(String key);
}
