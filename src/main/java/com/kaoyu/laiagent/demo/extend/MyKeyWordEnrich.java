package com.kaoyu.laiagent.demo.extend;


import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 自定义关键词提取器
 * 设置为标签
 */
@Component
public class MyKeyWordEnrich {

    @Resource
    private ChatModel dashscopeChatModel;


    // 自定义中文关键词提取模板
    String chineseKeywordTemplate =
            """
                    {context_str}
                    请从上述文档中提取 %s 个最具代表性的中文关键词，用英文逗号分隔。
                    关键词:""";

    public List<Document> enrichDocument(List<Document> documents) {
        KeywordMetadataEnricher enricher = KeywordMetadataEnricher.builder(this.dashscopeChatModel)
                .keywordCount(5)
                .keywordsTemplate(new PromptTemplate(chineseKeywordTemplate))
                .build();

        return enricher.apply(documents);
    }


}
