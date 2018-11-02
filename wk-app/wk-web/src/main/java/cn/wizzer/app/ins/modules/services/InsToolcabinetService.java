package cn.wizzer.app.ins.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.ins.modules.models.Ins_toolcabinet;

import java.util.List;
import java.util.Map;

public interface InsToolcabinetService extends BaseService<Ins_toolcabinet>{

    List<Map<String,Object>> getToolBoxList(String personid, Integer pagenumber, Integer pagesize);

    void updateOrDeleteToolBoxData(String personid,String[] boxIds, double number, Integer doOrder);

    void add2ToolBox(String personid, String baseInsId);

    Map<String,Object> getTypeCountOfToolBox(String personid);
}
