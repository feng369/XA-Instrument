package cn.wizzer.app.ins.modules.services.impl;

import cn.wizzer.app.ins.modules.models.Ins_order;
import cn.wizzer.app.ins.modules.models.Ins_orderentry;
import cn.wizzer.app.ins.modules.services.*;
import cn.wizzer.app.web.commons.base.Globals;
import cn.wizzer.app.web.modules.controllers.open.api.Datatime.newDataTime;
import cn.wizzer.framework.base.ValidatException;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.ins.modules.models.Ins_repair;
import cn.wizzer.framework.page.OffsetPager;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

@IocBean(args = {"refer:dao"})
public class InsRepairServiceImpl extends BaseServiceImpl<Ins_repair> implements InsRepairService {
    public InsRepairServiceImpl(Dao dao) {
        super(dao);
    }

    @Inject
    private InsInstrumentService insInstrumentService;
    @Inject
    private InsOrderService insOrderService;
    @Inject
    private InsOrderentryService insOrderentryService;
    @Inject
    private InsRepairtrackService insRepairtrackService;
    @Aop(TransAop.READ_COMMITTED)
    public Map<String, Object> insertInsRepair(String orderId, String orderentryId, String describ, String repairType, String personId) {
        if (Strings.isBlank(orderId)) {
            throw new ValidatException("传入订单ID为空");
        }
    /*    if (Strings.isBlank(insId)) {
            throw new ValidatException("传入实体工具ID为空");
        }*/
        if (Strings.isBlank(orderentryId)) {
            throw new ValidatException("传入订单明细ID为空");
        }
        if (Strings.isBlank(personId)) {
            throw new ValidatException("传入维修人ID为空");
        }
        //已经存在该工具的维修申报对象(非正常)
        List<Ins_order> orders = insOrderService.query(Cnd.where("id", "=", orderId));
        if(orders.size() != 1){
            throw new ValidatException("不能确定唯一的订单");
        }
        if(orders.get(0).getPstatus() == 1){
            //订单已完成
            throw new ValidatException("此订单已完成,不能进行维修申报操作!");
        }
        List<Ins_orderentry> orderentries = insOrderentryService.query(Cnd.where("id", "=", orderentryId));
        if(orderentries.size() != 1){
            throw new ValidatException("不能确定唯一的订单明细");
        }
        Ins_orderentry orderentry = orderentries.get(0);
        if(orderentry.getPstatus() == 1){
            throw new ValidatException("待借用订单明细,不能确定具体维修工具!");
        }
        if(orderentry.getPstatus() == 3){
            throw new ValidatException("当前工具已归还,不能进行维修申报操作!");
        }
        List<Ins_repair> insRepairs = this.query(Cnd.where("insId", "=", orderentry.getInsId()).and("pstatus", "<>", 2));
        if (insRepairs.size() > 0) {
            throw new ValidatException("该工具已经在[" + (insRepairs.get(0).getPstatus() == 0 ? "申请维修" : "维修") + "]中!");
        }
        Ins_repair repair = new Ins_repair();
        repair.setInsId(orderentry.getInsId());
        repair.setOrderId(orderId);
        repair.setOrderentryId(orderentryId);
        repair.setDescrib(describ);
        repair.setInTime(newDataTime.getDateYMDHMS());
        repair.setPersonId(personId);
        repair.setRepairType(repairType);
        //状态(默认申请中)
        String repairId = this.insert(repair).getId();
        //生成一条对应的维修历史记录
        insRepairtrackService.insertTrackRecord(repairId,personId,newDataTime.getDateYMDHMS(),0);
        Map<String, Object> map = new HashMap<>();
        map.put("repairId", repairId);
        return map;
    }

    @Aop(TransAop.READ_COMMITTED)
    public void uploadRepairImg(String filename, String base64, String repairId ) throws Exception {
        byte[] buffer=null;
        List<Ins_repair> repairs = this.query(Cnd.where("id", "=", repairId));
        if(repairs.size() != 1){
            throw new ValidatException("不能确定唯一的维修申报对象!");
        }
        Ins_repair repair = repairs.get(0);
        String fn= R.UU32()+filename.substring(filename.lastIndexOf("."));
        String path = Globals.AppUploadPath+"/repair/";
        String pathfile = Globals.AppUploadPath+"/repair/" + fn ;
        File file=new File(Globals.AppRoot+path);
        if(!file.exists()){
            file.mkdirs();
        }
        if(base64.indexOf(",")>=0){//兼容H5
            buffer = Base64.getDecoder().decode(base64.split(",")[1]);
        }else{
//                    buffer = Base64.getDecoder().decode(base64);
            buffer = org.apache.commons.codec.binary.Base64.decodeBase64(base64);
        }
        FileOutputStream out = new FileOutputStream(Globals.AppRoot+pathfile);
        out.write(buffer);
        out.close();
//将上传的文件修改对应用户照片名称
        String imgPath=repair.getImgPath();
        if(!Strings.isBlank(imgPath)){
            imgPath+=","+pathfile;
        }else{
            imgPath=pathfile;
        }
        String imgName = repair.getImgName();
        if(!Strings.isBlank(imgName)){
            imgName+=","+fn;
        }else{
            imgName=fn;
        }
        this.update(Chain.make("imgPath",imgPath).add("imgName",imgName),Cnd.where("id","=",repairId));
    }

    public NutMap getRepairList(String completeValue, int length, int start, int draw, List<DataTableOrder> orders, List<DataTableColumn> columns, Cnd cnd) {
        NutMap re = new NutMap();
        String sqlStr = " SELECT \n" +
                "\trp.id,rp.inTime,rp.describ,rp.pstatus,\n" +
                "\tio.ordernum,\n" +
                "\tins.insnum,\n" +
                "\tib.insname,\n" +
                "\tbp.personname \n" +
                "\tFROM ins_repair rp \n" +
                "\tLEFT JOIN ins_instrument ins ON rp.insId\t = ins.id\t\n" +
                "\tLEFT JOIN ins_order io ON rp.orderId = io.id\t\n" +
                "\tLEFT JOIN ins_orderentry  od ON rp.orderentryId = od.id \n" +
                "\tLEFT JOIN ins_baseins ib ON ib.id = ins.baseInsId \n" +
                "\tLEFT JOIN base_person bp ON rp.personId = bp.id \n" +
                "\t   ";
            if(!Strings.isBlank(completeValue)){
                    sqlStr += " WHERE rp.pstatus =  '"+completeValue+"' ";
            }
        Sql sql = Sqls.queryRecord(sqlStr);
        if (orders != null && orders.size() > 0) {
            for (DataTableOrder order : orders) {
                DataTableColumn col = columns.get(order.getColumn());
                cnd.orderBy(Sqls.escapeSqlFieldValue(col.getData()).toString(), order.getDir());
            }
        }
        Pager pager = new OffsetPager(start, length);
        sql.setCondition(cnd.getOrderBy());
        sql.setPager(pager);
        re.put("recordsFiltered", Daos.queryCount(dao(),sql));
        dao().execute(sql);
        List list = sql.getList(Record.class);
        re.put("data", list);
        re.put("draw", draw);
        re.put("recordsTotal", length);
        return  re;
    }

    @Aop(TransAop.READ_COMMITTED)
    public void completeRepair(String[] ids, String personId) {
        List<Ins_repair> repairs = this.query(Cnd.where("id", "IN", ids));
        if(repairs.size() == 0){
            throw new ValidatException("不能确定维修对象");
        }
        String sqlStr = " SELECT rp.id,rp.pstatus,rp.personid,ib.insnum,ib.insname \n" +
                "\t FROM ins_repair rp \n" +
                "\t JOIN ins_instrument ins ON rp.insId = ins.id \n" +
                "\t JOIN ins_baseins ib ON ins.baseInsId = ib.id "+
                "\t WHERE rp.id IN ("+ StringUtil.arr2SpecialStr(ids)+") ";
        Sql sql = Sqls.queryRecord(sqlStr);
        dao().execute(sql);
        List<Record> recordList = sql.getList(Record.class);
        if(recordList.size() > 0){
            StringBuffer sb = new StringBuffer(80);
            boolean errorRepair = false;
            sb.append("请选择[维修]状态的工具进行此操作!下列工具不符要求:");
            for (Record record : recordList) {
                int pstatus = record.getInt("pstatus");
                String insnum = record.getString("insnum");
                String insname = record.getString("insname");
                String repairId = record.getString("id");
//                String personId = record.getString("personid");
                if(pstatus != 1){
                    errorRepair = true;
                    sb.append("工具编号:" + insnum).append(",");
                    sb.append("工具名称:" + insname).append(";");
                }
                insRepairtrackService.insertTrackRecord(repairId,personId,newDataTime.getDateYMDHMS(),2);
            }
            if(errorRepair){
                throw new ValidatException(sb.toString());
            }
            this.update(Chain.make("pstatus",2),Cnd.where("id","IN",ids));
        }else{
            throw new ValidatException("未找到相应的维修对象");
        }

    }


 /*
    private String getOrderNum(String operatetime, String airportid) throws ParseException {
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
    }

    public synchronized int getOrderNumSeq(String dateStr, String airportid) throws ParseException {
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

}
