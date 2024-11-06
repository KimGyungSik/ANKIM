//package shoppingmall.ankim.global.validator;
//
//import jakarta.validation.Validator;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.validation.Errors;
//
//@Slf4j
//public abstract class AbstractValidator<T> implements Validator {
//
//    private static final Logger log = LoggerFactory.getLogger(AbstractValidator.class);
//
//    @Override
//    public boolean supports(Class<?> clazz) {
//        return true;
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public void validate(Object target, Errors errors) {
//        try {
//            doValidate((T) target, errors);
//        } catch (RuntimeException e) {
//            log.error("중복 검증 에러", e);
//            throw e;
//        }
//    }
//
//    protected abstract void doValidate(final T dto, final Errors errors);
//}
