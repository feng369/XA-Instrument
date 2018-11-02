package cn.wizzer.app.ins.modules.models;

import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;
@Table("ins_space")
@Comment("仓位表")
public class Ins_space extends BaseModel implements Serializable {
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("仓位名称")
    @ColDefine(type = ColType.VARCHAR,width = 255)
    private String spacename;

    @Column
    @Comment("状态: 0关闭 1打开")
    @ColDefine(type = ColType.INT,width = 20)
    private int pstatus;

    @Column
    @Comment("仓位编号")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String spacenum;

    @Column
    @Comment("参数")
    @ColDefine(type = ColType.VARCHAR,width = 255)
    private String params;

    @Column
    @Comment("型号")
    @ColDefine(type = ColType.VARCHAR,width = 100)
    private String model;
    @Column
    @Comment("所在位置")
    @ColDefine(type = ColType.VARCHAR,width = 255)
    private String address;
    @Column
    @Comment("对应二维码(保留字段)")
    @ColDefine(type = ColType.VARCHAR,width = 64)
    private String barcode;

    @Column
    @Comment("所属智能柜")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String cabinetid;
    @One(field = "cabinetid")
    private Ins_cabinet insCabinet;

    @Column
    @Comment("锁ID")
    @ColDefine(type = ColType.VARCHAR,width = 64)
    private String lockId;
    @One(field = "lockId")
    private Ins_lock insLock;

    @Column
    @Comment("仓位图片路径")
    @ColDefine(type = ColType.VARCHAR,width = 64)
    private String imgpath;

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public Ins_lock getInsLock() {
        return insLock;
    }

    public void setInsLock(Ins_lock insLock) {
        this.insLock = insLock;
    }

    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId;
    }

    public Ins_cabinet getInsCabinet() {

        return insCabinet;
    }

    public void setInsCabinet(Ins_cabinet insCabinet) {
        this.insCabinet = insCabinet;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSpacename() {
        return spacename;
    }

    public void setSpacename(String spacename) {
        this.spacename = spacename;
    }

    public int getPstatus() {
        return pstatus;
    }

    public void setPstatus(int pstatus) {
        this.pstatus = pstatus;
    }

    public String getSpacenum() {
        return spacenum;
    }

    public void setSpacenum(String spacenum) {
        this.spacenum = spacenum;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getCabinetid() {
        return cabinetid;
    }

    public void setCabinetid(String cabinetid) {
        this.cabinetid = cabinetid;
    }
    //状态
    public String getPstatusname(){
        switch (pstatus){
            case 0:return "关闭";
            case 1:return "打开";
            default:return "";
        }
    }
}
