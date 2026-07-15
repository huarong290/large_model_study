package com.lost.found.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lost.found.entity.LostItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LostItemMapper extends BaseMapper<LostItem> {

    /**
     * 根据手机号查询
     */
    List<LostItem> selectByPhone(@Param("phone") String phone);

    /**
     *
     * @param
     * @return
     */
    int updateLostItem(LostItem lostItem);

    /**
     * 动态更新（只更新传入的非空字段）
     */
    int updateLostItemSelective(LostItem lostItem);
}
