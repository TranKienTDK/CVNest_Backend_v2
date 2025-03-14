package com.harryberlin.cvnest.service;

import com.harryberlin.cvnest.domain.User;
import com.harryberlin.cvnest.repository.UserRepository;
import com.harryberlin.cvnest.util.constant.Error;
import com.harryberlin.cvnest.util.constant.RoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("userDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = this.userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(Error.USER_NOT_FOUND);
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user.getRole().equals(RoleEnum.USER)) {
            authorities.add(new SimpleGrantedAuthority(RoleEnum.USER.toString()));
        } else if (user.getRole().equals(RoleEnum.HR)) {
            authorities.add(new SimpleGrantedAuthority(RoleEnum.HR.toString()));
        } else {
            authorities.add(new SimpleGrantedAuthority(RoleEnum.ADMIN.toString()));
        }
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
