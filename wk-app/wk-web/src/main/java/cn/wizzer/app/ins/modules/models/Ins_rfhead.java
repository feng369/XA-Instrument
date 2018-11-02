package cn.wizzer.app.ins.modules.models;

import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

@Table("ins_rfhead")
@Comment("射频读头表")
public class Ins_rfhead extends BaseModel implements Serializable {
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;


    @Column
    @Comment("rf读头名称")
    @ColDefine(type = ColType.VARCHAR,width = 64)
    private String rfname;

    @Column
    @Comment("rf读头编号")
    @ColDefine(type = ColType.VARCHAR,width = 64)
    private String number;

    @Column
    @Comment("状态")
    @ColDefine(type = ColType.INT,width = 20)
    private int pstatus;

    @Column
    @Comment("机场ID")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String airportid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRfname() {
        return rfname;
    }

    public void setRfname(String rfname) {
        this.rfname = rfname;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
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
}
