package cn.wizzer.app.ins.modules.models;

import cn.wizzer.app.base.modules.models.base_airport;
import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.app.sys.modules.models.Sys_user;
import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;
import java.io.Serializable;

@Comment("智能柜表")
@Table("ins_cabinet")
public class Ins_cabinet extends BaseModel implements Serializable {

    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("柜名")
    @ColDefine(type = ColType.VARCHAR,width = 64)
    private String cabinetname;

    @Column
    @Comment("柜编号")
    @ColDefine(type = ColType.VARCHAR,width = 64)
    private String cabinetnum;

    @Column
    @Comment("地址")
    @ColDefine(type = ColType.VARCHAR,width = 64)
    private String address;

    @Column
    @Comment("状态:0上线,1下线 //2018.09.06暂改为保留字段")
    @ColDefine(type = ColType.INT,width = 10)
    private int pstatus;

    @Column
    @Comment("机场ID")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String airportid;
    @One(field="airportid")
    private base_airport baseAirport;

    @Column
    @Comment("智能柜使用者ID")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String userId;
    @One(field="userId")
    private Sys_user sysUser;

    @Column
    @Comment("使用状态:0空闲 1使用中")
    @ColDefine(type = ColType.INT,width = 10)
    private int useStatus;

    @Column
    @Comment("初始化状态:0未初始化 1已初始化")
    @ColDefine(type = ColType.INT,width = 10)
    private int initStatus;
    @Column
    @Comment("串口号")
    @ColDefine(type = ColType.VARCHAR,width = 64)
    private String serialPort;

    @Column
    @Comment("柜子类型")
    @ColDefine(type = ColType.VARCHAR,width = 64)
    private String cabinetType;
    @One(field = "cabinetType")
    private Sys_dict sysType;

    public String getCabinetType() {
        return cabinetType;
    }

    public void setCabinetType(String cabinetType) {
        this.cabinetType = cabinetType;
    }

    public Sys_dict getSysType() {
        return sysType;
    }

    public void setSysType(Sys_dict sysType) {
        this.sysType = sysType;
    }

    public String getSerialPort() {
        return serialPort;
    }

    public void setSerialPort(String serialPort) {
        this.serialPort = serialPort;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public base_airport getBaseAirport() {
        return baseAirport;
    }

    public void setBaseAirport(base_airport baseAirport) {
        this.baseAirport = baseAirport;
    }
    public String getCabinetname() {
        return cabinetname;
    }

    public void setCabinetname(String cabinetname) {
        this.cabinetname = cabinetname;
    }

    public String getCabinetnum() {
        return cabinetnum;
    }

    public void setCabinetnum(String cabinetnum) {
        this.cabinetnum = cabinetnum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPstatus() {
        return pstatus;
    }

    public void setPstatus(int pstatus) {
        this.pstatus = pstatus;
    }

    public String getAirportid() {
        return airportid;
    }
    public void setAirportid(String airportid) {
        this.airportid = airportid;
    }

    public String getUserId() {
        return userId;
    }

    public int getUseStatus() {
        return useStatus;
    }

    public void setUseStatus(int useStatus) {
        this.useStatus = useStatus;
    }

    public int getInitStatus() {
        return initStatus;
    }

    public void setInitStatus(int initStatus) {
        this.initStatus = initStatus;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Sys_user getSysUser() {
        return sysUser;
    }

    public void setSysUser(Sys_user sysUser) {
        this.sysUser = sysUser;
    }

    //状态
    public String getPstatusname(){
        switch (pstatus){
            case 0:return "上线";
            case 1:return "下线";
            default:return "";
        }
    }
    //使用状态
    public String getUseStatusName(){
        switch (pstatus){
            case 0:return "未使用";
            case 1:return "使用中";
            default:return "";
        }
    }
    //使用状态
    public String getInitStatusName(){
        switch (pstatus){
            case 0:return "未初始化";
            case 1:return "已初始化";
            default:return "";
        }
    }
}
