package com.github.alvaronaschez.crm.application;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.github.alvaronaschez.crm.domain.Customer;
import com.github.alvaronaschez.crm.domain.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepositoryInterface customerRepository;
    private final CustomerStorageInterface customerStorage;

    public void createCustomer(Customer customer) throws CustomerAlreadyExistsException {
        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            throw new CustomerAlreadyExistsException();
        }
        customerRepository.save(customer);
    }

    public Optional<Customer> getById(UUID id) {
        return customerRepository.findById(id);
    }

    public Optional<Customer> getByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    public void deleteCustomer(UUID id, User deleter) throws CustomerNotFoundException {
        var customer = customerRepository.findById(id);
        if (customer.isEmpty()) {
            throw new CustomerNotFoundException();
        }
        customerRepository.save(customer.get().withLastModifiedBy(deleter));
        customerRepository.deleteById(id);
    }

    public Customer updateCustomer(Customer customer) throws CustomerNotFoundException {
        var existingCustomer = customerRepository.findByEmail(customer.getEmail());
        if (existingCustomer.isEmpty()) {
            throw new CustomerNotFoundException();
        }
        var id = existingCustomer.get().getId();
        var updatedCustomer = customer.withId(id);
        // maintain existing photo (if exists)
        var photo = existingCustomer.get().getPhoto();
        if (photo.isPresent()) {
            updatedCustomer = updatedCustomer.withPhoto(photo);
        }
        customerRepository.save(updatedCustomer);
        return updatedCustomer;
    }

    public String uploadPhoto(UUID id, MultipartFile file) throws UploadFailureException {
        return customerStorage.uploadFile(file);
    }

    public static class CustomerAlreadyExistsException extends Exception {
    }

    public static class CustomerNotFoundException extends Exception {
    }

    public static class UploadFailureException extends Exception {
    }

}
