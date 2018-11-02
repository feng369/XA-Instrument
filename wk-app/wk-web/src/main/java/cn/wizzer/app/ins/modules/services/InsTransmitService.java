package cn.wizzer.app.ins.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.ins.modules.models.Ins_transmit;

public interface InsTransmitService extends BaseService<Ins_transmit>{

    void addTransmit(String newPersonId, String oldPersonId, String id, String dateYMDHMS, String dateflag, String oldOrderEntryId);

}
