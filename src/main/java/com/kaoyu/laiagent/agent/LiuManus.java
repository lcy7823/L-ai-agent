package com.kaoyu.laiagent.agent;

import com.kaoyu.laiagent.advisor.MyLoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

@Component
public class LiuManus extends ToolCallAgent {


    public LiuManus(ToolCallback[] allTools, ChatModel dashscopeChatModel) {
        super(allTools);
        setName("LiuManus");
        setMaxSteps(20);

        /*
         * 下一步提示词：指导AI如何分析需求、选择工具、分解复杂任务、汇报执行结果，以及如何使用terminate工具结束对话
         *
         * 分析用户的请求，选择最合适的工具或工具组合来继续执行。
         * 对于复杂任务，将其分解为更小的步骤，并依次应用不同的工具。
         * 每次工具执行后，提供清晰的结果总结，并建议下一步行动。
         * 如果需要结束对话，请调用 `terminate` 工具。
         * 请使用英文回复所有思考过程和结果。
         */
        String NEXT_PROMPT = """
                Analyze the user's request and select the most appropriate tool or combination of tools to proceed.

                For complex tasks, break them down into smaller steps and apply different tools sequentially.

                After each tool execution, provide a clear summary of the results and suggest the next action.

                If you need to end the conversation, please call the `terminate` tool.

                Please respond with all thinking processes and results in English.
                """;

        /*
         * 系统提示词：定义AI助手的身份（LiuManus）及其核心能力（全能助手，配备多种工具高效完成复杂任务）
         *
         * 你是 LiuManus，一个多功能AI助手，旨在处理用户提出的任何任务。
         * 你配备了多种工具，可以高效地完成复杂请求。
         * 重要要求：
         * 1. 所有思考过程必须使用英文
         * 2. 所有工具调用结果必须使用英文展示
         * 3. 所有总结和回复必须使用英文
         * 4. 遇到其他语言内容时，请翻译为英文后展示
         */
        String SYSTEM_PROMPT = """
                You are LiuManus, a versatile AI assistant designed to handle any task presented by the user.

                You are equipped with a variety of tools that allow you to efficiently complete complex requests.

                Important requirements:
                1. All thinking processes must be in English
                2. All tool call results must be displayed in English
                3. All summaries and replies must be in English
                4. When encountering content in other languages, please translate it into English before displaying
                """;
        setSystemPrompt(SYSTEM_PROMPT);

        //初始化客户端
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(new MyLoggerAdvisor())
                .build();
        setChatClient(chatClient);
    }
}