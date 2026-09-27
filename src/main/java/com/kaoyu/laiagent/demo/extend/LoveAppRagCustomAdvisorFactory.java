package com.kaoyu.laiagent.demo.extend;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

/**
 * 自定义RAG顾问工厂
 * 用于创建自定义的RAG顾问，根据状态标签筛选文档
 */
@Slf4j
public class LoveAppRagCustomAdvisorFactory {

    public static Advisor createLoveAppRagCustomAdvisor(String status, VectorStore vectorStore){
        //实例化筛选条件
        Filter.Expression expression = new FilterExpressionBuilder()
                .eq("status", status)
                .build();
        VectorStoreDocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .filterExpression(expression)//筛选条件
                .similarityThreshold(0.5)//相似度阈值
                .topK(3)//返回文档数量
                .build();
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .queryAugmenter(LoveAppRagContextualQueryAugmentFactory.createInstance())
                .build();
    }



}
