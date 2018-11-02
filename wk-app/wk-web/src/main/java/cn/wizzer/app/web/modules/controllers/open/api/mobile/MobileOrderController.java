package cn.wizzer.app.web.modules.controllers.open.api.mobile;

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
@At("/open/mobile/order")
@Filters({@By(type = TokenFilter.class)})
@Api(name = "订单", match = ApiMatchMode.ALL, description = "订单相关api接口")
public class MobileOrderController {
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
    @At("/getOrderAndEntryList")
    @Api(name = "得到订单列表"
            , params = {
            @ApiParam(name = "operator", type = "String", description = "订单操作人ID"),
            @ApiParam(name = "orderPstatus", type = "String", description = "订单状态:orderPstatus未填,显示所有.0未完成,1已完成", optional = true),
            @ApiParam(name = "pagenumber", type = "Integer", description = "当前页,默认返回第一页")
            ,@ApiParam(name = "pagesize", type = "Integer", description = "每页最多显示条数,默认返回10条", optional = true)
    },
            ok = {
                    @ReturnKey(key = "id", description = "订单ID"),
                    @ReturnKey(key = "ordernum", description = "订单编码"),
                    @ReturnKey(key = "personName", description = "下单人姓名"),
                    @ReturnKey(key = "airportname", description = "机场名称"),
                    @ReturnKey(key = "flightnum", description = "航班号"),
                    @ReturnKey(key = "operatetime", description = "操作时间"),
                    @ReturnKey(key = "pstatus", description = "订单状态 0未完成 1已完成"),
                    @ReturnKey(key = "pstatusname", description = "订单状态名称"),
                    @ReturnKey(key = "starttime", description = "订单开始时间"),
                    @ReturnKey(key = "endtime", description = "订单结束时间"),
                    @ReturnKey(key = "describ", description = "订单备注"),
                    @ReturnKey(key = "orderentryNumber", description = "订单明细數量"),
                    @ReturnKey(key = "picSet", description = "工具图片相对路径Set集合"),
//                    @ReturnKey(key = "insId", description = "实体工具ID"),
//                    @ReturnKey(key = "insPstatus", description = "实体工具借用状态  0:未借用 2:借用中"),
                    @ReturnKey(key = "insModel", description = "工具型号"),
                    @ReturnKey(key = "insName", description = "工具名称"),
                    @ReturnKey(key = "toolPic", description = "工具图片"),
                    @ReturnKey(key = "storeAddress", description = "工具所在位置(柜名:仓位1 仓位2 ... )"),
//                    @ReturnKey(key = "spaceName", description = "工具所属仓位"),
//                    @ReturnKey(key = "cabinetName", description = "仓位所属柜子名称"),
                    @ReturnKey(key = "entryList", description = "订单明细相关信息对象列表"),
                    @ReturnKey(key = "entryStatus", description = "订单明细状态 1.待借用 2.借用中 3.已归还"),
                    @ReturnKey(key = "entryStatusName", description = "订单明细状态名称"),
            }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object getOrderAndEntryList(@Param("operator") String operator, @Param("pagenumber") Integer pagenumber, @Param("pagesize") Integer pagesize, @Param("orderPstatus") String orderPstatus) {
        try {
            return Result.success("system.success",insOrderService.getOrderAndEntryList(operator, pagenumber, pagesize, orderPstatus));
        } catch (Exception e) {
            log.debug(e.getMessage());
            return Result.error(e.getMessage());
        }
    }



    @At("/getOrderListByMoibile")
    @Ok("json:full")
    @Api(name = "得到订单列表"
            , params = {
            @ApiParam(name = "operator", type = "String", description = "订单操作人ID"),
            @ApiParam(name = "orderPstatus", type = "String", description = "订单状态:orderPstatus未填,显示所有.0未完成,1已完成", optional = true),
            @ApiParam(name = "pagenumber", type = "Integer", description = "当前页,默认返回第一页")
            , @ApiParam(name = "pagesize", type = "Integer", description = "每页最多显示条数,默认返回10条", optional = true)
    },
            ok = {

                    @ReturnKey(key = "id", description = "订单ID"),
                    @ReturnKey(key = "ordernum", description = "订单编码"),
                    @ReturnKey(key = "airportname", description = "机场名称"),
                    @ReturnKey(key = "flightnum", description = "航班号"),
                    @ReturnKey(key = "operatetime", description = "操作时间"),
                    @ReturnKey(key = "pstatus", description = "订单状态 0未完成 1已完成"),
                    @ReturnKey(key = "pstatusname", description = "订单状态名称"),
                    @ReturnKey(key = "starttime", description = "订单开始时间"),
                    @ReturnKey(key = "endtime", description = "订单结束时间"),
                    @ReturnKey(key = "describ", description = "订单备注"),
                    @ReturnKey(key = "orderentryNumber", description = "订单明细數量"),
                    @ReturnKey(key = "picList", description = "工具图片相对路径集合"),
            }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object getOrderListByMoibile(@Param("operator") String operator, @Param("pagenumber") Integer pagenumber, @Param("pagesize") Integer pagesize, @Param("orderPstatus") String orderPstatus) {
        try {
            return Result.success("system.success", insOrderService.getOrderListByMoibile(operator, pagenumber, pagesize, orderPstatus));
        } catch (Exception e) {
            log.debug(e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @At("/getOrderEntryListByMoibile")
    @Ok("json:full")
    @Api(name = "订单明细列表(保留接口)"
            , params = {
            @ApiParam(name = "orderid", type = "String", description = "订单ID"),
            @ApiParam(name = "pagenumber", type = "Integer", description = "当前页,默认返回第一页", optional = true)
            , @ApiParam(name = "pagesize", type = "Integer", description = "每页最多显示条数,默认返回10条", optional = true)
    },
            ok = {
                    @ReturnKey(key = "id", description = "订单详情ID"),
                    @ReturnKey(key = "personname", description = "订单详情操作人"),
                    @ReturnKey(key = "pstatus", description = "订单详情状态值 0:未借用 1.待借用 2.借用中 3.已归还"),
                    @ReturnKey(key = "pstatusname", description = "订单详情状态名称"),
                    @ReturnKey(key = "insmodel", description = "工具型号"),
                    @ReturnKey(key = "insparam", description = "工具参数"),
                    @ReturnKey(key = "toolPic", description = "工具图片相对路径"),
                    @ReturnKey(key = "insUnitName", description = "工具所属单位"),
                    @ReturnKey(key = "describ", description = "工具描述"),
                    @ReturnKey(key = "rfidNumber", description = "工具RFID"),
                    @ReturnKey(key = "spacename", description = "工具所属仓位"),
                    @ReturnKey(key = "insname", description = "工具名"),
                    @ReturnKey(key = "insnum", description = "实体工具编号"),
                    @ReturnKey(key = "insId", description = "实体工具ID"),
                    @ReturnKey(key = "cabinetname", description = "实体工具箱子名称")
            }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object getOrderEntryListByMoibile(@Param("orderid") String orderid, @Param("pagenumber") Integer pagenumber, @Param("pagesize") Integer pagesize, HttpServletResponse response) {
        try {
            return Result.success("system.success",insOrderentryService.getOrderEntryListByMoibile(orderid,pagenumber,pagesize));
        } catch (Exception e) {
            log.debug(e.getMessage());
            return Result.error(e.getMessage());
        }
    }


    @At("/addOrderAndOrderEntryByMobile")
    @Ok("json:full")
    @Api(name = "生成订单、订单明细"
            , params = {
            @ApiParam(name = "boxIds", type = "String", description = "勾选状态工具箱ID数组字符串"),
            @ApiParam(name = "operator", type = "String", description = "下单人ID(目前无替他人添加订单需求)"),
            @ApiParam(name = "operatetime", type = "String", description = "下单时间"),
            @ApiParam(name = "flightnum", type = "String", description = "航班号"),
            @ApiParam(name = "seatnum", type = "String", description = "机位"),
            @ApiParam(name = "airportid", type = "String", description = "机场ID"),
            @ApiParam(name = "describ", type = "String", description = "订单备注"),
            @ApiParam(name = "selectCount", type = "Integer", description = "选中的工具总数量"),
    },
            ok = {
                    @ReturnKey(key = "code", description = "code=0,操作成功,cose=2 工具库存不足(会返回具体哪个工具库存不足)"),
            }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Result addOrderAndOrderEntryByMobile(@Param("boxIds") String[] boxIds, @Param("operator") String operator, @Param("operatetime") String operatetime, @Param("flightnum") String flightnum, @Param("seatnum") String seatnum, @Param("describ")  String describ, @Param("airportid") String airportid) {
        try {
            return insOrderService.addOrderAndOrderEntryByMobile(boxIds, operator, operatetime, flightnum, seatnum, describ, airportid);
        } catch (Exception e) {
            log.debug(e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @At("/getOrderInfo")
    @Ok("json:full")
    @Api(name = "订单基本信息详情"
            , params = {
            @ApiParam(name = "orderId", type = "String", description = "订单ID"),
    },
            ok = {
                    @ReturnKey(key = "describ", description = "订单备注"),
                    @ReturnKey(key = "pstatus", description = "订单状态值 0未完成 1已完成"),
                    @ReturnKey(key = "pstatusname", description = "订单状态名称"),
                    @ReturnKey(key = "operatetime", description = "订单操作时间"),
                    @ReturnKey(key = "ordernum", description = "订单编号"),
                    @ReturnKey(key = "flightnum", description = "航班号"),
                    @ReturnKey(key = "seatnum", description = "机位"),
                    @ReturnKey(key = "airportname", description = "机场名称"),
                    @ReturnKey(key = "personname", description = "订单所属人员名称")
            }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object getOrderInfo(@Param("orderId") String orderId) {
        try {
            return Result.success("system.success", insOrderService.getOrderInfo(orderId));
        } catch (Exception e) {
            log.debug(e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @At("/getAllBorrowingOrderEntries")
    @Ok("json:full")
    @Api(name = "得到所有正在借用中订单明细"
            , params = {
            @ApiParam(name = "personid", type = "String", description = "工具借用人ID(转交人)"),
            @ApiParam(name = "pagenumber", type = "Integer", description = "当前页,默认返回第一页", optional = true),
            @ApiParam(name = "pagesize", type = "Integer", description = "每页最多显示条数,默认返回10条", optional = true)
    },
            ok = {
                    @ReturnKey(key = "id", description = "订单明细ID"),
                    @ReturnKey(key = "orderId", description = "订单ID"),
                    @ReturnKey(key = "insId", description = "实体工具ID"),
                    @ReturnKey(key = "describ", description = "实体工具描述"),
                    @ReturnKey(key = "spacename", description = "实体工具所在仓位名称"),
//                    @ReturnKey(key = "insnum", description = "实体工具编号"),
                    @ReturnKey(key = "insnum", description = "基础工具编号"),
                    @ReturnKey(key = "insname", description = "基本工具名称"),
                    @ReturnKey(key = "insparam", description = "基本工具参数"),
                    @ReturnKey(key = "insmodel", description = "基本工具型号"),
                    @ReturnKey(key = "toolPic", description = "基本工具图片相对路径"),
                    @ReturnKey(key = "ordernum", description = "当前实体工具对应的订单编号"),
                    @ReturnKey(key = "cabinetName", description = "所属柜名"),
                    @ReturnKey(key = "entryPstatus", description = "订单明细状态 0：未借用 1：待借用 2.借用中 3.已归还"),
                    @ReturnKey(key = "entryPstatusName", description = "订单明细状态名称"),
                    @ReturnKey(key = "storeAddress", description = "库存地址"),
//                    @ReturnKey(key = "insPstatusName", description = "当前实体工具对应的状态名称"),

            }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object getAllBorrowingOrderEntries(@Param("personid")String personid,@Param("pagenumber")Integer pagenumber,@Param("pagesize")Integer pagesize) {
        try {
            return Result.success("system.success",insOrderentryService.getAllBorrowingOrderEntries(personid,pagenumber,pagesize));
        } catch (Exception e) {
            log.debug(e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @At("/clickDeliver2Code")
    @Ok("json:full")
    @Api(name = "转交页面生成二维码事件,生成时间戳标志"
            , params = {
            @ApiParam(name = "personid", type = "String", description = "工具借用人ID"),
            @ApiParam(name = "orderEntryIds", type = "String", description = "选择转交的订单明细ID数组字符串"),
    },
            ok = {
                    @ReturnKey(key = "dateflag", description = "时间戳标志"),
            }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Result clickDeliver2Code(@Param("personid")String personid, @Param("orderEntryIds")String[] orderEntryIds) {
        try {
            return Result.success("system.success",insOrderentryService.clickDeliver2Code(personid,orderEntryIds));
        } catch (Exception e) {
            log.debug(e.getMessage());
            return Result.error(e.getMessage());
        }
    }


    @At("/transmitOrderEntries")
    @Ok("json:full")
    @Api(name = "工具移交(被转交人扫描二维码)"
            , params = {
            @ApiParam(name = "newPersonId", type = "String", description = "被转交人ID"),
            @ApiParam(name = "dateflag", type = "String", description = "需转交的订单明细标志(时间戳)"),
    },
            ok = {
                    @ReturnKey(key = "code", description = "code=0 转交成功"),
            }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object transmitOrderEntry(@Param("newPersonId")String newPersonId,@Param("dateflag")String dateflag) {
        try {
            insOrderentryService.transmitOrderEntry(newPersonId,dateflag);
            return Result.success("system.success");
        } catch (Exception e) {
            log.debug(e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    @At("/listenTransmit")
    @Ok("json:full")
    @Api(name = "生成二维码后,监听工具移交"
            , params = {
            @ApiParam(name = "dateflag", type = "String", description = "选择标志:时间戳"),
    },
            ok = {
                    @ReturnKey(key = "result", description = "success 成功,fail 失败"),
            }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object listenTransmit(@Param("dateflag")String dateflag) {
        try {
            return Result.success("system.success",insOrderentryService.listenTransmit(dateflag));
        } catch (Exception e) {
            log.debug(e.getMessage());
            return Result.error(e.getMessage());
        }
    }
   /*
   public static void main(String[] args) throws UnsupportedEncodingException {
        *//*String xx = "å\u0093\u0088å\u0093\u0088å\u0093\u0088";
        String s = new String(xx.getBytes(), "UTF-8");*//*
//        String s = new String(xx.getBytes(), "ISO-8859-1");
        System.out.println(s);
    }
    */

}


