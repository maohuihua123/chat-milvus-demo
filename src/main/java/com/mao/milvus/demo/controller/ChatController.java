package com.mao.milvus.demo.controller;

import com.mao.milvus.demo.client.ChatClient;
import com.mao.milvus.demo.client.chat.ChatCompletion;
import com.mao.milvus.demo.client.chat.ChatResponse;
import com.mao.milvus.demo.client.chat.Message;
import com.mao.milvus.demo.client.embeddings.EmbeddingResponse;
import com.mao.milvus.demo.service.MilvusService;
import com.mao.milvus.demo.utils.Content;
import com.mao.milvus.demo.vo.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin
@Slf4j
public class ChatController {
    @Resource
    private ChatClient chatClient;
    @Resource
    private MilvusService milvusService;

    @PostMapping("/chat")
    public ReplyMsg chat(@RequestBody ChatParamVo chatParamVo) {
        try {
            // 0.加载向量集合
            milvusService.loadCollection(Content.COLLECTION_NAME);
            String question = chatParamVo.getQuestion();
            // 1.对问题进行嵌入
            EmbeddingResponse embeddings = chatClient.embeddings(question);
            List<Float> vectors = embeddings.getData().get(0).getEmbedding();
            SearchParamVo searchParamVo = SearchParamVo.builder()
                    .collectionName(Content.COLLECTION_NAME)
                    .queryVectors(Collections.singletonList(vectors))
                    .topK(4)
                    .build();
            // 2.在向量数据库中进行搜索内容知识
            List<List<SearchResultVo>> lists = milvusService.searchTopKSimilarity(searchParamVo);
            StringBuffer buffer = new StringBuffer();
            lists.forEach(searchResultVos -> {
                searchResultVos.forEach(searchResultVo -> {
                    buffer.append(searchResultVo.getConent());
                });
            });
            // 3.构造问答内容
            String content = buffer.toString();
            String chatContent = chatParamVo.getPrompt() + content + question;
            // 4.进行问答
            ChatCompletion chatCompletion = ChatCompletion.builder()
                    .messages(Collections.singletonList(Message.of(chatContent)))
                    .temperature(0.9)
                    .build();
            ChatResponse response = chatClient.chatCompletion(chatCompletion);
            // 5.输出答案
            String answer = response.getResult();
            ChatResultVo data = ChatResultVo.builder().search(question).answer(answer).build();
            log.info("AI Answer: {}", answer);
            return ReplyMsg.ofSuccess(data);
        } catch (Exception e) {
            ChatResultVo data = ChatResultVo.builder()
                    .search(chatParamVo.getQuestion())
                    .answer("抱歉, 问答发生异常")
                    .build();
            return  ReplyMsg.ofError(data);
        }
    }

}
