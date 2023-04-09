package dev.practice.snsmysql.application.usecase;

import dev.practice.snsmysql.domain.follow.dto.FollowDto;
import dev.practice.snsmysql.domain.follow.service.FollowReadService;
import dev.practice.snsmysql.domain.member.dto.MemberDto;
import dev.practice.snsmysql.domain.member.service.MemberReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetFollowingMemberUsecase {

    private final FollowReadService followReadService;
    private final MemberReadService memberReadService;

    public List<MemberDto> execute(Long memberId) {
        /**
         * 입력받은 memberId 가 FromMemberId 로 등록된 Follow List 를 조회한다.
         * List 를 순회하면서 Member 를 조회하여 반환
         */

        var followList = followReadService.getFollowingList(memberId);

        var ids = followList.stream().map(FollowDto::toMemberId).toList();

        return memberReadService.getMemberList(ids);
    }
}
