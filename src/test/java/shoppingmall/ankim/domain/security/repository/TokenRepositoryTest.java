//package shoppingmall.ankim.domain.security.repository;
//
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.redis.connection.RedisServer;
//import org.springframework.data.redis.core.RedisTemplate;
//import shoppingmall.ankim.domain.security.entity.RefreshToken;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//class TokenRepositoryTest {
//
//    @Autowired
//    private TokenRepository tokenRepository;
//
//    @Test
//    @DisplayName("RefreshToken 저장 및 조회 테스트")
//    void testSaveAndFindByAccessToken() {
//        // given
//        String accessToken = "test-access-token";
//        String refreshToken = "test-refresh-token";
//
//        RefreshToken token = RefreshToken.builder()
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
//                .build();
//
//        // when
//        tokenRepository.save(token);
//
//        // then
//        Optional<RefreshToken> savedToken = tokenRepository.findById(accessToken);
//        assertThat(savedToken).isPresent();
//        assertThat(savedToken.get().getRefreshToken()).isEqualTo(refreshToken);
//
////        tokenRepository.deleteAll();
//    }
//
//    @Test
//    @DisplayName("RefreshToken 삭제 테스트")
//    void testDeleteByAccessToken() {
//        // given
//        String accessToken = "test-access-token";
//        String refreshToken = "test-refresh-token";
//
//        RefreshToken token = RefreshToken.builder()
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
//                .build();
//
//        tokenRepository.save(token);
//        assertThat(tokenRepository.existsByAccessToken(accessToken)).isTrue();
//
//        // when
//        tokenRepository.deleteById(accessToken);
//
//        // then
//        assertThat(tokenRepository.existsByAccessToken(accessToken)).isFalse();
//
//        tokenRepository.deleteAll();
//    }
//}