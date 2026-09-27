package com.kaoyu.laiagent.rag;

import com.kaoyu.laiagent.demo.extend.MyKeyWordEnrich;
import com.kaoyu.laiagent.demo.extend.MyTokenTextSplit;
import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

/**
 * 自定义的rag知识库本地内存服务配置类
 */
@Configuration
public class LoveAppVectorStoreConfig {

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    @Resource
    private MyTokenTextSplit myTokenTextSplit;

    @Resource
    private MyKeyWordEnrich myKeyWordEnrich;

    @Bean
    VectorStore loveAppVectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(embeddingModel)
                .build();
        //加载文档
        List<Document> documents = loveAppDocumentLoader.loadDocuments();
        //自定义的切分文档，可以自己设置参数，一般情况下不使用
        //documents = myTokenTextSplit.splitDocument(documents);

        //使用ai添加status标签
        //documents = myKeyWordEnrich.enrichDocument(documents);
        simpleVectorStore.add(documents);
        return simpleVectorStore;
    }



}
