package cn.wizzer.app.ins.modules.services.impl;

import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.ins.modules.models.Ins_rfid;
import cn.wizzer.app.ins.modules.services.InsRfidService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
public class InsRfidServiceImpl extends BaseServiceImpl<Ins_rfid> implements InsRfidService {
    public InsRfidServiceImpl(Dao dao) {
        super(dao);
    }
}
