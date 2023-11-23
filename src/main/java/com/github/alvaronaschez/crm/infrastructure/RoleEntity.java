package com.github.alvaronaschez.crm.infrastructure;

import java.io.Serializable;
import java.util.Set;

// import com.fasterxml.jackson.annotation.JsonValue;
import com.github.alvaronaschez.crm.domain.UserRole;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@NoArgsConstructor(access = AccessLevel.PRIVATE) // required by JPA
@Getter
public class RoleEntity implements Serializable {
    @Id
    @Enumerated(EnumType.STRING)
    // @JsonValue
    private UserRole role;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    private Set<UserEntity> users;

    public RoleEntity(UserRole role) {
        this.role = role;
    }

    public UserRole toDomain() {
        return this.getRole();
    }

}
