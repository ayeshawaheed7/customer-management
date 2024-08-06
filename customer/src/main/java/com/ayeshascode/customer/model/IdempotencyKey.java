package com.ayeshascode.customer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class IdempotencyKey {

    @Id
    @Column(name = "idempotency_key_id")
    private String key;
}
