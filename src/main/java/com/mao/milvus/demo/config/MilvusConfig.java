package com.mao.milvus.demo.config;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MilvusConfig {

    @Value("${milvus.url}")
    private String url;
    @Value("${milvus.token}")
    private String token;

    @Bean
    public MilvusServiceClient milvusServiceClient() {
        log.info("Connecting to DB: {}", url);
        ConnectParam connectParam = ConnectParam.newBuilder()
                .withUri(url)
                .withToken(token)
                .build();
        return new MilvusServiceClient(connectParam);
    }
}
