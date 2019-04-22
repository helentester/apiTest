/**
 * @author helen
 * @date 2019年4月3日
 */
package good;

import com.alibaba.fastjson.JSONObject;

import common.MyAPI;

/**
 * @Description:康养公寓－房间间前台接口
 */
public class Rooms {
	MyAPI myAPI = new MyAPI();
	
	/*康养公寓专题页*/
	public JSONObject  roomList(String city,String startTime,String endTime,String platform) {
		JSONObject params = new JSONObject();
		params.put("city", city);
		params.put("startTime", startTime);
		params.put("endTime", endTime);
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiGet("/hotelCenter/room/v1/list", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Get("/hotelCenter/room/h5/list", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.pcGet("/hotelCenter/room/pc/list", params);
		}
		else {
			System.out.println(platform+"平台没有康养公寓主题列表");
		}
		
		return result;
	}
	
	/*康养公寓详情页*/
	public JSONObject  roomDetail(int id,String platform) {
		JSONObject params = new JSONObject();
		params.put("id", id);//康养公寓id
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiGet("/hotelCenter/room/v1/detail", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Get("/hotelCenter/room/h5/detail", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.pcGet("/hotelCenter/room/pc/detail", params);
		}
		else {
			System.out.println(platform+"平台没有康养公寓主题列表");
		}
		
		return result;
	}
	
	/*康养公寓获取库存*/
	public JSONObject  roomStock(int roomId,String startTime,String endTime,String platform) {
		JSONObject params = new JSONObject();
		params.put("roomId", roomId);//康养公寓id
		params.put("startTime", startTime);//开始时间
		params.put("endTime", endTime);//结束时间
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiGet("/hotelCenter/room/v1/stock/get", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Get("/hotelCenter/room/h5/stock/get", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.pcGet("/hotelCenter/room/pc/stock/get", params);
		}
		else {
			System.out.println(platform+"平台没有康养公寓库存查询接口");
		}
		
		return result;
	}

}
