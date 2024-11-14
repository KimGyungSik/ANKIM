package shoppingmall.ankim.domain.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.security.dto.CustomUserDetails;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
//    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        if (memberRepository.existsById(id)) {
            return new CustomUserDetails(memberRepository.findByEmail(id), "MEMBER");
//        } else if (adminRepository.existsByUsername(id)) {
//            return new CustomUserDetails(adminRepository.findByUsername(id), "ADMIN");
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }
}
