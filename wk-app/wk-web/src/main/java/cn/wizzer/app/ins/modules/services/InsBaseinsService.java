package cn.wizzer.app.ins.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.ins.modules.models.Ins_baseins;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import org.nutz.dao.Cnd;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.upload.TempFile;

import java.util.List;
import java.util.Map;

public interface InsBaseinsService extends BaseService<Ins_baseins>{

    Map<String,Object> uploadBaseIns(TempFile tf, String baseInsId);

    List<Map<String,Object>> getBaseInsList(String childDictId, Integer pagenumber, Integer pagesize);

    void bindSpace(String[] baseInsIds, String[] spaceId);

    NutMap getList(int length, int start, int draw, List<DataTableOrder> orders, List<DataTableColumn> columns, Cnd cnd, String s, String insnum, String keyword, String cabinetId, String spaceId);

    void unbindSpaces(String[] baseInsIds);
}
