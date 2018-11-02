package cn.wizzer.app.ins.modules.models;

import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;
@Table("ins_transmit")
@Comment("移交表")
public class Ins_transmit extends BaseModel implements Serializable {
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    /*@Column
    @Comment("订单ID")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String orderid;


    @Column
    @Comment("工具ID")
    @ColDefine(type = ColType.INT,width = 20)
    private String insid;
    */

    @Column
    @Comment("旧的订单明细ID")
    @ColDefine(type = ColType.VARCHAR,width = 40)
    private String oldOrderentryId;

    @Column
    @Comment("新的订单明细ID")
    @ColDefine(type = ColType.VARCHAR,width = 40)
    private String orderentryId;


    @Column
    @Comment("移交人")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String passer;

    @Column
    @Comment("被移交人")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String operator;

    @Column
    @Comment("操作时间")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String operatetime;

    @Column
    @Comment("选择标志：时间戳")
    @ColDefine(type = ColType.VARCHAR,width = 40)
    private String dateflag;

    public String getOldOrderentryId() {
        return oldOrderentryId;
    }

    public void setOldOrderentryId(String oldOrderentryId) {
        this.oldOrderentryId = oldOrderentryId;
    }

    public String getDateflag() {
        return dateflag;
    }

    public void setDateflag(String dateflag) {
        this.dateflag = dateflag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPasser() {
        return passer;
    }

    public void setPasser(String passer) {
        this.passer = passer;
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

    public String getOrderentryId() {
        return orderentryId;
    }

    public void setOrderentryId(String orderentryId) {
        this.orderentryId = orderentryId;
    }
}
