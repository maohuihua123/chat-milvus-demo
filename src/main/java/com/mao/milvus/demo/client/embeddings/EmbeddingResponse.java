package com.mao.milvus.demo.client.embeddings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmbeddingResponse implements Serializable {
    /**
     * 本轮对话的id
     */
    private String id;

    /**
     * 回包类型, 固定值"embedding_list"
     */
    private String object;

    /**
     * 时间戳
     */
    private long created;

    /**
     * embedding信息, data成员数和文本数量保持一致
     */
    private List<EmbeddingData> data;

    /**
     * token统计信息, token数 = 汉字数+单词数*1.3 (仅为估算逻辑)
     */
    private Usage usage;
}
