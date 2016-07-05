package com.buyi.network.core;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Map;

/**
 * Created by buyi on 16/7/4.
 */
public class NetResponse {
    public String version;//协议版本
    public int code; //状态码
    public String message;//结果消息
    public Map<String, String> header;//报文头
    public InputStream content;//报文实体
    public Object connection;//连接实体（采用实现不同，类型也不同）

    // 关闭连接
    public void closeConnection() {
        if (this.connection instanceof HttpURLConnection) {
            ((HttpURLConnection) this.connection).disconnect();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(64);
        sb.append("NetResponse##").append("version:").append(version).append(";").append("\n")
                .append("code:").append(code).append(";").append("\n")
                .append("message:").append(message).append(";").append("\n")
                .append("content:").append(content).append(";").append("\n")
                .append("connection:").append(connection);
        return sb.toString();
    }
}
