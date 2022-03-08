package com.nohi.common.core.domain;

import com.nohi.common.constant.HttpStatus;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 *
 * <p>created on 2020-4-2</p>
 * @author dute7liang
 */
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private Integer code;

    @Getter
    @Setter
    private String msg;

    @Getter
    @Setter
    private boolean success ;

    @Getter
    @Setter
    private T data;

    public static <T> R<T> ok() {
        return restResultSuccess(null, null);
    }

    public static <T> R<T> ok(T data) {
        return restResultSuccess(data, null);
    }

    public static <T> R<T> ok(T data, String msg) {
        return restResultSuccess(data, msg);
    }

   /* public static <T> R<T> ok(BaseCode baseCode) {
        return restResultSuccess(null, baseCode.getCode(), baseCode.getMsg());
    }*/

    public static <T> R<T> failed() {
        return restResultError(null, HttpStatus.ERROR, "操作失败");
    }

    public static <T> R<T> failed(String msg) {
        return restResultError(null,  HttpStatus.ERROR, msg);
    }

    public static <T> R<T> failed(Integer code, String msg) {
        return restResultError(null,  code, msg);
    }

    public static <T> R<T> failed(T data, Integer code, String msg) {
        return restResultError(data, code, msg);
    }

    public static <T> R<T> failed(T data) {
        return restResultError(data, HttpStatus.ERROR, "操作失败");
    }

    public static <T> R<T> failed(T data, String msg) {
        return restResultError(data, HttpStatus.ERROR, msg);
    }

    private static <T> R<T> restResultSuccess(T data, String msg) {
        R<T> apiResult = new R<>();
        apiResult.setCode(HttpStatus.SUCCESS);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        apiResult.setSuccess(true);
        return apiResult;
    }

    private static <T> R<T> restResultError(T data, Integer code, String msg) {
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        apiResult.setSuccess(false);
        return apiResult;
    }



}
