package cn.wizzer.app.ins.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.ins.modules.models.Ins_lock;

public interface InsLockService extends BaseService<Ins_lock>{

    void updateLockStatus(String spaceNum, Integer doOrder);
}
