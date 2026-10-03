package com.kaoyu.laiagent.tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

public class WebScrapeTool {


    @Tool(description = "Scrape and fetch the HTML content of a web page given its URL")
    public String scrapeWebPage(@ToolParam(description = "The URL of the web page to scrape") String url){
        try {
            Document document = Jsoup.connect(url).get();
            return document.html();
        }catch (Exception e){
            return "Error web page: " + e.getMessage();
        }
    }
}