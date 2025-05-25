package com.harryberlin.cvnest.repository;

import com.harryberlin.cvnest.domain.Company;
import com.harryberlin.cvnest.domain.User;
import com.harryberlin.cvnest.util.constant.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);
    User findByRefreshTokenAndEmail(String refreshToken, String email);
    List<User> findByCompanyAndRole(Company company, RoleEnum role);
}
