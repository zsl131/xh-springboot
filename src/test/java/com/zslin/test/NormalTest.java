package com.zslin.test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.tools.json.JSONUtil;
import com.zslin.business.dao.*;
import com.zslin.business.mini.dto.AgentCommissionDto;
import com.zslin.business.mini.dto.MsgDto;
import com.zslin.business.mini.dto.NewCustomDto;
import com.zslin.business.mini.tools.*;
import com.zslin.business.model.*;
import com.zslin.business.settlement.dao.IRewardDao;
import com.zslin.business.settlement.dto.AgentRewardDto;
import com.zslin.business.settlement.dto.RankingDto;
import com.zslin.business.settlement.tools.RankingTools;
import com.zslin.business.settlement.tools.RewardTools;
import com.zslin.business.tools.BindCodeTools;
import com.zslin.business.wx.dto.TemplateMessageDto;
import com.zslin.business.wx.tools.TemplateMessageAnnotationTools;
import com.zslin.business.wx.tools.WxMediaTools;
import com.zslin.business.wx.tools.WxMenuTools;
import com.zslin.core.annotations.NeedAuth;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.express.ExpressResultDto;
import com.zslin.core.repository.SimplePageBuilder;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.service.TestService;
import com.zslin.core.tasker.BeanCheckTools;
import com.zslin.core.tools.*;
import org.bouncycastle.util.encoders.Base64;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ClassUtils;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(value = "zsl")
public class NormalTest implements ApplicationContextAware {

    private RestTemplate template = new RestTemplate();

    @Autowired
    private AccessTokenTools accessTokenTools;

    @Autowired
    private MiniCommonTools miniCommonTools;

    @Autowired
    private BeanCheckTools beanCheckTools;

    @Autowired
    private IProductDao productDao;

    @Autowired
    private BuildAdminMenuTools buildAdminMenuTools;

    @Autowired
    private SortTools sortTools;

    @Autowired
    private TestService testService;

    @Autowired
    private BeanFactory factory;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Autowired
    private IProductFavoriteRecordDao productFavoriteRecordDao;

    @Autowired
    private IProductSpecsDao productSpecsDao;

    @Autowired
    private ICouponDao couponDao;

    @Autowired
    private PushMessageTools pushMessageTools;

    @Autowired
    private IAgentLevelDao agentLevelDao;

    @Autowired
    private QrTools qrTools;

    @Autowired
    private ConfigTools configTools;

    @Autowired
    private ExpressTools expressTools;

    @Autowired
    private TemplateMessageAnnotationTools templateMessageAnnotationTools;

    @Autowired
    private ICustomCommissionRecordDao customCommissionRecordDao;

    @Autowired
    private WxMenuTools wxMenuTools;

    @Autowired
    private WxMediaTools wxMediaTools;

    @Autowired
    private RankingTools rankingTools;

    @Autowired
    private RewardTools rewardTools;

    @Autowired
    private IRewardDao rewardDao;

    @Autowired
    private IAgentDao agentDao;

    @Autowired
    private ICashOutDao cashOutDao;

    @Autowired
    private IRefundRecordDao refundRecordDao;

    @Autowired
    private BindCodeTools bindCodeTools;

    @Test
    public void test51() {
        couponDao.plusAmount(1, 1);
    }

    @Test
    public void test50() {
        String str = bindCodeTools.bindWxMini("8051", "oy8_QwNcCgN4U8ulmskM6XeW3YWU");
        System.out.println("=========="+str);
    }

   @Test
    public void test49() {
        RefundRecord rr = refundRecordDao.queryRefundRecord("202007281524041387");
        System.out.println("--------->"+rr);
    }

    @Test
    public void test48() {
        Double money = cashOutDao.findMoney(0l, 1592925412601l);
        System.out.println("---->money:: "+money);
    }

    @Test
    public void test47() {
        Agent a = agentDao.findOkByOpenid("123");
        System.out.println(a);
    }

    private String createTextMsgCon(String toUser, String content) {
        StringBuffer sb = new StringBuffer();
        sb.append("{\"touser\":\"").append(toUser).append("\",")
                .append("\"msgtype\":\"text\",\"text\":")
                .append("{\"content\":\"").append(content).append("\"}}");
        System.out.println("----PushMessageTools.createTextMsgCon---"+sb.toString());
        return sb.toString();
    }

    @Test
    public void test46() {
//        String con = createTextMsgCon("oIguM5UvbfNglnWYj7W7_aBkS-3w", "456");
//        System.out.println(con);
        pushMessageTools.sendTextMsg("oIguM5UvbfNglnWYj7W7_aBkS-3w", "456");
    }

    @Test
    public void test45() {
        int i = 0;
        while(i++<10000) {
            System.out.print(RandomTools.genCodeNew()+"      ");
            if(i%50==0) {

                System.out.println("-----------------");
            }
        }
    }

    @Test
    public void test44() throws Exception {
        String openid = "oIguM5UvbfNglnWYj7W7_aBkS-2a";
        String id = "123";
        String md5 = SecurityUtil.md5(openid, id);
        System.out.println(md5);
    }

    @Test
    public void test43() {
        AgentRewardDto dto = rewardDao.queryDto("oIguM5UvbfNglnWYj7W7_aBkS-2a");
        System.out.println(dto);
    }

    @Test
    public void test42() {
        rewardTools.buildReward("202004");
    }

    @Test
    public void test41() {
        String pattern = "yyyyMM";
        System.out.println(NormalTools.getMonth(pattern));
        System.out.println(NormalTools.getMonth(pattern, 0));
        System.out.println(NormalTools.getMonth(pattern, -1));
        System.out.println(NormalTools.getMonth(pattern, 5));
    }

    @Test
    public void test40() {
        rankingTools.buildRanking("202004");
    }

    @Test
    public void test39() {
        Sort sort = SimpleSortBuilder.generateSort("totalMoney_d", "totalCount_d");
        Pageable page = SimplePageBuilder.generate(0, 2, sort);
        Page<RankingDto> list = customCommissionRecordDao.queryRanking("202004", page);
        System.out.println("page::"+list.getTotalPages()+", total: "+list.getTotalElements());
        for(RankingDto dto : list.getContent()) {
            System.out.println(dto);
        }
    }

    @Test
    public void test38() {
        AgentLevel al = agentLevelDao.queryMinLevel();
        System.out.println(al);
    }

    @Test
    public void test37() {
        String str = wxMediaTools.queryMedias(0, 20);
        System.out.println(str);
    }

    @Test
    public void test36() {
        String str = wxMenuTools.createMenuJson();
        System.out.println(str);
    }

    @Test
    public void test35() {
        String url = "https://msq-file.zslin.com/Product_Exception_3c0f192d-e825-4941-8a0b-ed4511a6686c.jpg";
        String key = url.substring(url.lastIndexOf("/")+1);
        System.out.println(key);
    }

    @Test
    public void test34() {
        for(int j=5;j<15;j++) {
            for (int i = 0; i < 150; i++) {
                CustomCommissionRecord ccr = new CustomCommissionRecord();
                ccr.setAgentId(j);
                ccr.setStatus(getStatus());
                ccr.setMoney(getMoney());
                ccr.setCustomNickname("顾客【" + j+"_"+i + "】");
                ccr.setCreateTime(NormalTools.curDatetime());
                ccr.setCreateMonth(NormalTools.getNow("yyyyMM"));
                ccr.setCreateLong(System.currentTimeMillis());
                ccr.setCreateDay(NormalTools.curDate());
                ccr.setProTitle("其中一项产品" + i);
                ccr.setSpecsName("规格" + i);
                customCommissionRecordDao.save(ccr);
            }
        }
    }

    private Float getMoney() {
        return Float.parseFloat((int)((Math.random()*10)+5)+"");
    }

    private String getStatus() {
        String [] array = new String[]{"-1", "0", "1", "2", "3", "4"};
        int index =Integer.parseInt(((int)(Math.random()*array.length))+"");
        return array[index];
    }

    @Test
    public void test33() {
        AgentCommissionDto dto = customCommissionRecordDao.queryCountDto("9", 1);
        System.out.println(dto);
    }

    @Test
    public void test32() {
        List<TemplateMessageDto> noConfig = templateMessageAnnotationTools.findNoConfigTemplateMessage();
        System.out.println("--------------------");
        System.out.println(noConfig);
    }

    @Test
    public void test31() {
        String str = "张三,广东省广州市海珠区新港中路397号,020-81167888";
        String res = str.substring(str.length()-4);
        System.out.println(res);
    }

    @Test
    public void test30() {
        String str = "{\"status\":\"0\",\"msg\":\"ok\",\"result\":{\"number\":\"780098068058\",\"type\":\"zto\",\"list\":[{\"time\":\"2018-03-09 11:59:26\",\"status\":\"【石家庄市】 快件已在 【长安三部】 签收,签收人: 本人, 感谢使用中通快递,期待再次为您服务!\"},{\"time\":\"2018-03-09 09:03:10\",\"status\":\"【石家庄市】快件已到达【长安三部】（0311-85344265）,业务员 容晓光（13081105270）正在第1次派件\"},{\"time\":\"2018-03-08 23:43:44\",\"status\":\"【石家庄市】 快件离开 【石家庄】 发往 【长安三部】\"},{\"time\":\"2018-03-08 21:00:44\",\"status\":\"【石家庄市】 快件到达 【石家庄】\"},{\"time\":\"2018-03-07 01:38:45\",\"status\":\"【广州市】 快件离开 【广州中心】 发往 【石家庄】\"},{\"time\":\"2018-03-07 01:36:53\",\"status\":\"【广州市】 快件到达 【广州中心】\"},{\"time\":\"2018-03-07 00:40:57\",\"status\":\"【广州市】 快件离开 【广州花都】 发往 【石家庄中转】\"},{\"time\":\"2018-03-07 00:01:55\",\"status\":\"【广州市】 【广州花都】（020-37738523） 的 马溪 （18998345739） 已揽收\"}],\"deliverystatus\":\"3\",\"issign\":\"1\",\"expName\":\"中通快递\",\"expSite\":\"www.zto.com\",\"expPhone\":\"95311\",\"courier\":\"容晓光\",\"courierPhone\":\"13081105270\",\"updateTime\":\"2019-08-27 13:56:19\",\"takeTime\":\"2天20小时14分\",\"logo\":\"http://img3.fegine.com/express/zto.jpg\"}}";
        ExpressResultDto dto = expressTools.query2DtoByStr(str);
        System.out.println(dto);
    }

    @Test
    public void test29() {
        //{"status":"0","msg":"ok","result":{"number":"780098068058","type":"zto","list":[{"time":"2018-03-09 11:59:26","status":"【石家庄市】 快件已在 【长安三部】 签收,签收人: 本人, 感谢使用中通快递,期待再次为您服务!"},{"time":"2018-03-09 09:03:10","status":"【石家庄市】快件已到达【长安三部】（0311-85344265）,业务员 容晓光（13081105270）正在第1次派件"},{"time":"2018-03-08 23:43:44","status":"【石家庄市】 快件离开 【石家庄】 发往 【长安三部】"},{"time":"2018-03-08 21:00:44","status":"【石家庄市】 快件到达 【石家庄】"},{"time":"2018-03-07 01:38:45","status":"【广州市】 快件离开 【广州中心】 发往 【石家庄】"},{"time":"2018-03-07 01:36:53","status":"【广州市】 快件到达 【广州中心】"},{"time":"2018-03-07 00:40:57","status":"【广州市】 快件离开 【广州花都】 发往 【石家庄中转】"},{"time":"2018-03-07 00:01:55","status":"【广州市】 【广州花都】（020-37738523） 的 马溪 （18998345739） 已揽收"}],"deliverystatus":"3","issign":"1","expName":"中通快递","expSite":"www.zto.com","expPhone":"95311","courier":"容晓光","courierPhone":"13081105270","updateTime":"2019-08-27 13:56:19","takeTime":"2天20小时14分","logo":"http://img3.fegine.com/express/zto.jpg"}}
        String no = "780098068058";
        ExpressResultDto dto = expressTools.query2Dto(no);
        System.out.println("------------------");
        System.out.println(dto);
    }

    @Test
    public void test28() {
        String sign = PayUtils.buildPaySign("111111", "222222", "333333", "444444", "555555");
        //51592969BD633CA352457A1DECEA4AC6
        System.out.println(sign);
    }

    @Test
    public void test27() {
        String certPath = configTools.getFilePath("cert") + "apiclient_cert.p12";
        File file = new File(certPath);
        System.out.println(file.exists());
    }

    @Test
    public void test26() {
        String str = PayUtils.buildSignXml("111111", "222222", "这里是标题", "333333");
        System.out.println(str);
        //appid=111111&body=这里是标题&device_info=WEB&mch_id=222222&nonce_str=MXY93QYOCU4CWYJP8IBPDO8H542K38C2
        //A72BB4CDC12E3AF10A7A7CC546B19B7B
    }

    @Test
    public void test25() {
        //制作800*1137的照片
        //qrTools.getQrB("", "1");
    }

    @Test
    public void test24() {
        AgentLevel al = agentLevelDao.findByAgentId(68);
        System.out.println(al);
    }

    @Test
    public void test23() {
        pushMessageTools.push("oIguM5UvbfNglnWYj7W7_aBkS-3w", "a7uRVse33w7zjMik362eMXJCp8cu45vjpaVNQesish8",
                "",
                new MsgDto("thing1", "代理审核结果"),
                new MsgDto("phrase2", "不通过"),
                new MsgDto("date3", NormalTools.curDate()),
                new MsgDto("thing4", "只是测试一下"),
                new MsgDto("name5", "钟述林"));
    }

    @Test
    public void test22() throws Exception {
//      String enc = "1nd8fmlQhvy4cY8pTATXq4nSb5Hybt0KXjLQwYZ2BJjBPbJrtsHttVjLxbmhpY/Pf hj7uEFS27joxyRw6SXaZuV5SHOQs9t3RvCpzp6XVsxxblVVbs48gUq3NyeqNLJyX815guJQ8OrMFLqBrC7GqRbf3DitSuAX7FGhRr9idmA67dK74qteRletIgZXuVvJhZ9/CxanyD3OTAy55qZGQ==";
        String enc = "WESx5YpvF9 Mhq3IqAsyrsDSgQja0hW4uwXDSXLIRRnfRaPjmvTsVsMi w5sZu5KvR6kipAwvZyPCR2sVtbPm5oOYP94dAAmrVy7bzUMTxdBItdlmgxy4xf22GHDTMmmw3SBrLTRCAjYgJHoCkNZyByVPRYlhi2jtHM8rntJkLwtQO12rx/w7rGAQVQ8gyiZfnbgSVvbxJ0C8vUXBTEtdQ==";
        String session = "MACea/t1c+He45nADD4fbw==";
        String iv = "ccJVXB46olfuEYjl9pH/YQ==";

        Base64.decode(session);

//        String result = MiniPhoneTools.decrypt(session, iv, enc);
        String result = MiniUtils.getPhone(enc, session, iv);
//        String result = AES.wxDecrypt(enc, session, iv);
        System.out.println(result);
    }

    @Test
    public void test21() {
        String str = NormalTools.curDatetime();
//        String str2 = CouponTools.buildEndTime(86400);
        System.out.println("str1: "+str);
        System.out.println("str2: "+str);
    }

    @Test
    public void test20() {
        Coupon c = couponDao.findByRuleSn("TEST");
        Coupon c1 = couponDao.findByRuleSn("BUY_PRODUCT");
        System.out.println("test:::"+c);
        System.out.println("product:::"+c1);
        System.out.println("----------->");
    }

    @Test
    public void test19() {
        List<Product> list = productDao.searchByTitle("苹果");
        System.out.println("---------->size::"+list.size());
        for(Product p : list) {
            System.out.println(p);
        }
    }

    @Test
    public void test18() {
        Product p = productDao.findOne(1);
        for(int i=0;i<22;i++) {
            Product np = new Product();
            MyBeanUtils.copyProperties(p, np, "id");
            np.setTitle(p.getTitle()+"_"+(i+1));
            np.setReadCount(0);
            np.setFavoriteCount(0);
            productDao.save(np);
            for(int j=1;j<=3;j++) {
                ProductSpecs ps = new ProductSpecs();
                ps.setCateId(np.getCateId());
                ps.setCateName(np.getCateName());
                ps.setName("果号—"+i+"-"+j);
                ps.setOrderNo(j);
                ps.setOriPrice((i+1)*j*1.5f);
                ps.setPrice((i+1)*j*1.0f);
                ps.setProId(np.getId());
                ps.setProTitle(np.getTitle());
                ps.setRemark("果号—"+i+"-"+j+" 这里是描述");
                productSpecsDao.save(ps);
            }
        }
    }

    @Test
    public void test17() {
        ProductFavoriteRecord pfr = productFavoriteRecordDao.findOne(9);
        System.out.println(pfr);
        for(int i=0;i<33;i++) {
            ProductFavoriteRecord p = new ProductFavoriteRecord();
            MyBeanUtils.copyProperties(pfr, p, "id");
            p.setProTitle(pfr.getProTitle()+"_"+i);
            productFavoriteRecordDao.save(p);
        }
    }

    @Test
    public void test16() throws Exception {
        String clsName = "webInterceptorService", methodName = "loadWebBase";
//        String clsName = "adminUserService", methodName = "login";
        Object obj = getApplicationContext().getBean(clsName);
        Method method = obj.getClass().getDeclaredMethod(methodName,"params".getClass());

        Class<?> userClass = ClassUtils.getUserClass(obj);
        //method代表接口中的方法，specificMethod代表实现类中的方法
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, userClass);
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
        Annotation [] annotations = specificMethod.getAnnotations();
        System.out.println("==========>size:: "+annotations.length);
        for(Annotation an : annotations) {
            System.out.println("---------------------------============="+an.annotationType().getName());
        }
//        TransactionAttribute txAtt = findTransactionAttribute(specificMethod);
    }

    @Test
    public void test15() throws Exception {
//        String clsName = "webInterceptorService", methodName = "loadWebBase";
        String clsName = "adminUserService", methodName = "login";
//        Object obj = factory.getBean("adminUserService");
//        Object obj = getApplicationContext().getBean("adminUserService");
        Object obj = getApplicationContext().getBean(clsName);
//        Class obj = Class.forName("com.zslin.core.service.AdminUserService");
//        System.out.println(obj.getClass().getName()+"========");
//        Method method = obj.getClass().getMethod("login", "params".getClass());
        Method method = obj.getClass().getDeclaredMethod(methodName,"params".getClass());

        Annotation[] annotations = method.getAnnotations();
        System.out.println(method.getName()+"------->annSize: "+ annotations.length);
        for(Annotation an : annotations) {
            System.out.println("---------------------------============="+an.annotationType().getName());
        }
        Annotation[] annos = method.getDeclaredAnnotations();
        System.out.println("------>decSize: "+annos.length);
        for (Annotation an : annos) {
            System.out.println(an.annotationType().getName());
        }

        Annotation ann = method.getAnnotation(NeedAuth.class);
        System.out.println("ann======>"+ann);
    }

    @Test
    public void test14() {
        JsonResult jr = testService.add("");
        System.out.println(jr);
    }

    @Test
    public void test13() {
        String str = "{\"address\":\"嘎斯地方阿斯蒂芬\",\"provinceCode\":\"110000\",\"cityCode\":\"110100\",\"sex\":\"1\",\"papers\":[{\"name\":\"身份证正面\",\"id\":80,\"url\":\"https://zz-specialty.zslin.com/agent_e7b96a3f-fc87-4b30-8b2f-8f09c3e538d1.png\"},\n" +
                "{\"name\":\"身份证背面\",\"id\":81,\"url\":\"https://zz-specialty.zslin.com/agent_6575dc4b-de2b-4d60-a23f-a413144e5a15.png\"}],\"countyCode\":\"110102\",\"headerParams\":{\"unionid\":\"okOD4jgutW_OHQBkIJYD8NL4NhEU\",\"apicode\":\"min\n" +
                "iAgentService.addAgent\",\"openid\":\"oHoS55Tke2HI5m62XKVXRwRm_HAk\",\"nickname\":\"想攀登的胖子\",\"authtoken\":\"test-token\",\"customid\":\"2\"},\"cityName\":\"市辖区\",\"phone\":\"15925061256\",\"identity\":\"532127198803011115\",\"name\n" +
                "\":\"颖三要\",\"hasExperience\":\"1\",\"provinceName\":\"北京市\",\"countyName\":\"西城区\"}";
        JSONArray jsonArray = JsonTools.str2JsonArray(JsonTools.getJsonParam(str, "papers"));
        for(int i=0;i<jsonArray.size();i++) {
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            System.out.println("----->" + jsonObj.toJSONString());
        }

/*        ----->{"name":"身份证正面","id":80,"url":"https://zz-specialty.zslin.com/agent_e7b96a3f-fc87-4b30-8b2f-8f09c3e538d1.png"}
        ----->{"name":"身份证背面","id":81,"url":"https://zz-specialty.zslin.com/agent_6575dc4b-de2b-4d60-a23f-a413144e5a15.png"}*/

    }

    @Test
    public void test12() {
        String str = "[{\"orderNo\":4,\"name\":\"移动端管理\",\"id\":158},{\"orderNo\":2,\n" +
                "\"name\":\"七牛管理\",\"id\":271},{\"orderNo\":3,\"name\":\"产品管理\",\"id\":235}]";
        sortTools.handler("AdminMenu", str);
    }

    @Test
    public void test11() {
        buildAdminMenuTools.buildAdminMenusOrderNo();
    }

    @Test
    public void test10() {
        productDao.plusSpecsCount(5, 1);
    }

    @Test
    public void test09() {
        beanCheckTools.checkMethod("testService", "handler", "zslzsl");
        System.out.println("++++++++++++++++++++++++++++++");
        beanCheckTools.checkMethod("testService", "handler", "sdfsf", 5);
        System.out.println("=========================================");
        beanCheckTools.checkMethod("testService", "handler");
        System.out.println("1----------------------------------------------1");
        beanCheckTools.checkMethod("testService", "handler", null);
        System.out.println("2----------------------------------------------2");
        beanCheckTools.checkMethod("testService", "test", null);
        System.out.println("3----------------------------------------------3");
        beanCheckTools.checkMethod("zslzsService", "handler", null);
    }

    @Test
    public void test08() {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        System.out.println(NormalTools.getDate("2019-12-18 18:18:18", pattern));
    }

    @Test
    public void test07() {
        String enc = "HSu0ylkXSAPqgi8eZxSFdjlNUTvmU5d3zzj7Egd fDc2wty0vtiE2s3qyB/jN34GdOFgI3n1d7OLCYo1t/TGoM 7wWXGM1S qVfI9KYjqDuXhhTMBeJ2Ffl1Ahhw8 bfCnG9O IbutFXXYvceWnfDZH/RFIKHxfA katYBNH7oxLJx5/Z4E7nA3OuEpXVbLjJvGlSsuOPX16sT5q/73xmoR 53/tkhK06VWBlh9PyxOPTtYxw1aBWbU6qBPqWkNVQr1GVTa/yPTq8wkJW4eju2iRX4Iq9ZZauFzkebeElLWlo5dU J0r5aQpLBbBIT3MfttpGW iFQUW KCbE1Z3O2Vkm1QMjn1qAJPaH QUCXbH4NaFecMpW0veCTM4U25H1DlpnQcDG5/1qiUhk3dGEpJdJ2tH63xFkHze9 Hm0DwivYys3L41S/msndCTToxDkFQh03AQN5nO qABGLEm9ucr/q/2dxgMda6vq6r6th M7l2oFgk05GOhzjFeDxKKw2lCIUbFePAX C9XA26dmLW n8sGCzRbZDmPRSKBd1M=";
        enc = enc.replaceAll(" ", "+");
//               enc = "HSu0ylkXSAPqgi8eZxSFdjlNUTvmU5d3zzj7Egd+fDc2wty0vtiE2s3qyB/jN34GdOFgI3n1d7OLCYo1t/TGoM+7wWXGM1S+qVfI9KYjqDuXhhTMBeJ2Ffl1Ahhw8+bfCnG9O+IbutFXXYvceWnfDZH/RFIKHxfA+katYBNH7oxLJx5/Z4E7nA3OuEpXVbLjJvGlSsuOPX16sT5q/73xmoR+53/tkhK06VWBlh9PyxOPTtYxw1aBWbU6qBPqWkNVQr1GVTa/yPTq8wkJW4eju2iRX4Iq9ZZauFzkebeElLWlo5dU+J0r5aQpLBbBIT3MfttpGW+iFQUW+KCbE1Z3O2Vkm1QMjn1qAJPaH+QUCXbH4NaFecMpW0veCTM4U25H1DlpnQcDG5/1qiUhk3dGEpJdJ2tH63xFkHze9+Hm0DwivYys3L41S/msndCTToxDkFQh03AQN5nO+qABGLEm9ucr/q/2dxgMda6vq6r6th+M7l2oFgk05GOhzjFeDxKKw2lCIUbFePAX+C9XA26dmLW+n8sGCzRbZDmPRSKBd1M=";
        String iv = "R5W2FVYMEDD29BHk0aDFlg==";
        String sessionKey = "QbnmzydXklCXXNcIrhqw6A==";
        NewCustomDto res = MiniUtils.decryptionUserInfo(enc, sessionKey, iv);
        System.out.println("=========>"+res);
    }

    @Test
    public void test06() throws Exception {
        BufferedInputStream bis = miniCommonTools.getUnlimited("id=123&p=aaa", true, false);
        OutputStream os = new FileOutputStream(new File("D:/temp/1.png"));
        int len;
        byte[] arr = new byte[1024];
        while ((len = bis.read(arr)) != -1)
        {
            os.write(arr, 0, len);
            os.flush();
        }
        os.close();
    }

    @Test
    public void test05() {
        String token = accessTokenTools.getAccessToken();
        System.out.println("------>"+token);
    }

    @Test
    public void test04() {
        System.out.println(NormalTools.curDate());
        System.out.println(NormalTools.curDatetime());
        System.out.println(NormalTools.getNow("yyyy-MM-dd HH:mm"));
        System.out.println(NormalTools.getNow("yyyy-MM-dd HH:mm:ss.d"));
    }

    @Test
    public void test03() {
        String code = "asdfsdf";
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=wxdc7b00047690374f&secret=bb3303d93f2837d553da56836cf24c68&js_code="+code+"&grant_type=authorization_code";
        String res = template.getForObject(url, String.class);
        System.out.println(res);
    }

    @Test
    public void test01() {
        System.out.println("-------------->");
    }

    @Test
    public void test02() throws Exception {
        String str = "JUU2JTgzJUIzJUU2JTk0JTgwJUU3JTk5JUJCJUU3JTlBJTg0JUU4JTgzJTk2JUU1JUFEJTkw";

        String real = Base64Utils.getFromBase64(str);
        System.out.println(real);

        real = URLDecoder.decode(real, "utf-8");

        System.out.println(real);
    }
}
