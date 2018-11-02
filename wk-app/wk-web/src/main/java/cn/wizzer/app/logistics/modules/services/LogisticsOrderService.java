package cn.wizzer.app.logistics.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.logistics.modules.models.logistics_order;
import org.nutz.dao.Cnd;
import org.nutz.lang.util.NutMap;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface LogisticsOrderService extends BaseService<logistics_order>{
    void save(logistics_order logistics_order, String logisticsOrder);

    logistics_order getLogistics_order(String id);

    List<logistics_order>  getdata(Cnd cnd,String linkname);

    NutMap getJsonDataOfCountOrder(String otype,String airportId);

    Map getOrderCountByMobile(String personid,boolean receivedCheck);

    List<Map<String,Object>> getOrderList(String personid, String pstatus, String numorder, String startstock, String endstock, String btype, String deliveryorderid, Integer pagenumber, Integer pagesize);

    void updataVehicle(String vehicleid, String orderid);

    Map updateDvSteps(String orderid, String content, String stepnum, String toreason, String personid);

    void addelDo(String orderid, String personid,String curPosition);

    Map<String,Object> getOrderInfo(String id);

    void updateOrderReject(String id);

    Map getOrderPstatus(String id);

    List<Map<String,Object>> getOrderListOfCustomer(String personid);

    Map getOrderInfoOfCustomer(String orderid);

    List<Map<String,Object>> getOrderListByPersonId(String personid, Integer pagenumber, Integer pagesize);

    void addOrderAndOrderEntry(String logisticsOrder, String logisticsOrderentry) throws ParseException;

    Map<String,Object> addOrderByMobile(String btype, String emergency, String hctype, String startstock, String endstock, String customerid, String personid, String userid) throws ParseException;

    Map<String,Object> addOrderEntryByMobiile(String materielid, String materielname, String materielnum, String sequencenum, String batch, String quantity,String pnr,String sn,String stock,String location,String orderflag,String lorderno,String state);

    void receiveOrderBy2Code(String personId, String orderId);

    Map<String, Object> ListenOrderid(String orderid);

    Map<String, Object> checkIsOver(String orderid);
}
