package dev.practice.snsmysql.domain.follow.service;

import dev.practice.snsmysql.domain.follow.entity.Follow;
import dev.practice.snsmysql.domain.follow.repository.FollowRepository;
import dev.practice.snsmysql.domain.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor
public class FollowWriteService {

    private final FollowRepository followRepository;

    /**
     * memberId(식별자)가 아닌 MemberDto 를 받은 이유..
     * memberId 로 받으면.. 여기 클래스에서 Member 도메인을 의존해야 한다. (조회)
     * 추후, MSA 도입을 위해서는 도메인간의 의존성을 낮추는게 좋다.
     * 따라서, Application Layer 를 만들어주고 해당 Layer 에서 도메인간 의존성을 풀어준다. (orchestration)
     * -> CreateFollowMemberUseCase
     */
    public void create(MemberDto from, MemberDto to) {
        /**
         * From To member 정보를 받아서 팔로우를 생성한다.
         * From To 가 동일한지 체크하는 validate
         */

        Assert.isTrue(!from.id().equals(to.id()), "동일한 회원은 팔로우 할 수 없습니다.");

        var follow = Follow.builder()
                .fromMemberId(from.id())
                .toMemberId(to.id())
                .build();

        followRepository.save(follow);
    }
}
