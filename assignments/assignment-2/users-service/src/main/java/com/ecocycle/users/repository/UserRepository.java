package com.ecocycle.users.repository;

import com.ecocycle.users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
