package dev.practice.snsmysql.domain.member.service;

import dev.practice.snsmysql.domain.member.dto.RegisterMemberCommand;
import dev.practice.snsmysql.domain.member.entity.Member;
import dev.practice.snsmysql.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberWriteService {

    private final MemberRepository memberRepository;

    public Member register(RegisterMemberCommand command) {
        /**
         * 목표 - 회원정보(이메일, 닉네임, 생년월일)를 등록한다.
         *     - 닉네임은 10자를 넘길 수 없다.
         *
         * 파라미터 - memberRegisterCommand
         *
         * val member = Member.of(memberRegisterCommand)
         *
         * memberRepository.save(member)
         */

        var member = Member.builder()
                .nickname(command.nickname())
                .email(command.email())
                .birthday(command.birthday())
                .build();

        return memberRepository.save(member);
    }
}
