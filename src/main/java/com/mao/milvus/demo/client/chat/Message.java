package com.mao.milvus.demo.client.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message {
    /**
     * 当前支持以下：
     * user: 表示用户
     * assistant: 表示对话助手
     */
    private String role;
    private String content;

    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public static Message of(String content) {

        return new Message(Role.USER.getValue(), content);
    }

    public static Message ofAssistant(String content) {

        return new Message(Role.ASSISTANT.getValue(), content);
    }

    @Getter
    @AllArgsConstructor
    public enum Role {

        USER("user"),
        ASSISTANT("assistant"),
        ;
        private final String value;
    }

}
