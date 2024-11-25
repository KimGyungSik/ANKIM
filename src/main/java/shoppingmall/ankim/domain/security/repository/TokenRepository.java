//package shoppingmall.ankim.domain.security.repository;
//
//import org.springframework.data.repository.CrudRepository;
//import org.springframework.stereotype.Repository;
//import shoppingmall.ankim.domain.security.entity.RefreshToken;
//
//import java.util.Optional;
//
//@Repository
//public interface TokenRepository extends CrudRepository<RefreshToken, String> {
//
//    // access token 조회
//    Boolean existsByAccessToken(String accessToken);
//    // refresh token 조회
////    Boolean existsByRefreshToken(String refreshToken);
//
//    // refresh token 삭제
////    @Transactional
////    void deleteByRefreshToken(String refreshToken);
//
//    // refreshToken 값으로 삭제
//    void deleteById(String accessToken);
//
//    // 사용자 정의 삭제 메서드
////    Optional<RefreshToken> findByRefreshToken(String refreshToken);
//
//}
