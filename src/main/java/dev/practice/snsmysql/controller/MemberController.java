package dev.practice.snsmysql.controller;

import dev.practice.snsmysql.domain.member.dto.MemberDto;
import dev.practice.snsmysql.domain.member.dto.RegisterMemberCommand;
import dev.practice.snsmysql.domain.member.service.MemberReadService;
import dev.practice.snsmysql.domain.member.service.MemberWriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}
