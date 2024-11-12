package shoppingmall.ankim.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.repository.query.MemberQueryRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberQueryRepository {

    // 이메일 중복 검사를 한다.
    Boolean existsById(String email);

    // id(Email)을 조회한다.(소셜 로그인 기능 추가하게 되면 social_login 테이블에서 Email 조회 진행)
    @Query("select m from Member m where m.id = :id")
    Member findByEmail(@Param("id")String id);

    // 모든 회원 정보를 불러온다.
    List<Member> findAll();

}