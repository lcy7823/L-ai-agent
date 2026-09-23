package com.kaoyu.laiagent.advisor;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义 Re2 Advisor
 * 可提高大型语言模型的推理能力
 */
public class ReReadingAdvisor implements CallAdvisor, StreamAdvisor {
    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        return callAdvisorChain.nextCall(this.before(chatClientRequest));
    }

    private ChatClientRequest before(ChatClientRequest chatClientRequest) {
        Prompt oldPrompt = chatClientRequest.prompt();
        UserMessage oldUserMsg = oldPrompt.getUserMessage();
        String originalQuery = oldUserMsg.getText();

        Map<String, Object> newContext = new HashMap<>(chatClientRequest.context());
        newContext.put("re2_input_query", originalQuery);

        Prompt newPrompt = oldPrompt.augmentUserMessage(prevMsg -> {
            String newText = """
                    {re2_input_query}
                    Read the question again: {re2_input_query}
                    """.replace("{re2_input_query}", originalQuery);
            return new UserMessage(newText);
        });

        return chatClientRequest.mutate()
                .prompt(newPrompt)
                .context(newContext)
                .build();
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        return streamAdvisorChain.nextStream(this.before(chatClientRequest));
    }


    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
