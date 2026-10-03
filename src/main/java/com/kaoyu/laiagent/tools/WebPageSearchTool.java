package com.kaoyu.laiagent.tools;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.HashMap;
import java.util.Map;

/**
 * 网页搜索工具
 */
public class WebPageSearchTool {

    //搜索的api地址
    private static final String SEARCH_API_URL = "https://www.searchapi.io/api/v1/search";

    private final String apiKey;


    public WebPageSearchTool(String apiKey) {
        this.apiKey = apiKey;
    }


    /**
     * 网页搜索工具
     */
    @Tool(description = "search the web for information")
    public String search(@ToolParam(description = "search query keyword") String query) {
        try {
            //构建请求参数
            Map<String, Object> params = new HashMap<>();
            params.put("q", query);
            params.put("api_key", apiKey);
            params.put("engine", "baidu");

            //发送HTTP请求
            String response = HttpUtil.get(SEARCH_API_URL, params);

            //解析返回结果
            JSONObject jsonObject = JSONUtil.parseObj(response);
            JSONArray organicResults = jsonObject.getJSONArray("organic_results");

            if (organicResults == null || organicResults.isEmpty()) {
                return "No search results found";
            }

            //格式化搜索结果
            StringBuilder result = new StringBuilder();
            result.append("Search results for: ").append(query).append("\n\n");

            for (int i = 0; i < 5; i++) {
                JSONObject item = organicResults.getJSONObject(i);
                Integer position = item.getInt("position");
                String title = item.getStr("title");
                String link = item.getStr("link");
                String snippet = item.getStr("snippet");

                result.append(position).append(". ").append(title).append("\n");
                result.append("URL: ").append(link).append("\n");
                result.append("Summary: ").append(snippet).append("\n\n");
            }

            return result.toString();
        } catch (Exception e) {
            return "Error searching: " + e.getMessage();
        }
    }


}