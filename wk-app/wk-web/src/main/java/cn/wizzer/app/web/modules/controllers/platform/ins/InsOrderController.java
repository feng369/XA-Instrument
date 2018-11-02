package cn.wizzer.app.web.modules.controllers.platform.ins;

import cn.wizzer.app.ins.modules.services.InsOrderentryService;
import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.ins.modules.models.Ins_order;
import cn.wizzer.app.ins.modules.services.InsOrderService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.beetl.ext.nutz.BeetlView;
import org.beetl.ext.nutz.BeetlViewMaker;
import org.nutz.dao.Cnd;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.view.ViewWrapper;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@IocBean
@At("/platform/ins/order")
public class InsOrderController {
    private static final Log log = Logs.get();
    @Inject
    private InsOrderService insOrderService;
    @Inject
    private InsOrderentryService insOrderentryService;

    @At("")
    @Ok("beetl:/platform/ins/order/index.html")
    @RequiresPermissions("platform.ins.order.select")
    public void index() {
    }

    @At("/stockOrders")
    @Ok("beetl:/platform/ins/order/stockOrders.html")
    @RequiresPermissions("platform.ins.order.stockOrders")
    public void stockOrders() {
    }

    @At("/data")
    @Ok("json:full")
    @RequiresPermissions("platform.ins.order.select")
    public Object data(@Param("pstatus") String pstatus, @Param("ordernum") String ordernum, @Param("operator") String operator, @Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
        Cnd cnd = Cnd.NEW();
        if (!Strings.isBlank(pstatus)) {
            cnd.and("pstatus", "=", pstatus);
        }
        if (!Strings.isBlank(ordernum)) {
            cnd.and("ordernum", "like", "%" + ordernum + "%");
        }
        if (!Strings.isBlank(operator)) {
            cnd.and("operator", "=", operator);
        }
        return insOrderService.data(length, start, draw, order, columns, cnd, "baseAirport|basePerson");
    }

    @At("/getStockOrders")
    @Ok("json:full")
    @RequiresPermissions("platform.ins.getStockOrders.select")
    public Object getStockOrders(@Param("pstatus") String pstatus, @Param("ordernum") String ordernum, @Param("operator") String operator, @Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
        Cnd cnd = Cnd.NEW();
        if (!Strings.isBlank(pstatus)) {
            cnd.and("pstatus", "=", pstatus);
        }
        if (!Strings.isBlank(ordernum)) {
            cnd.and("ordernum", "like", "%" + ordernum + "%");
        }
        if (!Strings.isBlank(operator)) {
            cnd.and("operator", "=", operator);
        }
        return insOrderService.data(length, start, draw, order, columns, cnd, "baseAirport|basePerson");
    }

    @At("/add")
    @Ok("beetl:/platform/ins/order/add.html")
    @RequiresPermissions("platform.ins.order")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.ins.order.add")
    @SLog(tag = "订单表", msg = "${args[0].id}")
    public Object addDo(@Param("..") Ins_order insOrder, HttpServletRequest req) {
        try {
            insOrderService.insert(insOrder);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/edit/?")
    @Ok("beetl:/platform/ins/order/edit.html")
    @RequiresPermissions("platform.ins.order")
    public void edit(String id, HttpServletRequest req) {
        req.setAttribute("obj", insOrderService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.ins.order.edit")
    @SLog(tag = "订单表", msg = "${args[0].id}")
    public Object editDo(@Param("..") Ins_order insOrder, HttpServletRequest req) {
        try {
            insOrder.setOpBy(StringUtil.getUid());
            insOrder.setOpAt((int) (System.currentTimeMillis() / 1000));
            insOrderService.updateIgnoreNull(insOrder);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.ins.order.delete")
    @SLog(tag = "订单表", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids") String[] ids, HttpServletRequest req) {
        try {
            if (ids != null && ids.length > 0) {
                insOrderService.delete(ids);
                req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
            } else {
                insOrderService.delete(id);
                req.setAttribute("id", id);
            }
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }
    @At({"/prepareTools"})
    @Ok("json")
    @RequiresPermissions("platform.ins.order.prepareTools")
    public Object prepareTools(String id, @Param("ids") String[] ids, HttpServletRequest req) {
        try {
            if (ids != null && ids.length > 0) {
                insOrderService.prepareTools(ids);
            }else{
                insOrderService.prepareTool(id);
            }
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error",e);
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/ins/order/detail.html")
    @RequiresPermissions("platform.ins.order.select")
    public void detail(String id, HttpServletRequest req) {
        if (!Strings.isBlank(id)) {
            req.setAttribute("obj", insOrderService.getOrderDetail(id));
        } else {
            req.setAttribute("obj", null);
        }
//            return new ViewWrapper(new BeetlView(new BeetlViewMaker().render, "/platform/ins/order/detail.html"), "");
//            return Result.success("system.success");
    }


    @At({"/getOrderCount"})
    @Ok("json")
    public Object getOrderCount(HttpServletRequest req) {
        String airportId = req.getSession().getAttribute("airportid").toString();
        return insOrderService.getOrderCount(airportId);
    }

}
