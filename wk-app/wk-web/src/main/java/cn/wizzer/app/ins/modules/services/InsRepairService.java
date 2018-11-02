package cn.wizzer.app.ins.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.ins.modules.models.Ins_repair;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import org.nutz.dao.Cnd;
import org.nutz.lang.util.NutMap;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

public interface InsRepairService extends BaseService<Ins_repair>{
    /**
     *插入维修申报对象
     */
    Map<String,Object> insertInsRepair(String orderId, String orderentryId, String describ, String repairType, String personId);

    void uploadRepairImg(String filename, String base64, String repairId) throws FileNotFoundException, Exception;

    NutMap getRepairList(String completeValue, int length, int start, int draw, List<DataTableOrder> order, List<DataTableColumn> columns, Cnd aNew);

    void completeRepair(String[] ids, String personId);
}
