package cn.wizzer.app.ins.modules.services.impl;

import cn.wizzer.app.base.modules.models.base_cnctobj;
import cn.wizzer.app.base.modules.services.BaseCnctobjService;
import cn.wizzer.app.base.modules.services.BasePersonService;
import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.app.sys.modules.services.SysDictService;
import cn.wizzer.app.sys.modules.services.SysUserService;
import cn.wizzer.app.web.commons.base.Globals;
import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.base.ValidatException;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.ins.modules.models.Ins_cabinet;
import cn.wizzer.app.ins.modules.services.InsCabinetService;
import cn.wizzer.framework.util.StringUtil;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IocBean(args = {"refer:dao"})
public class InsCabinetServiceImpl extends BaseServiceImpl<Ins_cabinet> implements InsCabinetService {
    public InsCabinetServiceImpl(Dao dao) {
        super(dao);
    }
    @Inject
    private SysUserService sysUserService;
    @Inject
    private BaseCnctobjService baseCnctobjService;
    @Inject
    private BasePersonService basePersonService;
    @Inject
    private SysDictService sysDictService;
    public List<Map<String, Object>> getCabinetList(Integer pagenumber, Integer pagesize) {
        List<Ins_cabinet> cabinetList = this.query(Cnd.NEW(), StringUtil.getPagerObject(pagenumber,pagesize));
        List<Map<String, Object>> list = new ArrayList<>();
        if(cabinetList.size() > 0 ){
            for (Ins_cabinet insCabinet : cabinetList) {
                Map<String, Object> map = new HashMap<>();
                map.put("id",insCabinet.getId());
                map.put("cabinetname",insCabinet.getCabinetname());
                map.put("cabinetnum",insCabinet.getCabinetnum());
                map.put("address",insCabinet.getAddress());
                map.put("pstatus",insCabinet.getPstatus());
                map.put("pstatusname",insCabinet.getPstatusname());
                map.put("airportid",insCabinet.getAirportid());
                list.add(map);
            }
        }
        return list;
    }

    public Map<String,Object> getAllCabinetInfo() {
        List<Ins_cabinet> cabinetList = this.query();
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> infoMap = new HashMap<>();
        infoMap.put("BaudRate", Globals.BaudRate);
        infoMap.put("CheckBit", Globals.CheckBit);
        infoMap.put("DataBit", Globals.DataBit);
        infoMap.put("StopBit", Globals.StopBit);
        if(cabinetList.size() > 0){
            for (Ins_cabinet cabinet : cabinetList) {
                Map<String, Object> map = new HashMap<>();
                map.put("cabinetId",StringUtil.null2TrimStr(cabinet.getId()));
                map.put("cabinetNum",StringUtil.null2TrimStr(cabinet.getCabinetnum()));
                map.put("cabinetName",StringUtil.null2TrimStr(cabinet.getCabinetname()));
                map.put("initStatus",cabinet.getInitStatus());
                map.put("initStatusName",cabinet.getInitStatusName());
                map.put("useStatus",cabinet.getUseStatus());
                map.put("useStatusName",cabinet.getUseStatusName());
                map.put("serialPort",cabinet.getSerialPort());
                List<Sys_dict> sysDictList = sysDictService.query(Cnd.where("id", "=", cabinet.getCabinetType()));
                if (sysDictList.size() > 0){
                    Sys_dict dict = sysDictList.get(0);
                    map.put("cabinetType",dict.getCode());
                }
                list.add(map);
            }
        }
        infoMap.put("cabinetList",list);
        return infoMap;
    }

    public void initCabinet(String cabinetId) {
        if(Strings.isBlank(cabinetId)){
            throw new ValidatException("智能柜ID为空");
        }
        List<Ins_cabinet> cabinets = this.query(Cnd.where("id", "=", cabinetId));
        if(cabinets.size() != 1){
            throw new ValidatException("未找到唯一的智能柜");
        }
        this.update(Chain.make("initStatus",1),Cnd.where("id","=",cabinetId));
    }

    @Aop(TransAop.READ_COMMITTED)
    public void doLoginByQRcode(String cabinetId, String userId, String key) {
        if(Strings.isBlank(cabinetId)){
            throw  new ValidatException("传入的智能柜ID为空");
        }
        if(Strings.isBlank(userId)){
            throw  new ValidatException("传入的用户ID为空");
        }
        if(Strings.isBlank(key)){
            throw  new ValidatException("传入的登录凭证键值为空");
        }
        if(!"XIANPADLOGIN".equals(key)){
            throw new ValidatException("登录凭证key不正确");
        }
        List<Ins_cabinet> cabinets = this.query(Cnd.where("id", "=", cabinetId));
        if(cabinets.size() != 1){
             throw new ValidatException("不能确定唯一的智能柜");
        }
        Ins_cabinet cabinet = cabinets.get(0);
        if(!Strings.isBlank(cabinet.getUserId())){
            throw new ValidatException("该智能柜正在使用中,请先退出系统");
        }
        this.update(Chain.make("userId",userId).add("useStatus",1),Cnd.where("id","=",cabinetId));
    }

    @Aop(TransAop.READ_COMMITTED)
    public Map<String, Object> listenLogin(String cabinetId) {
        if(Strings.isBlank(cabinetId)){
            throw new ValidatException("智能柜ID为空");
        }
        List<Ins_cabinet> cabinets = this.query(Cnd.where("id", "=", cabinetId));
        if(cabinets.size() != 1){
            throw new ValidatException("不能确定唯一的智能柜");
        }
        Map<String, Object> map = new HashMap<>();
        Ins_cabinet cabinet = cabinets.get(0);

        if(Strings.isBlank(cabinet.getUserId())){
            map.put("status",0);
        }else{
            map.put("status",1);
        }
        String userId = cabinet.getUserId();
        if(!Strings.isBlank(userId)){
            base_cnctobj baseCnctobj = baseCnctobjService.fetch(Cnd.where("userId", "=", cabinet.getUserId()));
            if(baseCnctobj != null){
                map.put("personId",baseCnctobj.getPersonId());
            }else{
                map.put("personId","");
            }
        }
        map.put("userId",StringUtil.null2TrimStr(cabinet.getUserId()));
        return map;
    }

    @Aop(TransAop.READ_COMMITTED)
    public void logoutByPad(String cabinetId, String key) {
        if(Strings.isBlank(cabinetId)){
            throw new ValidatException("仓位ID为空");
        }
        if(!"XIANPADLOGIN".equals(key)){
            throw new ValidatException("登录凭证不正确");
        }
        List<Ins_cabinet> cabinets = this.query(Cnd.where("id", "=", cabinetId));
        if(cabinets.size() != 1){
             throw new ValidatException("不能确定唯一的仓位");
        }
        if(Strings.isBlank(cabinets.get(0).getUserId())){
            throw new ValidatException("该账号已退出登录,请勿重复操作!");
        }
        this.update(Chain.make("userId",null).add("useStatus",0),Cnd.where("id","=",cabinetId));
    }

}
