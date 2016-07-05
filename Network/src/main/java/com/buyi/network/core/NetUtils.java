package com.buyi.network.core;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class NetUtils {

    /**
     * encode url getParams
     *
     * @param in
     * @return 如果in为null或者为空 返回空串。如果encode失败返回空串
     */
    private static String urlEncode(String in) {
        if (in == null || in.length() == 0) {
            return "";
        }

        try {
            return URLEncoder.encode(in, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    static String setupCompleteUrl (NetRequest request) {
        String link = setupCompleteUrlInternal(request.url, request.getParams);

        // 如果原url为空 则返回空串
        if (link == null || link.length() == 0){
            // TODO
        }
        System.out.println("link##" + link);
        return link;
    }

    /**
     * 将哈希表中的数据转换成url encode完好的请求参数字符串 然后与原有url拼接成完整URL
     * @param url
     * @param parameters
     * @return
     */
    private static String setupCompleteUrlInternal(String url, Map<String, String> parameters) {

        final String params = convertParams(parameters);

        //  如果参数字串为空， 则直接返回原url
        if (params == null || params.length() == 0) {
            return url;
        }

        // 如果原url为空 则返回空串
        if (url == null || url.length() == 0){
            return "";
        }

        // 处理url里边含有#的情况
        final String HASH_KEY = "#";
        String fragment = null;
        if (url.contains(HASH_KEY)) {
            int hashKeyPos = url.indexOf(HASH_KEY);
            fragment = url.substring(hashKeyPos);
            url = url.substring(0, hashKeyPos);
        }

        // 判断url本身如果不带有?符号 添加?
        if (url.indexOf('?') != -1) {
            url += "&" + params;
        } else {
            url += "?" + params;
        }

        // 如果之前截取的fragment不为空
        if (!(fragment == null || fragment.length() == 0)) {
            url += fragment;
        }
        return url;
    }

    /**
     * 转换parameter map为字串
     * 添加上& 和 做encode处理
     * @param parameters
     * @return
     */
    static String convertParams(Map<String, String> parameters) {
        // 首先验证参数合法性 如果没有get参数则返回空串
        if (parameters == null) {
            return "";
        }

        // 拼接参数
        StringBuilder sb = new StringBuilder(64);
        boolean first = true;
        for (String key : parameters.keySet()) {
            if (first) {
                first = false;
            } else {
                sb.append("&");
            }
            sb.append(urlEncode(key) + "=" + urlEncode(String.valueOf(parameters.get(key))));
        }

        return sb.toString();
    }
}