//package shoppingmall.ankim.domain.member.validator;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.validation.Errors;
//import shoppingmall.ankim.domain.member.controller.request.MemberEmailRequest;
//import shoppingmall.ankim.domain.member.service.port.MemberRepository;
//import shoppingmall.ankim.global.validator.AbstractValidator;
//
//@RequiredArgsConstructor
//@Component
//public class CheckEmailValidator extends AbstractValidator<MemberEmailRequest> {
//
//    private final MemberRepository memberRepository;
//
//    @Override
//    protected void doValidate(MemberEmailRequest dto, Errors errors) {
//        if(memberRepository.existsByEmail(dto.getId())){
//            errors.rejectValue("email", "이메일 중복 오류", "이미 사용중인 이메일 입니다.");
//        }
//    }
//}
