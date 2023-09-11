package com.mao.milvus.demo.config;

import com.mao.milvus.demo.client.ChatClient;
import com.mao.milvus.demo.client.ChatClientStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ChatConfig {

    @Value("${chat.key:GNRPnTZfsC5G6mkjfgI0EGgi}")
    private String apiKey;
    @Value("${chat.secret:OFWG4DyhUBpFHX0NXPv29RVSpPY8ajA4}")
    private String secretKey;

    @Bean
    public ChatClient chatClientConfig(){
        return ChatClient.builder()
                .clientId(apiKey)
                .clientSecret(secretKey)
                .build()
                .init();
    }

    @Bean
    public ChatClientStream chatClientStream(){
        return ChatClientStream.builder()
                .clientId(apiKey)
                .clientSecret(secretKey)
                .timeout(600)
                .build()
                .init();
    }
}
