package cn.wizzer.app.logistics.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.logistics.modules.models.logistics_orderstep;

import java.util.List;
import java.util.Map;

public interface LogisticsOrderstepService extends BaseService<logistics_orderstep>{

    Map<String,Object> getStepByMobile(String orderid, String stepnum);

    List<Map<String,Object>> getStepAndStepNameByOtype();

    String getStepByStepnum(String stepnum,String otype);

}
