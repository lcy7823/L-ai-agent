package com.kaoyu.laiagent.agent;

import cn.hutool.core.util.StrUtil;
import com.kaoyu.laiagent.agent.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽象基础代理类，管理执行流程和代理状态
 * <p>
 * 状态转换、内存管理和基于步骤的执行流程循环的基础功能
 * step()方法：执行单个步骤，子类必须实现
 */
@Data
@Slf4j
public abstract class BaseAgent {

    // 子类实现的智能体名称
    private String name;

    //系统提示词、下一步提示词
    private String systemPrompt;
    private String nextStepPrompt;

    //状态
    private AgentState state = AgentState.IDLE;

    //当前步骤和最大步骤
    private int currentStep = 0;
    private int maxSteps = 20;

    //大模型LLM
    private ChatClient chatClient;

    //memory 对话记忆
    private List<Message> messageList = new ArrayList<>();


    /**
     * 运行代理
     */
    public String run(String userPrompt) {
        if (this.state != AgentState.IDLE) {
            throw new RuntimeException("Cannot run agent from state: " + this.state);
        }
        if (StrUtil.isBlank(userPrompt)) {
            throw new RuntimeException("cannot run agent with empty user prompt");
        }
        //更改状态
        state = AgentState.RUNNING;
        //记录上下文
        messageList.add(new UserMessage(userPrompt));
        //记录结果列表
        List<String> results = new ArrayList<>();

        try {
            for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                int stepNumber = i + 1;
                currentStep = stepNumber;
                log.info("executing step {}/{}", stepNumber, maxSteps);
                //执行单步操作
                String stepResult = step();
                String result = "step" + stepNumber + ": " + stepResult;
                results.add(result);
            }
            //检查是否超出步骤限制
            if (currentStep >= maxSteps) {
                state = AgentState.FINISHED;
                results.add("Terminated : Reached max steps (" + maxSteps + ")");
            }
            //将每个列表中字符串用换行符连接
            return String.join("\n", results);
        } catch (Exception e) {
            state = AgentState.FINISHED;
            log.error("Error executing agent ", e);
            return "执行错误：" + e.getMessage();
        } finally {
            //清理资源
            cleanup();
        }
    }

    /**
     * 执行单个步骤
     *
     * @return 单个步骤执行结果
     */
    public abstract String step();

    /**
     * 清理资源
     * 子类重写实现
     */
    protected void cleanup() {
    }


}
