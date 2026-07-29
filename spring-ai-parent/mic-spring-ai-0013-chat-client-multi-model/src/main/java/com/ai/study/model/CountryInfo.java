package com.ai.study.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 辅助类：国家信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountryInfo {
    private  String name;
    private  String area;
    private  String population;
    private  String ethnicGroups;
}
