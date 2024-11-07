package shoppingmall.ankim.domain.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shoppingmall.ankim.domain.category.entity.Category;
import shoppingmall.ankim.domain.category.repository.query.CategoryQueryRepository;



public interface CategoryRepository extends JpaRepository<Category,Long>, CategoryQueryRepository {
}
