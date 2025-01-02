package shoppingmall.ankim.domain.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shoppingmall.ankim.domain.item.entity.Item;

public interface ItemLockRepository extends JpaRepository<Item,Long> {
    @Query(value = "SELECT GET_LOCK(:key, 90)", nativeQuery = true)
    Long getLock(@Param("key") String key);

    @Query(value = "SELECT RELEASE_LOCK(:key)", nativeQuery = true)
    void releaseLock(@Param("key") String key);
}
