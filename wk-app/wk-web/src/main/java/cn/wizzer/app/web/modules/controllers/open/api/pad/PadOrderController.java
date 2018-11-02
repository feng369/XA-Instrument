package cn.wizzer.app.web.modules.controllers.open.api.pad;

import cn.wizzer.app.ins.modules.services.*;
import cn.wizzer.app.web.commons.filter.TokenFilter;
import cn.wizzer.framework.base.Result;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.filter.CrossOriginFilter;
import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.apidoc.annotation.ApiMatchMode;
import org.nutz.plugins.apidoc.annotation.ApiParam;
import org.nutz.plugins.apidoc.annotation.ReturnKey;

import javax.servlet.http.HttpServletResponse;

@IocBean
@At("/open/pad/order")
//@Filters({@By(type = TokenFilter.class)})
@Api(name = "平板订单", match = ApiMatchMode.ALL, description = "平板订单相关api接口")
public class PadOrderController {
    private Log log = Logs.get();
    @Inject
    Dao dao;
    @Inject
    private InsCabinetService insCabinetService;
    @Inject
    private InsSpaceService insSpaceService;
    @Inject
    private InsRfidService insRfidService;
    @Inject
    private InsInstrumentService insInstrumentService;
    @Inject
    private InsToolcabinetService insToolcabinetService;
    @Inject
    private InsCommonService insCommonService;
    @Inject
    private InsBaseinsService insBaseinsService;
    @Inject
    private InsOrderService insOrderService;
    @Inject
    private InsOrderentryService insOrderentryService;
    /*@At("/doCheckOrders")
    @Ok("json:full")
    @Api(name = "（关闭柜门后）检查订单中的工具是否已经全部归还"
            , params = {
            @ApiParam(name = "addRFIDs", type = "String", description = "增加的RFID标签号"),
            @ApiParam(name = "reduceRFIDs", type = "String", description = "减少的RFID标签号"),
            @ApiParam(name = "orderId", type = "String", description = "订单ID"),
    },
            ok = {
                    @ReturnKey(key = "code", description = "code=0 操作成功"),
            }
    )
    @Filters({@By(type = CrossOriginFilter.class)})
    public Result doCheckOrders(@Param("addRFIDs") String[] addRFIDs, @Param("orderId") String orderId, @Param("reduceRFIDs") String[] reduceRFIDs) {
        try {
            insInstrumentService.doCheckOrders(addRFIDs, reduceRFIDs, orderId);
            return Result.success("system.success");
        } catch (Exception e) {
            log.debug(e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    */

    @At("/doCheckOrders")
    @Ok("json:full")
    @Api(name = "（关闭柜门后）检查订单中的工具是否已经借出或全部归还"
            , params = {
            @ApiParam(name = "rfIDs", type = "String", description = "轮询得到的此次仓位内RFID标签号数组"),
            @ApiParam(name = "cabinetType", type = "String", description = "智能柜类型 传:YDcabinet(移动智能柜) GDcabinet(固定柜)"),
            @ApiParam(name = "spaceNum", type = "String", description = "关闭操作对应的仓位编号"),
            @ApiParam(name = "orderId", type = "String", description = "订单ID"),
    },
            ok = {
                    @ReturnKey(key = "code", description = "code=0操作成功 2归还错误 3根据RFID未找到对应实体工具数据"),
            }
    )
    @Filters({@By(type = CrossOriginFilter.class)})
    public Result doCheckOrders(@Param("rfIDs") String[] rfIDs, @Param("orderId") String orderId,@Param("spaceNum")String spaceNum,@Param("cabinetType")String cabinetType) {
        try {
            return insInstrumentService.doCheckOrders(rfIDs,spaceNum,orderId,cabinetType);
        }catch(Exception e){
            log.debug(e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    @At("/getOrderAndEntryList")
    @Api(name = "得到订单列表"
            , params = {
            @ApiParam(name = "personId", type = "String", description = "订单操作人ID"),
            @ApiParam(name = "cabinetId", type = "String", description = "智能柜ID"),
            @ApiParam(name = "pagenumber", type = "Integer", description = "当前页,默认返回第一页"),
            @ApiParam(name = "pagesize", type = "Integer", description = "每页最多显示条数,默认返回10条", optional = true),
            @ApiParam(name = "orderPstatus", type = "String", description = "订单状态:orderPstatus未填,显示所有.0未完成,1已完成", optional = true),
    },
            ok = {
                    @ReturnKey(key = "id", description = "订单ID"),
                    @ReturnKey(key = "ordernum", description = "订单编码"),
                    @ReturnKey(key = "personName", description = "下单人姓名"),
                    @ReturnKey(key = "airportname", description = "机场名称"),
                    @ReturnKey(key = "flightnum", description = "航班号"),
                    @ReturnKey(key = "operatetime", description = "操作时间"),
                    @ReturnKey(key = "pstatus", description = "订单状态 0未完成 1已完成"),
//                    @ReturnKey(key = "starttime", description = "订单开始时间"),
//                    @ReturnKey(key = "endtime", description = "订单结束时间"),
//                    @ReturnKey(key = "describ", description = "订单备注"),
//                  @ReturnKey(key = "insId", description = "实体工具ID"),
//                  @ReturnKey(key = "insPstatus", description = "实体工具借用状态  0:未借用 2:借用中"),
                    @ReturnKey(key = "insModel", description = "工具型号"),
                    @ReturnKey(key = "insName", description = "工具名称"),
                    @ReturnKey(key = "toolPic", description = "工具图片"),
                    @ReturnKey(key = "entryStatus", description = "订单明细状态 1.待借用 2.借用中 3.已归还"),

                    @ReturnKey(key = "pstatusname", description = "订单状态名称"),
                    @ReturnKey(key = "picSet", description = "工具图片相对路径Set集合"),
                    @ReturnKey(key = "orderentryNumber", description = "订单明细數量"),
                    @ReturnKey(key = "storeAddress", description = "工具所在位置(柜名:仓位1 仓位2 ... )"),
                    @ReturnKey(key = "entryList", description = "订单明细相关信息对象列表"),
                    @ReturnKey(key = "entryStatusName", description = "订单明细状态名称"),
                    @ReturnKey(key = "canOpen", description = "智能柜是否能打开 1可以,0否"),
                    @ReturnKey(key = "spaceNumSet", description = "待借用、借用中状态订单明细对应工具存在于此柜子中,能开锁的Set集合(目前size=1,后续可能基础工具对应多个仓位,故用Set集合)"),
}
    )
    @Filters({@By(type = CrossOriginFilter.class)})
    public Object getOrderAndEntryList(@Param("personId") String personId, @Param("pagenumber") Integer pagenumber, @Param("pagesize") Integer pagesize, @Param("orderPstatus") String orderPstatus,@Param("cabinetId")String cabinetId) {
        try {
            return Result.success("system.success",insOrderService.getAllOrderAndEntry(personId, pagenumber, pagesize, orderPstatus,cabinetId));
        } catch (Exception e) {
            log.debug(e.getMessage());
            return Result.error(e.getMessage());
        }
    }

}


