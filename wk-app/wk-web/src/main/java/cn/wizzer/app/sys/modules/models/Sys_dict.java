package cn.wizzer.app.sys.modules.models;

import cn.wizzer.app.base.modules.models.base_airport;
import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.DB;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by wizzer on 2016/6/21.
 */
@Table("sys_dict")
@TableIndexes({@Index(name = "INDEX_SYS_DICT_PATH", fields = {"path"}, unique = true)})
public class Sys_dict extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("父级ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String parentId;

    @Column
    @Comment("树路径")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String path;

    @Column
    @Comment("名称")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String name;

    @Column
    @Comment("机构编码")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String code;

    @Column
    @Comment("排序字段")
    @Prev({
            @SQL(db= DB.MYSQL,value = "SELECT IFNULL(MAX(location),0)+1 FROM sys_dict"),
            @SQL(db= DB.ORACLE,value = "SELECT COALESCE(MAX(location),0)+1 FROM sys_dict")
    })
    private Integer location;

    @Column
    @Comment("有子节点")
    private boolean hasChildren;

    @Column
    @Comment("图片")
    @ColDefine(type = ColType.VARCHAR,width = 255)
    private String picture;

    @Column
    @Comment("机场ID")
    @ColDefine(type = ColType.VARCHAR,width = 64)
    private String airportId;
    @One(field = "airportId")
    private base_airport baseAirport;
    public String getAirportId() {
        return airportId;
    }

    public base_airport getBaseAirport() {
        return baseAirport;
    }

    public void setBaseAirport(base_airport baseAirport) {
        this.baseAirport = baseAirport;
    }

    public void setAirportId(String airportId) {
        this.airportId = airportId;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getLocation() {
        return location;
    }

    public void setLocation(Integer location) {
        this.location = location;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }
}
