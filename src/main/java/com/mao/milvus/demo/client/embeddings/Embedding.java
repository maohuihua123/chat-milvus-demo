package com.mao.milvus.demo.client.embeddings;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

@Getter
@Slf4j
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class Embedding implements Serializable {
    @NonNull
    private List<String> input;

    @NonNull
    @JsonProperty("user_id")
    private String userId;
}
