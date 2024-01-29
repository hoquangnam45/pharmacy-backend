package com.hoquangnam45.pharmacy.component.mapper;

import com.hoquangnam45.pharmacy.entity.Comment;
import com.hoquangnam45.pharmacy.pojo.CommentResponse;
import org.mapstruct.Mapper;


@Mapper
public interface CommentMapper {
    CommentResponse createResponse(Comment comment);
}
