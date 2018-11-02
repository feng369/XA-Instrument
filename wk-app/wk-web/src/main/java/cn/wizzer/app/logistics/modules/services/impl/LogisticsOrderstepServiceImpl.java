package cn.wizzer.app.logistics.modules.services.impl;

import cn.wizzer.app.logistics.modules.models.logistics_order;
import cn.wizzer.app.logistics.modules.services.LogisticsOrderService;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.logistics.modules.models.logistics_orderstep;
import cn.wizzer.app.logistics.modules.services.LogisticsOrderstepService;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IocBean(args = {"refer:dao"})
public class LogisticsOrderstepServiceImpl extends BaseServiceImpl<logistics_orderstep> implements LogisticsOrderstepService {
    public LogisticsOrderstepServiceImpl(Dao dao) {
        super(dao);
    }
    @Inject
    private LogisticsOrderService logisticsOrderService;

    public Map<String,Object> getStepByMobile(String orderid, String stepnum) {
        Map<String,Object> map = new HashMap<>();
        logistics_order order = logisticsOrderService.fetch(orderid);
        if (order != null) {
            logistics_orderstep logisticsOrderstep = this.fetch(Cnd.where("otype", "=", order.getOtype()).and("stepnum", "=", stepnum));
            if(logisticsOrderstep!= null){
                map.put("step",logisticsOrderstep.getStep());
            }
        }
        return map;
    }
    //航线运输
    public List<Map<String, Object>> getStepAndStepNameByOtype() {
        List<logistics_orderstep> ordersteps = this.query(Cnd.where("otype", "=", "72dd55d2c3cc42799c4e014745db2cdb").orderBy("step","ASC"));
        List<Map<String,Object>> list = new ArrayList<>();
        for (logistics_orderstep orderstep : ordersteps) {
            Map<String,Object> map = new HashMap<>();
            map.put("step",orderstep.getStep());
            map.put("stepname",orderstep.getStepname());
            list.add(map);
        }
        return list;
    }

    public String getStepByStepnum(String stepnum,String otype){
        try{
            Cnd cnd=Cnd.NEW();
            cnd.and("stepnum","=",stepnum);
            cnd.and("otype","=",otype);
            logistics_orderstep orderstep = this.fetch(cnd);
            if(orderstep!=null){
                return orderstep.getStep().toString();
            }
            return "";
        }catch(Exception e){
            return "";
        }
    }
}
