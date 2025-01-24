package shoppingmall.ankim.domain.memberHistory.handler;

import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.domain.memberHistory.entity.MemberHistory;
import shoppingmall.ankim.domain.memberHistory.entity.ModificationCode;

public class MemberHistoryHandler {

    public static MemberHistory handleNameChange(Member member, String request) {
        return MemberHistory.builder()
                .member(member)
                .modCode(ModificationCode.NAME)
                .oldValue(member.getName())
                .newValue(request)
                .build();
    }

    public static MemberHistory handlePasswordChange(Member member, String request) {
        return MemberHistory.builder()
                .member(member)
                .modCode(ModificationCode.PASSWORD)
                .oldValue(member.getPassword())
                .newValue(request)
                .build();
    }

    public static MemberHistory handleEmailChange(Member member, String request) {
        return MemberHistory.builder()
                .member(member)
                .modCode(ModificationCode.EMAIL)
                .oldValue(member.getLoginId())
                .newValue(request)
                .build();
    }

    public static MemberHistory handleStatusChange(Member member, MemberStatus memberStatus) {
        return MemberHistory.builder()
                .member(member)
                .modCode(ModificationCode.STATUS)
                .oldValue(member.getStatus().name())
                .newValue(memberStatus.name())
                .build();
    }
}
