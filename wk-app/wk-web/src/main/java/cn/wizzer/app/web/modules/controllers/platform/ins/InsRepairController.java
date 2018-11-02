package cn.wizzer.app.web.modules.controllers.platform.ins;

import cn.wizzer.app.base.modules.models.base_cnctobj;
import cn.wizzer.app.base.modules.services.BaseCnctobjService;
import cn.wizzer.app.ins.modules.models.Ins_baseins;
import cn.wizzer.app.ins.modules.models.Ins_instrument;
import cn.wizzer.app.ins.modules.services.InsBaseinsService;
import cn.wizzer.app.ins.modules.services.InsInstrumentService;
import cn.wizzer.app.ins.modules.services.InsRepairtrackService;
import cn.wizzer.app.sys.modules.models.Sys_user;
import cn.wizzer.app.web.modules.controllers.open.api.Datatime.newDataTime;
import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.ins.modules.models.Ins_repair;
import cn.wizzer.app.ins.modules.services.InsRepairService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.Cnd;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@IocBean
@At("/platform/ins/repair")
public class InsRepairController {
    private static final Log log = Logs.get();
    @Inject
    private InsRepairService insRepairService;
    @Inject
    private InsInstrumentService insInstrumentService;
    @Inject
    private InsBaseinsService insBaseinsService;
    @Inject
    private InsRepairtrackService insRepairtrackService;
    @Inject
    private BaseCnctobjService baseCnctobjService;
    @At("")
    @Ok("beetl:/platform/ins/repair/index.html")
    @RequiresPermissions("platform.ins.repair.select")
    public void index() {
    }

    @At("/data")
    @Ok("json:full")
    @RequiresPermissions("platform.ins.repair.select")
    public NutMap data(@Param("completeValue") String completeValue, @Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
        return insRepairService.getRepairList(completeValue, length, start, draw, order, columns, Cnd.NEW());
    }

    @At("/add")
    @Ok("beetl:/platform/ins/repair/add.html")
    @RequiresPermissions("platform.ins.repair")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.ins.repair.add")
    @SLog(tag = "维修申报表", msg = "${args[0].id}")
    public Object addDo(@Param("..") Ins_repair insRepair, HttpServletRequest req) {
        try {
            insRepairService.insert(insRepair);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/edit/?")
    @Ok("beetl:/platform/ins/repair/edit.html")
    @RequiresPermissions("platform.ins.repair")
    public void edit(String id, HttpServletRequest req) {
        if (!Strings.isBlank(id)) {
            Ins_repair repair = insRepairService.fetch(id);
            if (repair != null) {
                String insId = repair.getInsId();
                if (!Strings.isBlank(insId)) {
                    Ins_instrument instrument = insInstrumentService.fetch(insId);
                    if(instrument != null){
                        req.setAttribute("instrument",instrument);
                        String baseInsId = instrument.getBaseInsId();
                        if(!Strings.isBlank(baseInsId)){
                            Ins_baseins baseins = insBaseinsService.fetch(baseInsId);
                            if(baseins != null){
                                req.setAttribute("baseins",baseins);
                            }
                        }
                    }
                }
                req.setAttribute("obj", insRepairService.fetchLinks(repair, "order|orderentry|instrument|basePerson"));
            }
        } else {
            req.setAttribute("obj", null);
        }
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.ins.repair.edit")
    @SLog(tag = "维修申报表", msg = "${args[0].id}")
    public Object editDo(@Param("..") Ins_repair insRepair, HttpServletRequest req) {
        try {
            Subject subject = SecurityUtils.getSubject();

            if(subject!= null){
                Sys_user user = (Sys_user) subject.getPrincipal();
                base_cnctobj cnctobj = baseCnctobjService.fetch(Cnd.where("userId", "=", user.getId()));
                if(cnctobj != null){
                    String personId = cnctobj.getPersonId();
                    //插入一条维修历史对象
                    insRepairtrackService.insertTrackRecord(insRepair.getId(),personId, newDataTime.getDateYMDHMS(),1);
                }
            }
            insRepair.setOpBy(StringUtil.getUid());
            insRepair.setOpAt((int) (System.currentTimeMillis() / 1000));
            insRepairService.updateIgnoreNull(insRepair);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.ins.repair.delete")
    @SLog(tag = "维修申报表", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids") String[] ids, HttpServletRequest req) {
        try {
            if (ids != null && ids.length > 0) {
                insRepairService.delete(ids);
                req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
            } else {
                insRepairService.delete(id);
                req.setAttribute("id", id);
            }
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/completeRepair")
    @Ok("json")
    @RequiresPermissions("platform.ins.repair")
//    @SLog(tag = "完成维修", msg = "${req.getAttribute('id')}")
    public Object completeRepair(@Param("ids") String[] ids, HttpServletRequest req){
        try {
            if(ids != null && ids.length > 0){
                Subject subject = SecurityUtils.getSubject();

                if(subject!= null){
                    Sys_user user = (Sys_user) subject.getPrincipal();
                    base_cnctobj cnctobj = baseCnctobjService.fetch(Cnd.where("userId", "=", user.getId()));
                    if(cnctobj != null){
                        String personId = cnctobj.getPersonId();
                        insRepairService.completeRepair(ids,personId);
                    }
                }
            }
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/ins/repair/detail.html")
    @RequiresPermissions("platform.ins.repair.select")
    public void detail(String id, HttpServletRequest req) {
        if (!Strings.isBlank(id)) {
            Ins_repair repair = insRepairService.fetch(id);
            if (repair != null) {
                String insId = repair.getInsId();
                if (!Strings.isBlank(insId)) {
                    Ins_instrument instrument = insInstrumentService.fetch(insId);
                    if(instrument != null){
                        req.setAttribute("instrument",instrument);
                        String baseInsId = instrument.getBaseInsId();
                        if(!Strings.isBlank(baseInsId)){
                            Ins_baseins baseins = insBaseinsService.fetch(baseInsId);
                            if(baseins != null){
                                req.setAttribute("baseins",baseins);
                            }
                        }
                    }
                }
                req.setAttribute("obj", insRepairService.fetchLinks(repair, "order|orderentry|instrument|basePerson"));
            }
        } else {
            req.setAttribute("obj", null);
        }
    }

}
