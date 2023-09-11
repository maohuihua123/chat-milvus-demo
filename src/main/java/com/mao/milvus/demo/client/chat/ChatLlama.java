package com.mao.milvus.demo.client.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Data
@Builder
@Slf4j
@AllArgsConstructor
@NoArgsConstructor(force = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatLlama {
    @NonNull
    @Builder.Default
    private String chatEndpoint = Model.LLAMA_2_13B.getName();
    /**
     * 聊天上下文信息。说明：
     * （1）messages成员不能为空，1个成员表示单轮对话，多个成员表示多轮对话
     * （2）最后一个message为当前请求的信息，前面的message为历史对话信息
     * （3）必须为奇数个成员，成员中message的role必须依次为user、assistant
     * （4）最后一个message的content长度（即此轮对话的问题）不能超过2000个字符；
     *      如果messages中content总长度大于2000字符，系统会依次遗忘最早的历史会话，
     *      直到content的总长度不超过2000个字符
     */
    @NonNull
    private List<Message> messages;

    /**
     * 是否流式输出
     */
    @Builder.Default
    private boolean stream = false;

    /**
     * 用户唯一值，可以监视和检测滥用行为，防止接口恶意调用
     */
    @JsonProperty("user_id")
    private String userId;

    @Getter
    @AllArgsConstructor
    public enum Model {
        LLAMA_2_7B("llama_2_7b"),
        LLAMA_2_13B("llama_2_13b"),
        LLAMA_2_70B("llama_2_70b"),
        ;
        private final String name;
    }

}
