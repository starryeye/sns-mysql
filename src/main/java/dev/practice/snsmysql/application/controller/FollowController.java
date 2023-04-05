package dev.practice.snsmysql.application.controller;

import dev.practice.snsmysql.application.usecase.CreateFollowMemberUsecase;
import dev.practice.snsmysql.application.usecase.GetFollowingMemberUsecase;
import dev.practice.snsmysql.domain.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/follows")
public class FollowController {

    private final CreateFollowMemberUsecase createFollowMemberUsecase;
    private final GetFollowingMemberUsecase getFollowingMemberUsecase;

    @PostMapping("/{fromMemberId}/{toMemberId}")
    public void createFollowMember(@PathVariable Long fromMemberId, @PathVariable Long toMemberId) {
        createFollowMemberUsecase.execute(fromMemberId, toMemberId);
    }

    @GetMapping("/{fromMemberId}")
    public List<MemberDto> getFollowingMember(@PathVariable Long fromMemberId) {
        return getFollowingMemberUsecase.execute(fromMemberId);
    }
}
