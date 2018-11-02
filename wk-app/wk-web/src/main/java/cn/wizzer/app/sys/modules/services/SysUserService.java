package cn.wizzer.app.sys.modules.services;

import cn.wizzer.app.sys.modules.models.Sys_menu;
import cn.wizzer.app.sys.modules.models.Sys_tempuser;
import cn.wizzer.app.sys.modules.models.Sys_user;
import cn.wizzer.app.web.commons.base.Globals;
import cn.wizzer.framework.base.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * Created by wizzer on 2016/12/22.
 */
public interface SysUserService extends BaseService<Sys_user> {
    List<String> getRoleCodeList(Sys_user user);

    List<Sys_menu> getMenusAndButtons(String userId);

    List<Sys_menu> getDatas(String userId);

    void fillMenu(Sys_user user);

    void deleteById(String userId);

    void deleteByIds(String[] userIds);

    Map getUserInfo(Sys_user curUser, String clientId);


    void logoutByMobile(Sys_user user);

    Map addUserByMobile(String cutomerid, String  username, String loginname, String password, String jobNumber, String tel,String wxNumber);

    void doChangePassword(Sys_user user, String newPassword);

    Sys_user insertUserByTempUser(Sys_tempuser sysTempuser);

    Map doLoginByMobile(Sys_user user);
}
