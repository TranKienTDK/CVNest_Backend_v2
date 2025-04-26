package com.harryberlin.cvnest.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.harryberlin.cvnest.util.constant.GenderEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "infos")
public class Info {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String fullName;
    String address;
    String avatar;
    LocalDate dob;
    String email;

    @Enumerated(EnumType.STRING)
    GenderEnum gender;
    String phone;
    String position;

    String city;

    String linkedin;
    String github;

    @OneToOne
    @JoinColumn(name = "cv_id")
    @JsonIgnore
    CV cv;

}
