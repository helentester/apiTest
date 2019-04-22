/**
 * @author helen
 * @date 2018年12月4日
 */
package user;

import com.alibaba.fastjson.JSONObject;

import common.MyAPI;

/**
 * @Description:申请信用卡相关接口
 * APP->会员实名认证 : Auth Real Name App Controller
 */
public class ApplyCreditCard {
	MyAPI myAPI = new MyAPI();
	
	/*申请信用卡*/
	public JSONObject apply(JSONObject userdata,String agentPhone,String platform) {
		JSONObject params = new JSONObject();
		params.put("account", userdata.getString("loginAccount"));
		params.put("agentPhone", agentPhone);//代理人手机号
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/userCenter/member/v1/applyCreditCard", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/userCenter/member/h5/applyCreditCard", params);
		}
		else {
			System.out.println(platform+"端没有[申请信用卡]接口");
		}
		return result;
	}
	
	/*申请信用卡列表*/
	public JSONObject creditCardApplyList(JSONObject sysUserdata,String account) {
		JSONObject params = new JSONObject();
		params.put("account", account);//申请人账号
		return myAPI.sysGet(sysUserdata, "/userCenter/member/applyCreditCard/creditCardApplyList", params);
	}
	
	/*申请信用卡列表导出*/
	public JSONObject exportCreditCardApplyList(JSONObject sysUserdata) {
		JSONObject params = new JSONObject();
		return myAPI.sysGet(sysUserdata, "/userCenter/member/applyCreditCard/exportCreditCardApplyList", params);
	}

}
