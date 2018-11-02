package cn.wizzer.app.web.modules.controllers.platform.ins;

import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.ins.modules.models.Ins_orderentry;
import cn.wizzer.app.ins.modules.services.InsOrderentryService;
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
@At("/platform/ins/orderentry")
public class InsOrderentryController{
    private static final Log log = Logs.get();
    @Inject
    private InsOrderentryService insOrderentryService;

    @At("")
    @Ok("beetl:/platform/ins/orderentry/index.html")
    @RequiresPermissions("platform.ins.orderentry")
    public void index() {
    }

    @At("/data")
    @Ok("json:full")
    @RequiresPermissions("platform.ins.orderentry")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return insOrderentryService.data(length, start, draw, order, columns, cnd, null);
    }

    @At("/add")
    @Ok("beetl:/platform/ins/orderentry/add.html")
    @RequiresPermissions("platform.ins.orderentry")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.ins.orderentry.add")
    @SLog(tag = "订单工具表", msg = "${args[0].id}")
    public Object addDo(@Param("..")Ins_orderentry insOrderentry, HttpServletRequest req) {
		try {
			insOrderentryService.insert(insOrderentry);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/ins/orderentry/edit.html")
    @RequiresPermissions("platform.ins.orderentry")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", insOrderentryService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.ins.orderentry.edit")
    @SLog(tag = "订单工具表", msg = "${args[0].id}")
    public Object editDo(@Param("..")Ins_orderentry insOrderentry, HttpServletRequest req) {
		try {
            insOrderentry.setOpBy(StringUtil.getUid());
			insOrderentry.setOpAt((int) (System.currentTimeMillis() / 1000));
			insOrderentryService.updateIgnoreNull(insOrderentry);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.ins.orderentry.delete")
    @SLog(tag = "订单工具表", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				insOrderentryService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				insOrderentryService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/ins/orderentry/detail.html")
    @RequiresPermissions("platform.ins.orderentry")
	public void detail(String id, HttpServletRequest req) {
        		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", insOrderentryService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }}

}
