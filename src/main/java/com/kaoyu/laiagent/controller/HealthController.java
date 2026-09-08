package com.kaoyu.laiagent.controller;


import com.kaoyu.laiagent.common.BaseResponse;
import com.kaoyu.laiagent.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class HealthController {

    @GetMapping("/health")
    public BaseResponse<String> health(){
        return ResultUtils.success("health");
    }



}
