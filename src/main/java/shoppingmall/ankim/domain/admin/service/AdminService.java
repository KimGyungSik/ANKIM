package shoppingmall.ankim.domain.admin.service;

import shoppingmall.ankim.domain.admin.controller.request.AdminRegisterRequest;
import shoppingmall.ankim.domain.admin.service.request.AdminIdValidServiceRequest;
import shoppingmall.ankim.domain.admin.service.request.AdminRegisterServiceRequest;

public interface AdminService {

    // 아이디 중복 검증
    void isLoginIdDuplicated(AdminIdValidServiceRequest request);

    // 회원 가입
    void register(AdminRegisterServiceRequest request);
}
