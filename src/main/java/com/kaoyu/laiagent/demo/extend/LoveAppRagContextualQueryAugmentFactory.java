package com.kaoyu.laiagent.demo.extend;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;

/**
 * 上下文查询增强器工厂类
 * 没有rag知识库时，自定义回复
 */
@Slf4j
public class LoveAppRagContextualQueryAugmentFactory {

    public static ContextualQueryAugmenter createInstance(){
        PromptTemplate promptTemplate = new PromptTemplate("""
                你必须回答:
                抱歉，我只能回答你关于情感的问题，别的没办法帮到你
                其它内容不用回答
                """);
        return ContextualQueryAugmenter.builder()
                .allowEmptyContext(false)
                .emptyContextPromptTemplate(promptTemplate)
                .build();
    }



}
