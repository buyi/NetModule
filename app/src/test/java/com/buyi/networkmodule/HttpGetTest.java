package com.buyi.networkmodule;

import android.os.Build;

import com.buyi.network.core.NetRequest;
import com.buyi.network.core.URLConnectionImpl;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by buyi on 16/7/4.
 */
public class HttpGetTest {

        @Test
        public void httpGetNormal() {

            NetRequest request = new NetRequest();
            request.url = "http://api.jikexueyuan.com/v3/activity/banner?";
            Map<String, String> params = new HashMap<>();
            params.put("type", "2");
            params.put("debug", "ADubaC");
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
