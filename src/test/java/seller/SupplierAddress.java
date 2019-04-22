/**
 * @author helen
 * @date 2018年12月3日
 */
package seller;

import com.alibaba.fastjson.JSONObject;

import common.MyAPI;

/**
 * @Description:商家中心 - 地址管理 : Supplier Address Controller
 */
public class SupplierAddress {
	MyAPI myAPI = new MyAPI();
	
	/*省份列表*/
	public JSONObject getTopAddress(JSONObject sellerUserdata) {
		JSONObject params = new JSONObject();
		return myAPI.sellerPost(sellerUserdata, "/userCenter/supplier/getTopAddress", params);
	}
	
	/*根据父区域码查子区域*/
	public JSONObject getSubAddress(JSONObject sellerUserdata,String parentCode) {
		JSONObject params = new JSONObject();
		params.put("parentCode", parentCode);//父区域编码
		return myAPI.sellerPost(sellerUserdata, "/userCenter/supplier/getSubAddress", params);
	}
	
	/*添加地址*/
	public JSONObject addAddress(JSONObject sellerUserdata,String areaCode,String areaName) {
		JSONObject params = new JSONObject();
		params.put("addressPerson", sellerUserdata.getString("userName"));//地址联系人 ,
		params.put("personMobile", sellerUserdata.getString("loginAccount"));//联系人手机号 ,
		params.put("addressType", 1);// 地址类型 ,默认为退货地址：1
		params.put("areaCode", areaCode);//区域编码 ,
		params.put("areaName", areaName);//区域名称
		params.put("address", "888号环球大爱有限公司");
		params.put("isDefault", true);// 是否默认地址 ,
		params.put("supplierId", sellerUserdata.getString("supplierId"));
		return myAPI.sellerPost(sellerUserdata, "/userCenter/supplierBack/addAddress", params);
	}
	
	/*地址管理列表*/
	public JSONObject addressList(JSONObject sellerUserdata) {
		JSONObject params = new JSONObject();
		params.put("supplierId", sellerUserdata.getString("supplierId"));
		return myAPI.sellerPost(sellerUserdata, "/userCenter/supplierBack/addressList", params);
	}
	
	/*地址详情*/
	public JSONObject addressDetail(JSONObject sellerUserdata,String addressId) {
		JSONObject params = new JSONObject();
		params.put("addressId", addressId);
		return myAPI.sellerPost(sellerUserdata, "/userCenter/supplierBack/addressDetail", params);
	}

}
