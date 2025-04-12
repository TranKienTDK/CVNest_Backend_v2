package com.harryberlin.cvnest.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.harryberlin.cvnest.util.constant.LevelLanguageEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "languages")
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String language;

    @Enumerated(EnumType.STRING)
    LevelLanguageEnum level;

    @ManyToOne
    @JoinColumn(name = "cv_id")
    @JsonBackReference
    CV cv;
}
