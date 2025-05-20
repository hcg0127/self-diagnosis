package openai.example.demo.domain.mapping;

import jakarta.persistence.*;
import lombok.*;
import openai.example.demo.domain.BodySystem;
import openai.example.demo.domain.Symptom;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SymptomSystem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "body_system_id")
    private BodySystem bodySystem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "symptom_id")
    private Symptom symptom;
}
