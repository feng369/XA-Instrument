package cn.wizzer.app.getui;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.LinkTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.gexin.rp.sdk.template.style.Style0;
import org.nutz.aop.interceptor.async.Async;

public class PushtoSingle {
    //采用"Java SDK 快速入门"， "第二步 获取访问凭证 "中获得的应用配置，用户可以自行替换
    //自己
//    static String CID = "5d80c27a5f0a50b6b97eea1924f45aa7";
//    private static String appId = "RhdYj9rhKI8DmxAOMXrs97";
//    private static String appKey = "8AL16IBC456ZMUHyVlS7N7";
//    private static String masterSecret = "VhTdT62TVT9bexkNO6TwA9";

    //龙哥
  /*  private static String appId = "PZYPccNbAo9IT4gzUn3EH1";
    private static String appKey = "aq0bkMiBjL63XmcgXx0S57";
    private static String masterSecret = "SCQCnY5GiM665zBsG6j5u3";
    static String CID = "94422cc66ee991893b4934bd5919fb0d";*/

    //
//    private static String appId = "PZYPccNbAo9IT4gzUn3EH1";
//    private static String appKey = "aq0bkMiBjL63XmcgXx0S57";
//    private static String masterSecret = "SCQCnY5GiM665zBsG6j5u3";
//    static String CID = "8490a3e5c20595f9acbc49e9185bcd7f";

    //粥老师
    static String CID = "7ca55dfae3b58ffc6629c52434d929e6";
    private static String appId = "MXkqnhNlO67iowxGEkFeV6";
    private static String appKey = "T6EoiBN5jw9GEBujFfeGv3";
    private static String masterSecret = "enqH3xf2SLAYeuTveJRTQ4";
    //别名推送方式
    static String host = "http://sdk.open.api.igexin.com/apiex.htm";

    public static void main(String[] args) throws Exception {
//        push2Single("测试", "西安工具", CID);
        pushTransMessageToSingle(CID,"西安工具");
    }

    public static void push2Single(String title, String content, String clientId) {
        IGtPush push = new IGtPush(host, appKey, masterSecret);
        LinkTemplate template = linkTemplateDemo(title, content);
        SingleMessage message = new SingleMessage();
        message.setOffline(true);
        // 离线有效时间，单位为毫秒，可选
        message.setOfflineExpireTime(24 * 3600 * 1000);
        message.setData(template);
        // 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
        message.setPushNetWorkType(0);
        Target target = new Target();
        target.setAppId(appId);
        target.setClientId(clientId);
        //target.setAlias(Alias);
        IPushResult ret = null;
        try {
            ret = push.pushMessageToSingle(message, target);
        } catch (RequestException e) {
            e.printStackTrace();
            ret = push.pushMessageToSingle(message, target, e.getRequestId());
        }
        if (ret != null) {
            System.out.println(ret.getResponse().toString());
        } else {
            System.out.println("服务器响应异常");
        }
    }

    /**
     *      * 对单个用户推送透传消息
     *      * 
     *      * @param alias
     *      * @param title
     *      * @param content
     *      * @return
     *     
     */
    public static LinkTemplate linkTemplateDemo(String title, String content) {
        LinkTemplate template = new LinkTemplate();
        // 设置APPID与APPKEY
        template.setAppId(appId);
        template.setAppkey(appKey);

        Style0 style = new Style0();
        // 设置通知栏标题与内容
        style.setTitle(title);
        style.setText(content);
        // 配置通知栏图标
        style.setLogo("icon.png");
        // 配置通知栏网络图标
        style.setLogoUrl("");
        // 设置通知是否响铃，震动，或者可清除
        style.setRing(true);
        style.setVibrate(true);
        style.setClearable(true);
        template.setStyle(style);
        // 设置打开的网址地址
        template.setUrl("http://www.baidu.com");
        return template;
    }

    private static TransmissionTemplate transTemplate(String cid, String msg) {
        TransmissionTemplate template = new TransmissionTemplate();
        // 设置APPID与APPKEY
        template.setAppId(appId);
        template.setAppkey(appKey);
        template.setTransmissionContent(msg);
        template.setTransmissionType(0); // 这个Type为int型，填写1则自动启动app
        return template;
    }

    public static boolean pushTransMessageToSingle(String cid, String msg) {
        IGtPush push = new IGtPush(host, appKey, masterSecret);
        TransmissionTemplate template = transTemplate(cid, msg);
        SingleMessage message = new SingleMessage();
        message.setOffline(true);
// 离线有效时间，单位为毫秒，可选
        message.setOfflineExpireTime(24 * 3600 * 1000);
        message.setData(template);
// 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
        message.setPushNetWorkType(0);
        Target target = new Target();
        target.setAppId(appId);
        target.setClientId(cid);
        IPushResult ret = null;
        try {
            ret = push.pushMessageToSingle(message, target);
        } catch (RequestException e) {
            e.printStackTrace();
            ret = push.pushMessageToSingle(message, target, e.getRequestId());
        }
        if (ret != null && ret.getResponse() != null && ret.getResponse().containsKey("result")) {
            System.out.println(ret.getResponse().toString());
            if (ret.getResponse().get("result").toString().equals("ok") && ret.getResponse().containsKey("status")) {
                return true;
            }
        }
        return false;
    }
}
