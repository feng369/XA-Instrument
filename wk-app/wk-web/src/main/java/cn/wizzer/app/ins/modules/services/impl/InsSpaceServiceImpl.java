package cn.wizzer.app.ins.modules.services.impl;

import cn.wizzer.app.ins.modules.services.InsLockService;
import cn.wizzer.framework.base.ValidatException;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.ins.modules.models.Ins_space;
import cn.wizzer.app.ins.modules.services.InsSpaceService;
import cn.wizzer.framework.util.StringUtil;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IocBean(args = {"refer:dao"})
public class InsSpaceServiceImpl extends BaseServiceImpl<Ins_space> implements InsSpaceService {
    private static Logger infoLog = Logger.getLogger("InfoLog");
    public InsSpaceServiceImpl(Dao dao) {
        super(dao);
    }
    @Inject
    private InsLockService lockService;
    public List<Map<String, Object>> getSpaceList(Integer pagenumber, Integer pagesize) {
        List<Ins_space> insSpaces = this.query(Cnd.NEW(), StringUtil.getPagerObject(pagenumber,pagesize));
        List<Map<String, Object>> list = new ArrayList<>();
        if(insSpaces.size() > 0){
            for (Ins_space insSpace : insSpaces) {
                Map<String,Object> map = new HashMap<>();
                map.put("id",insSpace.getId());
                map.put("spacename",insSpace.getSpacename());
                map.put("spacenum",insSpace.getSpacenum());
                map.put("params",insSpace.getParams());
                map.put("model",insSpace.getModel());
                map.put("address",insSpace.getModel());
                map.put("barcode",insSpace.getBarcode());
                map.put("pstatus",insSpace.getPstatus());
                map.put("pstatusname",insSpace.getPstatusname());
                list.add(map);
            }
        }
        return list;
    }
    public NutMap getinfo(String type, String lockstate, String time, NutMap re) {
        Map map = new HashMap();
        map.put("type", type);
        map.put("lockstate", lockstate);
        map.put("time", time);
        JSONObject json = new JSONObject(map);
        infoLog.info("getinfo-->收到的数据格式：" + json);
        int ret = 0;
        if (!Strings.isBlank(type)) {
            if("0".equals(type) && !Strings.isBlank(lockstate)){
                if("0".equals(lockstate)){
                    re.put("lockState","open");
                }else if("1".equals(lockstate)){
                    re.put("lockState","close");
                }else{
                    re.put("lockState","none");
                }
            }
            re.put("return_msg", "SUCCESS");
            re.put("return_code", "0");
        }else{
            re.put("return_msg", "ERROR");
            re.put("return_code", "1");
        }
        return re;
    }


    @Aop(TransAop.READ_COMMITTED)
    public void updateSpaceStatus(Ins_space space, Integer doOrder){
        /*List<Ins_space> spaces = this.query(Cnd.where("lockId", "=", lockId));
        if(spaces.size() != 1 ){
            throw new ValidatException("不能确定唯一的仓位");
        }*/
        /*if(doOrder == space.getPstatus()){
            throw new ValidatException(doOrder == 0 ? "柜门已关闭!请勿重复此操作":"柜门已打开!请勿重复此操作");
        }*/
        this.update(Chain.make("pstatus",doOrder),Cnd.where("id","=",space.getId()));
    }

    @Aop(TransAop.READ_COMMITTED)
    public void updateLock(String spaceId, String lockId) {
        if (Strings.isBlank(spaceId) || Strings.isBlank(spaceId)) {
            throw new ValidatException("传入参数不正确");
        }
        this.update(Chain.make("lockId", lockId), Cnd.where("id", "=", spaceId));
        //2.改变lock状态为使用中
        lockService.update(Chain.make("bindStatus", 1), Cnd.where("id", "=", lockId));
    }

    @Aop(TransAop.READ_COMMITTED)
    public void updateLockStatus(String cabinetId,String spaceId, String pstatus){
        if (Strings.isBlank(cabinetId) || Strings.isBlank(pstatus)) {
            throw new ValidatException("传入参数不正确");
        }
        this.update(Chain.make("pstatus",pstatus),Cnd.where("cabinetid","=",cabinetId));
    }
}
