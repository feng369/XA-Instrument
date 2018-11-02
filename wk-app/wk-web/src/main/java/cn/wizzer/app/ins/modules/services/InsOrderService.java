package cn.wizzer.app.ins.modules.services;

import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.ins.modules.models.Ins_order;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface InsOrderService extends BaseService<Ins_order>{

    List<Map<String,Object>> getOrderListByMoibile(String operator, Integer pagenumber, Integer pagesize, String orderPstatus);

    Result addOrderAndOrderEntryByMobile(String[] boxIds, String operator, String operatetime, String flightnum, String seatnum, String describ, String airportid) throws ParseException;

    Map<String,Object> getOrderCount(String airportId);

    Map<String,Object> getOrderInfo(String orderId);

    Ins_order transmitAndNewOrder(String personId,String airportId) throws ParseException;

    List<Map<String,Object>> getOrderAndEntryList(String operator, Integer pagenumber, Integer pagesize, String orderPstatus);
    List<Map<String,Object>> getAllOrderAndEntry(String personId, Integer pagenumber, Integer pagesize, String orderPstatus, String cabinetId);

    List<Map<String,Object>> getOrderDetail(String orderId);
    String getOrderNum(String operatetime, String airportid) throws ParseException;

    void prepareTool(String id);

    void prepareTools(String[] ids);
}
