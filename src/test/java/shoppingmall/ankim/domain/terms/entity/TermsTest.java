package shoppingmall.ankim.domain.terms.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;
import shoppingmall.ankim.domain.terms.repository.TermsRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
class TermsTest {

    @Autowired
    TermsRepository termsRepository;

    @Test
    @DisplayName("약관명 길이가 200자를 초과할 경우 예외가 발생해야 한다.")
    void invalidNameLength() {
        Terms terms = Terms.builder()
                .name("a".repeat(201))  // 길이 초과
                .category(TermsCategory.JOIN)
                .contents("약관 내용입니다.")
                .termsYn("Y")
//                .termsVersion("v1")
                .termsVersion(1)
                .level(1)
                .activeYn("Y")
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> termsRepository.saveAndFlush(terms));
    }

    @Test
    @DisplayName("약관명이 null일 경우 예외가 발생해야 한다.")
    void nullName() {
        Terms terms = Terms.builder()
                .name(null)  // 필수 값이므로 null
                .category(TermsCategory.JOIN)
                .contents("약관 내용입니다.")
                .termsYn("Y")
//                .termsVersion("v1")
                .termsVersion(1)
                .level(1)
                .activeYn("Y")
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> termsRepository.saveAndFlush(terms));
    }

//    @Test
//    @DisplayName("약관 버전 길이가 10자를 초과할 경우 예외가 발생해야 한다.")
//    void invalidTermsVersionLength() {
//        Terms terms = Terms.builder()
//                .name("이용약관")
//                .category(TermsCategory.JOIN)
//                .contents("약관 내용입니다.")
//                .termsYn("Y")
//                .termsVersion("v".repeat(11))  // 길이 초과
//                .level(1)
//                .activeYn("Y")
//                .build();
//
//        assertThrows(DataIntegrityViolationException.class, () -> termsRepository.saveAndFlush(terms));
//    }

    @Test
    @DisplayName("약관 버전이 null일 경우 예외가 발생해야 한다.")
    void nullTermsVersion() {
        Terms terms = Terms.builder()
                .name("이용약관")
                .category(TermsCategory.JOIN)
                .contents("약관 내용입니다.")
                .termsYn("Y")
                .termsVersion(null)  // 필수 값이므로 null
                .level(1)
                .activeYn("Y")
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> termsRepository.saveAndFlush(terms));
    }

    @Test
    @DisplayName("필수 동의 여부가 null일 경우 예외가 발생해야 한다.")
    void nullTermsYn() {
        Terms terms = Terms.builder()
                .name("이용약관")
                .category(TermsCategory.JOIN)
                .contents("약관 내용입니다.")
                .termsYn(null)  // 필수 값이므로 null
//                .termsVersion("v1")
                .termsVersion(1)
                .level(1)
                .activeYn("Y")
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> termsRepository.saveAndFlush(terms));
    }

    @Test
    @DisplayName("활성화 상태가 null일 경우 예외가 발생해야 한다.")
    void nullActiveYn() {
        Terms terms = Terms.builder()
                .name("이용약관")
                .category(TermsCategory.JOIN)
                .contents("약관 내용입니다.")
                .termsYn("Y")
//                .termsVersion("v1")
                .termsVersion(1)
                .level(1)
                .activeYn(null)  // 필수 값이므로 null
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> termsRepository.saveAndFlush(terms));
    }

    @Test
    @DisplayName("약관 레벨이 null일 경우 예외가 발생해야 한다.")
    void nullLevel() {
        Terms terms = Terms.builder()
                .name("이용약관")
                .category(TermsCategory.JOIN)
                .contents("약관 내용입니다.")
                .termsYn("Y")
//                .termsVersion("v1")
                .termsVersion(1)
                .level(null)  // 필수 값이므로 null
                .activeYn("Y")
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> termsRepository.saveAndFlush(terms));
    }
}