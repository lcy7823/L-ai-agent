package com.kaoyu.laiagent.app;

import com.kaoyu.laiagent.advisor.MyLoggerAdvisor;
import com.kaoyu.laiagent.chatmemory.FileBaseMemory;
import com.kaoyu.laiagent.rag.extend.QueryRewrite;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
@Slf4j
public class LoveApp {



    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
            "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
            "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";


    public LoveApp(ChatModel dashscopeChatModel) {
        //初始化内存保存记忆
//        ChatMemory chatMemory = MessageWindowChatMemory.builder().maxMessages(10).build();

        //自定义持久化对话记忆
        String fileDir = System.getProperty("user.dir")+"/tmp/chat-memory";
        ChatMemory chatMemory = new FileBaseMemory(fileDir,10);
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                        //自定义日志，按需开启
//                        new MyLoggerAdvisor()
                        // 重读强化回答，按需开启
//                        new ReReadingAdvisor()
                )
                .build();
    }




    /**
     * 普通调用ai
     *
     */
    public String doChat(String message,String chatId){
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .chatResponse();
        String text = chatResponse.getResult().getOutput().getText();
        //查看token消耗
        log.info("打印消息：{}",text);
        return text;
    }




    record LoveReport(String title, List<String> suggestions) {
    }

    /**
     * 结构化输出
     *
     */
    public LoveReport doChatWithStructure(String message,String chatId){
        LoveReport loveReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成恋爱结果，标题位{用户名}的恋爱报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .entity(LoveReport.class);
        log.info("loveReport:{}",loveReport);
        return loveReport;
    }





    @Resource
    private VectorStore loveAppVectorStore;

    @Resource
    private VectorStore pgVectorStore;

    @Resource
    private Advisor loveAppRagCloudAdvisor;

    @Resource
    private QueryRewrite queryRewrite;

    /**
     * rag 检索增强
     *
     */
    public String doChatWithRag(String message,String chatId){
        //查询重写
        //String rewriteMessage=queryRewrite.doQueryRewrite(message);
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .advisors(new MyLoggerAdvisor())
                //自定义的rag本地内存知识库服务
               .advisors(QuestionAnswerAdvisor.builder(loveAppVectorStore).build())
                //云rag知识库服务
                //.advisors(loveAppRagCloudAdvisor)
                //使用pgvector的rag知识库服务
               //.advisors(QuestionAnswerAdvisor.builder(pgVectorStore).build())
                //添加状态标签字段筛选，例：已婚，恋爱，未婚
//                .advisors(LoveAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(
//                        "家庭",loveAppVectorStore
//                ))
                .call()
                .chatResponse();
        String text = chatResponse.getResult().getOutput().getText();
        //查看token消耗
        Usage tokenUsage = chatResponse.getMetadata().getUsage();
//        log.info("打印消息：{}",text);
        return text;
    }




    @Resource
    private ToolCallback[] allTools;

    /**
     * 工具调用
     *
     */
    public String doChatWithTools(String message,String chatId){
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .advisors(new MyLoggerAdvisor())
                .toolCallbacks(allTools)
                .call()
                .chatResponse();
        String text = chatResponse.getResult().getOutput().getText();
        //查看token消耗
        log.info("打印消息:{}",text);
        return text;
    }

    @Resource
    private ToolCallbackProvider toolCallbackProvider;

    /**
     * 工具调用
     *
     */
    public String doChatWithMcp(String message,String chatId){
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .advisors(new MyLoggerAdvisor())
                .toolCallbacks(toolCallbackProvider)
                .call()
                .chatResponse();
        String result = chatResponse.getResult().getOutput().getText();
        //查看token消耗
        log.info("打印 消息:{}",result);
        return result;
    }



    /**
     * 流式输出
     *
     */
    public Flux<String> doChatByStream(String message, String chatId){
        return chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .content();
    }




}