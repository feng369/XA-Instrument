package cn.wizzer.app.sys.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.sys.modules.models.Sys_wx;
import org.nutz.dao.Cnd;

import java.util.Set;

public interface SysWxService extends BaseService<Sys_wx>{

    String getTokenOrNew(Cnd cnd);
    /**
     *发送文本微信信息给指定用户
     */
      String sendMessageToUser(String[] userIds,String appnum,String content);

      void sendWxMessageAsy(Set<String> userIds,String content);
    String addWXUser(String username,String mobile);

}
