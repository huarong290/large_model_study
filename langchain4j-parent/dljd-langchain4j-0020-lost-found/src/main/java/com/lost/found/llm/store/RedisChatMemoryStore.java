package com.lost.found.llm.store;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * redis 存储对话记忆
 */
@Service("redisChatMemoryStore")
public class RedisChatMemoryStore implements ChatMemoryStore {
    /**
     * 对话记忆的key
     */
    private static final String CHAT_STORE_KEY = "CHAT:STORE:";
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        // 查询redis 获取某个memoryId的所有对话json
       String messageJson= stringRedisTemplate.opsForValue().get(CHAT_STORE_KEY+memoryId.toString());
        if(StringUtils.hasText(messageJson)){
            //反序列化
            return ChatMessageDeserializer.messagesFromJson(messageJson);
        }
        return List.of();
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        String key = CHAT_STORE_KEY+memoryId.toString();

        // 使用json格式存储chat message 对象存储需要序列化
        String messageJson = ChatMessageSerializer.messagesToJson(messages);
        stringRedisTemplate.opsForValue().set(key,messageJson,2, TimeUnit.HOURS);
    }

    @Override
    public void deleteMessages(Object memoryId) {
        //删除消息
        stringRedisTemplate.delete(CHAT_STORE_KEY+memoryId.toString());
    }
}
