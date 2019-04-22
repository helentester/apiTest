/**
 * @author helen
 * @date 2018年11月29日
 */
package order;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import common.BaseData;
import common.MyAPI;
import data.OrderData;

/**
 * @Description:退换货管理 : Trade Refund Admin Controller
 */
public class TradeRefund {
	BaseData baseData = new BaseData();
	OrderData orderData = new OrderData();
	MyAPI myAPI = new MyAPI();

	/*退款申请*/
	public JSONObject moneyRefund(JSONObject userdata,String orderId,String platform) {
		//获取订单商品ID，多个用逗号隔开
		JSONArray orderItemsIdArray = orderData.getOrderItemsId_orderId(orderId);
		String orderItemId = orderItemsIdArray.getJSONObject(0).getString("id");
		for (int i = 1; i < orderItemsIdArray.size()-1; i++) {
			orderItemId = ","+orderItemsIdArray.getJSONObject(i).getString("id");
		}
		
		JSONObject params = new JSONObject();
		params.put("orderId", orderId);//订单ID
		params.put("orderItemId", orderItemId);//订单商品ID
		params.put("cause", "退款（接口提交）");//退款原因
		params.put("userId", userdata.getString("userId"));
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/tradeCenter/order/v1/moneyRefund", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/tradeCenter/order/h5/moneyRefund", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/tradeCenter/web/order/moneyRefund", params);
		}
		else {
			System.out.println(platform+"平台没有[退款申请]接口");
		}
		return result;
	}
	
	/*退货退款申请*/
	public JSONObject goodsRefund(JSONObject userdata,String orderId,String orderItemId,String number,String platform) {
		JSONObject params = new JSONObject();
		params.put("orderId", orderId);
		params.put("orderItemId", orderItemId);//订单商品ID
		params.put("number", number);//商品数量
		params.put("cause", "退货退款（接口提交）");//退货退款原因
		params.put("deliveryName", "顺丰快递");//快递名称
		params.put("deliveryNo", baseData.getNum(1000, 999999));//快递单号
		params.put("mobile", userdata.getString("mobile"));//联系方式
		params.put("refundName", userdata.getString("loginAccount"));//退货人
		params.put("userId", userdata.getString("userId"));
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/tradeCenter/order/v1/goodsRefund", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/tradeCenter/order/h5/goodsRefund", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/tradeCenter/web/order/goodsRefund", params);
		}
		else {
			System.out.println(platform+"平台没有[退货退款]接口");
		}
		return result;
	}

	/*运营中心、商家中心退货退款审核*/
	public JSONObject RefundAudit(JSONObject userdata,String refundId,int status) {
		JSONObject params = new JSONObject();
		params.put("id", refundId);//退款单 或退货退款单ID
		params.put("status", status);//退货审核状态 -1：取消，-2：未通过， 2：通过
		if (status==2) {
			params.put("adminDesc", "退款或退货退款审核不通过（接口提交）");//拒绝理由
		}
		
		JSONObject result = new JSONObject();
		if (userdata.getString("userType").equals("supplier")) {
			result = myAPI.sellerPost(userdata, "/tradeCenter/order/admin/supplierRefundAudit", params);
		}
		else if (userdata.getString("userType").equals("backend")) {
			params.put("adminId", userdata.getString("uid"));
			params.put("loginAccount", userdata.getString("loginAccount"));
			result = myAPI.sysPost(userdata, "/tradeCenter/order/admin/adminRefundAudit", params);
		}
		
		return result;
	}

	/*运营中心、商家中心退货退款列表查询*/
	public JSONObject refundList(JSONObject userdata,String orderSn) {
		JSONObject params = new JSONObject();
		params.put("orderSn", orderSn);//订单号
		params.put("startTime", baseData.getTimeStamp(-1));//昨天
		params.put("endTime", baseData.getTimeStamp(1));//明天 
		JSONObject result = new JSONObject();
		if (userdata.getString("userType").equals("supplier")) {
			result = myAPI.sellerGet(userdata, "/userCenter/supplier/order/refundList", params);
		}
		else if (userdata.getString("userType").equals("backend")) {
			result = myAPI.sysGet(userdata, "/tradeCenter/order/admin/refundList", params);
		}
		else {
			System.out.println(userdata.getString("userType")+"的用户没有[退货退款列表]的接口");
		}
		return result;		
	}
	
	/*运营中心、商家中心退货退款详情*/
	public JSONObject refundDetail(JSONObject userdata,String refundId) {
		JSONObject params = new JSONObject();
		params.put("id", refundId);
		
		JSONObject result = new JSONObject();
		if (userdata.getString("userType").equals("supplier")) {
			result = myAPI.sellerGet(userdata, "/tradeCenter/order/admin/refundDetail", params);
		}
		else if (userdata.getString("userType").equals("backend")) {
			result = myAPI.sysGet(userdata, "/userCenter/supplier/order/refundListDetail", params);
		}
		return result;
	}
}
