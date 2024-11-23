package shoppingmall.ankim.domain.admin.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.admin.entity.Admin;
import shoppingmall.ankim.domain.admin.entity.AdminStatus;
import shoppingmall.ankim.domain.admin.entity.QAdmin;
import shoppingmall.ankim.domain.member.entity.MemberStatus;

@Transactional(readOnly = true)
@Repository
@RequiredArgsConstructor
public class AdminQueryRepositoryImpl implements AdminQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Admin findByLoginIdExcludingResigned(String loginId) {
        QAdmin admin = QAdmin.admin;

        return queryFactory
                .selectFrom(admin)
                .where(
                        admin.loginId.eq(loginId),
                        admin.status.ne(AdminStatus.RESIGNED) // 퇴사한 직원
                )
                .fetchOne();
    }
}
