package dev.practice.snsmysql.application.controller;

import dev.practice.snsmysql.domain.post.dto.PostCommand;
import dev.practice.snsmysql.domain.post.service.PostWriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostWriteService postWriteService;

    @PostMapping
    public Long create(PostCommand postCommand) {
        return postWriteService.create(postCommand);
    }
}
