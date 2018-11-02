package cn.wizzer.app.ins.modules.services.impl;

import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.ins.modules.models.Ins_transmit;
import cn.wizzer.app.ins.modules.services.InsTransmitService;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Dao;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@IocBean(args = {"refer:dao"})
public class InsTransmitServiceImpl extends BaseServiceImpl<Ins_transmit> implements InsTransmitService {
    public InsTransmitServiceImpl(Dao dao) {
        super(dao);
    }

    @Aop(TransAop.READ_COMMITTED)
    public void addTransmit(String newPersonId, String oldPersonId, String orderEntryId, String opertetime, String dateflag, String oldOrderEntryId) {
        Ins_transmit insTransmit = new Ins_transmit();
        insTransmit.setOperatetime(opertetime);
        insTransmit.setOperator(newPersonId);
        insTransmit.setPasser(oldPersonId);
        insTransmit.setOrderentryId(orderEntryId);
        insTransmit.setDateflag(dateflag);
        insTransmit.setOldOrderentryId(oldOrderEntryId);
        this.insert(insTransmit);
    }
}
