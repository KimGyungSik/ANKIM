package shoppingmall.ankim.domain.member.service.port;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.global.config.QuerydslConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
@Import(QuerydslConfig.class)
class MemberHistoryRepositoryTest {

    @Autowired
    EntityManager em;
    
    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("이메일 중복 검증 테스트_중복 이메일이 없는 경우")
    void notExistsByLoginIdTest() {
        // given
        Member member = Member.builder()
                .loginId("user123@ankim.com")
                .pwd("password")
//                .uuid(UUID.randomUUID())
                .name("user1")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .grade(50)
                .gender("M")
                .joinDate(LocalDateTime.now())
                .status(MemberStatus.ACTIVE)
                .build();

        em.persist(member);
        em.flush();
        em.clear();


        String email = "test@ankim.com";

        // when
        Boolean isExist = memberRepository.existsByLoginId(email);

        // then
        Assertions.assertThat(isExist).isFalse();
    }

    @Test
    @DisplayName("이메일 중복 검증 테스트_중복 이메일이 있는 경우")
    void existsByLoginIdTest() {
        // given
        Member member = Member.builder()
                .loginId("user123@ankim.com")
                .pwd("password")
//                .uuid(UUID.randomUUID())
                .name("user1")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .grade(50)
                .gender("M")
                .joinDate(LocalDateTime.now())
                .status(MemberStatus.ACTIVE)
                .build();

        em.persist(member);
        em.flush();
        em.clear();

        String email = "user123@ankim.com";

        // when
        Boolean isExist = memberRepository.existsByLoginId(email);

        // then
        Assertions.assertThat(isExist).isTrue();
    }

    @Test
    @DisplayName("회원 정보를 DB에 저장하고 조회할 수 있다.")
    void saveAndFindMember() {
        // given
        Member member = Member.builder()
                .loginId("test@example.com")
                .pwd("ValidPassword123!")
                .name("홍길동")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .gender("M")
                .joinDate(LocalDateTime.now())
                .grade(1)
                .status(MemberStatus.ACTIVE)
                .build();

        // when
        memberRepository.save(member);
        Member savedMember = memberRepository.findByLoginId(member.getLoginId());

        // then
        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getLoginId()).isEqualTo("test@example.com");
        assertThat(savedMember.getName()).isEqualTo("홍길동");
        assertThat(savedMember.getPhoneNum()).isEqualTo("010-1234-5678");
    }

    @Test
    @DisplayName("아이디가 존재하는 회원의 상태를 조회한다.")
    void findByLoginIdAndStatusActive() {
        // given
        String loginId = "test@example.com";

        Member member = Member.builder()
                .loginId(loginId)
                .pwd("ValidPassword123!")
                .name("홍길동")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .gender("M")
                .joinDate(LocalDateTime.now())
                .grade(1)
                .status(MemberStatus.ACTIVE)
                .build();

        // when
        memberRepository.save(member);
        Member savedMember = memberRepository.findByLoginIdAndStatus(loginId, MemberStatus.ACTIVE);

        // then
        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getLoginId()).isEqualTo("test@example.com");
        assertThat(savedMember.getName()).isEqualTo("홍길동");
        assertThat(savedMember.getPhoneNum()).isEqualTo("010-1234-5678");
    }

    @Test
    @DisplayName("아이디가 존재하는 회원의 상태가 활성상태인지 잠김상태인지 조회한다.")
    void findByLoginIdAndStatusActiveLocked() {
        // given
        Member activeMember = Member.builder()
                .loginId("active@example.com")
                .pwd("password123")
                .name("Active User")
                .phoneNum("010-1111-2222")
                .birth(LocalDate.of(1990, 5, 15))
                .gender("M")
                .status(MemberStatus.ACTIVE)
                .build();

        Member lockedMember = Member.builder()
                .loginId("locked@example.com")
                .pwd("password456")
                .name("Locked User")
                .phoneNum("010-3333-4444")
                .birth(LocalDate.of(1985, 3, 10))
                .gender("F")
                .status(MemberStatus.LOCKED)
                .build();

        em.persist(activeMember);
        em.persist(lockedMember);
        em.flush();
        em.clear();

        // when
        Member foundActiveMember = memberRepository.findByLoginIdAndStatus("active@example.com", MemberStatus.ACTIVE);
        Member foundLockedMember = memberRepository.findByLoginIdAndStatus("locked@example.com", MemberStatus.LOCKED);
        Member nonExistingMember = memberRepository.findByLoginIdAndStatus("nonexistent@example.com", MemberStatus.ACTIVE);

        // then
        assertThat(foundActiveMember).isNotNull();
        assertThat(foundActiveMember.getName()).isEqualTo("Active User");

        assertThat(foundLockedMember).isNotNull();
        assertThat(foundLockedMember.getName()).isEqualTo("Locked User");

        assertThat(nonExistingMember).isNull();
    }

    @Test
    @DisplayName("탈퇴 상태가 아닌 회원을 조회한다.")
    void findByLoginIdAndStatusNot_success() {
        // given
        Member activeMember = Member.builder()
                .loginId("activeUser@example.com")
                .pwd("password")
                .name("Active User")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .grade(50)
                .gender("M")
                .joinDate(LocalDateTime.now())
                .status(MemberStatus.ACTIVE) // ACTIVE 상태
                .build();

        Member withdrawnMember = Member.builder()
                .loginId("withdrawnUser@example.com")
                .pwd("password")
                .name("Withdrawn User")
                .phoneNum("010-9876-5432")
                .birth(LocalDate.of(1990, 1, 1))
                .grade(50)
                .gender("F")
                .joinDate(LocalDateTime.now())
                .status(MemberStatus.WITHDRAWN) // WITHDRAWN 상태
                .build();

        memberRepository.save(activeMember);
        memberRepository.save(withdrawnMember);

        // when
        Member foundMember = memberRepository.findByLoginIdExcludingWithdrawn("activeUser@example.com");

        // then
        assertThat(foundMember).isNotNull();
        assertThat(foundMember.getLoginId()).isEqualTo("activeUser@example.com");
    }

    @Test
    @DisplayName("탈퇴 상태 회원은 조회되지 않는다.")
    void findByLoginIdAndStatusNot_withdrawnExcluded() {
        // given
        Member withdrawnMember = Member.builder()
                .loginId("withdrawnUser@example.com")
                .pwd("password")
                .name("Withdrawn User")
                .phoneNum("010-9876-5432")
                .birth(LocalDate.of(1990, 1, 1))
                .grade(50)
                .gender("F")
                .joinDate(LocalDateTime.now())
                .status(MemberStatus.WITHDRAWN) // WITHDRAWN 상태
                .build();

        memberRepository.save(withdrawnMember);

        // when
        Member foundMember = memberRepository.findByLoginIdExcludingWithdrawn("withdrawnUser@example.com");

        // then
        assertThat(foundMember).isNull(); // 결과가 없어야 됨
    }
}