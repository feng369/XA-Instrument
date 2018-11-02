package cn.wizzer.app.ins.modules.models;

import cn.wizzer.app.base.modules.models.base_airport;
import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

@Table("ins_rfid")
@Comment("射频标签表")
public class Ins_rfid extends BaseModel implements Serializable {
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("rfid号码")
    @ColDefine(type = ColType.VARCHAR, width = 64)
    private String number;

    @Column
    @Comment("状态 0:未使用 1:使用中")
    @ColDefine(type = ColType.INT, width = 20)
    private int pstatus;

    @Column
    @Comment("机场ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String airportid;
    @One(field = "airportid")
    private base_airport baseAirport;

    public base_airport getBaseAirport() {
        return baseAirport;
    }

    public void setBaseAirport(base_airport baseAirport) {
        this.baseAirport = baseAirport;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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


    public String getPstatusname() {
        switch (pstatus) {
            case 0:
                return "未使用";
            case 1:
                return "使用中";
            default:
                return "";
        }
    }
}
