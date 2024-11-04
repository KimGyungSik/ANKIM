package shoppingmall.ankim.domain.image.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "ProductImg")
public class ProductImg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(name = "prod_no", nullable = false)
    private Long prodNo;

    private String filename;

    @Column(name = "orig_name")
    private String origName;

    private String path;
    private String type;
    private Integer ord;
}
