package cn.wizzer.app.web.modules.controllers.platform.ins;

import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.ins.modules.models.Ins_common;
import cn.wizzer.app.ins.modules.services.InsCommonService;
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
@At("/platform/ins/common")
public class InsCommonController{
    private static final Log log = Logs.get();
    @Inject
    private InsCommonService insCommonService;

    @At("")
    @Ok("beetl:/platform/ins/common/index.html")
    @RequiresPermissions("platform.ins.common")
    public void index() {
    }

    @At("/data")
    @Ok("json:full")
    @RequiresPermissions("platform.ins.common")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return insCommonService.data(length, start, draw, order, columns, cnd, null);
    }

    @At("/add")
    @Ok("beetl:/platform/ins/common/add.html")
    @RequiresPermissions("platform.ins.common")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.ins.common.add")
    @SLog(tag = "常用工具表", msg = "${args[0].id}")
    public Object addDo(@Param("..")Ins_common insCommon, HttpServletRequest req) {
		try {
			insCommonService.insert(insCommon);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/ins/common/edit.html")
    @RequiresPermissions("platform.ins.common")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", insCommonService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.ins.common.edit")
    @SLog(tag = "常用工具表", msg = "${args[0].id}")
    public Object editDo(@Param("..")Ins_common insCommon, HttpServletRequest req) {
		try {
            insCommon.setOpBy(StringUtil.getUid());
			insCommon.setOpAt((int) (System.currentTimeMillis() / 1000));
			insCommonService.updateIgnoreNull(insCommon);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.ins.common.delete")
    @SLog(tag = "常用工具表", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				insCommonService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				insCommonService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/ins/common/detail.html")
    @RequiresPermissions("platform.ins.common")
	public void detail(String id, HttpServletRequest req) {
        		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", insCommonService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }}

}
