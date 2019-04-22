/**
 * @author helen
 * @date 2018年11月26日
 */
package seller;

import java.util.ArrayList;
import com.alibaba.fastjson.JSONObject;

import common.BaseData;
import common.MyAPI;

/**
 * @Description:商家中心角色管理
 */
public class SupplierRole {
	BaseData baseData = new BaseData();
	MyAPI myAPI = new MyAPI();
	
	/*新增角色*/
	public JSONObject addSupplierRole(JSONObject sellerUserdata,String roleName,ArrayList<Integer> permissionIds) {
		JSONObject params = new JSONObject();
		params.put("roleName", roleName);
		params.put("permissionIds", permissionIds);
		params.put("supplierId", sellerUserdata.getString("supplierId"));
		
		return myAPI.sellerPost(sellerUserdata,"/userCenter/supplier/role/addSupplierRole", params);
	}
	
	/*查询商家对应权限信息*/
	public JSONObject supplierPermissionList(JSONObject sellerUserdata) {
		return myAPI.sellerGet(sellerUserdata, "/userCenter/supplier/role/supplierPermissionList");
	}
	
	/*商家角色列表*/
	public JSONObject supplierRoleList(JSONObject sellerUserdata) {
		JSONObject params = new JSONObject();
		params.put("supplierId", sellerUserdata.getString("supplierId"));
		return myAPI.sellerGet(sellerUserdata, "/userCenter/supplier/order/supplierRoleList", params);
	}

}
