/**
 * @author helen
 * @date 2018年12月3日
 */
package testcase;

import static org.testng.Assert.assertEquals;

import org.testng.Reporter;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONObject;

import data.MemberData;
import dataProvider.UserDataProvider;
import user.Login;
import user.Registry;

/**
 * @Description:找回密码业务测试
 */
public class FindPasswordTest {
	MemberData memberData = new MemberData();
	Registry registry=new Registry();
	Login login=new Login();

	@Test(dataProvider = "platform", dataProviderClass = UserDataProvider.class)
	public void test_findPassword_phone(String platform) {
		Reporter.log("(手机账号)忘记密码－用新密码登录");
		
		String account = memberData.get_account("phone");
		Reporter.log("1)获取一个正常在用并且是手机账号的会员："+account);
		JSONObject findPasswordResult = registry.findPassword("member", account, "12345678li", platform);
		assertEquals(findPasswordResult.getString("code"), "10000","修改密码结果校验"+findPasswordResult);
		
		Reporter.log("2)用新密码登录商城，应当登录成功");
		JSONObject loginResult = login.login(account, "12345678li", platform);
		assertEquals(loginResult.getString("code"), "10000","新密码登录校验校验"+loginResult);
		
		Reporter.log("3)用旧密码登录商城，应当提示密码错误");
		JSONObject loginWrongResult = login.login(account, "12345li",platform);
		assertEquals(String.valueOf(loginWrongResult.get("code")), "60003");
		assertEquals(loginWrongResult.get("msg"), "用户名或密码错误");
	}
	
	@Test(dataProvider = "platform", dataProviderClass = UserDataProvider.class)
	public void test_findPassword_email(String platform) {
		Reporter.log("(邮箱账号)忘记密码－用新密码登录");
		
		String account = memberData.get_account("email");
		Reporter.log("1)获取一个正常在用并且是手机账号的会员："+account);
		JSONObject findPasswordResult = registry.findPassword("member", account, "12345678li", platform);
		assertEquals(findPasswordResult.getString("code"), "10000","修改密码结果校验"+findPasswordResult);
		
		Reporter.log("2)用新密码登录商城，应当登录成功");
		JSONObject loginResult = login.login(account, "12345678li", platform);
		assertEquals(loginResult.getString("code"), "10000","新密码登录校验校验"+loginResult);
	}
}
