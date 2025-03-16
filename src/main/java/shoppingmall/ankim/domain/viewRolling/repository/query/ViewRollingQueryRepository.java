package shoppingmall.ankim.domain.viewRolling.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import shoppingmall.ankim.domain.product.dto.ProductListResponse;
import shoppingmall.ankim.domain.viewRolling.entity.RollingPeriod;


public interface ViewRollingQueryRepository {
    Page<ProductListResponse> getViewRollingProducts(Long categoryNo, RollingPeriod period, Pageable pageable);
}
