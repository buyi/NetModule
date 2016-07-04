package com.buyi.network.core;

/**
 * Created by buyi on 16/7/4.
 */

/**
 * 响应处理器
 * @param <T>
 */
public interface ResponseProcessor<T> {
    T processResponse(NetResponse response);
}
