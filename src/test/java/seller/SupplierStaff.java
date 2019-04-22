/**
 * @author helen
 * @date 2018年11月26日
 */
package seller;

import com.alibaba.fastjson.JSONObject;

import common.BaseData;
import common.MyAPI;

/**
 * @Description:商家中心员工管理
 */
public class SupplierStaff {
	BaseData baseData = new BaseData();
	MyAPI myAPI = new MyAPI();
	
	/*新增员工*/
	public JSONObject addStaff(JSONObject sellerUserdata,String mobile,String roleId) {
		JSONObject params = new JSONObject();
		params.put("mobile", mobile);//登录手机号
		params.put("password", baseData.getMD5("12345678li"));
		params.put("roleId", roleId);//角色ID
		params.put("supplierId", sellerUserdata.getString("supplierId"));
		params.put("userName", "new员工"+mobile);
		return myAPI.sellerPost(sellerUserdata,"/userCenter/supplier/order/addStaff", params);
	}
	
	/*修改员工*/
	public JSONObject editStaff(JSONObject sellerUserdata,String staffId,String mobile,String roleId) {
		JSONObject params = new JSONObject();
		params.put("staffId", staffId);//员工ID
		params.put("mobile", mobile);//登录手机号
		params.put("password", baseData.getMD5("123456li"));
		params.put("roleId", roleId);//角色ID
		params.put("userName", "update员工"+mobile);
		
		return myAPI.sellerPost(sellerUserdata, "/userCenter/supplier/order/editStaff", params);
	}
	
	/*删除员工*/
	public JSONObject delStaff(JSONObject sellerUserdata,String staffId) {
		JSONObject params = new JSONObject();
		params.put("staffId", staffId);//员工ID
		return myAPI.sellerPost(sellerUserdata, "/userCenter/supplier/order/delStaff", params);
	}
	
	/*员工列表*/
	public JSONObject staffList(JSONObject sellerUserdata,String mobile,String userName,String roleId) {
		JSONObject params = new JSONObject();
		params.put("mobile", mobile);//登录手机号
		params.put("password", baseData.getMD5("12345678li"));
		params.put("roleId", roleId);//角色ID
		params.put("userName", userName);//员工名称
		params.put("supplierId", sellerUserdata.getString("supplierId"));
		return myAPI.sellerGet(sellerUserdata, "/userCenter/supplier/order/staffList", params);
	}

}
