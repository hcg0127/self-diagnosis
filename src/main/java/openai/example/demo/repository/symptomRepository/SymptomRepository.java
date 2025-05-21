package openai.example.demo.repository.symptomRepository;

import openai.example.demo.domain.Symptom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SymptomRepository extends JpaRepository<Symptom, Long> {

    boolean existsSymptomByKoName(String koName);

    boolean existsSymptomByEnName(String enName);

    List<Symptom> findTop5ByOrderBySearchCountDesc();

    @Query("SELECT s FROM Symptom AS s JOIN SymptomSearch AS ss ON s = ss.symptom JOIN DetailSymptom AS ds ON ds = ss.detailSymptom WHERE ds.id = :detailSymptomId")
    List<Symptom> findSymptomsWithDetailSymptom(@Param("detailSymptomId") Long detailSymptomId);
}
