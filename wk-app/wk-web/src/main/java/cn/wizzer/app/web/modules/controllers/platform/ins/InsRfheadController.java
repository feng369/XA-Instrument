package cn.wizzer.app.web.modules.controllers.platform.ins;

import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.ins.modules.models.Ins_rfhead;
import cn.wizzer.app.ins.modules.services.InsRfheadService;
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
@At("/platform/ins/rfhead")
public class InsRfheadController{
    private static final Log log = Logs.get();
    @Inject
    private InsRfheadService insRfheadService;

    @At("")
    @Ok("beetl:/platform/ins/rfhead/index.html")
    @RequiresPermissions("platform.ins.rfhead")
    public void index() {
    }

    @At("/data")
    @Ok("json:full")
    @RequiresPermissions("platform.ins.rfhead")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return insRfheadService.data(length, start, draw, order, columns, cnd, null);
    }

    @At("/add")
    @Ok("beetl:/platform/ins/rfhead/add.html")
    @RequiresPermissions("platform.ins.rfhead")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.ins.rfhead.add")
    @SLog(tag = "射频读头表", msg = "${args[0].id}")
    public Object addDo(@Param("..")Ins_rfhead insRfhead, HttpServletRequest req) {
		try {
			insRfheadService.insert(insRfhead);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/ins/rfhead/edit.html")
    @RequiresPermissions("platform.ins.rfhead")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", insRfheadService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.ins.rfhead.edit")
    @SLog(tag = "射频读头表", msg = "${args[0].id}")
    public Object editDo(@Param("..")Ins_rfhead insRfhead, HttpServletRequest req) {
		try {
            insRfhead.setOpBy(StringUtil.getUid());
			insRfhead.setOpAt((int) (System.currentTimeMillis() / 1000));
			insRfheadService.updateIgnoreNull(insRfhead);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.ins.rfhead.delete")
    @SLog(tag = "射频读头表", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				insRfheadService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				insRfheadService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/ins/rfhead/detail.html")
    @RequiresPermissions("platform.ins.rfhead")
	public void detail(String id, HttpServletRequest req) {
        		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", insRfheadService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }}

}
