package com.mao.milvus.demo.client.embeddings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmbeddingData implements Serializable {
    /**
     * 固定值 embedding
     */
    private String object;

    /**
     * embedding 内容
     */
    private List<Float> embedding;

    /**
     * 序号
     */
    private Integer index;
}
