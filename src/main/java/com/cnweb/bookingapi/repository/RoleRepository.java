package com.cnweb.bookingapi.repository;

import com.cnweb.bookingapi.model.ERole;
import com.cnweb.bookingapi.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(ERole name);
}