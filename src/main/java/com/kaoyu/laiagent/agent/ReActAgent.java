package com.kaoyu.laiagent.agent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * ReAct (Reasoning and Acting) 模式的代理抽象类
 * 实现了思考->行动的循环模式
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class ReActAgent extends BaseAgent {

    /**
     * 思考模式
     * 处理当前状态并决定是否进行下一步
     * @return 是否行动，true可以执行，false不需要执行
     */
    public abstract boolean think();

    /**
     * 执行决定的行动
     * @return 执行行动返回的结果
     */
    public abstract String act();


    /**
     * 单个步骤执行过程  思考->行动
     *
     * @return 步骤执行结果
     */
    @Override
    public String step() {
        try {
            boolean think = think();
            if (!think) {
                return "思考完成 - 无需行动";
            }
            return act();
        } catch (Exception e) {
            log.error("思考或执行error: {}", e.getMessage());
            return "步骤执行失败：" + e.getMessage();
        }
    }
}
