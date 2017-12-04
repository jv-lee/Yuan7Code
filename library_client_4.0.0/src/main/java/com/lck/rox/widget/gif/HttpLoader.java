package com.lck.rox.widget.gif;

/**
 * Created by Administrator on 2017/11/14.
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wanghao on 16/5/23.
 */
public class HttpLoader {
    public static InputStream getInputStreanFormUrl(String param) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(param).openConnection();
        return conn.getInputStream();
    }

}
