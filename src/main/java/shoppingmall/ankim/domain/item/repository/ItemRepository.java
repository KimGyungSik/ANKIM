package shoppingmall.ankim.domain.item.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.item.repository.query.ItemQueryRepository;
import shoppingmall.ankim.domain.product.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item,Long>, ItemQueryRepository {

    @Query(value = "SELECT GET_LOCK(:key, 30)", nativeQuery = true)
    Long getLock(@Param("key") String key);

    @Query(value = "SELECT RELEASE_LOCK(:key)", nativeQuery = true)
    void releaseLock(@Param("key") String key);

    List<Item> findByProduct_No(Long prodNo);

    long countByProduct(Product product);

    @Query("SELECT i FROM Item i WHERE i.no = :itemNo")
    Optional<Item> findByNo(@Param("itemNo") Long itemNo);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Item i WHERE i.no = :itemNo")
    Optional<Item> findByIdWithPessimisticLock(@Param("itemNo") Long itemNo);


    @Modifying
    @Query(value = "UPDATE Item i SET i.qty = i.qty - :quantity WHERE i.no = :itemNo AND i.qty >= :quantity")
    void reduceStock(@Param("quantity") Integer quantity, @Param("itemNo") Long itemNo);
}
