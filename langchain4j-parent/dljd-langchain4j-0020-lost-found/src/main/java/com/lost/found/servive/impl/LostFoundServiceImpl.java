package com.lost.found.servive.impl;

import cn.hutool.core.util.PhoneUtil;
import cn.hutool.json.JSONUtil;
import com.lost.found.constant.FoundStatus;
import com.lost.found.constant.LostStatus;
import com.lost.found.dto.FoundRegisterDTO;
import com.lost.found.dto.IntentionDTO;
import com.lost.found.dto.LostRegisterDTO;
import com.lost.found.dto.MatchResultDTO;
import com.lost.found.entity.FoundItem;
import com.lost.found.entity.LostItem;
import com.lost.found.llm.aiservice.FoundAssistant;
import com.lost.found.llm.aiservice.LostAssistant;
import com.lost.found.mapper.FoundItemMapper;
import com.lost.found.mapper.LostItemMapper;
import com.lost.found.servive.LostFoundService;
import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.bean.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LostFoundServiceImpl implements LostFoundService {

    @Autowired
    private LostAssistant lostAssistant;
    @Autowired
    private FoundAssistant foundAssistant;
    @Autowired
    private LostItemMapper lostItemMapper;
    @Autowired
    private FoundItemMapper foundItemMapper;

    @Override
    public String doWithLostFound(String sessionId, String userMessage) {
        log.info("===========doWithLostFound开始================sessionId={},userMessage={}",sessionId,userMessage);

        //调用意图分析方法
        IntentionDTO intentionDTO = lostAssistant.intention(sessionId,userMessage);
        log.info("===========doWithLostFound意图分析:{}",intentionDTO);

        String response = intentionDTO.getResponse();
        log.info("===========doWithLostFound意图LLM应答:{}",response);
        //判断分析结果意图分析：1.失物登记 2.招领登记(找到失物登记) 3.失物查询 4.其他
        switch (intentionDTO.getIntention()){
            case 1:
                log.info("===========doWithLostFound意图分析:1.失物登记");
                response = actionLostRegister(sessionId,userMessage,response);
                break;
            case 2:
                log.info("===========doWithLostFound意图分析:2.招领登记(找到失物登记)");
                response = actionFoundRegister(sessionId,userMessage);
                break;
            case 3:
                log.info("===========doWithLostFound意图分析:3.失物查询");
                response = queryMatchLostFound(sessionId,userMessage);
                break;
            case 4:
                log.info("===========doWithLostFound意图分析:{}",intentionDTO.getIntention());
                break;
            default:
                log.info("===========doWithLostFound意图分析:{}",intentionDTO.getIntention());

        }
        return response;
    }
    /**
     * 失物查询
     * @param sessionId 会话ID
     * @param phone 用户消息
     * @return 返回值
     */
    private String queryMatchLostFound(String sessionId, String phone) {

        // 1. 校验手机号
        if (!PhoneUtil.isPhone(phone)) {
            return "失物查询，必须提供失物人的手机号";
        }
        // 2. 从失物表查询该手机号的所有失物记录
        List<LostItem> lostItemList = lostItemMapper.selectByPhone(phone);
        if (lostItemList.isEmpty()) {
            return "根据您提供的手机号，暂时没有找到对应的失物登记记录。";
        }

        // 3. 查询所有未匹配的招领记录
        List<FoundItem> foundItemList = foundItemMapper.selectUnmatchedItems();
        if (foundItemList.isEmpty()) {
            return "当前暂无招领登记记录，请稍后再查询。";
        }
        // 5. 进行匹配
        List<MatchResultDTO> matchResultDTOList = new ArrayList<>();

        for(LostItem lostItem : lostItemList){
            for(FoundItem foundItem :foundItemList){
                String prompt = String.format(
                        "请判断以下物品是否为同一物品，给出匹配分数(0-1之间的小数)和匹配原因。\n" +
                                "失物信息：\n" +
                                "  物品名称：%s\n" +
                                "  丢失时间：%s\n" +
                                "  丢失地点：%s\n" +
                                "  物品特征：%s\n" +
                                "招领信息：\n" +
                                "  物品名称：%s\n" +
                                "  拾得时间：%s\n" +
                                "  拾得地点：%s\n" +
                                "  物品特征：%s\n" +
                                "请以JSON格式返回，包含以下字段：\n" +
                                "  matchScore: 匹配度分数(0-1之间的小数)\n" +
                                "  matchReason: 匹配原因\n" +
                                "  phone: 失物人联系手机号\n" +
                                "  lostName: 失物名称\n" +
                                "  response: 大模型返回输出内容",
                        lostItem.getLostName(), lostItem.getLostDateTime(),
                        lostItem.getLostLocation(), lostItem.getLostDescription(),
                        foundItem.getFindName(), foundItem.getFindDateTime(),
                        foundItem.getFindLocation(), foundItem.getFindDescription()
                );

                try{
                    // 调用 LLM 进行匹配判断
                    String outPut = lostAssistant.lostQuery(sessionId,prompt);
                    matchResultDTOList.add(JSONUtil.toBean(outPut,MatchResultDTO.class));
                } catch (Exception e) {
                    log.error("匹配查询失败: lostItemId={}, foundItemId={}",
                            lostItem.getId(), foundItem.getId(), e);

                }

            }

        }
        // 6. 筛选匹配度 >= 0.5 的结果
        List<MatchResultDTO> matchedList = matchResultDTOList.stream()
                .filter(m -> m.getMatchScore() >= 0.5)
                .sorted((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()))
                .collect(Collectors.toList());

        // 7. 构建返回结果
        if (matchedList.isEmpty()) {
            return "经过智能匹配，您登记的失物在招领库中没有找到匹配度较高的记录。\n" +
                    "建议您补充更多物品特征信息，或联系失物招领处咨询。";
        }
        StringBuilder result = new StringBuilder("🔍 **失物匹配结果**\n\n");
        for (MatchResultDTO match : matchedList) {
            result.append("📌 失物：").append(match.getLostName()).append("\n")
                    .append("  匹配度：").append(String.format("%.0f%%", match.getMatchScore() * 100)).append("\n")
                    .append("  匹配原因：").append(match.getMatchReason()).append("\n")
                    .append("  招领信息：").append(match.getResponse()).append("\n")
                    .append("  ---\n");
        }
        result.append("\n💡 请携带有效证件前往失物招领处认领。");

        return result.toString();
    }

    /**
     * 失物登记
     * @param sessionId 会话ID
     * @param userMessage 用户消息
     * @param response 大模型返回
     * @return 返回值
     */
    private String  actionLostRegister(String sessionId, String userMessage,String response) {
        LostRegisterDTO lostRegisterDTO = lostAssistant.lostRegister(sessionId, userMessage);
        String responseText= response;
        try{
            //判断登记是否完成
            if(lostRegisterDTO.getCompleteFlag()){
                // 区分是insert ｜ 还是update
                Long id = lostRegisterDTO.getId();;
                if(id !=null ){
                    // 更新
                    LostItem lostItem = BeanUtil.copyProperties(lostRegisterDTO, LostItem.class);
//
                    lostItemMapper.updateLostItem(lostItem);
                }else{
                    //实现存储到mysql lost_item
                    LostItem lostItem = new LostItem();
                    lostItem.setUserName(lostRegisterDTO.getUserName());
                    lostItem.setPhone(lostRegisterDTO.getPhone());
                    lostItem.setLostName(lostRegisterDTO.getLostName());
                    lostItem.setLostDateTime(lostRegisterDTO.getLostDateTime());
                    lostItem.setLostLocation(lostRegisterDTO.getLostLocation());
                    lostItem.setLostDescription(lostRegisterDTO.getLostDescription());
                    lostItem.setStatus(LostStatus.REGISTER);
                    lostItemMapper.insert(lostItem);
                }

                responseText +=  "\n\n 失物登记完成";
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseText += "\n\n 失物登记异常,请稍后再试";
        }
        return responseText;
    }

    /**
     * 招领登记
     * @param sessionId 会话ID
     * @param userMessage 用户消息
     *
     * @return 返回值
     */
    private String  actionFoundRegister(String sessionId, String userMessage) {
        //与LLM对话
       FoundRegisterDTO foundRegisterDTO = foundAssistant.foundRegister(sessionId,userMessage);
       String responseText = foundRegisterDTO.getResponse();
       try{
           if(foundRegisterDTO.getCompleteFlag()){
               //创建招领登记记录
               FoundItem foundItem = new FoundItem();
               foundItem.setFinderName(foundRegisterDTO.getFinderName());
               foundItem.setFinderPhone(foundRegisterDTO.getFinderPhone());
               foundItem.setFindName(foundRegisterDTO.getFindName());
               foundItem.setFindDateTime(foundRegisterDTO.getFindDateTime());
               foundItem.setFindLocation(foundRegisterDTO.getFindLocation());
               foundItem.setFindDescription(foundRegisterDTO.getFindDescription());
               foundItem.setStatus(FoundStatus.REGISTER);
               foundItemMapper.insert(foundItem);
               responseText +=  "\n\n 招领信息登记完成";
           }
       }catch (Exception e) {
            e.printStackTrace();
           responseText += "\n\n 招领信息登记异常,请稍后再试";
        }

        return  responseText;
    }
}
