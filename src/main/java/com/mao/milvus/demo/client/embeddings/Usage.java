package com.mao.milvus.demo.client.embeddings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Usage implements Serializable {

    @JsonProperty("prompt_tokens")
    private long promptTokens;

    @JsonProperty("total_tokens")
    private long totalTokens;

}
