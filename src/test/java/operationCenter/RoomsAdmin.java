/**
 * @author helen
 * @date 2019年4月3日
 */
package operationCenter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import common.MyAPI;

/**
 * @Description:康养卡房间管理
 */
public class RoomsAdmin {
	MyAPI myAPI = new MyAPI();
	
	/*康养公寓房间列表*/
	public JSONObject roomList(JSONObject sysUserdata,int id) {
		JSONObject params = new JSONObject();
		params.put("id", id);//房间ID
		
		JSONObject result = new JSONObject();
		result = myAPI.sysGet(sysUserdata, "/hotelCenter/room/admin/list", params);
		return result;
	}
	
	/*审核房间*/
	public JSONObject roomAudit(JSONObject sysUserdata,JSONArray roomIdList,int status) {
		JSONObject params = new JSONObject();
		params.put("id",roomIdList ); //(Array[string], optional): 房间id列表 ,
		params.put("status", status);//(integer, optional): 状态(0:待提交,1:待审核,2:审核通过,3:审核不通过,4:已上架,5:已下架,6:已失效)
		params.put("notice", "接口审核房间");//(string, optional): 操作备注 ,
		
		JSONObject result = new JSONObject();
		result = myAPI.sysPost(sysUserdata, "/hotelCenter/room/admin/status/update", params);
		return result;
	}
	
	/*房间上下架*/
	public JSONObject roomSellSet(JSONObject sysUserdata,JSONArray roomIdList,int status) {
		JSONObject params = new JSONObject();
		params.put("id",roomIdList ); //(Array[string], optional): 房间id列表 ,
		params.put("status", status);//(integer, optional): 状态(0:待提交,1:待审核,2:审核通过,3:审核不通过,4:已上架,5:已下架,6:已失效)
		params.put("notice", "接口审核房间");//(string, optional): 操作备注 ,
		
		JSONObject result = new JSONObject();
		result = myAPI.sysPost(sysUserdata, "/hotelCenter/room/admin/grounding/update", params);
		return result;
	}
	
}
