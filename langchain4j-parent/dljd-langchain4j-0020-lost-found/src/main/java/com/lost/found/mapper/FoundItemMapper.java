package com.lost.found.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lost.found.entity.FoundItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FoundItemMapper extends BaseMapper<FoundItem> {

    List<FoundItem> selectUnmatchedItems();
}