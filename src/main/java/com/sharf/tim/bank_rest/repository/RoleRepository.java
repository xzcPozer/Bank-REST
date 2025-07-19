package com.sharf.tim.bank_rest.repository;

import com.sharf.tim.bank_rest.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);
}
