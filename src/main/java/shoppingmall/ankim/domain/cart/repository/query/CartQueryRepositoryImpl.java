package shoppingmall.ankim.domain.cart.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Repository
@RequiredArgsConstructor
public class CartQueryRepositoryImpl implements CartQueryRepository {

    private final JPAQueryFactory queryFactory;

}
