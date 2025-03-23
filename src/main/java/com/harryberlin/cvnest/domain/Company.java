package com.harryberlin.cvnest.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.harryberlin.cvnest.util.constant.IndustryEnum;
import com.harryberlin.cvnest.util.listener.CompanyEntityListener;
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
@EntityListeners(CompanyEntityListener.class)
@Entity(name = "companies")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String name;
    String address;
    String website;
    String avatar;
    String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    IndustryEnum industry;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    @JsonIgnore
    List<Job> jobs;
}
