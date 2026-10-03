package com.kaoyu.imagesearchmcp.tools;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageSearchTool {

    private static final String IMAGE_API_KEY = ApiKey.IMAGE_API_KEY;

    private static final String IMAGE_SEARCH_URL = "https://api.pexels.com/v1/search";

    @Tool(description = "Search for images on Pexels. Returns a list of image URLs and metadata based on the search query.")
    public String searchImageTool(@ToolParam(description = "Search query for finding images, e.g., 'nature', 'city', 'animals'") String query) {
        try {
            return String.join(",", searchMediumImage(query));
        } catch (Exception e) {
            return "Error searching image: " + e.getMessage();
        }
    }

    private List<String> searchMediumImage(String query) {

        String responseBody = HttpUtil.createGet(IMAGE_SEARCH_URL)
                .header("Authorization", IMAGE_API_KEY)
                .form("query",query)
                .timeout(10000)
                .execute()
                .body();

        JSONObject json = JSONUtil.parseObj(responseBody);
        int totalResults = json.getInt("total_results", 0);

        if (totalResults == 0) {
            return List.of("No images found for query: " + query);
        }

        return json.getJSONArray("photos").toList(JSONObject.class).stream()
                .map(photo -> photo.getJSONObject("src").getStr("medium"))
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());
    }
}