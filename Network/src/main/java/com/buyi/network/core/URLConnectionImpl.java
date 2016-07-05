package com.buyi.network.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by buyi on 16/7/4.
 */
public class URLConnectionImpl {
    // 连接超时时间常量
    private final int CONNECT_TIMEOUT = 5000;
    private final int READ_TIMEOUT = 10000;

    /**
     * http post请求
     * @param request
     * @return
     */
    public NetResponse httpPost (NetRequest request) {
        long current = -1;

        try {
            // 计算网络访问开始时间
            current = System.currentTimeMillis();

            // get参数永远都拼在url上面
            URL url = new URL(NetUtils.setupCompleteUrl(request));

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 装载header
            setupConHeader(conn, request.headers);
            // 打印header
            dumpRequestHeader(conn);
            // 打印请求参数
            dumpRequestParams(request.getParams);
            // 打印请求参数
            dumpRequestParams(request.postParams);

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);

            // 写post params
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            String urlParameters = NetUtils.convertParams(request.postParams);
            writer.write(urlParameters);
            writer.flush();


            // 检查返回code
            checkResponse(conn);



            NetResponse nr = new NetResponse();
            nr.connection = conn;
            nr.code = conn.getResponseCode();
            nr.content = conn.getInputStream();
            nr.message = conn.getResponseMessage();
            // 组装并打印响应体header
            nr.header = setupAndDumpResponseHeader(conn);

            // 打印返回内容
            dumpResponseContent(nr);

            long duration = System.currentTimeMillis() - current;
            System.out.println("normal duration##" + duration);
            writer.close();


            return nr;
        } catch (IOException e) {
            e.printStackTrace();
            long duration = System.currentTimeMillis() - current;
            System.out.println("exception duration##" + duration);
            return null;
        }
    }




    /**
     * 执行http get请求方法
     * @param request
     * @return
     */
    public NetResponse httpGet (NetRequest request) {
        long current = -1;
        try {
            // 计算网络访问开始时间
            current = System.currentTimeMillis();

            // 组装url 打开连接
            URL url = new URL(NetUtils.setupCompleteUrl(request));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 装载header
            setupConHeader(conn, request.headers);
            // 打印header
            dumpRequestHeader(conn);
            // 打印请求参数
            dumpRequestParams(request.getParams);

            // 只取不写
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(false);
            // 设置超时时间
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);


            conn.connect();

            // 检查返回code
            checkResponse(conn);



            NetResponse nr = new NetResponse();
            nr.connection = conn;
            nr.code = conn.getResponseCode();
            nr.content = conn.getInputStream();
            nr.message = conn.getResponseMessage();
            // 组装并打印响应体header
            nr.header = setupAndDumpResponseHeader(conn);

            // 打印返回内容
            dumpResponseContent(nr);

            long duration = System.currentTimeMillis() - current;
            System.out.println("normal duration##" + duration);
            return nr;
        } catch (IOException e) {
            e.printStackTrace();
            long duration = System.currentTimeMillis() - current;
            System.out.println("exception duration##" + duration);
            return null;
        }
    }



    /**
     * 设置头部
     * @param con
     * @param headerMap
     */
    private void setupConHeader (URLConnection con, Map<String, String> headerMap) {
        if (headerMap == null) {
            return;
        }

        Set<Map.Entry<String, String>> headers = headerMap.entrySet();
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
     * 打印请求体参数
     * @param value
     */
    private void dumpRequestParams ( Map<String, String> value) {
        String headers = NetUtils.convertParams(value);
        System.out.println("request Params##" + headers);
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
     * 获取连接的响应header 并打印
     * @param connection
     * @return
     */
    private HashMap<String, String> setupAndDumpResponseHeader(HttpURLConnection connection) {
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
            System.out.println("response header##" + key + ":" + sb.toString());
            sb.delete(0, sb.length());// 清空
        }
        return map;
    }



    /**
     * 打印响应内容
     */
    private void dumpResponseContent (NetResponse nr) {
        System.out.println("response content##" + nr);
        try {
            BufferedReader reader =new BufferedReader(new InputStreamReader(nr.content, "UTF-8"));
            String webPage = "",data="";

            while ((data = reader.readLine()) != null){
                webPage += data + "\n";
            }
            System.out.println("webPage:" + webPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        /**HTTP响应处理器，此处将Inputstream处理为文本**/
//        ResponseProcessor<String> mStringProcessor = new ResponseProcessor<String>() {
//            @Override
//            public String processResponse(NetResponse response){
//                if (response == null) {
//                    return null;
//                }
////            StringBuilder buffer = new StringBuilder(64);
//                ByteArrayBuffer buffer = new ByteArrayBuffer(32 * 1024);
//                InputStream in = null;
//                try {
//                    in = response.content;
//                    if (response.header == null) {
////                    LogAssist.e(Developer.WANGBO, Module.NET_CORE,
////                            "header is null");
//                    }
//
//                    String encoding = response.header.get("Content-Encoding");
//                    if (encoding != null && encoding.contains("gzip")) {
////                    LogAssist.d(Developer.WANGBO, Module.NET_CORE, "gzip");
//                        in = new GZIPInputStream(in);
//                    } else {
////                    LogAssist.d(Developer.WANGBO, Module.NET_CORE, "no gzip");
//                    }
//
//                    int temp;
//                    byte[] bytes = new byte[8096];
//                    while ((temp = in.read(bytes)) != -1) {
////                    buffer.append(bytes);
//                        buffer.append(bytes, 0, temp);
//                    }
//
//                } catch (SocketTimeoutException e) {
//                    e.printStackTrace();
////                throw new NetworkException(NetworkException.SOCKET_TIMEOUT_EXCEPTION,
////                        "tips_network_error2",e);
//                } catch (IOException e) {
////                e.printStackTrace();
////                throw new NetworkException(NetworkException.IO_EXCEPTION, e);
//                } finally {
//                    if (in != null) {
//                        try {
//                            in.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    if (response != null) {
//                        response.closeConnection();
//                    }
//                }
//                byte[] result = buffer.toByteArray();
//                String data = new String(result);
//                System.out.println(" buffer.toString():" + data);
////            LogAssist.d(Developer.WANGBO, Module.NET_CORE, data);
//                return buffer.toString();
//            }
//        };
//        mStringProcessor.processResponse(nr);
    }
}
