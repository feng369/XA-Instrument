package cn.wizzer.app.ins.modules.services.impl;

import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.ins.modules.models.Ins_baseins_space;
import cn.wizzer.app.ins.modules.services.InsBaseinsSpaceService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
public class InsBaseinsSpaceServiceImpl extends BaseServiceImpl<Ins_baseins_space> implements InsBaseinsSpaceService {
    public InsBaseinsSpaceServiceImpl(Dao dao) {
        super(dao);
    }
}
