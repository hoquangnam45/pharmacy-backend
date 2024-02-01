package com.hoquangnam45.pharmacy.controller;

import com.hoquangnam45.pharmacy.entity.Tag;
import com.hoquangnam45.pharmacy.pojo.PartialPage;
import com.hoquangnam45.pharmacy.repo.TagRepo;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("tag")
public class TagController {
    private final TagRepo tagRepo;

    public TagController(TagRepo tagRepo) {
        this.tagRepo = tagRepo;
    }

    public ResponseEntity<PartialPage<Tag>> searchTags(
            @RequestParam("searchTerm") String searchTerm,
            @RequestParam(value = "limit", defaultValue = "5") Integer limit) {
        List<Tag> tags = tagRepo.findAllByValueLike("%" + searchTerm + "%", Pageable.ofSize(limit + 1).first());
        boolean more = tags.size() > limit;
        tags.remove(tags.size() - 1);
        return ResponseEntity.ok().body(new PartialPage<>(more, tags));
    }
}
