package com.ai.study.tools;

import com.ai.study.records.NumberRecord;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component("calculateTools")
@Slf4j
public class CalculateTools {

    @Tool(name="funcAddNumber",value = "计算两个整数相加,返回整数。不能处理浮点数")
    public int add(int a ,int b){

        log.info("calculateTools 中add()方法执行了");
        return  a+b;
    }

    /**
     * 计算两个整数相乘
     * @param numberRecord 参数类型
     * @return 返回结果
     */
    @Tool(name ="funcMultiply",value="计算两个整数相乘，返回整数。不能处理浮点数")
    public int multiply(@P(value = "包含两个整数的对象") NumberRecord numberRecord){
        log.info("calculateTools multiply()方法执行了");
        return numberRecord.num1()* numberRecord.num2();
    }

    /**
     *
     * @param chatId  会话ID
     * @param orderId 订单号
     * @return 返回结果
     */
    @Tool(name ="queryOrder",value="根据订单号查询订单的状态，返回订单说明")
    public String queryOrder(@ToolMemoryId String chatId,@P(value = "订单号，由数字和字母组成",required = true) String orderId){
        log.info("calculateTools queryOrder()方法执行了chatId={},orderId={}",chatId,orderId);

        return "订单是存在的";
    }
}
