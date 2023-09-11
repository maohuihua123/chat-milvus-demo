package com.mao.milvus.demo.vo;

import com.mao.milvus.demo.utils.Content;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SearchParamVo {
    private String collectionName;
    private List<List<Float>> queryVectors;
    @Builder.Default
    private Integer topK = 5;
    @Builder.Default
    private List<String> outputFields = List.of(Content.Field.CONTENT);
    @Builder.Default
    private String params = "{\"nprobe\":10}";
    private String expr;
}
