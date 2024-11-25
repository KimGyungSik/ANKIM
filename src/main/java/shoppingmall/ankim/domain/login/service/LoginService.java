package shoppingmall.ankim.domain.login.service;

import jakarta.servlet.http.HttpServletRequest;
import shoppingmall.ankim.domain.login.service.request.LoginServiceRequest;

import java.util.Map;

public interface LoginService {

    Map<String, String> memberLogin(LoginServiceRequest loginServiceRequest, HttpServletRequest request);
    Map<String, String> adminLogin(LoginServiceRequest loginServiceRequest, HttpServletRequest request);

}
