package shoppingmall.ankim.domain.member.service.port;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.global.config.QuerydslConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest
@Import(QuerydslConfig.class)
class MemberRepositoryTest {

    @Autowired
    EntityManager em;
    
    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("이메일 중복 검증 테스트_중복 이메일이 없는 경우")
    void notExistsByIdTest() {
        // given
        Member member = Member.builder()
                .id("user123@ankim.com")
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
        Boolean isExist = memberRepository.existsById(email);

        // then
        Assertions.assertThat(isExist).isFalse();
    }

    @Test
    @DisplayName("이메일 중복 검증 테스트_중복 이메일이 있는 경우")
    void existsByIdTest() {
        // given
        Member member = Member.builder()
                .id("user123@ankim.com")
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
        Boolean isExist = memberRepository.existsById(email);

        // then
        Assertions.assertThat(isExist).isTrue();
    }

    @Test
    @DisplayName("회원 정보를 DB에 저장하고 조회할 수 있다.")
    void saveAndFindMember() {
        // given
        Member member = Member.builder()
                .id("test@example.com")
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
        Member savedMember = memberRepository.findByEmail(member.getId());

        // then
        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getId()).isEqualTo("test@example.com");
        assertThat(savedMember.getName()).isEqualTo("홍길동");
        assertThat(savedMember.getPhoneNum()).isEqualTo("010-1234-5678");
    }
}