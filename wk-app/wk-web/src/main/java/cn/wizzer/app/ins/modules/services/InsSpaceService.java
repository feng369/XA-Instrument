package cn.wizzer.app.ins.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.ins.modules.models.Ins_space;
import org.nutz.lang.util.NutMap;

import java.util.List;
import java.util.Map;

public interface InsSpaceService extends BaseService<Ins_space>{
    List<Map<String,Object>> getSpaceList(Integer pagenumber, Integer pagesize);

    NutMap getinfo(String type, String lockstate, String time, NutMap re);
    void updateSpaceStatus(Ins_space id, Integer doOrder);

    /**
     * 绑定智能锁
     * @param spaceId
     * @param lockId
     */
    void updateLock(String spaceId, String lockId);

    void updateLockStatus(String cabinetId,String spaceId, String pstatus);
}
