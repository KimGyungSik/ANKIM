package shoppingmall.ankim.domain.member.entity;

import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import shoppingmall.ankim.domain.member.repository.MemberRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberTest {

    @Autowired
    private MemberRepository memberRepository;

    /*
    * 에러가 발생하는 경우를 테스트한다.
    * 1. id
    *   1.1. 길이 초과하는 경우
    *   1.2. null 값인 경우
    * 2. pwd
    *   2.1. 길이 초과하는 경우
    *   2.2. null 값인 경우
    * 3. 이름
    *   3.1. 길이 초과하는 경우
    *   3.2. null 값인 경우
    * 4. 휴대전화번호
    *   4.1 길이 초과하는 경우
    *   4.2. null 값인 경우
    * 5. 생년월일
    *   5.1. null 값인 경우
    * 6. 성별
    *   6.1. 길이 초과하는 경우
    *   6.2. null 값인 경우
    * 7. 회원 등급
    *   7.1. null 값인 경우
    * 8. 회원 상태
    *   8.1. null 값인 경우
    * 9. 여러 값의 형식을 잘 못 입력했을 때
    * */

    @Test
    @DisplayName("아이디 길이가 50자를 초과할 경우 예외가 발생해야 한다.")
    void invalidIdLength() {
        // given
        String invalidId = "a".repeat(51);  // 길이가 51인 문자열

        // when
        Member member = Member.builder()
//                .uuid(UUID.randomUUID())
                .id(invalidId)
                .pwd("validPwd123!")
                .name("홍길동")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .gender("M")
                .joinDate(LocalDateTime.now())
                .status(MemberStatus.ACTIVE)
                .build();

        // then
        assertThrows(DataIntegrityViolationException.class, () -> memberRepository.saveAndFlush(member));
    }

    @Test
    @DisplayName("아이디가 null인 경우 예외가 발생해야 한다.")
    void nullId() {
        // given
        String nullId = null;

        // when
        Member member = Member.builder()
//                .uuid(UUID.randomUUID())
                .id(nullId)
                .pwd("validPwd123!")
                .name("홍길동")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .gender("M")
                .joinDate(LocalDateTime.now())
                .status(MemberStatus.ACTIVE)
                .build();

        // then
        assertThrows(DataIntegrityViolationException.class, () -> memberRepository.saveAndFlush(member));
    }

    @Test
    @DisplayName("비밀번호 길이가 200자를 초과할 경우 예외가 발생해야 한다.")
    void invalidPwdLength() {
        // given
        String invalidPwd = "a".repeat(201); // 길이가 201인 문자열

        // when
        Member member = Member.builder()
//                .uuid(UUID.randomUUID())
                .id("test@example.com")
                .pwd(invalidPwd)
                .name("홍길동")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .gender("M")
                .joinDate(LocalDateTime.now())
                .status(MemberStatus.ACTIVE)
                .build();

        // then
        assertThrows(DataIntegrityViolationException.class, () -> memberRepository.saveAndFlush(member));
    }

    @Test
    @DisplayName("비밀번호가 null인 경우 예외가 발생해야 한다.")
    void nullPwd() {
        // given
        String nullPwd = null;

        // when
        Member member = Member.builder()
//                .uuid(UUID.randomUUID())
                .id("test@example.com")
                .pwd(nullPwd)
                .name("홍길동")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .gender("M")
                .joinDate(LocalDateTime.now())
                .status(MemberStatus.ACTIVE)
                .build();

        // then
        assertThrows(DataIntegrityViolationException.class, () -> memberRepository.saveAndFlush(member));
    }

    @Test
    @DisplayName("이름 길이가 20자를 초과할 경우 예외가 발생해야 한다")
    void invalidNameLength() {
        // given
        String invalidName = "a".repeat(21); // 길이가 21인 문자열

        // when
        Member member = Member.builder()
//                .uuid(UUID.randomUUID())
                .id("test@example.com")
                .pwd("validPwd123!")
                .name(invalidName)
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .gender("M")
                .joinDate(LocalDateTime.now())
                .status(MemberStatus.ACTIVE)
                .build();

        // then
        assertThrows(DataIntegrityViolationException.class, () -> memberRepository.saveAndFlush(member));
    }

    @Test
    @DisplayName("이름이 null인 경우 예외가 발생해야 한다")
    void nullName() {
        // given
        String nullName = null;

        // when
        Member member = Member.builder()
//                .uuid(UUID.randomUUID())
                .id("test@example.com")
                .pwd("validPwd123!")
                .name(nullName)
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .gender("M")
                .joinDate(LocalDateTime.now())
                .status(MemberStatus.ACTIVE)
                .build();

        // then
        assertThrows(DataIntegrityViolationException.class, () -> memberRepository.saveAndFlush(member));
    }

    @Test
    @DisplayName("전화번호 길이가 20자를 초과할 경우 예외가 발생해야 한다")
    void invalidPhoneNumLength() {
        // given
        String invalidPhoneNum = "0".repeat(21);

        // when
        Member member = Member.builder()
//                .uuid(UUID.randomUUID())
                .id("test@example.com")
                .pwd("validPwd123!")
                .name("홍길동")
                .phoneNum(invalidPhoneNum) // 길이가 21인 문자열
                .birth(LocalDate.of(1990, 1, 1))
                .gender("M")
                .joinDate(LocalDateTime.now())
                .status(MemberStatus.ACTIVE)
                .build();

        // then
        assertThrows(DataIntegrityViolationException.class, () -> memberRepository.saveAndFlush(member));
    }

    @Test
    @DisplayName("전화번호 길이가 20자를 초과할 경우 예외가 발생해야 한다")
    void nullPhoneNum() {
        // given
        String nullPhoneNum = null;

        // when
        Member member = Member.builder()
//                .uuid(UUID.randomUUID())
                .id("test@example.com")
                .pwd("validPwd123!")
                .name("홍길동")
                .phoneNum(nullPhoneNum)
                .birth(LocalDate.of(1990, 1, 1))
                .gender("M")
                .joinDate(LocalDateTime.now())
                .status(MemberStatus.ACTIVE)
                .build();

        // then
        assertThrows(DataIntegrityViolationException.class, () -> memberRepository.saveAndFlush(member));
    }

    @Test
    @DisplayName("성별이 null인 경우 예외가 발생해야 한다")
    void invalidGenderLength() {
        // given
        String nullGender = null;

        // when
        Member member = Member.builder()
//                .uuid(UUID.randomUUID())
                .id("test@example.com")
                .pwd("validPwd123!")
                .name("홍길동")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .gender(nullGender)
                .joinDate(LocalDateTime.now())
                .status(MemberStatus.ACTIVE)
                .build();

        // then
        assertThrows(DataIntegrityViolationException.class, () -> memberRepository.saveAndFlush(member));
    }

    @Test
    @DisplayName("가입일이 null인 경우 예외가 발생해야 한다")
    void nullJoinDate() {
        // given
        LocalDateTime nullJoinDate = null;

        // when
        Member member = Member.builder()
//                .uuid(UUID.randomUUID())
                .id("test@example.com")
                .pwd("validPwd123!")
                .name("홍길동")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .gender("M")
                .joinDate(nullJoinDate)
                .status(MemberStatus.ACTIVE)
                .build();

        // then
        assertThrows(DataIntegrityViolationException.class, () -> memberRepository.saveAndFlush(member));
    }

    @Test
    @DisplayName("회원 상태가 null인 경우 예외가 발생해야 한다")
    void nullMemberStatus() {
        // given
        MemberStatus nullMemberStatus = null;

        // when
        Member member = Member.builder()
//                .uuid(UUID.randomUUID())
                .id("test@example.com")
                .pwd("validPwd123!")
                .name("홍길동")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .gender("M")
                .joinDate(LocalDateTime.now())
                .status(nullMemberStatus)
                .build();

        // then
        assertThrows(DataIntegrityViolationException.class, () -> memberRepository.saveAndFlush(member));
    }
}