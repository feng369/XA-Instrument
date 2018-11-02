package cn.wizzer.app.ins.modules.services.impl;

import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.ins.modules.models.Ins_rfhead;
import cn.wizzer.app.ins.modules.services.InsRfheadService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
public class InsRfheadServiceImpl extends BaseServiceImpl<Ins_rfhead> implements InsRfheadService {
    public InsRfheadServiceImpl(Dao dao) {
        super(dao);
    }
}
