package cn.wizzer.app.logistics.modules.services.impl;

import cn.wizzer.app.base.modules.models.base_person;
import cn.wizzer.app.base.modules.models.base_place;
import cn.wizzer.app.base.modules.services.BasePersonService;
import cn.wizzer.app.base.modules.services.BasePlaceService;
import cn.wizzer.app.logistics.modules.models.logistics_Deliveryorder;
import cn.wizzer.app.logistics.modules.models.logistics_order;
import cn.wizzer.app.logistics.modules.services.LogisticsDeliveryorderService;
import cn.wizzer.app.logistics.modules.services.LogisticsOrderService;
import cn.wizzer.app.logistics.modules.services.LogisticsOrderentryService;
import cn.wizzer.app.web.modules.controllers.open.api.Datatime.newDataTime;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.logistics.modules.models.logistics_sendtrace;
import cn.wizzer.app.logistics.modules.services.LogisticsSendtraceService;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.trans.Trans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@IocBean(args = {"refer:dao"})
public class LogisticsSendtraceServiceImpl extends BaseServiceImpl<logistics_sendtrace> implements LogisticsSendtraceService {
    public LogisticsSendtraceServiceImpl(Dao dao) {
        super(dao);
    }

    @Inject
    private BasePersonService basePersonService;
    @Inject
    private BasePlaceService basePlaceService;
    @Inject
    private LogisticsOrderService logisticsOrderService;
    @Inject
    private LogisticsDeliveryorderService logisticsDeliveryorderService;
    @Inject
    private LogisticsOrderentryService logisticsOrderentryService;


    @Aop(TransAop.READ_COMMITTED)
    public void addOne(logistics_Deliveryorder deliveryorder, logistics_order order, String curPostion) {
        logistics_sendtrace sendtrace = new logistics_sendtrace();
        if (order != null) {
            sendtrace.setOrderid(order.getId());
            sendtrace.setOrderpstatus(order.getPstatus());

        }
        if (!Strings.isBlank(curPostion)) {
            sendtrace.setPosition(curPostion);
        }
        if (deliveryorder != null) {
            sendtrace.setDeliveryorderid(deliveryorder.getId());
            sendtrace.setPersonid(deliveryorder.getPersonid());

        }
        sendtrace.setIntime(newDataTime.getDateYMDHMS());
        this.insert(sendtrace);
    }

    @Aop(TransAop.READ_COMMITTED)
    public void insertSendTrace(String orderid, String personid, String position) {
        logistics_sendtrace sendtrace = new logistics_sendtrace();
        sendtrace.setPosition(position);
        sendtrace.setPersonid(personid);
        sendtrace.setOrderid(orderid);
        logistics_order order = logisticsOrderService.fetch(orderid);
        if(order != null){
            sendtrace.setDeliveryorderid(order.getDeliveryorderid());
            sendtrace.setOrderpstatus(order.getPstatus());
        }
        sendtrace.setIntime(newDataTime.getDateYMDHMS());
        this.insert(sendtrace);
    }


}