package com.harryberlin.cvnest.domain;

import com.harryberlin.cvnest.util.constant.RoleEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String username;
    String email;
    String password;

    @Enumerated(EnumType.STRING)
    RoleEnum role;

    @Column(columnDefinition = "MEDIUMTEXT")
    String refreshToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<CV> cvs;
}
