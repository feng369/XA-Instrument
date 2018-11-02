package cn.wizzer.app.ins.modules.models;

import cn.wizzer.app.base.modules.models.base_airport;
import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;
import java.io.Serializable;

@Table("ins_lock")
@Comment("锁表")
public class Ins_lock extends BaseModel implements Serializable {

    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("编码")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String locknum;

    @Column
    @Comment("状态: 0关闭 1打开")
    @ColDefine(type = ColType.INT, width = 32)
    private int pstatus;

    @Column
    @Comment("绑定仓位状态: 0未绑定 1已绑定")
    @ColDefine(type = ColType.INT, width = 32)
    private int bindStatus;

    @Column
    @Comment("机场ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String airportId;
    @One(field = "airportId")
    private base_airport baseAirport;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocknum() {
        return locknum;
    }

    public void setLocknum(String locknum) {
        this.locknum = locknum;
    }

    public int getPstatus() {
        return pstatus;
    }

    public void setPstatus(int pstatus) {
        this.pstatus = pstatus;
    }

    public String getAirportId() {
        return airportId;
    }

    public void setAirportId(String airportId) {
        this.airportId = airportId;
    }

    public base_airport getBaseAirport() {
        return baseAirport;
    }

    public void setBaseAirport(base_airport baseAirport) {
        this.baseAirport = baseAirport;
    }
    public int getBindStatus() {
        return bindStatus;
    }

    public void setBindStatus(int bindStatus) {
        this.bindStatus = bindStatus;
    }
}
