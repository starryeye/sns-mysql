package dev.practice.snsmysql.domain.post.service;

import dev.practice.snsmysql.domain.post.dto.PostCommand;
import dev.practice.snsmysql.domain.post.entity.Post;
import dev.practice.snsmysql.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostWriteService {

    private final PostRepository postRepository;

    public Long create(PostCommand command) {
        var post = Post.builder()
                .memberId(command.memberId())
                .content(command.contents())
                .build();

        return postRepository.save(post).getId();
    }
}
