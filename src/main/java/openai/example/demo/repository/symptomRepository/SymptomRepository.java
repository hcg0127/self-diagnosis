package openai.example.demo.repository.symptomRepository;

import openai.example.demo.domain.Symptom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SymptomRepository extends JpaRepository<Symptom, Long> {
}
