package openai.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;
import openai.example.demo.domain.mapping.SymptomSystem;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BodySystem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String koName;

    private String enName;

    @OneToMany(mappedBy = "bodySystem", cascade = CascadeType.ALL)
    @Builder.Default
    private List<SymptomSystem> symptomSystemList = new ArrayList<>();
}
