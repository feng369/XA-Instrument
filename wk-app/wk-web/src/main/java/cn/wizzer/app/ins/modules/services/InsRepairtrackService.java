package cn.wizzer.app.ins.modules.services;

import cn.wizzer.app.ins.modules.models.Ins_repair;
import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.ins.modules.models.Ins_repairtrack;

import java.util.List;
import java.util.Map;

public interface InsRepairtrackService extends BaseService<Ins_repairtrack>{

    void insertTrackRecord(String repairId, String personId, String dateYMDHMS, int pstatus);

    List<Map<String,Object>> getRepairTracks(String repairId);
}
