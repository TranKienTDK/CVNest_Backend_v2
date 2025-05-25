package com.harryberlin.cvnest.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.harryberlin.cvnest.util.constant.RoleEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
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

    String avatar;
    String phone;
    LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    RoleEnum role;

    @Column(columnDefinition = "MEDIUMTEXT")
    String refreshToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    List<CV> cvs;

    @ManyToOne
    @JoinColumn(name = "company_id")
    @JsonIgnore
    Company company;

    public void deleteCV(String id) {
        var cv = this.cvs.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("CV not found"));
        cv.setUser(null);
        this.cvs.remove(cv);
    }
}
