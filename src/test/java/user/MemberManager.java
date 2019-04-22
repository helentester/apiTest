/**
 * @author helen
 * @date 2018年11月21日
 */
package user;

import com.alibaba.fastjson.JSONObject;

import common.MyAPI;

/**
 * @Description:大后台会员管理 : Member Manager Controller
 */
public class MemberManager {
	MyAPI myAPI = new MyAPI();
	
	/*删除会员
	 * @type (string, optional): 0,物理删除，1，软删除 
	 * */
	public JSONObject delete(JSONObject sysUserdata,String userId) {
		JSONObject params = new JSONObject();
		params.put("type", "1");//0,物理删除，1，软删除 ，备注：物理删除的功能已经去除
		params.put("userId", userId);
		
		return myAPI.sysPost(sysUserdata, "/userCenter/back/member/delete", params);
	}
	
	/*禁用账号*/
	public JSONObject disable(JSONObject sysUserdata,String userId) {
		JSONObject params = new JSONObject();
		params.put("userId", userId);
		
		return myAPI.sysPost(sysUserdata, "/userCenter/back/member/disable", params);
	}
}
