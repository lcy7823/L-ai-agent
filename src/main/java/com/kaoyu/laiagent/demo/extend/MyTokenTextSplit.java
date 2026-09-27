package com.kaoyu.laiagent.demo.extend;


import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 自定义的文档切分器
 */
@Component
public class MyTokenTextSplit {


    public List<Document> splitDocument(List<Document> documents){
        TokenTextSplitter splitter = new TokenTextSplitter();
        return splitter.apply(documents);
    }

    public List<Document> splitCustom(List<Document> documents){
        TokenTextSplitter splitter = new TokenTextSplitter(200,100,10,5000,true,null);
        return splitter.apply(documents);
    }






}
