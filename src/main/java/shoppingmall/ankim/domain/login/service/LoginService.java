package shoppingmall.ankim.domain.login.service;

import jakarta.servlet.http.HttpServletRequest;
import shoppingmall.ankim.domain.login.service.request.LoginServiceRequest;

import java.util.Map;

public interface LoginService {

    Map<String, Object> memberLogin(LoginServiceRequest loginServiceRequest, HttpServletRequest request);
    Map<String, Object> adminLogin(LoginServiceRequest loginServiceRequest, HttpServletRequest request);

}
