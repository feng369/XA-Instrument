package cn.wizzer.app.ins.modules.services.impl;

import cn.wizzer.app.ins.modules.models.Ins_repair;
import cn.wizzer.app.web.modules.controllers.open.api.Datatime.newDataTime;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.ins.modules.models.Ins_repairtrack;
import cn.wizzer.app.ins.modules.services.InsRepairtrackService;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IocBean(args = {"refer:dao"})
public class InsRepairtrackServiceImpl extends BaseServiceImpl<Ins_repairtrack> implements InsRepairtrackService {
    public InsRepairtrackServiceImpl(Dao dao) {
        super(dao);
    }

    @Aop(TransAop.READ_COMMITTED)
    public void insertTrackRecord(String repairId, String personId, String operateTime, int pstatus) {
        Ins_repairtrack repairtrack = new Ins_repairtrack();
        repairtrack.setRepairId(repairId);
        repairtrack.setOperaterId(personId);
        repairtrack.setOperatetime(operateTime);
        repairtrack.setPstatus(pstatus);
        this.insert(repairtrack);
    }

    public List<Map<String,Object>> getRepairTracks(String repairId) {
        String sqlStr = " SELECT \n" +
                "\trt.id as rtId,rt.operatetime,rt.pstatus,\n" +
                "\trp.id as rpId,rp.describ,rp.imgPath,rp.inTime,\n" +
                "\tbp.personname as operator,\n" +
                "\tins.insnum,\n" +
                "\tib.insname,\n" +
                "\tio.ordernum\n" +
                "\tFROM \n" +
                "\tins_repairtrack rt \n" +
                "\tJOIN ins_repair rp ON rt.repairId  = rp.id\n" +
                "\tJOIN ins_order io ON rp.orderId = io.id\n" +
                "\tJOIN base_person bp ON rt.operaterId = bp.id\n" +
                "\tJOIN ins_instrument ins ON rp.insId = ins.id\n" +
                "\tJOIN ins_baseins ib ON ins.baseInsId = ib.id\n" +
                "\t WHERE rp.id = '"+repairId+"' \n" +
                "\t ORDER BY rt.operatetime  \n ";
        Sql sql = Sqls.queryRecord(sqlStr);
        dao().execute(sql);
        List<Record> recordList = sql.getList(Record.class);
        List<Map<String,Object>> list = new ArrayList<>();
        if(recordList.size() > 0){
            for (Record record : recordList) {
                Map<String,Object> map = new HashMap<>();
                map.put("trackId",record.getString("rtId"));
                map.put("operatetime",record.getString("operatetime"));
                map.put("pstatus",record.getInt("pstatus"));
                map.put("repairId",record.getString("rpId"));
                map.put("describ",record.getString("describ"));
                map.put("imgPath",record.getString("imgPath"));
                map.put("inTime",record.getString("inTime"));
                map.put("operator",record.getString("operator"));
                map.put("insNum",record.getString("insnum"));
                map.put("insName",record.getString("insname"));
                map.put("ordernum",record.getString("ordernum"));
                list.add(map);
            }
        }
        return list;
    }
}
