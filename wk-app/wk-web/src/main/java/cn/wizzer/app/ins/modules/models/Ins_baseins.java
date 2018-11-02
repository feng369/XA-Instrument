package cn.wizzer.app.ins.modules.models;

import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.app.sys.modules.models.Sys_unit;
import cn.wizzer.framework.base.dao.entity.annotation.Ref;
import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Table("ins_baseins")
@Comment("基础工具表")
public  class Ins_baseins extends BaseModel implements Serializable {
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;
    @Column
    @Comment("基础工具编号")
    @ColDefine(type = ColType.VARCHAR,width = 64)
    private String insnum;
    @Column
    @Comment("工具名称")
    @ColDefine(type = ColType.VARCHAR,width = 64)
    private String insname;
    @Column
    @Comment("工具参数")
    @ColDefine(type = ColType.VARCHAR,width = 120)
    private String insparam;

    @Column
    @Comment("工具型号")
    @ColDefine(type = ColType.VARCHAR,width = 120)
        private String insmodel;

    @Column
    @Comment("工具单位")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String insunit;
    @One(field = "insunit")
    private Sys_unit sys_insunit;


    @Column
    @Comment("工具类别")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String instype;
    @One(field = "instype")
    private Sys_dict dict_instype;

    @Column
    @Comment("工具图片")
    @ColDefine(type = ColType.VARCHAR,width = 255)
    private String toolPic;

    @Column
    @ColDefine(type = ColType.INT )
    @Comment("上线实体工具总数量")
    private int totalCount;

    @Column
    @ColDefine(type = ColType.INT )
    @Comment("上线实体工具锁定数量")
    private int reserveCount;

    @Column
    @ColDefine(type = ColType.INT )
    @Comment("上线实体工具可用数量(未借出，且未生成订单)")
    private int useableCount;

    @Column
    @Comment("仓位ID")
    @ColDefine(type = ColType.VARCHAR,width = 40)
    private String spaceId;
    @One(field = "spaceId")
    private Ins_space insSpace;
//    @ManyMany(relation = "ins_baseins_space",from ="baseInsId",to="spaceId")
//    private List<Ins_space> spaces = new ArrayList<>();

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public Ins_space getInsSpace() {
        return insSpace;
    }

    public void setInsSpace(Ins_space insSpace) {
        this.insSpace = insSpace;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getReserveCount() {
        return reserveCount;
    }

    public void setReserveCount(int reserveCount) {
        this.reserveCount = reserveCount;
    }

    public int getUseableCount() {
        return useableCount;
    }

    public void setUseableCount(int useableCount) {
        this.useableCount = useableCount;
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

    public String getInsname() {
        return insname;
    }

    public void setInsname(String insname) {
        this.insname = insname;
    }

    public String getInsparam() {
        return insparam;
    }

    public void setInsparam(String insparam) {
        this.insparam = insparam;
    }

    public String getInsmodel() {
        return insmodel;
    }

    public void setInsmodel(String insmodel) {
        this.insmodel = insmodel;
    }

    public String getInsunit() {
        return insunit;
    }

    public void setInsunit(String insunit) {
        this.insunit = insunit;
    }

    public Sys_unit getSys_insunit() {
        return sys_insunit;
    }

    public void setSys_insunit(Sys_unit sys_insunit) {
        this.sys_insunit = sys_insunit;
    }

    public String getInstype() {
        return instype;
    }

    public void setInstype(String instype) {
        this.instype = instype;
    }

    public Sys_dict getDict_instype() {
        return dict_instype;
    }

    public void setDict_instype(Sys_dict dict_instype) {
        this.dict_instype = dict_instype;
    }

    public String getToolPic() {
        return toolPic;
    }

    public void setToolPic(String toolPic) {
        this.toolPic = toolPic;
    }

    /*public List<Ins_space> getSpaces() {
        return spaces;
    }

    public void setSpaces(List<Ins_space> spaces) {
        this.spaces = spaces;
    }*/
}
