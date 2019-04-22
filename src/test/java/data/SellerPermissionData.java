/**
 * @author helen
 * @date 2018年11月26日
 */
package data;

import com.alibaba.fastjson.JSONArray;

import common.MysqlConnect;

/**
 * @Description:商家权限数据
 */
public class SellerPermissionData {
	MysqlConnect mysqlConnect = new MysqlConnect();
	JSONArray arrayResult = new JSONArray();
	
	/*查询所有权限*/
	public JSONArray getAllPermission() {
		return mysqlConnect.getData("SELECT permission_id from m_permission_expand");
	}

}
