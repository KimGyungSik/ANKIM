package shoppingmall.ankim.domain.cart.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.cart.entity.CartItem;
import shoppingmall.ankim.domain.cart.entity.QCartItem;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.product.entity.ProductSellingStatus;

import java.util.List;

@Transactional(readOnly = true)
@Repository
@RequiredArgsConstructor
public class CartItemQueryRepositoryImpl implements CartItemQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CartItem> findOutOfStockItems(Member member) {
        QCartItem cartItem = QCartItem.cartItem;
        return queryFactory
                .selectFrom(QCartItem.cartItem)
                .where(
                        cartItem.cart.member.eq(member),
                        QCartItem.cartItem.item.qty.eq(0),  // 재고가 0인 경우
                        QCartItem.cartItem.activeYn.eq("Y") // 활성화된 품목만 조회
                )
                .fetch();
    }
}
