/**
 * @author helen
 * @date 2018年10月18日
 */
package user;
import common.BaseData;
import common.MyAPI;
import data.MemberData;

import static org.testng.Assert.assertEquals;

import com.alibaba.fastjson.JSONObject;

/**
 * @Description:登录相关接口，包含前台pc\android\ios\h5的登录，和运营中心、商家中心、代理商中心的登录接口
 */
public class Login {
	BaseData baseData = new BaseData();
	MemberData memberData = new MemberData();
	MyAPI myAPI = new MyAPI();
	VerificationCode verificationCode = new VerificationCode();
	
	/*登录
	 * @platform  登录的平台：android、ios、pc、h5
	 * */
	public JSONObject login(String username,String password,String platform) {
		String passwords = memberData.get_password(username);
		
		JSONObject params = new JSONObject();
		params.put("account", username);
		params.put("password", passwords);
		//params.put("password", baseData.getMD5(password));
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")) {
			params.put("deviceId", "PRA-AL00");
			params.put("deviceType", "android");
			params.put("regid", "160a3797c831eb081cf");//极光推送ID，不为空执可
			result = myAPI.apiPost("/userCenter/member/v1/login",params,"android");
		}
		else if (platform.equals("ios")) {
			params.put("deviceId", "1BFD7A8B-4746-4B57-B33B-CCA2AED7780F");
			params.put("deviceType", "ios");
			params.put("regid", "121c83f7600e4ce8200");//极光推送ID，不为空执可
			result = myAPI.apiPost("/userCenter/member/v1/login",params,"ios");
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost("/login/userCenter/member", params);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post("/api/userCenter/login", params);
		}
		else {
			System.out.println(platform+"平台不存在");
		}
		
		//System.out.println(result);
		return result;
	}
	
	/*运营中心登录*/
	public JSONObject sysLogin(String account,String password) {
		JSONObject params = new JSONObject();
		params.put("account", account);
		params.put("password", baseData.getMD5(password));
		
		JSONObject result = myAPI.sysPost("/userCenter/user/login", params);
		if (result.getString("code").equals("60050")) {
			VerificationCode verificationCode = new VerificationCode();
			JSONObject sendCodeResult = verificationCode.sendCode(result.getString("data"), "", 7, "sys");
			assertEquals(sendCodeResult.getString("code"), "10000","发送验证码校验"+sendCodeResult);
			String Vcode = memberData.getVerificationCode(result.getString("data"), "phone");
			params.put("mobile", result.getString("data"));
			params.put("mobileCode", Vcode);
			result = myAPI.sysPost("/userCenter/user/login", params);
		}
		//System.out.println(result);
		return result;
	}
	
	/*代理中心登录*/
	public JSONObject agentLogin(String account,String password) {
		//获取验证码
		JSONObject imageCodeReslut = verificationCode.getImageVerificationCode("agent");
		assertEquals(imageCodeReslut.getString("code"), "10000","代理中心获取图片验证码"+imageCodeReslut);
		
		String passwords = memberData.get_password(account);
		JSONObject params = new JSONObject();
		params.put("account", account);
		params.put("password", passwords);
		params.put("imageId", imageCodeReslut.getJSONObject("data").getString("imageId"));//验证码图片ID
		params.put("verificationCode", imageCodeReslut.getJSONObject("data").getString("verifyCode"));
		
		JSONObject result = new JSONObject();
		result = myAPI.agentPost("/userCenter/agentBack/agentLogin/login", params);
		return result;
	}
	
	/*商家中心登录*/
	public JSONObject sellerLogin(String account,String password) {
		//获取图片验证码
		JSONObject imageCodeReslut = verificationCode.getImageVerificationCode("seller");
		assertEquals(imageCodeReslut.getString("code"), "10000","校验是否能获得验证码"+imageCodeReslut);
		
		String passwords = memberData.get_password(account);
		JSONObject params = new JSONObject();
		params.put("account", account);//商家账号
		params.put("password", passwords);
		params.put("imageId", imageCodeReslut.getJSONObject("data").getString("imageId"));// 验证码图片id ,
		params.put("verificationCode", imageCodeReslut.getJSONObject("data").getString("verifyCode"));//验证码
		
		return myAPI.sellerPost("/userCenter/supplierBack/supplierLogin/login", params);
	}
	
	/*康养商家中心登录*/
	public JSONObject KYSellerLogin(String account,String password) {
		//获取图片验证码
		JSONObject imageCodeReslut = verificationCode.getImageVerificationCode("seller");
		assertEquals(imageCodeReslut.getString("code"), "10000","校验是否能获得验证码"+imageCodeReslut);
		
		String passwords = memberData.get_password(account);
		JSONObject params = new JSONObject();
		params.put("account", account);//商家账号
		params.put("password", passwords);
		params.put("imageId", imageCodeReslut.getJSONObject("data").getString("imageId"));// 验证码图片id ,
		params.put("verificationCode", imageCodeReslut.getJSONObject("data").getString("verifyCode"));//验证码
		
		return myAPI.KYSellerPost("/userCenter/supplierBack/supplierLogin/login", params);
	}
}
