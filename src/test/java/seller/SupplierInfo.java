/**
 * @author helen
 * @date 2019年4月10日
 */
package seller;

import com.alibaba.fastjson.JSONObject;

import common.MyAPI;

/**
 * @Description:商家中心信息
 */
public class SupplierInfo {
	MyAPI myAPI = new MyAPI();
	
	public JSONObject homePageInfo(JSONObject sellerUserdata) {
		JSONObject result = new JSONObject();
		result = myAPI.sellerGet(sellerUserdata, "/hotelCenter/supplierCenter/statistics/homePage");
		return result;
	}

}
