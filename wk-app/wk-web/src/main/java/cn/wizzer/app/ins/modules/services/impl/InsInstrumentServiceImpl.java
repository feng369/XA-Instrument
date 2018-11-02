package cn.wizzer.app.ins.modules.services.impl;

import cn.wizzer.app.ins.modules.models.*;
import cn.wizzer.app.ins.modules.services.*;
import cn.wizzer.app.web.commons.base.Globals;
import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.base.ValidatException;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.framework.page.OffsetPager;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.*;
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
import org.nutz.mvc.upload.TempFile;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@IocBean(args = {"refer:dao"})
public class InsInstrumentServiceImpl extends BaseServiceImpl<Ins_instrument> implements InsInstrumentService {
    public InsInstrumentServiceImpl(Dao dao) {
        super(dao);
    }

    @Inject
    private InsRfidService rfidService;
    @Inject
    private InsOrderService insOrderService;
    @Inject
    private InsOrderentryService insOrderentryService;
    @Inject
    private InsSpaceService insSpaceService;
    @Inject
    private InsBaseinsService insBaseinsService;
    @Inject
    private InsCabinetService insCabinetService;
    @Inject
    private InsLockService insLockService;
    @Inject
    private InsRfidService insRfidService;

    @Aop(TransAop.READ_COMMITTED)
    public void bindSpace(String[] insIds, String spaceid) {
        if (insIds.length < 1 && Strings.isBlank(spaceid)) {
            throw new ValidatException("传入参数不正确");
        }
        for (String insId : insIds) {
            Ins_instrument insInstrument = this.fetch(insId);
            if (insInstrument != null) {
                this.update(Chain.make("insspaceid", spaceid), Cnd.where("id", "=", insId));
            }
        }
    }

    @Aop(TransAop.READ_COMMITTED)
    public void updateRFID(String insId, String rfID) {
        if (Strings.isBlank(insId) || Strings.isBlank(rfID)) {
            throw new ValidatException("传入参数不正确");
        }
        //1.添加Ins_instrument的rfid
        this.update(Chain.make("rfid", rfID), Cnd.where("id", "=", insId));
        //2.改变RFID状态为使用中
        rfidService.update(Chain.make("pstatus", 1), Cnd.where("id", "=", rfID));
    }

    @Aop(TransAop.READ_COMMITTED)
    public Map<String, Object> uploadIns(TempFile tf, String insId) {
        try {
            Map<String, Object> map = new HashMap();
            String[] postfix = tf.getSubmittedFileName().split("\\.");
            if (postfix.length < 1) {
                throw new ValidatException("上传失败");
            }
            String imgPath = Globals.AppRoot + Globals.AppUploadPath + "/uploadTool/";
            File file = new File(imgPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            String insPhoto = R.UU32() + "." + postfix[postfix.length - 1];
            //写入文件
            tf.write(file.getPath() + "\\/" + insPhoto);
            tf.delete();
            map.put("result", "上传成功");
            //将上传路径写入ins_instrument
            Ins_instrument insInstrument = this.fetch(insId);
            //删除原来的图片
            if (!Strings.isBlank(insInstrument.getToolPic())) {
                //一张
                deleteOldPicture(insInstrument.getToolPic(), imgPath);
            }
            if (insInstrument != null) {
                this.update(Chain.make("toolPic", Globals.AppUploadPath + "/uploadTool/" + insPhoto), Cnd.where("id", "=", insId));
            }
            return map;
        } catch (Exception e) {
            throw new ValidatException("上传失败");
        }
    }


   /* public List<Map<String, Object>> getInstrumentList(String childDictId, Integer pagenumber, Integer pagesize) {
        if(Strings.isBlank(childDictId)){
            throw new ValidatException("传入参数不能为空");
        }
        String sqlStr = "SELECT DISTINCT ins.insname,ins.id,ins.insnum,ins.insparam,ins.insmodel,su.name,sp.spacename,ins.toolPic,ins.describ,ins.pstatus,\n" +
                "(case \n" +
                "\twhen ins.pstatus = 0 then '未借用' \n" +
                "when ins.pstatus = 1 then  '借用中' else '' end)  \n" +
                "as pstatusname\n" +
                " FROM ins_instrument ins LEFT JOIN   sys_unit su ON ins.insunit = su.id LEFT JOIN ins_space  sp ON ins.insspaceid = sp.id  WHERE ins.pstatus = 0 AND ins.instype = '" + childDictId + "'";
        List<Map<String, Object>> list = new ArrayList<>();
        sqlStr = StringUtil.appendSqlStrByPager(pagenumber, pagesize, sqlStr);
        Sql sql = Sqls.queryRecord(sqlStr);
        dao().execute(sql);
        List<Record> recordList = sql.getList(Record.class);
        if(recordList.size() > 0){

            for (Record record : recordList) {
                Map<String, Object> map = new HashMap<>();
                map.put("id",record.getString("id"));
                map.put("insnum",record.getString("insnum"));
                map.put("insname",record.getString("insname"));
                map.put("insparam",record.getString("insparam"));
                map.put("insmodel",record.getString("insmodel"));
                map.put("insunitName",record.getString("name"));
                map.put("insSpaceName",record.getString("spacename"));
                map.put("toolPic",record.getString("toolPic"));
                map.put("describ",record.getString("describ"));
                map.put("pstatus",record.getInt("pstatus"));
                map.put("pstatusname",record.getString("pstatusname"));
                list.add(map);
            }
        }
        return list;
    }*/

    /*@Aop(TransAop.READ_COMMITTED)
    public Result checkOrders(String[] rfidNumber, String orderId, Integer doOrder) {
        if (Strings.isBlank(orderId)) {
            throw new ValidatException("传入的订单ID为空");
        }
        if (doOrder != 0 || doOrder != 1) {
            throw new ValidatException("不明确的操作指令");
        }
        //实际所拿工具
        if(rfidNumber.length == 0){
            throw new ValidatException("请传入RFID数组参数");
        }
        //订单待借用状态下的订单明细和基础工具
        String sqlStr = " SELECT ib.id as baseInsId,io.operator FROM ins_order io \n" +
                "   JOIN   ins_orderentry od ON io.id = od.orderid \n" +
                "\t  JOIN  ins_baseins ib ON od.baseInsId = ib.id \n" +
                "\t WHERE io.id = '" + orderId + "' AND od.pstatus =  " + (doOrder == 0 ? " 1 " : " 2 ");
        Sql sql = Sqls.queryRecord(sqlStr);
        dao().execute(sql);
        List<Record> orderInfoList = sql.getList(Record.class);
        if (orderInfoList.size() > 0) {
            List<String> baseInsIds = new ArrayList<>();
            //订单所属人（登录平板拿物品用户）
            String operator = orderInfoList.get(0).getString("operator");
            for (Record record : orderInfoList) {
                String baseInsId = record.getString("baseInsId");
                baseInsIds.add(baseInsId);
            }
            List<Ins_rfid> insRfids = rfidService.query(Cnd.where("number", "IN", rfidNumber));
            List<String> rfids = new ArrayList<>();
            if(insRfids.size() > 0){
                for (Ins_rfid insRfid : insRfids) {
                    rfids.add(insRfid.getId());
                }
            }
            //实际所拿/归还工具
            String selectSqlStr = "  SELECT ib.id as baseInsId,ib.insname as baseInsName,ib.insnum as baseInsNum,ins.id as insId,od.id as entryId FROM ins_instrument ins \n" +
                    "  JOIN ins_baseins ib ON ins.baseInsId = ib.id\n" +
                    "LEFT JOIN ins_orderentry od ON ib.id = od.baseInsId " +
                    " WHERE ins.rfid IN (" + StringUtil.list2SpecialStr(rfids) + ") ";
            Sql selectSql = Sqls.queryRecord(selectSqlStr);
            dao().execute(selectSql);
            List<Record> selectRecods = selectSql.getList(Record.class);
            if (selectRecods.size() > 0) {
                int baseInsCount = baseInsIds.size();
                int selectCount = selectRecods.size();
                //选中的实际工具
                List<String> selectInsIds = new ArrayList<>();
                //选中的订单已经存在的订单明细
                List<String> selectEntryIds = new ArrayList<>();
                //拿了其他人订单的工具
                List<Map<String, Object>> errorBorrowInsList = new ArrayList<>();
                //归还错误,提示信息
                StringBuffer errorReturnMsg = new StringBuffer(80);
                boolean errorReturn = false;
                errorReturnMsg.append("归还错误!");
                for (Record selectRecod : selectRecods) {
                    String baseInsId = selectRecod.getString("baseInsId");
                    String insId = selectRecod.getString("insId");
                    String entryId = selectRecod.getString("entryId");
                    if (!Strings.isBlank(entryId)) {
                        selectEntryIds.add(entryId);
                    }
                    if (!baseInsIds.contains(baseInsId)) {
                        if (doOrder == 0) {
                            //1.拿出此前订单不存在的工具
                            //生成新的订单明细,且明细状态为借用中
                            insOrderentryService.addNewOrderEntry(orderId, baseInsId, insId, operator);
                        } else {
                            *//*
                            Map<String,Object> errorBorrowInsMap = new HashMap<>();
                            errorBorrowInsMap.put("baseInsName",selectRecod.getString("baseInsName"));
                            errorBorrowInsMap.put("baseInsNum",selectRecod.getString("baseInsNum"));
                            errorBorrowInsList.add(errorBorrowInsMap);
                            *//*
                            errorReturn = true;
                            errorReturnMsg.append("工具名称:[" + selectRecod.getString("baseInsName") + "],");
                            errorReturnMsg.append("工具编号:[" + selectRecod.getString("baseInsNum") + "];");
                        }
                    } else {
                        selectInsIds.add(insId);
                    }
                }
                if (doOrder == 1 && errorReturn) {
                    return Result.error(2, errorReturnMsg.toString());
                }
                List<Ins_instrument> storeInstruments = this.query(Cnd.where("id", "IN", selectInsIds));
                Map<String, Integer> countMap = getCountMap(storeInstruments);
                if (selectInsIds.size() > 0 && selectEntryIds.size() > 0) {
                    if (doOrder == 0) {
                        //将实际拿的工具状态变为借用中
                        this.update(Chain.make("pstatus", 2), Cnd.where("id", "IN", selectInsIds));
                        //实际所拿的工具对应订单明细变为借用中
                        insOrderentryService.update(Chain.make("pstatus", 2), Cnd.where("id", "IN", selectEntryIds));
                    } else {
                        //将实际归还的工具状态变为未借用
                        this.update(Chain.make("pstatus", 0), Cnd.where("id", "IN", selectInsIds));
                        //归还的工具明细变为已归还
                        insOrderentryService.update(Chain.make("pstatus", 3), Cnd.where("id", "IN", selectEntryIds));
                    }
                }
                //处理库存
                for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
                    String baseInsId = entry.getKey();
                    Ins_baseins insBaseins = insBaseinsService.fetch(baseInsId);
                    if (insBaseins != null) {
                        Integer insCount = entry.getValue();
                            if(doOrder == 0){
                                //reserveCount(预留数量)
                                insBaseinsService.update(Chain.make("reserveCount", insBaseins.getReserveCount() - insCount), Cnd.where("id", "=", baseInsId));
                            }else{
                                //useableCount(可用数量)
                                insBaseinsService.update(Chain.make("useableCount",  insBaseins.getUseableCount() + insCount), Cnd.where("id", "=", baseInsId));
                            }
                    }
                }
            }
            throw new ValidatException("实际所拿工具数据为空");
        }
        throw new ValidatException("查询不到订单相关数据");
    }*/
/*
    @Aop(TransAop.READ_COMMITTED)
    public Result checkOrders (String[] rfids, String orderId, Integer doOrder) {
        if (Strings.isBlank(orderId)) {
            throw new ValidatException("传入的订单ID为空");
        }
        if (doOrder != 0 || doOrder != 1) {
            throw new ValidatException("不明确的操作指令");
        }
        List<Ins_order> insOrders = insOrderService.query(Cnd.where("id", "=", orderId));
        if (insOrders.size() != 1) {
            throw new ValidatException("无法确定唯一的订单");
        }
        //实际所拿工具
        if (rfids.length == 0) {
            throw new ValidatException("请传入RFID数组参数");
        }
        String sqlStr = "SELECT ins.id,od.id as odId FROM ins_instrument ins JOIN ins_orderentry od ON ins.id = od.insId  JOIN ins_order io ON od.orderid = io.id  WHERE od.pstatus = 2 AND io.id = '" +  orderId + "' ";
        Sql sql = Sqls.queryRecord(sqlStr);
        dao().execute(sql);
        List<Record> recordList = sql.getList(Record.class);
        //订单中工具的ID集合
        List<String> orderInsIds = new ArrayList<>();
        //装订单明细ID的集合
        List<String> orderEntryIds = new ArrayList<>();
        if (recordList.size() > 0) {
            for (Record record : recordList) {
                String id = record.getString("id");
                orderInsIds.add(id);
                String odId = record.getString("odId");
                orderEntryIds.add(odId);
            }
        }
        List<Ins_instrument> selectIns = this.query(Cnd.where("rfid", "IN", rfids));
        //判断拿出的工具是否对应订单中的工具
        //1.拿错(包含拿多)或还错
        StringBuffer content = new StringBuffer(80);
        if (doOrder == 0) {
            //借出
            content.append("您拿出的若干工具与订单不相符!");
        } else {
//            归还
            content.append("您归还的若干工具与订单不相符!");
        }
        boolean errorFlag = false;
        if (selectIns.size() > 0) {
            for (Ins_instrument selectIn : selectIns){
                if (!orderInsIds.contains(selectIn.getId())) {
                    selectIn = this.fetchLinks(selectIn, "insRfid|insBaseins");
                    content.append("工具名称:" + (selectIn.getInsBaseins() == null ? "" : selectIn.getInsBaseins().getInsname()) + ",工具RFID号码:" + (selectIn.getInsRfid() == null ? "" : selectIn.getInsRfid().getNumber()) + ";");
                    errorFlag = true;
                }
            }
        }
        if (errorFlag) {
            return Result.error(2, content.toString());
        }
        //2.拿少或还少
        if (rfids.length < orderInsIds.size()) {
            String lessStr;
            if (doOrder == 0) {
                lessStr = "您还有工具尚未拿出!";
            } else {
                lessStr = "您还有工具尚未归还!";
            }
            return Result.error(3, lessStr);
        }
        //3.正常借用或归还
        if (orderInsIds.size() <= 0 || orderEntryIds.size() <= 0) {
            return Result.error("system.error");
        }
        if (doOrder == 0) {
            //正确拿出
            //修改工具状态为2借用中
//            executeUpdateSQL(orderInsIds, "UPDATE ins_instrument SET pstatus = 2 WHERE id IN(", ")");
            //2.修改订单明细状态为2借用中
//            executeUpdateSQL(orderEntryIds, "UPDATE ins_orderentry SET pstatus = 2 WHERE id IN(", ")");

        } else {
            //正确归还
            //1.修改工具状态为0未借用
//            executeUpdateSQL(orderInsIds, "UPDATE ins_instrument SET pstatus = 0 WHERE id IN(", ")");
            //2.修改订单明细状态为3已归还
//            insOrderentryService.update(Chain.make("pstatus", 3), Cnd.where("id", "IN", orderEntryIds));
            //3.修改订单状态为已完成
            insOrderService.update(Chain.make("pstatus", 1), Cnd.where("id", "=", orderId));
        }
        return Result.success("system.success");
    }
*/

    private void executeUpdateSQL(List<String> ids, String profixStr, String suffixStr) {
        StringBuffer sb = new StringBuffer(80);
        for (String s : ids) {
            sb.append("'" + s + "'").append(",");
        }
        sb = sb.deleteCharAt(sb.length() - 1);
        String sqlToolStr = profixStr + sb.toString() + suffixStr;
        Sql sqlTool = Sqls.queryRecord(sqlToolStr);
        dao().execute(sqlTool);
    }

    @Aop(TransAop.READ_COMMITTED)
    public Map<String, Object> verifyOpenLockBy2Code(String barCode, String insId) {
        if (Strings.isBlank(barCode)) {
            throw new ValidatException("传入的二维码为空");
        }
        if (Strings.isBlank(insId)) {
            throw new ValidatException("传入的实体工具ID为空");
        }
        Map<String, Object> map = new HashMap<>();
        List<Ins_space> insSpaces = insSpaceService.query(Cnd.where("barcode", "=", barCode));
        if (insSpaces.size() != 1) {
            throw new ValidatException("找不到二维码对应的仓位");
        }
        Ins_space insSpace = insSpaces.get(0);
        //此时应该是借用中(已生成订单)的工具
        int insCount = this.count(Cnd.where("insspaceid", "=", insSpace.getId()).and("id", "=", insId).and("pstatus", "=", 1));
        if (insCount == 0) {
            //不存在
            map.put("result", "fail");
        } else {
            //存在
            map.put("result", "success");

            //开锁
           /* HttpClientUtil httpClientUtil = new HttpClientUtil();
            String url = "";
            //
            Map<String,String> paramMap = new HashMap<>();
            String returnParams = httpClientUtil.doPost(url, paramMap, "UTF-8");
            JSONObject jsonObject = JSONObject.parseObject(returnParams);*/

        }
        return map;
    }

    @Aop(TransAop.READ_COMMITTED)
    public void borrowOrReturnTest(String type, String insId) {
        if (Strings.isBlank(type)) {
            throw new ValidatException("传入的锁指令不能为空");
        }
        if (Strings.isBlank(insId)) {
            throw new ValidatException("实体工具ID不能为空");
        }
        Map<String, Object> map = new HashMap<>();
        List<Ins_orderentry> insOrderentries = insOrderentryService.query(Cnd.where("insId", "=", insId));
        if (insOrderentries.size() != 1) {
            throw new ValidatException("不能确定唯一的订单明细");
        }
        if ("1".equals(type)) {
            //借用
            //将订单明细变为借用中
            insOrderentryService.update(Chain.make("pstatus", 2), Cnd.where("id", "=", insOrderentries.get(0).getId()));
        } else if ("2".equals(type)) {
            //归还
            //将订单明细变为已归还
            insOrderentryService.update(Chain.make("pstatus", 3), Cnd.where("id", "=", insOrderentries.get(0).getId()));
            //将工具状态变为未借用
            this.update(Chain.make("pstatus", 0), Cnd.where("id", "=", insId));
        }
    }

    @Aop(TransAop.READ_COMMITTED)
    public Result onLineIns(String[] insIds) {
        if (insIds.length == 0) {
            throw new ValidatException("请选择至少一条数据");
        }
        StringBuffer sb = new StringBuffer(80);
        for (String s : insIds) {
            sb.append("'" + s + "'").append(",");
        }
        sb = sb.deleteCharAt(sb.length() - 1);
        String sqlStr = " SELECT " +
                " ins.id,ins.insnum,ins.describ,ins.pstatus as insPstatus,ins.linePstatus, " +
                " ib.id  as ibId,ib.insname,ib.insparam,ib.insmodel," +
                " isp.id as spId,isp.spacename, " +
                " irf.id as rfId,irf.number " +
                " FROM ins_instrument ins " +
                " LEFT JOIN ins_baseins ib ON ins.baseInsId = ib.id " +
                " LEFT JOIN ins_space isp ON ins.insspaceid = isp.id " +
                " LEFT JOIN ins_rfid irf ON ins.rfid = irf.id " +
                " WHERE ins.id IN (" + sb.toString() + ") ";
        Sql sql = Sqls.queryRecord(sqlStr);
        dao().execute(sql);
        List<Record> recordList = sql.getList(Record.class);
        if (recordList.size() == 0) {
            throw new ValidatException("未找到相应ID的工具");
        }
        StringBuffer content = new StringBuffer(80);
        Map<String, List<String>> insMaps = new HashMap<>();
        for (Record record : recordList) {
            List<String> infoList = new ArrayList<>();
            String insnum = record.getString("insnum");
//            String spaceName = record.getString("spacename");
            String number = record.getString("number");
            int linePstatus = record.getInt("linePstatus");
            if (linePstatus == 1) {
                infoList.add("工具已上线!无需执行此操作!");
            }
         /*   if (Strings.isBlank(spaceName)) {
                infoList.add("未分配仓位.");
            }*/
            if (Strings.isBlank(number)) {
                infoList.add("未分配RFID;");
            }
            if (infoList.size() > 0) {
                insMaps.put(insnum, infoList);
            }
        }
        if (insMaps.size() > 0) {
            for (Map.Entry<String, List<String>> info : insMaps.entrySet()) {
                String insNum = info.getKey();
                List<String> infoList = info.getValue();
                if (infoList.size() > 0) {
                    content.append("工具编号").append(insNum).append(":");
                    for (String infoStr : infoList) {
                        content.append(infoStr);
                    }
                }
            }
            return Result.error(2, content.toString());
        }
        //可以上线
        this.update(Chain.make("linePstatus", 1), Cnd.where("id", "IN", insIds));
        //改变totalCount,reserveCount,useableCount
        List<Ins_instrument> insInstruments = this.query(Cnd.where("id", "IN", insIds).and("linePstatus", "=", 1));
        Map<String, Integer> countMap = getCountMap(insInstruments);
        if (countMap.size() > 0) {
            for (Map.Entry<String, Integer> insMap : countMap.entrySet()) {
                String baseInsId = insMap.getKey();
                Ins_baseins insBaseins = insBaseinsService.fetch(baseInsId);
                Integer insCount = insMap.getValue();
                if (insBaseins != null) {
                    insBaseinsService.update(Chain.make("totalCount", insBaseins.getTotalCount() + insCount).add("useableCount", insBaseins.getUseableCount() + insCount), Cnd.where("id", "=", baseInsId));
                }
            }
        }
        return Result.success("system.success");
    }

    @Aop(TransAop.READ_COMMITTED)
    public Result offLineIns(String[] insIds) {
        if (insIds.length == 0) {
            throw new ValidatException("请选择至少一条数据进行下线");
        }
        StringBuffer sb = new StringBuffer(80);
        for (String s : insIds) {
            sb.append("'" + s + "'").append(",");
        }
        sb = sb.deleteCharAt(sb.length() - 1);
        String sqlStr = " SELECT " +
                " ins.id,ins.insnum,ins.describ,ins.pstatus as insPstatus,ins.linePstatus, " +
                " ib.insname,ib.insparam,ib.insmodel" +
//                "od.id as entryId,od.pstatus as entryPstatus," +
//                "io.id as orderId,io.pstatus as orderPstatus, " +
                " FROM ins_instrument ins " +
                " LEFT JOIN ins_baseins ib ON ins.baseInsId = ib.id " +
//                " LEFT JOIN ins_orderentry od ON od.insId = ins.id "+
//                " LEFT JOIN ins_order io ON od.orderid = io.id " +
                " WHERE ins.id IN (" + sb.toString() + ") ";
        Sql sql = Sqls.queryRecord(sqlStr);
        dao().execute(sql);
        List<Record> recordList = sql.getList(Record.class);
        if (recordList.size() == 0) {
            throw new ValidatException("未找到相应ID的工具");
        }
        StringBuffer content = new StringBuffer(80);
        Map<String, List<String>> insMaps = new HashMap<>();
        for (Record record : recordList) {
            List<String> infoList = new ArrayList<>();
            String insnum = record.getString("insnum");
            int linePstatus = record.getInt("linePstatus");
            if (linePstatus == 0) {
                infoList.add("工具已下线!无需执行此操作!");
            }
            int insPstatus = record.getInt("insPstatus");
            if (insPstatus == 2) {
                infoList.add("当前工具正被使用,请完成订单后再下线!");
            }
            /*
            String entryId = record.getString("entryId");
            int entryPstatus = record.getInt("entryPstatus");
            int orderPstatus = record.getInt("orderPstatus");
            String orderId = record.getString("orderId");
            */
            if (infoList.size() > 0) {
                insMaps.put(insnum, infoList);
            }
        }
        if (insMaps.size() > 0) {
            for (Map.Entry<String, List<String>> info : insMaps.entrySet()) {
                String insNum = info.getKey();
                List<String> infoList = info.getValue();
                if (infoList.size() > 0) {
                    content.append("工具编号").append(insNum).append(":");
                    for (String infoStr : infoList) {
                        content.append(infoStr);
                    }
                }
            }
            return Result.error(2, content.toString());
        }
        //可以下线
        this.update(Chain.make("linePstatus", 0), Cnd.where("id", "IN", insIds));
        //改变totalCount,reserveCount,useableCount
        List<Ins_instrument> insInstruments = this.query(Cnd.where("id", "IN", insIds).and("linePstatus", "=", 0));
        Map<String, Integer> countMap = getCountMap(insInstruments);
        if (countMap.size() > 0) {
            for (Map.Entry<String, Integer> insMap : countMap.entrySet()) {
                String baseInsId = insMap.getKey();
                Ins_baseins insBaseins = insBaseinsService.fetch(baseInsId);
                Integer insCount = insMap.getValue();
                if (insBaseins != null) {
                    int tempTotalCount = insBaseins.getTotalCount() - insCount;
                    int tempUseableCount = insBaseins.getUseableCount() - insCount;
                    if (tempTotalCount < 0) {
                        tempTotalCount = 0;
                    }
                    if (tempUseableCount < 0) {
                        tempUseableCount = 0;
                    }
                    insBaseinsService.update(Chain.make("totalCount", tempTotalCount).add("useableCount", tempUseableCount), Cnd.where("id", "=", baseInsId));
                }
            }
        }

        //改变totalCount,reserveCount,useableCount
        return Result.success("system.success");

    }

    public Map<String, Integer> getCountMap(List<Ins_instrument> insInstruments) {
        Map<String, Integer> countMap = new ConcurrentHashMap();
        if (insInstruments.size() > 0) {
            //对工具分类
            for (Ins_instrument insInstrument : insInstruments) {
                if (!Strings.isBlank(insInstrument.getBaseInsId())) {
                    if (countMap.get(insInstrument.getBaseInsId()) == null) {
                        countMap.put(insInstrument.getBaseInsId(), 1);
                    } else {
                        countMap.put(insInstrument.getBaseInsId(), countMap.get(insInstrument.getBaseInsId()) + 1);
                    }
                }
            }
        }
        return countMap;
    }

    @Aop(TransAop.READ_COMMITTED)
    public void checkIns(String cabinetId, String[] rfIDs) {
        if (Strings.isBlank(cabinetId)) {
            throw new ValidatException("柜子ID为空");
        }
        if (rfIDs.length == 0) {
            throw new ValidatException("未传入RFID数据");
        }
        String sqlStr = "SELECT rf.number FROM ins_rfid rf \n" +
                "\tJOIN ins_instrument ins ON rf.id = ins.rfid \n" +
                "\tJOIN ins_baseins ib ON ins.baseInsId = ib.id \n" +
                "\tJOIN ins_space sp ON ib.spaceId = sp.id \n" +
                "\tJOIN ins_cabinet ca ON sp.cabinetid = ca.id \n" +
                "\tWHERE ca.id = '" + cabinetId + "' ";
        Sql sql = Sqls.queryRecord(sqlStr);
        dao().execute(sql);
        List<Record> recordList = sql.getList(Record.class);
        //不存在的ID列表
        List<String> notExistList = new ArrayList<>();
        if (recordList.size() > 0) {
            for (Record record : recordList) {
                String number = record.getString("number");
                if (!Arrays.asList(rfIDs).contains(number)) {
                    notExistList.add(number);
                }
            }
        }
        if (notExistList.size() > 0) {
            StringBuffer sb = new StringBuffer(80);
            sb.append("RFID号码与数据库不匹配!具体如下:");
            for (String str : notExistList) {
                sb.append(str).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            throw new ValidatException(sb.toString());
        }
    }


   /* @Aop(TransAop.READ_COMMITTED)
    public void doCheckOrders(String[] addRFIDs, String reduceRFIDs, String orderId){
        if (Strings.isBlank(orderId)) {
            throw new ValidatException("传入的订单ID为空");
        }
        if(addRFIDs.length == 0 && reduceRFIDs.length == 0){
                throw new ValidatException("未传入借出或归还数据");
        }
        Integer doOrder;
        if(addRFIDs.length > 0){
            //归还
            doOrder = 1;
            checkOrders(addRFIDs, orderId, doOrder);
        }
        if(reduceRFIDs.length > 0){
            //借出
            doOrder = 0;
            checkOrders(reduceRFIDs, orderId, doOrder);
        }
    }*/


    @Aop(TransAop.READ_COMMITTED)
    public Result unbindRFIDs(String[] insIds) {
        if (insIds.length == 0) {
            throw new ValidatException("请选择至少一条数据进行解绑");
        }
        List<Ins_instrument> instruments = this.query(Cnd.where("id", "IN", insIds));
        if (instruments.size() == 0) {
            throw new ValidatException("未查询到相应的实体工具数据");
        }
        List<String> rfids = new ArrayList<>();
        StringBuffer sb = new StringBuffer(80);
        List<Ins_instrument> lineIns = new ArrayList<>();
        for (Ins_instrument instrument : instruments) {
            if (instrument.getLinePstatus() == 1) {
                throw new ValidatException("编号:" + instrument.getInsnum() + "工具已上线!请下线后再解绑");
            }
            if (instrument.getPstatus() != 0) {
                throw new ValidatException("编号:" + instrument.getInsnum() + "工具已被借用!请完成订单后再解绑");
            }
            rfids.add(instrument.getRfid());
        }
        insRfidService.update(Chain.make("pstatus", 0), Cnd.where("id", "IN", rfids));
        this.update(Chain.make("rfid", null), Cnd.where("id", "IN", insIds));
        return Result.success("system.success");
    }

    public NutMap getInsList(int length, int start, int draw, List<DataTableOrder> orders, List<DataTableColumn> columns, Cnd cnd, String insnum, String insname, String number, String pstatus, String linePstatus, String cabinetId, String spaceId) {
        NutMap re = new NutMap();
        String sqlStr = " \tSELECT \n" +
                "\tins.id,ins.insnum,ins.pstatus,ins.linePstatus,\n" +
                "\tib.insname,ib.insmodel,\n" +
                "\trf.number as rfNumber,\n" +
                "\tsp.spacename,ca.cabinetname\t \n" +
                "\tFROM ins_instrument ins \n" +
                "\t LEFT JOIN ins_baseins ib ON ins.baseInsId = ib.id\n" +
                "\t LEFT JOIN ins_rfid rf ON ins.rfid = rf.id\n" +
                "\tLEFT JOIN ins_baseins_space bsp ON ib.id = bsp.baseInsId\n" +
                "\tLEFT JOIN ins_space sp ON bsp.spaceId = sp.id\n" +
                "\tLEFT JOIN ins_cabinet ca ON sp.cabinetid = ca.id\n  WHERE 1 = 1";
        if (Strings.isNotBlank(insname)) {
            sqlStr += " AND ib.insname  LIKE '%" + insname + "%' ";
        }
        if (Strings.isNotBlank(insnum)) {
            sqlStr += " AND ins.insnum LIKE '%" + insnum + "%' ";
        }
        if (Strings.isNotBlank(number)) {
            sqlStr += " AND rf.number LIKE '%" + number + "%' ";
        }
        if (Strings.isNotBlank(pstatus)) {
            sqlStr += " AND ins.pstatus =  " + pstatus + "  ";
        }
        if (Strings.isNotBlank(linePstatus)) {
            sqlStr += " AND ins.linePstatus =  " + linePstatus + "  ";
        }
        if (!Strings.isBlank(cabinetId)) {
            sqlStr += " AND ca.id =  '" + cabinetId + "' ";
        }
        if (!Strings.isBlank(spaceId)) {
            sqlStr += " AND sp.id =  '" + spaceId + "' ";
        }
        if (orders != null && orders.size() > 0) {
            for (DataTableOrder order : orders) {
                DataTableColumn col = columns.get(order.getColumn());
                sqlStr += " ORDER BY  " + Sqls.escapeSqlFieldValue(col.getData()).toString() + " " + order.getDir();
            }
        }
        Sql sql = Sqls.queryRecord(sqlStr);
        Pager pager = new OffsetPager(start, length);
        sql.setPager(pager);
        re.put("recordsFiltered", Daos.queryCount(dao(), sql));
        dao().execute(sql);
        List list = sql.getList(Record.class);
        re.put("data", list);
        re.put("draw", draw);
        re.put("recordsTotal", length);
        return re;
    }


    @Aop(TransAop.READ_COMMITTED)
    public Result doCheckOrders(String[] rfIDs, String spaceNum, String orderId, String cabinetType) {
        /*
        if (Strings.isBlank(orderId)) {
            throw new ValidatException("传入的订单ID为空");
        }
        if (Strings.isBlank(spaceNum)) {
            throw new ValidatException("仓位编号为空");
        }
        if (Strings.isBlank(cabinetType)) {
            throw new ValidatException("柜子类型为空");
        }
        List<Ins_space> spaces = insSpaceService.query(Cnd.where("spacenum", "=", spaceNum));
        if (spaces.size() != 1) {
            throw new ValidatException("根据仓位编号未能确定唯一的智能柜仓位");
        }
        Ins_space space = spaces.get(0);
        StringBuffer allSqlStr = new StringBuffer(80);
        allSqlStr.append(" SELECT rf.number FROM ins_rfid rf  "
                + " LEFT JOIN ins_instrument ins ON rf.id = ins.rfid "
                + "\t LEFT JOIN ins_baseins ib ON ins.baseInsId = ib.id \n"
                + "\t LEFT JOIN ins_baseins_space ibp ON ibp.baseInsId = ib.id\n"
        );
        allSqlStr.append(" \t WHERE ibp.spaceId = '" + space.getId() + "' AND  ins.linePstatus = 1 \n");
        Sql sql = Sqls.queryRecord(allSqlStr.toString());
        dao().execute(sql);
        List<Record> recordList = sql.getList(Record.class);
        List<String> rfidNumList = new ArrayList<>();
        List<String> borrowingRFIDs = new ArrayList<>();
        if (recordList.size() > 0) {
            for (Record record : recordList) {
                String number = record.getString("number");
                rfidNumList.add(number);
            }
        }
        StringBuffer orderInfoStr = new StringBuffer(80);
        orderInfoStr.append(
                " SELECT rf.number,od.id,od.pstatus FROM ins_rfid rf \n" +
                        "\t LEFT JOIN ins_instrument  ins ON rf.id = ins.rfid \n" +
                        "\t LEFT JOIN ins_baseins ib ON ins.baseInsId = ib.id \n" +
                        "\t LEFT JOIN ins_baseins_space ibp ON ibp.baseInsId = ib.id \n" +
                        "\t LEFT JOIN ins_orderentry od ON ins.id = od.insId \n"
        );
        orderInfoStr.append(" \tWHERE  ibp.spaceId = '" + space.getId() + "' AND od.pstatus = 2 AND  ins.linePstatus = 1 \n");
        Sql orderInfoSql = Sqls.queryRecord(orderInfoStr.toString());
        dao().execute(orderInfoSql);
        List<Record> orderInfoList = orderInfoSql.getList(Record.class);
        List<String> inRFIDs = Arrays.asList(rfIDs);
        List<String> addRFIDs = new ArrayList<>(inRFIDs);
        if (orderInfoList.size() > 0) {
            for (Record record : orderInfoList) {
                String number = record.getString("number");
                borrowingRFIDs.add(number);
            }
        }
        List<String> reduceRFIDs = new ArrayList<>(rfidNumList);
        List<String> allRFIDs = new ArrayList<>(rfidNumList);
        if (rfidNumList.size() > 0) {
            reduceRFIDs.removeAll(borrowingRFIDs);
            reduceRFIDs.removeAll(inRFIDs);
          *//*
            if ("YDcabinet".equals(cabinetType)) {
                Iterator<String> iterator = addRFIDs.iterator();
                while (iterator.hasNext()) {
                    String rfidNumber = iterator.next();
                    if (!allRFIDs.contains(rfidNumber)) {
                        iterator.remove();
                    }
                }
               }
            *//*
            allRFIDs.removeAll(borrowingRFIDs);
            addRFIDs.removeAll(allRFIDs);
        }
        addOrReduceRFIDs(addRFIDs, reduceRFIDs, orderId, cabinetType);
        */
        if (Strings.isBlank(orderId)) {
            throw new ValidatException("传入的订单ID为空");
        }
        if (Strings.isBlank(spaceNum)) {
            throw new ValidatException("仓位编号为空");
        }
        if (Strings.isBlank(cabinetType)) {
            throw new ValidatException("柜子类型为空");
        }
        List<Ins_space> spaces = insSpaceService.query(Cnd.where("spacenum", "=", spaceNum));
        if (spaces.size() != 1) {
            throw new ValidatException("根据仓位编号未能确定唯一的智能柜仓位");
        }
        Ins_space space = spaces.get(0);
        StringBuffer allSqlStr = new StringBuffer(80);
        allSqlStr.append(" SELECT rf.number FROM ins_rfid rf  " +
                " JOIN ins_instrument ins ON rf.id = ins.rfid " +
                " JOIN ins_baseins ib ON ins.baseInsId = ib.id " +
                " JOIN ins_baseins_space ibp ON ibp.baseInsId = ib.id ");
        if ("YDcabinet".equals(cabinetType)) {
            allSqlStr.append(" JOIN ins_space sp ON ibp.spaceId = sp.id ");
            allSqlStr.append(" JOIN ins_cabinet cb ON sp.cabinetid = cb.id ");
            allSqlStr.append(" WHERE ins.linePstatus = 1 AND cb.id = '" + space.getCabinetid() + "' ");
        } else if ("GDcabinet".equals(cabinetType)) {
            allSqlStr.append("  WHERE ibp.spaceId = '" + space.getId() + "' AND  ins.linePstatus = 1 ");
        }
        Sql sql = Sqls.queryRecord(allSqlStr.toString());
        dao().execute(sql);
        List<Record> recordList = sql.getList(Record.class);
        List<String> rfidNumList = new ArrayList<>();
        List<String> borrowingRFIDs = new ArrayList<>();
        if (recordList.size() > 0) {
            for (Record record : recordList) {
                String number = record.getString("number");
                rfidNumList.add(number);
            }
        }
        StringBuffer orderInfoStr = new StringBuffer(80);
        orderInfoStr.append(" SELECT rf.number,od.id,od.pstatus FROM ins_rfid rf \n" +
                "\tJOIN ins_instrument  ins ON rf.id = ins.rfid \n" +
                "\tJOIN ins_baseins ib ON ins.baseInsId = ib.id \n" +
                "\tJOIN ins_baseins_space ibp ON ibp.baseInsId = ib.id \n" +
                "\tJOIN ins_orderentry od ON ins.id = od.insId \n");
        if ("YDcabinet".equals(cabinetType)) {
            orderInfoStr.append(" JOIN ins_space sp ON ibp.spaceId = sp.id ");
            orderInfoStr.append(" JOIN ins_cabinet cb ON sp.cabinetid = cb.id ");
            orderInfoStr.append(" WHERE od.pstatus = 2 AND  ins.linePstatus = 1 AND cb.id = '" + space.getCabinetid() + "' ");
        } else if ("GDcabinet".equals(cabinetType)) {
            orderInfoStr.append(" \tWHERE ibp.spaceId = '" + space.getId() + "' AND od.pstatus = 2 AND  ins.linePstatus = 1 \n");
        }
        Sql orderInfoSql = Sqls.queryRecord(orderInfoStr.toString());
        dao().execute(orderInfoSql);
        List<Record> orderInfoList = orderInfoSql.getList(Record.class);
        List<String> inRFIDs = Arrays.asList(rfIDs);
        List<String> addRFIDs = new ArrayList<>(inRFIDs);//inRFIDs副本
        if (orderInfoList.size() > 0) {
            for (Record record : orderInfoList) {
                String number = record.getString("number");
                borrowingRFIDs.add(number);
            }
        }
        List<String> reduceRFIDs = new ArrayList<>(rfidNumList);
        List<String> allRFIDs = new ArrayList<>(rfidNumList);
        if (rfidNumList.size() > 0) {
            reduceRFIDs.removeAll(borrowingRFIDs);
            reduceRFIDs.removeAll(inRFIDs);
            allRFIDs.removeAll(borrowingRFIDs);
            addRFIDs.removeAll(allRFIDs);
        }
        return addOrReduceRFIDs(addRFIDs, reduceRFIDs, orderId, cabinetType, spaceNum);
    }

    public Result addOrReduceRFIDs(List<String> addRFIDs, List<String> reduceRFIDs, String orderId, String cabinetType, String spaceNum) {
        Integer doOrder;
        Result result = null;
        if (reduceRFIDs.size() > 0) {
            doOrder = 0;
            result = checkOrders(reduceRFIDs, orderId, doOrder, cabinetType, spaceNum);
            if (result != null) {
                return result;
            }
        }
        if (addRFIDs.size() > 0) {
            doOrder = 1;
            result = checkOrders(addRFIDs, orderId, doOrder, cabinetType, spaceNum);
            if (result != null) {
                return result;
            }
        }
        return Result.success("system.success");
    }

    public Result checkOrders(List<String> rfidNumber, String orderId, Integer doOrder, String cabinetType, String spaceNum) {
        StringBuffer sqlStr = new StringBuffer(80);
        sqlStr.append(" SELECT ib.id as baseInsId,io.operator,od.id as odId,od.pstatus as entryStatus ").append((doOrder == 1 ? ",rf.number as rfNumber " : "")).append(" FROM ins_order io ");
        sqlStr.append("  JOIN   ins_orderentry od ON io.id = od.orderid ");
        sqlStr.append("  JOIN  ins_baseins ib ON od.baseInsId = ib.id ");
        if (doOrder == 1) {
            sqlStr.append(" JOIN  ins_instrument ins ON ins.id = od.insId ");
            sqlStr.append("  JOIN  ins_rfid rf ON ins.rfid = rf.id  ");
            sqlStr.append(" WHERE od.pstatus = '2' ");
        } else {
            sqlStr.append(" WHERE  od.pstatus <> '3' ");
        }
        sqlStr.append(" AND io.id = '").append(orderId).append("' ");
        Sql sql = Sqls.queryRecord(sqlStr.toString());
        dao().execute(sql);
        List<Record> orderInfoList = sql.getList(Record.class);
        List<Record> outSpaceRecords = new ArrayList<>();
        List<Record> inSpaceRecords = new ArrayList<>();
        Map<String, List<String>> baseInsCoutMap = new HashMap<>();
        String operator;
        List<String> outRFIDs = new ArrayList<>();
        List<Ins_instrument> storeInstruments = new ArrayList<>();
        if (orderInfoList.size() > 0) {
            operator = orderInfoList.get(0).getString("operator");
            for (Record record : orderInfoList) {
                if (doOrder == 1) {
                    String rfNumber = record.getString("rfNumber");
                    outRFIDs.add(rfNumber);
                } else {
                    int entryStatus = record.getInt("entryStatus");
                    if (entryStatus == 1) {
                        inSpaceRecords.add(record);
                    } else if (entryStatus == 2) {
                        outSpaceRecords.add(record);
                    }
                }
            }
        } else {
//            throw new ValidatException(doOrder == 0 ? "此订单已关闭" : "未找到订单信息");
            if (doOrder == 0) {
                return Result.error(4, "此订单已关闭");
            } else {
                return Result.error(5, "检测到不能识别的工具，请取出");
            }
        }
        if (inSpaceRecords.size() > 0) {
            for (Record inSpaceRecord : inSpaceRecords) {
                String baseInsId = inSpaceRecord.getString("baseInsId");
                String entryId = inSpaceRecord.getString("odId");
                if (baseInsCoutMap.get(baseInsId) == null) {
                    List<String> entryIds = new ArrayList<>();
                    entryIds.add(entryId);
                    baseInsCoutMap.put(baseInsId, entryIds);
                } else {
                    List<String> entryIds = baseInsCoutMap.get(baseInsId);
                    entryIds.add(entryId);
                    baseInsCoutMap.put(baseInsId, entryIds);
                }
            }
        }
        StringBuffer selectSqlStr = new StringBuffer(80);
        selectSqlStr.append("  SELECT ib.id as baseInsId,ib.insname as baseInsName,ib.insnum as baseInsNum,ins.id as insId,rf.number as rfNumber,ins.pstatus as insStatus");
        if (1 == doOrder) {
            selectSqlStr.append(",sp.spacenum as spacenum ");
        }
        selectSqlStr.append(" FROM ins_instrument ins " +
                " JOIN  ins_rfid rf ON ins.rfid = rf.id " +
                " JOIN ins_baseins ib ON ins.baseInsId = ib.id ");
        if (1 == doOrder) {
            selectSqlStr.append(" JOIN ins_baseins_space ibp ON ibp.baseInsId = ib.id ");
            selectSqlStr.append(" JOIN ins_space sp ON ibp.spaceId = sp.id ");
        }
        selectSqlStr.append(" WHERE rf.number IN (" + StringUtil.list2SpecialStr(rfidNumber) + ") AND  ins.linePstatus = '1' ");
        Sql selectSql = Sqls.queryRecord(selectSqlStr.toString());
        dao().execute(selectSql);
        List<Record> selectRecods = selectSql.getList(Record.class);
        if (selectRecods.size() > 0) {
            List<String> selectEntryIds = new ArrayList<>();
            StringBuffer errorReturnMsg = new StringBuffer(80);
            boolean errorReturn = false;
            errorReturnMsg.append("归还错误!");
            Map<String, List<String>> selectBaseInsCountMap = new HashMap<>();
            for (Record selectRecod : selectRecods) {
                String baseInsId = selectRecod.getString("baseInsId");
                String insId = selectRecod.getString("insId");
                int insStatus = selectRecod.getInt("insStatus");
//                String rfNumber = selectRecod.getString("rfNumber");
                String spaceNumber = null;
                if (1 == doOrder) {
                    spaceNumber = selectRecod.getString("spacenum");
                }
                if (doOrder == 0) {
                    if (insStatus == 0) {
                        if (selectBaseInsCountMap.get(baseInsId) == null) {
                            List<String> selectInsIds = new ArrayList<>();
                            selectInsIds.add(insId);
                            selectBaseInsCountMap.put(baseInsId, selectInsIds);
                        } else {
                            List<String> selectInsIds = selectBaseInsCountMap.get(baseInsId);
                            selectInsIds.add(insId);
                            selectBaseInsCountMap.put(baseInsId, selectInsIds);
                        }
                    }
                }
                if (doOrder == 1) {
                    if (!spaceNum.equals(spaceNumber)) {
                        errorReturn = true;
                        errorReturnMsg.append("工具名称:[" + selectRecod.getString("baseInsName") + "],");
                        errorReturnMsg.append("工具编号:[" + selectRecod.getString("baseInsNum") + "];");
                    } else {
                        Ins_instrument instrument = this.fetch(insId);
                        if (instrument != null) {
                            storeInstruments.add(instrument);
                        }
                        Ins_orderentry orderentry = insOrderentryService.fetch(Cnd.where("pstatus", "=", 2).and("insId", "=", insId));
                        if (orderentry != null) {
                            selectEntryIds.add(orderentry.getId());
                        }
                    }
                }
            }
            if (doOrder == 1 && errorReturn) {
//                throw new ValidatException(errorReturnMsg.toString());
                return Result.error(2, errorReturnMsg.toString());
            }
            /*
            if (doOrder == 1 && errorReturn && !"YDcabinet".equals(cabinetType)) {
                throw new ValidatException(errorReturnMsg.toString());
            }
            */
            if (doOrder == 0) {
                for (Map.Entry<String, List<String>> selectBaseInsInfo : selectBaseInsCountMap.entrySet()) {
                    String baseInsId = selectBaseInsInfo.getKey();
                    List<String> insIds = selectBaseInsInfo.getValue();
                    if (baseInsCoutMap.get(baseInsId) == null) {
                        for (String insId : insIds) {
                            insOrderentryService.addNewOrderEntry(orderId, baseInsId, insId, operator);
                        }
                    } else if (insIds.size() > baseInsCoutMap.get(baseInsId).size()) {
                        List<String> entryList = baseInsCoutMap.get(baseInsId);
                        int baseInsInOrder = entryList.size();
                        if (baseInsInOrder > 0) {
                            List<String> updateInsList = insIds.subList(0, baseInsInOrder);
                            insOrderentryService.dao().run(new ConnCallback() {
                                public void invoke(Connection conn) throws Exception {
                                    PreparedStatement psUpdate = null;
                                    try {
                                        String sql = " UPDATE ins_orderentry SET pstatus = ?,insId = ?  WHERE id = ? ";
                                        psUpdate = conn.prepareStatement(sql);
                                        for (int i = 0; i < updateInsList.size(); i++) {
                                            psUpdate.setString(1, "2");
                                            psUpdate.setString(2, updateInsList.get(i));
                                            psUpdate.setString(3, entryList.get(i));
                                            psUpdate.addBatch();
                                        }
                                        psUpdate.executeBatch();
                                    } catch (Exception e) {

                                    } finally {
                                        if (psUpdate != null)
                                            psUpdate.close();
                                    }
                                }
                            });
                            this.update(Chain.make("pstatus", 2), Cnd.where("id", "IN", updateInsList).and("linePstatus", "=", 1));
                            storeInstruments.addAll(this.query(Cnd.where("id", "IN", updateInsList).and("linePstatus", "=", 1)));
                        }
                        List<String> newEntryList = insIds.subList(baseInsInOrder, insIds.size());
                        for (String insId : newEntryList) {
                            insOrderentryService.addNewOrderEntry(orderId, baseInsId, insId, operator);
                        }
                    } else {
                        List<String> entryList = baseInsCoutMap.get(baseInsId);
                        insOrderentryService.dao().run(new ConnCallback() {
                            public void invoke(Connection conn) throws Exception {
                                PreparedStatement psUpdate = null;
                                try {
                                    String sql = " UPDATE ins_orderentry SET pstatus = ?,insId = ?  WHERE id = ?";
                                    psUpdate = conn.prepareStatement(sql);
                                    for (int i = 0; i < insIds.size(); i++) {
                                        psUpdate.setString(1, "2");
                                        psUpdate.setString(2, insIds.get(i));
                                        psUpdate.setString(3, entryList.get(i));
                                        psUpdate.addBatch();
                                    }
                                    psUpdate.executeBatch();
                                } catch (Exception e) {
                                } finally {
                                    if (psUpdate != null)
                                        psUpdate.close();
                                }
                            }
                        });
                        this.update(Chain.make("pstatus", 2), Cnd.where("id", "IN", insIds).and("linePstatus", "=", 1));
                        storeInstruments.addAll(this.query(Cnd.where("id", "IN", insIds)));
                    }
                }
            }
            if (doOrder == 1) {
                List<String> returnInsIds = new ArrayList<>();
                if (storeInstruments.size() > 0 && selectEntryIds.size() > 0) {
                    for (Ins_instrument instrument : storeInstruments) {
                        returnInsIds.add(instrument.getId());
                    }
                    this.update(Chain.make("pstatus", 0), Cnd.where("id", "IN", returnInsIds));
                    insOrderentryService.update(Chain.make("pstatus", 3), Cnd.where("id", "IN", selectEntryIds));
                    int totalReturnCount = insOrderentryService.count(Cnd.where("pstatus", "=", 3).and("orderid", "=", orderId));
                    int entryCount = insOrderentryService.count(Cnd.where("orderid", "=", orderId));
                    if (totalReturnCount == entryCount) {
                        insOrderService.update(Chain.make("pstatus", 1), Cnd.where("id", "=", orderId));
                    }
                }
            }
            countInsStoresByDoOrder(storeInstruments, doOrder);
        } else {
            return Result.error(3, "检测到不属于此柜的工具，请取出");
        }
        return null;
    }

    private void countInsStoresByDoOrder(List<Ins_instrument> storeInstruments, Integer doOrder) {
        if (storeInstruments.size() > 0) {
            Map<String, Integer> countMap = getCountMap(storeInstruments);
            for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
                String baseInsId = entry.getKey();
                Ins_baseins insBaseins = insBaseinsService.fetch(baseInsId);
                if (insBaseins != null) {
                    Integer insCount = entry.getValue();
                    if (doOrder == 0) {
                        int reserveCount = insBaseins.getReserveCount() - insCount;
                        if (reserveCount < 0) {
                            reserveCount = 0;
                        }
                        insBaseinsService.update(Chain.make("reserveCount", reserveCount), Cnd.where("id", "=", baseInsId));
                    } else {
                        insBaseinsService.update(Chain.make("useableCount", insBaseins.getUseableCount() + insCount), Cnd.where("id", "=", baseInsId));
                    }
                }
            }
        }
    }
}
