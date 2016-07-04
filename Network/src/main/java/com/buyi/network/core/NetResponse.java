package com.buyi.network.core;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Map;

/**
 * Created by buyi on 16/7/4.
 */
public class NetResponse {

    public String mVersion;//协议版本
    public int mCode;//状态码
    public Map<String, String> mHeaders;//报文头
    public InputStream mContent;//报文实体
    public Object connection;//链接（采用实现不同，类型也不同）

    public void closeConnection () {
        if (this.connection instanceof HttpURLConnection) {
            ((HttpURLConnection) this.connection).disconnect();
        }
    }
}
