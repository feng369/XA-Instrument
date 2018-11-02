package cn.wizzer.app.ins.modules.services;

import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.ins.modules.models.Ins_cabinet;

import java.util.List;
import java.util.Map;

public interface InsCabinetService extends BaseService<Ins_cabinet>{

    List<Map<String,Object>> getCabinetList(Integer pagenumber, Integer pagesize);

    Map<String,Object> getAllCabinetInfo();

    void initCabinet(String cabinetId);

    void doLoginByQRcode(String cabinetId, String userId, String key);

    Map<String,Object> listenLogin(String cabinetId);

    void logoutByPad(String cabinetId, String key);
}
