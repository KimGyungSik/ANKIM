package shoppingmall.ankim.domain.member.service.port;

import org.springframework.data.jpa.repository.JpaRepository;
import shoppingmall.ankim.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 이메일 중복 검사
    Boolean existsById(String email);

}