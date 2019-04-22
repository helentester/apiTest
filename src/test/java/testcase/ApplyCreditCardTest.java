/**
 * @author helen
 * @date 2018年12月4日
 */
package testcase;

import static org.testng.Assert.assertEquals;

import org.testng.Reporter;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONObject;

import common.BaseData;
import data.AgentData;
import data.MemberData;
import dataProvider.UserDataProvider;
import user.ApplyCreditCard;
import user.AuthRealName;
import user.Login;
import user.Registry;

/**
 * @Description:信用卡申请业务
 */
public class ApplyCreditCardTest {
	BaseData baseData = new BaseData();
	AgentData agentData = new AgentData();
	MemberData memberData = new MemberData();
	Login login = new Login();
	ApplyCreditCard applyCreditCard = new ApplyCreditCard();
	AuthRealName authRealName = new AuthRealName();
	Registry registry = new Registry();

	@Test(dataProvider="autoRealNameData",dataProviderClass=UserDataProvider.class,enabled=false)
	public void test_makeApplyData(String username) {
		Reporter.log("为信用卡代理分润创造前提数据");
		Reporter.log("1）会员登录"+username);
		String platform = "h5";
		
		Reporter.log("[会员1操作]-----");
		Reporter.log("1）登录商城："+username);
		JSONObject loginResult = login.login(username, "123456li", platform);
		assertEquals(loginResult.getString("code"), "10000","登录校验"+loginResult);
		JSONObject userdata = loginResult.getJSONObject("data");
		
		String idCard = memberData.getIdCardNotExit();
		 Reporter.log("2）提交身份证信息,身份证号："+idCard);
		 JSONObject addIdCardResult = authRealName.idCardEdit(userdata,idCard,platform);
		 assertEquals(addIdCardResult.getString("code"), "10000","提交身份证信息校验"+addIdCardResult);
		 assertEquals(memberData.get_fieldByAccount(username,"real_name_audit_status"), "3","实名审核状态校验（审核中）");
		
		String agentPhone = "17800194624";
		Reporter.log("3）申请信用卡，代理账号："+agentPhone);
		JSONObject applyResult = applyCreditCard.apply(userdata, agentPhone, platform);
		assertEquals(applyResult.getString("code"), "10000","插入申请信用卡校验"+applyResult);
	}
	
	@Test(dataProvider="noPCPlatform",dataProviderClass=UserDataProvider.class)
	public void test_applyCreditCard1(String platform) {
		Reporter.log("实名审核中会员正常申请信用卡：填写信用卡代理商");
		
		Reporter.log("[会员操作]-----");
		String username = memberData.getAccount_realNameAuditStatus("1");// 获取未实名认证的账号
		Reporter.log("1）会员登录"+username);
		JSONObject loginResult = login.login(username, "123456li", platform);
		assertEquals(loginResult.getString("code"), "10000","登录校验"+loginResult);
		JSONObject userdata = loginResult.getJSONObject("data");
		
		Reporter.log("2）提交实名认证审核");
		JSONObject addIdCardResult1 = authRealName.idCardEdit(userdata, baseData.getIdCard(), platform);
		assertEquals(addIdCardResult1.getString("code"), "10000", "提交身份证信息校验" + addIdCardResult1);
		assertEquals(memberData.get_fieldByAccount(username, "real_name_audit_status"), "3", "实名审核状态校验（审核中）");
		
		String agentPhone = agentData.getLastCardAgent("1");
		Reporter.log("3）申请信用卡，代理账号："+agentPhone);
		JSONObject applyResult = applyCreditCard.apply(userdata, agentPhone, platform);
		assertEquals(applyResult.getString("code"), "10000","插入申请信用卡校验"+applyResult);
		assertEquals(applyResult.getJSONObject("data").getString("authStatus"), "2");//1：实名未验证 2：实名已验证 3: 信用卡代理不存
		
		Reporter.log("[运营中心操作]-----");
		JSONObject sysLoginResult = login.sysLogin("helen", "123456li");
		assertEquals(sysLoginResult.getString("code"), "10000","运营中心登录校验"+sysLoginResult);
		JSONObject sysUserdata = sysLoginResult.getJSONObject("data");
		
		Reporter.log("1）查看信用卡申请列表");
		JSONObject creditCardApplyListResult = applyCreditCard.creditCardApplyList(sysUserdata,username);
		assertEquals(creditCardApplyListResult.getString("code"), "10000","查看信用卡申请列表校验"+creditCardApplyListResult);
		assertEquals(creditCardApplyListResult.getJSONObject("data").getJSONArray("list").getJSONObject(0).getString("agentNumber"), agentPhone,"校验代理商账号是否一至");
		
		Reporter.log("2）信用卡列表导出");
		JSONObject exportCreditCardApplyListResult = applyCreditCard.exportCreditCardApplyList(sysUserdata);
		assertEquals(exportCreditCardApplyListResult.getString("code"), "10000","信用卡列表导出校验"+exportCreditCardApplyListResult);
	}
	
	@Test(dataProvider="noPCPlatform",dataProviderClass=UserDataProvider.class)
	public void test_applyCreditCard2(String platform) {
		Reporter.log("实名认证通过的会员，正常申请信用卡：填写信用卡代理商");
		
		Reporter.log("[会员操作]-----");
		String username = memberData.getAccount_realNameAuditStatus("1");// 获取未实名认证的账号
		Reporter.log("1）会员登录"+username);
		JSONObject loginResult = login.login(username, "123456li", platform);
		assertEquals(loginResult.getString("code"), "10000","登录校验"+loginResult);
		JSONObject userdata = loginResult.getJSONObject("data");
		
		Reporter.log("2）提交实名认证审核");
		JSONObject addIdCardResult1 = authRealName.idCardEdit(userdata, baseData.getIdCard(), platform);
		assertEquals(addIdCardResult1.getString("code"), "10000", "提交身份证信息校验" + addIdCardResult1);
		assertEquals(memberData.get_fieldByAccount(username, "real_name_audit_status"), "3", "实名审核状态校验（审核中）");
		
		Reporter.log("[运营中心操作]-----");
		JSONObject sysLoginResult = login.sysLogin("helen", "123456li");
		assertEquals(sysLoginResult.getString("code"), "10000","运营中心登录校验"+sysLoginResult);
		JSONObject sysUserdata = sysLoginResult.getJSONObject("data");
		
		Reporter.log("1）实名认证审核：审核通过");
		JSONObject updateRealNameAuditStatusResult = authRealName.updateRealNameAuditStatus(sysUserdata,userdata.getString("uid"), 2);
		assertEquals(updateRealNameAuditStatusResult.getString("code"), "10000","实名认证审核（通过）校验" + updateRealNameAuditStatusResult);
		assertEquals(memberData.get_fieldByAccount(username, "real_name_audit_status"), "2", "实名审核状态校验（审核通过）");
		
		Reporter.log("[会员操作]-----");
		String agentPhone = agentData.getLastCardAgent("1");
		Reporter.log("1）申请信用卡，代理账号："+agentPhone);
		JSONObject applyResult = applyCreditCard.apply(userdata, agentPhone, platform);
		assertEquals(applyResult.getString("code"), "10000","插入申请信用卡校验"+applyResult);
		assertEquals(applyResult.getJSONObject("data").getString("authStatus"), "2");//1：实名未验证 2：实名已验证 3: 信用卡代理不存
		
		Reporter.log("[运营中心操作]-----");
		Reporter.log("1）查看信用卡申请列表");
		JSONObject creditCardApplyListResult = applyCreditCard.creditCardApplyList(sysUserdata,username);
		assertEquals(creditCardApplyListResult.getString("code"), "10000","查看信用卡申请列表校验"+creditCardApplyListResult);
		assertEquals(creditCardApplyListResult.getJSONObject("data").getJSONArray("list").getJSONObject(0).getString("agentNumber"), agentPhone,"校验代理商账号是否一至");
	}
	
	@Test(dataProvider="noPCPlatform",dataProviderClass=UserDataProvider.class)
	public void test_applyCreditCard3(String platform) {
		Reporter.log("实名认证审核中的会员，正常申请信用卡：不填写信用卡代理商");
		
		Reporter.log("[会员操作]-----");
		String username = memberData.getAccount_realNameAuditStatus("1");// 获取未实名认证的账号
		Reporter.log("1）会员登录"+username);
		JSONObject loginResult = login.login(username, "123456li", platform);
		assertEquals(loginResult.getString("code"), "10000","登录校验"+loginResult);
		JSONObject userdata = loginResult.getJSONObject("data");
		
		Reporter.log("2）提交实名认证审核");
		JSONObject addIdCardResult1 = authRealName.idCardEdit(userdata, baseData.getIdCard(), platform);
		assertEquals(addIdCardResult1.getString("code"), "10000", "提交身份证信息校验" + addIdCardResult1);
		assertEquals(memberData.get_fieldByAccount(username, "real_name_audit_status"), "3", "实名审核状态校验（审核中）");

		Reporter.log("3）申请信用卡，代理账号为空");
		JSONObject applyResult = applyCreditCard.apply(userdata, "", platform);
		assertEquals(applyResult.getString("code"), "10000","插入申请信用卡校验"+applyResult);
		
		Reporter.log("[运营中心操作]-----");
		JSONObject sysLoginResult = login.sysLogin("helen", "123456li");
		assertEquals(sysLoginResult.getString("code"), "10000","运营中心登录校验"+sysLoginResult);
		JSONObject sysUserdata = sysLoginResult.getJSONObject("data");
		
		Reporter.log("1）查看信用卡申请列表");
		JSONObject creditCardApplyListResult = applyCreditCard.creditCardApplyList(sysUserdata,username);
		assertEquals(creditCardApplyListResult.getString("code"), "10000","查看信用卡申请列表校验"+creditCardApplyListResult);
		assertEquals(creditCardApplyListResult.getJSONObject("data").getString("listCount"), "0","校验代理商账号为空时不生成申请记录");
	}
	
	@Test(dataProvider="noPCPlatform",dataProviderClass=UserDataProvider.class)
	public void test_applyCreditCard4(String platform) {
		Reporter.log("未提交过实名认证信息的会员，正常申请信用卡：填写信用卡代理商");
		
		Reporter.log("[会员操作]-----");
		String username = memberData.getAccount_realNameAuditStatus("1");// 获取已实名认证的账号
		Reporter.log("1）会员登录"+username);
		JSONObject loginResult = login.login(username, "123456li", platform);
		assertEquals(loginResult.getString("code"), "10000","登录校验"+loginResult);
		JSONObject userdata = loginResult.getJSONObject("data");
		
		String agentPhone = agentData.getLastCardAgent("1");
		Reporter.log("2）申请信用卡，代理账号："+agentPhone);
		JSONObject applyResult = applyCreditCard.apply(userdata, agentPhone, platform);
		assertEquals(applyResult.getString("code"), "10000","插入申请信用卡校验"+applyResult);
		assertEquals(applyResult.getJSONObject("data").getString("authStatus"), "1");//1：实名未验证 2：实名已验证 
		
		Reporter.log("[运营中心操作]-----");
		JSONObject sysLoginResult = login.sysLogin("helen", "123456li");
		assertEquals(sysLoginResult.getString("code"), "10000","运营中心登录校验"+sysLoginResult);
		JSONObject sysUserdata = sysLoginResult.getJSONObject("data");
		
		Reporter.log("1）查看信用卡申请列表");
		JSONObject creditCardApplyListResult = applyCreditCard.creditCardApplyList(sysUserdata,username);
		assertEquals(creditCardApplyListResult.getString("code"), "10000","查看信用卡申请列表校验"+creditCardApplyListResult);
		assertEquals(creditCardApplyListResult.getJSONObject("data").getString("listCount"), "0","校验未实名认证的会员不能申请信用卡，所以没有产生申请记录");
	}

	@Test(dataProvider="applyCreditCard",dataProviderClass=UserDataProvider.class)
	public void test_applyCreditCard5(String platform,String card_auditor_status,String msg) {
		Reporter.log("申请信用卡,信用卡代理商身份为："+msg);
		
		Reporter.log("[会员操作]-----");
		String username = memberData.getAccount_realNameAuditStatus("2");// 获取已实名认证的账号
		Reporter.log("1）会员登录"+username);
		JSONObject loginResult = login.login(username, "123456li", platform);
		assertEquals(loginResult.getString("code"), "10000","登录校验"+loginResult);
		JSONObject userdata = loginResult.getJSONObject("data");
		
		String agentPhone = agentData.getLastCardAgent(card_auditor_status);
		Reporter.log("2）申请信用卡，代理账号："+agentPhone);
		JSONObject applyResult = applyCreditCard.apply(userdata, agentPhone, platform);
		assertEquals(applyResult.getString("code"), "99999","插入申请信用卡校验"+applyResult);
		assertEquals(applyResult.getString("msg"), "代理手机输入有误");
	}
}
