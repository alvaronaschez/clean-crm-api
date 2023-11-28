package com.github.alvaronaschez.crm.api;

import java.util.List;
import java.util.UUID;

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
import com.github.alvaronaschez.crm.application.CustomerService.UserNotFoundException;
import com.github.alvaronaschez.crm.application.dto.CreateCustomerDTO;
import com.github.alvaronaschez.crm.application.dto.CustomerOutDTO;
import com.github.alvaronaschez.crm.application.dto.UpdateCustomerDTO;
import com.github.alvaronaschez.crm.configuration.security.SecurityUser;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    private final UserService userService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CustomerOutDTO createCustomer(@RequestBody CreateCustomerDTO customer,
            @AuthenticationPrincipal SecurityUser user) {
        try {
            return customerService.createCustomer(customer, user.getUsername());
        } catch (CustomerAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public CustomerOutDTO getCustomerById(@PathVariable UUID id) {
        try {
            return customerService.getById(id);
        } catch (CustomerNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<CustomerOutDTO> list() {
        return customerService.getCustomers();
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

    @PutMapping("/{id}")
    public CustomerOutDTO update(@PathVariable UUID id, @RequestBody UpdateCustomerDTO c,
            @AuthenticationPrincipal SecurityUser user) {
        try {
            return customerService.updateCustomer(id, c, user.getUsername());
        } catch (CustomerNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{id}/upload-photo")
    public ResponseEntity<String> uploadPhoto(@PathVariable UUID id, @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal SecurityUser user) {
        try {
            String fileUri = customerService.updatePhoto(id, file, user.getUsername());
            return ResponseEntity.ok(fileUri);
        } catch (CustomerNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } catch (UploadFailureException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (UserNotFoundException e) {
            // unreachable
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
