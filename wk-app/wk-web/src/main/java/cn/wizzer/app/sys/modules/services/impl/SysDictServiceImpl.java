package cn.wizzer.app.sys.modules.services.impl;

import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.app.sys.modules.services.SysDictService;
import cn.wizzer.app.web.commons.base.Globals;
import cn.wizzer.framework.base.ValidatException;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.framework.util.StringUtil;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.mvc.upload.TempFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by mac on 2017/1/19.
 */
@IocBean(args = {"refer:dao"})
public class SysDictServiceImpl extends BaseServiceImpl<Sys_dict> implements SysDictService {
    public SysDictServiceImpl(Dao dao) {
        super(dao);
    }


    /**
     * 通过code获取name
     *
     * @param code
     * @return
     */
    //@Cacheable
    public String getNameByCode(String code) {
        Sys_dict dict = this.fetch(Cnd.where("code", "=", code));
        return dict == null ? "" : dict.getName();
    }

    public String getIdByCode(String code) {
        Sys_dict dict = this.fetch(Cnd.where("code", "=", code));
        return dict == null ? "" : dict.getId();
    }

    /**
     * 通过id获取name
     *
     * @param id
     * @return
     */
    //@Cacheable
    public String getNameById(String id) {
        Sys_dict dict = this.fetch(id);
        return dict == null ? "" : dict.getName();
    }

    /**
     * 通过name获取id
     *
     * @param name
     * @return
     */
    //@Cacheable
    public String getIdByName(String name) {
        Sys_dict dict = this.fetch(Cnd.where("name", "=", name));
        return dict == null ? "" : dict.getId();

    }

    /**
     * 通过name和code获取id
     *
     * @param name
     * @param code
     * @return
     */
    //@Cacheable
    public String getIdByNameAndCode(String name, String code) {
        Sys_dict dict = this.fetch(Cnd.where("name", "=", name).and("code", "=", code));
        return dict == null ? "" : dict.getId();

    }

    /**
     * 通过树path获取下级列表
     *
     * @param path
     * @return
     */
    //@Cacheable
    public List<Sys_dict> getSubListByPath(String path) {
        return this.query(Cnd.where("path", "like", Strings.sNull(path) + "____").asc("location"));
    }

    /**
     * 通过父id获取下级列表
     *
     * @param id
     * @return
     */
    //@Cacheable
    public List<Sys_dict> getSubListById(String id) {
        return this.query(Cnd.where("parentId", "=", Strings.sNull(id)).asc("location"));
    }

    /**
     * 通过code获取下级列表
     *
     * @param code
     * @return
     */
    //@Cacheable
    public List<Sys_dict> getSubListByCode(String code) {
        Sys_dict dict = this.fetch(Cnd.where("code", "=", code));
        return dict == null ? new ArrayList<>() : this.query(Cnd.where("parentId", "=", Strings.sNull(dict.getId())).asc("location"));
    }

    /**
     * 通过path获取下级map
     *
     * @param path
     * @return
     */
    //@Cacheable
    public Map getSubMapByPath(String path) {
        return this.getMap(Sqls.create("select code,name from sys_dict where path like @path order by location asc").setParam("path", path + "____"));
    }

    /**
     * 通过id获取下级map
     *
     * @param id
     * @return
     */
    //@Cacheable
    public Map getSubMapById(String id) {
        return this.getMap(Sqls.create("select code,name from sys_dict where parentId = @id order by location asc").setParam("id", id));
    }

    /**
     * 通过code获取下级map
     *
     * @param code
     * @return
     */
    //@Cacheable
    public Map getSubMapByCode(String code) {
        Sys_dict dict = this.fetch(Cnd.where("code", "=", code));
        return dict == null ? new HashMap() : this.getMap(Sqls.create("select code,name from sys_dict where parentId = @id order by location asc").setParam("id", dict.getId()));
    }

    /**
     * 新增单位
     *
     * @param dict
     * @param pid
     */
    @Aop(TransAop.READ_COMMITTED)
    public void save(Sys_dict dict, String pid) {
        String path = "";
        if (!Strings.isEmpty(pid)) {
            Sys_dict pp = this.fetch(pid);
            path = pp.getPath();
        }
        dict.setPath(getSubPath("sys_dict", "path", path));
        dict.setParentId(pid);
        dao().insert(dict);
        if (!Strings.isEmpty(pid)) {
            this.update(Chain.make("hasChildren", true), Cnd.where("id", "=", pid));
        }
    }

    /**
     * 级联删除单位
     *
     * @param dict
     */
    @Aop(TransAop.READ_COMMITTED)
    public void deleteAndChild(Sys_dict dict) {
        dao().execute(Sqls.create("delete from sys_dict where path like @path").setParam("path", dict.getPath() + "%"));
        if (!Strings.isEmpty(dict.getParentId())) {
            int count = count(Cnd.where("parentId", "=", dict.getParentId()));
            if (count < 1) {
                dao().execute(Sqls.create("update sys_dict set hasChildren=0 where id=@pid").setParam("pid", dict.getParentId()));
            }
        }
    }

    public String getDictOtypeId() {
        Sys_dict sysDict = this.fetch(Cnd.where("name", "=", "航线运输"));
        if (sysDict != null) {
            return sysDict.getId();
        }
        return "";
    }

    public List<Map<String, Object>> bindDDLbyM(String path, Integer pagenumber, Integer pagesize) {
        Pager pager = new Pager(1);
        if (pagenumber != null && pagenumber.intValue() > 0) {
            pager.setPageNumber(pagenumber.intValue());
        }
        if (pagesize == null || pagesize > 10) {
            pager.setPageSize(10);
        } else {
            pager.setPageSize(pagesize.intValue());
        }
        List<Sys_dict> sysDicts = this.query(Cnd.where("path", "like", path + "%").and("path", "<>", path), pager);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Sys_dict sysDict : sysDicts) {
            Map<String, Object> map = new HashMap<>();
            /*@ReturnKey(key = "id", description = "id")
                    ,@ReturnKey(key = "name", description = "名称")
                    ,@ReturnKey(key = "code", description = "机构编码")
                    ,@ReturnKey(key = "location", description = "排序字段")
                    ,@ReturnKey(key = "hasChildren", description = "是否有子节点")}*/
            map.put("id", sysDict.getId());
            map.put("name", sysDict.getName());
            map.put("code", sysDict.getCode());
            map.put("location", sysDict.getLocation());
            map.put("hasChildren", sysDict.isHasChildren());
            list.add(map);
        }
        return list;
    }

    @Aop(TransAop.READ_COMMITTED)
    public void uploadTools(TempFile tf, String id) throws IOException {
        String tfFileName = tf.getSubmittedFileName();
        Sys_dict sysDict = this.fetch(id);
        if (sysDict == null) {
            throw new ValidatException("找不到相应数据字典");
        }
        if (Strings.isBlank(tfFileName)) {
            throw new ValidatException("上传失败:找不到对应文件名");
        }
        String[] postFfix = tfFileName.split("\\.");
        if (postFfix.length == 0) {
            throw new ValidatException("上传失败:文件名不规范");
        }
        String absoluteFilePath = Globals.AppRoot + Globals.AppUploadPath + "/uploadDict/";
        String relativeFilePath = Globals.AppUploadPath + "/uploadDict/";
        //删除原来的图片
        if(!Strings.isBlank(sysDict.getPicture())){
            //一张
            String old = sysDict.getPicture();
            String[] oldRelaPath = old.split("\\/");
            if(oldRelaPath.length > 0){
                new File(absoluteFilePath+oldRelaPath[oldRelaPath.length-1]).delete();
            }
        }
        //生成图片存放文件位置
        File temp = new File(absoluteFilePath);
        //创建路径的目录
        if (!temp.exists()) {
            temp.mkdirs();
        }
        //1.将上传文件写入到指定文件中
        String fileName = R.UU32() + "." + postFfix[postFfix.length - 1];
        tf.write(temp.getPath() + "\\/" + fileName);
        tf.delete();
        //2.将上传相对路径写入数据字典的picture字段
        this.update(Chain.make("picture", relativeFilePath + fileName), Cnd.where("id", "=", id));
    }


 /*   public List<Map<String, Object>> getDictToolList(String parentCode, String airportId) {
        if (Strings.isBlank(parentCode)) {
            throw new ValidatException("传入参数不能为空");
        }
        List<Sys_dict> sysDicts = this.query(Cnd.where("code", "=", parentCode).and("airportId", "=", airportId));
        if (sysDicts.size() != 1) {
            throw new ValidatException("找不到对应数据字典");
        }
        List<Map<String, Object>> list = new ArrayList<>();
        Sys_dict sysDict = sysDicts.get(0);
        String parentId = sysDict.getId();
        if (!Strings.isBlank(parentId)) {
            List<Sys_dict> childList = this.query(Cnd.where("parentId", "=", parentId)); //暂不分页
            if (childList.size() > 0) {
                for (Sys_dict child : childList) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("parentId", child.getId());
                    map.put("parentName", child.getName());
                    map.put("parentPicture", child.getPicture());
                    //大类之下子类的数量
                    int kidCout = this.count(Cnd.where("parentId", "=", child.getId()));
                    map.put("kidCout", kidCout);
                    list.add(map);
                }
            }
        }
        return list;
    }*/

    public List<Map<String, Object>> getKidToolList(String airportId, String parentId) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (Strings.isBlank(airportId) || Strings.isBlank(parentId)) {
            throw new ValidatException("传入参数有误");
        }
        List<Sys_dict> sysDicts = this.query(Cnd.where("parentId", "=", parentId).and("airportId", "=", airportId));
        if (sysDicts.size() > 0) {
            for (Sys_dict sysDict : sysDicts) {
                Map<String, Object> map = new HashMap<>();
                map.put("kidId", sysDict.getId());
                map.put("kidName", sysDict.getName());
                map.put("kidPicture",sysDict.getPicture());

                list.add(map);
            }
        }
        return list;
    }

    public List<Map<String, Object>> getDictToolList(String airportId, String parentId) {
        if (Strings.isBlank(airportId)) {
            throw new ValidatException("传入机场ID不能为空");
        }
        if(Strings.isBlank(parentId)){
            Sys_dict sysDict = this.fetch(Cnd.where("code", "=", "toolType"));
            //未传值 ,父类未工具类型
            if(sysDict != null){
                parentId = sysDict.getId();
            }
        }
        List<Map<String, Object>> list = new ArrayList<>();
        List<Sys_dict> sysDicts = this.query(Cnd.where("parentId", "=", parentId).and("airportId", "=", airportId));
        if (sysDicts.size() > 0) {
            for (Sys_dict sysDict : sysDicts) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", sysDict.getId());
                map.put("name", sysDict.getName());
                map.put("picture",sysDict.getPicture());
                int kidCout = this.count(Cnd.where("parentId", "=", sysDict.getId()));
                map.put("kidCout", kidCout);
                list.add(map);
            }
        }
        return list;
    }
}
