/**
 * @author helen
 * @date 2018年11月26日
 */
package testcase;

import static org.testng.Assert.assertEquals;

import org.testng.Reporter;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONObject;
import data.MemberData;
import dataProvider.UserDataProvider;
import user.Login;
import user.MemberManager;

/**
 * @Description:
 */
public class LoginTest {
	MemberData memberData = new MemberData();
	MemberManager memberManager = new MemberManager();
	Login login = new Login();
	
	@Test(dataProvider="platform",dataProviderClass=UserDataProvider.class)
	public void test_login(String platform) {
		Reporter.log("正确用户名和密码");
		String username = memberData.get_account("phone");//获取密码为123456li的用户名
		JSONObject result = login.login(username, "123456li",platform);
		assertEquals(String.valueOf(result.get("code")), "10000");
	}
	
	@Test(dataProvider="platform",dataProviderClass=UserDataProvider.class)
	public void test_login2(String platform) {
		Reporter.log("不存在的用户名");
		String username = memberData.get_unexitUsername("phone");
		JSONObject result = login.login(username, "123456li",platform);
		assertEquals(String.valueOf(result.get("code")), "60001");
		assertEquals(result.get("msg"), "该账号不存在");
	}
	
	@Test
	public void test_login3() {
		Reporter.log("错误密码登录：放在找回密码业务中校验");
	}
	
	@Test(dataProvider="platform",dataProviderClass=UserDataProvider.class)
	public void test_login4(String platform) {
		Reporter.log("账号被禁用");
		
		String username = memberData.get_accountByStatus("0");
		Reporter.log("1)找到正常的会员账号:"+username);
		
		Reporter.log("2)从运营后台禁用该会员");
		JSONObject sysLoginResult = login.sysLogin("helen", "123456li");
		assertEquals(sysLoginResult.getString("code"), "10000","校验运营后台登录"+sysLoginResult);
		JSONObject sysUserdata = sysLoginResult.getJSONObject("data");
		String userId = memberData.get_fieldByAccount(username, "user_id");
		JSONObject disableResult = memberManager.disable(sysUserdata, userId);
		assertEquals(disableResult.getString("code"), "10000","校验运营中心禁用会员是否成功"+disableResult);
		
		Reporter.log("3)用已禁用的用户登录商城，应当提示会员账号状态异常");
		JSONObject result = login.login(username, "123456li",platform);
		assertEquals(String.valueOf(result.get("code")), "20012");
		assertEquals(result.get("msg"), "会员账号状态异常");
	}
	
	@Test(dataProvider="platform",dataProviderClass=UserDataProvider.class)
	public void test_login5(String platform) {
		Reporter.log("账号被删除");
		
		String username = memberData.get_accountByStatus("0");
		Reporter.log("1)找到正常的会员账号:"+username);
		
		Reporter.log("2)从运营后台删除该会员");
		JSONObject sysLoginResult = login.sysLogin("helen", "123456li");
		assertEquals(sysLoginResult.getString("code"), "10000","校验运营后台登录"+sysLoginResult);
		JSONObject sysUserdata = sysLoginResult.getJSONObject("data");
		String userId = memberData.get_fieldByAccount(username, "user_id");
		JSONObject deleteResult = memberManager.delete(sysUserdata, userId);
		assertEquals(deleteResult.getString("code"), "10000","校验运营中心删除会员是否成功"+deleteResult);
		
		Reporter.log("3)用已删除的用户登录商城，应当提示会员账号状态异常");
		JSONObject result = login.login(username, "123456li",platform);
		assertEquals(result.getString("code"), "20012","账号被删除，应当不可以登录"+result);
		assertEquals(result.get("msg"), "会员账号状态异常");
	}

}
