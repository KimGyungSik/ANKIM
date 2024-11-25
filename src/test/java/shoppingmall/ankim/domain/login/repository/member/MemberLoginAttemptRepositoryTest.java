package shoppingmall.ankim.domain.login.repository.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.login.entity.BaseLoginAttempt;
import shoppingmall.ankim.domain.login.entity.member.loginHistory.MemberLoginAttempt;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.security.handler.RedisHandler;
import shoppingmall.ankim.global.config.QuerydslConfig;
import shoppingmall.ankim.global.config.RedisConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest
@Import(QuerydslConfig.class) // QuerydslConfig를 테스트에 추가
class MemberLoginAttemptRepositoryTest {

    @Autowired
    private MemberLoginAttemptRepository memberLoginAttemptRepository;

    @Autowired
    private MemberRepository memberRepository;

    @MockBean
    private RedisHandler redisHandler;

    @MockBean
    private RedisConfig redisConfig;

    @MockBean
    S3Service s3Service;

    @Test
    @DisplayName("활성화된 로그인 시도 정보를 조회한다.")
    void testFindByMemberAndLoginAttemptDetailsActiveYn() {
        // given
        Member member = Member.builder()
                .loginId("test@example.com")
                .pwd("password")
                .name("user1")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .grade(50)
                .gender("M")
                .joinDate(LocalDateTime.now())
                .status(MemberStatus.ACTIVE)
                .build();
        memberRepository.save(member);

        BaseLoginAttempt loginAttemptDetails = BaseLoginAttempt.builder()
                .failCount(1)
                .lastAttemptTime(LocalDateTime.now())
                .unlockTime(null)
                .activeYn("Y")
                .build();

        MemberLoginAttempt loginAttempt = MemberLoginAttempt.builder()
                .member(member)
                .loginAttemptDetails(loginAttemptDetails)
                .build();
        memberLoginAttemptRepository.save(loginAttempt);

        // when
        Optional<MemberLoginAttempt> result = memberLoginAttemptRepository
                .findByMemberAndLoginAttemptDetailsActiveYn(member, "Y");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getLoginAttemptDetails().getActiveYn()).isEqualTo("Y");
        assertThat(result.get().getLoginAttemptDetails().getFailCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("활성화된 로그인 시도가 없을 경우 빈 결과를 반환한다.")
    void testFindByMemberAndLoginAttemptDetailsActiveYnNotFound() {
        // given
        Member member = Member.builder()
                .loginId("test@example.com")
                .pwd("password")
                .name("user1")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .grade(50)
                .gender("M")
                .joinDate(LocalDateTime.now())
                .status(MemberStatus.ACTIVE)
                .build();
        memberRepository.save(member);

        // when
        Optional<MemberLoginAttempt> result = memberLoginAttemptRepository
                .findByMemberAndLoginAttemptDetailsActiveYn(member, "Y");

        // then
        assertThat(result).isNotPresent();
    }

    @Test
    @DisplayName("로그인 시도 정보를 업데이트한다.")
    void testUpdateLoginAttempt() {
        // given
        Member member = Member.builder()
                .loginId("test@example.com")
                .pwd("password")
                .name("user1")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .grade(50)
                .gender("M")
                .joinDate(LocalDateTime.now())
                .status(MemberStatus.ACTIVE)
                .build();
        memberRepository.save(member);

        BaseLoginAttempt loginAttemptDetails = BaseLoginAttempt.builder()
                .failCount(1)
                .lastAttemptTime(LocalDateTime.now())
                .unlockTime(null)
                .activeYn("Y")
                .build();

        MemberLoginAttempt loginAttempt = MemberLoginAttempt.builder()
                .member(member)
                .loginAttemptDetails(loginAttemptDetails)
                .build();
        memberLoginAttemptRepository.save(loginAttempt);

        // when
        Optional<MemberLoginAttempt> existingAttempt = memberLoginAttemptRepository
                .findByMemberAndLoginAttemptDetailsActiveYn(member, "Y");
        assertThat(existingAttempt).isPresent(); // 기존 데이터가 존재하는지 검증

        // Update: 실패 횟수 증가 및 마지막 시도 시간 업데이트
        MemberLoginAttempt retrievedAttempt = existingAttempt.get();
        retrievedAttempt.increaseFailCount();

        memberLoginAttemptRepository.save(retrievedAttempt);

        // then
        Optional<MemberLoginAttempt> updatedAttempt = memberLoginAttemptRepository
                .findByMemberAndLoginAttemptDetailsActiveYn(member, "Y");
        assertThat(updatedAttempt).isPresent();
        assertThat(updatedAttempt.get().getLoginAttemptDetails().getFailCount()).isEqualTo(2);
        assertThat(updatedAttempt.get().getLoginAttemptDetails().getLastAttemptTime()).isNotNull();
    }

    @Test
    @DisplayName("활성화된 로그인 시도를 비활성화한다.")
    void testDeactivateLoginAttempt() {
        // given
        Member member = Member.builder()
                .loginId("test@example.com")
                .pwd("password")
                .name("user1")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .grade(50)
                .gender("M")
                .joinDate(LocalDateTime.now())
                .status(MemberStatus.ACTIVE)
                .build();
        memberRepository.save(member);

        BaseLoginAttempt loginAttemptDetails = BaseLoginAttempt.builder()
                .failCount(1)
                .lastAttemptTime(LocalDateTime.now())
                .unlockTime(null)
                .activeYn("Y")
                .build();

        MemberLoginAttempt loginAttempt = MemberLoginAttempt.builder()
                .member(member)
                .loginAttemptDetails(loginAttemptDetails)
                .build();
        memberLoginAttemptRepository.save(loginAttempt);

        // when
        Optional<MemberLoginAttempt> existingAttempt = memberLoginAttemptRepository
                .findByMemberAndLoginAttemptDetailsActiveYn(member, "Y");
        assertThat(existingAttempt).isPresent(); // 기존 데이터가 존재하는지 검증

        // Update: 활성화 상태 비활성화로 변경
        MemberLoginAttempt retrievedAttempt = existingAttempt.get();
        retrievedAttempt.deactivateLoginAttempt();

        memberLoginAttemptRepository.save(retrievedAttempt);

        // then
        Optional<MemberLoginAttempt> deactivatedAttempt = memberLoginAttemptRepository
                .findByMemberAndLoginAttemptDetailsActiveYn(member, "Y");
        assertThat(deactivatedAttempt).isNotPresent(); // 비활성화되었으므로 조회되지 않아야 함
    }

}