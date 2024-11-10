package shoppingmall.ankim.domain.terms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.terms.entity.TermsCategory;
import shoppingmall.ankim.domain.terms.repository.TermsRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TermsServiceImpl implements TermsService {

    private final TermsRepository termsRepository;

    @Override
    public List<Terms> join() {
        return termsRepository.findByCategoryAndActiveYn(TermsCategory.JOIN, "Y");
    }
}
