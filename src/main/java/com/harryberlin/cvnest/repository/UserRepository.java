package com.harryberlin.cvnest.repository;

import com.harryberlin.cvnest.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);
    User findByRefreshTokenAndEmail(String refreshToken, String email);
}
