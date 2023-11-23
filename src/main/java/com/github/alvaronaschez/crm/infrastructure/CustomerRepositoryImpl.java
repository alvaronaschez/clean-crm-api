package com.github.alvaronaschez.crm.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Component;

import com.github.alvaronaschez.crm.application.CustomerRepositoryInterface;
import com.github.alvaronaschez.crm.domain.Customer;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepositoryInterface {
    private final CustomerDao customerDao;

    @Override
    public Optional<Customer> findById(UUID id) {
        return customerDao.findById(id).map(c -> c.toDomain());
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return customerDao.findByEmail(email).map(c -> c.toDomain());
    }

    @Override
    public List<Customer> findAll() {
        var customers = customerDao.findAll();
        var stream = StreamSupport.stream(customers.spliterator(), false);
        return stream.map(c -> c.toDomain()).collect(Collectors.toList());
    }

    @Override
    public void save(Customer customer) {
        customerDao.save(CustomerEntity.fromDomain(customer));
    }

    @Override
    public void deleteById(UUID id) {
        customerDao.deleteById(id);
    }

}
