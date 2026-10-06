package com.kaoyu.laiagent.agent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.kaoyu.laiagent.agent.model.AgentState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class ToolCallAgent extends ReActAgent {

    //可用的工具
    private final ToolCallback[] availableTools;

    //工具的响应
    private ChatResponse toolChatResponse;

    //工具的管理者
    private final ToolCallingManager toolCallingManager;

    //内置的工具调用机制，自己维护上下文
    private final ChatOptions chatOptions;


    public ToolCallAgent(ToolCallback[] availableTools) {
        super();
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        //禁用spring ai内置的工具调用机制，自己维护消息和工具执行
        this.chatOptions = DashScopeChatOptions.builder()
                .internalToolExecutionEnabled(false)
                .build();
    }

    /**
     * 处理当前状态并决定下一步行动
     *
     * @return 是否执行act行动
     */
    @Override
    public boolean think() {
        if (StrUtil.isNotBlank(getNextStepPrompt())) {
            UserMessage userMessage = new UserMessage(getNextStepPrompt());
            getMessageList().add(userMessage);
        }
        List<Message> messageList = getMessageList();
        Prompt prompt = new Prompt(messageList, chatOptions);
        try {
            ChatResponse chatResponse = getChatClient().prompt(prompt)
                    .toolCallbacks(availableTools)
                    .system(getSystemPrompt())
                    .call()
                    .chatResponse();
            this.toolChatResponse = chatResponse;
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            //提示信息
            String result = assistantMessage.getText();
            List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();
            //输出相关信息
            log.info("{} 的思考过程: {}", getName(), result);
            log.info("{} 使用了{}个工具", getName(), toolCallList.size());
            String toolCallInfo = toolCallList.stream()
                    .map(toolCall -> String.format("工具名称: %s, 工具参数: %s",
                            toolCall.name(),
                            toolCall.arguments())
                    )
                    .collect(Collectors.joining("\n"));
            log.info(toolCallInfo);
            if (toolCallList.isEmpty()) {
                //不调用工具，所以需要记录
                getMessageList().add(assistantMessage);
                return false;
            } else {
                //调用工具，不需要记录，act过程会记录
                return true;
            }
        } catch (Exception e) {
            log.error("{} 思考遇到问题: {}", getName(), e.getMessage());
            getMessageList().add(
                    new AssistantMessage("处理时遇到问题" + e.getMessage())
            );
            return false;
        }
    }

    /**
     * 执行工具调用并返回结果
     *
     * @return 返回执行结果
     */
    @Override
    public String act() {
        if (!toolChatResponse.hasToolCalls()) {
            return "没有工具调用";
        }
        //调用工具
        Prompt prompt = new Prompt(getMessageList(), chatOptions);
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolChatResponse);
        //记录上下文
        setMessageList(toolExecutionResult.conversationHistory());
        //提取当前工具调用结果
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory());
        String resultInfo = toolResponseMessage.getResponses().stream()
                .map(response -> String.format("工具 %s 完成了任务, 结果是 %s",
                        response.name(),
                        response.responseData())
                )
                .collect(Collectors.joining("\n"));
        //判断是否调用终止工具
        boolean resultTerminate = toolResponseMessage.getResponses().stream()
                .anyMatch(response -> "doTerminate".equals(response.name()));
        if (resultTerminate){
            setState(AgentState.FINISHED);
        }
        //打印出信息方便调试
        log.info(resultInfo);
        return resultInfo;
    }
}
