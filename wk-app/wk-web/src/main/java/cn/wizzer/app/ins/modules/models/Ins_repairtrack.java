package cn.wizzer.app.ins.modules.models;

import cn.wizzer.app.base.modules.models.base_person;
import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

@Table("ins_repairtrack")
@Comment("维修历史对象")
public class Ins_repairtrack  extends BaseModel{
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("维修对象ID")
    @ColDefine(type = ColType.VARCHAR,width = 40)
    private String repairId;
    @One(field = "repairId")
    private Ins_repair insRepair;

    @Column
    @Comment("操作时间")
    @ColDefine(type = ColType.VARCHAR, width = 300)
    private String operatetime;

    @Column
    @Comment("操作人")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String operaterId;
    @One(field = "operaterId")
    private base_person basePerson;

    @Column
    @Comment("状态:1申请  2维修中 0完成 ")
    @ColDefine(type = ColType.INT, width = 10)
    private int pstatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRepairId() {
        return repairId;
    }

    public void setRepairId(String repairId) {
        this.repairId = repairId;
    }

    public Ins_repair getInsRepair() {
        return insRepair;
    }

    public void setInsRepair(Ins_repair insRepair) {
        this.insRepair = insRepair;
    }

    public String getOperatetime() {
        return operatetime;
    }

    public void setOperatetime(String operatetime) {
        this.operatetime = operatetime;
    }

    public String getOperaterId() {
        return operaterId;
    }

    public void setOperaterId(String operaterId) {
        this.operaterId = operaterId;
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
}
