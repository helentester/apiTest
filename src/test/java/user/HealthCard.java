/**
 * @author helen
 * @date 2019年3月21日
 */
package user;

import com.alibaba.fastjson.JSONObject;

import common.BaseData;
import common.MyAPI;

/**
 * @Description:会员康养卡相关业务
 */
public class HealthCard {
	BaseData baseData = new BaseData();
	MyAPI myAPI = new MyAPI();
	
	/*会员康养卡列表*/
	public JSONObject memberCardList(JSONObject userdata,String platform) {	
		JSONObject params = new JSONObject();
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiGet(userdata,"/goodsCenter/app/healthcard/v1/cardList",platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Get(userdata, "/goodsCenter/h5/healthcard/cardList");
		}
		else if (platform.equals("pc")) {
			result = myAPI.pcGet(userdata, "/goodsCenter/pc/healthcard/cardList",params);
		}
		else {
			System.out.println(platform+"平台没有会员康养卡列表接口");
		}
		
		return result;
	}
	
	/*激活康养卡*/
	public JSONObject healthCardActive(JSONObject userdata,int id,String veriCode,String veriMobile,String platform) {
		JSONObject params = new JSONObject();
		params.put("id", id);//康养卡id ,
		params.put("idNumber", baseData.getIdCard());//参保人身份证号 
		params.put("mobile", veriMobile);//参保人联系方式
		params.put("name", "李白");//参保人姓名
		params.put("veriCode", veriCode);//验证码
		params.put("veriMobile", veriMobile);//验证手机号
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata,"/goodsCenter/app/healthcard/v1/active", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/goodsCenter/h5/healthcard/active", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/goodsCenter/pc/healthcard/active", params);
		}
		else {
			System.out.println(platform+"平台没有康养卡激活接口");
		}
		
		return result;
	}
	
	/*康养卡转赠*/
	public JSONObject healthCardGive(JSONObject userdata,int id,String account,String veriCode,String veriMobile,String platform) {
		JSONObject params = new JSONObject();
		params.put("id", id);//康养卡id ,
		params.put("account", account);//被转赠人手机或邮箱 ,
		params.put("veriCode", veriCode);//验证码
		params.put("veriMobile", veriMobile);//验证手机号
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost("/goodsCenter/app/healthcard/v1/give", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/goodsCenter/h5/healthcard/give", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/goodsCenter/pc/healthcard/give", params);
		}
		else {
			System.out.println(platform+"平台没有康养卡转赠接口");
		}
		
		return result;
	}
	
	/*会员可升级康养卡列表*/
	public JSONObject memberUpgradeableList(JSONObject userdata,int id,String platform) {
		JSONObject params = new JSONObject();
		params.put("id", id);//康养卡ID
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiGet(userdata, "/goodsCenter/app/healthcard/v1/upgradeableList", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Get(userdata, "/goodsCenter/h5/healthcard/upgradeableList", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.pcGet(userdata, "/goodsCenter/pc/healthcard/upgradeableList", params);
		}
		else {
			System.out.println(platform+"平台没有会员可升级康养卡列表接口");
		}
		
		return result;
	}
	
	/*康养卡使用日志*/
	public JSONObject healthCardLog(JSONObject userdata,String platform) {
		JSONObject params = new JSONObject();
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost("/goodsCenter/app/healthcard/v1/history", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Get(userdata, "/goodsCenter/h5/healthcard/history", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/goodsCenter/pc/healthcard/history", params);
		}
		else {
			System.out.println(platform+"平台没有康养卡转赠接口");
		}
		
		return result;
	}
	
	/*康养卡专题页*/
	public JSONObject healthCardList(JSONObject userdata,String platform) {
		JSONObject params = new JSONObject();
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiGet(userdata,"/goodsCenter/app/healthcard/v1/list",platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Get(userdata,"/goodsCenter/h5/healthcard/list", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.pcGet(userdata,"/goodsCenter/pc/healthcard/list", params);
		}
		else {
			System.out.println(platform+"平台康养卡专题页接口");
		}
		
		return result;
	}

}
