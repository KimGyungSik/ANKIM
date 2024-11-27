package shoppingmall.ankim.domain.cart.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import shoppingmall.ankim.domain.cart.entity.Cart;
import shoppingmall.ankim.domain.cart.entity.CartItem;
import shoppingmall.ankim.domain.item.repository.ItemRepository;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.global.config.QuerydslConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(QuerydslConfig.class)
class CartItemRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("장바구니에 상품으")
    @Test
    void findByCartAndItemNo() {
        // given
        Member member = Member.builder()
                .loginId("test@ankim.com")
                .pwd("password!")
                .name("홍길동")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .grade(50)
                .gender("M")
                .joinDate(LocalDateTime.now())
                .status(MemberStatus.ACTIVE)
                .build();
        memberRepository.save(member);

        Cart cart = Cart.create(member, LocalDateTime.now());
        cartRepository.save(cart);

        // when
        Optional<Cart> result = cartRepository.findByMemberAndActiveYn(member, "Y");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getMember()).isEqualTo(member);
        assertThat(result.get().getActiveYn()).isEqualTo("Y");
    }


}