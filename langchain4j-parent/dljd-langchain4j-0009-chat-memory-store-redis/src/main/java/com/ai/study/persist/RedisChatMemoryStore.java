package com.ai.study.persist;

import com.ai.study.entity.ChatMsg;
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

@Service
public class RedisChatMemoryStore implements ChatMemoryStore {

    private static final String CHAT_STORE_KEY = "CHAT:STORE";
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        // 从redis查询memoryId 对应的json
        String messageJson = stringRedisTemplate.opsForValue().get(CHAT_STORE_KEY+memoryId.toString());
        if(StringUtils.hasText(messageJson)){
            //饭序列化
           return ChatMessageDeserializer.messagesFromJson(messageJson);
        }
        return List.of();
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {

        // 使用json格式存储chat message 对象存储需要序列化
        String messageJson = ChatMessageSerializer.messagesToJson(messages);
        stringRedisTemplate.opsForValue().set(CHAT_STORE_KEY+memoryId.toString(),messageJson,2, TimeUnit.HOURS);
    }

    @Override
    public void deleteMessages(Object memoryId) {
        //删除消息
        stringRedisTemplate.delete(CHAT_STORE_KEY+memoryId.toString());
    }
}
