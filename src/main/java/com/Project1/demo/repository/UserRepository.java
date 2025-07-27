package com.Project1.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Project1.demo.model.User;

@Repository
public interface UserRepository extends  JpaRepository<User, Long>{
	User findByUsername(String username);
}
