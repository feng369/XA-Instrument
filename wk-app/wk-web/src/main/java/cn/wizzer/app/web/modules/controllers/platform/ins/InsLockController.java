package cn.wizzer.app.web.modules.controllers.platform.ins;

import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.ins.modules.models.Ins_lock;
import cn.wizzer.app.ins.modules.services.InsLockService;
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
@At("/platform/ins/lock")
public class InsLockController{
    private static final Log log = Logs.get();
    @Inject
    private InsLockService insLockService;

    @At("")
    @Ok("beetl:/platform/ins/lock/index.html")
    @RequiresPermissions("platform.ins.lock.select")
    public void index() {
    }

    @At("/data")
    @Ok("json:full")
    @RequiresPermissions("platform.ins.lock.select")
    public Object data(@Param("locknum")String locknum,@Param("bindStatus")String bindStatus,@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	if(!Strings.isBlank(locknum)){
    	    cnd.and("locknum","LIKE","%"+locknum+"%");
        }
        if(!Strings.isBlank(bindStatus)){
    	    cnd.and("bindStatus","=",bindStatus);
        }
		return insLockService.data(length, start, draw, order, columns, cnd, "baseAirport");
    }

    @At("/add")
    @Ok("beetl:/platform/ins/lock/add.html")
    @RequiresPermissions("platform.ins.lock")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.ins.lock.add")
    @SLog(tag = "锁表", msg = "${args[0].id}")
    public Object addDo(@Param("..")Ins_lock insLock, HttpServletRequest req) {
		try {
			insLockService.insert(insLock);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/ins/lock/edit.html")
    @RequiresPermissions("platform.ins.lock")
    public void edit(String id,HttpServletRequest req) {
        Ins_lock lock = insLockService.fetch(id);
        if(lock != null){
            req.setAttribute("obj",insLockService.fetchLinks(lock,"baseAirport"));
        }
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.ins.lock.edit")
    @SLog(tag = "锁表", msg = "${args[0].id}")
    public Object editDo(@Param("..")Ins_lock insLock, HttpServletRequest req) {
		try {
            insLock.setOpBy(StringUtil.getUid());
			insLock.setOpAt((int) (System.currentTimeMillis() / 1000));
			insLockService.updateIgnoreNull(insLock);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.ins.lock.delete")
    @SLog(tag = "锁表", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				insLockService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				insLockService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/ins/lock/detail.html")
    @RequiresPermissions("platform.ins.lock.select")
	public void detail(String id, HttpServletRequest req) {
        		if (!Strings.isBlank(id)) {
                    Ins_lock lock = insLockService.fetch(id);
                    if(lock != null){
                        req.setAttribute("obj",insLockService.fetchLinks(lock,"baseAirport"));
                    }
		}else{
            req.setAttribute("obj", null);
        }}

}
