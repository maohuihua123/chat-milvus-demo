package com.mao.milvus.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplyMsg implements Serializable {

    public static final Integer REPLY_STATUS_SUCCESS = 200;
    public static final Integer REPLY_STATUS_FAIL = 500;

    private Integer status;

    private String msg;

    private Object data;

    public ReplyMsg(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    /**
     * 返回成功
     */
    public static ReplyMsg ofSuccess(Object data) {
        return ReplyMsg.ofSuccess("success", data);
    }

    /**
     * 返回成功信息
     * @param msg message
     */
    public static ReplyMsg ofSuccessMsg(String msg) {
        return ReplyMsg.ofSuccess(msg, null);
    }

    /**
     * 返回成功-自定义message
     */
    public static ReplyMsg ofSuccess(String message, Object data) {
        return new ReplyMsg(REPLY_STATUS_SUCCESS, message, data);
    }

    /**
     * 返回失败
     */
    public static ReplyMsg ofError(Object data) {
        return ReplyMsg.ofError("error", data);
    }

    public static ReplyMsg ofErrorMsg(String msg) {
        return ReplyMsg.ofError(msg, null);
    }

    /**
     * 返回失败-自定义message
     */
    public static ReplyMsg ofError(String message, Object data) {
        return new ReplyMsg(REPLY_STATUS_FAIL, message, data);
    }
}
