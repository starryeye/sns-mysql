package dev.practice.snsmysql.application.controller;

import dev.practice.snsmysql.domain.member.dto.MemberDto;
import dev.practice.snsmysql.domain.member.dto.MemberNicknameHistoryDto;
import dev.practice.snsmysql.domain.member.dto.RegisterMemberCommand;
import dev.practice.snsmysql.domain.member.service.MemberReadService;
import dev.practice.snsmysql.domain.member.service.MemberWriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberWriteService memberWriteService;
    private final MemberReadService memberReadService;

    @PostMapping("/members")
    public MemberDto register(@RequestBody RegisterMemberCommand command) {

        var member = memberWriteService.register(command);
        return memberReadService.toDto(member); //TODO: DtoMapper
    }

    @GetMapping("/members/{id}")
    public MemberDto getMember(@PathVariable Long id) {

        return memberReadService.getMember(id);
    }

    @PutMapping("/members/{id}")
    public MemberDto changeNickname(@PathVariable Long id, @RequestBody String nickname) {
        memberWriteService.changeNickname(id, nickname);
        return memberReadService.getMember(id);
    }

    @GetMapping("/members/{memberId}/nickname-histories")
    public List<MemberNicknameHistoryDto> getMemberNicknameHistories(@PathVariable Long memberId) {
        return memberReadService.getMemberNicknameHistories(memberId);
    }
}
