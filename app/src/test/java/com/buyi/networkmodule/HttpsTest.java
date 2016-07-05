package com.buyi.networkmodule;

import android.os.Build;

import com.buyi.network.core.NetRequest;
import com.buyi.network.core.URLConnectionImpl;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by buyi on 16/7/5.
 */
public class HttpsTest {



    @Test
    public void httpsGetNormal() {

        //javax.net.ssl.SSLHandshakeException: java.security.cert.CertificateException: No subject alternative DNS name matching ebanks.gdb.com.cn found.
        NetRequest request = new NetRequest();
        request.url = "https://kyfw.12306.cn/otn";
//        request.url = "https://ebanks.gdb.com.cn/sperbank/perbankLogin.jsp";
//        request.url = "https://api.douban.com/v2/book/1220562";
        Map<String, String> params = new HashMap<>();
        request.getParams = params;
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", String.format("%s/%s (Linux; Android %s; %s Build/%s)", "buyi_network", "1.0", Build.VERSION.RELEASE, Build.MANUFACTURER, Build.ID));
        headers.put("Cache-control", "");
        request.headers = headers;
        URLConnectionImpl impl = new URLConnectionImpl();
        impl.httpGet(request);
//            assertThat(EmailValidator.isValidEmail("name@email.com"), is(true));
    }
}
