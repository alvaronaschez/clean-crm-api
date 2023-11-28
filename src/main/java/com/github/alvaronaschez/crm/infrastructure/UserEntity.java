package com.github.alvaronaschez.crm.infrastructure;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.hibernate.annotations.SQLDelete;

import com.github.alvaronaschez.crm.domain.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PRIVATE) // required by JPA
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@SQLDelete(sql = "UPDATE users SET active = false WHERE id=?")
public class UserEntity implements Serializable {
    @Id
    private UUID id;
    private String username;
    private String password;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "username"), inverseJoinColumns = @JoinColumn(name = "role"))
    private Set<RoleEntity> roles;

    @Column(name = "active")
    private boolean active;

    public static UserEntity fromDomain(User user) {
        var roles = user.getRoles().stream().map(role -> new RoleEntity(role)).collect(Collectors.toSet());
        return new UserEntity(user.getId(), user.getUsername(), user.getPassword(), roles, user.isActive());
    }

    public User toDomain() {
        var roles = this.getRoles().stream().map(role -> role.toDomain()).collect(Collectors.toSet());
        var user = new User(this.getId(), this.getUsername(), this.getPassword(), roles, this.isActive());
        return user;
    }
}
