package com.mao.milvus.demo.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchResultVo {
    private Long id;
    private Float score;
    private String conent;
}
