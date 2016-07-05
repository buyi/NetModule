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
public class HttpPostTest {

    @Test
    public void httpPostNormal() {

        NetRequest request = new NetRequest();
        request.url = "http://api.jikexueyuan.com/v3/account/login_common";
        Map<String, String> getParams = new HashMap<>();
        getParams.put("debug", "ADubaC");

        Map<String, String> postParams = new HashMap<>();
        postParams.put("password", "000000");
        postParams.put("uname", "不易");

        request.getParams = getParams;
        request.postParams = postParams;
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", String.format("%s/%s (Linux; Android %s; %s Build/%s)", "buyi_network", "1.0", Build.VERSION.RELEASE, Build.MANUFACTURER, Build.ID));
//        headers.put("Cache-control", "");
        request.headers = headers;
        URLConnectionImpl impl = new URLConnectionImpl();
        impl.httpPost(request);
//            assertThat(EmailValidator.isValidEmail("name@email.com"), is(true));
    }
}
