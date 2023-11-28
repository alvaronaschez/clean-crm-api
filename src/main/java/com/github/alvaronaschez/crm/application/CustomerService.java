package com.github.alvaronaschez.crm.application;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.github.alvaronaschez.crm.application.dto.CreateCustomerDTO;
import com.github.alvaronaschez.crm.application.dto.CustomerOutDTO;
import com.github.alvaronaschez.crm.application.dto.UpdateCustomerDTO;
import com.github.alvaronaschez.crm.domain.Customer;
import com.github.alvaronaschez.crm.domain.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepositoryInterface customerRepository;
    private final UserRepositoryInterface userRepository;
    private final CustomerStorageInterface customerStorage;

    public CustomerOutDTO createCustomer(CreateCustomerDTO customer, String creatorUsername)
            throws CustomerAlreadyExistsException {
        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            throw new CustomerAlreadyExistsException();
        }
        User creator = userRepository.findActiveUserByUsername(creatorUsername).get();
        Customer newCustomer = customer.toDomain(creator);
        customerRepository.save(newCustomer);
        return CustomerOutDTO.fromDomain(newCustomer);
    }

    public CustomerOutDTO getById(UUID id) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(id).orElseThrow(CustomerNotFoundException::new);
        return CustomerOutDTO.fromDomain(customer);
    }

    public CustomerOutDTO getByEmail(String email) throws CustomerNotFoundException {
        Customer customer = customerRepository.findByEmail(email).orElseThrow(CustomerNotFoundException::new);
        return CustomerOutDTO.fromDomain(customer);
    }

    public List<CustomerOutDTO> getCustomers() {
        return customerRepository.findAll().stream()
                .map(c -> CustomerOutDTO.fromDomain(c))
                .collect(Collectors.toList());
    }

    public void deleteCustomer(UUID id, User deleter) throws CustomerNotFoundException {
        var customer = customerRepository.findById(id);
        if (customer.isEmpty()) {
            throw new CustomerNotFoundException();
        }
        customerRepository.save(customer.get().withLastModifiedBy(deleter));
        customerRepository.deleteById(id);
    }

    public CustomerOutDTO updateCustomer(UUID id, UpdateCustomerDTO customer, String creatorUsername)
            throws CustomerNotFoundException {
        User modifier = userRepository.findActiveUserByUsername(creatorUsername).get();
        Customer existingCustomer;
        try {
            existingCustomer = customerRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new CustomerNotFoundException();
        }

        var updatedCustomer = new Customer(
                id,
                customer.getEmail(),
                customer.getFirstName(),
                customer.getLastName(),
                existingCustomer.getPhoto(),
                modifier,
                Instant.now(),
                true);
        customerRepository.save(updatedCustomer);
        return CustomerOutDTO.fromDomain(updatedCustomer);
    }

    public String updatePhoto(UUID id, MultipartFile file, String modifierUsername)
            throws CustomerNotFoundException, UploadFailureException, UserNotFoundException {
        Customer existingCustomer;
        try {
            existingCustomer = customerRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new CustomerNotFoundException();
        }

        User modifier;
        try {
            modifier = userRepository.findActiveUserByUsername(modifierUsername).get();
        } catch (NoSuchElementException e) {
            throw new UserNotFoundException();
        }

        String photoUrl = customerStorage.uploadFile(file);

        Customer updatedCustomer = existingCustomer
                .withPhoto(Optional.of(photoUrl))
                .withLastModifiedBy(modifier);
        customerRepository.save(updatedCustomer);
        return photoUrl;
    }

    public static class CustomerAlreadyExistsException extends Exception {
    }

    public static class CustomerNotFoundException extends Exception {
    }

    public static class UploadFailureException extends Exception {
    }

    public static class UserNotFoundException extends Exception {
    }

}
