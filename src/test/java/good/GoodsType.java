/**
 * @author helen
 * @date 2018年11月27日
 */
package good;

import com.alibaba.fastjson.JSONObject;

import common.MyAPI;

/**
 * @Description:商品类型相关接口
 */
public class GoodsType {
	MyAPI myAPI = new MyAPI();
	
	/*查询商品全部分类列表*/
	public JSONObject goodsTypeAllList(JSONObject userdata,String pid) {
		JSONObject params = new JSONObject();
		params.put("pid", pid);//分类ID
		
		JSONObject result = new JSONObject();
		if (userdata.getString("userType").equals("backend")) {
			result = myAPI.sysGet(userdata, "/goodsCenter/admin/goodsType/allList", params);
		}
		else if (userdata.getString("userType").equals("supplier")) {
			result = myAPI.sellerGet(userdata, "/goodsCenter/admin/goodsType/allList", params);
		}
		else {
			System.out.println(userdata.getString("userType")+"的用户没有相应公共接口");
		}
		return result;
	}

}
