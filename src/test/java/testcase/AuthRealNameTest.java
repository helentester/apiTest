/**
 * @author helen
 * @date 2018年12月4日
 */
package testcase;

import static org.testng.Assert.assertEquals;

import org.testng.Reporter;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import data.MemberData;
import dataProvider.UserDataProvider;
import user.AuthRealName;
import user.Login;
import user.UserOperateLog;

/**
 * @Description:实名认证测试
 */
public class AuthRealNameTest {
	MemberData memberData = new MemberData();
	AuthRealName authRealName = new AuthRealName();
	Login login=new Login();
	UserOperateLog userOperateLog = new UserOperateLog();
	
	
	@Test(dataProvider="noPCPlatform",dataProviderClass=UserDataProvider.class)
	public void test_AuthRealName_idcard(String platform) {
		Reporter.log("实名认证业务：主要校验身份证号唯一性");
		String idCard = memberData.getIdCardNotExit();
		
		String username1 = memberData.getAccount_realNameAuditStatus("1");// 获取未实名认证的账号
		Reporter.log("[会员1操作]-----");
		Reporter.log("1）登录商城：" + username1);
		JSONObject loginResult1 = login.login(username1, "123456li", platform);
		assertEquals(loginResult1.getString("code"), "10000", "登录校验" + loginResult1);
		JSONObject userdata1 = loginResult1.getJSONObject("data");

		Reporter.log("2）提交身份证信息,身份证号：" + idCard);
		JSONObject addIdCardResult1 = authRealName.idCardEdit(userdata1, idCard, platform);
		assertEquals(addIdCardResult1.getString("code"), "10000", "提交身份证信息校验" + addIdCardResult1);
		assertEquals(memberData.get_fieldByAccount(username1, "real_name_audit_status"), "3", "实名审核状态校验（审核中）");
		 
		String username2 = memberData.getAccount_realNameAuditStatus("1");// 获取未实名认证的账号
		Reporter.log("[会员2操作]-----");
		Reporter.log("1）登录商城：" + username2);
		JSONObject loginResult2 = login.login(username2, "123456li", platform);
		assertEquals(loginResult2.getString("code"), "10000", "登录校验" + loginResult2);
		JSONObject userdata2 = loginResult2.getJSONObject("data");

		Reporter.log("2）提交身份证信息（身份证在审核中，已占用）,身份证号：" + idCard);
		JSONObject addIdCardResult2 = authRealName.idCardEdit(userdata2, idCard, platform);
		assertEquals(addIdCardResult2.getString("code"), "99999", "提交身份证信息校验" + addIdCardResult2);
		assertEquals(addIdCardResult2.getString("msg"), "此身份证号码已经被占用");

		Reporter.log("[运营中心操作]------");
		JSONObject sysLoginResult = login.sysLogin("helen", "123456li");
		assertEquals(sysLoginResult.getString("code"), "10000", "运营中心登录校验" + sysLoginResult);
		JSONObject sysUserdata = sysLoginResult.getJSONObject("data");

		Reporter.log("1）运营中心实名认证审核：会员1审核不通过");
		JSONObject updateRealNameAuditStatusResult_42 = authRealName.updateRealNameAuditStatus(sysUserdata,
				userdata1.getString("uid"), 4);
		assertEquals(updateRealNameAuditStatusResult_42.getString("code"), "10000",
				"实名认证审核（不通过）校验" + updateRealNameAuditStatusResult_42);
		assertEquals(memberData.get_fieldByAccount(username1, "real_name_audit_status"), "4", "实名审核状态校验（审核不通过）");
		 
		String username3 = memberData.getAccount_realNameAuditStatus("1");// 获取未实名认证的账号
		Reporter.log("[会员3操作]-----");
		Reporter.log("1）登录商城：" + username3);
		JSONObject loginResult3 = login.login(username3, "123456li", platform);
		assertEquals(loginResult3.getString("code"), "10000", "登录校验" + loginResult3);
		JSONObject userdata3 = loginResult3.getJSONObject("data");

		Reporter.log("2）提交身份证信息,身份证号：" + idCard);
		JSONObject addIdCardResult3 = authRealName.idCardEdit(userdata3, idCard, platform);
		assertEquals(addIdCardResult3.getString("code"), "10000", "提交身份证信息校验" + addIdCardResult3);
		assertEquals(memberData.get_fieldByAccount(username3, "real_name_audit_status"), "3", "实名审核状态校验（审核中）");

		Reporter.log("[运营中心操作]------");
		Reporter.log("1）实名认证审核：会员3审核通过");
		JSONObject updateRealNameAuditStatusResult_23 = authRealName.updateRealNameAuditStatus(sysUserdata,userdata3.getString("uid"), 2);
		assertEquals(updateRealNameAuditStatusResult_23.getString("code"), "10000","实名认证审核（通过）校验" + updateRealNameAuditStatusResult_23);
		assertEquals(memberData.get_fieldByAccount(username3, "real_name_audit_status"), "2", "实名审核状态校验（审核通过）");
		
		String username4 = memberData.getAccount_realNameAuditStatus("1");// 获取未实名认证的账号
		Reporter.log("[会员4操作]-----");
		Reporter.log("1）登录商城：" + username4);
		JSONObject loginResult4 = login.login(username4, "123456li", platform);
		assertEquals(loginResult4.getString("code"), "10000", "登录校验" + loginResult4);
		JSONObject userdata4 = loginResult4.getJSONObject("data");

		Reporter.log("2）提交身份证信息,身份证号：" + idCard);
		JSONObject addIdCardResult4 = authRealName.idCardEdit(userdata4, idCard, platform);
		assertEquals(addIdCardResult4.getString("code"), "99999", "提交身份证信息校验(身份证号不唯一)" + addIdCardResult4);
		assertEquals(memberData.get_fieldByAccount(username4, "real_name_audit_status"), "1", "实名审核状态校验（未提交）");
		 
		Reporter.log("[运营中心操作]-----");
		 Reporter.log("1）实名认证审核：撤消会员3（"+username3+"）的实名认证审核");
		 JSONObject updateRealNameAuditStatusResult_5 = authRealName.updateRealNameAuditStatus(sysUserdata,userdata3.getString("uid"), 5);
		 assertEquals(updateRealNameAuditStatusResult_5.getString("code"), "10000","实名认证审核（撤消）校验"+updateRealNameAuditStatusResult_5);
		 assertEquals(memberData.get_fieldByAccount(username3,"real_name_audit_status"), "5","实名审核状态校验（撤消审核）");
		 
		String username5 = memberData.getAccount_realNameAuditStatus("1");// 获取未实名认证的账号
		Reporter.log("[会员5操作]-----");
		Reporter.log("1）登录商城：" + username5);
		JSONObject loginResult5 = login.login(username5, "123456li", platform);
		assertEquals(loginResult5.getString("code"), "10000", "登录校验" + loginResult5);
		JSONObject userdata5 = loginResult5.getJSONObject("data");

		Reporter.log("2）提交身份证信息,身份证号：" + idCard);
		JSONObject addIdCardResult5 = authRealName.idCardEdit(userdata5, idCard, platform);
		assertEquals(addIdCardResult5.getString("code"), "10000", "提交身份证信息校验" + addIdCardResult5);
		assertEquals(memberData.get_fieldByAccount(username5, "real_name_audit_status"), "3", "实名审核状态校验（审核中）");

		Reporter.log("[运营中心操作]-----");
		Reporter.log("1）实名认证审核：会员5审核通过");
		JSONObject updateRealNameAuditStatusResult_25 = authRealName.updateRealNameAuditStatus(sysUserdata,
				userdata5.getString("uid"), 2);
		assertEquals(updateRealNameAuditStatusResult_25.getString("code"), "10000",
				"实名认证审核（通过）校验" + updateRealNameAuditStatusResult_25);
		assertEquals(memberData.get_fieldByAccount(username5, "real_name_audit_status"), "2", "实名审核状态校验（审核通过）");
		
	}
	
	@Test(dataProvider="noPCPlatform",dataProviderClass=UserDataProvider.class)
	public void test_AuthRealName(String platform) {
		Reporter.log("实名认证业务：同一个会员的业务流程");
		String username = memberData.getAccount_realNameAuditStatus("1");//获取未实名认证的账号
		
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
		 
		 Reporter.log("[运营中心操作]-----");
		 JSONObject sysLoginResult = login.sysLogin("helen", "123456li");
		 assertEquals(sysLoginResult.getString("code"), "10000","运营中心登录校验"+sysLoginResult);
		 JSONObject sysUserdata = sysLoginResult.getJSONObject("data");
		 
		Reporter.log("1）查看实名认证列表");
		 JSONObject realNameAuthListResult = authRealName.realNameAuthList(sysUserdata);
		 assertEquals(realNameAuthListResult.getString("code"), "10000","实名认证列表校验"+realNameAuthListResult);
		 
		 Reporter.log("2）查看实名认证详情");
		 JSONObject realNameDetailResult = authRealName.realNameDetail(sysUserdata, userdata.getString("uid"));
		 assertEquals(realNameDetailResult.getString("code"), "10000","实名认证详情校验"+realNameDetailResult);
		 
		 Reporter.log("3）实名认证审核：审核不通过");
		 JSONObject updateRealNameAuditStatusResult_4 = authRealName.updateRealNameAuditStatus(sysUserdata,userdata.getString("uid"),4);
		 assertEquals(updateRealNameAuditStatusResult_4.getString("code"), "10000","实名认证审核（不通过）校验"+updateRealNameAuditStatusResult_4);
		 assertEquals(memberData.get_fieldByAccount(username,"real_name_audit_status"), "4","实名审核状态校验（审核不通过）");
		 
		 Reporter.log("[会员操作]-----");
		 Reporter.log("1）修改实名认证信息");
		 JSONObject idCardEditResult = authRealName.idCardEdit(userdata,idCard, platform);
		 assertEquals(idCardEditResult.getString("code"), "10000","修改实名认证信息校验"+idCardEditResult);
		 assertEquals(memberData.get_fieldByAccount(username,"real_name_audit_status"), "3","实名审核状态校验（审核中）");
		 
		 Reporter.log("[运营中心操作]-----");
		 Reporter.log("1）实名认证审核：审核通过");
		 JSONObject updateRealNameAuditStatusResult_2 = authRealName.updateRealNameAuditStatus(sysUserdata,userdata.getString("uid"), 2);
		 assertEquals(updateRealNameAuditStatusResult_2.getString("code"), "10000","实名认证审核（不通过）校验"+updateRealNameAuditStatusResult_2);
		 assertEquals(memberData.get_fieldByAccount(username,"real_name_audit_status"), "2","实名审核状态校验（审核通过）");
		 
		 Reporter.log("2）实名认证审核：撤消");
		 JSONObject updateRealNameAuditStatusResult_5 = authRealName.updateRealNameAuditStatus(sysUserdata,userdata.getString("uid"), 5);
		 assertEquals(updateRealNameAuditStatusResult_5.getString("code"), "10000","实名认证审核（不通过）校验"+updateRealNameAuditStatusResult_5);
		 assertEquals(memberData.get_fieldByAccount(username,"real_name_audit_status"), "5","实名审核状态校验（撤消审核）");
		 
		 Reporter.log("[会员操作]-----");
		 Reporter.log("1）修改实名认证信息");
		 JSONObject idCardEditResult2 = authRealName.idCardEdit(userdata,idCard, platform);
		 assertEquals(idCardEditResult2.getString("code"), "10000","修改实名认证信息校验"+idCardEditResult2);
		 assertEquals(memberData.get_fieldByAccount(username,"real_name_audit_status"), "3","实名审核状态校验（审核中）");
		 
		 Reporter.log("[运营中心操作]-----");
		 Reporter.log("1）实名认证审核：审核通过");
		 JSONObject updateRealNameAuditStatusResult_22 = authRealName.updateRealNameAuditStatus(sysUserdata,userdata.getString("uid"), 2);
		 assertEquals(updateRealNameAuditStatusResult_22.getString("code"), "10000","实名认证审核（不通过）校验"+updateRealNameAuditStatusResult_22);
		 assertEquals(memberData.get_fieldByAccount(username,"real_name_audit_status"), "2","实名审核状态校验（审核通过）");
		 
		 Reporter.log("2）查看实名认证审核日志");
		 JSONObject operateLogResult = userOperateLog.operateLog(sysUserdata, userdata.getString("uid"), 3);
		 assertEquals(operateLogResult.getString("code"), "10000","实名认证审核日志查看校验"+operateLogResult);
		 JSONArray operateLogList = operateLogResult.getJSONObject("data").getJSONArray("list");
		 assertEquals(operateLogList.getJSONObject(0).getString("operateContent"), "审核通过","操作内容校验");
		 assertEquals(operateLogList.getJSONObject(1).getString("operateContent"), "撤销","操作内容校验");
		 assertEquals(operateLogList.getJSONObject(2).getString("operateContent"), "审核通过","操作内容校验");
		 assertEquals(operateLogList.getJSONObject(3).getString("operateContent"), "审核不通过","操作内容校验");
		 
		 Reporter.log("[会员操作]-----");
		 Reporter.log("1）查看自已的实名认证详情");
		 JSONObject idcardResult = authRealName.idcard(userdata, platform);
		 assertEquals(idcardResult.getString("code"), "10000","实名认证详情校验"+idcardResult);
	}

}
