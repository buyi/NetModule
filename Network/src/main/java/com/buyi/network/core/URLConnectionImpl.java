package com.buyi.network.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

/**
 * Created by buyi on 16/7/4.
 */
public class URLConnectionImpl {

    /**
     * encode url params
     * @param in
     * @return
     */
    private String urlEncode(String in) {
        if (in == null || in.length() == 0) {
            return "";
        }

        try {
            return URLEncoder.encode(in, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    /**
     * 将哈希表中的数据转换成url encode完好的请求参数字符串 然后与原有url拼接成完整URL
     * @param url
     * @param parameters
     * @return
     */
    public String setupCompleteUrl(String url, Map<String, String> parameters) {

        final String params = convertHeader (parameters);

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

    public NetResponse httpGet (NetRequest request) {


        long current = -1;
        try {
            current = System.currentTimeMillis();
//                    String link = "http://www.baidu.com";

            String link = setupCompleteUrl(request.url, request.params);


            // 如果原url为空 则返回空串
            if (link == null || link.length() == 0){
               // TODO
            }
            System.out.println("url:" + link);
            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();



            setupConHeader(conn, request.headers);
            dumpRequestHeader(conn);
            dumpRequestParams(request.params);




            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(false);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);


            conn.connect();

            checkResponse(conn);
            dumpResponseHeader (conn);


            InputStream is = conn.getInputStream();


            NetResponse nr = new NetResponse();
            nr.connection = conn;
            nr.mCode = conn.getResponseCode();
            nr.mContent = conn.getInputStream();
            nr.mHeaders = headerToHashMap(conn);
//            nr.mVersion = conn.getre
//            BufferedReader reader =new BufferedReader(new InputStreamReader(is, "UTF-8"));
//            String webPage = "",data="";
//
//            while ((data = reader.readLine()) != null){
//                webPage += data + "\n";
//            }
            mStringProcessor.processResponse(nr);
//            System.out.println("webPage:" + webPage);
        } catch (IOException e) {
            e.printStackTrace();
            long duration = System.currentTimeMillis() - current;
            System.out.println("duration:" + duration);
        }
    return null;


    }




    /**HTTP响应处理器，此处将Inputstream处理为文本**/
    private ResponseProcessor<String> mStringProcessor = new ResponseProcessor<String>() {
        @Override
        public String processResponse(NetResponse response){
            if (response == null) {
                return null;
            }
//            StringBuilder buffer = new StringBuilder(64);
            ByteArrayBuffer buffer = new ByteArrayBuffer(32 * 1024);
            InputStream in = null;
            try {
                in = response.mContent;
                if (response.mHeaders == null) {
//                    LogAssist.e(Developer.WANGBO, Module.NET_CORE,
//                            "header is null");
                }

                String encoding = response.mHeaders.get("Content-Encoding");
                if (encoding != null && encoding.contains("gzip")) {
//                    LogAssist.d(Developer.WANGBO, Module.NET_CORE, "gzip");
                    in = new GZIPInputStream(in);
                } else {
//                    LogAssist.d(Developer.WANGBO, Module.NET_CORE, "no gzip");
                }

                int temp;
                byte[] bytes = new byte[8096];
                while ((temp = in.read(bytes)) != -1) {
//                    buffer.append(bytes);
                    buffer.append(bytes, 0, temp);
                }

            } catch (SocketTimeoutException e) {
                e.printStackTrace();
//                throw new NetworkException(NetworkException.SOCKET_TIMEOUT_EXCEPTION,
//                        "tips_network_error2",e);
            } catch (IOException e) {
//                e.printStackTrace();
//                throw new NetworkException(NetworkException.IO_EXCEPTION, e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (response != null) {
                    response.closeConnection();
                }
            }
            byte[] result = buffer.toByteArray();
            String data = new String(result);
            System.out.println(" buffer.toString():" + data);
//            LogAssist.d(Developer.WANGBO, Module.NET_CORE, data);
            return buffer.toString();
        }
    };

    /**
     * 获取相应体的header
     * @param connection
     * @return
     */
    private HashMap<String, String> headerToHashMap(HttpURLConnection connection) {
        // printResponseHeaders(connection);
        HashMap<String, String> map = new HashMap<String, String>();
        Map<String, List<String>> headers = connection.getHeaderFields();
        StringBuilder sb = new StringBuilder();
        for (String key : headers.keySet()) {
            List<String> list = headers.get(key);

            boolean first = true;
            for (String head : list) {
                if (first) {
                    first = false;
                } else {
                    sb.append(";");
                }
                sb.append(head);
            }
            map.put(key, sb.toString());
            sb.delete(0, sb.length());// 清空
        }
        return map;
    }

    /**
     * 设置头部
     * @param con
     * @param value
     */
    private void setupConHeader (URLConnection con, Map<String, String> value) {
        if (value == null) {
            return;
        }

        Set<Map.Entry<String, String>> headers = value.entrySet();
        for (Map.Entry<String, String> header : headers) {
            con.setRequestProperty(header.getKey(), header.getValue());
        }
    }

    /**
     * 设置请求Header 并打印
     * @param con
     */
    private void dumpRequestHeader(URLConnection con) {


            Map<String, List<String>> map = con.getRequestProperties();
            Set<String> set = map.keySet();
            for (String key : set) {
                List<String> list = map.get(key);
                StringBuilder values = new StringBuilder();
                boolean first = true;
                for (String value : list) {
                    if (first) {
                        first = false;
                        values.append(value);
                    } else {
                        values.append("," + value);
                    }
                }
            System.out.println("request header##" + key + ":" + values.toString());
            }


    }


    /**
     * 转换header map为字串
     * 添加上& 和 做encode处理
     * @param parameters
     * @return
     */
    private String convertHeader (Map<String, String> parameters) {
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

    /**
     * 打印请求体参数
     * @param value
     */
    private void dumpRequestParams ( Map<String, String> value) {
        String headers = convertHeader (value);
        System.out.println("request params##" + headers);
    }


    /**
     * 检查响应码
     * @param connection
     */
    private void checkResponse(HttpURLConnection connection)
           /* throws NetworkException*/ {
        try {
            int statusCode = connection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
//                throw new NetworkException(NetworkException.RESPONSE_EXCEPTION,
//                        "Server error,code:" + statusCode);
            }
        } catch (IOException e) {
//            throw new NetworkException(NetworkException.IO_EXCEPTION, e);
        }
    }


    /**
     * 打印出响应的header
     * @param connection
     */

    private  void dumpResponseHeader (HttpURLConnection connection) {
        Map<String, List<String>> map = connection.getHeaderFields();
        Set<String> set = map.keySet();
        for (String key : set) {
            List<String> list = map.get(key);
            StringBuilder values = new StringBuilder();
            boolean first = true;
            for (String value : list) {
                if (first) {
                    first = false;
                    values.append(value);
                } else {
                    values.append("," + value);
                }
            }
            System.out.println("response header##"+ key + ":" + values.toString());
        }
    }

    private void dumpResponseContent () {

    }
}
