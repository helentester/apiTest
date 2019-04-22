/**
 * @author helen
 * @date 2018年10月18日
 */
package user;

import common.MyAPI;
import data.MemberData;
import data.PubData;

import com.alibaba.fastjson.JSONObject;

/**
 * @Description:手机验证码模块: Verification Code Controller
 */
public class VerificationCode {
	PubData pubData = new PubData();
	MemberData memberData = new MemberData();
	MyAPI myAPI = new MyAPI();

	
	/*发送验证码
	 * mobile (string, optional): 手机号码或邮箱地址格式 ,
		telephoneCode (string, optional): 电话区号(国外的才需要传，国内不要传) ,
		type (string, optional): 验证码类型:1:忘记密码找回、2:注册、3:旧手机验证、4:新手机绑定、5:豆豆互转验证、6：绑定会员、7：运营中心登陆验、8：康养卡升级、9：康养卡激活、10：康养卡转赠、11：体检卡预约
		@userType:member（会员）  supplier（商家） agent(代理)
	 * */
	public JSONObject sendCode(String account,String telephoneCode,int type,String platform) {
		JSONObject params = new JSONObject();
		params.put("account", account);//账号
		params.put("telephoneCode", telephoneCode);//区号
		params.put("type", type);//消息类型
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost("/userCenter/member/v2/sendCode", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post("/api/userCenter/sendCode", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost("/userCenter/user/sendCode", params);
		}
		else if (platform.equals("sys")) {
			result = myAPI.sysPost("/userCenter/user/sendCode", params);
		}
		else if (platform.equals("agent")) {
			result = myAPI.agentPost("/userCenter/user/sendCode", params);
		}
		else if (platform.equals("seller")) {
			result = myAPI.sellerPost("/userCenter/user/sendCode", params);
		}
		else if (platform.equals("h5share")) {//h5分享注册页面发出的
			params.put("mobile", account);// 账号
			params.put("sign", "sign");
			params.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
			params.put("requestId", "af92573f-665b-4b76-be44-e664927d71a9");
			result = myAPI.apiPost("/userCenter/member/v1/sendCode", params, "h5");
		}
		else {
			System.out.println(platform+"平台不存在发送验证码接口");
		}
		
		return result;
	}
	
	/*获取图片验证码*/
	public JSONObject getImageVerificationCode(String userType) {
		JSONObject params = new JSONObject();
		
		JSONObject result = new JSONObject();
		if (userType.equals("seller")) {
			result = myAPI.sellerPost("/userCenter/supplierBack/supplierLogin/getImageVerificationCode",params);
		}
		else if (userType.equals("agent")) {
			result = myAPI.agentPost("/userCenter/agentBack/agentLogin/getImageVerificationCode", params);
		}
		else {
			System.out.println(userType+"无获取图片验证码接口");
		}
		return result;
	}
}
