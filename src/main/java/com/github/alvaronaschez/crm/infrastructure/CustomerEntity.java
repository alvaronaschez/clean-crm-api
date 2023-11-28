package com.github.alvaronaschez.crm.infrastructure;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.github.alvaronaschez.crm.domain.Customer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customers")
@NoArgsConstructor(access = AccessLevel.PRIVATE) // required by JPA
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@SQLDelete(sql = "UPDATE customers SET active = false WHERE id=?")
@Where(clause = "active=true")
public class CustomerEntity {
    @Id
    private UUID id;

    @Column(unique = true)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String photo;

    @Column(name = "last_modified_at")
    private Instant lastModifiedAt;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private UserEntity lastModifiedBy;

    private boolean active;

    public static CustomerEntity fromDomain(Customer c) {
        var modifiedBy = UserEntity.fromDomain(c.getLastModifiedBy());
        return new CustomerEntity(
                c.getId(),
                c.getEmail(),
                c.getFirstName(),
                c.getLastName(),
                c.getPhoto().orElse(null),
                c.getLastModifiedAt(),
                modifiedBy,
                c.isActive());
    }

    public Customer toDomain() {
        Optional<String> photo = Optional.ofNullable(this.photo);
        return new Customer(id, email, firstName, lastName, photo, lastModifiedBy.toDomain(), lastModifiedAt, active);
    }
}
