package openai.example.demo.domain.enums;

public enum BodySystem {
    NERVOUS("신경계"),           // 신경계
    CARDIOVASCULAR("심혈관계"),    // 심혈관계
    RESPIRATORY("호흡기계"),       // 호흡기계
    DIGESTIVE("소화기계"),         // 소화기계
    UROGENITAL("비뇨생식기계"),        // 비뇨생식기계
    MUSCULOSKELETAL("근골격계"),   // 근골격계
    OPHTHALMIC("안과계"),        // 안과계
    OTOLARYNGOLOGY("이비인후계"),   // 이비인후계
    DERMATOLOGY("피부과계"),      // 피부과계
    PSYCHIATRIC("정신과"),       // 정신과
    SYSTEMIC("전신 증상");          // 전신 증상

    private final String koName;

    BodySystem(String koName) {
        this.koName = koName;
    }

    public String getKoName() {
        return koName;
    }
}
