/**
 * @author helen
 * @date 2018年11月2日
 */
package user;

import common.MyAPI;
import com.alibaba.fastjson.JSONObject;

/**
 * @Description:->会员中心、个人中心 : Member Center Controller
 */
public class MemberCenter {
	MyAPI myAPI = new MyAPI();

	/*会员个人信息－“我的页页”*/
	public JSONObject memberInfo(JSONObject userdata) {
		JSONObject params = new JSONObject();
		params.put("uid", userdata.getString("userId"));
		JSONObject result = myAPI.PCPost(userdata, "/userCenter/pc/member/memberInfo", params);
		return result;
	}
	
	/*个人账户:个人中心(会员信息、福豆、余额、优惠券数量、邀请人数、邀请金额*/
	public JSONObject getMemberAccountInfo(JSONObject userdata,String platform) {
		JSONObject params = new JSONObject();
		params.put("uid", userdata.getString("userId"));
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost("/userCenter/member/v1/getMemberAccountInfo", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/userCenter/member/h5/getMemberAccountInfo", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/userCenter/pc/member/personal/center/info/get", params);
		}
		else {
			System.out.println(platform+"没有个人账户信息接口");
		}
		return result;
	}
}
