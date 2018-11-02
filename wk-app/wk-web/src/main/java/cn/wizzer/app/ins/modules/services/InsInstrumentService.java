package cn.wizzer.app.ins.modules.services;

import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.ins.modules.models.Ins_instrument;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import org.nutz.dao.Cnd;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.upload.TempFile;

import java.util.List;
import java.util.Map;

public interface InsInstrumentService extends BaseService<Ins_instrument>{
    /**
     * 绑定仓位
     * @param insIds
     * @param spaceid
     */
    void bindSpace(String[] insIds, String spaceid);

    /**
     * 绑定RFID
     * @param rfID
     */
    void updateRFID(String insId, String rfID);

    Map<String,Object> uploadIns(TempFile tf, String insId);


    Map<String,Object> verifyOpenLockBy2Code(String barCode, String insId);


    void borrowOrReturnTest(String type, String insId);

    Result onLineIns(String[] insIds);

    Result offLineIns(String[] insIds);

//    List<Map<String,Object>> getInstrumentList(String childDictId, Integer pagenumber, Integer pagesize);

    /**
     * 统计库存
     */
    Map<String,Integer> getCountMap(List<Ins_instrument> insInstruments);

    void checkIns(String cabinetId, String[] rfIDs);


    /**
     *从智能柜拿出订单上对应设备后,检查工具的库存
     */
//    Result checkOrders(List<String> rfidNumber, String orderId, Integer doOrder);

    Result doCheckOrders(String[] rfIDs, String spaceNum, String orderId, String cabinetType);

    Result unbindRFIDs(String[] insIds);
    NutMap getInsList(int length, int start, int draw, List<DataTableOrder> order, List<DataTableColumn> columns, Cnd cnd, String insnum, String insname, String number, String pstatus, String linePstatus, String cabinetId, String spaceId);

}
