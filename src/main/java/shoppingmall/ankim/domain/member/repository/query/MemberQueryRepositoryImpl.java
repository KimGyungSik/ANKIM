package shoppingmall.ankim.domain.member.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.domain.member.entity.QMember;

import java.util.Optional;

@Transactional(readOnly = true)
@Repository
@RequiredArgsConstructor
public class MemberQueryRepositoryImpl implements MemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Member> findByLoginIdExcludingWithdrawn(String loginId) {
//    public Member findByLoginIdExcludingWithdrawn(String loginId) {
        QMember member = QMember.member;

        return Optional.ofNullable(queryFactory
                .selectFrom(member)
                .where(
                        member.loginId.eq(loginId),
                        member.status.ne(MemberStatus.LEAVE) // WITHDRAWN이 아닌 상태
                )
                .fetchOne());
    }
}
