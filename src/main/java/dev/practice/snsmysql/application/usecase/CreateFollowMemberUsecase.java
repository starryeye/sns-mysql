package dev.practice.snsmysql.application.usecase;

import dev.practice.snsmysql.domain.follow.service.FollowWriteService;
import dev.practice.snsmysql.domain.member.service.MemberReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * class 명이 동사인데
 * 이유는 메서드가 하나인 class 로 할 것이고, 메서드 명은 execute 로 할 것이다.
 *
 * usecase 는 도메인을 조합해서 하나의 기능을 만드는 것이다. (흐름만 제어)
 * 도메인 서비스의 흐름을 제어하는 역할이다. (도메인 로직이 들어가면 안된다.)
 */
@Service
@RequiredArgsConstructor
public class CreateFollowMemberUsecase {

    private final FollowWriteService followWriteService;
    private final MemberReadService memberReadService;

    public void execute(Long fromMemberId, Long toMemberId) {
        /**
         * 입력받은 memberId 로 Member 를 조회한다.
         * FollowWriteService.create(fromMember, toMember) 를 호출한다.
         */

        var fromMember = memberReadService.getMember(fromMemberId);
        var toMember = memberReadService.getMember(toMemberId);

        followWriteService.create(fromMember, toMember);
    }
}
