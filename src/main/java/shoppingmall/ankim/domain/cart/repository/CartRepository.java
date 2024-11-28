package shoppingmall.ankim.domain.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shoppingmall.ankim.domain.cart.entity.Cart;
import shoppingmall.ankim.domain.cart.repository.query.CartQueryRepository;
import shoppingmall.ankim.domain.member.entity.Member;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long>, CartQueryRepository {

    // 특정 회원의 활성화된 장바구니가 있는지 확인한다.(null값 체크가 잦을꺼 같아서 Optional로 작성)
    @Query("SELECT c FROM Cart c JOIN FETCH c.cartItems WHERE c.member = :member AND c.activeYn = :activeYn")
    Optional<Cart> findByMemberAndActiveYn(@Param("member") Member member, @Param("activeYn") String activeYn);

}