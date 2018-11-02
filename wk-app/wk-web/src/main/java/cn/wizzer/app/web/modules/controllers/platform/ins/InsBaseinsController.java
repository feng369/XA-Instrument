package cn.wizzer.app.web.modules.controllers.platform.ins;

import cn.wizzer.app.ins.modules.models.*;
import cn.wizzer.app.ins.modules.services.InsBaseinsSpaceService;
import cn.wizzer.app.ins.modules.services.InsCabinetService;
import cn.wizzer.app.ins.modules.services.InsSpaceService;
import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.app.sys.modules.services.SysDictService;
import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.base.ValidatException;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.ins.modules.services.InsBaseinsService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IocBean
@At("/platform/ins/baseins")
public class InsBaseinsController {
    private static final Log log = Logs.get();
    @Inject
    private InsBaseinsService insBaseinsService;
    @Inject
    private InsBaseinsSpaceService insBaseinsSpaceService;
    @Inject
    private InsSpaceService insSpaceService;
    @Inject
    private InsCabinetService insCabinetService;
    @Inject
    private SysDictService sysDictService;

    @At("")
    @Ok("beetl:/platform/ins/baseins/index.html")
    @RequiresPermissions("platform.ins.baseins")
    public void index() {
    }

    @At("/data")
    @Ok("json:full")
    @RequiresPermissions("platform.ins.baseins")
    public NutMap data(@Param("cabinetId")String cabinetId,@Param("spaceId")String spaceId,@Param("insnum") String insnum, @Param("insname") String insname, @Param("keyword") String keyword, @Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
        Cnd cnd = Cnd.NEW();
        /*if(!Strings.isBlank(keyword)){
            keyword = keyword.replace("'","");
            cnd.and(cnd.exps("insname","LIKE","%"+keyword+"%").or("insnum","LIKE","%"+keyword+"%"));
        }*/
        return insBaseinsService.getList(length, start, draw, order, columns, cnd, keyword, insnum, insname,cabinetId,spaceId);
//        return insBaseinsService.data(length, start, draw, order, columns, cnd, "sys_insunit|dict_instype|insSpace");
    }

    @At("/add")
    @Ok("beetl:/platform/ins/baseins/add.html")
    @RequiresPermissions("platform.ins.baseins")
    public void add() {

    }

    @At("/selectBaseIns")
    @Ok("beetl:/platform/ins/baseins/selectBaseIns.html")
    @RequiresPermissions("platform.ins.baseins")
    public void selectBaseIns() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.ins.baseins.add")
    @SLog(tag = "基础工具表", msg = "${args[0].id}")
    public Object addDo(@Param("..") Ins_baseins insBaseins, HttpServletRequest req) {
        try {
            String instype = insBaseins.getInstype();
            if (Strings.isNotBlank(instype)) {
                Sys_dict sysDict = sysDictService.fetch(instype);
                if (sysDict != null && !Strings.isBlank(sysDict.getParentId())) {
                    Sys_dict parentDict = sysDictService.fetch(sysDict.getParentId());
                    if (parentDict != null) {
                        if ("toolkit".equals(parentDict.getCode())) {
                            //工具包
                            Sql sql = Sqls.queryRecord("SELECT MAX(insnum) insnum FROM ins_baseins WHERE insnum LIKE 'GJX%'");
                            insBaseinsService.dao().execute(sql);
                            List<Record> recordList = sql.getList(Record.class);
                            if(recordList.size() > 0){
                                String insnum = recordList.get(0).getString("insnum");
                                insnum = insnum.substring(insnum.length() - 4, insnum.length());
                                insBaseins.setInsnum("GJX-" + String.format("%04d", Integer.valueOf(insnum) + 1));
                            }else{
                                insBaseins.setInsnum("GJX-" + String.format("%04d",1));
                            }
                        } else {
                            //散装工具
                            Sql sql = Sqls.queryRecord("SELECT MAX(insnum) insnum FROM ins_baseins WHERE insnum LIKE 'GJS%'");
                            insBaseinsService.dao().execute(sql);
                            List<Record> recordList = sql.getList(Record.class);
                            if(recordList.size() > 0){
                                String insnum = recordList.get(0).getString("insnum");
                                insnum = insnum.substring(insnum.length() - 4, insnum.length());

                                insBaseins.setInsnum("GJS-" + String.format("%04d", Integer.valueOf(insnum) + 1));
                            }else{
                                insBaseins.setInsnum("GJS-" + String.format("%04d",  1));
                            }
                        }
                    }
                }
            }
            insBaseinsService.insert(insBaseins);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error",e);
        }
    }
/*public static void main(String[] args){
    String str = "GJX-0003";
        str = str.substring(str.length() - 4, str.length());
    System.out.println(str);
    }*/

    @At("/edit/?")
    @Ok("beetl:/platform/ins/baseins/edit.html")
    @RequiresPermissions("platform.ins.baseins")
    public void edit(String id, HttpServletRequest req){
        Ins_baseins insBaseins = insBaseinsService.fetch(id);
        if (insBaseins != null) {
            req.setAttribute("obj", insBaseinsService.fetchLinks(insBaseins, "sys_insunit|dict_instype"));
        }
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.ins.baseins.edit")
    @SLog(tag = "基础工具表", msg = "${args[0].id}")
    public Object editDo(@Param("..")Ins_baseins insBaseins, HttpServletRequest req) {
        try {
            insBaseins.setOpBy(StringUtil.getUid());
            insBaseins.setOpAt((int) (System.currentTimeMillis() / 1000));
            insBaseinsService.updateIgnoreNull(insBaseins);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.ins.baseins.delete")
    @SLog(tag = "基础工具表", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids") String[] ids, HttpServletRequest req) {
        try {
            if (ids != null && ids.length > 0) {
                List<Ins_baseins> baseins = insBaseinsService.query(Cnd.where("id", "IN", ids));
                if (baseins.size() > 0) {
                    List<String> infoList = new ArrayList<>();
                    for (Ins_baseins insBaseins : baseins) {
                        int count = insBaseinsSpaceService.count(Cnd.where("baseInsId", "=", insBaseins.getId()));
                        if(count > 0){
                            infoList.add(insBaseins.getInsnum());
                        }
                    }
                    if (infoList.size() > 0) {
                        StringBuffer sb = new StringBuffer(80);
                        sb.append("请先解绑仓位!");
                        sb.append("基础工具编号:");
                        for (String insnum : infoList) {
                            sb.append(insnum).append(";");
                        }
                        return Result.error(sb.toString());
                    }
                    insBaseinsService.delete(ids);
                    req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
                }
            } else {
                Ins_baseins baseins = insBaseinsService.fetch(id);
                if (baseins != null) {
                    int count = insBaseinsSpaceService.count(Cnd.where("baseInsId", "=", baseins.getId()));
                    if(count > 0){
                        StringBuffer sb = new StringBuffer(80);
                        sb.append("请先解绑仓位!");
                        sb.append("工具编号:");
                        sb.append(baseins.getInsnum());
                        return Result.error(sb.toString());
                    }
                    insBaseinsService.delete(id);
                    req.setAttribute("id", id);
                }
            }
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/ins/baseins/detail.html")
    @RequiresPermissions("platform.ins.baseins")
    public void detail(String id, HttpServletRequest req) {
        if (!Strings.isBlank(id)) {
            Ins_baseins insBaseins = insBaseinsService.fetch(id);
            if (insBaseins != null) {
                req.setAttribute("obj", insBaseinsService.fetchLinks(insBaseins, "sys_insunit|dict_instype"));
            }
            List<Ins_baseins_space> insBaseinsSpaces = insBaseinsSpaceService.query(Cnd.where("baseInsId", "=", id));
            Map<String, List<String>> spacesStrMap = new HashMap<>();
            if (insBaseinsSpaces.size() > 0) {
                for (Ins_baseins_space insBaseinsSpace : insBaseinsSpaces) {
                    if (!Strings.isBlank(insBaseinsSpace.getSpaceId())) {
                        String spaceId = insBaseinsSpace.getSpaceId();
                        Ins_space insSpace = insSpaceService.fetch(spaceId);
                        if (insSpace != null) {
                            Ins_cabinet insCabinet = insCabinetService.fetch(insSpace.getCabinetid());
                            if (insCabinet != null) {
                                if (spacesStrMap.get(insCabinet.getCabinetname()) == null) {
                                    List<String> spaceNames = new ArrayList<>();
                                    spaceNames.add(insSpace.getSpacename());
                                    spacesStrMap.put(insCabinet.getCabinetname(), spaceNames);
                                } else {
                                    List<String> spaceNames = spacesStrMap.get(insCabinet.getCabinetname());
                                    spaceNames.add(insSpace.getSpacename());
                                    spacesStrMap.put(insCabinet.getCabinetname(), spaceNames);
                                }
                            }
                        }
                    }
                }
            }

            if (spacesStrMap.size() > 0) {
                StringBuilder sb = new StringBuilder(80);
                for (Map.Entry<String, List<String>> entry : spacesStrMap.entrySet()) {
                    String cabinetName = entry.getKey();
                    List<String> spaceNames = entry.getValue();
                    sb.append(cabinetName).append(":");
                    for (String spaceName : spaceNames) {
                        sb.append(spaceName).append(" ");
                    }
                }
                req.setAttribute("spaceNames", sb.toString());
            }
        } else {
            req.setAttribute("obj", null);
        }
    }
    //上传图片

    @At("/uploadBaseIns")
    @AdaptBy(type = UploadAdaptor.class, args = {"ioc:imageUpload"})
    @Ok("json")
    public Object uploadBaseIns(@Param("uploads") TempFile tf, @Param("baseInsId") String baseInsId) {
        Map<String, Object> map = new HashMap<>();
        if (tf == null || "".equals(tf.getSubmittedFileName())) {
            map.put("result", "上传失败");
            return map;
        }
        try {
            return insBaseinsService.uploadBaseIns(tf, baseInsId);
        } catch (Exception e) {
            log.debug(e.getMessage());
            map.put("result", "上传失败");
        }
        return map;
    }

    @At("/bindSpace")
    @Ok("json")
    @RequiresPermissions("platform.ins.baseins.bindSpace")
    public Object bindSpace(@Param("baseInsIds") String[] baseInsIds, @Param("spaceIds") String[] spaceIds, HttpServletRequest req) {
        try {
            insBaseinsService.bindSpace(baseInsIds, spaceIds);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }

    }

    @At("/unbindSpaces")
    @Ok("json")
    @RequiresPermissions("platform.ins.baseins.bindSpace")
    public Object unbindSpaces(@Param("baseInsIds") String[] baseInsIds, HttpServletRequest req) {
        try {
            if(baseInsIds.length <= 0){
                throw new ValidatException("请选择需要解绑的仓位");
            }
            insBaseinsService.unbindSpaces(baseInsIds);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error",e);
        }

    }
}
