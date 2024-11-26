package shoppingmall.ankim.domain.address.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shoppingmall.ankim.domain.address.entity.member.MemberAddress;
import shoppingmall.ankim.domain.member.entity.Member;

import java.util.List;

public interface MemberAddressRepository extends JpaRepository<MemberAddress, Long> {
    List<MemberAddress> findByMember(Member member);
}
