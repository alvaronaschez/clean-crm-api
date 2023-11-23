package com.github.alvaronaschez.crm.infrastructure;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.github.alvaronaschez.crm.domain.UserRole;

@Repository
public interface RoleDao extends CrudRepository<RoleEntity, UserRole> {
}
