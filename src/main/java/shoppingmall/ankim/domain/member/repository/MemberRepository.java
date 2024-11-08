package shoppingmall.ankim.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.repository.query.MemberQueryRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberQueryRepository {

    // 이메일 중복 검사
    Boolean existsById(String email);

    // Email 조회(소셜 로그인 기능 추가하게 되면 social_login 테이블에서 Email 조회 진행)
    Member findById(String Email);

    // 모든 회원 정보 불러오기
    List<Member> findAll();

}