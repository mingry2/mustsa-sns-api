package com.mutsasns.controller;

import com.mutsasns.domain.dto.alarm.AlarmContainer;
import com.mutsasns.domain.dto.alarm.AlarmResponse;
import com.mutsasns.domain.dto.response.Response;
import com.mutsasns.service.AlarmService;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import java.util.List;

@RestController
@RequestMapping("/api/v1/alarms")
@RequiredArgsConstructor
@Slf4j
@Data
public class AlarmRestController {

    private final AlarmService alarmService;

    @ApiOperation(
            value = "받은 알람 조회"
            , notes = "NEW_COMMENT_ON_POST / NEW_LIKE_ON_POST 받은 알람 조회")
    @GetMapping("")
    public Response<AlarmContainer> alarm(@ApiIgnore Authentication authentication, @ApiIgnore @PageableDefault(size = 20) @SortDefault(sort = "createdAt",direction = Sort.Direction.DESC) Pageable pageable){
        List<AlarmResponse> list = alarmService.listAlarm(authentication.getName(), pageable);
        return Response.success(new AlarmContainer(list));

    }
}
