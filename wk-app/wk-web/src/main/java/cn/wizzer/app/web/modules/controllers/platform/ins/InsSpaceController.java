package cn.wizzer.app.web.modules.controllers.platform.ins;

import cn.wizzer.app.ins.modules.models.Ins_instrument;
import cn.wizzer.app.ins.modules.services.InsBaseinsSpaceService;
import cn.wizzer.app.web.commons.filter.TokenFilter;
import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.ins.modules.models.Ins_space;
import cn.wizzer.app.ins.modules.services.InsSpaceService;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.json.JSONObject;
import org.nutz.dao.Cnd;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.filter.CrossOriginFilter;
import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.apidoc.annotation.ApiParam;
import org.nutz.plugins.apidoc.annotation.ReturnKey;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IocBean
@At("/platform/ins/space")
public class InsSpaceController {
    private static final Log log = Logs.get();

    @Inject
    private InsSpaceService insSpaceService;
    @Inject
    private InsBaseinsSpaceService insBaseinsSpaceService;
    @At("")
    @Ok("beetl:/platform/ins/space/index.html")
    @RequiresPermissions("platform.ins.space.select")
    public void index() {
    }

    /*d.spacename = $("#spacename").val()
                    d.spacenum = $("#spacenum").val()
                    d.pstatus = $("#pstatus").val();*/
    @At("/data")
    @Ok("json:full")
    @RequiresPermissions("platform.ins.space.select")
    public Object data(@Param("keyword") String keyword, @Param("spacenum") String spacenum, @Param("pstatus") String pstatus, @Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
        Cnd cnd = Cnd.NEW();
        if (!Strings.isBlank(spacenum)) {
            cnd.and("spacenum", "LIKE", "%" + spacenum +  "%");
        }
        if (!Strings.isBlank(keyword)) {
            keyword = keyword.replace("'","");
            cnd.and(cnd.exps("spacename","LIKE","%" + keyword +  "%").or("spacenum","LIKE","%" + keyword +  "%"));
        }
        if (!Strings.isBlank(pstatus)) {
            cnd.and("pstatus", "=", pstatus);
        }
        return insSpaceService.data(length, start, draw, order, columns, cnd, "insCabinet|insLock");
    }

    @At("/add")
    @Ok("beetl:/platform/ins/space/add.html")
    @RequiresPermissions("platform.ins.space")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.ins.space.add")
    @SLog(tag = "仓位表", msg = "${args[0].id}")
    public Object addDo(@Param("..") Ins_space insSpace, HttpServletRequest req) {
        try {
            insSpaceService.insert(insSpace);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/edit/?")
    @Ok("beetl:/platform/ins/space/edit.html")
    @RequiresPermissions("platform.ins.space")
    public void edit(String id, HttpServletRequest req) {
        Ins_space insSpace = insSpaceService.fetch(id);
        if (insSpace != null) {
            req.setAttribute("obj", insSpaceService.fetchLinks(insSpace, "insCabinet"));
        }
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.ins.space.edit")
    @SLog(tag = "仓位表", msg = "${args[0].id}")
    public Object editDo(@Param("..") Ins_space insSpace, HttpServletRequest req) {
        try {
            insSpace.setOpBy(StringUtil.getUid());
            insSpace.setOpAt((int) (System.currentTimeMillis() / 1000));
            insSpaceService.updateIgnoreNull(insSpace);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.ins.space.delete")
    @SLog(tag = "仓位表", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids") String[] ids, HttpServletRequest req) {
        try {
            if (ids != null && ids.length > 0) {
                List<Ins_space> spaces = insSpaceService.query(Cnd.where("id", "IN", ids));
                if(spaces.size() > 0){
                    List<String> infoList = new ArrayList<>();
                    for (Ins_space space : spaces) {
                        int spaceCount = insBaseinsSpaceService.count(Cnd.where("spaceId", "=", space.getId()));
                        if(spaceCount > 0){
                            infoList.add(space.getSpacename());
                        }
                    }
                    if(infoList.size() > 0){
                        StringBuffer sb = new StringBuffer(80);
                        sb.append("请先在基础工具列表解绑仓位！");
                        sb.append("基础工具名称:");
                        for (String info : infoList) {
                            sb.append(info).append(";");
                        }
                        return Result.error(sb.toString());
                    }
                    insSpaceService.delete(ids);
                    req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
                }

            } else {
                Ins_space space = insSpaceService.fetch(id);
                if(space != null){
                    int spaceCount = insBaseinsSpaceService.count(Cnd.where("spaceId", "=", space.getId()));
                    if(spaceCount > 0){
                        StringBuffer sb = new StringBuffer(80);
                        sb.append("请先在基础工具列表解绑仓位！");
                        sb.append("基础工具名称:");
                        sb.append(space.getSpacename()).append(";");
                        return Result.error(sb.toString());
                    }
                    insSpaceService.delete(id);
                    req.setAttribute("id", id);
                }

            }
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error",e);
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/ins/space/detail.html")
    @RequiresPermissions("platform.ins.space.select")
    public void detail(String id, HttpServletRequest req) {
        if (!Strings.isBlank(id)) {
            Ins_space insSpace = insSpaceService.fetch(id);
            if (insSpace != null) {
                req.setAttribute("obj", insSpaceService.fetchLinks(insSpace, "insCabinet"));
            }
        } else {
            req.setAttribute("obj", null);
        }
    }
    @At("/countByNum")
    @Ok("json:full")
    public Object countByNum(@Param("num")String num) {
        return insSpaceService.count("ins_space",Cnd.where("spacenum","=",num));
    }

    @At("/getLocks/?")
    @Ok("beetl:/platform/ins/lock/selectLock.html")
    public void getLocks(String id, HttpServletRequest req) {
        if (!Strings.isBlank(id)) {
            req.setAttribute("spaceId",id);
        } else {
            req.setAttribute("obj", null);
        }
    }
    @At("/updateLock")
    @Ok("json")
    public Object updateLock(@Param("spaceId") String spaceId, @Param("lockId") String lockId, HttpServletRequest req) {
        try {

            insSpaceService.updateLock(spaceId,lockId);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }

    }

    @At("/getSpaceList")
    @Ok("json:full")
    public Object getCabinetList(@Param("cabinetId")String cabinetId) {
        return insSpaceService.query(Cnd.where("cabinetid","=",cabinetId).orderBy("spacename","ASC"));
    }

    @At("/setSpaceLockStatus")
    @Ok("json:full")
    public Object setSpaceLockStatus(@Param("cabinetId") String cabinetId){
        try{
            insSpaceService.updateLockStatus(cabinetId,null,"0");
            return Result.success("system.success");
        }catch (Exception e){
            return Result.error("system.error");
        }
    }
}
