package shoppingmall.ankim.domain.security.service;

import java.util.Map;

public interface ReissueService {

    public void validateRefreshToken(String refreshToken);
    public Map<String, String> reissueToken(String refreshToken);
}
