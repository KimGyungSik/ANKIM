package shoppingmall.ankim.domain.security.service;

import java.util.Map;

public interface ReissueService {

    String validateRefreshToken(String accessToken);
    Map<String, String> reissueToken(String accessToken, String refreshToken);
    void isAccessTokenExist(String accessToken);
    void validateAccessToken(String accessToken);
}
