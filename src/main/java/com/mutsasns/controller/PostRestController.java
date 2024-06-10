package com.mutsasns.controller;

import com.mutsasns.domain.dto.post.*;
import com.mutsasns.domain.dto.response.Response;
import com.mutsasns.service.PostService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
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
import static com.sun.tools.attach.VirtualMachine.list;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Slf4j
public class PostRestController {

    private final PostService postService;

    @ApiOperation(
            value = "포스트 작성"
            , notes = "title, body -> 포스트 작성 - 회원만 작성 가능(token 필)")
    @PostMapping("")
    public Response<PostResponse> createPost(@RequestBody PostRequest postRequest, @ApiIgnore Authentication authentication){
        PostResponse postResponse = postService.create(postRequest.getTitle(), postRequest.getBody(), authentication.getName());
        return Response.success(postResponse);
    }

    @ApiOperation(
            value = "포스트 조회(전체)"
            , notes = "모든 포스트 조회 - 회원/비회원 모두 조회 가능")
    @GetMapping("")
    public Response<Page<PostListResponse>> postList(@ApiIgnore @PageableDefault(size = 20) @SortDefault(sort = "createdAt",direction = Sort.Direction.DESC) Pageable pageable){
        Page<PostListResponse> list = postService.getAll(pageable);
        return Response.success(list);
    }

    @ApiOperation(
            value = "포스트 조회(상세)"
            , notes = "포스트 1개 조회 - 회원/비회원 모두 조회 가능")
    @ApiImplicitParam(
            name = "postId"
            , value = "포스트 ID"
            , required = true
            , dataType = "long"
            , paramType = "path"
            , defaultValue = "None")
    @GetMapping("/{postId}")
    public Response<PostListResponse> findById(@PathVariable Long postId){
        PostListResponse postListResponse = postService.getPost(postId);
        return Response.success(postListResponse);
    }

    @ApiOperation(
            value = "포스트 수정"
            , notes = "title, body -> 포스트 수정 - 포스트 등록한 회원만 수정 가능(token 필)")
    @ApiImplicitParam(
            name = "postId"
            , value = "포스트 ID"
            , required = true
            , dataType = "long"
            , paramType = "path"
            , defaultValue = "None")
    @PutMapping("/{postId}")
    public Response<PostResponse> modify(@PathVariable Long postId, @RequestBody PostRequest postModifyRequest, @ApiIgnore Authentication authentication){
        log.debug("userName : {} postId : {} title : {} body : {} ", authentication.getName(), postId, postModifyRequest.getTitle(), postModifyRequest.getBody());
        PostResponse postModifiedResponse = postService.modify(authentication.getName(), postId, postModifyRequest.getTitle(), postModifyRequest.getBody());
        return Response.success(postModifiedResponse);
    }

    @ApiOperation(
            value = "포스트 삭제"
            , notes = "포스트 삭제 - 포스트 등록한 회원만 삭제 가능(token 필)")
    @ApiImplicitParam(
            name = "postId"
            , value = "포스트 ID"
            , required = true
            , dataType = "long"
            , paramType = "path"
            , defaultValue = "None")
    @DeleteMapping("/{postId}")
    public Response<PostResponse> delete(@PathVariable Long postId, @ApiIgnore Authentication authentication){
        PostResponse postDeletedResponse = postService.delete(postId, authentication.getName());
        return Response.success(postDeletedResponse);
    }

    @ApiOperation(
            value = "마이피드 조회"
            , notes = "마이피드 조회 - 인증 된 사용자가 작성한 포스트 리스트 조회하는 기능")
    @GetMapping("/my")
    public Response<Page<PostListResponse>> myFeed(@ApiIgnore Authentication authentication, @ApiIgnore @PageableDefault(size = 20) @SortDefault(sort = "createdAt",direction = Sort.Direction.DESC) Pageable pageable){
        Page<PostListResponse> myFeed = postService.myFeedAll(authentication.getName(), pageable);
        return Response.success(myFeed);
    }

}
