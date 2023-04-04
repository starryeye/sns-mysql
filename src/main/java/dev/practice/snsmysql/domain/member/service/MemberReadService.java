package dev.practice.snsmysql.domain.member.service;

import dev.practice.snsmysql.domain.member.dto.MemberDto;
import dev.practice.snsmysql.domain.member.entity.Member;
import dev.practice.snsmysql.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MemberReadService {

    private final MemberRepository memberRepository;

    public MemberDto getMember(Long id) {

        var member = memberRepository.findById(id).orElseThrow();
        return toDto(member);
    }

    public MemberDto toDto(Member member) {
        return new MemberDto(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getBirthday()
        );
    }
}
