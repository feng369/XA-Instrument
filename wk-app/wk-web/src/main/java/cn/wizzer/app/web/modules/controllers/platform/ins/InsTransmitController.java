package cn.wizzer.app.web.modules.controllers.platform.ins;

import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.ins.modules.models.Ins_transmit;
import cn.wizzer.app.ins.modules.services.InsTransmitService;
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
@At("/platform/ins/transmit")
public class InsTransmitController {
    private static final Log log = Logs.get();
    @Inject
    private InsTransmitService insTransmitService;

    @At("")
    @Ok("beetl:/platform/ins/transmit/index.html")
    @RequiresPermissions("platform.ins.transmit")
    public void index() {
    }

    @At("/data")
    @Ok("json:full")
    @RequiresPermissions("platform.ins.transmit")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
        Cnd cnd = Cnd.NEW();
        return insTransmitService.data(length, start, draw, order, columns, cnd, null);
    }

    @At("/add")
    @Ok("beetl:/platform/ins/transmit/add.html")
    @RequiresPermissions("platform.ins.transmit")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.ins.transmit.add")
    @SLog(tag = "移交表", msg = "${args[0].id}")
    public Object addDo(@Param("..") Ins_transmit insTransmit, HttpServletRequest req) {
        try {
            insTransmitService.insert(insTransmit);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/edit/?")
    @Ok("beetl:/platform/ins/transmit/edit.html")
    @RequiresPermissions("platform.ins.transmit")
    public void edit(String id, HttpServletRequest req) {
        req.setAttribute("obj", insTransmitService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.ins.transmit.edit")
    @SLog(tag = "移交表", msg = "${args[0].id}")
    public Object editDo(@Param("..") Ins_transmit insTransmit, HttpServletRequest req) {
        try {
            insTransmit.setOpBy(StringUtil.getUid());
            insTransmit.setOpAt((int) (System.currentTimeMillis() / 1000));
            insTransmitService.updateIgnoreNull(insTransmit);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.ins.transmit.delete")
    @SLog(tag = "移交表", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids") String[] ids, HttpServletRequest req) {
        try {
            if (ids != null && ids.length > 0) {
                insTransmitService.delete(ids);
                req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
            } else {
                insTransmitService.delete(id);
                req.setAttribute("id", id);
            }
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/ins/transmit/detail.html")
    @RequiresPermissions("platform.ins.transmit")
    public void detail(String id, HttpServletRequest req) {
        if (!Strings.isBlank(id)) {
            req.setAttribute("obj", insTransmitService.fetch(id));
        } else {
            req.setAttribute("obj", null);
        }
    }

}
