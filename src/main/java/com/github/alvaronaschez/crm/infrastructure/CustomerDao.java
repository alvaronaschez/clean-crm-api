package com.github.alvaronaschez.crm.infrastructure;

import java.util.Optional;
import java.util.UUID;

// import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerDao extends CrudRepository<CustomerEntity, UUID> {
    // @Query("select e from #{#entityName} e where e.email = ?1 and e.active =
    // true")
    public Optional<CustomerEntity> findByEmail(String email);
}