package com.kaoyu.laiagent.chatmemory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于文件持久化的对话记忆
 */
public class FileBaseMemory implements ChatMemory {

    private final String BASE_DIR;
    private final int lastN;
    private static final Kryo kryo=new Kryo();

    static {
        kryo.setRegistrationRequired(false);
        //设置策略
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }


    /**
     * 文件不存在就创建
     */
    public FileBaseMemory(String baseDir,int lastN) {
        this.BASE_DIR = baseDir;
        this.lastN=lastN;
        File file = new File(baseDir);
        if (!file.exists()){
            boolean mkdirs = file.mkdirs();
        }
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        List<Message> messageList = getOrCreateConversation(conversationId);
        messageList.addAll(messages);
        saveConversationFile(conversationId,messageList);
    }

    @Override
    public List<Message> get(String conversationId) {
        List<Message> messageList = getOrCreateConversation(conversationId);
        return messageList.stream()
                .skip(Math.max(0,messageList.size()-lastN))
                .toList();
    }

    @Override
    public void clear(String conversationId) {
        File file = getConversationFile(conversationId);
        if (file.exists()){
            boolean delete = file.delete();
        }
    }

    private List<Message> getOrCreateConversation(String conversationId){
        File file = getConversationFile(conversationId);
        List<Message> messages = new ArrayList<>();
        if (file.exists()){
            try (Input input=new Input(new FileInputStream(file))){
                messages = kryo.readObject(input, ArrayList.class);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return messages;
    }




    private void saveConversationFile(String conversationId,List<Message> messages){
        File file = getConversationFile(conversationId);
        try (Output output = new Output(new FileOutputStream(file))) {
            kryo.writeObject(output,messages);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    private File getConversationFile(String conversationId){
        return new File(BASE_DIR,conversationId+".kryo");
    }

}
