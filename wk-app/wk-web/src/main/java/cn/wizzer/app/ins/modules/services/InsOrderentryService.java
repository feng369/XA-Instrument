package cn.wizzer.app.ins.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.ins.modules.models.Ins_orderentry;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface InsOrderentryService extends BaseService<Ins_orderentry>{

    String getOrderEntryListByMoibile(String orderid, Integer pagenumber, Integer pagesize);


    List<Map<String,Object>> getAllBorrowingOrderEntries(String personid, Integer pagenumber, Integer pagesize);

    void transmitOrderEntry(String newPersonId, String dateflag) throws ParseException;

    Map<String,Object>  clickDeliver2Code(String personid, String[] orderEntryIds);

    Map<String,Object> listenTransmit(String dateflag);

    void addOrderEntryByMobile(String orderId, Map<String, Integer> countMap, String operator);

    /**
     * 从智能柜中拿出原订单中不存在的工具
     * @param orderId
     * @param baseInsId
     * @param insId
     * @param operator
     */
    void addNewOrderEntry(String orderId, String baseInsId, String insId, String operator);
}
