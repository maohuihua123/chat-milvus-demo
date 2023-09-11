package com.mao.milvus.demo.client.helper;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@UtilityClass
@Slf4j
public class SseHelper {

    public void complete(SseEmitter sseEmitter) {

        try {
            sseEmitter.complete();
        } catch (Exception e) {
            log.error("SseHelper complete error", e);
        }
    }

    public void send(SseEmitter sseEmitter, Object data) {

        try {
            sseEmitter.send(data);
        } catch (Exception e) {
            log.error("SseHelper send error", e);
        }
    }
}
