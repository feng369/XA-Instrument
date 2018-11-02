package cn.wizzer.app.ins.modules.models;

import cn.wizzer.app.base.modules.models.base_person;
import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;
@Table("ins_common")
@Comment("常用工具表")
public class Ins_common extends BaseModel implements Serializable{

    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("员工ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String personid;
    @One(field = "personid")
    private base_person basePerson;

    @Column
    @Comment("基础工具ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String baseinsId;
    @One(field = "baseinsId")
    private Ins_baseins insBaseins;
    @Column
    @Comment("数量")
    @ColDefine(type = ColType.AUTO)
    private double number;

    @Column
    @Comment("收藏类型: 最近借的  我的收藏")
    @ColDefine(type = ColType.VARCHAR,width = 64)
    private String commontype;
    @One(field = "commontype")
    private Sys_dict sys_commontype;

    public base_person getBasePerson() {
        return basePerson;
    }

    public void setBasePerson(base_person basePerson) {
        this.basePerson = basePerson;
    }

    public String getBaseinsId() {
        return baseinsId;
    }

    public void setBaseinsId(String baseinsId) {
        this.baseinsId = baseinsId;
    }

    public Ins_baseins getInsBaseins() {
        return insBaseins;
    }

    public void setInsBaseins(Ins_baseins insBaseins) {
        this.insBaseins = insBaseins;
    }

    public String getCommontype() {
        return commontype;
    }

    public void setCommontype(String commontype) {
        this.commontype = commontype;
    }

    public Sys_dict getSys_commontype() {
        return sys_commontype;
    }

    public void setSys_commontype(Sys_dict sys_commontype) {
        this.sys_commontype = sys_commontype;
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
}
