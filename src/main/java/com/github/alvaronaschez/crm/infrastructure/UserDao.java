package com.github.alvaronaschez.crm.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends CrudRepository<UserEntity, UUID> {
    @Query("select e from #{#entityName} e where e.username = ?1 and e.is_active = true")
    Optional<UserEntity> findByUsername(String username);

    public List<UserEntity> findByIsActive(boolean active);

    // public List<UserEntity> findByRoles(Set<RoleEntity> roles);

}
