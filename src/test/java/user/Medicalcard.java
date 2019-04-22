/**
 * @author helen
 * @date 2019年4月1日
 */
package user;

import com.alibaba.fastjson.JSONObject;

import common.MyAPI;

/**
 * @Description:体检卡相关业务
 */
public class Medicalcard {
	MyAPI myAPI = new MyAPI();
	
	/*查询体检卡列表*/
	public JSONObject MedicalcardList(JSONObject userdata,int status,String platform) {
		JSONObject params = new JSONObject();
		params.put("status", status);//状态(0:未使用,1:已预约,2:已使用,3:已过期)
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiGet(userdata,"/goodsCenter/app/medicalcard/v1/list", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Get(userdata, "/goodsCenter/h5/medicalcard/list", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.pcGet(userdata, "/goodsCenter/h5/medicalcard/list", params);
		}
		else {
			System.out.println(platform+"平台没有康养卡激活接口");
		}
		
		
		return result;
	}

}
