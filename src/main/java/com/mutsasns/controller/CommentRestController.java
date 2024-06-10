package com.mutsasns.controller;

import com.mutsasns.domain.dto.comment.create.CommentCreateRequest;
import com.mutsasns.domain.dto.comment.create.CommentCreateResponse;
import com.mutsasns.domain.dto.comment.delete.CommentDeleteResponse;
import com.mutsasns.domain.dto.comment.list.CommentListResponse;
import com.mutsasns.domain.dto.comment.modify.CommentModifyRequest;
import com.mutsasns.domain.dto.comment.modify.CommentModifyResponse;
import com.mutsasns.domain.dto.response.Response;
import com.mutsasns.service.CommentService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Slf4j
@Data
public class CommentRestController {

    private final CommentService commentService;

    @ApiOperation(
            value = "댓글 등록"
            , notes = "comment -> 댓글 등록 - 회원만 등록 가능")
    @ApiImplicitParam(
            name = "postId"
            , value = "포스트 ID"
            , required = true
            , dataType = "long"
            , paramType = "path"
            , defaultValue = "None")
    @PostMapping("/{postId}/comments")
    public Response<CommentCreateResponse> createComment(@PathVariable Long postId, @RequestBody CommentCreateRequest commentCreateRequest, @ApiIgnore Authentication authentication){
        log.debug("postId : {} comment : {} userName : {}", postId, commentCreateRequest, authentication.getName());
        CommentCreateResponse commentCreateResponse = commentService.create(postId, commentCreateRequest, authentication.getName());
        return Response.success(commentCreateResponse);
    }

    @ApiOperation(
            value = "댓글 조회"
            , notes = "comment -> 댓글 등록 - 회원/비회원 모두 조회 가능")
    @ApiImplicitParam(
            name = "postId"
            , value = "포스트 ID"
            , required = true
            , dataType = "long"
            , paramType = "path"
            , defaultValue = "None")
    @GetMapping("/{postId}/comments")
    public Response<Page<CommentListResponse>> listComment(@PathVariable Long postId, @ApiIgnore @PageableDefault(size = 10) @SortDefault(sort = "createdAt",direction = Sort.Direction.DESC) Pageable pageable){
        Page<CommentListResponse> list = commentService.getAll(postId, pageable);
        return Response.success(list);
    }

    @ApiOperation(
            value = "댓글 수정"
            , notes = "comment -> 댓글 수정 - 댓글 작성한 회원만 수정 가능")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "postId"
                    , value = "포스트 ID"
                    , required = true
                    , dataType = "long"
                    , paramType = "path"
                    , defaultValue = "None")
            ,
            @ApiImplicitParam(
                    name = "id"
                    , value = "댓글 ID"
                    , required = true
                    , dataType = "long"
                    , paramType = "path"
                    , defaultValue = "None")
    })
    @PutMapping("/{postId}/comments/{id}")
    public Response<CommentModifyResponse> modifyComment(@PathVariable Long postId, @PathVariable Long id, @RequestBody CommentModifyRequest commentModifyRequest, @ApiIgnore Authentication authentication){
        log.debug("postId : {} id : {} commentModifyRequest.getComment : {} authentication.getName : {}", postId, id, commentModifyRequest.getComment(), authentication.getName());
        CommentModifyResponse commentModifyResponse = commentService.modify(postId, id, commentModifyRequest, authentication.getName());
        return Response.success(commentModifyResponse);
    }

    @ApiOperation(
            value = "댓글 삭제"
            , notes = "댓글 삭제 - 댓글 작성한 회원만 삭제 가능")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "postId"
                    , value = "포스트 ID"
                    , required = true
                    , dataType = "long"
                    , paramType = "path"
                    , defaultValue = "None")
            ,
            @ApiImplicitParam(
                    name = "id"
                    , value = "댓글 ID"
                    , required = true
                    , dataType = "long"
                    , paramType = "path"
                    , defaultValue = "None")
    })
    @DeleteMapping("/{postId}/comments/{id}")
    public Response<CommentDeleteResponse> deleteComment(@PathVariable Long postId, @PathVariable Long id, @ApiIgnore Authentication authentication){
        log.debug("postId : {} id : {} authentication.getName : {}", postId, id, authentication.getName());
        CommentDeleteResponse commentDeleteResponse = commentService.delete(postId, id, authentication.getName());
        return Response.success(commentDeleteResponse);
    }
}
