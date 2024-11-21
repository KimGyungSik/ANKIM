package shoppingmall.ankim.domain.security.service;

import java.util.Map;

public interface ReissueService {

    void validateRefreshToken(String refreshToken);
    Map<String, String> reissueToken(String accessToken, String refreshToken);
    void isAccessTokenExist(String accessToken);
//    void isRefreshTokenExist(String refreshToken);
}
