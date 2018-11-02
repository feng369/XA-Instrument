package cn.wizzer.app.web.commons.quartz.job;

import cn.wizzer.app.base.modules.models.base_cnctobj;
import cn.wizzer.app.base.modules.services.BaseCnctobjService;
import cn.wizzer.app.getui.GeTuiMessagePush;
import cn.wizzer.app.getui.PushtoSingle;
import cn.wizzer.app.ins.modules.models.Ins_order;
import cn.wizzer.app.ins.modules.models.Ins_orderentry;
import cn.wizzer.app.ins.modules.services.InsOrderService;
import cn.wizzer.app.ins.modules.services.InsOrderentryService;
import cn.wizzer.app.sys.modules.models.Sys_task;
import cn.wizzer.app.sys.modules.models.Sys_user;
import cn.wizzer.app.sys.modules.services.SysTaskService;
import cn.wizzer.app.sys.modules.services.SysUserService;
import cn.wizzer.framework.base.ValidatException;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.ConnCallback;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@IocBean
public class GetuiExecute implements Job {
    private static final Log log = Logs.get();
    @Inject
    private SysTaskService sysTaskService;
    @Inject
    protected Dao dao;
    @Inject
    private InsOrderService orderService;
    @Inject
    private BaseCnctobjService baseCnctobjService;
    @Inject
    private SysUserService sysUserService;
    @Inject
    private InsOrderentryService orderentryService;

    //秒换毫秒
    public final long ss = 1000;
    //分换毫秒
    public final long mm = 1000 * 60;
    //时换毫秒
    public final long hh = 1000 * 60 * 60;

    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.debug("啦啦啦");
        JobDataMap data = context.getJobDetail().getJobDataMap();
        String taskId = context.getJobDetail().getKey().getName();
        try {
            //未发过透传消息的借出的工具
            List<Ins_orderentry> orderentries = orderentryService.query(Cnd.where("pstatus", "=", 1).and("msgStatus","=",0));
            if ( orderentries.size() > 0 ) {
                orderentryService.dao().run(new ConnCallback() {
                    public void invoke(Connection conn) throws Exception {
                        PreparedStatement psUpdate = null;
                        try {
                            String sql = " UPDATE ins_orderentry SET msgStatus = ?  WHERE id = ? ";
                            psUpdate = conn.prepareStatement(sql);
                            for (int i = 0; i < orderentries.size(); i++){
                                //订单操作人
                                Ins_orderentry orderentry = orderentries.get(i);
                                String operator = orderentry.getOperator();
                                List<base_cnctobj> cnctobjs = baseCnctobjService.query(Cnd.where("personId", "=", operator));
                                if (cnctobjs.size() != 1) {
                                    throw new ValidatException("不能确定唯一的联系对象");
                                }
                                base_cnctobj cnctobj = cnctobjs.get(0);
                                String userId = cnctobj.getUserId();
                                Sys_user sysUser = sysUserService.fetch(userId);
//                                int msgStatus = orderentry.getMsgStatus();
//                                int entryStatus = orderentry.getPstatus();
                                //下单时间
                                String orderId = orderentry.getOrderid();
                                Ins_order order = orderService.fetch(orderId);
                                if (order != null) {
                                    String operatetime = order.getOperatetime();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    if (!Strings.isBlank(operatetime)){
                                        Date orderDate = sdf.parse(operatetime);
                                        if( orderDate != null) {
                                            long orderTime   = orderDate.getTime();
                                            long currentTime = System.currentTimeMillis();
                                            long distance    = orderTime - currentTime;
                                            //6小时
                                            /*
                                            if (6 * hh <= distance && 8 * hh > distance) {
                                                if (msgStatus == 0) {
                                                    //发送推送
//                                                    PushtoSingle.push2Single("下单提醒", "您的订单预约时间快到期了!请尽快取出订单工具!", sysUser.getClientId());
                                                    //更改推送状态
                                                    psUpdate.setString(1, "1");
                                                    psUpdate.setString(2, order.getId());
                                                    psUpdate.addBatch();
                                                }
                                            }
                                            if (8 * hh <= distance ){
                                                if (msgStatus != 2) {
                                                    //发送推送
                                                    PushtoSingle.push2Single("下单超时", "您的订单预约时间已到期!", sysUser.getClientId());
                                                    //更改推送状态
                                                    psUpdate.setString(1, "1");
                                                    psUpdate.setString(2, order.getId());
                                                    psUpdate.addBatch();
                                                    //暂未有其它关闭订单的操作,后续优化
                                                }
                                            }
                                            */
                                            //10s ~ 30s
                                            /*if(10 * ss <= distance &&  30 * ss > distance ){
                                                if( msgStatus == 0 ){
                                                    //发送推送
                                                    GeTuiMessagePush.pushMessage("测试","西安工具,请尽快从智能柜中拿出相应工具","工具已下单","西安工具,请尽快从智能柜中拿出相应工具",sysUser.getClientId());
//                                                    PushtoSingle.push2Single("下单提醒", "您的订单预约时间快到期了!请尽快取出订单工具!", sysUser.getClientId());
                                                    //更改推送状态
                                                    psUpdate.setString(1, "1");
                                                    psUpdate.setString(2, order.getId());
                                                    psUpdate.addBatch();
                                                }
                                            }
                                            */
                                            if ( 1 * mm >= distance ) {
                                                    //发送推送
                                                    GeTuiMessagePush.pushMessage("工具已下单","西安工具,请尽快从智能柜中拿出相应工具!订单编号["+order.getOrdernum()+"]",sysUser.getClientId());
                                                    //PushtoSingle.push2Single("下单提醒", "您的订单预约时间快到期了!请尽快取出订单工具!", sysUser.getClientId());
                                                    //更改推送状态
                                                    psUpdate.setString(1, "1");
                                                    psUpdate.setString(2, orderentry.getId());
                                                    psUpdate.addBatch();
                                            }
                                            /*
                                            if ( 8 * hh <= distance) {/
                                                if (msgStatus != 2) {
                                                    //发送推送
                                                    PushtoSingle.push2Single("下单超时", "您的订单预约时间已到期!", sysUser.getClientId());
                                                    //更改推送状态
                                                    psUpdate.setString(1, "1");
                                                    psUpdate.setString(2, order.getId());
                                                    psUpdate.addBatch();
                                                    //暂未有其它关闭订单的操作,后续优化
                                                }
                                            }
                                            */
                                        }
                                    }
                                }
                            }
                            psUpdate.executeBatch();
                        } catch (Exception e) {

                        } finally {
                            if (psUpdate != null)
                                psUpdate.close();
                        }
                    }
                });
            }
            dao.update(Sys_task.class, Chain.make("exeAt", (int) (System.currentTimeMillis() / 1000)).add("exeResult", "执行成功"), Cnd.where("id", "=", taskId));
        } catch (Exception e) {
            log.error("定时推送失败:" + e.getMessage());
            dao.update(Sys_task.class, Chain.make("exeAt", (int) (System.currentTimeMillis() / 1000)).add("exeResult", "执行失败"), Cnd.where("id", "=", taskId));
        }
    }
    /*
    public static void main(String[] args) throws ParseException{
        String after = "2018-10-23 10:20:00";
        String before = "2018-10-23 10:19:23";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long afterTime = sdf.parse(after).getTime();
        long beforeTime = sdf.parse(before).getTime();
        System.out.println(afterTime - beforeTime);
    }
    */
}
