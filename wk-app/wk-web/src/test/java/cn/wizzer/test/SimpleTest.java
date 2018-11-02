package cn.wizzer.test;

import cn.wizzer.app.base.modules.models.base_person;
import cn.wizzer.app.base.modules.models.base_place;
import cn.wizzer.app.base.modules.services.BaseAirmeterialService;
import cn.wizzer.app.base.modules.services.BaseCnctobjService;
import cn.wizzer.app.base.modules.services.BasePersonService;
import cn.wizzer.app.base.modules.services.BasePlaceService;
import cn.wizzer.app.ins.modules.models.*;
import cn.wizzer.app.ins.modules.services.*;
import cn.wizzer.app.logistics.modules.services.*;
import cn.wizzer.app.sys.modules.models.*;
import cn.wizzer.app.sys.modules.services.*;
import cn.wizzer.app.web.modules.controllers.open.api.Datatime.newDataTime;
import cn.wizzer.framework.base.Result;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nutz.dao.*;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.util.*;

@RunWith(MyNutTestRunner.class)
@IocBean// 必须有
public class SimpleTest extends Assert {


    private static final Log log = Logs.get();

    @Inject("refer:$ioc")
    protected Ioc ioc;

    @Inject
    protected Dao dao;


    @Inject
    private BaseCnctobjService baseCnctobjService;

    @Inject
    private SysUserService sysUserService;
    @Inject
    private SysRoleService roleService;
    @Inject
    private BasePersonService basePersonService;
    @Inject
    private InsRfidService insRfidService;
    @Inject
    private InsSpaceService insSpaceService;

    @Test
    public void testUser1() {
        List<Ins_rfid> rfids = insRfidService.query();
        for (Ins_rfid rfid : rfids) {
            System.out.println(rfid);
        }
//        System.out.println(sysUserService.fetch("7732ad1aeb1b470da6e9c86592ea51ab"));
//        System.out.println(roleService.fetch("5c805862fbd24ea49d5afb33e6519981"));
//        System.out.println(basePersonService.fetch("0dfd52a47a1f454b909055ac8c2a35c0"));
       /* List<Map<String, Object>> borrowingOrderEntries = insOrderentryService.getAllBorrowingOrderEntries("4804cad4ebdc475c8535513d5549d986");
        System.out.println(borrowingOrderEntries);*/
//        insOrderService.getOrderAndEntryList("4804cad4ebdc475c8535513d5549d986",1,20,null, cabinetId);
    }


    @Test
    public void test_user() {
        String orderno = "1145202";
        RPCServiceClient serviceClient = null;
        try {
            String wurl = "http://jw_test.shenzhenair.com:8090/MeWebService/services/JIWUService?wsdl";
            String nameSpace = "http://models.peanuts.smartme.szair.com";
            serviceClient = new RPCServiceClient();
            Options options = serviceClient.getOptions();
            // 这一步指定了该服务的提供地址
            EndpointReference targetEPR = new EndpointReference(wurl);
            // 将option绑定到该服务地址
            options.setTo(targetEPR);
            options.setManageSession(true);
            options.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, true);
            options.setTimeOutInMilliSeconds(600000L);
            // 添加具体要调用的方法，这个可以从该服务的wsdl文件中得知
            // 第一个参数是该服务的targetNamespace，第二个为你所要调用的operation名称
            QName namespace = new QName(nameSpace, "zh_material_dispatching");
            // 输入参数
            Long l_orderno = Long.valueOf(orderno);
            Object[] param = new Object[]{l_orderno};
            // 指定返回的数据类型
            Class[] clazz = new Class[]{String.class};
            // 设置返回值类型
            Object[] res;
            res = serviceClient.invokeBlocking(namespace, param, clazz);
            log.info("消息接口返回结果:" + res[0]);
            /*String rest =  "[{\"ORDERNO\":\"1466449\",\"ITEMNO\":\"1\",\"ORDERSTATE\":\"OPEN\",\"PN_L\":\"0-112-0016-2000\",\"PN_R\":\"0\",\"SN\":\"0\",\"BN\":\"0\",\"QTY\":\"1\"},{\"ORDERNO\":\"1466449\",\"ITEMNO\":\"2\",\"ORDERSTATE\":\"OPEN\",\"PN_L\":\"10-60751-1\",\"PN_R\":\"0\",\"SN\":\"0\",\"BN\":\"0\",\"QTY\":\"1\"},{\"ORDERNO\":\"1466449\",\"ITEMNO\":\"3\",\"ORDERSTATE\":\"OPEN\",\"PN_L\":\"101660-205\",\"PN_R\":\"0\",\"SN\":\"0\",\"BN\":\"0\",\"QTY\":\"1\"},{\"ORDERNO\":\"1466449\",\"ITEMNO\":\"4\",\"ORDERSTATE\":\"OPEN\",\"PN_L\":\"9059110-1\",\"PN_R\":\"9059110-1\",\"SN\":\"UNKM1778\",\"BN\":\"1447148\",\"QTY\":\"1\"},{\"ORDERNO\":\"1466449\",\"ITEMNO\":\"5\",\"ORDERSTATE\":\"OPEN\",\"PN_L\":\"8450B5\",\"PN_R\":\"8450B5\",\"SN\":\"8450B5-233\",\"BN\":\"1428418\",\"QTY\":\"1\"},{\"ORDERNO\":\"1466449\",\"ITEMNO\":\"7\",\"ORDERSTATE\":\"OPEN\",\"PN_L\":\"3291238-2\",\"PN_R\":\"0\",\"SN\":\"0\",\"BN\":\"0\",\"QTY\":\"1\"},{\"ORDERNO\":\"1466454\",\"ITEMNO\":\"1\",\"ORDERSTATE\":\"ISSUED\",\"PN_L\":\"3291238-2\",\"PN_R\":\"3291238-2\",\"SN\":\"10383\",\"BN\":\"890129\",\"QTY\":\"1\"}]";
             HashMap[] maps = Json.fromJsonAsArray(HashMap.class,rest);*/
            List<HashMap> lists = Json.fromJsonAsList(HashMap.class, res[0].toString());
            List<Object> onoList = new ArrayList<Object>();//存放需求单号
            Map<String, List<Object>> dataMap = new HashMap<String, List<Object>>();
            for (int i = 0; i < lists.size(); i++) {
                HashMap map = lists.get(i);
                String ono = (String) map.get("ORDERNO");
                String state = (String) map.get("ORDERSTATE");
                //过滤掉已经领料的数据
                if (!"OPEN".equals(state)) {
                    lists.remove(map);
                    i--;
                    continue;
                }
                //按需求单号进行归类
                if (dataMap.containsKey(ono)) {
                    List<Object> lm = dataMap.get(ono);
                    lm.add(map);
                    dataMap.put(ono, lm);
                } else {
                    onoList.add(ono);
                    List<Object> lm = new ArrayList<Object>();
                    lm.add(map);
                    dataMap.put(ono, lm);
                }
            }
            dataMap.put("OrderNoList", onoList);
            System.out.println(onoList);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            if (e instanceof AxisFault) {
                new Exception("ERP系统接口查询失败：" + e.getMessage());
            }
            e.printStackTrace();
            System.out.println(e.toString());
        } finally {
            try {
                if (serviceClient != null)
                    serviceClient.cleanupTransport();
            } catch (AxisFault axisFault) {
                axisFault.printStackTrace();
            }
        }
    }


    @Inject
    private LogisticsOrderService logisticsOrderService;

/*11	唐秀周	H22910	13612955883	深圳港玄		WSTLY1234
12	刘炳汉	H22912	13631585537	深圳港玄		wxid_znhawsdqgigp21
13	谢正南	H22911	15012618389	深圳港玄		A15012618389
14	陈海亮	H22894	13510652127	深圳港玄		chl84181070
15	肖寿杰	H22895	13925294804	深圳港玄		xrj452110105
16	张深顺	H22897	13600199416	深圳港玄		s616773613
17	艾文奎	H22900	13428989197	深圳港玄		wxid_zwklflbivs5n11
18	聂刚	H22899	13628116408	深圳港玄		wxid_bb2aca9webw822
19	周京强	H22898	13352999126	深圳港玄		zjq540523880
20	莫崇杰	H22896	18923717882	深圳港玄		junfeng_2223
21	尹亮		13537733799	深圳港玄		yl13537733799
22	袁海波		17727447362	深圳港玄		wxid_x70am9lt2i9h22
23	苏水清		16675351214	深圳港玄		ssq053
24	谢火腾		15016723263	深圳港玄		wxid_kst466d0fqkj12
25	刘先成		13724260690	深圳港玄		benqyediaoxian
26	叶文君		13360980102	深圳港玄		jun343688782
27	唐境		18121949991	深圳港玄		yhnnxz
28	姚奇		15915468611	深圳港玄		wxid_kdw8wloyvhno21
29	朱少伟		13662298863	深圳港玄		a23314791
30	林伟		18948776124	深圳港玄		LniuniuO888
31	蒋承霖		13544060633	深圳港玄		j1004092671
32	丁海东		13532865195	深圳港玄		AAA13532865195
33	袁明浩		15012622334	深圳港玄		wxid_f2uqgq0d2u0w22
34	晏钦		13923474277	深圳港玄		sz_jysc
35	吴潭		13823610245	深圳港玄		wxid_cvjya79bghw222
36	张志远		15969479229	深圳港玄		RIDVEN
37	卓宇晨		13510945552	深圳港玄		zhuoyc5552
38	刘际德		18719045585	深圳港玄		wxid_g8rwtysl0ea022
39	徐勇		18607659825	深圳港玄		wxid_xflo9c97kgok22
40	万天保		15172767339	深圳港玄		wxid_fod32bqzrm9q22
41	龚国钢		13922933015	深圳港玄		gg_71062670
42	许斌		13724320406	深圳港玄		wxid_ese1w9h8c4ox21
43	温国全		13923486773	深圳港玄		wen13923486773
44	杨能茂		17503018187	深圳港玄		wxid_c59mv7mlap6222
45	刘必胜		13972281123	深圳港玄		wxid_3mskb158upd452
46	何忠权		13600251039	深圳港玄		wxid_pp68ae7d9sjd22
*/

    @Test
    public void testAddPerson() {
//        38	刘际德		18719045585	深圳港玄		wxid_g8rwtysl0ea022
      /*      base_person person = new base_person();
            person.setPersonnum("SZGX-0" +38);
            person.setPersonname("刘际德");
            person.setTel("18719045585");
            person.setWx("wxid_g8rwtysl0ea022");

            person.setPermitNumber("");
            person.setUnitid("ddb726791323403fafd0885ca55f234a");
            person.setAirportid("936546e4f4474ac3bdc9a2495a0adcee");
            base_person basePerson = basePersonService.insert(person);
            List<base_person> list = basePersonService.query(Cnd.where("personname", "=", basePerson.getPersonname()));
            System.out.println(list + "=====================================================================");
            System.out.println(list.size() + "<---------------------------");*/


    }

    @Test
    public void testDeletePerson() {
        base_person basePerson = basePersonService.fetch(Cnd.where("personname", "=", "刘际德"));
        basePersonService.delete(basePerson.getId());
    }

    @Inject
    private LogisticsOrderentryService logisticsOrderentryService;

    //物料清单
    @Test
    public void testEntity() {
        List<Map<String, Object>> entryByOrders = logisticsOrderentryService.getOrderEntryByOrderId("80c1355b29324d1bb9d751f88fcf9176", 1, null);
        for (Map<String, Object> entryByOrder : entryByOrders) {
            System.out.println(entryByOrder + "=================================================================");
        }
    }

    @Test
    public void testABC() {
 /*       Map map = logisticsOrderService.updateDvSteps("73c37ec47800483d989ae4b980ec0f57", "Ghgggg", "HL", "", "858c17e96e83445eb1770fee3d39e154");
        System.out.println(map);*/
    }

    @Test
    public void testAddUser() {
//        Sys_user sysUser = ;
//        System.out.println(sysUserService.fetch("7732ad1aeb1b470da6e9c86592ea51ab"));
//        System.out.println(roleService.fetch("5c805862fbd24ea49d5afb33e6519981"));
        System.out.println(basePersonService.fetch("0dfd52a47a1f454b909055ac8c2a35c0"));



       /* List<base_person> basePersonList = basePersonService.query(Cnd.where("personnum", "LIKE", "%SZGX%"));
        for (base_person basePerson : basePersonList) {
            //1.用户表
            Sys_user user = new Sys_user();
            user.setLoginname(basePerson.getTel());
            user.setUsername(basePerson.getPersonname());
            user.setPassword("888888");
            RandomNumberGenerator rng = new SecureRandomNumberGenerator();
            String salt = rng.nextBytes().toBase64();
            String hashedPasswordBase64 = new Sha256Hash(user.getPassword(), salt, 1024).toBase64();
            user.setSalt(salt);
            user.setPassword(hashedPasswordBase64);
            user.setLoginPjax(true);
            user.setLoginCount(0);
            user.setLoginAt(0);
            sysUserService.insert(user);
            //2.关联对象
            base_cnctobj cnc = new base_cnctobj();
            cnc.setPersonId(basePerson.getId());
            cnc.setUserId(user.getId());
            cnc.setEmptypeId("a93d62c6f9be4882a1f63c386d85459c");
            baseCnctobjService.insert(cnc);
            //3.权限分配
            String menuIds = "fcacccf28101427295211522f7beb7cd";
            String roleid = "f6a5b8e17743437e80a7524ba8d20774";
            String[] ids = StringUtils.split(menuIds, ",");
            for (String s : ids) {
                if (!Strings.isEmpty(s)) {
                    roleService.insert("sys_user_role", org.nutz.dao.Chain.make("roleId", roleid).add("userId", s));
                }
            }
        }*/
    }

    @Inject
    private BasePlaceService basePlaceService;

    //
    @Test
    public void testAddPlace() {
        base_place basePlace = new base_place();

        /*basePlace.setPlacecode("XK"+"210A");
        basePlace.setPlacename(     "2010A"+"复合材料钢索液压修理间");*/
        /*
        basePlace.setPlacecode("XK"+"207");
        basePlace.setPlacename(     "207"+"复合材料无尘修理间");
        basePlace.setAirportId("936546e4f4474ac3bdc9a2495a0adcee");
        basePlace.setPosition("113.825757,22.631571");
        basePlace.setHasChildren(false);
        basePlace.setOpBy("ffdbc674b3f949f3bb28a98cd55a3946");
        basePlace.setCreater("ffdbc674b3f949f3bb28a98cd55a3946");
        basePlaceService.insert(basePlace);
        */
    }

    @Inject
    private LogisticsDeliveryorderService deliveryorderService;
    @Inject
    private LogisticsSendtraceService logisticsSendtraceService;

    @Test
    public void testOrder() throws ParseException {
      /*
        String personid = "dc068fb98e8c43ecb9b9985b0a1a06e6";
        List<Map<String, Object>> customer = logisticsOrderService.getOrderListOfCustomer(personid);
        for (Map<String, Object> stringObjectMap : customer) {
            System.out.println(stringObjectMap);
        }
        */
        /*Map map = logisticsOrderService.getOrderInfoOfCustomer("a1bf5751ada448198b2a572384eb2bd3");
        System.out.println(map);*/

//        logistics_order ordr = new logistics_order();
//        ordr.setId("a1bf5751ada448198b2a572384eb2bd3");
//        logistics_Deliveryorder deliveryorder = new logistics_Deliveryorder();
//        deliveryorder.setId("f638636340504f32aa0617d9525089c8");
 /*       logistics_order order = logisticsOrderService.fetch("a1bf5751ada448198b2a572384eb2bd3");
        logistics_Deliveryorder deliveryorder = deliveryorderService.fetch("f638636340504f32aa0617d9525089c8");

        logisticsSendtraceService.addOne(deliveryorder,order);*/

  /*  Map map = logisticsOrderService.getOrderInfoOfCustomer("a1bf5751ada448198b2a572384eb2bd3");
        System.out.println(map);*/
        /*Sys_dict sysDict = sysDictService.fetch(Cnd.where("name", "=", "航线运输"));
        if(sysDict != null){
            //订单类型：航线运输
            String stepnum = logisticsOrderstepService.getStepByStepnum("JD", sysDict.getId());
            System.out.println(stepnum);
        }*/

           /*
            Map map = logisticsOrderService.getOrderInfoOfCustomer("a1bf5751ada448198b2a572384eb2bd3");
            System.out.println(map);
        */


//        Sql sql = Sqls.queryRecord("SELECT * FROM sys_dict WHERE id = '36a3a69fab514290bd4256c5def05a48'");
      /*  Sql sql = Sqls.fetchRecord("SELECT * FROM sys_dict WHERE id = '36a3a69fab514290bd4256c5def05a48'");
        dao.execute(sql);
                Record record = sql.getObject(Record.class);
        System.out.println(record.getString("id"));*/
//        System.out.println(record.getString("id"));


        /*logisticsSendtraceService.insertSendTrace("a1bf5751ada448198b2a572384eb2bd3","123","123.11,22.34");*/


        /*List<Map<String, Object>> mapList = logisticsOrderService.getOrderListByPersonId("dc068fb98e8c43ecb9b9985b0a1a06e6", null, null);
        System.out.println(mapList);*/

  /*      Map<String, Object> map = logisticsOrderService.addOrderByMobile("28737b2b73c64cc6b405beacb4d97e51", "6e25c4c2ccfa44caa19580bad4a0e4f5", "8fd7dec8862841e3986acdc2ad4f5f87", "8cba479c3b824c97b7b2e2501c221ee8", "23ba2bd8f9174a87933774806fbf1cce", "2018-06-02+15:00:09", "e34dddbfdc894db087734a475e9475c1", "858c17e96e83445eb1770fee3d39e154", "ffdbc674b3f949f3bb28a98cd55a3946");
        System.out.println(map);*/
        /*Map<String, Object> map = logisticsOrderService.addOrderEntryByMobiile("8c5e529fb3244db3a92e6380e5f61bcd", "物料C", "sahjdagjhdksah", "aksdakjdsadkjasdskj", "akkl", "sajkdsadgjdakjdjsdakjdsj");*/
        /*System.out.println(map);
        logistics_order order = logisticsOrderService.fetch(Cnd.where("id", "=", "8c5e529fb3244db3a92e6380e5f61bcd"));
        System.out.println(order.getLogistics_orderentry());*/

       /* Sys_user curUser = sysUserService.fetch("fcacccf28101427295211522f7beb7cd");
        if(curUser != null){
            System.out.println(curUser);*/
        /*logisticsOrderentryService.editOrderEntriesByMobile("123ABC","ae9f204ccee44f67917ee1d9081c4afe,saghkjaj");*/
        /*List<Map<String, Object>> orderEntryList = logisticsOrderentryService.getOrderEntryList("356e2c5c444d49119c79aeeb88381188,ef792c8b86154d99967fa23ed9e019c3");
         *//*  for (Map<String, Object> stringObjectMap : orderEntryList) {
            System.out.println(stringObjectMap);
        }*//*

        List<Map<String, Object>> placeInfoByMobile = basePlaceService.getPlaceListByMobile("37", "936546e4f4474ac3bdc9a2495a0adcee");

        for (Map<String, Object> stringObjectMap : placeInfoByMobile) {
            System.out.println(stringObjectMap);
        }*/


        /*List<Map<String, Object>> deliveryorderentryList = logisticsDeliveryorderentryService.getDeliveryorderentryList("a1bf5751ada448198b2a572384eb2bd3", null, null);
        for (Map<String, Object> stringObjectMap : deliveryorderentryList) {
            System.out.println(stringObjectMap);
            */
        logisticsDeliveryorderService.haveSendingOrders("1391d418c3ed4e72bca74ce72b983de7");
    }

    @Inject
    private LogisticsOrderstepService logisticsOrderstepService;

    @Inject
    private SysDictService sysDictService;
    @Inject
    private LogisticsDeliveryorderService logisticsDeliveryorderService;
    @Inject
    private LogisticsDeliveryorderentryService logisticsDeliveryorderentryService;

    @Inject
    private BaseAirmeterialService airmeterialService;
    @Inject
    private SysTempuserService sysTempuserService;

    @Test
    public void testSys() {
        /*sysUserService.addUserByMobile(Globals.cutomerid,"AAA","AAA","888888","aaa","15812341234");*/

      /*
        List<Map<String, Object>> list = basePlaceService.bindDDLByMobile("936546e4f4474ac3bdc9a2495a0adcee", null, null);
        for (Map<String, Object> stringObjectMap : list) {
            System.out.println(stringObjectMap);
        }
        */
     /*   try {
            logisticsOrderService.addOrderByMobile("c7fc2c3373254178ae3109ddb95a3944","df1e47b6e05e4f0b8ec86eec248a606d","7ff406d582594d6dace0a32a994c4f2e","23ba2bd8f9174a87933774806fbf1cce","f4ac901a6a944b85989c5f6dae533899","e34dddbfdc894db087734a475e9475c1","f61f152536ee48d8be7c85610a6987d4","");
        } catch (ParseException e) {
            e.printStackTrace();
        }


        Map<String, Object> idByMaterielNum = airmeterialService.getIdByMaterielNum("024147-000");
        System.out.println(idByMaterielNum);*/
      /*  Sys_tempuser tempuser = sysTempuserService.fetch(Cnd.where(Cnd.exps("username", "=", "哈哈").and("permitNumber", "=", "12345")).or("loginname", "=", "哈哈"));
        System.out.println(tempuser);*/

        /*
        List<Map<String, Object>> ss = logisticsOrderentryService.getOrderEntryByOrderId("2efeb614f4184ac5b7cf996c0741c4d2", null, null);
        for (Map<String, Object> s : ss) {
            System.out.println(s);
        }
        */

//        SELECT * FROM base_person WHERE  personnum = 'SZGX-034'
//        basePersonService.clear(Cnd.where("personnum","=","SZGX-034"));
    }

    @Inject
    private SysWxUserService sysWxUserService;
    @Inject
    private SysWxService sysWxService;
    @Inject
    private SysWxDeptService sysWxDeptService;


    @Inject
    private SysMenuService menuService;

    @Test
    public void testWx() {
//        sysWxUserService.download();
//         WHERE agentid='memo' AND corpid='ww60fc4401761a0e9f '
        /*
        List<Sys_wx> list = sysWxService.query(Cnd.where("agentid","=","memo").and("corpid","=","ww60fc4401761a0e9f"));
        System.out.println(list);
        System.out.println(list.size());*/
  /*      Set<String> sets = new HashSet<>();
        sets.add("38da70125db946709ea23c4dec886597");
        sets.add("cf7d7f34fa4e475e89533d7948542593");
        sets.add("a5ee5a274ef04561bb06102d6b0ac5d6");
        sysWxService.sendWxMessageAsy(sets,"您的订单已到达119机位,请及时签收!配送员[何中权],联系电话:13600251039");*/
       /*
        String userid = sysWxService.addWXUser("蔡巍", "18571532922");
        System.out.println(userid);
        */
        /*Sys_menu menu = menuService.fetch("c1543aa9a27a4c7db5655571db58b445");
        menuService.deleteAndChild(menu);*/
    }

    @Test
    public void testWxP() {
        Sys_user sysUser = sysUserService.fetch(Cnd.where("username", "=", "测试"));
      /*  String name = "测试";
        Sys_user sysUser = sysUserService.fetch(Cnd.where("username", "=", "测试"));
        System.out.println(sysUser.getId());*/

      /*Sys_wx_user user = new Sys_wx_user();
      user.setName("罗焱");
      user.setUserid("LuoYan");
      user.setDepartment("[1,4]");
      user.setGender("0");
      user.setMobile("18826258451");
      user.setStatus("1");
      sysWxUserService.insert(user);*/
    }

    @Test
    public void testIns() throws ParseException {
        /*List<Map<String, Object>> dictToolList = sy
        sDictService.getDictToolList("toolType");
        for (Map<String, Object> stringObjectMap : dictToolList) {
            System.out.println(stringObjectMap);
        }*/

        /*sysDictService.update(Chain.make("airportId","936546e4f4474ac3bdc9a2495a0adcee"),Cnd.where("id","is NOT",null));*/
        /*List<Map<String, Object>> kidToolList = sysDictService.getKidToolList("936546e4f4474ac3bdc9a2495a0adcee", "d0f4f1d216e8446d8dbf601990f012d4");
        for (Map<String, Object> obj : kidToolList) {
            System.out.println(obj);
        }*/

     /*   List<Map<String, Object>> instrumentList = insInstrumentService.getInstrumentList("b8cbdffbf6b744c094c24eade4390b83", null, null);
        System.out.println(instrumentList.size() + "====================");
        for (Map<String, Object> map : instrumentList) {
            System.out.println(map);
        }*/
/*        List<Map<String, Object>> dictToolList = sysDictService.getDictToolList("936546e4f4474ac3bdc9a2495a0adcee", null);
        System.out.println(dictToolList);*/

      /*  List<Map<String, Object>> mapList = insCommonService.getCommonTools("11", "dc9da33153af4d02ad1fb1f05bbc11f5", null, null);
        System.out.println(mapList);*/
/*        List<Map<String, Object>> mapList = insBaseinsService.getBaseInsList("b8cbdffbf6b744c094c24eade4390b83", null, null);
        System.out.println(mapList);*/

//        insToolcabinetService.add2ToolBox("11","11");
//        insCommonService.add2MyFavorite("11","11");

    /*    List<Map<String, Object>> list = insOrderentryService.getOrderEntryListByMoibile("", null, null);
        System.out.println(list);*/

//        insToolcabinetService.add2ToolBox("11","11");
//        insToolcabinetService.add2ToolBox("11","22");
        /*System.out.println(insOrderService.addOrderAndOrderEntryByMobile("3423565fb6a046baa011f922b7e5e353","22","2018-06-27 16:28:15","121","203","订单与订单明细","936546e4f4474ac3bdc9a2495a0adcee"));*/


       /*
        String sqlStr = " SELECT id FROM ins_instrument ";
        Sql sql = Sqls.queryRecord(sqlStr);
        dao.execute(sql);
        List<String> recordList = sql.getList(String.class);
        System.out.println(recordList);
        */

       /*List<String> list =new ArrayList<>();
       list.add("047655241c74439081ac43037303ac6c");
       list.add("b6b1e59a32e5464f8483d9120d8ec870");
        List<Ins_instrument> insInstruments = insInstrumentService.query(Cnd.where("id", "IN", list));
        System.out.println(insInstruments);*/
     /*   StringBuilder sb = new StringBuilder(80);
        for (String s : list) {
            sb.append("'"+s+"'").append(",");
        }
        sb = sb.deleteCharAt(sb.length()-1);
       String sqlSqr = "UPDATE test SET name ='BBB' WHERE id IN ("+ sb.toString()+")";
        Sql sql = Sqls.queryRecord(sqlSqr);
        dao.execute(sql);*/
       /* Map<String, Object> map = insOrderService.getOrderInfo("ad6ac599a4544c4fa86254dfb8653d33");
        System.out.println(map);*/
       /* List<Map<String, Object>> orderEntries = insOrderentryService.goToDeliverPage("858c17e96e83445eb1770fee3d39e154");
        System.out.println(orderEntries);*/

    }

    @Inject
    private InsInstrumentService insInstrumentService;
    @Inject
    private InsCommonService insCommonService;
    @Inject
    private InsBaseinsService insBaseinsService;
    @Inject
    private InsToolcabinetService insToolcabinetService;
    @Inject
    private InsOrderService insOrderService;
    @Inject
    private InsOrderentryService insOrderentryService;
    @Inject
    private InsTransmitService insTransmitService;


    @Test
    public void testOrderAndOrderEntry() throws ParseException {
        /*List<Map<String, Object>> allBorrowingOrderEntries = insOrderentryService.getAllBorrowingOrderEntries("858c17e96e83445eb1770fee3d39e154");
        for (Map<String, Object> OrderEntry: allBorrowingOrderEntries) {
            System.out.println(OrderEntry);
        }*/
        /*insOrderentryService.clickDeliver2Code("858c17e96e83445eb1770fee3d39e154","ef92bd6d1a3a429a84493396386de42e,456ed232d3354439b03b0e1787853d43,456ed232d3354439b03b0e1787853d4d");
         */
        /*  insOrderentryService.transmitOrderEntry("858c17e96e83445eb1770fee3d39e154","4804cad4ebdc475c8535513d5549d986","1531885507");*/
        /*List<Map<String, Object>> boxList = insToolcabinetService.getToolBoxList("858c17e96e83445eb1770fee3d39e154", null, null);
        for (Map<String, Object> stringObjectMap : boxList) {
            System.out.println(stringObjectMap);
        }*/
      /*  List<Map<String, Object>> borrowingOrderEntries = insOrderentryService.getAllBorrowingOrderEntries("c7cfe65a83264bbabb54921c0b9bb2ce");
        for (Map<String, Object> borrowingOrderEntry : borrowingOrderEntries) {
            System.out.println(borrowingOrderEntry);
        }*/
     /* String[] boxIds = {"f240a607360742cb96bfc3511cd3f076","4e665e8a3d4f41e588427faa47c470e8","6ff1b2a3732445c4b581545b18167c9e","982e9425af8744ab944f68694152dabf","364b0c715844494898fc41667032af18","67374e5fa5df46a0aa18a0b8fdaaeb77"};
      insOrderService.addOrderAndOrderEntryByMobile(boxIds,"4804cad4ebdc475c8535513d5549d986","2018-07-20 15:24:00","123","123","测试","936546e4f4474ac3bdc9a2495a0adcee");*/

    }

    @Test
    public void testOrder2() throws ParseException {
        /*List<Map<String, Object>> borrowingOrderEntries = insOrderentryService.getAllBorrowingOrderEntries("4804cad4ebdc475c8535513d5549d986");
        for (Map<String, Object> borrowingOrderEntry : borrowingOrderEntries) {
            System.out.println(borrowingOrderEntry);
        }
        String[] orderEntryIds = {"0016ee89e79b452592bfd2e93043ea2b","0b8fbb08b9644c4793e3ad1b7d5959b8","2bbb9a14f5564186b921b27acee9678a","324f6d1139004a54b083395fbce05837","4a1c3726853e45dcaf791b002a3723b8","59bd8581472a4b2d989b7b9598f710c8","7bb8baba83ce45b88b3e83e9039cbd4c","8835ec85360145efab6a19e60645fa7f","c9f95e0589c84fd6bd4026b13ffa65e8","cab9f3d14f8f4ed4a5e7cfd2016ddb07","e1349c6d401e4da8a69465864a3dcf5d","e83277e0ebc6423ab11f36e8037f94e3"};
        */
//        String[] orderEntryIds = {"ebbbb70acaea48af9ed6f54dae0f99c5"};
//        insOrderentryService.clickDeliver2Code("858c17e96e83445eb1770fee3d39e154",orderEntryIds);
//        insOrderentryService.transmitOrderEntry("4804cad4ebdc475c8535513d5549d986","11");
        /*insCommonService.getCommonTools("4804cad4ebdc475c8535513d5549d986","recentBorrow",null,null);*/
/*        String[] boxIds= {"f0904dcad9464d5080f7e17fa2de7e03","268d1d58bc864140ae1127ba35e15aaa","51bd5d4f37bb4ce2a0f89874b05714a6"};
//        insCommonService.add2Favorite("4804cad4ebdc475c8535513d5549d986",ids,"myFavorite");
        insOrderService.addOrderAndOrderEntryByMobile(boxIds,"4804cad4ebdc475c8535513d5549d986","2018-07-23 18:41:21","111","111","","936546e4f4474ac3bdc9a2495a0adcee");*/
      /*  List<Ins_baseins> insBaseins = insBaseinsService.query();
        if(insBaseins.size() > 0){
            for (Ins_baseins insBasein : insBaseins) {
                for (int i = 0; i < 100; i++) {
                    Ins_instrument instrument = new Ins_instrument();
                    instrument.setInsnum(insBasein.getInsnum() + "-" +i);
                    instrument.setPstatus(0);
                    instrument.setBaseInsId(insBasein.getId());
                    insInstrumentService.insert(instrument);
                }
            }
        }*/
        //       insOrderService.getOrderAndEntryList("4804cad4ebdc475c8535513d5549d986", null, null, "");
        //        insOrderService.
    }

    /*@Test
    public void testOrder3() throws ParseException {
        List<Ins_baseins> insBaseins = insBaseinsService.query();
        if(insBaseins.size() > 0){
            for (Ins_baseins insBasein : insBaseins) {
                for (int i = 0; i < 99; i++) {
                    Ins_instrument instrument = new Ins_instrument();
                    instrument.setInsnum(insBasein.getInsnum() + "-" +(i+1));
                    instrument.setPstatus(0);
                    instrument.setBaseInsId(insBasein.getId());
                    insInstrumentService.insert(instrument);
                }
            }
        }

    }*/
    @Test
    public void testOrder3() throws ParseException {
        //base_person basePerson = basePersonService.fetch("05da9b2c37c34995b9a4bc722971d676");
        //System.out.println(Json.toJson(basePerson, JsonFormat.compact()));
        //System.out.println(Json.toJson(basePerson, JsonFormat.tidy()));
        //System.out.println(JSONObject.toJSONString(basePerson, SerializerFeature.WriteNullStringAsEmpty));
        //String insOrderentryServiceOrderEntryListByMoibile = insOrderentryService.getOrderEntryListByMoibile("02ef83b4246d4b64bdaf57c12cb05cc5", null, null);
        //System.out.println(insOrderentryServiceOrderEntryListByMoibile);

//        insOrderentryService.transmitOrderEntry("858c17e96e83445eb1770fee3d39e154","1532598012566");
//        String[] odIds = {"0955bb56278141a2ae4f64245be95e9","04f5413b5bd643858bfdc694a65bba40","094fab83fd5a4e7294c2014af8dd64aa"};
//        insOrderentryService.clickDeliver2Code("",odIds);
       /* List<Map<String, Object>> borrowingOrderEntries = insOrderentryService.getAllBorrowingOrderEntries("4804cad4ebdc475c8535513d5549d986");
        for (Map<String, Object> borrowingOrderEntry : borrowingOrderEntries) {
            System.out.println(borrowingOrderEntry);
        }*/
        /*int count = insInstrumentService.count(Cnd.where("baseInsId", "=", "4107bfef6dfc4ee18ef1f9bd3be5c076").and("pstatus", "=", 0).and("linePstatus", "=", 1));
        System.out.println(count);*/
//        insToolcabinetService.add2ToolBox("4804cad4ebdc475c8535513d5549d986","c15c0caee60b4e3caeb4dbb12646b3b6");
       /*
        String[] boxIds = {"882c4c76d45d411a8bc43840e4e3729f"};
        insOrderService.addOrderAndOrderEntryByMobile(boxIds,"4804cad4ebdc475c8535513d5549d986","2018-7-27 14:07:00","test","test","test","936546e4f4474ac3bdc9a2495a0adcee");
        */
        /*List<Map<String, Object>> orderEntries = insOrderentryService.getAllBorrowingOrderEntries("4804cad4ebdc475c8535513d5549d986");
        for (Map<String, Object> orderEntry : orderEntries) {
            System.out.println(orderEntry);
        }
        String[] odIds = {"0e25dd99682d498e88161e4b63b44127","40a56140570243b1bd411eacbc16daa1","8d814a61dbe0477d9450730e8398e8a4"};
        Map<String, Object> map = insOrderentryService.clickDeliver2Code("4804cad4ebdc475c8535513d5549d986", odIds);
        System.out.println(map);*/
//        insOrderentryService.transmitOrderEntry("f61f152536ee48d8be7c85610a6987d4","1532672507750");

       /* List<Map<String, Object>> baseInsList = insBaseinsService.getBaseInsList("8b71142aaa9643afbde8222cdf16853e", 1, null);
        for (Map<String, Object> stringObjectMap : baseInsList) {
            System.out.println(stringObjectMap);
        }*/
    /*    Ins_baseins insBaseins = insBaseinsService.fetch("4107bfef6dfc4ee18ef1f9bd3be5c071");
        for (int i = 0; i < 29; i++) {
            Ins_baseins baseins = new Ins_baseins();
            baseins.setDict_instype(insBaseins.getDict_instype());
            baseins.setToolPic(insBaseins.getToolPic());
            baseins.setInsname(insBaseins.getInsname() + "TEST"+(i+1));
            baseins.setInsmodel("TEST"+i+"号");
            baseins.setInstype(insBaseins.getInstype());
            baseins.setInsnum("HKJ" +"-"+(i+1));
            baseins.setInsparam("CS"+(i+1));
            baseins.setInsunit(insBaseins.getInsunit());
            insBaseinsService.insert(baseins);
        }*/
    }

    @Test
    public void testOrder4() throws ParseException {
        /*insOrderService.getOrderAndEntryList("4804cad4ebdc475c8535513d5549d986",1,null,"");*/
   /*     List<Map<String, Object>> toolBoxList = insToolcabinetService.getToolBoxList("4804cad4ebdc475c8535513d5549d986", 1, null);
        for (Map<String, Object> stringObjectMap : toolBoxList) {
            System.out.println(stringObjectMap);
        }*/
        /*insCommonService.getCommonTools("4804cad4ebdc475c8535513d5549d986","recentBorrow",null,null);*/
        /*List<Map<String, Object>> orderAndEntryList = insOrderService.getOrderAndEntryList("858c17e96e83445eb1770fee3d39e154  ", 1, null, null);
        for (Map<String, Object> stringObjectMap : orderAndEntryList) {
            System.out.println(stringObjectMap);
        }*/
        /*      List entryList = insOrderService.getOrderAndEntryList("4804cad4ebdc475c8535513d5549d986", 1, null, null);
         *//* for (Map<String, Object> stringObjectMap : entryList) {
            System.out.println(stringObjectMap);
        }*//*
         *//*
       for (Map<String, Object> stringObjectMap : entryList) {
            String jsonString = JSONObject.toJSONString(stringObjectMap, SerializerFeature.WriteNullStringAsEmpty);
            System.out.println(jsonString);
        }*//*
        StringUtil.ergodic(entryList);
        for (Object o : entryList) {
            System.out.println(o);
        }*/
//        String s = JSONObject.toJSONString(entryList,SerializerFeature.WriteNullStringAsEmpty);
       /* Ins_instrument instrument = insInstrumentService.fetch("0006b74e19464ef8ad324d3f017ff369");
        String s = Json.toJson(instrument);
        System.out.println(s);*/
    }


    /* //库存
     @Test
     public void testOrder5() throws ParseException {
         *//*insToolcabinetService.add2ToolBox("c7cfe65a83264bbabb54921c0b9bb2ce","004c0459b8e1408eaa3829cdbc7bb04c");*//*
     *//*insToolcabinetService.add2ToolBox("c7cfe65a83264bbabb54921c0b9bb2ce","4dd24faebfcb49a5a6d45f80bda16b73");*//*
     *//*String[] boxIds = {"9be0fed2950945518a24ef128aeacbd9","aa18a501fd334dc3828b462e1f64d522"};
        insOrderService.addOrderAndOrderEntryByMobile(boxIds,"c7cfe65a83264bbabb54921c0b9bb2ce","2018-08-06 16:33:00","AAA","AAA","AA","936546e4f4474ac3bdc9a2495a0adcee");*//*
        List<Ins_baseins> insBaseins = insBaseinsService.query();
        if(insBaseins.size() > 0){
            for (Ins_baseins insBasein : insBaseins) {
                int count = insInstrumentService.count(Cnd.where("baseInsId", "=", insBasein.getId()).and("linePstatus","=",1));
                insBaseinsService.update(Chain.make("totalCount",count),Cnd.where("id","=",insBasein.getId()));
            }
        }

    }*/
    //库存
    @Test
    public void testOrder5() throws ParseException {
        /*insToolcabinetService.add2ToolBox("c7cfe65a83264bbabb54921c0b9bb2ce","004c0459b8e1408eaa3829cdbc7bb04c");*/
        /*insToolcabinetService.add2ToolBox("c7cfe65a83264bbabb54921c0b9bb2ce","4dd24faebfcb49a5a6d45f80bda16b73");*/
       /*String[] boxIds = {"9be0fed2950945518a24ef128aeacbd9","aa18a501fd334dc3828b462e1f64d522"};
       insOrderService.addOrderAndOrderEntryByMobile(boxIds,"c7cfe65a83264bbabb54921c0b9bb2ce","2018-08-06 16:33:00","AAA","AAA","AA","936546e4f4474ac3bdc9a2495a0adcee");*/

        //工具
        List<Ins_instrument> insInstruments = insInstrumentService.query(Cnd.where("rfid", "IS", null));
        for (int i = 0; i < insInstruments.size(); i++) {
            //           System.out.println(instrument);
            Ins_rfid insRfid = new Ins_rfid();
            //使用中
            insRfid.setPstatus(1);
            insRfid.setAirportid("936546e4f4474ac3bdc9a2495a0adcee");
            insRfid.setNumber("RF" + i);
            insRfid = insRfidService.insert(insRfid);
            insInstruments.get(i).setRfid(insRfid.getId());
            insInstrumentService.update(insInstruments.get(i));
        }
        //生成仓位

        for (int i = 4; i < 13; i++) {
            Ins_space insSpace = new Ins_space();
            insSpace.setCabinetid("806999f777ac4957868456d334e377d8");
            insSpace.setPstatus(0);
            insSpace.setBarcode(String.format("%09d", i));
            insSpace.setSpacenum("A" + String.format("%03d", i));
            insSpace.setSpacename(i + "号仓");
            insSpaceService.insert(insSpace);
        }
        List<Ins_space> insSpaces = insSpaceService.query();
        List<Ins_baseins> insBaseins = insBaseinsService.query(Cnd.where("spaceId", "IS", null));
        int count = 0;
        for (Ins_baseins insBasein : insBaseins) {
            if (count >= insSpaces.size()) {
                count = 0;
            }
            for (int i = 0; i < insSpaces.size(); i++) {
                if (count == i) {
                    insBasein.setSpaceId(insSpaces.get(i).getId());
                    insBaseinsService.update(insBasein);
                }
            }
            count++;
        }
        //库存
        List<Ins_baseins> ibs = insBaseinsService.query();
        if (insBaseins.size() > 0) {
            for (Ins_baseins insBasein : ibs) {
                int count1 = insInstrumentService.count(Cnd.where("baseInsId", "=", insBasein.getId()).and("linePstatus", "=", 1));
                insBaseinsService.update(Chain.make("totalCount", count1), Cnd.where("id", "=", insBasein.getId()));
                insBaseinsService.update(Chain.make("useableCount", count1), Cnd.where("id", "=", insBasein.getId()));
            }
        }


        //绑定基础工具仓位


    }

    //库存
    @Test
    public void testOrder6() throws ParseException {
        /*insToolcabinetService.add2ToolBox("c7cfe65a83264bbabb54921c0b9bb2ce","004c0459b8e1408eaa3829cdbc7bb04c");*/
        /*insToolcabinetService.add2ToolBox("c7cfe65a83264bbabb54921c0b9bb2ce","4dd24faebfcb49a5a6d45f80bda16b73");*/
        /*
        String[] boxIds = {"9be0fed2950945518a24ef128aeacbd9","aa18a501fd334dc3828b462e1f64d522"};
        insOrderService.addOrderAndOrderEntryByMobile(boxIds,"c7cfe65a83264bbabb54921c0b9bb2ce","2018-08-06 16:33:00","AAA","AAA","AA","936546e4f4474ac3bdc9a2495a0adcee");
        */
     /*   List<Map<String, Object>> orderAndEntryList = insOrderService.getOrderAndEntryList("858c17e96e83445eb1770fee3d39e154", 1, null, null);
        for (Map<String, Object> stringObjectMap : orderAndEntryList) {
            System.out.println(stringObjectMap);
        }*/
     /*   List<Map<String, Object>> allBorrowingOrderEntries = insOrderentryService.getAllBorrowingOrderEntries("858c17e96e83445eb1770fee3d39e154", 1, null);
        for (Map<String, Object> allBorrowingOrderEntry : allBorrowingOrderEntries) {
            System.out.println(allBorrowingOrderEntry);
        }*/
//     insOrderentryService.transmitOrderEntry("858c17e96e83445eb1770fee3d39e154","1533717934104");
       /* List<Map<String, Object>> allBorrowingOrderEntries = insOrderentryService.getAllBorrowingOrderEntries("858c17e96e83445eb1770fee3d39e154", 1, null);
        for (Map<String, Object> allBorrowingOrderEntry : allBorrowingOrderEntries) {
            System.out.println(allBorrowingOrderEntry);
        }*/
    }
    /*
    @Test
    public void testOrder7() throws ParseException {
        List<Map<String, Object>> orderAndEntryList = insOrderService.getOrderAndEntryList("858c17e96e83445eb1770fee3d39e154", 1, null, null);
        for (Map<String, Object> stringObjectMap : orderAndEntryList) {
            System.out.println(stringObjectMap);
        }
    }*/

    @Inject
    private InsCabinetService insCabinetService;

    @Test
    public void testOrder8() throws ParseException {
       /* String str =
                " SELECT ib.id as baseInsId FROM ins_instrument ins \n" +
                " JOIN ins_baseins ib ON ins.baseInsId = ib.id";
        Sql sql = Sqls.queryRecord(str);
        dao.execute(sql);
        List<Record> list = sql.getList(Record.class);
        */
       /* int orderNumFe = insOrderService.getOrderNumFe("2018-09-05", "936546e4f4474ac3bdc9a2495a0adcee");
        System.out.println(orderNumFe);*/

//        Map<String, Object> allCabinetInfo = insCabinetService.getAllCabinetInfo();
//        insCabinetService.initCabinet("806999f777ac4957868456d334e377d8");
        /* insCabinetService.update(Chain.make("userId","0fe6b1ce502545d9991fe5a7986a867c").add("useStatus",1),Cnd.where("id","=","806999f777ac4957868456d334e377d8"));;*/
        System.out.println(insOrderService.query());
    }

    /*  private synchronized int getOrderNumSeq(String dateStr, String airportid){
        //今日订单
        int prevCount = this.count(Cnd.where("DATE_FORMAT(operatetime,'%Y-%m-%d')", "=",dateStr).and("airportid", "=", airportid).orderBy("operatetime", "ASC"));
        if (countMap.get("tool") == null) {
            countMap.put("tool", prevCount + 1);
        } else {
            countMap.put("tool", countMap.get("tool") + 1);
        }
        return countMap.get("tool") + 1;
    }*/


    @Test
    public void testOrder9() throws ParseException {
/*//        insCabinetService.initCabinet("806999f777ac4957868456d334e377d8");
//        insCabinetService.doLoginByQRcode("","","");
        *//*Map<String, Object> allCabinetInfo = insCabinetService.getAllCabinetInfo();
        System.out.println(allCabinetInfo);*//*
        //智能标签录入
        String[] rfids = {
                "E28011700000020A2B77F42D",
                "E28011700000020A2B77F4FD",
                "E28011700000020A2B77F4DD",
                "E28011700000020A2B78280D",
                "E28011700000020A2B784A5D",
                "E28011700000020A2B77C8AD",
                "E28011700000020A2B77C8CD",
                "E28011700000020A2B77F4ED",
                "E28011700000020A2B784A4D",
                "E28011700000020A2B77928D",
                "E28011700000020A2B77925D",
                "E28011700000020A2B77C8BD",
                "E28011700000020A2B77927D",
                "E28011700000020A2B77F41D",
                "E28011700000020A2B779CED",
                "E28011700000020A2B77C89D",
                "E28011700000020A2B779CDD",
                "E28011700000020A2B77926D"
        };
        Set<String> stringSet = new HashSet<>();
        for (String number : rfids){
            Ins_rfid rf = new Ins_rfid();
            rf.setNumber(number);
            rf.setAirportid("936546e4f4474ac3bdc9a2495a0adcee");
            rf.setPstatus(0);
            insRfidService.insert(rf);
            stringSet.add(number);
        }
        System.out.println(stringSet.size());*/
    }


    @Inject
    private InsLockService lockService;

    @Test
    public void testOrder11() throws ParseException {
        lockService.updateLockStatus("LOCK_001", 0);
    }


    @Test
    public void testOrder10() throws ParseException {
        //baseInsId
//        开口扳手         c4bb836e4ce745a3803e8f5a4a6916d5         5    LOCK_001
//        千分尺(外径)     4dd24faebfcb49a5a6d45f80bda16b73         4    LOCK_002
//         20mm游标卡尺    07ce091cb7f246caae5f8dc750d338a4         5    LOCK_003
//         一字螺丝刀      c5258577e29f4e8896a90f4b20147f48         4    LOCK_004
        //添加进工具箱
       /* for (int i = 0; i < 3; i++) {
            //3个开口扳手
            insToolcabinetService.add2ToolBox("4804cad4ebdc475c8535513d5549d986", "c4bb836e4ce745a3803e8f5a4a6916d5");
        }*/
    /*    for (int i = 0; i <4; i++) {
            //4个一字螺丝刀
            insToolcabinetService.add2ToolBox("4804cad4ebdc475c8535513d5549d986","c5258577e29f4e8896a90f4b20147f48");
        }*/
        //生成订单
//    String[] boxIds = {"16e1f3d2ab824521818d8ec6a4a50325"};
//        insOrderService.addOrderAndOrderEntryByMobile(boxIds,"4804cad4ebdc475c8535513d5549d986","2018-09-10 15:41:00","123","124","测试","936546e4f4474ac3bdc9a2495a0adcee");
        //模拟
        //需3实际2(开口扳手)
//        String[] rfids = {"E28011700000020A2B77F4FD","E28011700000020A2B77F42D","E28011700000020A2B77C8AD","E28011700000020A2B77C8CD","E28011700000020A2B784A5D"};
//         String[] rfids = {"E28011700000020A2B77F4FD","E28011700000020A2B77F42D"};
//        String[] rfids = {"E28011700000020A2B77C8CD"};
//        String[] rfids = {"E28011700000020A2B77F42D","E28011700000020A2B77C8CD"};
//        insInstrumentService.doCheckOrders(rfids,"LOCK_001","f179b85f364a449881fe55dfda4c2ab4");

    }

    @Inject
    private InsRepairService insRepairService;

    @Test
    public void testOrder12() throws Exception {
//        insRepairService.insertInsRepair("967b317981a5449cac1610185803aa9e","577cb3bb46584be7a2d78a408f14a653","111","111","4804cad4ebdc475c8535513d5549d986");
        /*List<Map<String, Object>> entries = insOrderentryService.getAllBorrowingOrderEntries("4804cad4ebdc475c8535513d5549d986", 1, null);
        for (Map<String, Object> entry : entries) {
            System.out.println(entry);
        }*/
    /*    List<Map<String, Object>> toolBoxList = insToolcabinetService.getToolBoxList("858c17e96e83445eb1770fee3d39e154", 1, null);
        System.out.println(toolBoxList);*/
    }

    @Test
    public void testOrder13() throws Exception {
       /* List<Map<String, Object>> orderEntries = insOrderentryService.getAllBorrowingOrderEntries("858c17e96e83445eb1770fee3d39e154", 1, null);
        for (Map<String, Object> orderEntry : orderEntries) {
            System.out.println(orderEntry);
        }*/
        /* insRepairService.insertInsRepair("abca5564f5cb472d81491d756c6b9eef","225325f055d241aa9c354e9f9a799f26","测试","","4804cad4ebdc475c8535513d5549d986");*/
    }

    @Test
    public void testOrder14() throws Exception {
        List<Map<String, Object>> orderEntries = insOrderentryService.getAllBorrowingOrderEntries("4804cad4ebdc475c8535513d5549d986", 1, null);
        for (Map<String, Object> orderEntry : orderEntries) {
            System.out.println(orderEntry);
        }
       /* String[] odIds = {"a9099eca60f545a2b8eb708ac6f614a3"};
        System.out.println(insOrderentryService.clickDeliver2Code("858c17e96e83445eb1770fee3d39e154",odIds));*/
//        insOrderentryService.transmitOrderEntry("4804cad4ebdc475c8535513d5549d986","1536895629104");
    }

    @Inject
    private InsLockService insLockService;

    @Test
    public void testOrder15() throws Exception {
       /* for (int i = 1; i <= 6; i++) {
            Ins_space space = new Ins_space();
            space.setSpacename(i+"号柜门");
            space.setSpacenum("GD000" + i +"-0" + i+"-0" + i);
            space.setCabinetid("d113a6c40a0c408e83d0cfd1c12d0071");
            insSpaceService.insert(space);
        }
        for (int i = 1; i <= 8; i++) {
            Ins_space space = new Ins_space();
            space.setSpacename(i+"号柜门");
            space.setSpacenum("YD000" + i +"-0" + i+"-0" + i);
            space.setCabinetid("5aee46ab53944014aa957c3ee2720a70");
            insSpaceService.insert(space);
        }*/
        //移动
        List<Ins_space> YDspaces = insSpaceService.query(Cnd.where("spacenum", "LIKE", "YD%"));
        if (YDspaces.size() > 0) {
            for (Ins_space space : YDspaces) {
                String spacenum = space.getSpacenum();
                Ins_lock lock = new Ins_lock();
                lock.setLocknum("YD-LOCK-" + spacenum.substring(spacenum.length() - 2, spacenum.length()));
                lock.setAirportId("936546e4f4474ac3bdc9a2495a0adcee");
                lock.setBindStatus(1);
                lock = insLockService.insert(lock);
                insSpaceService.update(Chain.make("lockId", lock.getId()), Cnd.where("id", "=", space.getId()));
            }
        }
        //固定
        List<Ins_space> GDspaces = insSpaceService.query(Cnd.where("spacenum", "LIKE", "GD%"));
        if (GDspaces.size() > 0) {
            for (Ins_space space : GDspaces) {
                String spacenum = space.getSpacenum();
                Ins_lock lock = new Ins_lock();
                lock.setLocknum("GD-LOCK-" + spacenum.substring(spacenum.length() - 2, spacenum.length()));
                lock.setAirportId("936546e4f4474ac3bdc9a2495a0adcee");
                lock.setBindStatus(1);
                lock = insLockService.insert(lock);
                insSpaceService.update(Chain.make("lockId", lock.getId()), Cnd.where("id", "=", space.getId()));
            }
        }

    }

    @Test
    public void testOrder16() throws Exception {
       /* for (int i = 1; i <= 6; i++) {
            Ins_space space = new Ins_space();
            space.setSpacename(i+"号柜门");
            space.setSpacenum("GD000" + i +"-0" + i+"-0" + i);
            space.setCabinetid("d113a6c40a0c408e83d0cfd1c12d0071");
            insSpaceService.insert(space);
        }
        for (int i = 1; i <= 8; i++) {
            Ins_space space = new Ins_space();
            space.setSpacename(i+"号柜门");
            space.setSpacenum("YD000" + i +"-0" + i+"-0" + i);
            space.setCabinetid("5aee46ab53944014aa957c3ee2720a70");
            insSpaceService.insert(space);
        }*/
        //移动
        /*List<Ins_space> YDspaces = insSpaceService.query(Cnd.where("spacenum","LIKE","YD%"));
        if(YDspaces.size() > 0){
            for (Ins_space space : YDspaces) {
                String spacenum = space.getSpacenum();
                Ins_lock lock = new Ins_lock();
                lock.setLocknum("YD-LOCK-"+spacenum.substring(spacenum.length()-2,spacenum.length()));
                lock.setAirportId("936546e4f4474ac3bdc9a2495a0adcee");
                lock.setBindStatus(1);
                lock = insLockService.insert(lock);
                insSpaceService.update(Chain.make("lockId",lock.getId()),Cnd.where("id","=",space.getId()));
            }
        }
        //固定
        List<Ins_space> GDspaces = insSpaceService.query(Cnd.where("spacenum","LIKE","GD%"));
        if(GDspaces.size() > 0){
            for (Ins_space space : GDspaces) {
                String spacenum = space.getSpacenum();
                Ins_lock lock = new Ins_lock();
                lock.setLocknum("GD-LOCK-"+spacenum.substring(spacenum.length()-2,spacenum.length()));
                lock.setAirportId("936546e4f4474ac3bdc9a2495a0adcee");
                lock.setBindStatus(1);
                lock = insLockService.insert(lock);
                insSpaceService.update(Chain.make("lockId",lock.getId()),Cnd.where("id","=",space.getId()));
            }
        }*/
    }

    @Test
    public void testOrder17() throws Exception {
        //baseInsId
//        开口扳手         c4bb836e4ce745a3803e8f5a4a6916d5         5    LOCK_001
//        千分尺(外径)     4dd24faebfcb49a5a6d45f80bda16b73         4    LOCK_002
//         20mm游标卡尺    07ce091cb7f246caae5f8dc750d338a4         5    LOCK_003
//         一字螺丝刀      c5258577e29f4e8896a90f4b20147f48         4    LOCK_004
        //添加进工具箱
        /*for (int i = 0; i < 3; i++) {
            //3个开口扳手
            insToolcabinetService.add2ToolBox("4804cad4ebdc475c8535513d5549d986", "c4bb836e4ce745a3803e8f5a4a6916d5");
        }*/
    /*    for (int i = 0; i <4; i++) {
            //4个一字螺丝刀
            insToolcabinetService.add2ToolBox("4804cad4ebdc475c8535513d5549d986","c5258577e29f4e8896a90f4b20147f48");
        }*/
        //生成订单
        /*String[] boxIds = {"a4f75bddf94b491c9fdb8cf0bfeca8af"};
        insOrderService.addOrderAndOrderEntryByMobile(boxIds,"4804cad4ebdc475c8535513d5549d986","2018-09-10 15:41:00","123","124","测试","936546e4f4474ac3bdc9a2495a0adcee");*/
       /* List<Map<String, Object>> orders = insOrderService.getAllOrderAndEntry("4804cad4ebdc475c8535513d5549d986", 1, null, null, "d113a6c40a0c408e83d0cfd1c12d0071");
        for (Map<String, Object> order : orders) {
            System.out.println(order);
        }*/
        //模拟
        //需3实际2(开口扳手)
      /*  String[] rfids = {"E28011700000020A2B77F4FD","E28011700000020A2B77F42D","E28011700000020A2B77C8AD","E28011700000020A2B77C8CD","E28011700000020A2B784A5D"};
//         String[] rfids = {"E28011700000020A2B77F4FD","E28011700000020A2B77F42D"};
//        String[] rfids = {"E28011700000020A2B77C8CD"};
//        String[] rfids = {"E28011700000020A2B77F42D","E28011700000020A2B77C8CD"};
        insInstrumentService.doCheckOrders(rfids,"GD0001-01-01","8918973a1cad44aebaa9035e97f446cb");*/
    }

    @Test
    public void testOrder18() throws Exception {
    /*
    1. 工具名称： 双向开口扳手 (7/9),    工具型号：   7/9
    2. 工具名称： 双向开口扳手 (8/10),   工具型号：  8/10
    3. 工具名称： 双向开口扳手(10/12)，  工具型号： 10/12
    4. 工具名称： 双向开口扳手(12/14)，  工具型号： 12/14
    5. 工具名称： 双向开口扳手(14/17)，  工具型号： 14/17
    6. 工具名称： 双向开口扳手(17/19)，  工具型号： 17/19
    7. 工具名称： 双向开口扳手(9/11)，   工具型号：  9/11
    8. 工具名称：双向开口扳手(19/22)，  工具型号： 19/22
    9. 工具名称：双向开口扳手(22/24)，  工具型号： 22/24
    10.工具名称：双向开口扳手(24/27)， 工具型号：24/27
    11.工具名称：双向开口扳手(13/16)， 工具型号：13/16
    12.工具名称：套筒开口扳手(8/8)，   工具型号：  8/8
    */
        Ins_baseins insBaseins = new Ins_baseins();
        insBaseins.setInsnum("GJS-001" + 2);
        insBaseins.setInsmodel("8/8");
        insBaseins.setInsunit("080f21444c59423390c6c83ff6ef5cd1");
        insBaseins.setInsname("双向开口扳手");
        insBaseins.setInstype("dd02f51ef8224b1cba6dbe99e6a01dae");
        insBaseins = insBaseinsService.insert(insBaseins);
        insBaseinsService.update(Chain.make("insname", "双向开口扳手(" + insBaseins.getInsmodel() + ")"), Cnd.where("id", "=", insBaseins.getId()));
    }

    @Test
    public void testOrder19() throws Exception {
        /*
        List<Ins_baseins> baseinsList = insBaseinsService.query();
        if(baseinsList.size() > 0){
            for (int i = 0; i <baseinsList.size() ; i++) {
                Ins_baseins baseins = baseinsList.get(i);
                Ins_instrument instrument = new Ins_instrument();
                instrument.setBaseInsId(baseins.getId());
                instrument.setPstatus(0);
                instrument.setInsnum("TOOL-000-"+String.format("%04d",i+1));
                instrument.setLinePstatus(1);
                insInstrumentService.insert(instrument);
            }
        }
        */
    }

    @Test
    public void testOrder20() throws Exception {
        /*
        insToolcabinetService.add2ToolBox("4804cad4ebdc475c8535513d5549d986","11d2bc9ddcb642aaa170527f5274fd89");
        */
        /*
        Map<String, Object> stringObjectMap = insCabinetService.getAllCabinetInfo();
        System.out.println(stringObjectMap);
        List<Map<String, Object>> mapList = insOrderService.getAllOrderAndEntry("4804cad4ebdc475c8535513d5549d986", 1, null,"","d113a6c40a0c408e83d0cfd1c12d0071");
        for (Map<String, Object> map : mapList) {
            System.out.println(map);
        }
        */
        /*
        String[] nums = {
               "000000000000000000000122",
               "000000000000000000000579",
               "000000000000000000000120",
               "000000000000000000000575",
               "000000000000000000000580",
               "000000000000000000000121",
               "000000000000000000000123"
        };
        for (String num: nums) {
            Ins_rfid rf = new Ins_rfid();
            rf.setPstatus(0);
            rf.setAirportid("936546e4f4474ac3bdc9a2495a0adcee");
            rf.setNumber(num);
            insRfidService.insert(rf);
        }
        */


    }

    @Inject
    private SysVersionService sysVersionService;

    @Test
    public void testOrder21() throws Exception {
        /*List<Map<String, Object>> entryList = insOrderService.getAllOrderAndEntry("4804cad4ebdc475c8535513d5549d986", 1, null, "","d113a6c40a0c408e83d0cfd1c12d0071");
        for (Map<String, Object> map : entryList) {
            System.out.println(map);
        }*/
//        String orderNum = insOrderService.getOrderNum("2018-09-19 19:15:00", "936546e4f4474ac3bdc9a2495a0adcee");
//        System.out.println(orderNum);
        /*
        Cnd cnd = Cnd.NEW();
        cnd.and("name", "=", "ios");
        List<Sys_version> svList = sysVersionService.query(cnd);
        HashMap map = new HashMap();
        if (svList.size() > 0) {
            Sys_version sysVersion = svList.get(0);
            Map<String,Object> versionMap = new HashMap<>();
            versionMap.put("id",sysVersion.getId());
            versionMap.put("name",sysVersion.getName());
            versionMap.put("version",sysVersion.getVersion());
            versionMap.put("wgt",sysVersion.getWgt());
//            return Result.success("system.success", map);
            System.out.println(versionMap);
        }
        */
        Map<String, Object> cabinetInfo = insCabinetService.getAllCabinetInfo();
        System.out.println(cabinetInfo);
    }

    @Test
    public void testOrder23() throws Exception {
        //baseInsId
//        开口扳手         c4bb836e4ce745a3803e8f5a4a6916d5         5    LOCK_001  GD
//        千分尺(外径)     4dd24faebfcb49a5a6d45f80bda16b73         4    LOCK_002  GD
//         20mm游标卡尺    07ce091cb7f246caae5f8dc750d338a4         5    LOCK_003  GD
//         一字螺丝刀      c5258577e29f4e8896a90f4b20147f48         4    LOCK_004  YD
        //添加进工具箱
       /* for (int i = 0; i < 3; i++) {
            //3个开口扳手
            insToolcabinetService.add2ToolBox("4804cad4ebdc475c8535513d5549d986", "c4bb836e4ce745a3803e8f5a4a6916d5");
        }*/
      /*  for (int i = 0; i <3; i++) {
            //3个一字螺丝刀
            insToolcabinetService.add2ToolBox("4804cad4ebdc475c8535513d5549d986","c5258577e29f4e8896a90f4b20147f48");
        }*/
      /*  for (int i = 0; i <2; i++) {
            //2个20mm游标卡尺
            insToolcabinetService.add2ToolBox("4804cad4ebdc475c8535513d5549d986","07ce091cb7f246caae5f8dc750d338a4");
        }*/
        //生成订单
        /*String[] boxIds = {"62006b9a8b9f46f9a450ee70d8d1ad73"};
        insOrderService.addOrderAndOrderEntryByMobile(boxIds,"4804cad4ebdc475c8535513d5549d986","2018-09-10 15:41:00","123","124","测试","936546e4f4474ac3bdc9a2495a0adcee");*/
        /*List<Map<String, Object>> orders = insOrderService.getAllOrderAndEntry("4804cad4ebdc475c8535513d5549d986", 1, null, null, "d113a6c40a0c408e83d0cfd1c12d0071");
        for (Map<String, Object> order : orders) {
            System.out.println(order);
        }*/
        //模拟
        //需3实际2(开口扳手)
//       String[] rfids = {"E28011700000020A2B77F4FD","E28011700000020A2B77F42D","E28011700000020A2B77C8AD","E28011700000020A2B77C8CD","E28011700000020A2B784A5D"};
//         String[] rfids = {"E28011700000020A2B77F4FD","E28011700000020A2B77F42D"};
//        String[] rfids = {"E28011700000020A2B77C8CD"};
//        String[] rfids = {"E28011700000020A2B77F42D"};
//        insInstrumentService.doCheckOrders(rfids,"GD0001-01-01","05a3c0b25d7647e0bb28ab867e279635","GDcabinet");
//        需3实际2(一字螺丝刀)
        String[] rfids = {"E28011700000020A2B77C8BD", "E28011700000020A2B77C89D", "E28011700000020A2B77926D", "E28011700000020A2B77F41D"};
        //(20mm游标卡尺)
//        String[] rfids = {"E28011700000020A2B77925D","E28011700000020A2B77928D","E28011700000020A2B77927D","E28011700000020A2B779CED","E28011700000020A2B779CDD"};

//        String[] rfids = {"E28011700000020A2B77C89D","E28011700000020A2B77925D","E28011700000020A2B77928D","E28011700000020A2B77927D"};
//        String[] rfids = {"E28011700000020A2B77F4FD"};
//        String[] rfids = {"E28011700000020A2B77C8BD","E28011700000020A2B77C89D","E28011700000020A2B77926D","E28011700000020A2B77F41D","E28011700000020A2B77925D","E28011700000020A2B77928D","E28011700000020A2B77927D","E28011700000020A2B779CED","E28011700000020A2B779CDD"};
        insInstrumentService.doCheckOrders(rfids, "YD0001-01-01", "8c8d45cc4fb64d5891da50df48999c35", "YDcabinet");
    }

    /*@Test
    public void testOrder24() throws Exception {
        System.out.println(insInstrumentService.getList());
    }*/
    @Test
    public void testOrder25() throws Exception {
//        System.out.println(insInstrumentService.count());
//        Sql sql = Sqls.queryRecord("SELECT MAX(insnum) insnum FROM ins_baseins WHERE insnum LIKE 'GJX%'");
    /*    Sql sql = Sqls.fetchString("SELECT MAX(insnum) insnum FROM ins_baseins WHERE insnum LIKE 'GJX%'");
        insBaseinsService.dao().execute(sql);
        String str = sql.getObject(String.class);
        System.out.println(str);*/
    /*String[] str = {"E2000019101000472340167F","E20000191010014227606E43", "E20000191010011026704BCB", "E20000191010018426709A24", "E20000191010015723008746", "E20000191010015627007FD6", "E200001910100118241054BB", "E200001910100171231098C5", "E2000017990402310830C47E", "E20000191010013124906D41",       "E20000191010013526906D93", "E20000191010007124902B33", "E2000019101001482450777E", "E200001910100141238075E6", "E2000019101001652650908A", "E20000191010016224408901", "E20000191010009124004269", "E2000019610202551680E51F", "E20000191010006925202B3A", "E2000019101001852440A19C", "E2000019100F016913508E90", "E2000019101002082410B3F0", "E2000019101002162270BC4C", "E20000191010008525503AA6", "E2000017990401330890BFD2", "E20000191010010226004387"};
        insInstrumentService.doCheckOrders(str,"YD0001-01-01","20672066f54f446594e463de310b94d1","YDcabinet");
       */
        /*insOrderService.dao().run(new ConnCallback() {
            public void invoke(Connection conn) throws Exception {
                PreparedStatement psUpdate = null;
                try {
                    Map<String,String> map1 = new HashMap();
                    Map<String,String> map2 = new HashMap();
                    Object[] params = {map1,map2};
                    String sql = " UPDATE ins_order SET ordernum = ?,flightnum = ?  WHERE id = ?";
                    psUpdate = conn.prepareStatement(sql);
                    map1.put("ordernum","AAA");
                    map1.put("flightnum","AAA");
                    map1.put("id","20672066f54f446594e463de310b94d1");

                    map2.put("ordernum","BBB");
                    map2.put("flightnum","BBB");
                    map2.put("id","287a6d876ab647afbece30e5f57786cd");
                    ;//更新
                    for (int i=0;i < params.length;i++){
                        Map<String, String> paramMap = (Map<String, String>) params[i];
                        psUpdate.setString(1,paramMap.get("ordernum"));
                        psUpdate.setString(2,paramMap.get("flightnum"));
                        psUpdate.setString(3,paramMap.get("id"));
                        psUpdate.addBatch();
                    }
                    psUpdate.executeBatch();
                } catch (Exception e) {

                } finally {
                    if(psUpdate!=null)
                        psUpdate.close();
                }
            }
        });*/

    }

    @Test
    public void testOrder26() throws Exception {
      /*
         List<Map<String, Object>> orderAndEntry = insOrderService.getAllOrderAndEntry("4804cad4ebdc475c8535513d5549d986", 1, null, "0", "5aee46ab53944014aa957c3ee2720a70");
        for (Map<String, Object> map : orderAndEntry) {
            System.out.println(map);
        }
      */

//        List<Map<String, Object>> orderDetail = insOrderService.getOrderDetail("160f05bba6f74ae6b58c9f2c8cb7f1bb");
      /*  String sqlStr = " SELECT * FROM ins_order ";
        Sql sql = Sqls.queryRecord(sqlStr);
        dao.execute(sql);
        List<Record> recordList = sql.getList(Record.class);
        for (Record record : recordList) {
            System.out.println(record);
        }*/

/*
        List<Map<String, Object>> mapList = insOrderService.getAllOrderAndEntry("4804cad4ebdc475c8535513d5549d986", 1, null, "0", "5aee46ab53944014aa957c3ee2720a70");
        for (Map<String, Object> map : mapList) {
            System.out.println(map);
        }*/
        String[] rfids = {"E2000019101000472340167F", "E2000019610202171650C194", "E2000019610202561590E1B4", "E20000191010014227606E43", "E2000019101002002460AB8C", "E200001961020030146007C7", "E2000019100F004612701297", "E2000019610201971780B18E", "E20000191010016427408886", "E2000019101001852240A14C", "E200001910100171231098C5", "E2000019610202081530B550", "E2000019101001482450777E", "E20000196102017816909BA5", "E2000019100F01951480B109", "E2000019100F02321330CD50", "E2000019100F01481250795E", "E20000196102013816307009", "E2000019100F011312005150", "E20000191010006925202B3A", "E2000019610202301720CCB3", "E2000019100F016913508E90", "E2000019101001862650A2C1", "E2000019610200791750319B", "E2000019100F02561290E228", "E200001910100155256087A1", "E2000017990401330890BFD2", "E20000191010010226004387", "E2000019610202811660F410", "E2000019100F0054134018AF", "E20000191010007527003311", "E20000191010011026704BCB", "E20000191010018426709A24", "E2000019100F01971350B0D2", "E20000191010015723008746", "E2000019100F0049128014CC", "E2000019100F018514009FFC", "E20000191010008526503AC2", "E2000019101002422510D9B9", "E20000196102015418008141", "E20000191010015627007FD6", "E2000019101001002750434E", "E200001910100118241054BB", "E2000017990402310830C47E", "E20000196102003316800A3C", "E2000019100F02741330F235", "E20000191010013124906D41", "E20000191010013526906D93", "E20000191010007124902B33", "E2000017990400980830C368", "E2000019100F02771380F39E", "E2000019100F0023118004FF", "E2000019100F01911220A83B", "E2000019100F012113805A30", "E200001910100141238075E6", "E2000017990401400830C3C5", "E2000019100F01161430563E", "E20000191010016224408901", "E2000019101001652650908A", "E2000019100F01341510677B", "E20000191010009124004269", "E2000019610202551680E51F", "E2000019101001852440A19C", "E20000196102018215609BDF", "E2000019101002082410B3F0", "E20000196102012117805AD0", "E2000019101002162270BC4C",
                "E2000019101001112420532B", "E2000019101002252360CA80", "E200001961020118168055D7", "E20000191010008525503AA6"};
        insInstrumentService.doCheckOrders(rfids, "YD0003-03-03", "e156e7a6b32840319ba97b13957b334a", "YDcabinet");
    }

    @Test
    public void testGoods() throws ParseException {
        //添加进工具箱
        //一字螺丝刀(3 * 75)
       /* for (int i = 0; i < 2; i++) {
            //3个开口扳手
            insToolcabinetService.add2ToolBox("4804cad4ebdc475c8535513d5549d986", "0e78846492664ccfbee82195bc6b2311");
        }*/
        String[] boxIds = {"bf4f33e4d40f4dfb873a1bec37cee726"};
        insOrderService.addOrderAndOrderEntryByMobile(boxIds, "4804cad4ebdc475c8535513d5549d986", "2018-10-24 15:07:00", "123", "124", "测试", "936546e4f4474ac3bdc9a2495a0adcee");
        //       d0f4f1d216e8446d8dbf601990f012d4
       /*
        List<Map<String, Object>> baseInsList = insBaseinsService.getBaseInsList("d0f4f1d216e8446d8dbf601990f012d4", 1, null);
        for(Map<String, Object> stringObjectMap : baseInsList){
            System.out.println(stringObjectMap);
        }
        */

    }

    @Test
    public void testGoods1() throws ParseException {
        /*
        for ( int i = 0; i < 2; i++ ){
            //3个开口扳手
            insToolcabinetService.add2ToolBox("4804cad4ebdc475c8535513d5549d986", "0e78846492664ccfbee82195bc6b2311");
        }
        */
        /*
        String[] boxIds = {""};
        insOrderService.addOrderAndOrderEntryByMobile(boxIds, "4804cad4ebdc475c8535513d5549d986", "2018-11-28 14:05:00", "123", "124", "测试", "936546e4f4474ac3bdc9a2495a0adcee");
        */
        //insOrderService.getOrderAndEntryList("4804cad4ebdc475c8535513d5549d986",1,null,"0");

    }
}