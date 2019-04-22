/**
 * @author helen
 * @date 2018年12月7日
 */
package testcase;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

import java.math.BigDecimal;

import org.apache.commons.lang3.ArrayUtils;
import org.testng.Reporter;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import common.BaseData;
import common.MyConfig;
import data.MemberData;
import data.MemberScoreData;
import data.PrizeData;
import dataProvider.UserDataProvider;
import user.Login;
import user.MemberDeliveryAddress;
import user.Registry;
import user.SignIn;
import user.SignInManage;
import user.VerificationCode;

/**
 * @Description:签到业务测试
 */
public class SignInTest {
	MyConfig myConfig = new MyConfig();
	BaseData baseData = new BaseData();
	MemberData memberData = new MemberData();
	MemberScoreData memberScoreData = new MemberScoreData();
	PrizeData prizeData = new PrizeData();
	VerificationCode verificationCode = new VerificationCode();
	Registry registry = new Registry();
	Login login = new Login();
	SignInManage signInManage = new SignInManage();
	SignIn signIn = new SignIn();
	MemberDeliveryAddress memberDeliveryAddress = new MemberDeliveryAddress();
	
	@Test
	public void test_addModelforUse() {
		Reporter.log("[运营中心操作]模板设置主流程");
		
		Reporter.log("1）登录运营中心");
		JSONObject sysLoginResult = login.sysLogin("helen", "123456li");
		assertEquals(sysLoginResult.getString("code"), "10000","运营中心登录检验");
		JSONObject userdata = sysLoginResult.getJSONObject("data");
		
		Reporter.log("2）检查是否存在[使用中]的模板，如果有，则把该模板禁用");
		JSONObject prizeModelListResult = signInManage.prizeModelList(userdata, "", 1, -1);
		assertEquals(prizeModelListResult.getString("code"), "10000","查询[使用中]的模板列表");
		JSONArray prizeModelList = prizeModelListResult.getJSONObject("data").getJSONArray("list");
		for (int i = 0; i < prizeModelList.size(); i++) {
			JSONObject disableResult = signInManage.prizeModelStart(userdata, prizeModelList.getJSONObject(i).getString("id"), "2", baseData.getTimeStamp(0),
					baseData.getTimeStamp(2));
			assertEquals(prizeData.get_keyValue(prizeModelList.getJSONObject(i).getString("id"), "status"), "2", "模板禁用校验:" + disableResult);
		}

		String modelName = "4实物2个虚拟的抽奖模板" + baseData.getNum(0, 99999);
		Reporter.log("3）新增抽奖模板:"+modelName);
		JSONObject addresult = signInManage.prizeModelAdd(userdata, modelName, "15.1501");
		assertEquals(addresult.getString("code"), "10000", "新增模板校验:"+addresult);
		String templateId = addresult.getString("data");// 取得新增的模板Id
		assertEquals(Double.valueOf(prizeData.get_templateDrawRate(templateId)), Double.valueOf(1),"校验抽奖模板各奖项的中奖几率之和是否等于1");
		
/*		Reporter.log("2）增加同名模板：应当失败");// 目前该校验不通过，有待确认是否还有名称唯一校验
		JSONObject result2 = signInManage.prizeModelAdd(userdata, modelName, "15.1501");
		assertEquals(result2.getString("code"), "99999", "抽奖模板名称唯一校验：" + result2);*/

		Reporter.log("4）查看模板详情");
		JSONObject detailReulst = signInManage.prizeModelDetail(userdata, templateId);
		assertEquals(detailReulst.getString("code"), "10000", "模板详情响应校验：" + detailReulst);
		JSONArray detailList = detailReulst.getJSONArray("data");
		BigDecimal drawRate_sum = new BigDecimal("0");
		for (int i = 0; i < detailList.size(); i++) {
			BigDecimal drawRate = new BigDecimal(detailList.getJSONObject(i).getString("drawRate"));
			drawRate_sum=drawRate_sum.add(drawRate);
		}
		assertEquals(drawRate_sum.toString(), "1.000000","校验中奖率之和为1");

		Reporter.log("5）启用模板：还未审核通过，应当不可以启用");// ）
		JSONObject enableResult2 = signInManage.prizeModelStart(userdata, templateId, "1", baseData.getTimeStamp(80),
				baseData.getTimeStamp(82));
		assertEquals(enableResult2.getString("code"), "70030", "模板启用校验（应当失败）:" + enableResult2);
		assertEquals(enableResult2.getString("msg"), "启用抽奖模板前,模板审核状态不正确!!");
		
		Reporter.log("6）审核模板：审核通过");
		JSONObject auditResult = signInManage.prizeModelAudit(userdata, "1", templateId);
		assertEquals(prizeData.get_keyValue(templateId, "audit_status"), "1", "模板审核通过校验:" + auditResult);
		
		Reporter.log("7）启用模板：时间小于当前时间，应当失败");// 启用模板（启用时间小于当前时间，应当失败）
		JSONObject enableResult1 = signInManage.prizeModelStart(userdata, templateId, "1", baseData.getTimeStamp(-6),
				baseData.getTimeStamp(-4));
		assertEquals(enableResult1.getString("code"), "99999", "模板启用校验(应当失败):" + enableResult1);
		assertEquals(enableResult1.get("msg"), "模板结束时间小于当前时间");
		
		Reporter.log("8）启用模板：正常启用");// 启用模板
		JSONObject enableResult = signInManage.prizeModelStart(userdata, templateId, "1", baseData.getTimeStamp(60),
				baseData.getTimeStamp(62));
		assertEquals(prizeData.get_keyValue(templateId, "status"), "1", "模板启用校验:" + enableResult);
		
		Reporter.log("9）禁用模板");// 禁用模板
		JSONObject disableResult = signInManage.prizeModelStart(userdata, templateId, "2", baseData.getTimeStamp(0),
				baseData.getTimeStamp(2));
		assertEquals(prizeData.get_keyValue(templateId, "status"), "2", "模板禁用校验:" + disableResult);
		
		Reporter.log("10）数据库中把该模板的启用时间设置为过期的时间");//把模板时间设置为过期 
		prizeData.update_enableTime(templateId, baseData.getTime(0, -6, 0, "yyyy-MM-dd HH:mm:ss"), baseData.getTime(0, -4, 0, "yyyy-MM-dd HH:mm:ss"));
		
		Reporter.log("11）删除模板：已审核通过，应当不可以删除");// 删除（已审核通过，应当不可以删除）
		JSONObject deleteResult = signInManage.prizeModelDelete(userdata, templateId);
		assertEquals(deleteResult.getString("code"), "70030", "删除模板校验:" + deleteResult);
		assertEquals(deleteResult.getString("msg"), "删除抽奖模板,模板已审核通过无法删除!!");
		
		Reporter.log("12）启用模板");// 启用模板
		JSONObject enableResult3 = signInManage.prizeModelStart(userdata, templateId, "1", baseData.getTimeStamp(0),
				baseData.getTimeStamp(1));
		assertEquals(prizeData.get_keyValue(templateId, "status"), "1", "模板启用校验:" + enableResult3);
	}

	@Test
	public void test_prizeModelAdd_rateBiger1() {
		Reporter.log("新增抽奖模板：所有奖项几率之和超过100%（应当不可新增）");
		JSONObject userdata = login.sysLogin("helen", "123456li").getJSONObject("data");
		String modelName = "抽奖模板" + baseData.getNum(0, 99999);
		JSONObject result = signInManage.prizeModelAdd(userdata, modelName, "55.1501");
		assertEquals(result.getString("code"), "70027");
		assertEquals(result.getString("msg"), "所有奖项几率之和不得超过 100%");
	}

	@Test
	public void test_modelAddAUditStartDelete() {
		Reporter.log("主要校验：审核不通过的模板不可以启用，可以删除");
		JSONObject userdata = login.sysLogin("helen", "123456li").getJSONObject("data");
		
		String templateName = "模板" + baseData.getNum(0, 99999);
		Reporter.log("1）新增模板："+templateName);// 新增模板
		JSONObject addresult = signInManage.prizeModelAdd(userdata,templateName , "15.1501");
		String templateId = addresult.getString("data");// 取得新增的模板Id
		assertEquals(addresult.getString("code"), "10000", "新增模板校验");
		
		Reporter.log("2）审核不通过");// 审核不通过
		JSONObject auditResult = signInManage.prizeModelAudit(userdata, "2", templateId);
		assertEquals(prizeData.get_keyValue(templateId, "audit_status"), "2", "模板审核通过校验:" + auditResult);
		
		Reporter.log("3）启用：审核不通过，应当不可以启用");// 启用（审核不通过，应当不可以启用）
		JSONObject startResult = signInManage.prizeModelStart(userdata, templateId, "1", baseData.getTimeStamp(70),
				baseData.getTimeStamp(71));
		assertEquals(startResult.getString("code"), "70030", "启用校验(应当失败):" + startResult);
		assertEquals(startResult.getString("msg"), "启用抽奖模板前,模板审核状态不正确!!");
		
		Reporter.log("4）删除：审核不通过的模板，应当可以删除");// 删除
		JSONObject deleteResult = signInManage.prizeModelDelete(userdata, templateId);
		assertEquals(deleteResult.getString("code"), "10000", "删除模板校验:" + deleteResult);
		assertEquals(prizeData.get_keyValue(templateId, "count(*)"), "0", "模板为硬删除，校验模板ID是否为0");

	}

	@Test
	public void test_modelAddDelete() {
		Reporter.log("主要校验：新增未审核的模板可以删除");
		JSONObject userdata = login.sysLogin("helen", "123456li").getJSONObject("data");
		
		String templateName = "模板" + baseData.getNum(0, 99999);
		Reporter.log("1）新增模板："+templateName);// 新增模板
		JSONObject addresult = signInManage.prizeModelAdd(userdata, templateName, "15.1501");
		String templateId = addresult.getString("data");// 取得新增的模板Id
		assertEquals(addresult.getString("code"), "10000", "新增模板校验");
		
		Reporter.log("2）删除：未审核的模板，应当可以删除");// 删除
		JSONObject deleteResult = signInManage.prizeModelDelete(userdata, templateId);
		assertEquals(prizeData.get_keyValue(templateId, "count(*)"), "0", "删除模板校验:" + deleteResult);
	}

	@Test(dependsOnMethods="test_addModelforUse")
	public void test_modelAddStart() {
		Reporter.log("主要校验:时间重合的模板不能启用");
		JSONObject userdata = login.sysLogin("helen", "123456li").getJSONObject("data");
		
		Reporter.log("[对模板B的操作]－－－－－－－－－－");
		String templateIdB_name = "B模板" + baseData.getNum(0, 99999);
		Reporter.log("1）新增抽奖模板B："+templateIdB_name);// 新增模板b
		JSONObject addresultB = signInManage.prizeModelAdd(userdata, templateIdB_name, "15.1501");
		String templateIdB = addresultB.getString("data");// 取得新增的模板Id
		assertEquals(addresultB.getString("code"), "10000", "新增模板校验");
		
		Reporter.log("2）审核模板B：审核通过");// 审核模板b
		JSONObject auditResultB = signInManage.prizeModelAudit(userdata, "1", templateIdB);
		assertEquals(prizeData.get_keyValue(templateIdB, "audit_status"), "1", "模板审核通过校验:" + auditResultB);
		
		Reporter.log("3）启用模板B：时间与方法test_addModelforUse的模板A重合，应当启用失败");// 启用模板b
		JSONObject enableResultB = signInManage.prizeModelStart(userdata, templateIdB, "1", baseData.getTimeStamp(0),
				baseData.getTimeStamp(2));
		assertEquals(enableResultB.getString("code"), "70031", "模板启用校验(应当失败):" + enableResultB);

	}
	
	@Test(dependsOnMethods="test_addModelforUse",dataProvider="apiPlatform",dataProviderClass=UserDataProvider.class)
	public void test_oldMemberSign(String platform) {
		Reporter.log("老用户登录操作：校验主要业务流");
		String username = myConfig.getKeys("member1");

		Reporter.log("1）会员登录商城"+username);
		JSONObject loginResult = login.login(username, "123456li", platform);
		assertEquals(loginResult.getString("code"), "10000", "用户登录校验：" + loginResult);
		JSONObject userdata = loginResult.getJSONObject("data");
		String userId = userdata.getString("userId");
		
		Reporter.log("2）数据库中操作：会员数据，设置福豆100，并清空已往签到和获奖记录）");
		memberData.delete_signRecord(userId);// 清空以产的签到记录，以免影响测试结果
		double abledScore_beforSign = Double.valueOf("100");
		memberData.update_ableScore(username, "100");// 把用户的可用福豆设置为100

		Reporter.log("3）正常签到");
		JSONObject ordinarySignInReulst = signIn.ordinarySignIn(userdata, platform);
		assertEquals(ordinarySignInReulst.getString("code"), "10000", "会员正常签到：" + ordinarySignInReulst);
		double signScore = memberData.get_signScore(userId);
		double abledScore = memberScoreData.getMemberScore_ByUserId(userId, "abled_score");
		double[] signScoreList = new double[] { 2, 3, 4, 5, 6, 7 };
		assertTrue(ArrayUtils.contains(signScoreList, signScore), "检查签到获取的福豆是否在2~7之间：" + signScore);
		assertEquals(abledScore, abledScore_beforSign + signScore, "查看福豆是否添加正确");
		
		Reporter.log("4）重复签到：不可重复签到，应当失败");
		JSONObject ordinarySignInReulst2 = signIn.ordinarySignIn(userdata, platform);
		assertEquals(ordinarySignInReulst2.getString("code"), "99997", "会员当天重复签到（应当失败）：" + ordinarySignInReulst2);
		assertEquals(ordinarySignInReulst2.getString("msg"), "该会员该天已签到！");
		
		Reporter.log("5）数据库操作：给用户设置一张补签卡");
		memberData.update_signCard(userId, "1");// 给用户设置一张补签卡
		
		Reporter.log("6）补签昨日：消耗补签卡");
		String repairTime = baseData.getTime(0, -1, 0, "yyyy-MM-dd");
		JSONObject repairSignInResult = signIn.repairSignIn(userdata, repairTime, platform);
		assertEquals(repairSignInResult.getString("code"), "10000", "正常补签昨天：" + repairSignInResult);
		assertEquals(memberScoreData.getMemberScore_ByUserId(userId, "abled_score"), abledScore, "有补签卡优先使用补签卡，福豆不减少");
		String signCard = memberData.getSignStatisticsValue_ByUserId(userId, "sign_card");
		assertEquals(signCard, "0", "补签卡应当被使用完");

		Reporter.log("7）重复补签昨日：不可重复签到，应当失败");
		JSONObject repairSignInResult2 = signIn.repairSignIn(userdata, repairTime, platform);
		assertEquals(repairSignInResult2.getString("code"), "99999", "重复补签昨天：" + repairSignInResult2);
		assertEquals(repairSignInResult2.getString("msg"), "该会员该天已签到！");
		assertEquals(memberScoreData.getMemberScore_ByUserId(userId, "abled_score"), abledScore, "补签不成功，福豆应当不变");

		Reporter.log("8）补签前天：无补签卡，应当消耗10个福豆");
		JSONObject repairSignInResult3 = signIn.repairSignIn(userdata, baseData.getTime(0, -2, 0, "yyyy-MM-dd"),platform);
		assertEquals(repairSignInResult3.getString("code"), "10000", "正常补签前天：" + repairSignInResult3);
		abledScore = abledScore - 10;
		assertEquals(memberScoreData.getMemberScore_ByUserId(userId, "abled_score"), abledScore, "检查补签后福豆是否减少10个");

		Reporter.log("9）补签大前天：两次机会已用完，应当失败");
		JSONObject repairSignInResult4 = signIn.repairSignIn(userdata, baseData.getTime(0, -3, 0, "yyyy-MM-dd"),
				platform);
		assertEquals(repairSignInResult4.getString("code"), "99999", "正常补签大前天（机会用完应当失败）：" + repairSignInResult4);
		assertEquals(repairSignInResult4.getString("msg"), "本周补签机会已用完");
		
		//开启或关闭签到提醒
		String is_sign_remind = memberData.getSignStatisticsValue_ByUserId(userId, "is_sign_remind");
		if (is_sign_remind.equals("0")) {
			is_sign_remind = "1";
			Reporter.log("10）开启签到提醒");
		}
		else {
			is_sign_remind = "0";
			Reporter.log("10）关闭签到提醒");
		}
		JSONObject signRemindSetResult = signIn.signRemindSet(userdata, is_sign_remind, platform);
		assertEquals(signRemindSetResult.getString("code"), "10000","签到提醒"+signRemindSetResult);
		
		Reporter.log("11）获取会员签到页面相关统计信息");
		JSONObject statisticsInfoReulst = signIn.statisticsInfo(userdata, platform);
		assertEquals(statisticsInfoReulst.getString("code"), "10000","获取统计信息"+statisticsInfoReulst);
		assertEquals(statisticsInfoReulst.getJSONObject("data").getString("totalDays"), memberData.getSignStatisticsValue_ByUserId(userId, "total_days"),"累计签到天数校验");
		assertEquals(statisticsInfoReulst.getJSONObject("data").getString("fillSignDays"), memberData.getSignStatisticsValue_ByUserId(userId, "fill_sign_days"),"已补签天数校验");
		assertEquals(statisticsInfoReulst.getJSONObject("data").getString("runningDays"), memberData.getSignStatisticsValue_ByUserId(userId, "running_days"),"连续天数校验");
		assertEquals(statisticsInfoReulst.getJSONObject("data").getString("isSignRemind"), is_sign_remind,"签到提醒校验");
		
		Reporter.log("12）获取签到日历信息");
		JSONObject getSignInCalendarResult = signIn.getSignInCalendar(userdata, platform);
		assertEquals(getSignInCalendarResult.getString("code"), "10000","获取签到日历信息"+getSignInCalendarResult);
	}

	@Test(enabled=false,dataProvider="apiPlatform",dataProviderClass=UserDataProvider.class)
	public void test_newMemberSignlastdata(String platform) {
		Reporter.log("主要校验：新注册用户补签以往时间，应当补签失败");//该功能前端做控制，不再执行该后台业务测试

		String username = baseData.getPhoneNumber();
		Reporter.log("1）注册新用户："+username);
		//获取验证码
		JSONObject sendCodeResult = verificationCode.sendCode(username, "", 1, platform);
		assertEquals(sendCodeResult.getString("code"), "10000", "发送验证码校验" + sendCodeResult);
		String verifyCode = memberData.getVerificationCode(username, "phone");
		JSONObject registryResult = registry.register("43", username,verifyCode, "", platform, "phone");
		assertEquals(registryResult.getString("code"), "10000", "注册用户结果校验：" + registryResult);

		Reporter.log("2）新用户登录");
		JSONObject loginResult = login.login(username, "123456li", platform);
		assertEquals(loginResult.getString("code"), "10000", "用户登录校验：" + loginResult);
		JSONObject userdata = loginResult.getJSONObject("data");
		
		Reporter.log("3）补签昨日：补签日期小于用户注册日期，应当失败");
		String repairTime = baseData.getTime(0, -1, 0, "yyyy-MM-dd'T'HH:mm:ss'Z'");
		JSONObject repairSignInResult = signIn.repairSignIn(userdata, repairTime, platform);
		assertEquals(repairSignInResult.getString("code"), "99999", "新注册用户补签昨天（应当失败）：" + repairSignInResult);
		assertEquals(repairSignInResult.getString("msg"), "补签日期不能小于用户注册日期！");
	}

	@Test(dataProvider="apiPlatform",dataProviderClass=UserDataProvider.class)
	public void test_getPrize(String platform) {
		Reporter.log("主要测试百日签到礼品的获取：主要业务测试流");
		//String platform = "android";
		Reporter.log("[会员操作]－－－－－－－－－－");
		JSONArray usernameList = memberData.getAccount_createDay("100");//获取注册时间超过100天的最后一个用户名
		String username = "";
		if (platform.equals("android")) {
			username = usernameList.getJSONObject(0).getString("account");
		}
		else if (platform.equals("ios")) {
			username = usernameList.getJSONObject(1).getString("account");
		}
		else if (platform.equals("h5")) {
			username = usernameList.getJSONObject(2).getString("account");
		}
		else if (platform.equals("pc")) {
			username = usernameList.getJSONObject(3).getString("account");
		}
		
		//username = "15804995841";
		
		Reporter.log("1）会员登录："+username);
		JSONObject loginResult = login.login(username, "123456li", platform);
		assertEquals(loginResult.getString("code"), "10000", "用户登录校验：" + loginResult);
		JSONObject userdata = loginResult.getJSONObject("data");
		String userId = userdata.getString("userId");
		
		Reporter.log("2）数据库中操作：删除以往的签到详细记录、统计记录、中奖记录，以免影响测试结果");
		memberData.delete_signRecord(userId);
		
		Reporter.log("3）补签：补签一周前，应当失败");
		JSONObject repairSignInResult = signIn.repairSignIn(userdata, baseData.getTimeByDays(-14, "yyyy-MM-dd"),
				platform);
		assertEquals(repairSignInResult.getString("code"), "99999", "超出补签范围，应当补签失败" + repairSignInResult);
		assertEquals(repairSignInResult.getString("msg"), "补签日期只能是本周和上周！");

		Reporter.log("4）数据库操作：设置前99天连续签到");
		memberData.insert_sign(userId, baseData.getTimeByDays(-99, "yyyy-MM-dd"),
				baseData.getTimeByDays(-1, "yyyy-MM-dd"));
		memberData.update_signTag(userId, baseData.getTimeByDays(-99, "yyyy-MM-dd"));// 把第一天的[昨天签到标志sign_tag]改为0

		Reporter.log("5）正常签到：第100天连续签到");
		JSONObject ordinarySignInReulst = signIn.ordinarySignIn(userdata, platform);
		assertEquals(ordinarySignInReulst.getString("code"), "10000", "会员正常签到：" + ordinarySignInReulst);
		assertEquals(memberData.getLuckyDrawValue_ByUserIdAndDrawType(userId, "3", "count(*)"), "1", "连续签到100天获得1次额外礼物");
		String drawId = ordinarySignInReulst.getJSONObject("data").getString("drawRecordId");

		String addressId = memberDeliveryAddress.getAddrId(userdata, platform);
		Reporter.log("获取会员的收货地址ID："+addressId);
		
		Reporter.log("6）领取百日签到礼品");
		JSONObject userDrawApply = signIn.userDrawApply(userdata,addressId,drawId, platform);
		assertEquals(userDrawApply.getString("code"), "10000", "领取百日奖励" + userDrawApply);
		assertEquals(memberData.getLuckyDrawValue_ByUserIdAndDrawType(userId, "3", "status"), "1", "奖品状态改为[待发货]");

		Reporter.log("[运营中心操作]－－－－－－－－－－");
		Reporter.log("1）登录运营中心");
		JSONObject sysLogin = login.sysLogin("helen", "123456li");
		assertEquals(sysLogin.getString("code"), "10000", "运营中心登录" + sysLogin);
		JSONObject sysUser = sysLogin.getJSONObject("data");

		Reporter.log("2）百日中奖发货");
		JSONObject drawSend = signInManage.drawSend(sysUser, drawId);
		assertEquals(drawSend.getString("code"), "10000", "百日中奖发货：" + drawSend);
		assertEquals(memberData.getLuckyDrawValue_ByUserIdAndDrawType(userId, "3", "status"), "2", "奖的状态改为[已发货]");

		Reporter.log("[会员操作]－－－－－－－－－－");
		// 会员确认收货
		Reporter.log("1）百日中奖确认收货");
		JSONObject userDrawReceive = signIn.userDrawReceive(userdata, drawId, platform);
		assertEquals(userDrawReceive.getString("code"), "10000", "百日中奖确认收货：" + drawSend);
		assertEquals(memberData.getLuckyDrawValue_ByUserIdAndDrawType(userId, "3", "status"), "3", "奖的状态改为[已收货]");
	}
//dependsOnMethods="test_addModelforUse",
	@Test(dependsOnMethods="test_addModelforUse",dataProvider="apiPlatform",dataProviderClass=UserDataProvider.class)//dependsOnMethods="test_addModelforUse",
	public void test_luckyDraw(String platform) {
		Reporter.log("设置前1周周一至周六的签到－补签周日（获得一次抽奖）－抽奖-如果抽到实物就经过发货流程，如果抽到福豆福豆增加，如果抽到补签卡补签卡增加");
		
		//String platform="ios";
		Reporter.log("[会员操作]－－－－－－－－－－");
		JSONArray usernameList = memberData.getAccount_createDay("10");
		String username = "";
		if (platform.equals("android")) {
			username = usernameList.getJSONObject(0).getString("account");
		}
		else if (platform.equals("ios")) {
			username = usernameList.getJSONObject(1).getString("account");
		}
		else if (platform.equals("h5")) {
			username = usernameList.getJSONObject(2).getString("account");
		}
		else if (platform.equals("pc")) {
			username = usernameList.getJSONObject(3).getString("account");
		}
		
		try {
				Thread.sleep(50000);//处理服务器时间存在差异
			//	username = "15802346282";
				
				Reporter.log("1）会员登录"+username);
				JSONObject loginResult = login.login(username, "123456li", platform);
				assertEquals(loginResult.getString("code"), "10000", "用户登录校验：" + loginResult);
				JSONObject userdata = loginResult.getJSONObject("data");
				String userId = userdata.getString("userId");
				
				Reporter.log("2）数据库操作：删除以往的签到详细记录、统计记录、中奖记录，以免影响测试结果");
				memberData.delete_signRecord(userId);// 清空以产的签到记录，以免影响测试结果
				
				Reporter.log("3）数据库操作：把用户的可用福豆设置为100");
				memberData.update_ableScore(username, "100");// 把用户的可用福豆设置为100

				Reporter.log("4）数据库操作：设置前1周周日起，前99天的签到");
				memberData.insert_sign(userId, baseData.getTimeByWeek( -1,7,-99, "yyyy-MM-dd"),
						baseData.getTimeByWeek(-1, 7, "yyyy-MM-dd"));
				memberData.update_signTag(userId, baseData.getTimeByWeek( -1,7,-99, "yyyy-MM-dd"));// 把第一天的[昨天签到标志sign_tag]改为0，否则会影响补签
				
				Reporter.log("5）补签周日：同时获得一次抽奖和百日签到礼品");
				String repairTime = baseData.getTimeByWeek(0, 1, "yyyy-MM-dd");
				JSONObject repairSignInResult = signIn.repairSignIn(userdata, repairTime, platform);
				assertEquals(repairSignInResult.getString("code"), "10000", "正常补签周日：" + repairSignInResult);
				String drawId = memberData.getLuckyDrawValue_ByUserIdAndDrawType(userId, "1", "id");
				assertNotEquals(drawId, "", "检查是否获得周末抽奖机会");
				assertEquals(memberData.getLuckyDrawValue_ByUserIdAndDrawType(userId, "3", "count(*)"), "1", "连续签到100天获得1次额外礼物");

				Reporter.log("6）获取本次抽奖模板的奖品列表");
				JSONObject luckyDrawListResult = signIn.luckyDrawList(userdata, platform);
				assertEquals(luckyDrawListResult.getString("code"), "10000", "本次抽奖模板的奖品列表" + luckyDrawListResult);
				assertEquals(luckyDrawListResult.getJSONObject("data").getString("templateId"), prizeData.get_inUseTemplatID(),"判断正在使用的模板是否正确");

				Reporter.log("7）进行抽奖");
				JSONObject luckyDraw = signIn.luckyDraw(userdata, platform);
				assertEquals(luckyDraw.getString("code"), "10000", "签到转盘抽奖" + luckyDraw);
				
				if (luckyDraw.getJSONObject("data").getString("type").equals("1")) {// 抽到实物商品，走发货收货流程
					Reporter.log("8）获取用户实物奖品列表");
					JSONObject userDrawListResult = signIn.userDrawList(userdata, "1", platform);
					assertEquals(userDrawListResult.getString("code"), "10000","用户获奖列表"+userDrawListResult);
					
					String addressId = memberDeliveryAddress.getAddrId(userdata, platform);
					Reporter.log("获取会员的收货地址ID："+addressId);

					Reporter.log("9）领取奖品");
					JSONObject userDrawApply = signIn.userDrawApply(userdata,addressId, drawId, platform);
					assertEquals(userDrawApply.getString("code"), "10000", "领取抽奖奖励" + userDrawApply);
					assertEquals(memberData.getLuckyDrawValue_ByUserIdAndDrawType(userId, "1", "status"), "1", "奖品状态改为[待发货]");
					
					Reporter.log("[运营中心操作]－－－－－－－－－－");
					Reporter.log("1）登录运营中心");
					JSONObject sysLogin = login.sysLogin("helen", "123456li");
					assertEquals(sysLogin.getString("code"), "10000", "运营中心登录" + sysLogin);
					JSONObject sysUser = sysLogin.getJSONObject("data");

					Reporter.log("2）抽奖发货");
					JSONObject drawSend = signInManage.drawSend(sysUser, drawId);
					assertEquals(drawSend.getString("code"), "10000", "抽奖发货：" + drawSend);
					assertEquals(memberData.getLuckyDrawValue_ByUserIdAndDrawType(userId, "1", "status"), "2", "奖的状态改为[已发货]");

					Reporter.log("[会员操作]－－－－－－－－－－");
					Reporter.log("1）会员确认收货");
					JSONObject userDrawReceive = signIn.userDrawReceive(userdata, drawId, platform);
					assertEquals(userDrawReceive.getString("code"), "10000", "抽奖确认收货：" + drawSend);
					assertEquals(memberData.getLuckyDrawValue_ByUserIdAndDrawType(userId, "1", "status"), "3", "奖的状态改为[已收货]");
				} else if (luckyDraw.getJSONObject("data").getString("type").equals("2")) {// 虚拟商品
					//获取用户奖品列表
					Reporter.log("8）获取用户虚拟奖品列表，并检查福豆或补签卡是否加到用户账户上");
					JSONObject userDrawListResult = signIn.userDrawList(userdata, "2", platform);
					assertEquals(userDrawListResult.getString("code"), "10000","用户获奖列表"+userDrawListResult);
					String prizeItemId = luckyDraw.getJSONObject("data").getString("prizeItemId");// 奖项的ID
					if (luckyDraw.getJSONObject("data").getString("prizeName").equals("福豆")) {// 抽到福豆
						String ableScore = prizeData.get_itemInfoById(prizeItemId, "cost");// 当前福豆
						assertEquals(Double.valueOf(memberScoreData.getMemberScore_ByUserId(userId, "abled_score")),
								100-10 + Double.valueOf(ableScore));
					} else if (luckyDraw.getJSONObject("data").getString("prizeName").equals("补签卡")) {// 抽到补签卡
						assertEquals(memberData.getSignStatisticsValue_ByUserId(userId, "sign_card"), "1", "现在有一个补签卡（因为之前的全部已清空）");
					}
				} else {
					System.out.println("没有该奖品类型");
				}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
