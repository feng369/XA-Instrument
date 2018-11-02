package cn.wizzer.app.ins.modules.services.impl;

import cn.wizzer.app.ins.modules.models.Ins_toolcabinet;
import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.app.sys.modules.services.SysDictService;
import cn.wizzer.framework.base.ValidatException;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.ins.modules.models.Ins_common;
import cn.wizzer.app.ins.modules.services.InsCommonService;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IocBean(args = {"refer:dao"})
public class InsCommonServiceImpl extends BaseServiceImpl<Ins_common> implements InsCommonService {
    public InsCommonServiceImpl(Dao dao) {
        super(dao);
    }
    @Inject
    private SysDictService sysDictService;

    public List<Map<String, Object>> getCommonTools(String personid, String commonCode, Integer pagenumber, Integer pagesize) {
        if(Strings.isBlank(personid) || Strings.isBlank(commonCode)){
            throw new ValidatException("传入参数不能为空");
        }
        List<Sys_dict> sysDicts = sysDictService.query(Cnd.where("code", "=", commonCode));
        if(sysDicts.size() != 1){
            throw new ValidatException("不能找到唯一的收藏类型");
        }
        List<Map<String, Object>> list =new ArrayList<>();
        Sys_dict sysDict = sysDicts.get(0);
        String sqlStr = "SELECT ic.id,ib.id as insid,ib.insname,ib.insmodel,ib.insnum,ib.insparam,ib.toolPic,ic.number FROM ins_common ic LEFT JOIN ins_baseins ib ON ic.baseinsId = ib.id  WHERE ic.personid = '" +personid+ "' AND ic.commontype = '" +sysDict.getId()+ "' ORDER BY ic.opAt DESC ";
        sqlStr = StringUtil.appendSqlStrByPager(pagenumber,pagesize,sqlStr);
        Sql sql = Sqls.queryRecord(sqlStr);
        dao().execute(sql);
        List<Record> recordList = sql.getList(Record.class);
        if(recordList.size()  > 0){
            for (Record record: recordList) {
                Map<String, Object> map = new HashMap<>();
                map.put("id",record.getString("id"));
                map.put("insid",record.getString("insid"));
                map.put("insname",record.getString("insname"));
                map.put("toolPic",record.getString("toolPic"));
                map.put("insmodel",record.getString("insmodel"));
                map.put("insparam",record.getString("insparam"));
                map.put("insnum",record.getString("insnum"));
                map.put("number",Math.round(record.getDouble("number")));
                list.add(map);
            }
        }
        return list;
    }

    @Aop(TransAop.READ_COMMITTED)
    public void add2Favorite(String personid, String[] baseInsIds, String commonCode) {
        if(Strings.isBlank(personid)){
            throw new ValidatException("传入参数不能为空");
        }
        if(baseInsIds.length  == 0){
            throw new ValidatException("基础工具ID字符串数组为空");
        }

        //数据字典我的收藏(先写死)
        Sys_dict sysDict = sysDictService.fetch(Cnd.where("code", "=", commonCode));
        if(!Strings.isBlank(sysDict.getId()) && baseInsIds.length > 0 ){
            for(String baseInsId: baseInsIds){
                List<Ins_common> insCommonList = this.query(Cnd.where("personid", "=", personid).and("baseinsId", "=", baseInsId).and("commontype","=",sysDict.getId()));
                if(insCommonList.size() > 1){
                    throw  new ValidatException("无法确认唯一的我的收藏工具");
                }
                if(insCommonList.size() == 0){
                    Ins_common insCommon =new Ins_common();
                    insCommon.setBaseinsId(baseInsId);
                    insCommon.setNumber(1);
                    insCommon.setPersonid(personid);
                    insCommon.setCommontype(sysDict.getId());
                    //之前没点过收藏按钮
                    this.insert(insCommon);
                }else{
                    //我的收藏已经存在该工具
                    Ins_common insCommon = insCommonList.get(0);
                    this.update(Chain.make("number",insCommon.getNumber() + 1),Cnd.where("id","=",insCommon.getId()));
                }
            }

        }
    }

    @Aop(TransAop.READ_COMMITTED)
    public void deleteFromMyFavorite(String commonInsId) {
        int delete = this.delete(commonInsId);
        if(delete == 0){
            throw new ValidatException("未找到对应的收藏表工具");
        }
    }
}
