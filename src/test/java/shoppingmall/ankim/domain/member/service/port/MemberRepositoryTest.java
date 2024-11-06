package shoppingmall.ankim.domain.member.service.port;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.entity.MemberStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    EntityManager em;
    
    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    public void testEntity() {
        Member member = Member.builder()
                .id("user123@ankim.com")
                .pwd("password")
                .uuid(UUID.randomUUID())
                .name("user1")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .gender("M")
                .joinDate(LocalDateTime.now())
                .status(MemberStatus.ACTIVE)
                .build();

        em.persist(member);

        // 초기화
        em.flush();
        em.clear();

        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        for (Member m : members) {
            System.out.println("m = " + m);
        }

    }

    @Test
    @DisplayName("이메일 중복 검증 테스트_중복 이메일이 없는 경우")
    void notExistsByIdTest() {
        String email = "test@ankim.com";
        Boolean isExist = memberRepository.existsById(email);

        Assertions.assertThat(isExist).isFalse();
    }

    @Test
    @DisplayName("이메일 중복 검증 테스트_중복 이메일이 있는 경우")
    void existsByIdTest() {
        String email = "user123@ankim.com";
        Boolean isExist = memberRepository.existsById(email);

        Assertions.assertThat(isExist).isTrue();
    }
}