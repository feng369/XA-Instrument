package cn.wizzer.app.web.modules.controllers.open.api.pad;

import cn.wizzer.app.ins.modules.services.*;
import cn.wizzer.app.web.commons.filter.TokenFilter;
import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.util.StringUtil;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.nutz.dao.Dao;
import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.filter.CrossOriginFilter;
import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.apidoc.annotation.ApiMatchMode;
import org.nutz.plugins.apidoc.annotation.ApiParam;
import org.nutz.plugins.apidoc.annotation.ReturnKey;

import java.util.ArrayList;
import java.util.List;

@IocBean
@At("/open/pad/ins")
//@Filters({@By(type = TokenFilter.class)})
@Api(name = "平板工具管理", match = ApiMatchMode.ALL, description = "平板工具管理相关api接口")
public class PadInsController {
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
    private InsLockService insLockService;

    @At("/getAllCabinetInfo")
    @Ok("json:full")
    @Api(name = "初始化前得到系统参数、串口相关信息以及所有智能柜编号、初始化状态等信息"
            , params = {},
            ok = {
                    @ReturnKey(key = "BaudRate", description = "波特率"),
                    @ReturnKey(key = "CheckBit", description = "校验位"),
                    @ReturnKey(key = "DataBit", description = "数据位"),
                    @ReturnKey(key = "StopBit", description = "停止位"),
                    @ReturnKey(key = "cabinetList", description = "柜子信息集合"),
                    @ReturnKey(key = "cabinetId", description = "柜子ID"),
                    @ReturnKey(key = "cabinetNum", description = "柜子编码"),
                    @ReturnKey(key = "cabinetName", description = "柜子名称"),
                    @ReturnKey(key = "initStatus", description = "柜子初始化状态 0未初始化 1已初始化"),
                    @ReturnKey(key = "initStatusName", description = "柜子初始化状态名称 0未初始化 1已初始化"),
                    @ReturnKey(key = "useStatus", description = "柜子使用状态 0空闲 1使用中"),
                    @ReturnKey(key = "useStatusName", description = "柜子使用状态名称 0空闲 1使用中"),
                    @ReturnKey(key = "serialPort", description = "智能柜串口号"),
                    @ReturnKey(key = "cabinetType", description = "智能柜类型 YDcabinet(移动智能柜) GDcabinet(固定柜)"),
            }
    )
    @Filters({@By(type = CrossOriginFilter.class)})
    public Object getAllCabinetInfo() {
        try {
            return Result.success("system.success", insCabinetService.getAllCabinetInfo());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @At("/initCabinet")
    @Ok("json:full")
    @Api(name = "智能柜初始化"
            , params = {
            @ApiParam(name = "cabinetId", type = "String", description = "智能柜ID")
    },
            ok = {
                    @ReturnKey(key = "code", description = "code=0 初始化成功"),
            }
    )
    @Filters({@By(type = CrossOriginFilter.class)})
    public Object initCabinet(@Param("cabinetId") String cabinetId) {
        try {
            insCabinetService.initCabinet(cabinetId);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /* @At("/getInfo")
     @Ok("json:full")
     @Api(name = "硬件调用查询锁的状态接口"
             , params = {
             @ApiParam(name = "type", type = "String", description = "type=0 查询锁的状态"),
 //            @ApiParam(name = "lockstate", type = "String", description = "开关锁状态"),
             @ApiParam(name = "lockNum", type = "String", description = "开关锁状态"),
 //            @ApiParam(name = "time", type = "String", description = ""),
     },
             ok = {
                     @ReturnKey(key = "return_code", description = "code=0成功,1失败"),
                     @ReturnKey(key = "return_msg", description = "操作信息"),
                     @ReturnKey(key = "lockState", description = "open开锁 ,close关锁,none不明确操作指令"),
             }
     )
     public Object getInfo(@Param("type") String type, @Param("lockstate") String lockstate, @Param("time") String time) {
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
    @At("/updateLockStatus")
    @Ok("json:full")
    @Api(name = "修改仓位、锁开门（开锁成功）、关门（手动关门）状态"
            , params =
            {
//            @ApiParam(name = "lockNum", type = "String", description = "锁编号"),
                    @ApiParam(name = "spaceNum", type = "String", description = "仓位编号"),
                    @ApiParam(name = "doOrder", type = "Integer", description = "0关门 1开门")
            },
            ok = {
                    @ReturnKey(key = "code", description = "code=0 操作成功"),
            }
    )
    @Filters({@By(type = CrossOriginFilter.class)})
    public Object updateLockStatus(@Param("spaceNum") String spaceNum, @Param("doOrder") Integer doOrder) {
        try {
            insLockService.updateLockStatus(spaceNum, doOrder);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error",e);
        }
    }

    @At("/checkIns")
    @Ok("json:full")
    @Api(name = "(保留接口)轮询柜子中的工具是否和数据库匹配"
            , params = {
            @ApiParam(name = "cabinetId", type = "String", description = "柜子ID"),
            @ApiParam(name = "rfIDs", type = "String", description = "柜子中扫描得到的RFID(标签号)数组字符串"),
    },
            ok = {
                    @ReturnKey(key = "code", description = "code=0 操作成功"),
            }
    )
    @Filters({@By(type = CrossOriginFilter.class)})
    public Object checkIns(@Param("cabinetId") String cabinetId, @Param("rfIDs") String[] rfIDs) {
        try {
            insInstrumentService.checkIns(cabinetId, rfIDs);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    public static void main(String[] args) {
       /*
        List<String> rfidNumber  = new ArrayList<>();
        rfidNumber.add("E28011700000020A2B77F4FD");
        rfidNumber.add("E28011700000020A2B77F42D");
        String str ="  SELECT ib.id as baseInsId,ib.insname as baseInsName,ib.insnum as baseInsNum,ins.id as insId FROM ins_instrument ins " +
                " JOIN  ins_rfid rf ON ins.rfid = rf.id \n" +
                "  JOIN ins_baseins ib ON ins.baseInsId = ib.id\n" +
//                    " LEFT JOIN ins_orderentry od ON ib.id = od.baseInsId " +
                " WHERE rf.number IN (" + StringUtil.list2SpecialStr(rfidNumber) + ") ";
        System.out.println(str);
        */
    }
}


