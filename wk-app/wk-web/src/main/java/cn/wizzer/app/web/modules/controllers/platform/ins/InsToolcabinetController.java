package cn.wizzer.app.web.modules.controllers.platform.ins;

import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.ins.modules.models.Ins_toolcabinet;
import cn.wizzer.app.ins.modules.services.InsToolcabinetService;
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
@At("/platform/ins/toolcabinet")
public class InsToolcabinetController{
    private static final Log log = Logs.get();
    @Inject
    private InsToolcabinetService insToolcabinetService;

    @At("")
    @Ok("beetl:/platform/ins/toolcabinet/index.html")
    @RequiresPermissions("platform.ins.toolcabinet")
    public void index() {
    }

    @At("/data")
    @Ok("json:full")
    @RequiresPermissions("platform.ins.toolcabinet")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return insToolcabinetService.data(length, start, draw, order, columns, cnd, null);
    }

    @At("/add")
    @Ok("beetl:/platform/ins/toolcabinet/add.html")
    @RequiresPermissions("platform.ins.toolcabinet")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.ins.toolcabinet.add")
    @SLog(tag = "购物车", msg = "${args[0].id}")
    public Object addDo(@Param("..")Ins_toolcabinet insToolcabinet, HttpServletRequest req) {
		try {
			insToolcabinetService.insert(insToolcabinet);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/ins/toolcabinet/edit.html")
    @RequiresPermissions("platform.ins.toolcabinet")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", insToolcabinetService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.ins.toolcabinet.edit")
    @SLog(tag = "购物车", msg = "${args[0].id}")
    public Object editDo(@Param("..")Ins_toolcabinet insToolcabinet, HttpServletRequest req) {
		try {
            insToolcabinet.setOpBy(StringUtil.getUid());
			insToolcabinet.setOpAt((int) (System.currentTimeMillis() / 1000));
			insToolcabinetService.updateIgnoreNull(insToolcabinet);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.ins.toolcabinet.delete")
    @SLog(tag = "购物车", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				insToolcabinetService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				insToolcabinetService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/ins/toolcabinet/detail.html")
    @RequiresPermissions("platform.ins.toolcabinet")
	public void detail(String id, HttpServletRequest req) {
        		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", insToolcabinetService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }}

}
