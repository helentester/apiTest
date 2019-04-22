/**
 * @author helen
 * @date 2018年10月30日
 */
package order;

import common.BaseData;
import common.MyAPI;
import data.OrderData;

import static org.testng.Assert.assertEquals;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @Description:物流相关接口
 */
public class TradeInvoiceAdmin {
	BaseData baseData = new BaseData();
	OrderData orderData = new OrderData();
	MyAPI myAPI = new MyAPI();
	
	
	/*物流列表*/
	public JSONObject deliveryList(JSONObject userdata) {
		JSONObject result = new JSONObject();
		if (userdata.getString("userType").equals("backend")) {
			result = myAPI.sysGet(userdata,"/tradeCenter/order/admin/delivery/list");
		}
		else if (userdata.getString("userType").equals("supplier")) {
			result = myAPI.sellerGet(userdata, "/userCenter/supplier/order/delivery/list");
		}
		else {
			System.out.println(userdata.getString("userType")+"的用户没有[物流列表]接口");
		}
		return result;
	}
	
	/*发货接口*/
	public JSONObject send(JSONObject userdata,String orderId) {

		// 根据订单ID获取订单－商品表ID
		String itemId = orderData.getOrderItem_orderItemId(orderId, "id");

		// 获取物流Id
		JSONObject deliveryListResult = this.deliveryList(userdata);
		assertEquals(deliveryListResult.getString("code"), "10000", "校验获取物流列表是否成功" + deliveryListResult);
		JSONArray deliveryArray = deliveryListResult.getJSONObject("data").getJSONArray("list");
		String deliveryId = deliveryArray.getJSONObject(baseData.getNum(0, deliveryArray.size() - 1)).getString("id");

		// 发货（随机选取物流公司）
		JSONObject params = new JSONObject();
		params.put("deliveryId", deliveryId);
		params.put("orderId", orderId);
		params.put("itemId", itemId);

		JSONObject result = new JSONObject();
		if (userdata.getString("userType").equals("backend")) {
			params.put("deliveryNo", baseData.getNum(100, 999999));// 物流编号
			params.put("msg", "接口确认发货");// 发货备注，运营中心才有这个字段
			result = myAPI.sysPost(userdata, "/tradeCenter/order/admin/delivery/send", params);
		} else if (userdata.getString("userType").equals("supplier")) {
			params.put("deliverySn", baseData.getNum(100, 999999));// 物流编号
			result = myAPI.sellerPost(userdata, "/userCenter/supplier/order/delivery/send", params);
		} else {
			System.out.println(userdata.getString("userType") + "的用户没有[确认发货]接口");
		}

		return result;
	}
	
	/*发货列表接口*/
	public JSONObject invoiceList(JSONObject userdata,String orderNo) {
		JSONObject params = new JSONObject();
		params.put("orderNo", orderNo);//订单号
		JSONObject result = new JSONObject();
		if (userdata.getString("userType").equals("backend")) {
			result = myAPI.sysGet(userdata, "/tradeCenter/order/admin/invoice/list", params);
		}
		else if (userdata.getString("userType").equals("supplier")) {
			result = myAPI.sellerGet(userdata, "/userCenter/supplier/order/invoice/list", params);
		}
		return result;
	}
	
}
