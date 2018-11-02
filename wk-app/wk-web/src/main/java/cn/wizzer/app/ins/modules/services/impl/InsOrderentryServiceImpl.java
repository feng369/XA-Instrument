package cn.wizzer.app.ins.modules.services.impl;

import cn.wizzer.app.base.modules.models.base_person;
import cn.wizzer.app.base.modules.services.BasePersonService;
import cn.wizzer.app.ins.modules.models.*;
import cn.wizzer.app.ins.modules.services.*;
import cn.wizzer.app.web.modules.controllers.open.api.Datatime.newDataTime;
import cn.wizzer.framework.base.ValidatException;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.framework.util.StringUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

import java.text.ParseException;
import java.util.*;

@IocBean(args = {"refer:dao"})
public class InsOrderentryServiceImpl extends BaseServiceImpl<Ins_orderentry> implements InsOrderentryService {
    public InsOrderentryServiceImpl(Dao dao) {
        super(dao);
    }

    @Inject
    private InsInstrumentService insInstrumentService;
    @Inject
    private InsBaseinsService insBaseinsService;
    @Inject
    private InsToolcabinetService insToolcabinetService;
    @Inject
    private InsOrderService insOrderService;
    @Inject
    private InsTransmitService insTransmitService;
    @Inject
    private BasePersonService basePersonService;
    @Inject
    private InsCommonService insCommonService;
    @Inject
    private InsBaseinsSpaceService insBaseinsSpaceService;
    @Inject
    private InsSpaceService insSpaceService;
    @Inject
    private InsCabinetService insCabinetService;

    public String getOrderEntryListByMoibile(String orderid, Integer pagenumber, Integer pagesize) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sqlStr = "SELECT " +
                "od.id," +
                " icp.cabinetname, "+
                "bp.personname," +
                "ib.insmodel,ib.insname, ib.insparam,ib.toolPic," +
                "su.name," +
                "ins.id as insId,ins.describ,od.pstatus,rf.number,sp.spacename,ins.insnum \n" +
                " FROM ins_orderentry od \n" +
                " LEFT JOIN base_person bp ON od.operator = bp.id \n" +
                " LEFT JOIN ins_instrument ins ON od.insId = ins.id \n" +
                " LEFT JOIN ins_baseins ib ON ins.baseInsId = ib.id \n" +
                " LEFT JOIN ins_space sp ON ins.insspaceid = sp.id  \n" +
                " LEFT JOIN ins_cabinet icp ON sp.cabinetid = icp.id \n" +
                " LEFT JOIN ins_rfid rf ON ins.rfid = rf.id  \n" +
                 " LEFT JOIN sys_unit su ON ib.insunit = su.id\n" +
                " WHERE od.orderid =  '" + orderid + "' " +
                " ORDER BY od.pstatus ASC ";
        sqlStr = StringUtil.appendSqlStrByPager(pagenumber, pagesize, sqlStr);
        Sql sql = Sqls.queryRecord(sqlStr);
        dao().execute(sql);
        List<Record> sqlList = sql.getList(Record.class);
        if (sqlList.size() > 0) {
            for (Record record : sqlList) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", record.getString("id"));
                map.put("insId",record.getString("insId"));
                map.put("cabinetname",record.getString("cabinetname"));
                map.put("personname", record.getString("personname"));
                map.put("insname",record.getString("insname"));
                map.put("insnum",record.getString("insnum"));
                map.put("insmodel", record.getString("insmodel"));
                map.put("insparam", record.getString("insparam"));
                map.put("toolPic", record.getString("toolPic"));
                map.put("insUnitName", record.getString("name"));
                map.put("describ", record.getString("describ"));
                map.put("pstatus", record.getInt("pstatus"));
                map.put("pstatusname", getInsStatusName(record.getInt("pstatus")));//待商榷
                map.put("rfidNumber", record.getString("number"));
                map.put("spacename", record.getString("spacename"));
                list.add(map);
            }
        }
        return JSONObject.toJSONString(list,SerializerFeature.WriteNullStringAsEmpty);
    }

    @Aop(TransAop.READ_COMMITTED)
    public List<Map<String,Object>> getAllBorrowingOrderEntries(String personid,Integer pagenumber, Integer pagesize){
        if(Strings.isBlank(personid)){
            throw new ValidatException("传入人员ID不能为空");
        }
        String sqlStr = " SELECT \n" +
                " io.ordernum,io.id as orderId, "+
                "\tod.id,od.insId,od.pstatus as entryPstatus,\n" +
                "\tins.describ,ins.pstatus as insPstatus,\n" +
//                "ins.insnum,"+
                "  ib.id as baseInsId,ib.insname,ib.insparam,ib.insmodel,ib.toolPic," +
                "ib.insnum,\n" +
                "\t isp.spacename,\n" +
                "\t it.cabinetname \n" +
                " FROM ins_orderentry od  \n" +
                " JOIN ins_order io ON io.id = od.orderid \n" +
//                " LEFT JOIN ins_instrument ins ON od.insId = ins.id \n" +
                " LEFT JOIN ins_instrument ins ON od.insId = ins.id \n" +
                " LEFT JOIN ins_baseins ib ON od.baseInsId = ib.id\t \n" +
//                " LEFT JOIN ins_space isp ON ins.insspaceid = isp.id \t\n" +
                " LEFT JOIN ins_space isp ON ib.spaceId = isp.id \t\n" +
                " LEFT JOIN ins_cabinet  it ON it.id = isp.cabinetid \t\n" +
                " LEFT JOIN ins_rfid irf ON ins.rfid = irf.id\n" +
                //AND ins.pstatus = '2'
                " \tWHERE io.operator = '" + personid +"'\n" +
                " AND ( od.pstatus = '2' ) ";
//                " AND od.pstatus = '2'  ";//订单明细状态
        sqlStr = StringUtil.appendSqlStrByPager(pagenumber,pagesize,sqlStr);
        Sql sql = Sqls.queryRecord(sqlStr);
        dao().execute(sql);
        List<Record> recordList = sql.getList(Record.class);
        List<Map<String,Object>> list = new ArrayList<>();
        //List<String> odIds = new ArrayList<>();
        if(recordList.size() > 0) {
            for (Record record : recordList) {
                String id = record.getString("id");
                String orderId = record.getString("orderId");
                String insId = record.getString("insId");
                String describ = record.getString("describ");
                String insnum = record.getString("insnum");
                String insname = record.getString("insname");
                String insparam = record.getString("insparam");
                String insmodel = record.getString("insmodel");
                String toolPic = record.getString("toolPic");
//                String spacename = record.getString("spacename");
                String ordernum = record.getString("ordernum");
                String baseInsId = record.getString("baseInsId");
//                String cabinetName = record.getString("cabinetname");
                int entryPstatus = record.getInt("entryPstatus");
                int insPstatus = record.getInt("insPstatus");
                Map<String,Object> map = new HashMap<>();
                map.put("id",id);
                map.put("orderId",orderId);
                map.put("insId",insId);
                map.put("describ",describ);
                map.put("insnum",insnum);
                map.put("insname",insname);
                map.put("insparam",insparam);
                map.put("insmodel",insmodel);
                map.put("toolPic",toolPic);
//                map.put("spacename",spacename);
                map.put("ordernum",ordernum);
//                map.put("cabinetName",cabinetName);
                map.put("entryPstatus",entryPstatus);
                map.put("entryPstatusName",getEntryPstatusName(entryPstatus));
                map.put("insPstatus",insPstatus);
                map.put("insPstatusName",getInsStatusName(insPstatus));
                List<Ins_baseins_space> insBaseinsSpaces = insBaseinsSpaceService.query(Cnd.where("baseInsId", "=",baseInsId ));
                //如果工具不在此柜中,则也不能打开
                Map<String,List<String>> spacesStrMap = new HashMap<>();
                if(insBaseinsSpaces.size() > 0) {
                    for (Ins_baseins_space insBaseinsSpace : insBaseinsSpaces) {
                        if(!Strings.isBlank(insBaseinsSpace.getSpaceId())){
                            String spaceId = insBaseinsSpace.getSpaceId();
                            Ins_space insSpace = insSpaceService.fetch(spaceId);
                            if(insSpace != null){
                                Ins_cabinet insCabinet = insCabinetService.fetch(insSpace.getCabinetid());
                                if(insCabinet != null){
                                    if(spacesStrMap.get(insCabinet.getCabinetname()) == null){
                                        List<String> spaceNames = new ArrayList<>();
                                        spaceNames.add(insSpace.getSpacename());
                                        spacesStrMap.put(insCabinet.getCabinetname(),spaceNames);
                                    }else{
                                        List<String> spaceNames = spacesStrMap.get(insCabinet.getCabinetname());
                                        spaceNames.add(insSpace.getSpacename());
                                        spacesStrMap.put(insCabinet.getCabinetname(),spaceNames);
                                    }
                                }
                            }
                        }
                    }
                }
                if(spacesStrMap.size() > 0){
                    StringBuilder sb = new StringBuilder(80);
                    for(Map.Entry<String, List<String>> entry : spacesStrMap.entrySet()){
                        String cabinetName = entry.getKey();
                        List<String> spaceNames = entry.getValue();
                        sb.append(cabinetName).append(":");
                        for (String spaceName : spaceNames) {
                            sb.append(spaceName).append(" ");
                        }
                    }
                    map.put("storeAddress",sb.toString());
                }else{
                    map.put("storeAddress","");
                }
                list.add(map);
            }
        }
        //this.update(Chain.make("dateflag",String.valueOf(System.currentTimeMillis()).substring(0, 10)),Cnd.where("id","IN",odIds));
        return list;
    }

    private String getEntryPstatusName(int entryPstatus) {
        switch (entryPstatus){
            case 0:
                return "未借用";
            case 1:
                return "待借用";
            case 2:
                return "借用中";
            case 3:
                return "已归还";
            default:
                return "";
        }
    }


    private String getInsStatusName(int pstatus) {
        switch (pstatus) {
            case 0:
                return "未借用";
            case 1:
                return "待借用";
            case 2:
                return "借用中";
            case 3:
                return "已归还";
            default:
                return "";
        }
    }

    @Aop(TransAop.READ_COMMITTED)
    public  Map<String,Object>  clickDeliver2Code(String personid, String[] orderEntryIds) {
        if(Strings.isBlank(personid)){
            throw new ValidatException("人员ID不能为空");
        }
        if(orderEntryIds.length == 0){
            throw new ValidatException("无法获取订单明细数组字符串");
        }
        String dateflag = String.valueOf(System.currentTimeMillis());
        this.update(Chain.make("dateflag",dateflag),Cnd.where("id","IN",orderEntryIds).and("operator","=",personid));
        Map<String,Object> map = new HashMap<>();
        map.put("dateflag",dateflag);
        return map;
    }

    @Aop(TransAop.READ_COMMITTED)
    public void transmitOrderEntry(String newPersonId, String dateflag) throws ParseException {

        if(Strings.isBlank(newPersonId)){
             throw new ValidatException("被转交人ID不能为空");
        }
        if(Strings.isBlank(dateflag)){
            throw new ValidatException("时间戳标志不能为空");
        }
        //查询转交人转交的所有订单明细
        List<Ins_orderentry> oldOrderentries = this.query(Cnd.where("dateflag", "=", dateflag));
        int transmitNumber = insTransmitService.count(Cnd.where("dateflag", "=", dateflag));
        List<base_person> newPersons = basePersonService.query(Cnd.where("id", "=", newPersonId));
        if(newPersons.size() != 1){
            throw new ValidatException("找不到确定的被转交人");
        }
        base_person newPerson = newPersons.get(0);
        if(oldOrderentries.size() <=0){
            throw new ValidatException("找不到需要转交的订单明细");
        }
        if(oldOrderentries.size() == transmitNumber){
            throw new ValidatException("已进行过转交,请勿重复此操作!");
        }
        //转交人转交的订单明细变为已归还
        this.update(Chain.make("pstatus",3),Cnd.where("dateflag", "=", dateflag));
        //生成新的订单
        Ins_order newOrder = insOrderService.transmitAndNewOrder(newPersonId,newPerson.getAirportid());
        //装转交的所有订单明细对应的订单
        Set<String> orderIds = new HashSet<>();
        Map<String,List<Ins_orderentry>> orderMap = new HashMap<>();
        List<String> baseInsIds = new ArrayList<>();
        //生成新的订单明细
        for (Ins_orderentry oldOrderentry : oldOrderentries) {
            orderIds.add(oldOrderentry.getOrderid());
            if(orderIds.size() > 0 && !Strings.isBlank(oldOrderentry.getOrderid())){
                Iterator<String> iterator = orderIds.iterator();
                //根据订单ID对转交的订单明细进行归类
                while(iterator.hasNext()){
                    if(iterator.next().equals(oldOrderentry.getOrderid())){
                        if(orderMap.get(oldOrderentry.getOrderid()) == null){
                            List<Ins_orderentry> passList = new ArrayList<>();
                            passList.add(oldOrderentry);
                            orderMap.put(oldOrderentry.getOrderid(),passList);
                        }else{
                            orderMap.get(oldOrderentry.getOrderid()).add(oldOrderentry);
                        }
                    }
                }
            }
            Ins_orderentry newOrderentry = new Ins_orderentry();
            newOrderentry.setPstatus(oldOrderentry.getPstatus());
            newOrderentry.setBaseInsId(oldOrderentry.getBaseInsId());
            newOrderentry.setInsId(oldOrderentry.getInsId());
            newOrderentry.setOperator(newPersonId);
            newOrderentry.setOrderid(newOrder.getId());
            newOrderentry = this.insert(newOrderentry);
                Ins_baseins insBaseins = insBaseinsService.fetch(oldOrderentry.getBaseInsId());
                if(insBaseins!=null){
                    baseInsIds.add(insBaseins.getId());
                }
            //(保留)工具状态转交时还是借用中,不作修改
            // 生成转交对象
            insTransmitService.addTransmit(newPersonId,oldOrderentry.getOperator(),newOrderentry.getId(),newDataTime.getDateYMDHMS(),dateflag,oldOrderentry.getId());
        }
        //若转交人转交所有订单明细,应该完成订单
        if(orderMap.size() > 0){
            for (Map.Entry<String, List<Ins_orderentry>> listEntry : orderMap.entrySet()) {
                String orderId = listEntry.getKey();//转交所属订单ID
                int completeCount = this.count(Cnd.where("orderid", "=", orderId).and("pstatus","=",3)) ;
                int orderCount = this.count(Cnd.where("orderid", "=", orderId));
                if(orderCount==completeCount){
                    insOrderService.update(Chain.make("pstatus",1),Cnd.where("id","=",orderId));
                }
            }
        }
        //添加到最近借用
        String[] strings = baseInsIds.toArray(new String[baseInsIds.size()]);
        insCommonService.add2Favorite(newPerson.getId(), strings, "recentBorrow");
    }

    @Aop(TransAop.READ_COMMITTED)
    public Map<String,Object> listenTransmit(String dateflag) {
        Map<String,Object> map = new HashMap<>();
        int odNumber = this.count(Cnd.where("dateflag", "=", dateflag));
        int transmitNumber = insTransmitService.count(Cnd.where("dateflag", "=", dateflag));
        if(odNumber == transmitNumber){
            map.put("result","success");
        }else{
            map.put("result","fail");
        }
        return map;
    }

    @Aop(TransAop.READ_COMMITTED)
    public void addOrderEntryByMobile(String orderId, Map<String, Integer> countMap, String operator) {
        if (countMap.size() > 0) {
            for (Map.Entry<String, Integer> orderMap : countMap.entrySet()) {
                String baseInsId = orderMap.getKey();
                Ins_baseins insBaseins = insBaseinsService.fetch(baseInsId);
                if (insBaseins != null) {
                    //对应基础工具所借数量
                    Integer borrowCount = orderMap.getValue();
                    for (int i = 0; i < borrowCount; i++) {
                        //生成相应数量的订单明细
                        Ins_orderentry orderentry = new Ins_orderentry();
                        orderentry.setOrderid(orderId);
                        orderentry.setOperator(operator);
                        orderentry.setBaseInsId(insBaseins.getId());
                        orderentry.setPstatus(1);
                        this.insert(orderentry);
                    }
                    //改变reserveCount,useableCount
                    int tempUseableCount = insBaseins.getUseableCount() - borrowCount;
                    if(tempUseableCount < 0){
                        tempUseableCount = 0;
                    }
                    insBaseinsService.update(Chain.make("useableCount",tempUseableCount).add("reserveCount", insBaseins.getReserveCount() + borrowCount), Cnd.where("id", "=", insBaseins.getId()));
                }
            }
        }
    }

    @Aop(TransAop.READ_COMMITTED)
    public void addNewOrderEntry(String orderId, String baseInsId, String insId, String operator){
        Ins_orderentry orderentry = new Ins_orderentry();
        orderentry.setBaseInsId(baseInsId);
        orderentry.setOrderid(orderId);
        orderentry.setInsId(insId);
        orderentry.setPstatus(2);
        orderentry.setOperator(operator);
        this.insert(orderentry);
        insInstrumentService.update(Chain.make("pstatus",2),Cnd.where("id","=",insId));
        Ins_baseins insBaseins = insBaseinsService.fetch(baseInsId);
        if(insBaseins != null){
            int tempUseableCount = insBaseins.getUseableCount() - 1;
            if(tempUseableCount < 0){
                tempUseableCount = 0;
            }
            insBaseinsService.update(Chain.make("useableCount",tempUseableCount),Cnd.where("id","=",baseInsId));
        }
    }
}
