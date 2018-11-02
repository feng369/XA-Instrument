package cn.wizzer.app.ins.modules.models;

import cn.wizzer.app.base.modules.models.base_airport;
import cn.wizzer.app.base.modules.models.base_person;
import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;
@Comment("订单表")
@Table("ins_order")
public class Ins_order extends BaseModel implements Serializable {
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("订单编号")
    @ColDefine(type = ColType.VARCHAR,width = 120)
    private String ordernum;
    @Column
    @Comment("机场ID")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String airportid;
    @One(field = "airportid")
    private base_airport baseAirport;
    @Column
    @Comment("航班号")
    @ColDefine(type = ColType.VARCHAR,width = 64)
    private String flightnum;

    @Column
    @Comment("机位")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String seatnum;

    @Column
    @Comment("操作人")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String operator;
    @One(field = "operator")
    private base_person basePerson;

    @Column
    @Comment("操作时间")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String operatetime;
    @Column
    @Comment("订单状态 -1草稿状态(备料前) 0:未完成 1.已完成")
    @ColDefine(type = ColType.INT,width = 20)
    private int pstatus;
    @Column
    @Comment("开始时间:保留字段")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String starttime;
    @Column
    @Comment("结束时间:保留字段")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String endtime;
    @Column
    @Comment("备注")
    @ColDefine(type = ColType.VARCHAR,width = 255)
    private String describ;
    @Column
    @Comment("订单状态 6~8 小时推送状态1  8小时后(包括8小时)推送状态2")
    @ColDefine(type = ColType.INT,width = 20)
    private int msgStatus;

    public int getMsgStatus() {
        return msgStatus;
    }

    public void setMsgStatus(int msgStatus) {
        this.msgStatus = msgStatus;
    }

    public base_airport getBaseAirport() {
        return baseAirport;
    }

    public void setBaseAirport(base_airport baseAirport) {
        this.baseAirport = baseAirport;
    }

    public base_person getBasePerson() {
        return basePerson;
    }

    public void setBasePerson(base_person basePerson) {
        this.basePerson = basePerson;
    }
    public int getPstatus() {
        return pstatus;
    }

    public void setPstatus(int pstatus) {
        this.pstatus = pstatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrdernum() {
        return ordernum;
    }

    public void setOrdernum(String ordernum) {
        this.ordernum = ordernum;
    }

    public String getAirportid() {
        return airportid;
    }

    public void setAirportid(String airportid) {
        this.airportid = airportid;
    }

    public String getFlightnum() {
        return flightnum;
    }

    public void setFlightnum(String flightnum) {
        this.flightnum = flightnum;
    }

    public String getSeatnum() {
        return seatnum;
    }

    public void setSeatnum(String seatnum) {
        this.seatnum = seatnum;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperatetime() {
        return operatetime;
    }

    public void setOperatetime(String operatetime) {
        this.operatetime = operatetime;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getDescrib() {
        return describ;
    }

    public void setDescrib(String describ) {
        this.describ = describ;
    }

    public String getPstatusname(){
        switch (pstatus){
            case 0 :return  "未完成";
            case 1 :return  "已完成";
            default: return  "";
        }
    }

}
