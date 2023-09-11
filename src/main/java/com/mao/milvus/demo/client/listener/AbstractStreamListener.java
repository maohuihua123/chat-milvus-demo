package com.mao.milvus.demo.client.listener;

import com.alibaba.fastjson.JSON;
import com.mao.milvus.demo.client.chat.ChatResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * EventSource listener for chat-related events.
 */
@Slf4j
public abstract class AbstractStreamListener extends EventSourceListener {

    protected String lastMessage = "";


    /**
     * Called when all new message are received.
     *
     */
    @Setter
    @Getter
    protected Consumer<String> onComplate = s -> {

    };

    /**
     * Called when a new message is received.
     * 收到消息 单个字
     *
     * @param message the new message
     */
    public abstract void onMsg(String message);

    /**
     * Called when an error occurs.
     * 出错时调用
     *
     * @param throwable the throwable that caused the error
     * @param response  the response associated with the error, if any
     */
    public abstract void onError(Throwable throwable, String response);

    @Override
    public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
        // log.info("Start chat and open.");
    }

    @Override
    public void onClosed(@NotNull EventSource eventSource) {
        // log.info("Complate chat and close.");
    }

    @Override
    public void onEvent(@NotNull EventSource eventSource, String id, String type, @NotNull String data) {
        ChatResponse response = JSON.parseObject(data, ChatResponse.class);

        String text = response.getResult();

        if (text != null) {
            lastMessage += text;
            onMsg(text);
        }

        if (response.getIsEnd()) {
            onComplate.accept(lastMessage);
        }

    }


    @SneakyThrows
    @Override
    public void onFailure(@NotNull EventSource eventSource, Throwable throwable, Response response) {

        try {
            log.error("Stream connection error: {}", throwable.getMessage(), throwable);

            String responseText = "";

            if (Objects.nonNull(response)) {
                responseText = Objects.requireNonNull(response.body()).string();
            }

            log.error("response：{}", responseText);

            this.onError(throwable, responseText);

        } catch (Exception e) {
            log.warn("onFailure error:{}", e.getMessage(), e);
        } finally {
            eventSource.cancel();
        }
    }
}
