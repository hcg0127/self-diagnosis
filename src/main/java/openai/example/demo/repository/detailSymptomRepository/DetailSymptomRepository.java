package openai.example.demo.repository.detailSymptomRepository;

import openai.example.demo.domain.DetailSymptom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetailSymptomRepository extends JpaRepository<DetailSymptom, Long> {

    List<DetailSymptom> findTop5ByOrderBySearchCountDesc();
}
