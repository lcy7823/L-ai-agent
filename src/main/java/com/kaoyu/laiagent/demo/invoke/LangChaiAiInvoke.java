package com.kaoyu.laiagent.demo.invoke;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LangChaiAiInvoke {

    public static void main(String[] args) {

        QwenChatModel build = QwenChatModel.builder()
                .apiKey(TestApiKey.API_KEY)
                .modelName("qwen-plus")
                .build();
        String chat = build.chat("你是哪个模型");
        log.info("chat: {}", chat);
        System.out.println(chat);
    }


}
