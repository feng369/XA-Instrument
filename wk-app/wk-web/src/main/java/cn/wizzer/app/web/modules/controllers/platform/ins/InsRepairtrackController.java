package cn.wizzer.app.web.modules.controllers.platform.ins;

import cn.wizzer.app.ins.modules.models.Ins_repair;
import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.ins.modules.models.Ins_repairtrack;
import cn.wizzer.app.ins.modules.services.InsRepairtrackService;
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
import java.util.Map;

@IocBean
@At("/platform/ins/repairtrack")
public class InsRepairtrackController{
    private static final Log log = Logs.get();
    @Inject
    private InsRepairtrackService insRepairtrackService;

    @At("")
    @Ok("beetl:/platform/ins/repairtrack/index.html")
    @RequiresPermissions("platform.ins.repairtrack.select")
    public void index() {
    }

    @At("/data")
    @Ok("json:full")
    @RequiresPermissions("platform.ins.repairtrack.select")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return insRepairtrackService.data(length, start, draw, order, columns, cnd, null);
    }

    @At("/add")
    @Ok("beetl:/platform/ins/repairtrack/add.html")
    @RequiresPermissions("platform.ins.repairtrack")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.ins.repairtrack.add")
    @SLog(tag = "维修历史对象", msg = "${args[0].id}")
    public Object addDo(@Param("..")Ins_repairtrack insRepairtrack, HttpServletRequest req) {
		try {
			insRepairtrackService.insert(insRepairtrack);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/ins/repairtrack/edit.html")
    @RequiresPermissions("platform.ins.repairtrack")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", insRepairtrackService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.ins.repairtrack.edit")
    @SLog(tag = "维修历史对象", msg = "${args[0].id}")
    public Object editDo(@Param("..")Ins_repairtrack insRepairtrack, HttpServletRequest req) {
		try {
            insRepairtrack.setOpBy(StringUtil.getUid());
			insRepairtrack.setOpAt((int) (System.currentTimeMillis() / 1000));
			insRepairtrackService.updateIgnoreNull(insRepairtrack);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.ins.repairtrack.delete")
    @SLog(tag = "维修历史对象", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				insRepairtrackService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				insRepairtrackService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/ins/repairtrack/detail.html")
    @RequiresPermissions("platform.ins.repairtrack.select")
	public void detail(String id, HttpServletRequest req) {
        		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", insRepairtrackService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }}


    @At("/trackRecord/?")
    @Ok("beetl:/platform/ins/repairtrack/detail.html")
    @RequiresPermissions("platform.ins.repairtrack")
    public void trackRecord(String id, HttpServletRequest req){
        if(!Strings.isBlank(id)){
            req.setAttribute("id",id);
        }else{
            req.setAttribute("id",null);
        }
    }

    @At("/getRepairTracks")
    @Ok("json:full")
    @RequiresPermissions("platform.ins.repairtrack")
    public List<Map<String,Object>> getRepairTracks(@Param("repairId")String repairId){
        return insRepairtrackService.getRepairTracks(repairId);
    }
}
