package com.github.alvaronaschez.crm.application;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.github.alvaronaschez.crm.domain.Customer;

public interface CustomerRepositoryInterface {
    public Optional<Customer> findById(UUID id);

    public Optional<Customer> findByEmail(String email);

    public List<Customer> findAll();

    public void save(Customer Customer);

    public void deleteById(UUID id);
}
