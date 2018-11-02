package cn.wizzer.app.ins.modules.models;

import cn.wizzer.app.base.modules.models.base_person;
import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

@Table("ins_repair")
@Comment("维修申报表")
public class Ins_repair extends BaseModel {
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("维修编号")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String repairNum;

    @Column
    @Comment("订单ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String orderId;
    @One(field = "orderId")
    private Ins_order order;

    @Column
    @Comment("状态: 0申请 1.维修  2.正常")
    @ColDefine(type = ColType.INT, width = 32)
    private int pstatus;

    @Column("orderentryId")
    @Comment("订单明细ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String orderentryId;
    @One(field = "orderentryId")
    private Ins_orderentry orderentry;

    @Column
    @Comment("实体工具ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String insId;
    @One(field = "insId")
    private Ins_instrument instrument;

    @Column
    @Comment("备注")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String describ;

    @Column
    @Comment("图片地址")
    @ColDefine(type = ColType.VARCHAR,width = 64)
    private String  imgPath;

    @Column
    @Comment("图片名称")
    @ColDefine(type = ColType.VARCHAR,width = 64)
    private String imgName;

    @Column
    @Comment("维修类型(保留字段)")
    @ColDefine(type = ColType.VARCHAR,width = 64)
    private String repairType;
    @One(field = "repairType")
    private Sys_dict sys_repair;

    @Column
    @Comment("申报人ID")
    @ColDefine(type = ColType.VARCHAR,width = 64)
    private String personId;
    @One(field = "personId")
    private base_person basePerson;

    @Column
    @Comment("申请时间")
    @ColDefine(type = ColType.VARCHAR,width = 64)
    private String  inTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Ins_order getOrder() {
        return order;
    }

    public void setOrder(Ins_order order) {
        this.order = order;
    }

    public int getPstatus() {
        return pstatus;
    }

    public void setPstatus(int pstatus) {
        this.pstatus = pstatus;
    }

    public String getOrderentryId() {
        return orderentryId;
    }

    public void setOrderentryId(String orderentryId) {
        this.orderentryId = orderentryId;
    }

    public Ins_orderentry getOrderentry() {
        return orderentry;
    }

    public void setOrderentry(Ins_orderentry orderentry) {
        this.orderentry = orderentry;
    }

    public String getInsId() {
        return insId;
    }

    public void setInsId(String insId) {
        this.insId = insId;
    }

    public Ins_instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Ins_instrument instrument) {
        this.instrument = instrument;
    }

    public String getDescrib() {
        return describ;
    }

    public void setDescrib(String describ) {
        this.describ = describ;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getRepairType() {
        return repairType;
    }

    public void setRepairType(String repairType) {
        this.repairType = repairType;
    }

    public Sys_dict getSys_repair() {
        return sys_repair;
    }

    public void setSys_repair(Sys_dict sys_repair) {
        this.sys_repair = sys_repair;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public base_person getBasePerson() {
        return basePerson;
    }

    public void setBasePerson(base_person basePerson) {
        this.basePerson = basePerson;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }
    public String getRepairNum() {
        return repairNum;
    }

    public void setRepairNum(String repairNum) {
        this.repairNum = repairNum;
    }
}
