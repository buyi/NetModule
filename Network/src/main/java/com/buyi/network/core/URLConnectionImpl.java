package com.buyi.network.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
            HttpURLConnection conn = wrapConnection(url);

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

    public NetResponse httpsGet (NetRequest request) {
        // 创建SSLContext对象，并使用我们指定的信任管理器初始化
        try {
//            TrustManager[] tm = { new MyX509TrustManager() };
//            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
//            sslContext.init(null, tm, new java.security.SecureRandom());
//            // 从上述SSLContext对象中得到SSLSocketFactory对象
//            SSLSocketFactory ssf = sslContext.getSocketFactory();
            // 创建URL对象
            URL myURL = new URL(request.url);
            // 创建HttpsURLConnection对象，并设置其SSLSocketFactory对象
            HttpsURLConnection httpsConn = (HttpsURLConnection) myURL.openConnection();
//            httpsConn.setSSLSocketFactory(ssf);
//            httpsConn.setHostnameVerifier(DO_NOT_VERIFY);
            // 取得该连接的输入流，以读取响应内容
            InputStreamReader insr = new InputStreamReader(httpsConn.getInputStream());
            // 读取服务器的响应内容并显示
            int respInt = insr.read();
            while (respInt != -1) {
                System.out.print((char) respInt);
                respInt = insr.read();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //javax.net.ssl.SSLHandshakeException: java.security.cert.CertificateException: No subject alternative DNS name matching ebanks.gdb.com.cn found.
    private final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    private HttpURLConnection wrapConnection (URL url) {
        HttpURLConnection conn = null;
        try {
            conn = null;
            if (url.getProtocol().equals("https")) {
                conn = (HttpsURLConnection) url.openConnection();
                TrustManager[] tm = { new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                } };
                try {
                    SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
                    sslContext.init(null, tm, new java.security.SecureRandom());
                    // 从上述SSLContext对象中得到SSLSocketFactory对象
                    SSLSocketFactory ssf = sslContext.getSocketFactory();
                    //PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
                    ((HttpsURLConnection)conn).setSSLSocketFactory(ssf);
                    ((HttpsURLConnection)conn).setHostnameVerifier(DO_NOT_VERIFY);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (NoSuchProviderException e) {
                    e.printStackTrace();
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                }
            } else {
                conn = (HttpURLConnection) url.openConnection();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conn;
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


            HttpURLConnection conn = wrapConnection(url);


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