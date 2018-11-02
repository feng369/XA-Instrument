package cn.wizzer.app.ins.modules.services.impl;

import cn.wizzer.app.ins.modules.models.Ins_space;
import cn.wizzer.app.ins.modules.services.InsSpaceService;
import cn.wizzer.framework.base.ValidatException;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.ins.modules.models.Ins_lock;
import cn.wizzer.app.ins.modules.services.InsLockService;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

import java.util.List;

@IocBean(args = {"refer:dao"})
public class InsLockServiceImpl extends BaseServiceImpl<Ins_lock> implements InsLockService {
    public InsLockServiceImpl(Dao dao) {
        super(dao);
    }
    @Inject
    private InsSpaceService insSpaceService;
    @Inject
    private InsLockService lockService;
        @Aop(TransAop.READ_COMMITTED)
        public void updateLockStatus(String spaceNum, Integer doOrder){
            if(Strings.isBlank(spaceNum)){
                throw new ValidatException("仓位编号不能为空");
            }
            if(!(doOrder == 0 || doOrder == 1)){
                throw new ValidatException("不明确的操作指令");
            }
            Ins_space space = insSpaceService.fetch(Cnd.where("spacenum", "=", spaceNum));
            if(doOrder == 0 && space.getPstatus() == doOrder){
                throw new ValidatException("请勿重复执行关门指令");
            }
            if(doOrder == 1 && space.getPstatus() == doOrder){
                throw new ValidatException("请勿重复执行开门指令");
            }
            if(space != null){
            /*    this.update(Chain.make("pstatus",doOrder),Cnd.where("id","=",lock.getId()));*/
                insSpaceService.updateSpaceStatus(space,doOrder);
            }
        }
}
