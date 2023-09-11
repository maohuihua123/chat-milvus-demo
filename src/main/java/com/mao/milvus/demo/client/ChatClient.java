package com.mao.milvus.demo.client;

import com.alibaba.fastjson.JSON;
import com.google.common.net.HttpHeaders;
import com.mao.milvus.demo.client.access.AccessToken;
import com.mao.milvus.demo.client.api.Api;
import com.mao.milvus.demo.client.chat.ChatCompletion;
import com.mao.milvus.demo.client.chat.ChatResponse;
import com.mao.milvus.demo.client.chat.ChatLlama;
import com.mao.milvus.demo.client.chat.Message;
import com.mao.milvus.demo.client.embeddings.Embedding;
import com.mao.milvus.demo.client.embeddings.EmbeddingResponse;
import com.mao.milvus.demo.exception.ChatException;
import com.mao.milvus.demo.exception.ErrorResponse;
import io.reactivex.Single;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatClient {
    @Builder.Default
    private String grantType = "client_credentials";

    @Builder.Default
    private long timeout = 300;

    @Builder.Default
    private String apiHost = Api.DEFAULT_API_HOST;

    private String clientId;

    private String clientSecret;

    private Api apiClient;

    private AccessToken accessToken;

    private OkHttpClient okHttpClient;


    public ChatClient init() {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.addInterceptor(chain -> {
            Request original = chain.request();
            String contentType = "application/json";
            Request request = original.newBuilder()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .method(original.method(), original.body())
                    .build();
            return chain.proceed(request);
        }).addInterceptor(chain -> {
            Request original = chain.request();
            Response response = chain.proceed(original);
            if (response.isSuccessful()) {
                return response;
            }
            String errorMsg = Objects.requireNonNull(response.body()).string();
            log.error("请求异常：{}", errorMsg);
            ErrorResponse errorResponse = JSON.parseObject(errorMsg, ErrorResponse.class);
            throw new ChatException(errorResponse.getErrorMsg());
        });

        client.connectTimeout(timeout, TimeUnit.SECONDS);
        client.writeTimeout(timeout, TimeUnit.SECONDS);
        client.readTimeout(timeout, TimeUnit.SECONDS);
        this.okHttpClient = client.build();

        this.apiClient = new Retrofit.Builder()
                .baseUrl(this.apiHost)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(Api.class);

        accessToken = apiClient.getAccessToken(grantType, clientId, clientSecret).blockingGet();
        LocalDateTime expiresTime = LocalDateTime.now().plusSeconds(accessToken.getExpiresIn());
        accessToken.setExpiresTime(expiresTime);

        log.info("AccessToken: {} and expiresTime: {}", accessToken.getAccessToken(), expiresTime);

        return this;
    }

    /**
     * ERNIE-Bot是百度自行研发的大语言模型
     * @param chatCompletion 对话内容
     * @return 对话结果
     */
    public ChatResponse chatCompletion(ChatCompletion chatCompletion){
        String token = accessToken.getAccessToken();
        Single<ChatResponse> chatCompletionResponse = this.apiClient.chatCompletion(token, chatCompletion);
        return chatCompletionResponse.blockingGet();
    }
    /**
     * 简易版
     *
     * @param messages 问答参数
     */
    public ChatResponse chatCompletion(List<Message> messages) {
        ChatCompletion completion = ChatCompletion.builder().messages(messages).build();
        return this.chatCompletion(completion);
    }

    /**
     * 直接问
     */
    public String chatCompletion(String message) {
        ChatCompletion completion = ChatCompletion.builder()
                .messages(Collections.singletonList(Message.of(message)))
                .build();

        ChatResponse response = this.chatCompletion(completion);
        return response.getResult();
    }


    /**
     * Llama-2-13b-chat由Meta AI研发并开源
     *
     * @param chatLlama 问答参数
     * @return 答案
     */
    public ChatResponse chatLlama(ChatLlama chatLlama) {
        String token = accessToken.getAccessToken();
        String chatEndpoint = chatLlama.getChatEndpoint();
        Single<ChatResponse> chatCompletionResponse = this.apiClient.chatLlama(chatEndpoint, token, chatLlama);
        return chatCompletionResponse.blockingGet();
    }

    /**
     * 简易版
     *
     * @param messages 问答参数
     */
    public ChatResponse chatLlama(List<Message> messages) {
        ChatLlama chatLlama = ChatLlama.builder().messages(messages).build();
        return this.chatLlama(chatLlama);
    }

    /**
     * 直接问
     */
    public String chatLlama(String message) {
        ChatLlama chatLlama = ChatLlama.builder()
                .chatEndpoint(ChatLlama.Model.LLAMA_2_13B.getName())
                .messages(Collections.singletonList(Message.of(message)))
                .userId(clientId)
                .build();

        ChatResponse response = this.chatLlama(chatLlama);
        return response.getResult();
    }

    /**
     * 向量计算：单文本
     *
     * @param input 单文本
     * @return EmbeddingResponse
     */
    public EmbeddingResponse embeddings(String input) {
        List<String> inputs = new ArrayList<>(1);
        inputs.add(input);
        Embedding embedding = Embedding.builder().input(inputs).userId(clientId).build();
        return this.embeddings(embedding);
    }

    /**
     * 向量计算：集合文本
     *
     * @param input 文本集合
     * @return EmbeddingResponse
     */
    public EmbeddingResponse embeddings(List<String> input) {
        Embedding embedding = Embedding.builder().input(input).userId(clientId).build();
        return this.embeddings(embedding);
    }

    /**
     * 文本转换向量
     *
     * @param embedding 入参
     * @return EmbeddingResponse
     */
    public EmbeddingResponse embeddings(Embedding embedding) {
        String accessToken1 = accessToken.getAccessToken();
        Single<EmbeddingResponse> embeddings = this.apiClient.embeddings(accessToken1, embedding);
        return embeddings.blockingGet();
    }

    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date);
    }
}
