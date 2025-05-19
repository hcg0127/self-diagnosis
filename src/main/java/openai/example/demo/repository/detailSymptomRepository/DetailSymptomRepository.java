package openai.example.demo.repository.detailSymptomRepository;

import openai.example.demo.domain.DetailSymptom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetailSymptomRepository extends JpaRepository<DetailSymptom, Long> {
}
