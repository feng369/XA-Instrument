package cn.wizzer.app.logistics.modules.services.impl;

import cn.wizzer.app.base.modules.models.*;
import cn.wizzer.app.base.modules.services.*;
import cn.wizzer.app.logistics.modules.models.*;
import cn.wizzer.app.logistics.modules.services.*;
import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.app.sys.modules.models.Sys_user;
import cn.wizzer.app.sys.modules.services.SysDictService;
import cn.wizzer.app.sys.modules.services.SysUserService;
import cn.wizzer.app.sys.modules.services.SysWxService;
import cn.wizzer.app.sys.modules.services.SysWxService;
import cn.wizzer.app.web.commons.base.Globals;
import cn.wizzer.app.web.modules.controllers.open.api.Datatime.newDataTime;
import cn.wizzer.framework.base.ValidatException;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import com.beust.jcommander.internal.Sets;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.cri.Static;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.nutz.aop.interceptor.ioc.TransAop.READ_COMMITTED;

@IocBean(args = {"refer:dao"})
public class LogisticsOrderServiceImpl extends BaseServiceImpl<logistics_order> implements LogisticsOrderService {
    @Inject
    private Dao dao;
    @Inject
    private BasePersonService basePersonService;
    @Inject
    private BaseCnctobjService baseCnctobjService;
    @Inject
    private BaseCustomerService baseCustomerService;
    @Inject
    private LogisticsOrderdeliveryService logisticsOrderdeliveryService;
    @Inject
    private SysDictService sysDictService;
    @Inject
    private BasePlaceService basePlaceService;
    @Inject
    private LogisticsDeliveryorderService logisticsDeliveryorderService;
    @Inject
    private LogisticsOrderentryService logisticsOrderentryService;
    @Inject
    private LogisticsOrderstepService logisticsOrderstepService;
    @Inject
    private BasePersonpoolService basePersonpoolService;
    @Inject
    private LogisticsEntourageService logisticsEntourageService;
    @Inject
    private LogisticsDeliveryorderentryService logisticsDeliveryorderentryService;
    @Inject
    private BaseAirportService baseAirportService;
    @Inject
    private LogisticsSendtraceService logisticsSendtraceService;
    @Inject
    private SysUserService sysUserService;
    @Inject
    private SysWxService sysWxService;


    public LogisticsOrderServiceImpl(Dao dao) {
        super(dao);
    }

    public void save(logistics_order logistics_order, String logistics_orderentry) {
        dao().insertWith(logistics_order, logistics_orderentry);
    }

    public logistics_order getLogistics_order(String id) {

        logistics_order logisticsOrder = dao().fetchLinks(dao().fetch(logistics_order.class, id), "logistics_orderentry");
        if (!Strings.isBlank(logisticsOrder.getCustomerid())) {
            base_customer baseCustomer = baseCustomerService.fetch(logisticsOrder.getCustomerid());
            logisticsOrder.setCustomer(baseCustomer);
        }
        if (!Strings.isBlank(logisticsOrder.getBtype())) {
            Sys_dict dict_btype = sysDictService.fetch(logisticsOrder.getBtype());
            logisticsOrder.setDict_btype(dict_btype);
        }
        if (!Strings.isBlank(logisticsOrder.getEmergency())) {
            Sys_dict dict_emergency = sysDictService.fetch(logisticsOrder.getEmergency());
            logisticsOrder.setDict_emergency(dict_emergency);
        }
        if (!Strings.isBlank(logisticsOrder.getStartstock())) {
            base_place place_startstock = basePlaceService.fetch(logisticsOrder.getStartstock());
            logisticsOrder.setPlace_startstock(place_startstock);
        }
        if (!Strings.isBlank(logisticsOrder.getEndstock())) {
            base_place place_endstock = basePlaceService.fetch(logisticsOrder.getEndstock());
            logisticsOrder.setPlace_endstock(place_endstock);
        }
        if (!Strings.isBlank(logisticsOrder.getTransporttype())) {
            Sys_dict dict_transporttype = sysDictService.fetch(logisticsOrder.getTransporttype());
            logisticsOrder.setDict_transporttype(dict_transporttype);
        }
        if (!Strings.isBlank(logisticsOrder.getHctype())) {
            Sys_dict dict_hctype = sysDictService.fetch(logisticsOrder.getHctype());
            logisticsOrder.setDict_hctype(dict_hctype);
        }
        if (!Strings.isBlank(logisticsOrder.getDeliveryorderid())) {
            logisticsOrder.setLogisticsDeliveryorder(logisticsDeliveryorderService.fetch(logisticsOrder.getDeliveryorderid()));
        }
        if (!Strings.isBlank(logisticsOrder.getPersonid())) {
            logisticsOrder.setPerson(basePersonService.fetch(logisticsOrder.getPersonid()));
        }
        return logisticsOrder;
    }

    public List<logistics_order> getdata(Cnd cnd, String linkname) {
        List<logistics_order> list = dao().fetchLinks(dao().query(logistics_order.class, cnd), linkname);
        return list;
    }

    //20180228zhf1409
    public NutMap getJsonDataOfCountOrder(String otype, String airportId) {
        NutMap json = new NutMap();
        int unrecievedOrders = 0;
        int tMonthOrders = 0;
        int overtimeOrders = 0;
        int todayOrders = 0;
        if (!Strings.isBlank(otype)) {
            //this.count("logistics_order", Cnd.where("otype", "=", otype));
            if (!Strings.isBlank(airportId)) {
                //今天订单
                todayOrders = this.count("logistics_order",
                        Cnd.where("otype", "=", otype)
                                .and("createTime", ">=", newDataTime.getIntegerByDate(newDataTime.startOfToday()))
                                .and("createTime", "<=", newDataTime.getIntegerByDate(newDataTime.endOfToday()))
                                .and("airportid", "=", airportId));
                //待接订单
                unrecievedOrders = this.count("logistics_order", Cnd.where("otype", "=", otype).
                        and("pstatus", "<", 3).
                        and("pstatus", ">", 0).
                        and("airportid", "=", airportId));
                //本月订单
                tMonthOrders = this.count("logistics_order", Cnd.where("otype", "=", otype)
                        .and("createTime", ">=", newDataTime.getIntegerByDate(newDataTime.startOfThisMonth()))
                        .and("createTime", "<=", newDataTime.getIntegerByDate(newDataTime.endOfThisMonth()))
                        .and("airportid", "=", airportId));
                //超时订单
                overtimeOrders = this.count("logistics_order", Cnd.where("otype", "=", otype)
                        .and("pstatus", "<>", 8)
                        .and("timerequest", "IS NOT", null)
                        .and("cast(substring(timerequest,1,19) as datetime)", "<", newDataTime.getDateYMDHMS())
                        .and("airportid", "=", airportId));
                json.put("todayOrders", todayOrders);
                json.put("unrecievedOrders", unrecievedOrders);
                json.put("tMonthOrders", tMonthOrders);
                json.put("overtimeOrders", overtimeOrders);
            } else {
                //今天订单
                todayOrders = this.count("logistics_order", Cnd.where("otype", "=", otype).and("createTime", ">=", newDataTime.getIntegerByDate(newDataTime.startOfToday())).and("createTime", "<=", newDataTime.getIntegerByDate(newDataTime.endOfToday())));
                //待接订单
                unrecievedOrders = this.count("logistics_order", Cnd.where("otype", "=", otype).
                        and("pstatus", "<", 3).
                        and("pstatus", ">", 0));
                //本月订单
                tMonthOrders = this.count("logistics_order", Cnd.where("otype", "=", otype)
                        .and("createTime", ">=", newDataTime.getIntegerByDate(newDataTime.startOfThisMonth()))
                        .and("createTime", "<=", newDataTime.getIntegerByDate(newDataTime.endOfThisMonth()))
                );
                //超时订单
                overtimeOrders = this.count("logistics_order", Cnd.where("otype", "=", otype)
                        .and("pstatus", "<", 8)
                        .and("timerequest", "IS NOT", null)
                        .and("cast(substring(timerequest,1,19) as datetime)", "<", newDataTime.getDateYMDHMS())
                );
                json.put("todayOrders", todayOrders);
                json.put("unrecievedOrders", unrecievedOrders);
                json.put("tMonthOrders", tMonthOrders);
                json.put("overtimeOrders", overtimeOrders);
            }
        }
        return json;
    }

    public Map getOrderCountByMobile(String personid, boolean receivedCheck) {

        HashMap map = new HashMap();
        int ordercount = 0;
        int sendingOrders = 0;
        int thisMonthOrders = 0;
        int overTimeOrders = 0;
        int todayOrders = 0;
        //待接订单
        List<logistics_order> logisticsOrders = this.query(Cnd.where("pstatus", "=", 2), "logistics_Deliveryorder");
        if (logisticsOrders.size() > 0) {
            ordercount = logisticsOrders.size();
        }
        map.put("ordercount", ordercount);
        if (receivedCheck) {
            return map;
        }
        if (!Strings.isBlank(personid)) {
            //配送中订单
            Sql sql = Sqls.queryRecord("SELECT lo.id FROM logistics_order lo JOIN  logistics_Deliveryorder ld ON lo.deliveryorderid = ld.id WHERE ld.pstatus = 1");
            dao().execute(sql);
            List<Record> list = sql.getList(Record.class);
            if (list.size() > 0) {
                sendingOrders = list.size();
            }
            //当日订单
            todayOrders = this.count(Cnd.where(new Static("Date(cptime)='" + newDataTime.getCurrentDateStr("yyyy-MM-dd") + "'")).and("pstatus", "=", 8).and("personid", "=", personid));

            //当月订单
            Calendar c = Calendar.getInstance();
            int month = c.get(Calendar.MONTH) + 1;
            int year = c.get(Calendar.YEAR);
            thisMonthOrders = this.count(Cnd.where(new Static("month(cptime)=" + month + " and year(cptime)=" + year)).and("pstatus", "=", 8).and("personid", "=", personid));

            //超时订单
            String currentDate = newDataTime.getDateYMDHMS();
            overTimeOrders = this.count(Cnd.where(
                    "timerequest", "<", currentDate).and("pstatus", "<", 8).and("personid", "=", personid));
        }
        map.put("sendingOrders", sendingOrders);
        map.put("todayOrders", todayOrders);
        map.put("monthOrders", thisMonthOrders);
        map.put("overTimeOrders", overTimeOrders);
        return map;
    }

    public List<Map<String, Object>> getOrderList(String personid, String pstatus, String numorder, String startstock, String endstock, String btype, String deliveryorderid, Integer pagenumber, Integer pagesize) {

        Cnd cnd = Cnd.NEW();
        if (!Strings.isBlank(pstatus))
            cnd.and("pstatus", "=", pstatus);
        if (!Strings.isBlank(numorder))
            cnd.and("ordernum", "like", "%" + numorder + "%");
        if (!Strings.isBlank(startstock))
            cnd.and("startstock", "=", startstock);
        if (!Strings.isBlank(endstock))
            cnd.and("endstock", "=", endstock);
        if (!Strings.isBlank(btype))
            cnd.and("btype", "=", btype);
        if (!Strings.isBlank(deliveryorderid))
            cnd.and("deliveryorderid", "=", deliveryorderid);
        String linkname = "customer|dict_btype|dict_emergency|place_startstock|place_endstock|dict_transporttype|dict_otype|logisticsDeliveryorder|dict_hctype|sendplan";
        Pager pager = new Pager(1);
        if (pagenumber != null && pagenumber.intValue() > 0) {
            pager.setPageNumber(pagenumber.intValue());
        }
        if (pagesize == null || pagesize > 10) {
            pager.setPageSize(10);
        } else {
            pager.setPageSize(pagesize.intValue());
        }

        List<logistics_order> logisticsOrders = this.query(cnd.orderBy("intime", "DESC"), linkname, pager);
        if (Strings.isBlank(deliveryorderid)) {
            for (logistics_order logisticsOrder : logisticsOrders) {
                List<logistics_orderentry> logisticsOrderentrys = logisticsOrderentryService.query(Cnd.where("orderid", "=", logisticsOrder.getId()));
                logisticsOrder.setLogistics_orderentry(logisticsOrderentrys);
            }
        }
        //20180518zhf1156
        List<Map<String, Object>> list = new ArrayList<>();
        for (logistics_order logisticsOrder : logisticsOrders) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", logisticsOrder.getId());
            map.put("ordernum", logisticsOrder.getOrdernum());
            map.put("timerequest", logisticsOrder.getTimerequest());
            map.put("cptime", logisticsOrder.getCptime());
            map.put("endstockphone", logisticsOrder.getEndstockphone());
            map.put("startstockphone", logisticsOrder.getStartstockphone());
            map.put("otype", logisticsOrder.getOtype());
            map.put("otypename", logisticsOrder.getDict_otype() == null ? "" : logisticsOrder.getDict_otype().getName());
            map.put("emergency", logisticsOrder.getEmergency());
            map.put("emergencypath", logisticsOrder.getDict_emergency() == null ? "" : logisticsOrder.getDict_emergency().getPath());
            map.put("emergencyname", logisticsOrder.getDict_emergency() == null ? "" : logisticsOrder.getDict_emergency().getName());

            map.put("startstock", logisticsOrder.getStartstock());
            map.put("startstockname", logisticsOrder.getPlace_startstock() == null ? "" : logisticsOrder.getPlace_startstock().getPlacename());

            map.put("endstock", logisticsOrder.getEndstock());
            map.put("endstockname", logisticsOrder.getPlace_endstock() == null ? "" : logisticsOrder.getPlace_endstock().getPlacename());

            map.put("hctype", logisticsOrder.getHctype());
            map.put("hctypename", logisticsOrder.getDict_hctype() == null ? "" : logisticsOrder.getDict_hctype().getName());
            map.put("btype", logisticsOrder.getBtype());
            map.put("btypename", logisticsOrder.getDict_btype() == null ? "" : logisticsOrder.getDict_btype().getName());
            map.put("pstatus", logisticsOrder.getPstatus());
            map.put("pstatusname", logisticsOrder.getPstatusname());
            list.add(map);
        }

        return list;
    }

    @Aop(READ_COMMITTED)
    public void updataVehicle(String vehicleid, String orderid) {
        logistics_order order = this.fetch(orderid);
        order.setVehicleid(vehicleid);
        this.updateIgnoreNull(order);
    }

    @Aop(READ_COMMITTED)
    public Map updateDvSteps(String orderid, String content, String stepnum, String toreason, String personid) {
        int rslt = 0;
        String deliveryid = null;
        Cnd cnd = Cnd.NEW();
        cnd.and("id", "=", orderid);
        logistics_order order = this.fetch(cnd);
        String otype = null;
        if (order == null) {
            throw new ValidatException("查询不到对应订单");
        }
        otype = order.getOtype();
//        if(order.isOtype()==false){
//            otype=0;
//        }

//        Cnd cnd1 = Cnd.NEW();
//        cnd1.and("stepname", "=", step);
//        cnd1.and("otype", "=", otype);
//        logistics_orderstep orderstep = logisticsOrderstepService.fetch(cnd1);

        //通过otypeid找到logistics_orderstep步骤
        //前端订单类型修改完毕后，传入step值再修改

        int step = getStep(otype, stepnum);
        int newstep = -1;
        if (order != null) {
            Cnd cnd3 = Cnd.NEW();
            cnd3.and("orderid", "=", orderid);
            deliveryid = order.getDeliveryorderid();
            cnd3.and("deliveryorderid", "=", deliveryid);


            String now = newDataTime.getDateYMDHMS();
            if (stepnum.equals("SD") && !Strings.isBlank(order.getTimerequest()))//送达
            {


                Date timeRequest = new Date();
                Date nowDate = new Date();
                DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    timeRequest = sdf.parse(order.getTimerequest());

                } catch (Exception e) {
                    String request = order.getTimerequest() + ":00";
                    try {
                        timeRequest = sdf.parse(request);
                    } catch (Exception ef) {
                        ef.printStackTrace();
                    }

                }
                try {
                    nowDate = sdf.parse(now);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //20180529cw
                //送达即送达
                //需求端需求人点击确认收货，才叫完成
//                if (nowDate.getTime() < timeRequest.getTime()) {//在超时之前
//                    stepnum = "WC";
//                    newstep = getStep(otype, stepnum);
//                }


            }
            if (newstep > -1)
                cnd3.and("step", "in", step + "," + newstep);//0908cw修改，如果是按时送达，则直接完成
            else
                cnd3.and("step", "=", step);

            rslt = logisticsDeliveryorderentryService.upDventry(1, content, toreason, now, personid, cnd3);
            //如果是送达状态，则需要修改订单状态为送达

            //1修改订单状态为送达
            //2检查订单所在配送单的所有单据状态
            //3将配送单状态改为最小状态，依次为配送中-送达未完成-已完成
            if (stepnum.equals("HL")) {
                order.setPstatus(6);
            } else if (stepnum.equals("SD")) {
                order.setPstatus(7);
            } else if (stepnum.equals("WC")) {
                order.setPstatus(8);
                order.setCptime(newDataTime.getDateYMDHMS());
                order.setReceivePersonId(personid);
            }
            rslt += this.updateIgnoreNull(order);
            Cnd cnd2 = Cnd.NEW();
            cnd2.and("deliveryorderid", "=", deliveryid);
            List<logistics_orderdelivery> orderdeliveryList = logisticsOrderdeliveryService.query(cnd2);
            if (orderdeliveryList.size() > 1) {
                //说明存在并单情况
                int status = 0;
                for (int i = 0; i < orderdeliveryList.size(); i++) {
                    logistics_order logisticsOrder = this.fetch(orderdeliveryList.get(i).getOrderid());
                    if (status > logisticsOrder.getPstatus()) {
                        status = logisticsOrder.getPstatus();
                    }
                }
                logistics_Deliveryorder deliveryorder = logisticsDeliveryorderService.fetch(deliveryid);
                if (status > 3 && status < 7) {
                    deliveryorder.setPstatus(1);
                    //配送中
                } else if (status == 7) {
                    //送达
                    deliveryorder.setPstatus(2);


                } else if (status == 8) {
                    //完成
                    deliveryorder.setPstatus(3);
                    deliveryorder.setCptime(newDataTime.getDateYMDHMS());
                } else {
                    //错误
                }
                rslt += logisticsDeliveryorderService.updateIgnoreNull(deliveryorder);
            } else {
                logistics_Deliveryorder deliveryorder = logisticsDeliveryorderService.fetch(deliveryid);
                if (stepnum.equals("SD")) {
                    //送达
                    deliveryorder.setPstatus(2);
                    Set<String> sets = new HashSet<>();
                    if (!Strings.isBlank(order.getPersonid())) {
                        base_cnctobj bc = baseCnctobjService.fetch(Cnd.where("personId", "=", order.getPersonid()));
                        String userId = bc.getUserId();
                        if (!Strings.isBlank(userId)) {
                            sets.add(userId);
                            String endstock = order.getEndstock();
                            if(!Strings.isBlank(endstock)){
                                base_place basePlace = basePlaceService.fetch(endstock);
                                if(basePlace != null){
                                    String senpPid = deliveryorder.getPersonid();
                                    if(!Strings.isBlank(senpPid)){
                                            base_person sendPerson = basePersonService.fetch(senpPid);
                                            String personname = null;
                                            if(sendPerson != null){
                                                personname = sendPerson.getPersonname();
                                            }else{
                                                personname ="";
                                            }
                                            sysWxService.sendWxMessageAsy(sets,"您的订单已到达"+basePlace.getPlacename()+",请及时签收!配送员["+personname+"],联系电话:"+sendPerson.getTel());
                                    }
                                }
                            }

                        }
                    }


                } else if (stepnum.equals("WC")) {
                    //完成
                    deliveryorder.setPstatus(3);
                    deliveryorder.setCptime(newDataTime.getDateYMDHMS());
                } else if (stepnum.equals("HL")) {
                    //配送中
                    deliveryorder.setPstatus(1);
//                        deliveryorder.setCptime(newDataTime.getDateYMDHMS());
                } else {
                    //错误
                }
                rslt += logisticsDeliveryorderService.updateIgnoreNull(deliveryorder);
            }

        }
        if (stepnum.equals("WC")) {

            if (!Strings.isBlank(personid)) {
                Cnd cnd4 = Cnd.NEW();
                cnd4.and("personid", "=", personid);
                cnd4.and("pstatus", "<>", "3");
                List<logistics_Deliveryorder> deliveryorderList = logisticsDeliveryorderService.query(cnd4);
                if (deliveryorderList.size() == 0) {//说明该配送员所有运单都已经完成，则该配送员空闲
                    Cnd cnd5 = Cnd.NEW();
                    cnd5.and("personid", "=", personid);
                    base_personpool personpool = basePersonpoolService.fetch(cnd5);
                    if (personpool != null) {
                        personpool.setPersonstatus(0);
                        basePersonpoolService.updateIgnoreNull(personpool);
                    }

                    //随行人员也要空闲
                    Cnd cnd6 = Cnd.NEW();
                    cnd6.and("orderid", "=", orderid);
                    List<logistics_entourage> entourageList = logisticsEntourageService.query(cnd);
                    for (int i = 0; i < entourageList.size(); i++) {
                        String enpid = entourageList.get(i).getPersonid();
                        Cnd cnd7 = Cnd.NEW();
                        cnd7.and("personid", "=", enpid);
                        personpool = basePersonpoolService.fetch(cnd7);
                        if (personpool != null) {
                            personpool.setPersonstatus(0);
                            basePersonpoolService.updateIgnoreNull(personpool);
                        }

                    }
                }

            }
        }
        Map map = new HashMap();
        map.put("rslt", rslt);
        return map;
    }


    private int getStep(String otype, String stepnum) {
        Cnd c = Cnd.NEW();
        c.and("otype", "=", otype);
        c.and("stepnum", "=", stepnum);
        logistics_orderstep orderstep = logisticsOrderstepService.fetch(c);
        int step = 0;
        if (orderstep != null) {

            step = orderstep.getStep();
        }
        return step;
    }

    @Aop(TransAop.READ_COMMITTED)
    public void addelDo(String orderid, String personid, String curPostion) {
        //更新订单
        logistics_order logisticsOrder = this.getLogistics_order(orderid);
        logisticsOrder.setPstatus(3);
        this.updateIgnoreNull(logisticsOrder);
        //将接单人更新到配送单中 cw0909
        //更新接单人状态为忙碌
        String dvid = logisticsOrder.getDeliveryorderid();
        logistics_Deliveryorder deliveryorder = logisticsDeliveryorderService.fetch(dvid);
        if (deliveryorder != null) {
            deliveryorder.setPersonid(personid);
            deliveryorder.setPstatus(1);
            logisticsDeliveryorderService.updateIgnoreNull(deliveryorder);
            Cnd cnd = Cnd.NEW();
            cnd.and("personid", "=", personid);
            base_personpool personpool = basePersonpoolService.fetch(cnd);
            if (personpool != null) {
                personpool.setPersonstatus(1);
                basePersonpoolService.updateIgnoreNull(personpool);
            }
        }
        if(curPostion==null){
            curPostion="113.822275,22.64526";
        }
        logisticsSendtraceService.addOne(deliveryorder, logisticsOrder, curPostion);
        String jd = logisticsOrderstepService.getStepByStepnum("JD", "72dd55d2c3cc42799c4e014745db2cdb");

        //更新跟踪单
        if (!Strings.isBlank(dvid)) {
            Cnd cnd = Cnd.NEW();
            cnd.and("orderid", "=", orderid);
            cnd.and("deliveryorderid", "=", dvid);
            cnd.and("step", "=", jd);
            String now = newDataTime.getDateYMDHMS();
            logisticsDeliveryorderentryService.upDventry(1, "", "", now,personid, cnd);
        }
    }


    public Map<String, Object> getOrderInfo(String id) {
        Map<String, Object> map = new HashMap<>();
        logistics_order logisticsOrder = this.fetch(id);
        logisticsOrder = this.fetchLinks(logisticsOrder, "logistics_orderentry|dict_otype|dict_btype|dict_emergency|place_startstock|place_endstock|dict_transporttype|dict_hctype|person|sendplan|customer|receivePerson");
        if (logisticsOrder != null) {
            if (!Strings.isBlank(logisticsOrder.getCustomerid())) {
                map.put("id", logisticsOrder.getId());
                map.put("customername", logisticsOrder.getCustomer() == null ? "" : logisticsOrder.getCustomer().getCustomername());
                map.put("otype", logisticsOrder.getOtype());
                map.put("otypename", logisticsOrder.getDict_otype() == null ? "" : logisticsOrder.getDict_otype().getName());
                map.put("btypename", logisticsOrder.getDict_btype() == null ? "" : logisticsOrder.getDict_btype().getName());
                map.put("emergencyname", logisticsOrder.getDict_emergency() == null ? "" : logisticsOrder.getDict_emergency().getName());
                map.put("isPackage", logisticsOrder.isPackage());
                map.put("startstockplacename", logisticsOrder.getPlace_startstock() == null ? "" : logisticsOrder.getPlace_startstock().getPlacename());
                map.put("endstockplacename", logisticsOrder.getPlace_endstock() == null ? "" : logisticsOrder.getPlace_endstock().getPlacename());
                map.put("startstockphone", logisticsOrder.getStartstockphone());
                map.put("endstockphone", logisticsOrder.getEndstockphone());
                map.put("transporttypename", logisticsOrder.getDict_transporttype() == null ? "" : logisticsOrder.getDict_transporttype().getName());
                map.put("airwaybillno", logisticsOrder.getAirwaybillno() == null ? "" : logisticsOrder.getAirwaybillno());
                map.put("contractnum", logisticsOrder.getContractnum());
                map.put("pstatus", logisticsOrder.getPstatus());
                map.put("pstatusname", logisticsOrder.getPstatusname());
                map.put("timerequest", logisticsOrder.getTimerequest());
                map.put("estimatetime", logisticsOrder.getEstimatetime());
                map.put("ordernum", logisticsOrder.getOrdernum());
                map.put("note", logisticsOrder.getNote());
                map.put("hctypename", logisticsOrder.getDict_hctype() == null ? "" : logisticsOrder.getDict_hctype().getName());
                map.put("startstockposition", logisticsOrder.getPlace_startstock() == null ? "" : logisticsOrder.getPlace_startstock().getPosition());
                map.put("endstockposition", logisticsOrder.getPlace_endstock() == null ? "" : logisticsOrder.getPlace_endstock().getPosition());
                map.put("receiver", logisticsOrder.getReceivePerson() == null ? "" : logisticsOrder.getReceivePerson().getPersonname());
            }
        }
        return map;
    }

    @Aop(TransAop.READ_COMMITTED)
    public void updateOrderReject(String id) {
        logistics_order order = this.fetch(id);
        order.setPstatus(99);
        this.updateIgnoreNull(order);
    }

    public Map getOrderPstatus(String id) {
        logistics_order logisticsOrder = this.fetch(id);
        Map map = new HashMap();
        if (logisticsOrder != null) {
            map.put("pstatus", logisticsOrder.getPstatus());
        }
        return map;
    }

    //起点位置,目的地,当前人的位置,order当前的状态
    public List<Map<String, Object>> getOrderListOfCustomer(String personid) {
        List<logistics_order> logisticsOrders = this.query(Cnd.where("personid", "=", personid).and("pstatus", ">=", "3").and("pstatus", "<=", "7"), "place_endstock");
        List<Map<String, Object>> list = new ArrayList<>();
        for (logistics_order logisticsOrder : logisticsOrders) {
            Map<String, Object> map = new HashMap<>();
            if (logisticsOrder != null) {
                List<logistics_sendtrace> sendtraces = logisticsSendtraceService.query(Cnd.where("orderid", "=", logisticsOrder.getId()).orderBy("intime", "DESC"));
                if (sendtraces.size() > 0) {
                    logistics_sendtrace logisticsSendtrace = sendtraces.get(0);
                    map.put("curPosition", logisticsSendtrace.getPosition()==null?"":logisticsSendtrace.getPosition());

                } else {
                    map.put("curPosition", "");
                }
                map.put("pstatus", logisticsOrder.getPstatus());
                map.put("orderid", logisticsOrder.getId());
                /*
                map.put("startstockname", logisticsOrder.getPlace_startstock() == null ? "" : logisticsOrder.getPlace_startstock().getPlacename());
                * */
                map.put("endstockname", logisticsOrder.getPlace_endstock() == null ? "" : logisticsOrder.getPlace_endstock().getPlacename());
                list.add(map);
            }
        }
        return list;
    }

    //通过orderid,得到pstus,psname,出发.目的地,运单personname,随行人员,车辆,接单时间

    //   logistics_Deliveryorderentry
    //    4e6054a2b0d546c3a397f96977c047c4	f638636340504f32aa0617d9525089c8	a1bf5751ada448198b2a572384eb2bd3		3	配送员已接单						0	fcacccf28101427295211522f7beb7cd	1527834307	fcacccf28101427295211522f7beb7cd	1527834307	0
    public Map getOrderInfoOfCustomer(String orderid) {
        Map map = new HashMap();
        String sqlStr = "SELECT lo.pstatus,bp.placename as startplace,pl.placename as endplace,pe.personname as sendperson,pe.tel,bv.vehiclenum,sd.`name` as hctypename   FROM logistics_order lo LEFT JOIN base_place bp ON lo.startstock = bp.id \n" +
                "LEFT JOIN base_place pl ON lo.endstock = pl.id LEFT JOIN logistics_Deliveryorder ld ON lo.deliveryorderid = ld.id LEFT JOIN base_person pe ON ld.personid = pe.id\n" +
                "LEFT JOIN base_vehicle bv ON lo.vehicleid = bv.id left join sys_dict sd on lo.hctype=sd.id WHERE lo.id = '" + orderid + "'";
        Sql sql = Sqls.fetchRecord(sqlStr);
        dao().execute(sql);
        Record record = sql.getObject(Record.class);
        if (record != null) {
            map.put("pstatus", record.getString("pstatus") == null ? "" : record.getString("pstatus"));
            map.put("startplace", record.getString("startplace") == null ? "" : record.getString("startplace"));
            map.put("endplace", record.getString("endplace") == null ? "" : record.getString("endplace"));
            map.put("sendperson", record.getString("sendperson") == null ? "" : record.getString("sendperson"));
            map.put("tel", record.getString("tel") == null ? "" : record.getString("tel"));
            map.put("vehiclenum", record.getString("vehiclenum") == null ? "" : record.getString("vehiclenum"));
            map.put("hctypename", record.getString("hctypename") == null ? "" : record.getString("hctypename"));
        } else {
            map.put("pstatus", "");
            map.put("startplace", "");
            map.put("endplace", "");
            map.put("sendperson", "");
            map.put("tel", "");
            map.put("vehiclenum", "");
            map.put("hctypename", "");
        }
        logistics_order logisticsOrder = this.fetch(orderid);
        if (logisticsOrder != null) {
            map.put("pstatusname", logisticsOrder.getPstatusname() == null ? "" : logisticsOrder.getPstatusname());
        }
        //后续优化：下面两个其实也可写入上叙sql查询
        //随行人员
        List<logistics_entourage> logisticsEntourages = logisticsEntourageService.query(Cnd.where("orderid", "=", orderid), "person");
        StringBuilder sb = new StringBuilder(80);
        if (logisticsEntourages.size() > 0) {
            sb.append("");

            for (logistics_entourage logisticsEntourage : logisticsEntourages) {
                sb.append(logisticsEntourage.getPerson() == null ? "" : logisticsEntourage.getPerson().getPersonname()).append(" ");
            }
        }

        map.put("followername", sb.toString());
        //接单时间 logistics_Deliveryorderentry
        List<logistics_Deliveryorderentry> logisticsDeliveryorderentries = logisticsDeliveryorderentryService.query(Cnd.where("orderid", "=", orderid));
        if (logisticsDeliveryorderentries.size() > 0) {
            for (logistics_Deliveryorderentry deliveryorderentry : logisticsDeliveryorderentries) {
                //订单类型：航线运输
                Sys_dict sysDict = sysDictService.fetch(Cnd.where("name", "=", "航线运输"));
                if (sysDict != null) {
                    //订单类型：航线运输
                    String stepnum = logisticsOrderstepService.getStepByStepnum("JD", sysDict.getId());
                    if (!Strings.isBlank(stepnum) && (Integer.parseInt(stepnum) == deliveryorderentry.getStep())) {
                        map.put("receiveOrderTime", deliveryorderentry.getOperatetime() == null ? "" : deliveryorderentry.getOperatetime());
                    }
                }
            }
        } else {
            map.put("receiveOrderTime", "");
        }

        return map;
    }

    public List<Map<String, Object>> getOrderListByPersonId(String personid, Integer pagenumber, Integer pagesize) {
        Pager pager = new Pager
                (1);
        if (pagenumber != null && pagenumber.intValue() > 0) {
            pager.setPageNumber(pagenumber.intValue());
        }
        if (pagesize == null || pagesize > 10) {
            pager.setPageSize(10);
        } else {
            pager.setPageSize(pagesize.intValue());
        }
        List<logistics_order> orders = this.query(Cnd.where("personid", "=", personid), "place_startstock|place_endstock|dict_btype|dict_hctype|receivePerson", pager);
        List<Map<String, Object>> list = new ArrayList<>();
        if (orders.size() > 0) {
            for (logistics_order order : orders) {
                Map<String, Object> map = new HashMap<>();
                map.put("orderid", order.getId());
                map.put("btypename", order.getDict_btype() == null ? "" : order.getDict_btype().getName());
                map.put("btype", order.getBtype());
/*
                map.put("otypename",order.getDict_otype() == null ? "" :order.getDict_otype().getName() );
                map.put("otype",order.getOtype());
*/
                map.put("hctype", order.getHctype());
                map.put("hctypename", order.getDict_hctype() == null ? "" : order.getDict_hctype().getName());
                /*   map.put("startPlace", order.getPlace_startstock() == null ? "" : order.getPlace_startstock().getPlacename());*/
                map.put("endPlace", order.getPlace_endstock() == null ? "" : order.getPlace_endstock().getPlacename());
                map.put("pstatus", order.getPstatus());
                map.put("pstatusName", order.getPstatusname());
                map.put("opTimeType", getOpTimeType(order.getPstatus()));
                map.put("receiver",order.getReceivePerson()==null?"":order.getReceivePerson().getPersonname());
                List<logistics_Deliveryorderentry> deliveryorderentries = logisticsDeliveryorderentryService.query(Cnd.where("orderid", "=", order.getId()).and("pstatus", "=", 1).desc("step"));
                if (deliveryorderentries.size() > 0) {
                    //获取当前订单状态的logistics_Deliveryorderentry()
                    logistics_Deliveryorderentry logisticsDeliveryorderentry = deliveryorderentries.get(0);
//                    Sys_dict sysDict = sysDictService.fetch(Cnd.where("name", "=", "航线运输"));
                    Integer curStep = logisticsDeliveryorderentry.getStep();
                    if (curStep != null) {
                        if (1 <= curStep && 3 > curStep) {
                            putOpTime2Map(deliveryorderentries, "CJXQ", map, order.getOtype());
                        } else if (3 <= curStep && 7 > curStep) {
                            putOpTime2Map(deliveryorderentries, "JD", map, order.getOtype());
                        } else if (7 == curStep) {
                            map.put("opTime", logisticsDeliveryorderentry.getOperatetime());
                        } else if (8 == curStep) {
                            map.put("opTime", logisticsDeliveryorderentry.getOperatetime());
                        } else {
                            map.put("opTime", "");
                        }
                    } else {
                        throw new ValidatException("当前订单步骤不能为空");
                    }
                }
                list.add(map);
            }
        }


        return list;
    }

    private void putOpTime2Map(List<logistics_Deliveryorderentry> deliveryorderentries, String stepCode, Map<String, Object> map, String otype) {
        String stepnum = logisticsOrderstepService.getStepByStepnum(stepCode, otype);
        for (logistics_Deliveryorderentry deliveryorderentry : deliveryorderentries) {
            if (!Strings.isBlank(stepnum) && (Integer.parseInt(stepnum) == deliveryorderentry.getStep())) {
                map.put("opTime", deliveryorderentry.getOperatetime() == null ? "" : deliveryorderentry.getOperatetime());
            }
        }
    }

    public String getOpTimeType(int pstatus) {
        if (1 <= pstatus && 3 > pstatus) {
            return "订单时间";
        } else if (3 <= pstatus && 7 > pstatus) {
            return "接单时间";
        } else if (7 == pstatus) {
            return "送达时间";
        } else if (8 == pstatus) {
            return "收货时间";
        }
        return "";
    }

    @Aop(TransAop.READ_COMMITTED)
    public void addOrderAndOrderEntry(String logisticsOrder, String logisticsOrderentry) throws ParseException {
        String num = "";
        logistics_order order = Json.fromJson(logistics_order.class, logisticsOrder);
        if (order != null) {
            String newtimerequest = order.getTimerequest();
            String newintime = order.getIntime();
            String estimatetime = order.getEstimatetime();
            if (newtimerequest != null) {
                newtimerequest = newtimerequest.replace('+', ' ');
                order.setTimerequest(newtimerequest);
            }
            if (newintime != null) {
                newintime = newintime.replace('+', ' ');
                order.setIntime(newintime);
            }
            if (estimatetime != null) {
                estimatetime = estimatetime.replace('+', ' ');
                order.setEstimatetime(estimatetime);
            }

            List<logistics_orderentry> logistics_orderentry = Json.fromJsonAsList(logistics_orderentry.class, logisticsOrderentry);
            order.setLogistics_orderentry(logistics_orderentry);
            //自动生成订单编号

//            String customerid = order.getCustomerid();
//            base_customer base_customer = baseCustomerService.fetch(customerid);

            /*
            String intime = order.getIntime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = formatter.parse(intime);
            intime = formatter.format(date);

            Cnd cnd = Cnd.NEW();
            cnd.and("DATE_FORMAT( intime, '%Y-%m-%d')", "=", intime);
            cnd.and("customerid", "=", customerid);
            List<logistics_order> logisticsOrders = this.query(cnd.orderBy("intime", "ASC"));
        ArrayList list = new ArrayList();
            if (logisticsOrders.size() == 0) {
                num = base_customer.getCusnum() + "-" + intime + "-0001";
            } else {
                for (logistics_order lo : logisticsOrders) {
                    //0009
                    String _num = lo.getOrdernum().substring(lo.getOrdernum().lastIndexOf("-") + 1, lo.getOrdernum().length());
                    //9
                    String newStr = _num.replaceFirst("^0*", "");

                    list.add(newStr);
                }
//                Collections.sort(list);
                int n = Integer.parseInt(list.get(list.size() - 1).toString());
                num = String.format("%04d", n + 1);
                num = base_customer.getCusnum() + "-" + intime + "-" + num;
            }*/

        }
        order.setOrdernum(getOrderNum(Globals.cutomerid,order.getIntime()));
        this.save(order, "logistics_orderentry");
    }


    //添加物料
    @Aop(TransAop.READ_COMMITTED)
    public Map<String, Object> addOrderEntryByMobiile(String materielid, String materielname, String materielnum, String sequencenum, String batch, String quantity, String pnr, String sn, String stock, String location, String orderflag, String lorderno, String state) {
        Map<String, Object> map = new HashMap<>();

        logistics_orderentry orderentry = new logistics_orderentry();
        orderentry.setBatch(batch);
        orderentry.setMaterielname(materielname);
        orderentry.setSequencenum(sequencenum);
        orderentry.setMaterielnum(materielnum);
        orderentry.setQuantity(quantity);
        orderentry.setMaterielid(materielid);
        orderentry.setPnr(pnr);
        orderentry.setSn(sn);
        orderentry.setStock(stock);
        orderentry.setLocation(location);
        orderentry.setOrderflag(orderflag);
        orderentry.setLorderno(lorderno);
        orderentry.setOrderstate(state);
        orderentry = logisticsOrderentryService.insert(orderentry);
        map.put("orderentryId", orderentry.getId());
        return map;
    }

    @Aop(TransAop.READ_COMMITTED)
    public void receiveOrderBy2Code(String personId, String orderId) {

        //1.订单
        Map<String, Object> stepMap = logisticsOrderstepService.getStepByMobile(orderId, "WC");
        Integer step = null;
        if (stepMap.size() == 1) {
            step = (Integer) stepMap.get("step");
        }
        if (!Strings.isBlank(orderId)) {
            logistics_order order = this.fetch(orderId);
            if (order != null) {
                if (!Strings.isBlank(personId)) {
                    base_person basePerson = basePersonService.fetch(personId);
                    if (basePerson != null) {
                        order.setReceivePersonId(basePerson.getId());
                        if (step != null) {
                            order.setPstatus(step);
                            this.update(Chain.make("pstatus", step).add("receivePersonId", basePerson.getId()), Cnd.where("id", "=", orderId));
                            //2.配送步骤状态的step和pstatus变为相应值
                            //2.1. pstatus变为1
                            //2.2 operatetime
                            //2.3 stepPersonId
                            List<logistics_Deliveryorderentry> deliveryorderentries = logisticsDeliveryorderentryService.query(Cnd.where("orderid", "=", order.getId()).and("step", "=", step));
                            if (deliveryorderentries.size() > 0) {
                                logistics_Deliveryorderentry deliveryorderentry = deliveryorderentries.get(0);
                                logisticsDeliveryorderentryService.update(Chain.make("pstatus", 1).add("operatetime", newDataTime.getDateYMDHMS()).add("setpPersonId", basePerson.getId()), Cnd.where("id", "=", deliveryorderentry.getId()));
                            }
                        } else {
                            throw new ValidatException("找不到订单当前状态");
                        }
                    } else {
                        throw new ValidatException("找不到收货人员ID");
                    }
                }
            }
        }
    }


    @Aop(TransAop.READ_COMMITTED)
    public Map<String, Object> addOrderByMobile(String btype, String emergency, String hctype, String startstock, String endstock, String customerid, String personid, String userid) throws ParseException {
        logistics_order order = new logistics_order();
        order.setBtype(btype);
        order.setEmergency(emergency);
        order.setHctype(hctype);
        order.setStartstock(startstock);
        order.setEndstock(endstock);
        if (!Strings.isBlank(emergency)) {
            Sys_dict sysDict = sysDictService.fetch(emergency);
            if (sysDict != null && !Strings.isBlank(sysDict.getCode())) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar c = Calendar.getInstance();
                if ("emergency.nomal".equals(sysDict.getCode())) {
                    c.add(Calendar.SECOND, Globals.normal);
                    order.setTimerequest(df.format(c.getTime()));
                } else if ("emergency.urgent".equals(sysDict.getCode())) {
                    c.add(Calendar.SECOND, Globals.emergent);
                    order.setTimerequest(df.format(c.getTime()));
                } else if ("emergency.aog".equals(sysDict.getCode())) {
                    c.add(Calendar.SECOND, Globals.AOG);
                    order.setTimerequest(df.format(c.getTime()));
                } else {
                    order.setTimerequest(null);
                }
            }
        }





        /*if (!Strings.isBlank(timerequest)) {

//            timerequest = timerequest.replace('+', ' ');
            order.setTimerequest(timerequest);
        }*/
        //intime

        order.setIntime(newDataTime.getDateYMDHMS());
        order.setOpBy(userid);
        order.setOpAt((int) (System.currentTimeMillis() / 1000));
        //先写定,深航
        order.setCustomerid(Globals.cutomerid);
        order.setPersonid(personid);
        base_person basePerson = basePersonService.fetch(Cnd.where("id", "=", personid));
        if (basePerson != null) {
            order.setAirportid(basePerson.getAirportid());
        }
        order.setOtype(sysDictService.getDictOtypeId());
        String stepnum = logisticsOrderstepService.getStepByStepnum("CJXQ", order.getOtype());
        if (!Strings.isBlank(stepnum)) {
            order.setPstatus(Integer.parseInt(stepnum));
        }
        //生成订单编号
        order.setOrdernum(getOrderNum(Globals.cutomerid, order.getIntime()));
        order = this.insert(order);
        //生成配送单
        if (logisticsDeliveryorderService.checkorderre(order.getId())) {
            logisticsDeliveryorderService.orderexe(order.getId());
        }
        if(!Strings.isBlank(userid)){
            Set<String> sets = new HashSet<>();
            sets.add(userid);
            sysWxService.sendWxMessageAsy(sets,"您已成功提交订单,请耐心等待!"+"订单编号:"+order.getOrdernum()+",订单预计送达时间:"+order.getTimerequest());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("orderid", order.getId());
        return map;
    }


    //生成订单编号
    public String getOrderNum(String customerid, String intime) throws ParseException {
        String num = "";
        base_customer customer = baseCustomerService.fetch(customerid);
        if (customer != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = formatter.parse(intime);
            intime = formatter.format(date);
            List<logistics_order> logisticsOrders = this.query(Cnd.where("DATE_FORMAT( intime, '%Y-%m-%d')", "=", intime).and("customerid", "=", customerid).orderBy("intime", "ASC"));
            ArrayList list = new ArrayList();
            if (logisticsOrders.size() == 0) {
                num = customer.getCusnum() + "-" + intime + "-0001";
            } else {
                for (logistics_order lo : logisticsOrders) {
                    //0009
                    String _num = lo.getOrdernum().substring(lo.getOrdernum().lastIndexOf("-") + 1, lo.getOrdernum().length());
                    //9
                    String newStr = _num.replaceFirst("^0*", "");
                    list.add(newStr);
                }
                int n = Integer.parseInt(list.get(list.size() - 1).toString());
                num = String.format("%04d", n + 1);
                num = customer.getCusnum() + "-" + intime + "-" + num;
            }
        }
        return num;
    }

    //监听是否扫码签收
    public Map<String, Object> ListenOrderid(String orderid) {
        int number=0;
        Map<String, Object> map = new HashMap<>();
        if (!Strings.isBlank(orderid)) {
            Cnd cnd = Cnd.NEW();
            cnd.and("id", "=", orderid);
            cnd.and("receivePersonId", "<>", "null");
            number = this.count(cnd);
            map.put("number", number);
        }else{
            map.put("number", 0);
        }
        return map;
    }

    //检查是否签收
    public Map<String, Object> checkIsOver(String orderid) {
        int number=0;
        Map<String, Object> map = new HashMap<>();
        if (!Strings.isBlank(orderid)) {
            logistics_order order = this.fetch(orderid);
            map.put("receive", order.getReceivePersonId()==null?"":order.getReceivePersonId());
        }else{
            map.put("receive", "");
        }
        return map;
    }

}
