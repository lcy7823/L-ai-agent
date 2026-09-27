package com.kaoyu.laiagent.rag;


import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;

import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType.COSINE_DISTANCE;
import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgIndexType.HNSW;

/**
 * 自定义的rag知识库数据库服务配置类
 * 我配置的docker的postgres数据库
 */
//@Configuration
public class PgVectorStoreConfig {

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    @Bean
    public VectorStore pgVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel, JdbcClient jdbcClient){

        PgVectorStore pgVectorStore = PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .dimensions(1024)
                .initializeSchema(true)
                .indexType(HNSW)
                .distanceType(COSINE_DISTANCE)
                .maxDocumentBatchSize(1000)
                .vectorTableName("vector_store")
                .schemaName("public")
                .build();
        //加载文档
        List<Document> documents = loveAppDocumentLoader.loadDocuments();
//        pgVectorStore.add(documents);

        //for循环分批次添加文档
        int batchSize = 10;
        for (int i = 0; i < documents.size(); i+=batchSize) {
            int end = Math.min(i+batchSize,documents.size());
            pgVectorStore.add(documents.subList(i,end));
        }
        return pgVectorStore;
    }



}
