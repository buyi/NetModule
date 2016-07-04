package com.buyi.networkmodule;

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
        public void emailValidator_CorrectEmailSimple_ReturnsTrue() {

            NetRequest request = new NetRequest();
            request.url = "http://api.jikexueyuan.com/v3/activity/banner?";
            Map<String, String> params = new HashMap<>();
            params.put("type", "2");
            params.put("debug", "ADubaC");
            request.params = params;
            Map<String, String> headers = new HashMap<>();
            request.headers = headers;
            URLConnectionImpl impl = new URLConnectionImpl();
            impl.httpGet(request);
//            assertThat(EmailValidator.isValidEmail("name@email.com"), is(true));
        }
}
