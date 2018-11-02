package cn.wizzer.app.getui;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.APNTemplate;
import com.gexin.rp.sdk.template.LinkTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.nutz.dao.util.Pojos.log;

public class AppPush {
    //定义常量, appId、appKey、masterSecret 采用本文档 "第二步 获取访问凭证 "中获得的应用配置
  /*  private static String appId = "RhdYj9rhKI8DmxAOMXrs97";
    private static String appKey = "8AL16IBC456ZMUHyVlS7N7";
    private static String masterSecret = "VhTdT62TVT9bexkNO6TwA9";
    //  private static String url = "http://sdk.open.api.igexin.com/apiex.htm";*/
    private static String url = "http://sdk.open.api.igexin.com/apiex.htm";
    //  static String CID = "7ca55dfae3b58ffc6629c52434d929e6";
    private static String appId = "MXkqnhNlO67iowxGEkFeV6";
    private static String appKey = "T6EoiBN5jw9GEBujFfeGv3";
    private static String masterSecret = "enqH3xf2SLAYeuTveJRTQ4";
    private static long OFFLINE_EXPLIRE_TIME = 24 * 3600 * 1000;
    public static void main(String[] args) throws IOException {
        IGtPush push = new IGtPush(url, appKey, masterSecret);
        // 定义"点击链接打开通知模板"，并设置标题、内容、链接
        LinkTemplate template = new LinkTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        template.setTitle("啦啦啦,我是小蜜蜂");
        template.setText("测试测试,西安工具");
        template.setUrl("http://getui.com");
        List<String> appIds = new ArrayList<String>();
        appIds.add(appId);
        // 定义"AppMessage"类型消息对象，设置消息内容模板、发送的目标App列表、是否支持离线发送、以及离线消息有效期(单位毫秒)
        AppMessage message = new AppMessage();
        message.setData(template);
        message.setAppIdList(appIds);
        message.setOffline(true);
        message.setOfflineExpireTime(1000 * 600);
        IPushResult ret = push.pushMessageToApp(message);
        System.out.println(ret.getResponse().toString());
    }

 /*   public String iosSinglePush(String cid,GeTuiModel model) throws IOException {
        IGtPush push = new IGtPush(model.getHost(), model.getAppKey(),model.getMaster());
// 建立连接,开始鉴权
        push.connect();
        APNTemplate template = new APNTemplate();
        APNPayload apnpayload = new APNPayload();
        apnpayload.setSound("");
        APNPayload.DictionaryAlertMsg alertMsg = new APNPayload.DictionaryAlertMsg();
        alertMsg.setTitle(model.getTitle());
        alertMsg.setBody(model.getContent());
        apnpayload.setAlertMsg(alertMsg);
        template.setAPNInfo(apnpayload);
        SingleMessage singleMessage = new SingleMessage();
        singleMessage.setOffline(true);
        singleMessage.setOfflineExpireTime(OFFLINE_EXPLIRE_TIME);
        singleMessage.setData(template);
        IPushResult ret = push.pushAPNMessageToSingle(model.getAppId(), cid, singleMessage);
        log.info("消息推送:"+ret.getResponse().toString());
        return ret.getResponse().toString();
    }*/

    public static TransmissionTemplate transmissionTemplate(String appId,String appKey,String title,String msgStr,String tranMsg) {
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId );
        template.setAppkey(appKey);
        template.setTransmissionType(2); //收到消息是否立即启动应用:1为立即启动,2则广播等待客户端自启动
        template.setTransmissionContent(tranMsg); //透传内容,不支持转义字符
        try {

            APNPayload payload = new APNPayload();
            payload.setContentAvailable(1);
            payload.setSound("default");
            APNPayload.DictionaryAlertMsg alertMsg = new APNPayload.DictionaryAlertMsg();
            alertMsg.setTitle(title);
            alertMsg.setBody(msgStr);
            payload.setAlertMsg(alertMsg);
            template.setAPNInfo(payload);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return template;
}
}
