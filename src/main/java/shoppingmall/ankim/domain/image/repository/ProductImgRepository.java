package shoppingmall.ankim.domain.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shoppingmall.ankim.domain.image.entity.ProductImg;

public interface ProductImgRepository extends JpaRepository<ProductImg,Long> {
}
