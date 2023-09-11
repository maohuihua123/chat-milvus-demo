package com.mao.milvus.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatResultVo {
    private String search;
    private String answer;
    @Builder.Default
    private List<String> tags = Collections.singletonList("assistant");
}
