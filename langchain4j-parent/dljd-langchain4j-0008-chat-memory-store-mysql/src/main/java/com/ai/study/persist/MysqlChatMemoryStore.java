package com.ai.study.persist;

import com.ai.study.entity.ChatMsg;
import com.ai.study.mapper.ChatMsgMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class MysqlChatMemoryStore implements ChatMemoryStore {
    @Autowired
    private ChatMsgMapper chatMsgMapper;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        // 根据memoryId 查询当前用户当前所有的响应对话记忆
       ChatMsg chatMsg= chatMsgMapper.selectOne( new LambdaQueryWrapper<ChatMsg>()
                .eq(ChatMsg::getChatId,memoryId)
                .orderByAsc(ChatMsg::getId)
        );
        // json- list<ChatMsg>
        if(chatMsg !=null && StringUtils.hasText(chatMsg.getContent())){
            // 将整个对话 JSON 反序列化为 List<ChatMessage>
            return ChatMessageDeserializer.messagesFromJson(chatMsg.getContent());
        }
        return List.of();
    }

    /**
     *  每次向chatMemory添加新的ChatMessage时都会调用updateMessages()方法
     *  每次与LLM交互时，通常会发生两次：一次是添加新的UserMessage，一次是添加新的AiMessage
     * @param memoryId The ID of the chat memory.
     * @param messages List of messages for the specified chat memory, that represent the current state of the {@link ChatMemory}.
     *                 Can be serialized to JSON using {@link ChatMessageSerializer}.
     */
    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        // 根据memoryId 查询当前用户当前所有的响应对话记忆
        ChatMsg chatMsg= chatMsgMapper.selectOne( new LambdaQueryWrapper<ChatMsg>()
                .eq(ChatMsg::getChatId,memoryId)
                .orderByAsc(ChatMsg::getId)
        );

        ChatMsg storeChatMsg = Optional.ofNullable(chatMsg).orElse(new ChatMsg());
        storeChatMsg.setChatId((String) memoryId);
        // 序列化
        storeChatMsg.setContent(ChatMessageSerializer.messagesToJson(messages));
        if(chatMsg == null){
            // 更新时也需要设置 message_type
            storeChatMsg.setMessageType("CONVERSATION");
            chatMsgMapper.insert(storeChatMsg);
        }else{
            storeChatMsg.setMessageType("CONVERSATION"); // ✅ 设置消息类型
            chatMsgMapper.update(storeChatMsg,new LambdaQueryWrapper<ChatMsg>().eq(ChatMsg::getChatId,memoryId));
        }
    }

    /**
     * 每次调用 ChatMemory.clear()时，deleteMessages 都会被调用。如果你不使用这个功能，可以让这个方法留空
     * @param memoryId The ID of the chat memory.
     */
    @Override
    public void deleteMessages(Object memoryId) {
        chatMsgMapper.deleteById(new LambdaQueryWrapper<ChatMsg>()
                .eq(ChatMsg::getChatId,memoryId) );
    }
}
