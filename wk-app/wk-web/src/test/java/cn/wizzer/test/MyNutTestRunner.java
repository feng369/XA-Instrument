package cn.wizzer.test;

import cn.wizzer.app.web.commons.core.Module;
import org.junit.runners.model.InitializationError;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.mock.NutTestRunner;

public class MyNutTestRunner extends NutTestRunner {
    public MyNutTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    public Class<?> getMainModule() {
        return Module.class;
    }

    /**
     * 可覆盖createIoc,实现参数覆盖, bean替换,等定制.
     */
    protected Ioc createIoc() {
        Ioc ioc = super.createIoc();
        PropertiesProxy conf = ioc.get(PropertiesProxy.class, "conf");
        /*
        conf.put("db.url", "jdbc:mysql://597c002394431.gz.cdb.myqcloud.com:5286/nutzwk-xian?useUnicode=true&characterEncoding=utf8&useSSL=false");
        conf.put("db.username","root");
        conf.put("db.password","bv_1qazxsw2");
        */
        conf.put("db.url", "jdbc:mysql://127.0.0.1:3306/xian?useUnicode=true&characterEncoding=utf8&useSSL=false");
        conf.put("db.username", "root");
        conf.put("db.password", "admin");
        /*
        conf.put("db.url", "jdbc:mysql://192.168.124.200:3306/xianins-test?useUnicode=true&characterEncoding=utf8");
        conf.put("db.username","root");
        conf.put("db.password","wkroot");
        */
        return ioc;
    }
}
