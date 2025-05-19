package openai.example.demo.domain.mapping;

import jakarta.persistence.*;
import lombok.*;
import openai.example.demo.domain.DetailSymptom;
import openai.example.demo.domain.Symptom;
import openai.example.demo.domain.common.BaseEntity;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SymptomSearch extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "symptom_id")
    private Symptom symptom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detail_symptom_id")
    private DetailSymptom detailSymptom;
}
