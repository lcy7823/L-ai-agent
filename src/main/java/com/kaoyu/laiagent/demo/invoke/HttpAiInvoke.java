package com.kaoyu.laiagent.demo.invoke;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

public class HttpAiInvoke {

    // 阿里云百炼 API Key（控制台获取：https://bailian.console.aliyun.com/）
    private static final String API_KEY = TestApiKey.API_KEY;
    // 百炼文本生成原生接口
    private static final String URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

    public static void main(String[] args) {
        // ---------- 1. 构造请求体 ----------
        JSONObject body = new JSONObject();
        body.set("model", "qwen-plus");

        JSONObject input = new JSONObject();
        JSONObject systemMsg = new JSONObject();
        systemMsg.set("role", "system");
        systemMsg.set("content", "You are a helpful assistant.");

        JSONObject userMsg = new JSONObject();
        userMsg.set("role", "user");
        userMsg.set("content", "你是哪个模型");

        input.set("messages", new JSONObject[]{systemMsg, userMsg});
        body.set("input", input);

        JSONObject parameters = new JSONObject();
        parameters.set("result_format", "message");  // 返回结构化 message
        parameters.set("temperature", 0.8);          // 采样温度，可选
        parameters.set("top_p", 0.8);                // 可选
        body.set("parameters", parameters);

        // ---------- 2. 用 Hutool 发送 POST ----------
        try (HttpResponse response = HttpRequest.post(URL)
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .body(body.toString())
                .timeout(60000)                      // 60秒超时
                .execute()) {

            System.out.println("HTTP状态码: " + response.getStatus());
            String result = response.body();
            System.out.println("原始响应: " + result);

            // ---------- 3. 解析返回内容 ----------
            JSONObject resp = JSONUtil.parseObj(result);
            JSONObject output = resp.getJSONObject("output");
            String content = output.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getStr("content");
            System.out.println("模型回答: " + content);
        }
    }
}
