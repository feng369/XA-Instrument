package cn.wizzer.app.ins.modules.services.impl;

import cn.wizzer.app.ins.modules.models.Ins_baseins;
import cn.wizzer.app.ins.modules.models.Ins_space;
import cn.wizzer.app.ins.modules.services.InsBaseinsService;
import cn.wizzer.app.ins.modules.services.InsInstrumentService;
import cn.wizzer.app.ins.modules.services.InsSpaceService;
import cn.wizzer.framework.base.ValidatException;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.ins.modules.models.Ins_toolcabinet;
import cn.wizzer.app.ins.modules.services.InsToolcabinetService;
import cn.wizzer.framework.util.StringUtil;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import java.util.*;

@IocBean(args = {"refer:dao"})
public class InsToolcabinetServiceImpl extends BaseServiceImpl<Ins_toolcabinet> implements InsToolcabinetService {
    public InsToolcabinetServiceImpl(Dao dao) {
        super(dao);
    }
    @Inject
    private InsSpaceService insSpaceService;
    @Inject
    private InsBaseinsService insBaseinsService;
    @Inject
    private InsInstrumentService insInstrumentService;
    public List<Map<String, Object>> getToolBoxList(String personid, Integer pagenumber, Integer pagesize) {
        if (Strings.isBlank(personid)) {
            throw new ValidatException("传入参数不能为空");
        }
        List<Ins_toolcabinet> toolcabinetList = this.query(Cnd.where("personid", "=", personid).orderBy("opAt","DESC"),"insBaseins", StringUtil.getPagerObject(pagenumber,pagesize));
        List<Map<String, Object>> list = new ArrayList<>();
        if (toolcabinetList.size() > 0) {
            for (Ins_toolcabinet toolcabinet : toolcabinetList) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", toolcabinet.getId());
                map.put("number", Math.round(toolcabinet.getNumber()));
                if(toolcabinet.getInsBaseins() == null){
                    map.put("insId", "");
                }else{
                    Ins_baseins insBaseins = toolcabinet.getInsBaseins();
                    //已上线的工具库存
                    map.put("insNumber",insBaseins.getUseableCount());
                    map.put("insId", toolcabinet.getInsBaseins().getId());
                }
                map.put("insnum",toolcabinet.getInsBaseins() == null ? "":toolcabinet.getInsBaseins().getInsnum());
                map.put("insname",toolcabinet.getInsBaseins() == null? "":toolcabinet.getInsBaseins().getInsname());
                map.put("insmodel",toolcabinet.getInsBaseins() == null? "":toolcabinet.getInsBaseins().getInsmodel());
                map.put("insparam",toolcabinet.getInsBaseins() == null? "":toolcabinet.getInsBaseins().getInsparam());
                map.put("toolPic",toolcabinet.getInsBaseins() == null? "":toolcabinet.getInsBaseins().getToolPic());
                map.put("pstatus",toolcabinet.getPstatus());
                list.add(map);
            }
        }
        return list;
    }

    @Aop(TransAop.READ_COMMITTED)
    public void updateOrDeleteToolBoxData(String personid, String[] boxIds, double number, Integer doOrder){
        //保留参数
        /*
        if(Strings.isBlank(personid)){
            throw new ValidatException("传入人员ID为空");
        }
        */
        if (doOrder ==1 && number <= 0 ){
            throw new ValidatException("编辑数量不能小于1");
        }
        if(doOrder == null){
            throw new ValidatException("请传入指令操作指令类型");
        }
        if(doOrder == 0){
            if(boxIds.length == 0){
                throw new ValidatException("请先选择一条数据进行删除");
            }
             //删除
            this.delete(boxIds);
            return;
        }else if(doOrder == 1){
            if(boxIds.length != 1){
                 throw new ValidatException("请选择一条数据进行编辑");
            }
            int update = this.update(Chain.make("number", number), Cnd.where("id", "=", boxIds[0]));
            if(update == 0){
                throw new ValidatException("未找到编辑的工具箱数据");
            }
            return ;
        }
        throw new ValidatException("未找到对应的操作指令");
    }

    @Aop(TransAop.READ_COMMITTED)
    public void add2ToolBox(String personid, String baseInsId) {
        if(Strings.isBlank(personid) || Strings.isBlank(baseInsId)){
                throw new ValidatException("传入参数不能为空");
        }
        int baseInsCount = insBaseinsService.count(Cnd.where("id", "=", baseInsId));
        if(baseInsCount == 0){
            throw new ValidatException("找不到相应的基本工具");
        }
        List<Ins_toolcabinet> toolcabinets = this.query(Cnd.where("personid", "=", personid).and("baseInsId", "=", baseInsId));

        if(toolcabinets.size() > 1){
            throw  new ValidatException("无法确认唯一的工具箱工具");
        }

        if(toolcabinets.size() == 0){
            //第一次点添加按钮
            Ins_toolcabinet toolcabinet = new Ins_toolcabinet();
            toolcabinet.setBaseInsId(baseInsId);
            toolcabinet.setPersonid(personid);
            toolcabinet.setNumber(1);
            toolcabinet.setPstatus(0);
            this.insert(toolcabinet);
            return;
        }
        //之前已经添加过相同基本工具
        Ins_toolcabinet insToolcabinet = toolcabinets.get(0);
        this.update(Chain.make("number",insToolcabinet.getNumber() + 1),Cnd.where("id","=",insToolcabinet.getId()));
    }

    public Map<String, Object> getTypeCountOfToolBox(String personid) {
        return null;
    }
}
