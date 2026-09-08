package com.kaoyu.laiagent.demo.invoke;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;

public class SdkAiInvoke {
    public static void main(String[] args) throws Exception {
        GenerationParam param = GenerationParam.builder()
                .model("qwen-plus")
                .apiKey(TestApiKey.API_KEY)
                .messages(java.util.List.of(
                        Message.builder().role(Role.USER.getValue()).content("我上个问题是什么").build()))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();

        GenerationResult result = new Generation().call(param);
        System.out.println(result.getOutput().getChoices().getFirst().getMessage().getContent());
    }
}
