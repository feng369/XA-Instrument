package cn.wizzer.app.web.modules.controllers.open.api.mobile;

import cn.wizzer.app.ins.modules.services.*;
import cn.wizzer.app.web.commons.filter.TokenFilter;
import cn.wizzer.framework.base.Result;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.filter.CrossOriginFilter;
import org.nutz.mvc.impl.AdaptorErrorContext;
import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.apidoc.annotation.ApiMatchMode;
import org.nutz.plugins.apidoc.annotation.ApiParam;
import org.nutz.plugins.apidoc.annotation.ReturnKey;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@IocBean
@At("/open/mobile/ins")
@Filters({@By(type = TokenFilter.class)})
@Api(name = "手机工具管理", match = ApiMatchMode.ALL, description = "手机工具管理相关api接口")
public class MobileInsController {
    private Log log = Logs.get();
    private static Logger infoLog = Logger.getLogger("InfoLog");
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
    private InsRepairService insRepairService;


    /*
    @At("/getCabinetList")
    @Ok("json:full")
    @Api(name = "得到智能柜列表"
            , params = { @ApiParam(name = "pagenumber", type = "Integer", description = "当前页,默认返回第一页")
            , @ApiParam(name = "pagesize", type = "Integer", description = "每页最多显示条数,默认返回10条",optional = true)},
            ok = {
            @ReturnKey(key = "id", description = "智能柜对象ID"),
            @ReturnKey(key = "cabinetname", description = "智能柜对象名称"),
            @ReturnKey(key = "cabinetnum", description = "智能柜对象编号"),
            @ReturnKey(key = "address", description = "智能柜对象地址"),
            @ReturnKey(key = "pstatus", description = "智能柜对象状态"),
            @ReturnKey(key = "pstatusname", description = "智能柜对象状态名称"),
            @ReturnKey(key = "airportid", description = "智能柜对象所属机场"),

    }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object getCabinetList(
            @Param("pagenumber")Integer pagenumber,@Param("pagesize")Integer pagesize){
        try {
            return Result.success("system.success",insCabinetService.getCabinetList(pagenumber,pagesize));
        }catch (Exception e){
            log.debug(e.getMessage());
            return Result.error("system.error");
        }
    }

    @At("/getSpaceList")
    @Ok("json:full")
    @Api(name = "得到智能柜列表"
            , params = {@ApiParam(name = "pagenumber", type = "Integer", description = "当前页,默认返回第一页")
            , @ApiParam(name = "pagesize", type = "Integer", description = "每页最多显示条数,默认返回10条",optional = true)},
            ok = {
            @ReturnKey(key = "id", description = "仓位ID"),
            @ReturnKey(key = "spacename", description = "仓位名称"),
            @ReturnKey(key = "spacenum", description = "仓位编码"),
            @ReturnKey(key = "params", description = "仓位参数"),
            @ReturnKey(key = "model", description = "仓位型号"),
            @ReturnKey(key = "address", description = "仓位地址"),
            @ReturnKey(key = "barcode", description = "仓位对应二维码"),
    }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object getSpaceList(@Param("pagenumber")Integer pagenumber,@Param("pagesize")Integer pagesize){
        try {
            return Result.success("system.success",insSpaceService.getSpaceList(pagenumber,pagesize));
        }catch (Exception e){
            log.debug(e.getMessage());
            return Result.error("system.error");
        }
    }*/
    @At("/getBaseInsList")
    @Ok("json:full")
    @Api(name = "得到基础工具列表(上线状态)"
            , params = {
            @ApiParam(name = "childDictId", type = "String", description = "(数据字典)工具类型小类ID"),
            @ApiParam(name = "pagenumber", type = "Integer", description = "当前页,默认返回第一页"),
            @ApiParam(name = "pagesize", type = "Integer", description = "每页最多显示条数,默认返回10条", optional = true)
    },
            ok = {
                    @ReturnKey(key = "id", description = "工具ID"),
                    @ReturnKey(key = "insnum", description = "工具编码"),
                    @ReturnKey(key = "insname", description = "工具名称"),
                    @ReturnKey(key = "insparam", description = "工具参数"),
                    @ReturnKey(key = "insmodel", description = "工具型号"),
                    @ReturnKey(key = "insunitName", description = "工具所属单位名称"),
                    @ReturnKey(key = "toolPic", description = "工具图片相对路径"),
                    @ReturnKey(key = "spaceName", description = "仓位名称"),
                    @ReturnKey(key = "useableCount", description = "可用数量(库存)"),
            }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object getBaseInsList(@Param("childDictId") String childDictId, @Param("pagenumber") Integer pagenumber, @Param("pagesize") Integer pagesize) {
        try {
            return Result.success("system.success", insBaseinsService.getBaseInsList(childDictId, pagenumber, pagesize));
        } catch (Exception e) {
            log.debug(e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @At("/getToolBoxList")
    @Ok("json:full")
    @Api(name = "得到工具箱列表"
            , params = {
            @ApiParam(name = "personid", type = "String", description = "借用人员ID"),
            @ApiParam(name = "pagenumber", type = "Integer", description = "当前页,默认返回第一页")
            , @ApiParam(name = "pagesize", type = "Integer", description = "每页最多显示条数,默认返回10条", optional = true)
    },
            ok = {
                    @ReturnKey(key = "id", description = "工具箱ID"),
                    @ReturnKey(key = "number", description = "工具箱显示数量"),
                    @ReturnKey(key = "pstatus", description = "工具箱状态 0未勾选  1已勾选(保留字段)"),
                    @ReturnKey(key = "insId", description = "基础工具ID"),
                    @ReturnKey(key = "insname", description = "基础工具名称"),
                    @ReturnKey(key = "insnum", description = "基础工具编号"),
                    @ReturnKey(key = "toolPic", description = "基础工具图片相对路径"),
                    @ReturnKey(key = "insmodel", description = "基础工具型号"),
                    @ReturnKey(key = "insparam", description = "基础工具参数"),
                    @ReturnKey(key = "insNumber", description = "工具库存数量"),
//                    @ReturnKey(key = "spaceName", description = "仓位名称"),
//                    @ReturnKey(key = "storeAddress", description = "工具所在位置"),
            }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object getToolBoxList(@Param("personid") String personid, @Param("pagenumber") Integer pagenumber, @Param("pagesize") Integer pagesize) {
        try {
            return Result.success("system.success", insToolcabinetService.getToolBoxList(personid, pagenumber, pagesize));
        } catch (Exception e) {
            log.debug(e.getMessage());
            return Result.error(e.getMessage());
        }
    }


    @At("/getCommonTools")
    @Ok("json:full")
    @Api(name = "得到常用工具列表"
            , params = {
            @ApiParam(name = "personid", type = "String", description = "借用人员ID"),
            @ApiParam(name = "commonCode", type = "String", description = "常用工具类型:最近借的传:recentBorrow,我的收藏传:myFavorite"),
            @ApiParam(name = "pagenumber", type = "Integer", description = "当前页,默认返回第一页"),
            @ApiParam(name = "pagesize", type = "Integer", description = "每页最多显示条数,默认返回10条", optional = true)
    },
            ok = {
                    @ReturnKey(key = "id", description = "常用工具对象ID"),
                    @ReturnKey(key = "number", description = "常用工具数量"),
                    @ReturnKey(key = "insname", description = "工具名称"),
                    @ReturnKey(key = "toolPic", description = "工具图片相对路径"),
                    @ReturnKey(key = "insmodel", description = "工具型号"),
                    @ReturnKey(key = "insparam", description = "工具参数"),
                    @ReturnKey(key = "insnum", description = "工具编码"),
            }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object getCommonTools(@Param("commonCode") String commonCode, @Param("personid") String personid, @Param("pagenumber") Integer pagenumber, @Param("pagesize") Integer pagesize) {
        try {
            return Result.success("system.success", insCommonService.getCommonTools(personid, commonCode, pagenumber, pagesize));
        } catch (Exception e) {
            log.debug(e.getMessage());
            return Result.error(e.getMessage());
        }
    }


    @At("/add2ToolBox")
    @Ok("json:full")
    @Api(name = "添加基本工具至工具箱"
            , params = {
            @ApiParam(name = "personid", type = "String", description = "借用人员ID"),
            @ApiParam(name = "baseInsId", type = "String", description = "基本工具ID"),
    },
            ok = {
                    @ReturnKey(key = "code", description = "code=0操作成功"),
            }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object add2ToolBox(@Param("personid") String personid, @Param("baseInsId") String baseInsId) {
        try {
            insToolcabinetService.add2ToolBox(personid, baseInsId);
            return Result.success("system.success");
        } catch (Exception e) {
            log.debug(e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @At("/add2Favorite")
    @Ok("json:full")
    @Api(name = "添加基本工具至收藏"
            , params = {

            @ApiParam(name = "personid", type = "String", description = "借用人员ID"),
            @ApiParam(name = "commonCode", type = "String", description = "常用工具类型:最近借的:传recentBorrow,我的收藏:传myFavorite"),
            @ApiParam(name = "baseInsIds", type = "String", description = "工具ID数组字符串"),
    },
            ok = {
                    @ReturnKey(key = "code", description = "code=0操作成功"),
            }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object add2Favorite(@Param("personid") String personid, @Param("baseInsIds") String[] baseInsIds, @Param("commonCode") String commonCode) {
        try {
            insCommonService.add2Favorite(personid, baseInsIds, commonCode);
            return Result.success("system.success");
        } catch (Exception e) {
            log.debug(e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @At("/updateOrDeleteToolBoxData")
    @Ok("json:full")
    @Api(name = "修改/删除工具箱列表数据"
            , params = {
            @ApiParam(name = "personid", type = "String", description = "借用人员ID(保留参数,暂可以不传)", optional = true),
            @ApiParam(name = "boxIds", type = "String", description = "工具箱ID,(删除指令传ID数组字符串)"),
            @ApiParam(name = "number", type = "double", optional = true, description = "工具数量,(删除可以不传)"),
            @ApiParam(name = "doOrder", type = "Integer", description = "操作指令 0:删除 1:编辑"),
    },
            ok = {
                    @ReturnKey(key = "code", description = "code=0操作成功"),
            }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object updateOrDeleteToolBoxData(@Param("personid") String personid, @Param("boxIds") String[] boxIds, @Param("number") double number, @Param("doOrder") Integer doOrder) {
        try {
            insToolcabinetService.updateOrDeleteToolBoxData(personid, boxIds, number, doOrder);
            return Result.success("system.success");
        } catch (Exception e) {
            log.debug(e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @At("/deleteFromMyFavorite")
    @Ok("json:full")
    @Api(name = "从我的收藏中删除工具"
            , params = {
            @ApiParam(name = "commonInsId", type = "String", description = "常用工具对象ID"),
    },
            ok = {
                    @ReturnKey(key = "code", description = "code=0 删除成功"),
            }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object deleteFromMyFavorite(@Param("commonInsId") String commonInsId) {
        try {
            insCommonService.deleteFromMyFavorite(commonInsId);
            return Result.success("system.success");
        } catch (Exception e) {
            log.debug(e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @At("/verifyOpenLockBy2Code")
    @Ok("json:full")
    @Api(name = "扫码解锁方法验证"
            , params = {
            @ApiParam(name = "barCode", type = "String", description = "二维码"),
            @ApiParam(name = "insId", type = "String", description = "实体工具ID"),
    },
            ok = {
                    @ReturnKey(key = "result", description = "成功：success,失败：fail"),
            }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object verifyOpenLockBy2Code(@Param("barCode") String barCode, @Param("insId") String insId) {
        try {
            return Result.success("system.success", insInstrumentService.verifyOpenLockBy2Code(barCode, insId));
        } catch (Exception e) {
            log.debug(e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    @At("/insertInsRepair")
    @Ok("json:full")
    @Api(name = "插入维修申报对象(目前维修对象与实体工具一对一)"
            , params = {
//            @ApiParam(name = "insId", type = "String", description = "实体工具ID"),
            @ApiParam(name = "orderId", type = "String", description = "订单ID"),
            @ApiParam(name = "orderentryId", type = "String", description = "订单明细ID"),
            @ApiParam(name = "personId", type = "String", description = "申报人ID(目前默认是下单人ID)"),
            @ApiParam(name = "describ", type = "String", description = "备注",optional = true),
            @ApiParam(name = "repairType", type = "String", description = "维修类型(保留字段)",optional = true),
//            @ApiParam(name = "inTime", type = "String", description = "申请时间"),

    },
            ok = {
                    @ReturnKey(key = "repairId", description = "维修对象ID"),
            }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object insertInsRepair(@Param("orderId") String orderId,@Param("orderentryId")String orderentryId,@Param("describ")String describ,@Param("repairType")String repairType,@Param("personId")String personId) {
        try {
            return Result.success("system.success", insRepairService.insertInsRepair(orderId, orderentryId, describ, repairType, personId));
        } catch (Exception e) {
            log.debug(e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    @At("/uploadRepairImg")
    @Ok("json")
    @Api(name = "维修申报上传图片"
            , params = {
            @ApiParam(name = "filename", type = "String", description = "文件名"),
            @ApiParam(name = "repairId", type = "String", description = "维修对象ID"),
            @ApiParam(name = "base64", type = "String", description = "base64"),
    },
            ok = {
                    @ReturnKey(key = "filePath", description = "图片上传的路径+文件名")
            }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    //AdaptorErrorContext必须是最后一个参数
    public Object uploadRepairImg(@Param("filename") String filename, @Param("base64") String base64, @Param("repairId") String repairId, HttpServletRequest req, AdaptorErrorContext err) {
        try {
            if (err != null && err.getAdaptorErr() != null) {
                return NutMap.NEW().addv("code", 1).addv("msg", "文件不合法");
            } else if (base64 == null) {
                return Result.error("空文件");
            } else {
                insRepairService.uploadRepairImg(filename,base64,repairId);
                return Result.success("上传成功");
            }
        } catch (Exception e) {
            log.debug(e.getMessage());
            return Result.error("system.error",e);
        } catch (Throwable e) {
            log.debug(e.getMessage());
            return Result.error("图片格式错误");
        }
    }

  /*  @At("/borrowOrReturnTest")
    @Ok("json:full")
    @Api(name = "借用、归还临时接口(仅供测试,模拟硬件接口)"
            , params = {
            @ApiParam(name = "type", type = "String", description = "锁指令 1：借用 2：归还"),
            @ApiParam(name = "insId", type = "String", description = "实体工具ID"),
    },
            ok = {
                    @ReturnKey(key = "code", description = "成功 code = 0"),
            }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object borrowOrReturnTest(@Param("type") String type, @Param("insId") String insId) {
        try {
            insInstrumentService.borrowOrReturnTest(type, insId);
            return Result.success("system.success");
        } catch (Exception e) {
            log.debug(e.getMessage());
            return Result.error(e.getMessage());
        }
    }*/
   /* @At("/getinfo")
    @Ok("json:full")
    @Api(name = "硬件调用查询锁的状态接口"
            , params = {
            @ApiParam(name = "type", type = "String", description = "type=0 查询锁的状态"),
            @ApiParam(name = "lockstate", type = "String", description = "开关锁状态"),
            @ApiParam(name = "time", type = "String", description = ""),
    },
            ok = {
                    @ReturnKey(key = "return_code", description = "code=0成功,1失败"),
                    @ReturnKey(key = "return_msg", description = "操作信息"),
                    @ReturnKey(key = "lockState", description = "open开锁 ,close关锁,none不明确操作指令"),
            }
    )
    public Object getinfo(@Param("type") String type, @Param("lockstate") String lockstate, @Param("time") String time) {
        NutMap re = new NutMap();
        try {
            re = insSpaceService.getinfo(type,lockstate,time,re);
        } catch (Exception e) {
            infoLog.debug(e.getMessage());
            re.put("return_msg", "ERROR");
            re.put("return_code", "1");
        }
        JSONObject json = new JSONObject(re);
        infoLog.info("getinfo-->返回数据：" + json);
        return re;
    }*/

   /* @At("/checkOrdersByLockState")
    @Ok("json:full")
    @Api(name = ""
            , params = {
            @ApiParam(name = "orderId", type = "String", description = ""),
    },
            ok = {
                    @ReturnKey(key = "", description = ""),
            }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object checkOrdersByLockState(@Param("orderId")String orderId,@Param("lockstate")String lockstate,){
      try {

      }catch (Exception e){
          return Result.error(e.getMessage());
      }
    }
    */
}


