package cn.wizzer.app.ins.modules.models;

import cn.wizzer.app.base.modules.models.base_person;
import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

@Table("ins_orderentry")
@Comment("订单工具表")
public class Ins_orderentry extends BaseModel implements Serializable {
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;
    @Column
    @Comment("基础工具ID")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String baseInsId;
    @One(field = "baseInsId")
    private Ins_baseins insBaseins;


    @Column
    @Comment("工具ID")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String insId;
    @One(field = "insId")
    private Ins_instrument instrument;


    @Column
    @Comment("订单ID")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String orderid;
    @One(field = "orderid")
    private Ins_order insOrder;


    @Column
//    @Comment("状态 0未借用 1.待借用 2.借用中 3.已归还(保留字段:暂从对应工具取出状态)")
    @Comment("状态 1.待借用 2.借用中 3.已归还")
    @ColDefine(type = ColType.INT,width = 20)
    private int pstatus;

    @Column
    @Comment("是否超出订单，默认为0:保留字段")
    @ColDefine(type = ColType.BOOLEAN,width = 1)
    private boolean outorder;

    @Column
    @Comment("超出数量:保留字段")
    @ColDefine(type = ColType.FLOAT)
    private double outnumber;

    @Column
    @Comment("操作者")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String operator;
    @One(field = "operator")
    private base_person basePerson;

    @Column
    @Comment("选择标志：时间戳")
    @ColDefine(type = ColType.VARCHAR,width = 40)
    private String dateflag;

    public String getDateflag() {
        return dateflag;
    }

    public void setDateflag(String dateflag) {
        this.dateflag = dateflag;
    }

    /* @Column
    @Comment("操作时间")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String operatetime;
    */
//    @Column
//    @Comment("开始时间")
//    @ColDefine(type = ColType.VARCHAR,width = 32)
//    private String starttime;

    @Column
    @Comment("订单工具总类（工具、耗材、工具包、工具车）")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String itype;
    @One(field = "itype")
    private Sys_dict sys_itype;
    @Column
    @Comment("订单状态 6~8 小时推送状态1  8小时后(包括8小时)推送状态2")
    @ColDefine(type = ColType.INT,width = 20)
    private int msgStatus;

    @Column
    @Comment("借出时间")
    @ColDefine(type = ColType.VARCHAR,width = 64)
    private String borrowTime;

    public String getBorrowTime () {
        return borrowTime;
    }

    public void setBorrowTime(String borrowTime) {
        this.borrowTime = borrowTime;
    }

    public Sys_dict getSys_itype() {
        return sys_itype;
    }

    public void setSys_itype(Sys_dict sys_itype) {
        this.sys_itype = sys_itype;
    }

    public int getMsgStatus() {
        return msgStatus;
    }

    public void setMsgStatus(int msgStatus) {
        this.msgStatus = msgStatus;
    }

    public Ins_baseins getInsBaseins() {
        return insBaseins;
    }

    public void setInsBaseins(Ins_baseins insBaseins) {
        this.insBaseins = insBaseins;
    }

    public String getBaseInsId() {
        return baseInsId;
    }

    public void setBaseInsId(String baseInsId) {
        this.baseInsId = baseInsId;
    }

    public base_person getBasePerson() {
        return basePerson;
    }

    public void setBasePerson(base_person basePerson) {
        this.basePerson = basePerson;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public int getPstatus() {
        return pstatus;
    }

    public void setPstatus(int pstatus) {
        this.pstatus = pstatus;
    }

    public boolean isOutorder() {
        return outorder;
    }

    public void setOutorder(boolean outorder) {
        this.outorder = outorder;
    }

    public double getOutnumber() {
        return outnumber;
    }

    public void setOutnumber(double outnumber) {
        this.outnumber = outnumber;
    }
    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }


    public String getInsId() {
        return insId;
    }

    public void setInsId(String insId) {
        this.insId = insId;
    }
    public Ins_instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Ins_instrument instrument) {
        this.instrument = instrument;
    }

    public Ins_order getInsOrder() {
        return insOrder;
    }

    public void setInsOrder(Ins_order insOrder) {
        this.insOrder = insOrder;
    }
    public String getPstatusname(){
        switch (pstatus){
            case 0 :return "未借用";
            case 1 :return "待借用";
            case 2 :return "借用中";
            case 3 :return "已归还";
            default:return "";
        }
    }

    public String getItype() {
        return itype;
    }

    public void setItype(String itype) {
        this.itype = itype;
    }
}
