/**
 * @author helen
 * @date 2018年10月22日
 */
package user;

import common.MyAPI;
import data.MemberData;
import com.alibaba.fastjson.JSONObject;

/**
 * @Description:APP->签到相关接口(新版) : App Sign In Controller
 */
public class SignIn {
	MemberData memberData = new MemberData();
	MyAPI myAPI = new MyAPI();

	/* 获取签到日历信息 */
	public JSONObject getSignInCalendar(JSONObject userdata,String platform) {
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiGet(userdata, "/userCenter/member/v1/sign/getSignInCalendar", platform);
		} else {
			System.out.println(platform + "平台没有[获取签到日历信息]接口");
		}
		return result;
	}

	/* 签到转盘抽奖 */
	public JSONObject luckyDraw(JSONObject userdata, String platform) {
		JSONObject params = new JSONObject();
		params.put("userId", userdata.getString("userId"));// 用户ID
		JSONObject result = new JSONObject();
		if (platform.equals("android") || platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/userCenter/member/v1/sign/luckyDraw", params, platform);
		} else {
			System.out.println(platform + "平台没有[签到转盘抽奖]接口");
		}
		return result;
	}

	/* 抽奖列表接口(当前抽奖模板中的所有选项) */
	public JSONObject luckyDrawList(JSONObject userdata, String platform) {
		JSONObject result = new JSONObject();
		if (platform.equals("android") || platform.equals("ios")) {
			result = myAPI.apiGet(userdata, "/userCenter/member/v1/sign/luckyDraw/list2", platform);
		} else {
			System.out.println(platform + "平台没有[抽奖列表]接口");
		}
		return result;
	}

	/* 会员普通签到 */
	public JSONObject ordinarySignIn(JSONObject userdata, String platform) {
		JSONObject params = new JSONObject();
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/userCenter/member/v1/sign/ordinarySignIn", params, platform);
		} else {
			System.out.println(platform + "平台没有[会员普通签到]接口");
		}
		return result;
	}

	/*
	 * 会员补签
	 * 
	 * @repairSignInTime 补签时间
	 */
	public JSONObject repairSignIn(JSONObject userdata, String repairSignInTime, String platform) {
		JSONObject params = new JSONObject();
		params.put("repairSignInTime", repairSignInTime);
		JSONObject result = new JSONObject();
		if (platform.equals("android")) {
			result = myAPI.apiPost(userdata, "/userCenter/member/v1/sign/repairSignIn", params, "android");
		} else if (platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/userCenter/member/v1/sign/repairSignIn", params, "ios");
		} else {
			System.out.println(platform + "平台没有[会员补签]接口");
		}
		return result;
	}

	/*
	 * 开启或关闭“连续签到提醒”
	 * 
	 * @isSignRemind 是否开启连续签到提醒(0.否、1.是)
	 */
	public JSONObject signRemindSet(JSONObject userdata, String isSignRemind, String platform) {
		JSONObject params = new JSONObject();
		params.put("isSignRemind", isSignRemind);
		params.put("userId", userdata.getString("userId"));
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/userCenter/member/v1/sign/signRemind/set", params, platform);
		} else {
			System.out.println(platform + "平台没有[开启关闭签到提醒]接口");
		}
		return result;
	}

	/* 获取会员签到页面相关统计信息 */
	public JSONObject statisticsInfo(JSONObject userdata, String platform) {
		JSONObject params = new JSONObject();
		params.put("userId", userdata.getString("userId"));
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/userCenter/member/v1/sign/statistics/info", params, platform);
		} else {
			System.out.println(platform + "平台没有[获取会员签到页面相关统计信息]接口");
		}
		return result;
	}

	/*确认申请奖品*/
	/* 确认申请奖品 */
	public JSONObject userDrawApply(JSONObject userdata,String addressId, String luckyDrawId, String platform) {
		JSONObject params = new JSONObject();
		params.put("userId", userdata.getString("userId"));// 用户ID
		params.put("id", luckyDrawId);// 获奖ID
		params.put("addressId", addressId);// 址址ID

		JSONObject result = new JSONObject();
		if (platform.equals("android") || platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/userCenter/member/v1/sign/userDraw/apply", params, platform);
		} else {
			System.out.println(platform + "平台不存在[领取奖品]接口");
		}
		return result;
	}

	/*
	 * 用户获奖记录
	 * 
	 * @type 奖品类型(1.实物奖品、2.虚拟奖品)
	 */
	public JSONObject userDrawList(JSONObject userdata, String type, String platform) {
		JSONObject params = new JSONObject();
		params.put("type", type);
		params.put("page", "1");// 默认第1页
		params.put("pageSize", "10");// 每页显示10条
		JSONObject result = new JSONObject();
		if (platform.equals("android") || platform.equals("ios")) {
			result = myAPI.apiGet(userdata, "/userCenter/member/v1/sign/userDraw/list", params, platform);
		} else {
			System.out.println(platform + "平台没有[用户获奖记录]接口");
		}
		//System.out.println(result);
		return result;
	}

	/* 确认收货奖品 */
	public JSONObject userDrawReceive(JSONObject userdata, String id, String platform) {
		JSONObject params = new JSONObject();
		params.put("userId", userdata.getString("userId"));
		params.put("id", id);// 获奖记录id

		JSONObject result = new JSONObject();
		if (platform.equals("android") || platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/userCenter/member/v1/sign/userDraw/receive", params, platform);
		} else {
			System.out.println(platform + "平台没有[确认收货奖品]接口");
		}
		return result;
	}
}
