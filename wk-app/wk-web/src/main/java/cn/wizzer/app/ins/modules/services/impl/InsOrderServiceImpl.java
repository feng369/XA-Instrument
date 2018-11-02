package cn.wizzer.app.ins.modules.services.impl;

import cn.wizzer.app.base.modules.models.base_cnctobj;
import cn.wizzer.app.base.modules.models.base_person;
import cn.wizzer.app.base.modules.services.BaseCnctobjService;
import cn.wizzer.app.base.modules.services.BasePersonService;
import cn.wizzer.app.getui.GeTuiMessagePush;
import cn.wizzer.app.getui.PushtoSingle;
import cn.wizzer.app.ins.modules.models.*;
import cn.wizzer.app.ins.modules.services.*;
import cn.wizzer.app.sys.modules.models.Sys_user;
import cn.wizzer.app.sys.modules.services.SysUserService;
import cn.wizzer.app.web.modules.controllers.open.api.Datatime.newDataTime;
import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.base.ValidatException;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.framework.util.StringUtil;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@IocBean(args = {"refer:dao"})
public class InsOrderServiceImpl extends BaseServiceImpl<Ins_order> implements InsOrderService {
    public InsOrderServiceImpl(Dao dao) {
        super(dao);
    }

    @Inject
    private BasePersonService basePersonService;
    @Inject
    private InsOrderentryService insOrderentryService;
    @Inject
    private InsInstrumentService insInstrumentService;
    @Inject
    private InsBaseinsService insBaseinsService;
    @Inject
    private InsToolcabinetService insToolcabinetService;
    @Inject
    private InsCommonService insCommonService;
    @Inject
    private InsSpaceService insSpaceService;
    @Inject
    private InsCabinetService insCabinetService;
    @Inject
    private InsBaseinsSpaceService insBaseinsSpaceService;
    @Inject
    private InsLockService insLockService;
    @Inject
    private BaseCnctobjService baseCnctobjService;
    @Inject
    private SysUserService sysUserService;
    private SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
    private ConcurrentHashMap<String, Object> countMap = new ConcurrentHashMap();

    public List<Map<String, Object>> getOrderListByMoibile(String operator, Integer pagenumber, Integer pagesize, String orderPstatus) {
        List<Map<String, Object>> list = new ArrayList<>();
        Cnd cnd = Cnd.NEW();
        cnd.and("operator", "=", operator);
        if (!Strings.isBlank(orderPstatus)) {
            cnd.and("pstatus", "=", orderPstatus);
        }
        cnd.orderBy("ordernum", "DESC");
        List<Ins_order> insOrders = this.query(cnd, "baseAirport", StringUtil.getPagerObject(pagenumber, pagesize));
        for (Ins_order order : insOrders) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", order.getId());
            map.put("ordernum", order.getOrdernum());
            map.put("airportname", order.getBaseAirport() == null ? "" : order.getBaseAirport().getAirportname());
            map.put("flightnum", order.getOrdernum());
            map.put("seatnum", order.getSeatnum());
            map.put("operatetime", order.getOperatetime());
            map.put("pstatus", order.getPstatus());
            map.put("pstatusname", order.getPstatusname());
            map.put("starttime", order.getStarttime());
            map.put("endtime", order.getEndtime());
            map.put("describ", order.getDescrib());
            List<Ins_orderentry> orderentries = insOrderentryService.query(Cnd.where("orderid", "=", order.getId()));
            map.put("orderentryNumber", orderentries.size());
            List<String> picList = new ArrayList<>();
            if (orderentries.size() > 0) {
                for (Ins_orderentry orderentry : orderentries) {
                    orderentry = insOrderentryService.fetchLinks(orderentry, "instrument");
                    Ins_instrument instrument = orderentry.getInstrument();
                    if (instrument != null) {
                        String baseInsId = instrument.getBaseInsId();
                        Ins_baseins baseins = insBaseinsService.fetch(baseInsId);
                        if (baseins != null) {
                            picList.add(baseins.getToolPic());
                        }
                        /*
                        Ins_instrument insBaseins = insBaseinsService.fetchLinks(instrument, "insBaseins");
                        if(insBaseins != null){

                        }
                        */
                    }
                }
            }
            map.put("picList", picList);
            list.add(map);
        }
        return list;
    }

    @Aop(TransAop.READ_COMMITTED)
    public Result addOrderAndOrderEntryByMobile(String[] boxIds, String operator, String operatetime, String flightnum, String seatnum, String describ, String airportid) throws ParseException {
        if (boxIds.length == 0) {
            throw new ValidatException("传入工具箱数据有误");
        }
        if (Strings.isBlank(operator)) {
            throw new ValidatException("传入下单人ID为空");
        }
        if (Strings.isBlank(operatetime)) {
            throw new ValidatException("传入操作时间为空");
        }
        if (boxIds.length > 0) {
            //先校验工具库存量
            //找到勾选的所有工具箱
            List<Ins_toolcabinet> toolcabinets = insToolcabinetService.query(Cnd.where("id", "IN", boxIds).and("personid","=",operator));
            //记录库存不足的工具
            List<Map<String, Object>> overTools = new ArrayList<>();
            //添加到订单明细中的具体工具
//            List<Ins_instrument> instruments = new ArrayList<>();
            List<String> baseInsIds = new ArrayList<>();
            Map<String, Integer> countMap = new HashMap<>();
            if (toolcabinets.size() > 0) {
                for (Ins_toolcabinet toolcabinet : toolcabinets) {
                    double number = toolcabinet.getNumber();
                    String baseInsId = toolcabinet.getBaseInsId();
                    if (Strings.isNotBlank(baseInsId)) {
                        baseInsIds.add(baseInsId);
                        Ins_baseins insBaseins = insBaseinsService.fetch(baseInsId);
                        if (insBaseins != null) {
                            //可用数量+预留数量
                            int insCount = insBaseins.getUseableCount();
                            if (insCount < number) {
                                Map<String, Object> map = new HashMap<>();
                                map.put("insName", insBaseins.getInsname());
                                map.put("insModel", insBaseins.getInsmodel());
                                map.put("instrumentsSize", insCount);
                                overTools.add(map);
                            } else {
                                countMap.put(insBaseins.getId(), (int) number);
                            }
                            /*else {
                                //满足要求,则分配相应数量的工具
                                instruments.addAll(insInstruments.subList(0, (int) number));
                            }*/
                        }
                    }
                }
                StringBuffer content = new StringBuffer();
                if (overTools.size() > 0) {
                    //有库存不足的情况
                    content.append("库存不足!");
                    for (Map<String, Object> overTool : overTools) {
                        content.append("[" + overTool.get("insName") + "]").append("[" + overTool.get("insModel") + "]").append("库存数量为" + overTool.get("instrumentsSize")).append(";");
                    }
                    return Result.error(2, content.toString());
                }
                //生成订单
                Ins_order order = new Ins_order();
                order.setAirportid(airportid);
                order.setFlightnum(flightnum);
                order.setDescrib(describ);
                order.setOperatetime(operatetime);
                order.setSeatnum(seatnum);
                order.setOperator(operator);
                order.setPstatus(-1);
                //生成订单编码
                order.setOrdernum(getOrderNum(order.getOperatetime(), airportid));
                order = this.insert(order);
                //订单明细,改变基础工具对应数量字段
                insOrderentryService.addOrderEntryByMobile(order.getId(), countMap, operator);
                //删除工具箱
                insToolcabinetService.delete(boxIds);
                String[] strings = baseInsIds.toArray(new String[baseInsIds.size()]);
                insCommonService.add2Favorite(operator, strings, "recentBorrow");
                //发送推送消息
                base_cnctobj cnctobj = baseCnctobjService.fetch(Cnd.where("personId", "=", operator));
                if(cnctobj != null){
                    String userId = cnctobj.getUserId();
                    Sys_user sysUser = sysUserService.fetch(userId);
//                    PushtoSingle.push2Single("下单了","西安工具已下单",user.getClientId());
//                    GeTuiMessagePush.pushMessage("测试","西安工具,请尽快从智能柜中拿出相应工具","工具已下单","西安工具,请尽快从智能柜中拿出相应工具",sysUser.getClientId());
                }
                return Result.success("system.success");
            }
        }
        return Result.error("system.error");
    }

    public String getOrderNum(String operatetime, String airportid) throws ParseException {
        StringBuffer num = new StringBuffer(80);
        num.append("TOOL-");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(operatetime);
        String dateStr = sdf.format(date);
        num.append(dateStr).append("-");
        int count = this.count(Cnd.where("DATE_FORMAT(operatetime,'%Y-%m-%d')", "=", dateStr));
        num.append(String.format("%04d", count + 1));
        return num.toString();
    }
    //后期可能需要变更此方法
   /* public String getOrderNum(String operatetime, String airportid) throws ParseException {
        StringBuffer num = new StringBuffer(80);
        num.append("TOOL-");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(operatetime);
        String dateStr = sdf.format(date);
        num.append(dateStr).append("-");
        //今日订单
        String format = String.format("%04d", getOrderNumSeq(dateStr, airportid));
        num.append(format);
        return num.toString();
    }*/

    /*public synchronized int getOrderNumSeq(String dateStr, String airportid) throws ParseException {
        boolean isNewDay = false;
//        Date date = ymd.parse(dateStr);
        //如果是今天
        if(countMap.get("date") == null){
            countMap.put("date",dateStr);
        }else {
            String date = (String)countMap.get("date");
            if(!date.equals(dateStr)){
                isNewDay = true;
                countMap.put("date",dateStr);
            }
        }
        if (countMap.get("tool") == null ) {
            //今日订单
            int prevCount = this.count(Cnd.where("DATE_FORMAT(operatetime,'%Y-%m-%d')", "=",dateStr).and("airportid", "=", airportid).orderBy("operatetime","ASC"));
            countMap.put("tool", prevCount + 1);
        } else {
            if(isNewDay){
                countMap.put("tool",1);
            }else{
                countMap.put("tool", (int)countMap.get("tool") + 1);
            }
        }
        return (int)countMap.get("tool");
    }*/


    public Map<String, Object> getOrderCount(String airportId) {
        Map<String, Object> map = new HashMap<>();
        int todayOrders;
        int tMonthOrders;
        //今日订单
        todayOrders = this.count(Cnd.where("operatetime", ">=", newDataTime.getSdfByPattern(null).format(newDataTime.startOfToday())).and("operatetime", "<=", newDataTime.getSdfByPattern(null).format(newDataTime.endOfToday())).and("airportid", "=", airportId));
        //本月订单
        tMonthOrders = this.count(Cnd.where("operatetime", ">=", newDataTime.getSdfByPattern(null).format(newDataTime.startOfThisMonth())).and("operatetime", "<=", newDataTime.getSdfByPattern(null).format(newDataTime.endOfThisMonth())).and("airportid", "=", airportId));
        map.put("todayOrders", todayOrders);
        map.put("tMonthOrders", tMonthOrders);
        return map;
    }

    public Map<String, Object> getOrderInfo(String orderId) {
        if (Strings.isBlank(orderId)) {
            throw new ValidatException("传入的订单ID为空");
        }
        Ins_order insOrder = this.fetch(orderId);
        if (insOrder == null) {
            throw new ValidatException("未找到确定的订单");
        }
        insOrder = this.fetchLinks(insOrder, "basePerson|baseAirport");
        Map<String, Object> map = new HashMap<>();
        map.put("describ", insOrder.getDescrib());
        map.put("pstatus", insOrder.getPstatus());
        map.put("pstatusname", insOrder.getPstatusname());
        map.put("operatetime", insOrder.getOperatetime());
        map.put("ordernum", insOrder.getOrdernum());
        map.put("flightnum", insOrder.getFlightnum());
        map.put("seatnum", insOrder.getSeatnum());
        map.put("airportname", insOrder.getBaseAirport() == null ? "" : insOrder.getBaseAirport().getAirportname());
        map.put("personname", insOrder.getBasePerson() == null ? "" : insOrder.getBasePerson().getPersonname());
        return map;
    }

    @Aop(TransAop.READ_COMMITTED)
    public Ins_order transmitAndNewOrder(String personId, String airportId) throws ParseException {
        Ins_order newOrder = new Ins_order();
        newOrder.setOrdernum(getOrderNum(newDataTime.getSdfByPattern(null).format(new Date()), airportId));
        newOrder.setOperator(personId);
        newOrder.setOperatetime(newDataTime.getSdfByPattern(null).format(new Date()));
        newOrder.setPstatus(0);
        newOrder.setAirportid(airportId);
        return this.insert(newOrder);
    }

    public List<Map<String, Object>> getOrderAndEntryList(String operator, Integer pagenumber, Integer pagesize, String orderPstatus) {

       /* List<Map<String, Object>> list = new ArrayList<>();
        Cnd cnd = Cnd.NEW();
        cnd.and("operator", "=", operator);
        if (!Strings.isBlank(orderPstatus)) {
            cnd.and("pstatus", "=", orderPstatus);
        }
        cnd.orderBy("ordernum", "DESC");
        List<Ins_order> insOrders = this.query(cnd, "baseAirport", StringUtil.getPagerObject(pagenumber, pagesize));
        for (Ins_order order : insOrders) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", order.getId());
            base_person basePerson = basePersonService.fetch(operator);
            if (basePerson != null) {
                map.put("personName", basePerson.getPersonname());
            } else {
                map.put("personName", "");
            }
            map.put("ordernum", order.getOrdernum());
            map.put("airportname", order.getBaseAirport() == null ? "" : order.getBaseAirport().getAirportname());
            map.put("flightnum", order.getFlightnum());
            map.put("seatnum", order.getSeatnum());
            map.put("operatetime", order.getOperatetime());
            map.put("pstatus", order.getPstatus());
            map.put("pstatusname", order.getPstatusname());
            map.put("starttime", order.getStarttime());
            map.put("endtime", order.getEndtime());
            map.put("describ", order.getDescrib());
            List<Ins_orderentry> orderentries = insOrderentryService.query(Cnd.where("orderid", "=", order.getId()));
            map.put("orderentryNumber", orderentries.size());
            Set<String> picSet = new HashSet<>();
            List<Map<String, Object>> entryList = new ArrayList<>();
            if (orderentries.size() > 0) {
                for (Ins_orderentry orderentry : orderentries) {

                    if (!Strings.isBlank(orderentry.getBaseInsId())) {
                        Ins_baseins baseins = insBaseinsService.fetch(orderentry.getBaseInsId());
                        if (baseins != null) {
                            picSet.add(baseins.getToolPic());
                            Map<String, Object> entryMap = new HashMap<>();
                            int entryStatus = orderentry.getPstatus();
                            entryMap.put("entryStatus", entryStatus);
                            *//*if(entryStatus == 1){
                                //如果订单明细状态为待借用,则可以打开柜门
                                entryMap.put("canOpen", true);
                            }else{
                                //如果订单明细状态为待借用,则不能打开柜门
                                entryMap.put("canOpen", false);
                            }*//*

                            entryMap.put("entryStatusName", getEntryPstatusName(orderentry.getPstatus()));
//                            entryMap.put("insId", instrument.getId());
//                            entryMap.put("insPstatus", instrument.getPstatus());
                            entryMap.put("insModel", baseins.getInsmodel());
                            entryMap.put("insName", baseins.getInsname());
                            entryMap.put("toolPic", baseins.getToolPic());
                            List<Ins_baseins_space> insBaseinsSpaces = insBaseinsSpaceService.query(Cnd.where("baseInsId", "=", baseins.getId()));
                            //如果工具不在此柜中,则也不能打开
                            Map<String, List<String>> spacesStrMap = new HashMap<>();
                            if (insBaseinsSpaces.size() > 0) {
                                for (Ins_baseins_space insBaseinsSpace : insBaseinsSpaces) {
                                    if (!Strings.isBlank(insBaseinsSpace.getSpaceId())) {
                                        String spaceId = insBaseinsSpace.getSpaceId();
                                        Ins_space insSpace = insSpaceService.fetch(spaceId);
                                        if (insSpace != null) {
                                            Ins_cabinet insCabinet = insCabinetService.fetch(insSpace.getCabinetid());
                                            if (insCabinet != null) {
                                                if (spacesStrMap.get(insCabinet.getCabinetname()) == null) {
                                                    List<String> spaceNames = new ArrayList<>();
                                                    spaceNames.add(insSpace.getSpacename());
                                                    spacesStrMap.put(insCabinet.getCabinetname(), spaceNames);
                                                } else {
                                                    List<String> spaceNames = spacesStrMap.get(insCabinet.getCabinetname());
                                                    spaceNames.add(insSpace.getSpacename());
                                                    spacesStrMap.put(insCabinet.getCabinetname(), spaceNames);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (spacesStrMap.size() > 0) {
                                StringBuilder sb = new StringBuilder(80);
                                for (Map.Entry<String, List<String>> entry : spacesStrMap.entrySet()) {
                                    String cabinetName = entry.getKey();
                                    List<String> spaceNames = entry.getValue();
                                    sb.append(cabinetName).append(":");
                                    for (String spaceName : spaceNames) {
                                        sb.append(spaceName).append(" ");
                                    }
                                }
                                entryMap.put("storeAddress", sb.toString());
                            } else {
                                entryMap.put("storeAddress", "");
                            }
                            entryList.add(entryMap);
                        }
                    }
                }
                   *//* orderentry = insOrderentryService.fetchLinks(orderentry, "instrument");
                    Ins_instrument instrument = orderentry.getInstrument();
                    if (instrument != null) {
                        String baseInsId = instrument.getBaseInsId();
                        Ins_baseins baseins = insBaseinsService.fetch(baseInsId);
                        picSet.add(baseins.getToolPic());
                        Map<String, Object> entryMap = new HashMap<>();
                        entryMap.put("entryStatus", orderentry.getPstatus());
                        entryMap.put("entryStatusName", getEntryPstatusName(orderentry.getPstatus()));
                        entryMap.put("insId", instrument.getId());
                        entryMap.put("insPstatus", instrument.getPstatus());
                        entryMap.put("insModel", baseins.getInsmodel());
                        entryMap.put("insName", baseins.getInsname());
                        entryMap.put("toolPic", baseins.getToolPic());
                        *//**//*if (!Strings.isBlank(instrument.getInsspaceid())) {
                            Ins_space insSpace = insSpaceService.fetch(instrument.getInsspaceid());
                            entryMap.put("spaceName", insSpace == null ? "" : insSpace.getSpacename());
                            if (insSpace != null) {
                                Ins_cabinet insCabinet = insCabinetService.fetch(insSpace.getCabinetid());
                                entryMap.put("cabinetName", insCabinet == null ? "" : insCabinet.getCabinetname());
                            }
                        } else {
                            entryMap.put("spaceName", "");
                            entryMap.put("cabinetName", "");
                        }*//**//*
                        if(!Strings.isBlank(baseins.getSpaceId())){
                            Ins_space insSpace = insSpaceService.fetch(baseins.getSpaceId());
                            entryMap.put("spaceName", insSpace == null ? "" : insSpace.getSpacename());
                            if (insSpace != null) {
                                Ins_cabinet insCabinet = insCabinetService.fetch(insSpace.getCabinetid());
                                entryMap.put("cabinetName", insCabinet == null ? "" : insCabinet.getCabinetname());
                            }
                        } else {
                            entryMap.put("spaceName", "");
                            entryMap.put("cabinetName", "");
                        }

                        entryList.add(entryMap);
                    }*//*
            }
            map.put("entryList", entryList);
            map.put("picSet", picSet);
            list.add(map);
        }
        return list;*/
            if (Strings.isBlank(operator)) {
            throw new ValidatException("用户ID为空");
        }
   /*     if (Strings.isBlank(operator)) {
            throw new ValidatException("智能柜ID为空");
        }*/
       /* if (Strings.isBlank(orderPstatus)) {
            orderPstatus = " 0 ";
        }*/
        String sqlStr = " SELECT \n" +
                "\t io.id,io.ordernum,io.flightnum,io.operatetime,io.pstatus, \n" +
                "\t ib.insmodel,ib.insname,ib.toolPic, \n" +
                "\t od.pstatus  entryStatus,od.id  entryId, \n" +
                "\t bp.personname personName, \n" +
                "\t ap.airportname \n" +
//                "\t ic.cabinetname, \n" +
//                "\t sp.spacename, \n" +
//                " ic.id  cabId " +
                "\t FROM ins_order io \n" +
                "\t LEFT JOIN ins_orderentry od ON io.id = od.orderid \n" +
                "\t LEFT JOIN base_person bp ON io.operator = bp.id \n" +
                "\t LEFT JOIN base_airport ap ON io.airportid = ap.id \n" +
                "\t LEFT JOIN ins_baseins ib ON od.baseInsId = ib.id \n" +
//                "\t LEFT JOIN ins_baseins_space ibs ON ib.id = ibs.baseInsId \n" +
//                "\t LEFT JOIN ins_space sp ON sp.id = ibs.spaceId \n" +
//                "\t LEFT JOIN ins_cabinet ic ON sp.cabinetid = ic.id \n" +
                " \t WHERE bp.id = '" + operator + "' " +
                (!Strings.isBlank(orderPstatus) ? " AND io.pstatus = '" + orderPstatus + "'" : " ") +
                "\t ORDER BY io.operatetime DESC ";
        sqlStr = StringUtil.appendSqlStrByPager(pagenumber, pagesize, sqlStr);
        Sql sql = Sqls.queryRecord(sqlStr);
        dao().execute(sql);
        List<Record> recordList = sql.getList(Record.class);
        Map<String, String> orderMap = new HashMap<>();
        List<Map<String, Object>> orderList = new ArrayList<>();
        //去除重复orderId数据
        Iterator<Record> iterator = recordList.iterator();
        while (iterator.hasNext()) {
            Record record = iterator.next();
            String orderId = record.getString("id");
            if (orderMap.get(orderId) == null) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", StringUtil.null2TrimStr(orderId));
                map.put("ordernum", StringUtil.null2TrimStr(record.getString("ordernum")));
                map.put("flightnum", StringUtil.null2TrimStr(record.getString("flightnum")));
                map.put("operatetime", StringUtil.null2TrimStr(record.getString("operatetime")));
                int pstatus = record.getInt("pstatus");
                map.put("pstatus", pstatus);
                map.put("pstatusname", getOrderStatusName(pstatus));
                map.put("personName", StringUtil.null2TrimStr(record.getString("personName")));
                map.put("describ", StringUtil.null2TrimStr(record.getString("describ")));
                orderList.add(map);
                orderMap.put(orderId, orderId);
            }
        }
        //订单相关信息
        for (Map<String, Object> map : orderList) {
            String orderId1 = (String) map.get("id");
            List<Map<String, Object>> entryList = new ArrayList<>();
            //订单内图片相对路径集合
            Set<String> picSet = new HashSet<>();
            //记录相同订单明细ID
//            Map<String, String> entryIdMap = new HashMap<>();
//            Set<String> spaceNumSet = new HashSet<>();
            int count = 0;
            for (Record record : recordList) {
                String orderId2 = record.getString("id");
                String entryId = record.getString("entryId");
                if (orderId1 != null && orderId1.equals(orderId2)) {
                    count++;
                    int entryStatus = record.getInt("entryStatus");
                    Map<String, Object> infoMap = new HashMap<>();
                    infoMap.put("entryStatus", entryStatus);
                    infoMap.put("entryStatusName", getEntryPstatusName(entryStatus));
                    infoMap.put("insModel", StringUtil.null2TrimStr(record.getString("insmodel")));
                    infoMap.put("insName", StringUtil.null2TrimStr(record.getString("insname")));
                    String toolPic = record.getString("toolPic");
                    infoMap.put("toolPic", StringUtil.null2TrimStr(toolPic));
                    if (!Strings.isBlank(toolPic)) {
                        picSet.add(toolPic);
                    }
                    String entryStr = " SELECT ic.cabinetname,sp.spacenum,sp.spacename \n" +
                            "\tFROM ins_orderentry od \n" +
                            "\tLEFT JOIN ins_baseins ib ON od.baseInsId = ib.id \n" +
                            "\tLEFT JOIN ins_baseins_space ibs ON ib.id = \tibs.baseInsId \n" +
                            "\tLEFT JOIN ins_space sp ON ibs.spaceId = sp.id\n" +
                            "\tLEFT JOIN ins_cabinet ic ON sp.cabinetid = ic.id\n" +
                            "\tWHERE od.id = '" + entryId + "' ";

                    Sql entrySql = Sqls.queryRecord(entryStr);
                    dao().execute(entrySql);
                    List<Record> entrySqlList = entrySql.getList(Record.class);
                    Map<String, List<String>> storeMap = new HashMap<>();
                    if (entrySqlList.size() > 0) {
                        for (Record entryRecord : entrySqlList) {
//                            String spaceNum = entryRecord.getString("spacenum");
//                            spaceNumSet.add(spaceNum);
                            String cabinetName = entryRecord.getString("cabinetname");
                            String spaceName = entryRecord.getString("spacename");
                            if (storeMap.get(cabinetName) == null) {
                                List<String> spaceNameList = new ArrayList<>();
                                spaceNameList.add(spaceName);
                                storeMap.put(cabinetName, spaceNameList);
                            } else {
                                List<String> spaceNameList = storeMap.get(cabinetName);
                                spaceNameList.add(spaceName);
                            }
                        }
                        StringBuilder sb = new StringBuilder(80);
                        if (storeMap.size() > 0) {
                            for (Map.Entry<String, List<String>> entry : storeMap.entrySet()) {
                                String cabinetName = entry.getKey();
                                List<String> spaceNameList = entry.getValue();
                                //多对多可能变更此处
                                sb.append(cabinetName).append(":");
                                for (String spaceName : spaceNameList) {
                                    sb.append(spaceName).append(" ");
                                }
                            }
                        }
//                        infoMap.put("spaceNumSet",spaceNumSet);
                        infoMap.put("storeAddress", sb.toString());
                    }
                    entryList.add(infoMap);
                }
            }
            map.put("orderentryNumber", count);
            map.put("picSet", picSet);
            map.put("entryList", entryList);
        }
        return orderList;
    }

    public List<Map<String, Object>> getAllOrderAndEntry(String personId, Integer pagenumber, Integer pagesize, String orderPstatus, String cabinetId) {
        if (Strings.isBlank(personId)) {
            throw new ValidatException("用户ID为空");
        }
        if (Strings.isBlank(cabinetId)) {
            throw new ValidatException("智能柜ID为空");
        }
       /* if (Strings.isBlank(orderPstatus)) {
            orderPstatus = " 0 ";
        }*/
        String sqlStr = " SELECT \n" +
                "\t io.id,io.ordernum,io.flightnum,io.operatetime,io.pstatus, \n" +
                "\t ib.insmodel,ib.insname,ib.toolPic, \n" +
                "\t od.pstatus  entryStatus,od.id  entryId, \n" +
                "\t bp.personname personName, \n" +
                "\t ap.airportname \n" +
//                "\t ic.cabinetname, \n" +
//                "\t sp.spacename, \n" +
//                " ic.id  cabId " +
                "\t FROM ins_order io \n" +
                "\t LEFT JOIN ins_orderentry od ON io.id = od.orderid \n" +
                "\t LEFT JOIN base_person bp ON io.operator = bp.id \n" +
                "\t LEFT JOIN base_airport ap ON io.airportid = ap.id \n" +
                "\t LEFT JOIN ins_baseins ib ON od.baseInsId = ib.id \n" +
//                "\t LEFT JOIN ins_baseins_space ibs ON ib.id = ibs.baseInsId \n" +
//                "\t LEFT JOIN ins_space sp ON sp.id = ibs.spaceId \n" +
//                "\t LEFT JOIN ins_cabinet ic ON sp.cabinetid = ic.id \n" +
                " \t WHERE bp.id = '" + personId + "' " + (!Strings.isBlank(orderPstatus) ? " AND io.pstatus = '" + orderPstatus + "' " : " ") +
                "\t ORDER BY io.operatetime ";
//        sqlStr = StringUtil.appendSqlStrByPager(pagenumber, pagesize, sqlStr);
        Sql sql = Sqls.queryRecord(sqlStr);
        dao().execute(sql);
        List<Record> recordList = sql.getList(Record.class);
        Map<String, String> orderMap = new HashMap<>();
        List<Map<String, Object>> orderList = new ArrayList<>();
        //去除重复orderId数据
        Iterator<Record> iterator = recordList.iterator();
        while (iterator.hasNext()) {
            Record record = iterator.next();
            String orderId = record.getString("id");
            if (orderMap.get(orderId) == null) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", StringUtil.null2TrimStr(orderId));
                map.put("ordernum", StringUtil.null2TrimStr(record.getString("ordernum")));
                map.put("flightnum", StringUtil.null2TrimStr(record.getString("flightnum")));
                map.put("operatetime", StringUtil.null2TrimStr(record.getString("operatetime")));
                int pstatus = record.getInt("pstatus");
                map.put("orderStatus", pstatus);
                map.put("orderStatusName", getOrderStatusName(pstatus));
                map.put("personName", StringUtil.null2TrimStr(record.getString("personName")));
                map.put("describ", StringUtil.null2TrimStr(record.getString("describ")));
                orderList.add(map);
                orderMap.put(orderId, orderId);
            }
        }
        //订单相关信息
        for (Map<String, Object> map : orderList) {
            String orderId1 = (String) map.get("id");
            List<Map<String, Object>> entryList = new ArrayList<>();
            //订单内图片相对路径集合
            Set<String> picSet = new HashSet<>();
            //记录相同订单明细ID
//            Map<String, String> entryIdMap = new HashMap<>();
            Set<String> spaceNumSet = new HashSet<>();
            int count = 0;
            for (Record record : recordList) {
                String orderId2 = record.getString("id");
                String entryId = record.getString("entryId");
                if (orderId1 != null && orderId1.equals(orderId2)) {
                    count++;
                    int entryStatus = record.getInt("entryStatus");
                    Map<String, Object> infoMap = new HashMap<>();
                    infoMap.put("entryStatus", entryStatus);
                    infoMap.put("entryStatusName", getEntryPstatusName(entryStatus));
                    infoMap.put("insModel", StringUtil.null2TrimStr(record.getString("insmodel")));
                    infoMap.put("insName", StringUtil.null2TrimStr(record.getString("insname")));
                    String toolPic = record.getString("toolPic");
                    infoMap.put("toolPic", StringUtil.null2TrimStr(toolPic));
                    if (!Strings.isBlank(toolPic)) {
                        picSet.add(toolPic);
                    }
                    String entryStr = " SELECT ic.id as cabid,ic.cabinetname,sp.spacenum,sp.spacename \n" +
                            "\tFROM ins_orderentry od \n" +
                            "\tLEFT JOIN ins_baseins ib ON od.baseInsId = ib.id \n" +
                            "\tLEFT JOIN ins_baseins_space ibs ON ib.id = \tibs.baseInsId \n" +
                            "\tLEFT JOIN ins_space sp ON ibs.spaceId = sp.id\n" +
                            "\tLEFT JOIN ins_cabinet ic ON sp.cabinetid = ic.id\n" +
                            "\tWHERE od.id = '" + entryId + "' ";
                    Sql entrySql = Sqls.queryRecord(entryStr);
                    dao().execute(entrySql);
                    List<Record> entrySqlList = entrySql.getList(Record.class);
                    Map<String, List<String>> storeMap = new HashMap<>();
                    if (entrySqlList.size() > 0) {
                        for (Record entryRecord : entrySqlList) {
                            String cabId = entryRecord.getString("cabid");
                            if (!Strings.isBlank(cabId)) {
                                if (cabId.equals(cabinetId) && (entryStatus == 1 || entryStatus == 2)) {
                                    //如果订单工具存在于此仓位,并且是待借用状态,则可以打开柜门
                                    infoMap.put("canOpen", 1);
                                } else {
                                    infoMap.put("canOpen", 0);
                                }
                            }
                            String spaceNum = entryRecord.getString("spacenum");
                            spaceNumSet.add(spaceNum);
                            String cabinetName = entryRecord.getString("cabinetname");
                            String spaceName = entryRecord.getString("spacename");
                            if (storeMap.get(cabinetName) == null) {
                                List<String> spaceNameList = new ArrayList<>();
                                spaceNameList.add(spaceName);
                                storeMap.put(cabinetName, spaceNameList);
                            } else {
                                List<String> spaceNameList = storeMap.get(cabinetName);
                                spaceNameList.add(spaceName);
                            }
                        }
                        StringBuilder sb = new StringBuilder(80);
                        if (storeMap.size() > 0) {
                            for (Map.Entry<String, List<String>> entry : storeMap.entrySet()) {
//                                String cabinetName = entry.getKey();
                                List<String> spaceNameList = entry.getValue();
                                //多对多可能变更此处
//                                sb.append(cabinetName).append(":");
                                for (String spaceName : spaceNameList) {
                                    sb.append(spaceName).append(" ");
                                }
                            }
                        }
                        infoMap.put("spaceNumSet", spaceNumSet);
                        infoMap.put("storeAddress", sb.toString());
                    }
                    entryList.add(infoMap);
                }
            }
            map.put("orderentryNumber", count);
            map.put("picSet", picSet);
            map.put("entryList", entryList);
        }
        return orderList;
    }
   /* public List<Map<String, Object>> getAllOrderAndEntry(String personId, Integer pagenumber, Integer pagesize, String orderPstatus, String cabinetId) {
        if(Strings.isBlank(personId)){
            throw new ValidatException("用户ID为空");
        }
        if(Strings.isBlank(cabinetId)){
            throw new ValidatException("智能柜ID为空");
        }
        List<Map<String, Object>> list = new ArrayList<>();
        Cnd cnd = Cnd.NEW();
        cnd.and("operator", "=", personId);
        if (!Strings.isBlank(orderPstatus)) {
            cnd.and("pstatus", "=", orderPstatus);
        }
        cnd.orderBy("ordernum", "DESC");
        List<Ins_order> insOrders = this.query(cnd, "baseAirport|basePerson", StringUtil.getPagerObject(pagenumber, pagesize));
        if(insOrders.size() > 0 ){
            for (Ins_order order : insOrders) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", order.getId());
                base_person basePerson = basePersonService.fetch(personId);
                if (basePerson != null) {
                    map.put("personName", basePerson.getPersonname());
                } else {
                    map.put("personName", "");
                }
                map.put("ordernum", order.getOrdernum());
                map.put("airportname", order.getBaseAirport() == null ? "" : order.getBaseAirport().getAirportname());
                map.put("flightnum", order.getFlightnum());
                map.put("seatnum", order.getSeatnum());
                map.put("operatetime", order.getOperatetime());
                map.put("pstatus", order.getPstatus());
                map.put("pstatusname", order.getPstatusname());
                map.put("starttime", order.getStarttime());
                map.put("endtime", order.getEndtime());
                map.put("describ", order.getDescrib());
                List<Ins_orderentry> orderentries = insOrderentryService.query(Cnd.where("orderid", "=", order.getId()));
                map.put("orderentryNumber", orderentries.size());
                Set<String> picSet = new HashSet<>();
                List<Map<String, Object>> entryList = new ArrayList<>();
                if (orderentries.size() > 0) {
                    for (Ins_orderentry orderentry : orderentries) {
                        if (!Strings.isBlank(orderentry.getBaseInsId())) {
                            Ins_baseins baseins = insBaseinsService.fetch(orderentry.getBaseInsId());
                            if (baseins != null) {
                                picSet.add(baseins.getToolPic());
                                Map<String, Object> entryMap = new HashMap<>();
                                Set<String> spaceNumSet = new HashSet<>();
                                int entryStatus = orderentry.getPstatus();
                                entryMap.put("entryStatus", entryStatus);
                                entryMap.put("entryStatusName", getEntryPstatusName(orderentry.getPstatus()));

//                            entryMap.put("insId", instrument.getId());
//                            entryMap.put("insPstatus", instrument.getPstatus());
                                entryMap.put("insModel", baseins.getInsmodel());
                                entryMap.put("insName", baseins.getInsname());
                                entryMap.put("toolPic", baseins.getToolPic());
                                List<Ins_baseins_space> insBaseinsSpaces = insBaseinsSpaceService.query(Cnd.where("baseInsId", "=", baseins.getId()));
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
                                                    if(cabinetId.equals(insCabinet.getId())  &&  (entryStatus == 1 || entryStatus == 2)){
                                                        //如果订单工具存在于此仓位,并且是待借用状态,则可以打开柜门
                                                        entryMap.put("canOpen", 1);
                                                        spaceNumSet.add(insSpace.getSpacenum());
                                                      *//*
                                                        String lockId = insSpace.getLockId();
                                                        if(!Strings.isBlank(lockId)){
                                                            Ins_lock lock = insLockService.fetch(lockId);
                                                            if(lock != null){
                                                                lockNumSet.add(lock.getLocknum());
                                                            }
                                                        }*//*


                                                    }else{
                                                        entryMap.put("canOpen", 0);
                                                    }
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
                                entryMap.put("spaceNumSet",spaceNumSet);
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
                                    entryMap.put("storeAddress",sb.toString());
                                }else{
                                    entryMap.put("storeAddress","");
                                }
                                entryList.add(entryMap);
                            }
                        }
                    }
  Ins_orderentry orderentry = insOrderentryService.fetchLinks(orderentry, "instrument");
                    Ins_instrument instrument = orderentry.getInstrument();
                    if (instrument != null) {
                        String baseInsId = instrument.getBaseInsId();
                        Ins_baseins baseins = insBaseinsService.fetch(baseInsId);
                        picSet.add(baseins.getToolPic());
                        Map<String, Object> entryMap = new HashMap<>();
                        entryMap.put("entryStatus", orderentry.getPstatus());
                        entryMap.put("entryStatusName", getEntryPstatusName(orderentry.getPstatus()));
                        entryMap.put("insId", instrument.getId());
                        entryMap.put("insPstatus", instrument.getPstatus());
                        entryMap.put("insModel", baseins.getInsmodel());
                        entryMap.put("insName", baseins.getInsname());
                        entryMap.put("toolPic", baseins.getToolPic());

if (!Strings.isBlank(instrument.getInsspaceid())) {
                            Ins_space insSpace = insSpaceService.fetch(instrument.getInsspaceid());
                            entryMap.put("spaceName", insSpace == null ? "" : insSpace.getSpacename());
                            if (insSpace != null) {
                                Ins_cabinet insCabinet = insCabinetService.fetch(insSpace.getCabinetid());
                                entryMap.put("cabinetName", insCabinet == null ? "" : insCabinet.getCabinetname());
                            }
                        } else {
                            entryMap.put("spaceName", "");
                            entryMap.put("cabinetName", "");
                        }
                        if(!Strings.isBlank(baseins.getSpaceId())){
                            Ins_space insSpace = insSpaceService.fetch(baseins.getSpaceId());
                            entryMap.put("spaceName", insSpace == null ? "" : insSpace.getSpacename());
                            if (insSpace != null) {
                                Ins_cabinet insCabinet = insCabinetService.fetch(insSpace.getCabinetid());
                                entryMap.put("cabinetName", insCabinet == null ? "" : insCabinet.getCabinetname());
                            }
                        } else {
                            entryMap.put("spaceName", "");
                            entryMap.put("cabinetName", "");
                        }

                        entryList.add(entryMap);
                    }

                }
                map.put("entryList", entryList);
                map.put("picSet", picSet);
//                map.put("spaceNumSet", spaceNumSet);
                list.add(map);
            }
        }
        return list;
    }*/

    public List<Map<String, Object>> getOrderDetail(String orderId) {
        String sqlStr = " SELECT \n" +
                "\t\tio.id as orderId,\n" +
                "\t\tod.id as entryId,od.pstatus as entryStatus,\n" +
                "\t\tib.insname,ib.toolPic,ib.insmodel,\n" +
                "\t\tins.pstatus as toolStstus\n" +
                "\tFROM ins_order io \n" +
                "\tJOIN ins_orderentry od ON io.id = od.orderid \n" +
                "\tJOIN ins_baseins ib ON od.baseInsId = ib.id\n" +
                "\tLEFT JOIN ins_instrument ins ON od.insId = ins.id\n" +
                "\tWHERE io.id = '" + orderId + "' ORDER BY od.pstatus ASC ";
        Sql sql = Sqls.queryRecord(sqlStr);
        dao().execute(sql);
        List<Record> recordList = sql.getList(Record.class);

        List<Map<String, Object>> list = new ArrayList<>();
        if (recordList.size() > 0) {
            for (Record record : recordList) {
                Map<String, Object> map = new HashMap<>();
                int entryStatus = record.getInt("entryStatus");
                String insname = record.getString("insname");
                String toolPic = record.getString("toolPic");
                String insmodel = record.getString("insmodel");
                int toolStstus = record.getInt("toolStstus");
                map.put("entryStatus", entryStatus);
                map.put("insname", insname);
                map.put("toolPic", toolPic);
                map.put("insmodel", insmodel);
                map.put("toolStstus", toolStstus);
                list.add(map);
            }
        }
        return list;
    }

    public String getEntryPstatusName(int pstatus) {
        switch (pstatus) {
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

    public String getOrderStatusName(int pstatus) {
        switch (pstatus) {
            case 0:
                return "未完成";
            case 1:
                return "已完成";
            default:
                return "";
        }
    }
    /*public static void main(String[] args) throws ParseException {
        String dateStr = "2018-10-10 13:20";
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
        System.out.println(date);
    }*/
    public void prepareTool(String id) {
        Ins_order order = this.fetch(id);
        if(order != null){
            int pstatus = order.getPstatus();
            if(pstatus != -1){
                throw new ValidatException("此订单状态已备料");
            }
        }
        this.update(Chain.make("pstatus",0),Cnd.where("id","=",id));
    }
    public void prepareTools(String[] ids) {
        List<Ins_order> orders = this.query(Cnd.where("id", "IN", ids));
        if(orders.size() > 0){
            for (Ins_order order : orders) {
                if(order.getPstatus() != -1){
                    throw  new ValidatException("此订单状态已备料!订单编号:["+order.getOrdernum()+"]");
                }
            }
        }
        this.update(Chain.make("pstatus",0),Cnd.where("id","IN",ids));
    }
}
