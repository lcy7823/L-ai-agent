package com.kaoyu.laiagent.rag;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 自定义的rag知识库云服务配置类
 */
@Configuration
public class LoveAppRagCloudAdvisorConfig {

    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;

    @Bean
    public Advisor loveAppRagCloudAdvisor(){
        DashScopeApi dashScopeApi = DashScopeApi.builder().apiKey(apiKey).build();
        String KNOWLEDGE_INDEX="恋爱大师";
        DashScopeDocumentRetriever retriever = new DashScopeDocumentRetriever(dashScopeApi,
                DashScopeDocumentRetrieverOptions.builder()
                        .indexName(KNOWLEDGE_INDEX)
                        .build());

        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(retriever)
                .build();





    }





}
