package shoppingmall.ankim.domain.cart.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import shoppingmall.ankim.domain.cart.entity.Cart;
import shoppingmall.ankim.domain.cart.entity.CartItem;
import shoppingmall.ankim.domain.cart.entity.QCartItem;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.repository.ProductRepository;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;
import shoppingmall.ankim.factory.CartFactory;
import shoppingmall.ankim.factory.MemberJwtFactory;
import shoppingmall.ankim.global.config.QuerydslConfig;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DataJpaTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
@Import(QuerydslConfig.class)
class CartItemRepositoryTest {

    @Autowired
    private EntityManager em;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    public void test() {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberJwtFactory.createMember(em, loginId);
        String accessToken = MemberJwtFactory.createToken(member, jwtTokenProvider);

        List<CartItem> cartItemList = CartFactory.createCart(em, member);
        Long cartItemNo1 = cartItemList.get(0).getNo();
        Long cartItemNo2 = cartItemList.get(1).getNo();
        System.out.println("cartItemNo1 = " + cartItemNo1 + "; cartItemNo2 = " + cartItemNo2);

        List<Long> cartItemNoList = List.of(cartItemNo1, cartItemNo2);

        // when
        List<CartItem> byNoIn = cartItemRepository.findByNoIn(cartItemNoList);

        // then
        assertThat(byNoIn).isNotNull();
        assertThat(byNoIn.size()).isEqualTo(cartItemNoList.size());
        for (CartItem cartItem : byNoIn) {
            System.out.println("cartItem.getItemName() = " + cartItem.getItemName());
        }

    }

}