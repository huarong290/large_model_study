package com.ai.study.mapper;

import com.ai.study.entity.ChatMsg;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatMsgMapper extends BaseMapper<ChatMsg> {
}
