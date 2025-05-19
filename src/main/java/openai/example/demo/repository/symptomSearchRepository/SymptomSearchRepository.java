package openai.example.demo.repository.symptomSearchRepository;

import openai.example.demo.domain.mapping.SymptomSearch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SymptomSearchRepository extends JpaRepository<SymptomSearch, Long> {
}
