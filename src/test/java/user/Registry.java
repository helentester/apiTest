/**
 * @author helen
 * @date 2018年10月18日
 */
package user;
import common.BaseData;
import common.MyAPI;
import data.MemberData;

import com.alibaba.fastjson.JSONObject;

/**
 * @Description:会员注册模块 : Registry Controller
 */
public class Registry {
	BaseData baseData = new BaseData();
	MemberData memberData = new MemberData();
	MyAPI myAPI = new MyAPI();
	VerificationCode verificationCode = new VerificationCode();

	/* 会员绑定邀请人的邀请码 */
	public JSONObject recommendCodeBind(JSONObject userdata, String inviter, String platform) {
		JSONObject params = new JSONObject();
		params.put("userId", userdata.getString("userId"));
		params.put("recommendCode", inviter);// 邀请人
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/userCenter/member/v1/recommendCode/bind", params, platform);
		} else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/api/userCenter/member/recommendCode/bind", params);
		} else {
			System.out.println(platform + "平台不存在绑定邀请人接口");
		}
		return result;
	}

	/* 获取邀请奖励信息 */
	public JSONObject getMemberCode(JSONObject userdata, String platform) {
		JSONObject params = new JSONObject();
		params.put("userId", userdata.getString("userId"));
		JSONObject result = new JSONObject();
		if (platform.equals("android") || platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/userCenter/member/v1/getMemberCode", params, platform);
		} else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/api/userCenter/member/getMemberCode", params);
		} else {
			System.out.println(platform + "平台，无获取邀请奖励信息接口");
		}

		// System.out.println(result);
		return result;
	}

	/*
	 * 会员、商家注册
	 * 
	 * @registryType： email phone
	 */
	public JSONObject register(String telephoneCodeId, String account,String verifyCode, String inviter, String platform,
			String registryType) {
		JSONObject params = new JSONObject();
		params.put("account", account);// 注册账号
		params.put("code", verifyCode);// 验证码
		params.put("password", baseData.getMD5("123456li"));// 密码
		params.put("invite", inviter);//邀请人
		if (registryType.equals("phone")) {
			params.put("telephoneCodeId", telephoneCodeId);// 注册地区区号
		}

		JSONObject result = new JSONObject();
		if (platform.equals("android")) {
			params.put("devicesId", "PRA-AL00");
			params.put("devicesType", "android");
			params.put("regid", "160a3797c831eb081cf");// 极光推送ID，不为空执可
			result = myAPI.apiPost("/userCenter/member/v2/register", params, "android");
		} else if (platform.equals("ios")) {
			params.put("deviceId", "1BFD7A8B-4746-4B57-B33B-CCA2AED7780F");
			params.put("deviceType", "ios");
			params.put("regid", "121c83f7600e4ce8200");// 极光推送ID，不为空执可
			result = myAPI.apiPost("/userCenter/member/v2/register", params, "ios");
		} else if (platform.equals("h5")) {
			result = myAPI.h5Post("/api/userCenter/register", params);
		} else if (platform.equals("pc")) {
			result = myAPI.PCPost("/userCenter/user/common/register", params);
		} 
		else if (platform.equals("seller")) {
			result = myAPI.sellerPost("/userCenter/user/common/register", params);
		}
		else if (platform.equals("h5share")) {// 分享注册页面
			params.put("telephoneCodeId", telephoneCodeId);
			params.put("mobile", account);// 注册账号
			params.put("devicesId", String.valueOf(System.currentTimeMillis() / 1000));
			params.put("devicesType", "h5");
			params.put("sign", "sign");
			params.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
			params.put("requestId", "af92573f-665b-4b76-be44-e664927d71a9");
			params.put("type", "2");
			params.put("regid", "111");// 极光推送ID，不为空执可
			result = myAPI.apiPost("/userCenter/member/v1/register", params, "h5");
		} else {
			System.out.println(platform + "平台无注册接口");
		}

		// 设置等待福豆时间
		try {
			if (result.getString("code").equals("10000")) {
				Thread.sleep(3000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/*
	 * 验证号码、邮箱是否已注册
	 * 
	 * @platform: android ios h5 pc
	 */
	public JSONObject checkAccount(String account, String platform) {
		JSONObject params = new JSONObject();
		params.put("mobile", account);

		JSONObject result = new JSONObject();
		if (platform.equals("android") || platform.equals("ios")) {
			result = myAPI.apiPost("/userCenter/member/v2/checkAccount", params, platform);
		} else if (platform.equals("h5")) {
			result = myAPI.h5Post("/api/userCenter/checkAccount", params);
		} else if (platform.equals("pc")) {

		} else {
			System.out.println(platform + "平台无[验证号码邮箱是否已注册]接口");
		}

		// System.out.println(result);
		return result;
	}

	/*
	 * 忘记密码
	 * 
	 * @userType 账号类型：member（会员）、 agent（代理商）、 supplier（商家）
	 * 
	 * @account 账号
	 * 
	 * @newPassword 新密码
	 * 
	 * @verifyCode 验证码
	 */
	public JSONObject findPassword(String account,String code, String newPassword, String platform) {
		JSONObject params = new JSONObject();
		
		params.put("devicesType", platform);// 设备类型
		params.put("account", account);// 账号
		params.put("newPassword", baseData.getMD5(newPassword));// 新密码
		params.put("code", code);// 验证码

		JSONObject result = new JSONObject();
		if (platform.equals("android") || platform.equals("ios")) {
			result = myAPI.apiPost("/userCenter/member/v2/findPass", params, platform);
		} else if (platform.equals("h5")) {
			result = myAPI.h5Post("/api/userCenter/findPass", params);
		} else if (platform.equals("pc")) {
			result = myAPI.PCPost("/userCenter/user/common/findPass", params);
		} else {
			System.out.println(platform + "平台没有[找回密码]接口");
		}
		// System.out.println(result);
		return result;
	}

}
