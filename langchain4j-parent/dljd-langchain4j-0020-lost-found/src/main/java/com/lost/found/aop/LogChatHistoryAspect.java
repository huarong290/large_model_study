package com.lost.found.aop;

import com.lost.found.constant.ConstantKey;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Aspect
@Component
public class LogChatHistoryAspect {

    private final String  TYPE_ROLE_USER = "USER";
    private final String  TYPE_ROLE_AI = "AI";
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Around("@annotation(com.lost.found.annotation.LogChatHistory)")
    public Object chatAround(ProceedingJoinPoint proceedingJoinPoint){

        try{
            // 获取业务方法的参数
            Object[] args = proceedingJoinPoint.getArgs();
            if(args !=null && args.length > 1){
                String sessionId = (String) args[0];
                String message = (String) args[1];

                // 记录到对话消息  消息队列: 把session 和 message 存入MQ

                recordChatLog(sessionId,message,TYPE_ROLE_USER);
                // 调用业务方法
                Object returnObject = proceedingJoinPoint.proceed();
                if( returnObject != null){
                    message = (String)returnObject;
                    recordChatLog(sessionId,message,TYPE_ROLE_AI);
                }
                return returnObject;
            }else {
                Object returnObject = proceedingJoinPoint.proceed();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return  null ;
    }

    private void recordChatLog(String sessionId, String message, String typeRole) {

        MapRecord<String,String,String> mapRecord = MapRecord.create(
                ConstantKey.CHAT_LOG_STREAM, Map.of("chat_id",sessionId,"msg",message,"role",typeRole)
        );
        stringRedisTemplate.opsForStream().add(mapRecord);
    }
}
