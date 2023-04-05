package dev.practice.snsmysql.domain.follow.service;

import dev.practice.snsmysql.domain.follow.dto.FollowDto;
import dev.practice.snsmysql.domain.follow.entity.Follow;
import dev.practice.snsmysql.domain.follow.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowReadService {

    private final FollowRepository followRepository;

    public List<FollowDto> getFollowList(Long memberId) {
        return followRepository.findAllByFromMemberId(memberId).stream()
                .map(this::toDto)
                .toList();
    }

    private FollowDto toDto(Follow follow) {
        return new FollowDto(
                follow.getId(),
                follow.getFromMemberId(),
                follow.getToMemberId(),
                follow.getCreatedAt()
        );
    }
}
