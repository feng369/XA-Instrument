package cn.wizzer.app.getui;

public class GeTuiModel {

    /**
     * 个推接口地址
     */
    private String host = "http://sdk.open.api.igexin.com/apiex.htm";
    /**
     * 个推申请的应用ID
     */
    private String appId = "MXkqnhNlO67iowxGEkFeV6";
    /**
     * 个推申请的应用KEY
     */
    private String appKey = "T6EoiBN5jw9GEBujFfeGv3";

    /**
     * 个推申请的安全密钥mastersecret
     */
    private String master = "enqH3xf2SLAYeuTveJRTQ4";
    /**
     * 通知的标题
     */
    private String title = "标题";

    /**
     * 推送的内容
     */
    private String content;

    /**
     * 透传内容
     */
    private String transContent;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTransContent() {
        return transContent;
    }

    public void setTransContent(String transContent) {
        this.transContent = transContent;
    }
}
