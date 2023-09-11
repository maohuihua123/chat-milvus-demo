package com.mao.milvus.demo;

import com.mao.milvus.demo.client.ChatClient;
import com.mao.milvus.demo.client.embeddings.EmbeddingResponse;
import com.mao.milvus.demo.client.embeddings.Usage;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ChatClientTest {
    @Resource
    private ChatClient chatClient;


    @Test
    void embeddingsTest(){
        EmbeddingResponse embeddings = chatClient.embeddings("embeddings");
        Usage usage = embeddings.getUsage();
        System.out.println(usage);
        System.out.println(embeddings.getData().get(0).getEmbedding().size());
    }

    @Test
    void chatLlamaTest(){
        String chat = chatClient.chatLlama("Please introduce yourself");
        System.out.println(chat);
    }

    @Test
    void chatCompletionTest(){
        String chat = chatClient.chatCompletion("Please introduce yourself");
        System.out.println(chat);
    }
}
