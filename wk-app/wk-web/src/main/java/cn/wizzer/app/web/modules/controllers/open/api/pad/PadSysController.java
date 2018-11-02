package cn.wizzer.app.web.modules.controllers.open.api.pad;

import cn.wizzer.app.ins.modules.services.*;
import cn.wizzer.app.web.commons.filter.TokenFilter;
import cn.wizzer.app.web.commons.shiro.filter.PlatformAuthenticationFilter;
import cn.wizzer.framework.base.Result;
import org.apache.log4j.Logger;
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

@IocBean
@At("/open/pad/sys")
//@Filters({@By(type = TokenFilter.class)})
@Api(name = "平板系统管理", match = ApiMatchMode.ALL, description = "平板系统管理API")
public class PadSysController {
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

   @At("/doLoginByQRcode")
   @Ok("json:full")
   @Api(name = "平板扫码登陆(手机端APP调用此接口)"
           , params = {
           @ApiParam(name = "cabinetId", type = "String", description = "智能柜ID"),
           @ApiParam(name = "userId", type = "String", description = "用户ID"),
           @ApiParam(name = "key", type = "String", description = "登录凭证 XIANPADLOGIN"),
   },
           ok = {
                   @ReturnKey(key = "code", description = "code=0登录成功"),
           }
   )
   public Object doLoginByQRcode(@Param("cabinetId")String cabinetId,@Param("userId")String userId,@Param("key")String key){
       try {
           insCabinetService.doLoginByQRcode(cabinetId,userId,key);
            return Result.success("system.success");
       }catch (Exception e){
           return Result.error(e.getMessage());
       }
    }

   @At("/logoutByPad")
   @Ok("json:full")
   @Api(name = "平板退出登陆"
           , params = {
           @ApiParam(name = "cabinetId", type = "String", description = "智能柜ID"),
           @ApiParam(name = "key", type = "String", description = "登录凭证 XIANPADLOGIN"),
   },
           ok = {
                   @ReturnKey(key = "code", description = "code=0登录成功"),
           }
   )
   public Object logoutByPad(@Param("cabinetId")String cabinetId,@Param("key")String key){
       try {
           insCabinetService.logoutByPad(cabinetId,key);
            return Result.success("system.success");
       }catch (Exception e){
           return Result.error(e.getMessage());
       }
    }

   @At("/listenLogin")
   @Ok("json:full")
   @Api(name = "监听是否已经登录成功"
           , params = {
           @ApiParam(name = "cabinetId", type = "String", description = "智能柜ID"),
   },
           ok = {
                   @ReturnKey(key = "status", description = "0未登录 1登录成功"),
                   @ReturnKey(key = "userId", description = "用户ID"),
                   @ReturnKey(key = "personId", description = "人员ID"),
           }
   )
   public Object listenLogin(@Param("cabinetId")String cabinetId){
       try {
            return Result.success("system.success",insCabinetService.listenLogin(cabinetId));
       }catch (Exception e){
           return Result.error(e.getMessage());
       }
    }
}


