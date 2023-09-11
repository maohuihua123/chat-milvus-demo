package com.mao.milvus.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatParamVo {
    private String question;
    private String prompt;
    private String collection;
}
