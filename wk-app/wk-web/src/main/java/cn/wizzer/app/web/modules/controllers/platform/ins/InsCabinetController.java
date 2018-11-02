package cn.wizzer.app.web.modules.controllers.platform.ins;

import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.ins.modules.models.Ins_cabinet;
import cn.wizzer.app.ins.modules.services.InsCabinetService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@IocBean
@At("/platform/ins/cabinet")
public class InsCabinetController {
    private static final Log log = Logs.get();
    @Inject
    private InsCabinetService insCabinetService;

    @At("")
    @Ok("beetl:/platform/ins/cabinet/index.html")
    @RequiresPermissions("platform.ins.cabinet.select")
    public void index() {
    }
    @At("/selectCabinet")
    @Ok("beetl:/platform/ins/cabinet/selectCabinet.html")
    public void selectCabinet() {

    }

    @At("/data")
    @Ok("json:full")
    @RequiresPermissions("platform.ins.cabinet.select")
    public Object data(@Param("cabinetnum") String cabinetnum, @Param("cabinetname") String cabinetname, @Param("pstatus") String pstatus,@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
        Cnd cnd = Cnd.NEW();
        if (!Strings.isBlank(cabinetnum)) {
            cnd.and("cabinetnum", "LIKE", "%" + cabinetnum + "%");
        }
        if (!Strings.isBlank(cabinetname)) {
            cnd.and("spacename", "LIKE", "%" + cabinetname +  "%");
        }
        if (!Strings.isBlank(pstatus)) {
            cnd.and("pstatus", "=", pstatus);
        }
        return insCabinetService.data(length, start, draw, order, columns, cnd, "baseAirport|sysType");
    }

    @At("/add")
    @Ok("beetl:/platform/ins/cabinet/add.html")
    @RequiresPermissions("platform.ins.cabinet")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.ins.cabinet.add")
    @SLog(tag = "智能柜表", msg = "${args[0].id}")
    public Object addDo(@Param("..") Ins_cabinet insCabinet, HttpServletRequest req) {
        try {
            insCabinetService.insert(insCabinet);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/edit/?")
    @Ok("beetl:/platform/ins/cabinet/edit.html")
    @RequiresPermissions("platform.ins.cabinet")
    public void edit(String id, HttpServletRequest req) {
        Ins_cabinet insCabinet = insCabinetService.fetch(id);
        if(insCabinet != null){
            insCabinet = insCabinetService.fetchLinks(insCabinet, "baseAirport|sysType");
            req.setAttribute("obj", insCabinet);
        }
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.ins.cabinet.edit")
    @SLog(tag = "智能柜表", msg = "${args[0].id}")
    public Object editDo(@Param("..") Ins_cabinet insCabinet, HttpServletRequest req) {
        try {
            insCabinet.setOpBy(StringUtil.getUid());
            insCabinet.setOpAt((int) (System.currentTimeMillis() / 1000));
            insCabinetService.updateIgnoreNull(insCabinet);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.ins.cabinet.delete")
    @SLog(tag = "智能柜表", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids") String[] ids, HttpServletRequest req) {
        try {
            if (ids != null && ids.length > 0) {
                insCabinetService.delete(ids);
                req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
            } else {
                insCabinetService.delete(id);
                req.setAttribute("id", id);
            }
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/ins/cabinet/detail.html")
    @RequiresPermissions("platform.ins.cabinet.select")
    public void detail(String id, HttpServletRequest req) {
        if (!Strings.isBlank(id)) {
            Ins_cabinet insCabinet = insCabinetService.fetch(id);
            if(insCabinet!= null){
                insCabinet = insCabinetService.fetchLinks(insCabinet,"baseAirport|sysType");
                req.setAttribute("obj",insCabinet);
            }
        } else {
            req.setAttribute("obj", null);
        }
    }
    @At("/countByNum")
    @Ok("json:full")
    public Object countByNum(@Param("num")String num) {
        return insCabinetService.count("ins_cabinet",Cnd.where("cabinetnum","=",num));
    }
    @At("/getCabinetList")
    @Ok("json:full")
    public Object getCabinetList() {
        return insCabinetService.query(Cnd.where("1","=","1").orderBy("cabinetname","ASC"));
    }


}
