package com.hoquangnam45.pharmacy.repo;

import com.hoquangnam45.pharmacy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {
    User findUserByEmail(String email);
    User findUserByUsername(String username);
}
