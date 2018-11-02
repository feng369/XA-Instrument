package cn.wizzer.app.ins.modules.models;

import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;
@Table("ins_baseins_space")
@Comment("基础工具仓位表")
public class Ins_baseins_space extends BaseModel implements Serializable {
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("基础工具ID")
    @ColDefine(type = ColType.VARCHAR,width = 64)
    private String baseInsId;
    @One(field = "baseInsId")
    private Ins_baseins insBaseins;
    @Column
    @Comment("仓位ID")
    @ColDefine(type = ColType.VARCHAR,width = 64)
    private String spaceId;
    @One(field = "spaceId")
    private Ins_space insSpace;
    @Column
    @Comment("启用状态")
    @ColDefine(type = ColType.INT,width = 20)
    private int pstatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBaseInsId() {
        return baseInsId;
    }

    public void setBaseInsId(String baseInsId) {
        this.baseInsId = baseInsId;
    }

    public int getPstatus() {
        return pstatus;
    }

    public void setPstatus(int pstatus) {
        this.pstatus = pstatus;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public Ins_baseins getInsBaseins() {
        return insBaseins;
    }

    public void setInsBaseins(Ins_baseins insBaseins) {
        this.insBaseins = insBaseins;
    }

    public Ins_space getInsSpace() {
        return insSpace;
    }

    public void setInsSpace(Ins_space insSpace) {
        this.insSpace = insSpace;
    }
}
