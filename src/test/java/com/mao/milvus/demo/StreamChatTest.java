package com.mao.milvus.demo;

import com.mao.milvus.demo.client.ChatClientStream;
import com.mao.milvus.demo.client.chat.ChatCompletion;
import com.mao.milvus.demo.client.chat.Message;
import com.mao.milvus.demo.client.listener.ConsoleStreamListener;
import com.mao.milvus.demo.client.listener.SseStreamListener;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@SpringBootTest
@Slf4j
public class StreamChatTest {

    @Resource
    private ChatClientStream chatClientStream;

    @Test
    public void chatCompletions() {
        ConsoleStreamListener listener = new ConsoleStreamListener();
        Message message = Message.of("沧浪之水清兮");
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .messages(List.of(message))
                .build();
        chatClientStream.streamChatCompletion(chatCompletion, listener);

        //卡住测试
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
          log.error(e.getMessage(), e);
        }
    }

    @GetMapping("/chat/sse")
    @CrossOrigin
    public SseEmitter sseEmitter(String prompt) {

        SseEmitter sseEmitter = new SseEmitter(-1L);

        SseStreamListener listener = new SseStreamListener(sseEmitter);
        Message message = Message.of(prompt);

        listener.setOnComplate(msg -> {
            //回答完成，可以做一些事情
        });
        chatClientStream.streamChatCompletion(List.of(message), listener);


        return sseEmitter;
    }

}
