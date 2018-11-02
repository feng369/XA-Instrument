package cn.wizzer.app.web.modules.controllers.platform.ins;

import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.ins.modules.models.Ins_instrument;
import cn.wizzer.app.ins.modules.services.InsInstrumentService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IocBean
@At("/platform/ins/instrument")
public class InsInstrumentController {
    private static final Log log = Logs.get();
    @Inject
    private InsInstrumentService insInstrumentService;

    @At("")
    @Ok("beetl:/platform/ins/instrument/index.html")
    @RequiresPermissions("platform.ins.instrument.select")
    public void index() {
    }

    @At("/data")
    @Ok("json:full")
    @RequiresPermissions("platform.ins.instrument.select")
    public NutMap data(@Param("cabinetId")String cabinetId,@Param("spaceId")String spaceId,@Param("insnum")String insnum, @Param("insname")String insname, @Param("number")String number, @Param("pstatus")String pstatus, @Param("linePstatus")String linePstatus, @Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
        Cnd cnd = Cnd.NEW();
//        return insInstrumentService.data(length, start, draw, order, columns, cnd, "insSpace|insRfid|insBaseins");
        return insInstrumentService.getInsList(length, start, draw, order, columns, cnd,insnum,insname,number,pstatus,linePstatus,cabinetId,spaceId);
    }

    @At("/add")
    @Ok("beetl:/platform/ins/instrument/add.html")
    @RequiresPermissions("platform.ins.instrument")
    public void add(){
    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.ins.instrument.add")
    @SLog(tag = "工具表", msg = "${args[0].id}")
    public Object addDo(@Param("..") Ins_instrument insInstrument, HttpServletRequest req) {
        try {
            Sql sql = Sqls.queryRecord("SELECT MAX(insnum) insnum FROM ins_instrument ");
            insInstrumentService.dao().execute(sql);
            List<Record> recordList = sql.getList(Record.class);
            if(recordList.size() > 0){
                String insnum = recordList.get(0).getString("insnum");
                insnum = insnum.substring(insnum.length() - 4, insnum.length());
                insInstrument.setInsnum("TOOL-000-" + String.format("%04d",Integer.valueOf(insnum) + 1));
            }else{
                insInstrument.setInsnum("TOOL-000-" + String.format("%04d",1));
            }
            insInstrumentService.insert(insInstrument);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/edit/?")
    @Ok("beetl:/platform/ins/instrument/edit.html")
    @RequiresPermissions("platform.ins.instrument")
    public void edit(String id, HttpServletRequest req) {
        Ins_instrument insInstrument = insInstrumentService.fetch(id);
        if (insInstrument != null) {
            req.setAttribute("obj", insInstrumentService.fetchLinks(insInstrument, "insSpace|insRfid|insBaseins"));
        }
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.ins.instrument.edit")
    @SLog(tag = "工具表", msg = "${args[0].id}")
    public Object editDo(@Param("..") Ins_instrument insInstrument, HttpServletRequest req) {
        try {
            insInstrument.setOpBy(StringUtil.getUid());
            insInstrument.setOpAt((int) (System.currentTimeMillis() / 1000));
            insInstrumentService.updateIgnoreNull(insInstrument);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.ins.instrument.delete")
    @SLog(tag = "工具表", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids") String[] ids, HttpServletRequest req) {
        try {
            if (ids != null && ids.length > 0) {
                List<Ins_instrument> instruments = insInstrumentService.query(Cnd.where("id", "IN", ids));
                if(instruments.size() > 0){
                    List<String> infoList = new ArrayList<>();
                    for (Ins_instrument instrument : instruments) {
                        if(!Strings.isBlank(instrument.getRfid())){
                            infoList.add(instrument.getInsnum());
                        }
                    }
                    if(infoList.size() > 0){
                        StringBuffer sb = new StringBuffer(80);
                        sb.append("请先解绑工具RFID!");
                        sb.append("工具编号:");
                        for (String insnum : infoList) {
                            sb.append(insnum).append(";");
                        }
                        return Result.error(sb.toString());
                    }
                    insInstrumentService.delete(ids);
                    req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
                }
            } else {
                Ins_instrument instrument = insInstrumentService.fetch(id);
                if(instrument != null){
                    if(!Strings.isBlank(instrument.getRfid())){
                        StringBuffer sb = new StringBuffer(80);
                        sb.append("请先解绑工具RFID!");
                        sb.append("工具编号:");
                        sb.append(instrument.getInsnum());
                        return Result.error(sb.toString());
                    }
                    insInstrumentService.delete(id);
                    req.setAttribute("id", id);
                }
            }
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error",e);
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/ins/instrument/detail.html")
    @RequiresPermissions("platform.ins.instrument.select")
    public void detail(String id, HttpServletRequest req) {
        if (!Strings.isBlank(id)) {
            Ins_instrument insInstrument = insInstrumentService.fetch(id);
            if (insInstrument != null) {
                req.setAttribute("obj", insInstrumentService.fetchLinks(insInstrument, "insSpace|insRfid|insBaseins"));
            }
        } else {
            req.setAttribute("obj", null);
        }
    }

    @At("/bindSpace")
    @Ok("json")
    @RequiresPermissions("platform.ins.instrument.space")
    public Object bindSpace(@Param("insIds") String insIds[], @Param("spaceid") String spaceid, HttpServletRequest req) {
        try {
            insInstrumentService.bindSpace(insIds, spaceid);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }

    }

    @At("/updateRFID")
    @Ok("json")
    @RequiresPermissions("platform.ins.instrument.rfid")
    public Object updateRFID(@Param("insId") String insId, @Param("rfID") String rfID, HttpServletRequest req) {
        try {

            insInstrumentService.updateRFID(insId, rfID);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }

    }

    @At("/getRFID/?")
    @Ok("beetl:/platform/ins/rfid/selectRFID.html")
    public void getRFID(String id, HttpServletRequest req) {
        if (!Strings.isBlank(id)) {
            Ins_instrument insInstrument = insInstrumentService.fetch(id);
            if (insInstrument != null) {
                req.setAttribute("obj", insInstrumentService.fetchLinks(insInstrument, "baseAirport"));
            }
        } else {
            req.setAttribute("obj", null);
        }
    }

    //上传图片
    @At("/uploadIns")
    @AdaptBy(type = UploadAdaptor.class, args = {"ioc:imageUpload"})
    @Ok("json")
    public Object uploadIns(@Param("uploads") TempFile tf, @Param("insId") String insId) {
        Map<String, Object> map = new HashMap<>();
        if (tf == null || "".equals(tf.getSubmittedFileName())) {
            map.put("result", "上传失败");
            return map;
        }
        try {
            return insInstrumentService.uploadIns(tf, insId);
        } catch (Exception e) {
            log.debug(e.getMessage());
            map.put("result", "上传失败");
        }
        return map;
    }

    @At("/onLineIns")
    @Ok("json")
    @RequiresPermissions("platform.ins.instrument.onLine")
    public Object onLineIns(@Param("insIds")String[] insIds) {
        try {
            return insInstrumentService.onLineIns(insIds);
        }catch (Exception e){
            return Result.error(e.getMessage());
        }
    }
    @At("/offLineIns")
    @Ok("json")
    @RequiresPermissions("platform.ins.instrument.offLine")
    public Object offLineIns(@Param("insIds")String[] insIds) {
        try {
            return insInstrumentService.offLineIns(insIds);
        }catch (Exception e){
            return Result.error(e.getMessage());
        }
    }
    @At("/unbindRFIDs")
    @Ok("json")
    @RequiresPermissions("platform.ins.instrument.unbindRFIDs")
    public Object unbindRFIDs(@Param("insIds")String[] insIds) {
        try {
            return insInstrumentService.unbindRFIDs(insIds);
        }catch (Exception e){
            return Result.error(e.getMessage());
        }
    }
}
