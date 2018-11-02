package cn.wizzer.app.web.modules.controllers.platform.ins;

import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.ins.modules.models.Ins_rfid;
import cn.wizzer.app.ins.modules.services.InsRfidService;
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
@At("/platform/ins/rfid")
public class InsRfidController {
    private static final Log log = Logs.get();
    @Inject
    private InsRfidService insRfidService;

    @At("")
    @Ok("beetl:/platform/ins/rfid/index.html")
    @RequiresPermissions("platform.ins.rfid.select")
    public void index() {
    }

    @At("/data")
    @Ok("json:full")
    @RequiresPermissions("platform.ins.rfid.select")
    public Object data(@Param("number")String number,@Param("pstatus")String pstatus,@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
        Cnd cnd = Cnd.NEW();
        if(!Strings.isBlank(number)){
            cnd.and("number","LIKE","%" + number + "%");
        }
        if(!Strings.isBlank(pstatus)){
            cnd.and("pstatus","=",pstatus);
        }
        return insRfidService.data(length, start, draw, order, columns, cnd, "baseAirport");
    }

    @At("/add")
    @Ok("beetl:/platform/ins/rfid/add.html")
    @RequiresPermissions("platform.ins.rfid")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.ins.rfid.add")
    @SLog(tag = "射频标签表", msg = "${args[0].id}")
    public Object addDo(@Param("..") Ins_rfid insRfid, HttpServletRequest req) {
        try {
            insRfidService.insert(insRfid);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/edit/?")
    @Ok("beetl:/platform/ins/rfid/edit.html")
    @RequiresPermissions("platform.ins.rfid")
    public void edit(String id, HttpServletRequest req) {
        Ins_rfid insRfid = insRfidService.fetch(id);
        if (insRfid != null) {
            req.setAttribute("obj", insRfidService.fetchLinks(insRfid, "baseAirport"));
        }
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.ins.rfid.edit")
    @SLog(tag = "射频标签表", msg = "${args[0].id}")
    public Object editDo(@Param("..") Ins_rfid insRfid, HttpServletRequest req) {
        try {
            insRfid.setOpBy(StringUtil.getUid());
            insRfid.setOpAt((int) (System.currentTimeMillis() / 1000));
            insRfidService.updateIgnoreNull(insRfid);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.ins.rfid.delete")
    @SLog(tag = "射频标签表", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids") String[] ids, HttpServletRequest req) {
        try {
            if (ids != null && ids.length > 0) {
                insRfidService.delete(ids);
                req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
            } else {
                insRfidService.delete(id);
                req.setAttribute("id", id);
            }
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error",e);
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/ins/rfid/detail.html")
    @RequiresPermissions("platform.ins.rfid.select")
    public void detail(String id, HttpServletRequest req) {
        if (!Strings.isBlank(id)) {
            Ins_rfid insRfid = insRfidService.fetch(id);
            if (insRfid != null) {
                req.setAttribute("obj", insRfidService.fetchLinks(insRfid, "baseAirport"));
            }
        } else {
            req.setAttribute("obj", null);
        }
    }

    @At("/getRFID/?")
    @Ok("beetl:/platform/ins/rfid/selectRFID.html")
    @RequiresPermissions("platform.ins.rfid.select")
    public void getRFID(String id, HttpServletRequest req) {
        if (!Strings.isBlank(id)) {
            Ins_rfid insRfid = insRfidService.fetch(id);
            if (insRfid != null) {
                req.setAttribute("obj", insRfidService.fetchLinks(insRfid, "baseAirport"));
            }
        } else {
            req.setAttribute("obj", null);
        }
    }
    @At("/getNumberCount")
    @Ok("json")
    public Object getNumberCount(String number){
        return insRfidService.count(Cnd.where("number","=",number));
    }
}
