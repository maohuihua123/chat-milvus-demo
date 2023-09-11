package com.mao.milvus.demo.client.api;

import com.mao.milvus.demo.client.access.AccessToken;
import com.mao.milvus.demo.client.chat.ChatCompletion;
import com.mao.milvus.demo.client.chat.ChatLlama;
import com.mao.milvus.demo.client.chat.ChatResponse;
import com.mao.milvus.demo.client.embeddings.Embedding;
import com.mao.milvus.demo.client.embeddings.EmbeddingResponse;
import io.reactivex.Single;
import retrofit2.http.*;


/**
 *
 */
public interface Api {

    String DEFAULT_API_HOST = "https://aip.baidubce.com/";

    /**
     * 鉴权
     *
     * @param grantType    client_credentials
     * @param clientId     client_id
     * @param clientSecret client_secret
     * @return AccessTokenResponse
     */
    @POST("oauth/2.0/token")
    Single<AccessToken> getAccessToken(
            @Query("grant_type") String grantType,
            @Query("client_id") String clientId,
            @Query("client_secret") String clientSecret
    );

    /**
     * 文本向量计算
     *
     * @param accessToken 访问token
     * @param embedding   请求参数
     * @return EmbeddingResponse
     */
    @POST("rpc/2.0/ai_custom/v1/wenxinworkshop/embeddings/embedding-v1")
    Single<EmbeddingResponse> embeddings(
            @Query("access_token") String accessToken,
            @Body Embedding embedding
    );

    /**
     *
     * @param chatEndpoint chatLlama算法模型名称
     * @param accessToken 访问token
     * @param chatLlama 对话信息
     * @return 对话结果
     */
    @POST("rpc/2.0/ai_custom/v1/wenxinworkshop/chat/{chatEndpoint}")
    Single<ChatResponse> chatLlama(
            @Path("chatEndpoint") String chatEndpoint,
            @Query("access_token") String accessToken,
            @Body ChatLlama chatLlama
    );

    @POST("rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions")
    Single<ChatResponse> chatCompletion(
            @Query("access_token") String accessToken,
            @Body ChatCompletion chatCompletion
    );
}
