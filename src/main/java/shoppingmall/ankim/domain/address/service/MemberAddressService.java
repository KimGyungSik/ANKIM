package shoppingmall.ankim.domain.address.service;

import shoppingmall.ankim.domain.address.service.request.MemberAddressRegisterServiceRequest;

public interface MemberAddressService {
    String saveOrUpdateAddress(String accessToken, MemberAddressRegisterServiceRequest request);
}
