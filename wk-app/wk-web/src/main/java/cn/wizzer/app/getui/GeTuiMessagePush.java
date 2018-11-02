package cn.wizzer.app.getui;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.base.payload.MultiMedia;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.LinkTemplate;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.gexin.rp.sdk.template.style.Style0;

public class GeTuiMessagePush {

    static String host = "https://api.getui.com/apiex.htm";

    //自己
//    static String CID = "5d80c27a5f0a50b6b97eea1924f45aa7";
//    private static String appId = "RhdYj9rhKI8DmxAOMXrs97";
//    private static String appkey = "8AL16IBC456ZMUHyVlS7N7";
//    private static String masterSecret = "VhTdT62TVT9bexkNO6TwA9";

    //粥老师
//    static String CID = "7ca55dfae3b58ffc6629c52434d929e6";
//    private static String appId = "MXkqnhNlO67iowxGEkFeV6";
//    private static String appkey = "T6EoiBN5jw9GEBujFfeGv3";
//    private static String masterSecret = "enqH3xf2SLAYeuTveJRTQ4";

    //龙哥
        private static String appId = "MXkqnhNlO67iowxGEkFeV6";
        private static String appkey = "T6EoiBN5jw9GEBujFfeGv3";
        private static String masterSecret = "enqH3xf2SLAYeuTveJRTQ4";
        static String CID = "8490a3e5c20595f9acbc49e9185bcd7f";

    public static void main(String[] args) throws Exception {
        pushMessage("测试","工具演示TEST",CID);
    }

    /*
    public static TransmissionTemplate transmissionTemplateDemo() {
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);
        template.setAppkey(appkey);
        // 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
        template.setTransmissionType(2);
        template.setTransmissionContent("透传");
        // 设置定时展示时间
        // template.setDuration("2015-01-16 11:40:00", "2015-01-16 12:24:00");
        return template;
    }
    */

    public static NotificationTemplate notificationTemplateDemo(String appId, String appkey) {
        NotificationTemplate template = new NotificationTemplate();
        // 设置APPID与APPKEY
        template.setAppId(appId);
        template.setAppkey(appkey);
        // 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
        template.setTransmissionType(1);
        template.setTransmissionContent("请输入您要透传的内容");
        // 设置定时展示时间
        // template.setDuration("2015-01-16 11:40:00", "2015-01-16 12:24:00");

        Style0 style = new Style0();
        // 设置通知栏标题与内容
        style.setTitle("请输入通知栏标题");
        style.setText("请输入通知栏内容");
        // 配置通知栏图标
        style.setLogo("icon.png");
        // 配置通知栏网络图标
        style.setLogoUrl("");
        // 设置通知是否响铃，震动，或者可清除
        style.setRing(true);
        style.setVibrate(true);
        style.setClearable(true);
        template.setStyle(style);
        return template;
    }
    public static void pushMessage(String simpleMsg,String transmissionContent,String CID){
        // https连接
//        IGtPush push = new IGtPush(appkey, masterSecret, true);
        // 此处true为https域名，false为http，默认为false。Java语言推荐使用此方式
        IGtPush push = new IGtPush(host, appkey, masterSecret);
        // host为域名，根据域名区分是http协议/https协议
//        LinkTemplate template = linkTemplateDemo(appId,appkey,title,content);
        //        getTemplate();
//        getDictionaryAlertMsg();
        SingleMessage message = new SingleMessage();
        message.setOffline(true);
        // 离线有效时间，单位为毫秒，可选
        message.setOfflineExpireTime(24 * 3600 * 1000);
//        message.setData(getTemplate(appId,appkey));
        message.setData(getTemplate(simpleMsg,transmissionContent));

        message.setPushNetWorkType(0); // 可选，判断是否客户端是否wifi环境下推送，1为在WIFI环境下，0为不限制网络环境。
        Target target = new Target();
        target.setAppId(appId);
        target.setClientId(CID);
        // 用户别名推送，cid和用户别名只能2者选其一
        // String alias = "个";
        // target.setAlias(alias);
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

    public static TransmissionTemplate getTemplate(String simpleMsg,String transmissionContent) {
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);
        template.setAppkey(appkey);
        template.setTransmissionContent(transmissionContent);
        template.setTransmissionType(1);
        APNPayload payload = new APNPayload();
        //在已有数字基础上加1显示，设置为-1时，在已有数字上减1显示，设置为数字时，显示指定数字
//        payload.setAutoBadge("-3");
        payload.setContentAvailable(1);
//        payload.setSound("default");
        payload.setCategory("$由客户端定义");

        //简单模式APNPayload.SimpleMsg
        payload.setAlertMsg(new APNPayload.SimpleAlertMsg(simpleMsg));

        //字典模式使用APNPayload.DictionaryAlertMsg
        //payload.setAlertMsg(getDictionaryAlertMsg());

//        //设置语音播报类型，int类型，0.不可用 1.播放body 2.播放自定义文本
//        apnpayload.setVoicePlayType(0);
//        //设置语音播报内容，String类型，非必须参数，用户自定义播放内容，仅在voicePlayMessage=2时生效
//        //注：当"定义类型"=2, "定义内容"为空时则忽略不播放
//        apnpayload.setVoicePlayMessage("定义内容");

        // 添加多媒体资源
        payload.addMultiMedia(new MultiMedia().setResType(MultiMedia.MediaType.video)
                .setResUrl("http://ol5mrj259.bkt.clouddn.com/test2.mp4")
                .setOnlyWifi(true));
//需要使用IOS语音推送，请使用VoIPPayload代替APNPayload
// VoIPPayload payload = new VoIPPayload();
// JSONObject jo = new JSONObject();
// jo.put("key1","value1");
//         payload.setVoIPPayload(jo.toString());
//
        template.setAPNInfo(payload);
        return template;
    }

    private static APNPayload.DictionaryAlertMsg getDictionaryAlertMsg(){
        APNPayload.DictionaryAlertMsg alertMsg = new APNPayload.DictionaryAlertMsg();
        alertMsg.setBody("body");
        alertMsg.setActionLocKey("ActionLockey");
        alertMsg.setLocKey("LocKey");
        alertMsg.addLocArg("loc-args");
        alertMsg.setLaunchImage("launch-image");
        // iOS8.2以上版本支持
        alertMsg.setTitle("Title");
        alertMsg.setTitleLocKey("TitleLocKey");
        alertMsg.addTitleLocArg("TitleLocArg");
        return alertMsg;
    }

    public static LinkTemplate linkTemplateDemo(String appId, String appKey,String title,String content) {
        LinkTemplate template = new LinkTemplate();
        // 设置APPID与APPKEY
        template.setAppId(appId);
        template.setAppkey(appKey);

        Style0 style = new Style0();
        // 设置通知栏标题与内容
        style.setTitle(title);
        style.setText(content);
        // 配置通知栏图标
//        style.setLogo("icon.png");
        // 配置通知栏网络图标
        style.setLogoUrl("");
        // 设置通知是否响铃，震动，或者可清除
        style.setRing(true);
        style.setVibrate(true);
        style.setClearable(true);
        template.setStyle(style);

        // 设置打开的网址地址
        template.setUrl("http://www.getui.com");
        // 设置定时展示时间
        // template.setDuration("2015-01-16 11:40:00", "2015-01-16 12:24:00");
        return template;
    }

}
