/**
 * @author helen
 * @date 2018年12月3日
 */
package testcase;

import static org.testng.Assert.assertEquals;

import org.testng.Reporter;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import common.BaseData;
import common.MyConfig;
import data.AgentData;
import data.MemberData;
import data.PubData;
import dataProvider.UserDataProvider;
import marketing.CouponUse;
import operationCenter.SellerAdmin;
import seller.SupplierRegister;
import user.Login;
import user.Registry;
import user.VerificationCode;

/**
 * @Description:注册业务
 */
public class RegistryTest {
	MyConfig myConfig = new MyConfig();
	BaseData baseData = new BaseData();
	PubData pubData = new PubData();
	AgentData agentData = new AgentData();
	MemberData memberData = new MemberData();
	Login login = new Login();
	VerificationCode verificationCode = new VerificationCode();
	CouponUse couponUse = new CouponUse();
	Registry registry= new Registry();
	SupplierRegister supplierRegister = new SupplierRegister();
	SellerAdmin sellerAdmin = new SellerAdmin();
	
	/*商家注册*/
	@Test
	public void test_sellerRegistry() {
		Reporter.log("商家注册测试");
		String telephoneCodeId = "43";//中国
		String telephoneCode = pubData.get_telephoneCode(telephoneCodeId, "short_area_code");
		String sellerName = memberData.get_unexitUsername("phone");
		String agentAccount = "";//代理商账号
		
		Reporter.log("[商家操作]--------");
		Reporter.log("1）发送验证码，手机号："+sellerName);
		JSONObject sendCodeResult = verificationCode.sendCode(sellerName, telephoneCode, 2, "seller");
		assertEquals(sendCodeResult.getString("code"), "10000","发送验证码校验"+sendCodeResult);
		String verifyCode = memberData.getVerificationCode(sellerName, "phone");
		
		Reporter.log("2）注册");
		JSONObject registerResult = registry.register(telephoneCodeId, sellerName,verifyCode, "", "seller", "phone");
		assertEquals(registerResult.getString("code"), "10000","注册"+registerResult);
		JSONObject sellerUserdata = registerResult.getJSONObject("data");
		
		String supplierName = "旅游酒店"+baseData.getNum(0, 10000);
		Reporter.log("3）完善商家信息");
		JSONObject addResult = supplierRegister.add(sellerUserdata, 377, sellerName, supplierName, agentAccount);
		assertEquals(addResult.getString("code"), "10000","完善商家信息"+addResult);
		
		Reporter.log("[运营中心操作]--------");
		JSONObject sysLoginResult = login.sysLogin("helen", "123456li");
		assertEquals(sysLoginResult.getString("code"), "10000","运营中心登录");
		JSONObject sysUserdata = sysLoginResult.getJSONObject("data");
		
		Reporter.log("1）审核商家，审核通过");
		JSONObject sellAuditResult = sellerAdmin.sellerAudit(sysUserdata, sellerUserdata.getIntValue("supplierId"), 1);
		assertEquals(sellAuditResult.getString("code"), "10000","审核商家"+sellAuditResult);
	}
	
	/*会员注册*/
	@Test(dataProvider = "platform", dataProviderClass = UserDataProvider.class)
	public void test_memberRegistryWithInviteMan1(String platform) {
		Reporter.log("注册填写的邀请人不存在（邮箱）");
		
		String username = memberData.get_unexitUsername("email");
		Reporter.log("1）发送验证码："+username);
		JSONObject sendCodeResult = verificationCode.sendCode(username, "", 2, platform);
		assertEquals(sendCodeResult.getString("code"), "10000","发送验证码校验"+sendCodeResult);
		String verifyCode = memberData.getVerificationCode(username, "email");
		
		String inviter = memberData.get_unexitUsername("email");
		Reporter.log("2)注册,邀请人账号："+inviter);
		JSONObject result = registry.register("125", username,verifyCode, inviter, platform, "email");
		assertEquals(String.valueOf(result.get("code")), "99999", "邮箱注册(邀请人不存在)校验" + result);
		assertEquals(result.getString("msg"), "邀请人不存在！");
	}
	
	@Test(dataProvider = "platform", dataProviderClass = UserDataProvider.class)
	public void test_memberRegistryWithInviteMan2(String platform) {
		Reporter.log("注册填写的邀请人不存在（手机）");
		String telephoneCodeId = "43";//中国
		String telephoneCode = pubData.get_telephoneCode(telephoneCodeId, "short_area_code");
		
		String username = memberData.get_unexitUsername("phone");
		Reporter.log("1）发送验证码："+username);
		JSONObject sendCodeResult = verificationCode.sendCode(username, telephoneCode, 2, platform);
		assertEquals(sendCodeResult.getString("code"), "10000","发送验证码校验"+sendCodeResult);
		String verifyCode = memberData.getVerificationCode(username, "phone");
		
		String inviter = memberData.get_unexitUsername("phone");
		Reporter.log("2)邀请人账号："+inviter);
		JSONObject result = registry.register(telephoneCodeId, username,verifyCode, inviter, platform, "phone");
		assertEquals(String.valueOf(result.get("code")), "99999", "手机注册(邀请人不存在)校验" + result);
		assertEquals(result.getString("msg"), "邀请人不存在！");
	}
	
	@Test(dataProvider = "platform", dataProviderClass = UserDataProvider.class)
	public void test_memberCheckAccount(String platform) {
		if (platform.equals("pc")) {
			Reporter.log("PC商城没有[验证号码邮箱是否已注册]接口");
		} else {
			Reporter.log(platform + "验证号码邮箱是否已注册：输入正常正在使用的账号");
			String username = memberData.get_accountByStatus("0");
			JSONObject result = registry.checkAccount(username, platform);
			assertEquals(String.valueOf(result.get("code")), "20016", "账号：" + result);
			assertEquals(result.get("msg"), "此账号已注册");
		}
	}

	@Test(dataProvider = "platform", dataProviderClass = UserDataProvider.class)
	public void test_memberRegister_phone(String platform) {
		Reporter.log("手机正常注册");
		String telephoneCodeId = "43";//中国
		String telephoneCode = pubData.get_telephoneCode(telephoneCodeId, "short_area_code");
		
		String username = memberData.get_unexitUsername("phone");
		Reporter.log("1）发送验证码");
		JSONObject sendCodeResult = verificationCode.sendCode(username, telephoneCode, 2, platform);
		assertEquals(sendCodeResult.getString("code"), "10000","发送验证码校验"+sendCodeResult);
		String verifyCode = memberData.getVerificationCode(username, "phone");
		
		Reporter.log("2)注册账号："+username);
		JSONObject result = registry.register(telephoneCodeId, username,verifyCode, myConfig.getKeys("member1"), platform, "phone");
		assertEquals(String.valueOf(result.get("code")), "10000", "手机注册" + result);
		
		Reporter.log("3)新账号登录商城");
		JSONObject loginResult = login.login(username, "123456li", platform);
		assertEquals(loginResult.getString("code"), "10000","校验登录"+loginResult);
		JSONObject userdata = loginResult.getJSONObject("data");
		
		Reporter.log("4)检查新注册用户送100福豆的情况");
		assertEquals(Double.valueOf(memberData.getScore_userId(userdata.getString("userId"), "abled_score")), Double.valueOf("100"),"校验新注册的用户是否有送100福豆");

		Reporter.log("5)检查送注册券的情况");
		JSONObject myCouponResult = couponUse.myCouponList(userdata, 3, platform);
		assertEquals(myCouponResult.getString("code"), "10000","查询我的全部优惠券"+myCouponResult);
		JSONArray couponList = myCouponResult.getJSONObject("data").getJSONArray("couponList");
		assertEquals(couponList.getJSONObject(0).getString("couponId"), myConfig.getKeys("couponId"),"校验新注册的用户[我的优惠券]列表中有注册券");
	}

	@Test(dataProvider = "platform", dataProviderClass = UserDataProvider.class)
	public void test_memberRegister_email(String platform) {
		Reporter.log("邮箱注册");
		
		String username = memberData.get_unexitUsername("email");
		Reporter.log("1）发送验证码："+username);
		JSONObject sendCodeResult = verificationCode.sendCode(username, "", 2, platform);
		assertEquals(sendCodeResult.getString("code"), "10000","发送验证码校验"+sendCodeResult);
		String verifyCode = memberData.getVerificationCode(username, "email");
		
		Reporter.log("2)注册账号："+username);
		JSONObject result = registry.register("", username,verifyCode, myConfig.getKeys("member2"), platform, "email");
		assertEquals(String.valueOf(result.get("code")), "10000", "邮箱注册" + result);
		
		Reporter.log("3)新账号登录商城");
		JSONObject loginResult = login.login(username, "123456li", platform);
		assertEquals(loginResult.getString("code"), "10000","校验登录"+loginResult);
		JSONObject userdata = loginResult.getJSONObject("data");
		
		Reporter.log("4)检查新注册用户送100福豆的情况");
		assertEquals(Double.valueOf(memberData.getScore_userId(userdata.getString("userId"), "abled_score")), Double.valueOf("100"),"校验新注册的用户是否有送100福豆");

		Reporter.log("5)检查送注册券的情况");
		JSONObject myCouponResult = couponUse.myCouponList(userdata, 3, platform);
		assertEquals(myCouponResult.getString("code"), "10000","查询我的全部优惠券"+myCouponResult);
		JSONArray couponList = myCouponResult.getJSONObject("data").getJSONArray("couponList");
		assertEquals(couponList.getJSONObject(0).getString("couponId"), myConfig.getKeys("couponId"),"校验新注册的用户[我的优惠券]列表中有注册券");
	}

	@Test(dataProvider = "telephoneId", dataProviderClass = UserDataProvider.class)
	public void test_memberRegister_allArea(String telephoneId, String name_zh) {
		Reporter.log("android平台：各地区(" + name_zh + ")手机号注册");
		String username = memberData.get_unexitUsername("phone");
		Reporter.log("1）发送验证码");
		String telephoneCode = pubData.get_telephoneCode(telephoneId, "short_area_code");
		JSONObject sendCodeResult = verificationCode.sendCode(username, telephoneCode, 2, "android");
		assertEquals(sendCodeResult.getString("code"), "10000","发送验证码校验"+sendCodeResult);
		String verifyCode = memberData.getVerificationCode(username, "phone");
		
		Reporter.log("2）注册："+username);
		JSONObject result = registry.register(telephoneId, username,verifyCode, myConfig.getKeys("member1"), "android", "phone");
		assertEquals(String.valueOf(result.get("code")), "10000","检查地址注册结果"+result);
	}

	@Test
	public void test_memberRegister_H5Phone() {
		Reporter.log("H5分享页注册(手机注册)");

		String username = memberData.get_unexitUsername("phone");
		Reporter.log("1）发送验证码");
		JSONObject sendCodeResult = verificationCode.sendCode(username, "", 2, "h5share");
		assertEquals(sendCodeResult.getString("code"), "10000","发送验证码校验"+sendCodeResult);
		String verifyCode = memberData.getVerificationCode(username, "phone");
		
		Reporter.log("2）注册"+username);
		JSONObject result = registry.register("125", username,verifyCode, myConfig.getKeys("member1"), "h5share", "phone");
		assertEquals(String.valueOf(result.get("code")), "10000","H5分享页手机注册校验"+result);
	}

	@Test
	public void test_memberRegister_H5Email() {
		Reporter.log("H5分享页注册(邮箱注册)");
		String username = memberData.get_unexitUsername("email");
		Reporter.log("1）发送验证码："+username);
		JSONObject sendCodeResult = verificationCode.sendCode(username, "", 2, "h5share");
		assertEquals(sendCodeResult.getString("code"), "10000","发送验证码校验"+sendCodeResult);
		String verifyCode = memberData.getVerificationCode(username, "email");
		
		Reporter.log("2）注册"+username);
		JSONObject result = registry.register("", username,verifyCode, myConfig.getKeys("member2"), "h5share", "email");
		assertEquals(String.valueOf(result.get("code")), "10000");
	}

	@Test(dataProvider = "platform", dataProviderClass = UserDataProvider.class)
	public void test_getMemberCode(String platform) {
		if (platform.equals("pc")) {
			Reporter.log("PC商城没有获取邀请奖励接口");
		} else {
			Reporter.log(platform + "获取邀请奖励信息查询");
			JSONObject userdata = login.login(myConfig.getKeys("member1"), "123456li", platform)
					.getJSONObject("data");
			JSONObject result = registry.getMemberCode(userdata, platform);
			assertEquals(result.getString("code"), "10000","获取邀请奖励"+result);
		}
	}

	@Test(dataProvider = "platform", dataProviderClass = UserDataProvider.class)
	public void test_recommendCodeBind(String platform) {
		if (platform.equals("pc")) {
			Reporter.log("pc商城没有绑定邀请人接口");
		} else {
			Reporter.log("绑定邀请人");
			JSONObject loginResult = login.login(memberData.getAccount_noInviter(), "123456li", platform);
			assertEquals(loginResult.getString("code"), "10000","登录校验"+loginResult);
			JSONObject userdata = loginResult.getJSONObject("data");
			JSONObject result = registry.recommendCodeBind(userdata, "13822289691", platform);
			assertEquals(result.getString("code"), "10000");
		}

	}
	
	@Test(dataProvider="platform",dataProviderClass=UserDataProvider.class)
	public void test_sendCode1(String platform) {
		Reporter.log("注册发送验证码：手机已注册（会员）");
		String username = memberData.get_account("phone");
		JSONObject result = verificationCode.sendCode(username, "", 2,platform);
		assertEquals(String.valueOf(result.get("code")), "99999","会员注册，手机号已注册校验"+result);
		assertEquals(result.get("msg"), "该手机号已被注册","账号："+username);
	}
	
	@Test(dataProvider="platform",dataProviderClass=UserDataProvider.class)
	public void test_sendCode2(String platform) {
		Reporter.log("注册发送验证码：邮箱已注册(代理商和商家前端已控制不能输入邮箱，所以只要校验会员即可)");
		
		String username = memberData.get_account("email");
		JSONObject result = verificationCode.sendCode(username, "", 2,platform);
		assertEquals(String.valueOf(result.get("code")), "99999","会员邮箱已注册校验结果"+result);
		assertEquals(result.get("msg"), "该邮箱已被注册","账号："+username);
	}
	
	@Test
	public void test_sendCode3() {
		Reporter.log("注册发送验证码：手机已注册(代理商)");
		String username = agentData.getLastAccount();
		JSONObject result = verificationCode.sendCode(username, "", 2,"agent");
		assertEquals(String.valueOf(result.get("code")), "99999","代理商注册，手机号已注册校验"+result);
		assertEquals(result.get("msg"), "该手机号已被注册","账号："+username);
	}
	
	@Test
	public void test_sendCode4() {
		Reporter.log("注册发送验证码：手机已注册(商家)");
		String username = agentData.getLastAccount();
		JSONObject result = verificationCode.sendCode(username, "", 2,"seller");
		assertEquals(String.valueOf(result.get("code")), "99999","商家注册，手机号已注册校验"+result);
		assertEquals(result.get("msg"), "该手机号已被注册","账号："+username);
	}
	

}
