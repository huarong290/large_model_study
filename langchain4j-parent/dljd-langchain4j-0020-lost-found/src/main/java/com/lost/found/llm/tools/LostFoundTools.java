package com.lost.found.llm.tools;

import com.lost.found.entity.LostItem;
import com.lost.found.mapper.LostItemMapper;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("lostFoundTools")
@Slf4j
public class LostFoundTools {

    @Autowired
    private LostItemMapper lostItemMapper;

    /**
     *
     * @param phone 手机号
     * @return 返回List<LostItem>
     */
    @Tool(name="queryByPhone",value = "根据失物人手机号查询所有登记的失物记录")
    public List<LostItem> queryByPhone(@P("失物人手机号") String phone){
    log.info("=========LostFoundTools中queryByPhone========= ");
       return lostItemMapper.selectByPhone(phone);
    }
}
