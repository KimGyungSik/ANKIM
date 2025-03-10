package shoppingmall.ankim.domain.viewRolling.entity;

import jakarta.persistence.*;
import lombok.*;
import shoppingmall.ankim.domain.category.entity.Category;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.viewRolling.controller.ViewRollingController;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "view_rolling",
        indexes = {
                @Index(name = "idx_view_rolling_category_period", columnList = "category_no, period"),
                @Index(name = "idx_view_rolling_prod_period", columnList = "prod_no, period")
        })
public class ViewRolling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_no", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_no", nullable = false)
    private Product product;

    // Rolling 기간 (REALTIME, DAILY, WEEKLY, MONTHLY)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RollingPeriod period;

    // 누적 조회수
    @Column(name = "total_views", nullable = false)
    private Integer totalViews = 0;

    // 최종 업데이트 시간
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    @Builder
    private ViewRolling(Category category, Product product, RollingPeriod period, Integer totalViews, LocalDateTime lastUpdated) {
        this.category = category;
        this.product = product;
        this.period = period;
        this.totalViews = totalViews == null ? 0 : totalViews;
        this.lastUpdated = lastUpdated;
    }

    public static ViewRolling create(Category category, Product product, RollingPeriod period, Integer totalViews, LocalDateTime lastUpdated) {
        return ViewRolling.builder()
                .category(category)
                .product(product)
                .period(period)
                .totalViews(totalViews)
                .lastUpdated(lastUpdated)
                .build();
    }
}
