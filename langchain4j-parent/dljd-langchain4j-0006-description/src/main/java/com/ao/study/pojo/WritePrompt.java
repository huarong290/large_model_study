package com.ao.study.pojo;

import dev.langchain4j.model.input.structured.StructuredPrompt;
import lombok.Data;

@StructuredPrompt(""" 
        请以{{style}}的风格，写一篇关于{{topic}}的文章，字数{{length}},语言{{lang}}
        """)
@Data
public class WritePrompt {
    /**
     * 幽默/正式/简洁
     */
    private String style;
    /**
     * 主题
     */
    private String topic;
    /**
     * 字数
     */
    private int length;
    /**
     * 中文/英文
     */
    private String lang;
}
