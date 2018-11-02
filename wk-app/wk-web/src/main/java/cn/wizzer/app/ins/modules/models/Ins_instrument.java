package cn.wizzer.app.ins.modules.models;

import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.app.sys.modules.models.Sys_unit;
import cn.wizzer.framework.base.dao.entity.annotation.Ref;
import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;
@Table("ins_instrument")
@Comment("工具实例表")
public class Ins_instrument extends BaseModel implements Serializable{
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;
    @Column
//    @Comment("工具编号(保留字段:暂时不取本类的,取ins_baseins中的insnum)")
    @Comment("201807181443 改成自己输入编号,不从ins_baseins取")
    // 201807181443 改成自己输入编号,不从ins_baseins取
    @ColDefine(type = ColType.VARCHAR,width = 64)
    private String insnum;

    @Column
    @Comment("基础工具ID")
    @ColDefine(type = ColType.VARCHAR,width = 64)
    private String baseInsId;
    @One(field = "baseInsId")
    private Ins_baseins insBaseins;
    @Column
    @Comment("工具图片:保留字段,暂时取baseins中的")
    @ColDefine(type = ColType.VARCHAR,width = 255)
    private String toolPic;

    @Column
    @Comment("所在仓位,暂时无用")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String insspaceid;
    @One(field = "insspaceid")
    private Ins_space insSpace;

    @Column
    @Comment("备注")
    @ColDefine(type = ColType.VARCHAR,width = 255)
    private String describ;

    @Column
    @Comment("射频标签")
    @ColDefine(type = ColType.VARCHAR,width = 64)
    @Ref(Ins_rfid.class)
    private String rfid;
    @One(field = "rfid")
    private Ins_rfid insRfid;
    @Column
    @Comment("使用状态 0未借用  2.借用中")
    @ColDefine(type = ColType.INT,width = 20)
    private int pstatus;

    @Column
    @Comment("上线状态 0下线  1上线")
    @ColDefine(type = ColType.INT,width = 20)
    private int linePstatus;

 /*   @Column
    @Comment("工具类型 0 工具包/柜，1 普通零散工具，2 耗材")
    @ColDefine(type = ColType.INT,width = 20)
    private int instype;*/

    public int getLinePstatus() {
        return linePstatus;
    }
    public void setLinePstatus(int linePstatus) {
        this.linePstatus = linePstatus;
    }

    public String getToolPic() {
        return toolPic;
    }

    public void setToolPic(String toolPic) {
        this.toolPic = toolPic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInsnum() {
        return insnum;
    }

    public void setInsnum(String insnum) {
        this.insnum = insnum;
    }

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



    public int getPstatus() {
        return pstatus;
    }

    public void setPstatus(int pstatus) {
        this.pstatus = pstatus;
    }

    public String getInsspaceid() {
        return insspaceid;
    }

    public void setInsspaceid(String insspaceid) {
        this.insspaceid = insspaceid;
    }

    public Ins_space getInsSpace() {
        return insSpace;
    }

    public void setInsSpace(Ins_space insSpace) {
        this.insSpace = insSpace;
    }

    public String getDescrib() {
        return describ;
    }

    public void setDescrib(String describ) {
        this.describ = describ;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public Ins_rfid getInsRfid() {
        return insRfid;
    }

    public void setInsRfid(Ins_rfid insRfid) {
        this.insRfid = insRfid;
    }

    public String getLinePstatusname(){
        switch (linePstatus){
            case 0 : return "上线";
            case 1 : return "下线";
            default: return "";
        }
    }
}
