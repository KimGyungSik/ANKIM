package shoppingmall.ankim.domain.option.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.product.entity.Product;
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "option_group")
public class OptionGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @ManyToOne
    @JoinColumn(name = "prod_no", nullable = false)
    private Product product;

    @Column(length = 255)
    private String name;

    // Getters and Setters
}

