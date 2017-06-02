package com.post.model.api;


import com.post.model.utils.Base64;

public interface API {

    String IP = new String(Base64.decode("MTE5LjIzLjEzNi4xOTA6ODA4OA==")).trim();
    //    String IP = "119.23.136.190:8088";
    String v = "v1";

    /**
     * 服务器代码
     */
    String VERSION_CODE_JAR = "http://" + IP + "/sdkserver/" + v + "/updateSdk";

    /**
     * 每日启动应用发送活动状态
     */
    String APP_ACTIVE = "http://" + IP + "/sdkserver/" + v + "/appActive";

    /**
     * 首次安装应用启动时 发送状态
     */
    String APP_ADDSDK = "http://" + IP + "/sdkserver/" + v + "/addSdk";

}
