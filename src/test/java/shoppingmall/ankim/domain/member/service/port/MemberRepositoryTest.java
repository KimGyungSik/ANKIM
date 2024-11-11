package shoppingmall.ankim.domain.member.service.port;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.global.config.QuerydslConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@DataJpaTest
@Import(QuerydslConfig.class)
@Transactional
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
}