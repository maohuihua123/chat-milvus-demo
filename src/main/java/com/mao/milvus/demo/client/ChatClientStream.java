package com.mao.milvus.demo.client;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mao.milvus.demo.client.access.AccessToken;
import com.mao.milvus.demo.client.api.Api;
import com.mao.milvus.demo.client.chat.ChatCompletion;
import com.mao.milvus.demo.client.chat.Message;
import com.mao.milvus.demo.exception.ChatException;
import com.mao.milvus.demo.exception.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatClientStream {

    @Builder.Default
    private String grantType = "client_credentials";

    @Builder.Default
    private long timeout = 300;

    @Builder.Default
    private String apiHost = Api.DEFAULT_API_HOST;

    private String clientId;

    private String clientSecret;

    private AccessToken accessToken;

    private OkHttpClient okHttpClient;

    public ChatClientStream init() {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.connectTimeout(timeout, TimeUnit.SECONDS);
        client.writeTimeout(timeout, TimeUnit.SECONDS);
        client.readTimeout(timeout, TimeUnit.SECONDS);

        okHttpClient = client.build();

        accessToken = getAccessToken();

        return this;
    }


    public void streamChatCompletion(
            ChatCompletion chatCompletion,
            EventSourceListener eventSourceListener
    ) {
        chatCompletion.setStream(true);

        try {
            EventSource.Factory factory = EventSources.createFactory(okHttpClient);
            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(chatCompletion);
            String token = accessToken.getAccessToken();
            log.info("请求参数: {}", requestBody);
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(requestBody, mediaType);
            Request request = new Request.Builder()
                    .url("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions?access_token=" + token)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            factory.newEventSource(request, eventSourceListener);

        } catch (Exception e) {
            log.error("请求出错：message = {}", e.getMessage(), e);
        }
    }

    /**
     * 流式输出
     */
    public void streamChatCompletion(
            List<Message> messages,
            EventSourceListener eventSourceListener
    ) {
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .messages(messages)
                .stream(true)
                .build();
        streamChatCompletion(chatCompletion, eventSourceListener);
    }

    /**
     * 从用户的AK, SK生成鉴权签名Access Token
     *
     * @return 鉴权签名（Access Token）
     */
    private AccessToken getAccessToken() {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        String content = "grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;
        RequestBody body = RequestBody.create(content, mediaType);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()){
                String string = Objects.requireNonNull(response.body()).string();
                AccessToken token = JSON.parseObject(string, AccessToken.class);
                log.info("AccessToken: {}", token.getAccessToken());
                return token;
            }
            String errorMsg = Objects.requireNonNull(response.body()).string();
            log.error("请求异常：{}", errorMsg);
            ErrorResponse errorResponse = JSON.parseObject(errorMsg, ErrorResponse.class);
            throw new ChatException(errorResponse.getErrorMsg());
        } catch (Exception e){
            log.error("请求异常：{}", e.getMessage());
            throw new ChatException(e.getMessage());
        }
    }
}
