package com.mao.milvus.demo.client.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@Slf4j
@AllArgsConstructor
@NoArgsConstructor(force = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatCompletion implements Serializable {

    @NonNull
    private List<Message> messages;
    /**
     * 使用什么取样温度，0到2之间。越高越奔放。越低越保守。
     * <p>
     * 不要同时改这个和topP
     */
    @Builder.Default
    private double temperature = 0.9;

    /**
     * 0-1
     * 建议0.9
     * 不要同时改这个和temperature
     */
    @JsonProperty("top_p")
    @Builder.Default
    private double topP = 0.9;

    /**
     * 通过对已生成的token增加惩罚，减少重复生成的现象。说明：
     * （1）值越大表示惩罚越大
     * （2）默认1.0，取值范围：[1.0, 2.0]
     */
    @JsonProperty("penalty_score")
    @Builder.Default
    private double penaltyScore = 1.3;

    /**
     * 是否流式输出.
     * default:false
     */
    @Builder.Default
    private boolean stream = false;

    /**
     * 用户唯一值，确保接口不被重复调用
     */
    @JsonProperty("user_id")
    private String userId;
}


