package com.github.alvaronaschez.crm.api;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.github.alvaronaschez.crm.application.CustomerService;
import com.github.alvaronaschez.crm.application.UserService;
import com.github.alvaronaschez.crm.application.CustomerService.CustomerAlreadyExistsException;
import com.github.alvaronaschez.crm.application.CustomerService.CustomerNotFoundException;
import com.github.alvaronaschez.crm.application.CustomerService.UploadFailureException;
import com.github.alvaronaschez.crm.application.dto.CustomerInDTO;
import com.github.alvaronaschez.crm.application.dto.CustomerOutDTO;
import com.github.alvaronaschez.crm.configuration.security.SecurityUser;
import com.github.alvaronaschez.crm.domain.Customer;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    private final UserService userService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CustomerOutDTO create(@RequestBody CustomerInDTO customer, @AuthenticationPrincipal SecurityUser user) {
        var creator = userService.getActiveUserByUsername(user.getUsername()).get();
        Customer c = customer.toDomain(creator);
        try {
            customerService.createCustomer(c);
        } catch (CustomerAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        return CustomerOutDTO.fromDomain(c);
    }

    @GetMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public CustomerOutDTO getById(@PathVariable UUID id) {
        Optional<Customer> customer = customerService.getById(id);
        if (customer.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return CustomerOutDTO.fromDomain(customer.get());
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<CustomerOutDTO> list() {
        return customerService.getCustomers().stream()
                .map(c -> CustomerOutDTO.fromDomain(c))
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id, @AuthenticationPrincipal SecurityUser user) {
        var deleter = userService.getActiveUserByUsername(user.getUsername()).get();
        try {
            customerService.deleteCustomer(id, deleter);
        } catch (CustomerNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping
    public CustomerOutDTO update(CustomerInDTO c, @AuthenticationPrincipal SecurityUser user) {
        var updater = userService.getActiveUserByUsername(user.getUsername()).get();
        Customer customer;
        try {
            customer = customerService.updateCustomer(c.toDomain(updater));
        } catch (CustomerNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return CustomerOutDTO.fromDomain(customer);
    }

    @PostMapping("/{id}/upload-photo")
    public ResponseEntity<String> uploadPhoto(@PathVariable UUID id, @RequestParam("file") MultipartFile file) {
        var customer = customerService.getById(id);
        if (customer.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        String fileUri;
        try {
            fileUri = customerService.uploadPhoto(id, file);
        } catch (UploadFailureException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        try {
            customerService.updateCustomer(
                    customer.get().withPhoto(Optional.of(fileUri)));
        } catch (CustomerNotFoundException e) {
            // unreachable
        }
        return ResponseEntity.ok(fileUri);
    }

}
