package shoppingmall.ankim.domain.address.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.address.entity.member.MemberAddress;
import shoppingmall.ankim.domain.address.entity.member.QMemberAddress;

import java.util.Optional;

@Transactional(readOnly = true)
@Repository
@RequiredArgsConstructor
public class MemberAddressQueryRepositoryImpl implements MemberAddressQueryRepository {

    private final JPAQueryFactory queryFactory;

}
