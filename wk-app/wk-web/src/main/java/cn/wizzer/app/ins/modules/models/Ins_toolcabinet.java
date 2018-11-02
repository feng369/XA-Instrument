package cn.wizzer.app.ins.modules.models;

import cn.wizzer.app.base.modules.models.base_person;
import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

@Table("ins_toolcabinet")
@Comment("工具箱列表")
public class Ins_toolcabinet extends BaseModel {
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("数量")
    @ColDefine(type = ColType.AUTO)
    private double number;
    @Column
    @Comment("状态 0未勾选  1已勾选 : 保留字段")
    @ColDefine(type = ColType.INT,width = 20)
    private int pstatus;

    @Column
    @Comment("借用员工ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String personid;
    @One(field = "personid")
    private base_person basePerson;

    @Column
    @Comment("基础工具ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String baseInsId;
    @One(field = "baseInsId")
    private Ins_baseins insBaseins;

    public String getBaseInsId() {
        return baseInsId;
    }

    public void setBaseInsId(String baseInsId) {
        this.baseInsId = baseInsId;
    }

    public Ins_baseins getInsBaseins() {
        return insBaseins;
    }

    public void setInsBaseins(Ins_baseins insBaseins) {
        this.insBaseins = insBaseins;
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

    public String getPersonid() {
        return personid;
    }

    public void setPersonid(String personid) {
        this.personid = personid;
    }
    public double getNumber() {
        return number;
    }

    public void setNumber(double number) {
        this.number = number;
    }

    public int getPstatus() {
        return pstatus;
    }

    public void setPstatus(int pstatus) {
        this.pstatus = pstatus;
    }
}
