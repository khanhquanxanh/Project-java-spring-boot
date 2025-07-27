package com.Project1.demo.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.Project1.demo.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {

}