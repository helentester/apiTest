/**
 * @author helen
 * @date 2019年3月21日
 */
package user;

import com.alibaba.fastjson.JSONObject;

import common.MyAPI;

/**
 * @Description:会员豆豆转账
 */
public class MemberTransferScore {
	MyAPI myAPI = new MyAPI();
	
	/*获取可转账总额*/
	public JSONObject ableTransferAmount(JSONObject userdata,int type,String platform) {
		JSONObject params = new JSONObject();
		params.put("type", type);//类型：1.福豆 2.乐豆 3.享豆 4.学习豆 ,
		params.put("userId", userdata.getString("userId"));//会员ID
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost("/userCenter/member/v1/fund/transfer/able/amount", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/userCenter/member/h5/fund/transfer/able/amount", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/userCenter/member/pc/fund/transfer/able/amount", params);
		}
		else {
			System.out.println(platform+"平台没获取可转账总额接口");
		}
		
		return result;
	}
	
	/*转赠豆豆接口*/
	public JSONObject transfer(JSONObject userdata,int type,Double amount,String account,String code,String platform) {
		JSONObject params = new JSONObject();
		params.put("type", type);//类型：1.福豆 2.乐豆 3.享豆 4.学习豆 ,
		params.put("amount", amount);//转赠数量
		params.put("account", account);//对方账号
		params.put("code", code);//验证码
		params.put("userId", userdata.getString("userId"));//会员ID
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost("/userCenter/member/v1/fund/transfer", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/userCenter/member/h5/fund/transfer", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/userCenter/member/pc/fund/transfer", params);
		}
		else {
			System.out.println(platform+"平台转赠豆豆接口");
		}
		
		return result;
	}
	
	/*转账明细列表*/
	public JSONObject transferList(JSONObject userdata,String type,String platform) {
		JSONObject params = new JSONObject();
		params.put("type", type);// 类型：1.转入、2.转出 ,
		params.put("userId", userdata.getString("userId"));//会员ID
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost("/userCenter/member/v1/fund/transfer/list", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/userCenter/member/h5/fund/transfer/list", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/userCenter/member/pc/fund/transfer/list", params);
		}
		else {
			System.out.println(platform+"平台转赠豆豆接口");
		}
		
		return result;
	}

}
