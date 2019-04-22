/**
 * @author helen
 * @date 2019年4月8日
 */
package order;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import common.MyAPI;

/**
 * @Description:康养公寓订单管理（运营中心、商家中心）
 */
public class HotelOrderadmin {
	MyAPI myAPI = new MyAPI();
	
	/*商家中心[订单详情]接口*/
	public JSONObject OrderDetail(JSONObject sellerUserdata,int orderId) {
		JSONObject params = new JSONObject();
		params.put("orderId",orderId ); //(integer): 订单ID
		
		JSONObject result = new JSONObject();
		result = myAPI.KYSellerGet(sellerUserdata, "/hotelCenter/order/admin/supplier/detail", params);
		return result;
	}
	
	/*商家中心[确认无房]接口*/
	public JSONObject confirmNoRoom(JSONObject sellerUserdata,int orderId) {
		JSONObject params = new JSONObject();
		params.put("orderId",orderId ); //订单Id
		
		JSONObject result = new JSONObject();
		result = myAPI.KYSellerPost(sellerUserdata, "/hotelCenter/order/admin/supplier/confirmNoRoom", params);
		return result;
	}
	
	/*商家中心[分配房间]接口*/
	public JSONObject distributionRoom(JSONObject sellerUserdata,int orderId,JSONArray liveInfos) {
		JSONObject params = new JSONObject();
		params.put("orderId",orderId ); //订单Id
		params.put("liveInfos", liveInfos);//(Array[LiveInformation], optional): 入住信息列表:ID ,入住人 ,房间编号
		
		JSONObject result = new JSONObject();
		result = myAPI.KYSellerPost(sellerUserdata, "/hotelCenter/order/admin/supplier/distributionRoom", params);
		return result;
	}
	
	/*商家中心[修改房间]接口*/
	public JSONObject redistributionRoom(JSONObject sellerUserdata,int orderId,JSONArray liveInfos) {
		JSONObject params = new JSONObject();
		params.put("orderId",orderId ); //订单Id
		params.put("liveInfos", liveInfos);//(Array[LiveInformation], optional): 入住信息列表:ID ,入住人 ,房间编号
		
		JSONObject result = new JSONObject();
		result = myAPI.KYSellerPost(sellerUserdata, "/hotelCenter/order/admin/supplier/redistributionRoom", params);
		return result;
	}
	
	/*商家中心[售后订单]接口*/
	public JSONObject refundList(JSONObject sellerUserdata,String orderSn,String memberAccount,int refundStatus) {
		JSONObject params = new JSONObject();
		params.put("orderSn",orderSn ); //订单号
		params.put("memberAccount",memberAccount ); //会员账号
		params.put("refundStatus", refundStatus);//售后状态 0：未申请；1：退款中(待审核)；2：已退款；3：退款拒绝；9全部
		
		JSONObject result = new JSONObject();
		result = myAPI.KYSellerGet(sellerUserdata, "/hotelCenter/order/admin/supplier/refundList", params);
		return result;
	}
	
	/*商家中心[审核退款]接口*/
	public JSONObject refundCheck(JSONObject sellerUserdata,int orderId,int status) {
		JSONObject params = new JSONObject();
		params.put("orderId",orderId ); //订单Id
		params.put("status", status);//(integer, optional): 状态：1通过; 2拒绝
		params.put("reason", "接口测试退款");
		
		JSONObject result = new JSONObject();
		result = myAPI.KYSellerPost(sellerUserdata, "/hotelCenter/order/admin/supplier/refundCheck", params);
		return result;
	}

}
