package shoppingmall.ankim.domain.address.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shoppingmall.ankim.domain.address.entity.member.MemberAddress;
import shoppingmall.ankim.domain.member.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberAddressRepository extends JpaRepository<MemberAddress, Long> {
    @Query("select ma from MemberAddress ma join fetch ma.member where ma.member = :member")
    List<MemberAddress> findByMember(@Param("member") Member member);

    @Query("SELECT ma FROM MemberAddress ma WHERE ma.member = :member AND ma.defaultAddressYn = 'Y'")
    Optional<MemberAddress> findDefaultAddressByMember(@Param("member") Member member);
}