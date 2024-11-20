package shoppingmall.ankim.domain.login.service;

import jakarta.servlet.http.HttpServletRequest;
import shoppingmall.ankim.domain.login.service.request.LoginServiceRequest;

import java.net.http.HttpRequest;
import java.util.Map;

public interface LoginService {

    Map<String, String> login(LoginServiceRequest loginServiceRequest, HttpServletRequest request);

}
