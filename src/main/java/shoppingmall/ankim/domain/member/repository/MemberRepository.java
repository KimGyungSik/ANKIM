package shoppingmall.ankim.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.domain.member.repository.query.MemberQueryRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberQueryRepository {

    // 이메일 중복 검사를 한다.
    Boolean existsByLoginId(String loginId);

    // loginId(Email)을 조회한다.(소셜 로그인 기능 추가하게 되면 social_login 테이블에서 Email 조회 진행)
    Member findByLoginId(String loginId); // FIXME Member -> Optional<Member>로 변경 고려

    // 모든 회원 정보를 불러온다.
    List<Member> findAll();

    // 회원의 아이디가 존재하는지 어떤 상태인지 조회한다.
    Member findByLoginIdAndStatus(String loginId, MemberStatus status);

    // 회원의 비밀번호를 조회한다.
    Member findByLoginIdAndPassword(Member member, String password);

}