package shoppingmall.ankim.factory;

import com.mysema.commons.lang.Pair;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shoppingmall.ankim.domain.leaveReason.entity.LeaveReason;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.security.dto.CustomUserDetails;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class MemberFactory {

    public static Member createSecureMember(EntityManager entityManager, String loginId, String rawPwd, BCryptPasswordEncoder encoder) {
        String encodedPwd = encoder.encode(rawPwd);
        Member member = Member.builder()
                .loginId(loginId)
                .pwd(encodedPwd) // 암호화된 비밀번호 저장
                .name("홍길동")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .grade(50)
                .gender("M")
                .joinDate(LocalDateTime.now())
                .status(MemberStatus.ACTIVE)
                .build();
        entityManager.persist(member);
        return member;
    }

    public static Member createMember(EntityManager entityManager, String loginId) {
        Member member = Member.builder()
                .loginId(loginId)
                .pwd("password")
                .name("홍길동")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .grade(50)
                .gender("M")
                .joinDate(LocalDateTime.now())
                .status(MemberStatus.ACTIVE)
                .build();
        entityManager.persist(member);
        return member;
    }

    public static Member createMemberAndProduct(EntityManager entityManager, String loginId) {
        Member member = createMember(entityManager, loginId);

        ProductFactory.createProduct(entityManager);

        entityManager.flush(); // DB로 반영
        entityManager.clear(); // 영속성 컨텍스트 초기화

        return member;
    }

    public static Pair<Member, List<LeaveReason>> createMemberAndLeaveReason(EntityManager entityManager, String loginId, String rawPwd, BCryptPasswordEncoder encoder) {
        String encodedPwd = encoder.encode(rawPwd);

        // Member 생성
        Member member = Member.builder()
                .loginId(loginId)
                .pwd(encodedPwd) // 암호화된 비밀번호 저장
                .name("홍길동")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .grade(50)
                .gender("M")
                .joinDate(LocalDateTime.now())
                .status(MemberStatus.ACTIVE)
                .build();
        entityManager.persist(member);

        // LeaveReason 목록 생성
        List<LeaveReason> leaveReasons = Arrays.asList(
                LeaveReason.builder().reason("탈퇴 후 재가입을 위해서").activeYn("Y").build(),
                LeaveReason.builder().reason("사고 싶은 상품이 없어서").activeYn("Y").build(),
                LeaveReason.builder().reason("자주 이용하지 않아서").activeYn("Y").build(),
                LeaveReason.builder().reason("서비스 및 고객지원이 만족스럽지 않아서").activeYn("Y").build(),
                LeaveReason.builder().reason("광고성 알림이 너무 많이 와서").activeYn("Y").build(),
                LeaveReason.builder().reason("기타").activeYn("Y").build()
        );

        // DB에 persist
        leaveReasons.forEach(entityManager::persist);

        return Pair.of(member, leaveReasons); // Member와 LeaveReason 목록 반환
    }

    public static Member createMemberAndTerms(EntityManager entityManager, String loginId) {
        Member member = createMember(entityManager, loginId);

        return member;
    }
}