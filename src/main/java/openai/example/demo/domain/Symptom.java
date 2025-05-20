package openai.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;
import openai.example.demo.domain.common.BaseEntity;
import openai.example.demo.domain.mapping.SymptomSearch;
import openai.example.demo.domain.mapping.SymptomSystem;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@DynamicInsert
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Symptom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String koName;

    private String enName;

    private String koDescription;

    private String enDescription;

    private Long searchCount;

    @OneToMany(mappedBy = "symptom", cascade = CascadeType.ALL)
    @Builder.Default
    private List<SymptomSearch> symptomSearchList = new ArrayList<>();

    @OneToMany(mappedBy = "symptom", cascade = CascadeType.ALL)
    @Builder.Default
    private List<SymptomSystem> symptomSystemList = new ArrayList<>();
}
