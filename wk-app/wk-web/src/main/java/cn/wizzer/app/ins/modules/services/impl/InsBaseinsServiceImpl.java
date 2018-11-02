package cn.wizzer.app.ins.modules.services.impl;

import cn.wizzer.app.ins.modules.models.Ins_baseins_space;
import cn.wizzer.app.ins.modules.services.InsBaseinsSpaceService;
import cn.wizzer.app.ins.modules.services.InsSpaceService;
import cn.wizzer.app.web.commons.base.Globals;
import cn.wizzer.framework.base.ValidatException;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.ins.modules.models.Ins_baseins;
import cn.wizzer.app.ins.modules.services.InsBaseinsService;
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
import org.nutz.mvc.upload.TempFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IocBean(args = {"refer:dao"})
public class InsBaseinsServiceImpl extends BaseServiceImpl<Ins_baseins> implements InsBaseinsService {
    public InsBaseinsServiceImpl(Dao dao) {
        super(dao);
    }

    @Inject
    private InsSpaceService insSpaceService;
    @Inject
    private InsBaseinsSpaceService insBaseinsSpaceService;

    @Aop(TransAop.READ_COMMITTED)
    public Map<String, Object> uploadBaseIns(TempFile tf, String baseInsId) {
        try {
            Map<String, Object> map = new HashMap();
            String[] postfix = tf.getSubmittedFileName().split("\\.");
            if (postfix.length < 1) {
                throw new ValidatException("上传失败");
            }
            String imgPath = Globals.AppRoot + Globals.AppUploadPath + "/uploadIns/";
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
            Ins_baseins insBaseins = this.fetch(baseInsId);
            //删除原来的图片
            if (!Strings.isBlank(insBaseins.getToolPic())) {
                //一张
                deleteOldPicture(insBaseins.getToolPic(), imgPath);
            }
            if (insBaseins != null) {
                this.update(Chain.make("toolPic", Globals.AppUploadPath + "/uploadIns/" + insPhoto), Cnd.where("id", "=", baseInsId));
            }
            return map;
        } catch (Exception e) {
            throw new ValidatException("上传失败");
        }
    }


    public List<Map<String, Object>> getBaseInsList(String childDictId, Integer pagenumber, Integer pagesize) {
        if (Strings.isBlank(childDictId)) {
            throw new ValidatException("传入参数不能为空");
        }
        String sqlStr = "SELECT  ib.insname,ib.insmodel,ib.id,ib.insnum,ib.insparam,ib.toolPic,ib.useableCount,su.name," +
                "sp.spacename FROM ins_baseins ib LEFT JOIN sys_unit su ON ib.insunit = su.id " +
                "LEFT JOIN sys_dict sd ON ib.instype = sd.id " +
                " LEFT JOIN ins_baseins_space ibp ON   ib.id = ibp.baseInsId " +
                " LEFT JOIN ins_space sp ON sp.id = ibp.spaceId " +
                " WHERE  ib.instype = '" + childDictId + "' " ;
//                " GROUP BY CONCAT(ib.insname,ib.insmodel)";
        List<Map<String,Object>> list = new ArrayList<>();
        sqlStr = StringUtil.appendSqlStrByPager(pagenumber, pagesize, sqlStr);
        Sql sql = Sqls.queryRecord(sqlStr);
        dao().execute(sql);
        List<Record> recordList = sql.getList(Record.class);
        if (recordList.size() > 0) {
            for (Record record : recordList) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", record.getString("id"));
                map.put("insnum", record.getString("insnum"));
                map.put("insname", record.getString("insname"));
                map.put("insparam", record.getString("insparam"));
                map.put("insmodel", record.getString("insmodel"));
                map.put("insunitName", record.getString("name"));
                map.put("toolPic", record.getString("toolPic"));
                map.put("spaceName", record.getString("spacename"));
                map.put("useableCount", record.getInt("useableCount"));
                list.add(map);
            }
        }
        return list;
    }

    @Aop({TransAop.READ_COMMITTED})
    public void bindSpace(String[] baseInsIds, String[] spaceIds) {
        if (baseInsIds.length < 1) {
            throw new ValidatException("未选择需要绑定的基础工具");
        }
        if (spaceIds.length < 1) {
            throw new ValidatException("未选择需要绑定的仓位");
        }
        for (String baseInsId : baseInsIds) {
            for (String spaceId : spaceIds) {
                int count = insBaseinsSpaceService.count(Cnd.where("baseInsId", "=", baseInsId).and("spaceId", "=", spaceId));

                if (count == 0) {
                    //后续多对多关系时需要优化此处
                    insBaseinsSpaceService.clear(Cnd.where("baseInsId","=",baseInsId));
                    Ins_baseins_space ibs = new Ins_baseins_space();
                    ibs.setSpaceId(spaceId);
                    ibs.setBaseInsId(baseInsId);
                    insBaseinsSpaceService.insert(ibs);
                }
            }
        }
        /*
        if (baseInsIds.length < 1 && Strings.isBlank(spaceId)) {
            throw new ValidatException("传入参数不正确");
        }
        for (String baseInsId: baseInsIds) {
            Ins_baseins insBaseins = this.fetch(baseInsId);
            if (insBaseins != null) {
                this.update(Chain.make("spaceId", spaceId), Cnd.where("id", "=", baseInsId));
            }
        }
        */
    }

    public NutMap getList(int length, int start, int draw, List<DataTableOrder> orders, List<DataTableColumn> columns, Cnd cnd, String keyword, String insnum, String insname, String cabinetId, String spaceId) {
        NutMap re = new NutMap();
        String sqlStr = "\tSELECT \n" +
                "\tib.id,ib.insmodel,ib.insparam,ib.insname,ib.insnum,\n" +
                "\tsu.name as unitname,sd.name as dictname,sp.spacename,\n" +
                "\tca.cabinetname\n" +
                "\tFROM ins_baseins ib \n" +
                "\tLEFT JOIN sys_unit su ON ib.insunit = su.id\n" +
                "\tLEFT JOIN sys_dict sd ON ib.instype = sd.id\n" +
                "\tLEFT JOIN ins_baseins_space bsp ON ib.id = bsp.baseInsId\n" +
                "\tLEFT JOIN ins_space sp ON bsp.spaceId = sp.id\n" +
                "\tLEFT JOIN ins_cabinet ca ON sp.cabinetid = ca.id\n" +
                "\tWHERE 1 = 1 \n" +
                (!Strings.isBlank(keyword) ? " AND ib.insname LIKE '%" + keyword + "%' OR ib.insnum LIKE '%" + keyword + "%'  " : "");
        if (!Strings.isBlank(insnum)) {
            sqlStr += " AND ib.insnum LIKE '%" + insnum + "%' ";
        }
        if (!Strings.isBlank(insname)) {
            sqlStr += " AND ib.insname LIKE '%" + insname + "%' ";
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
    public void unbindSpaces(String[] baseInsIds) {
        StringBuilder sb = new StringBuilder(80);
        sb.append("工具已解绑仓位,无需再次解绑!");
        boolean errorMsg = false;
        for (String baseInsId : baseInsIds) {
            int count = insBaseinsSpaceService.count(Cnd.where("baseInsId", "=", baseInsId));
            if (count == 0) {
                errorMsg = true;
                Ins_baseins baseins = this.fetch(baseInsId);
                if (baseins != null) {
                    sb.append("工具名称:");
                    sb.append(baseins.getInsname()).append(",");
                    sb.append("工具编号:");
                    sb.append(baseins.getInsnum()).append(";");
                }
            }
        }
        if (errorMsg) {
            throw new ValidatException(sb.toString());
        }
        insBaseinsSpaceService.clear(Cnd.where("baseInsId", "IN", baseInsIds));
    }
}
