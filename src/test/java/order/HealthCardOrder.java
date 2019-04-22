/**
 * @author helen
 * @date 2019年3月21日
 */
package order;



import com.alibaba.fastjson.JSONObject;

import common.MyAPI;

/**
 * @Description:
 */
public class HealthCardOrder {
	MyAPI myAPI = new MyAPI();
	
	/*康养卡下单接口*/
	public JSONObject generateHealthCardOrder(JSONObject userdata,String addressId,String agentAccount,int goodsId,String skuNumber,int number,int supplierId,String platform) {
		JSONObject params = new JSONObject();
		params.put("addressId", addressId);//收货地址
		params.put("agentAccount", agentAccount);//代理人 ,
		params.put("goodsId", goodsId);//商品ID
		params.put("skuNumber", skuNumber);//规格编码 ,
		params.put("number", number);//购买数量 ,
		params.put("supplierId", supplierId);//商家id
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata,"/tradeCenter/healthCardOrder/v1/generateOrder", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/tradeCenter/healthCardOrder/h5/generateOrder", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/tradeCenter/healthCardOrder/web/generateOrder", params);
		}
		else {
			System.out.println(platform+"平台没有康养卡下单接口");
		}
		
		return result;
	}
	
	/*康养卡订单详情*/
	public JSONObject healthCardOrderDetail(JSONObject userdata,String orderId,String platform) {
		JSONObject params = new JSONObject();
		params.put("orderId", orderId);//订单id
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiGet("/tradeCenter/healthCardOrder/v1/orderDetail", params, platform);
		}
		else if (platform.equals("h5")) {
			
		}
		else if (platform.equals("pc")) {
			
		}
		else {
			System.out.println(platform+"平台没有康养卡订单详情接口");
		}
		
		return result;
	}
	
	/*康养卡升级接口*/
	public JSONObject healthCardUpgrade(JSONObject userdata,int goodsId,int healthCardId,String veriCode,String veriMobile,String platform) {
		JSONObject params = new JSONObject();
		params.put("goodsId", goodsId);//升级商品id ,
		params.put("healthCardId", healthCardId);//康养卡ID 
		params.put("veriCode", veriCode);//验证码 
		params.put("veriMobile", veriMobile);//验证手机号
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata,"/tradeCenter/healthCardOrder/v1/upgrade", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/tradeCenter/healthCardOrder/h5/upgrade", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/tradeCenter/healthCardOrder/web/upgrade", params);
		}
		else {
			System.out.println(platform+"平台没有康养卡升级接口");
		}
		
		return result;
	}
	
	/*上传回执接口*/
	public JSONObject uploadReceipt(JSONObject userdata,String orderId,String platform) {
		JSONObject params = new JSONObject();
		params.put("orderId", orderId);//订单id
		params.put("urlList", "[\"upload/1540457654051.jpg\"]");//(Array[string]): 回执url
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata,"/tradeCenter/healthCardOrder/v1/uploadReceipt", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/tradeCenter/healthCardOrder/h5/uploadReceipt", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/tradeCenter/healthCardOrder/web/uploadReceipt", params);
		}
		else {
			System.out.println(platform+"平台没有康养卡上传回执接口");
		}
		
		return result;
	}
	
	/*升级上传回执接口*/
	public JSONObject uploadUpOrderReceipt(JSONObject userdata,int orderId,String platform) {
		JSONObject params = new JSONObject();
		params.put("orderId", orderId);//订单id
		params.put("urlList", "[\"upload/1540457654051.jpg\"]");//(Array[string]): 回执url
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata,"/tradeCenter/healthCardOrder/v1/uploadUpOrderReceipt", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/tradeCenter/healthCardOrder/h5/uploadUpOrderReceipt", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/tradeCenter/healthCardOrder/web/uploadUpOrderReceipt", params);
		}
		else {
			System.out.println(platform+"平台没有康养卡上传回执接口");
		}
		
		return result;
	}
	
	/*取消下单接口*/
	public JSONObject cancelHealthCardOrder(JSONObject userdata,String orderId,String platform) {
		JSONObject params = new JSONObject();
		params.put("orderId", orderId);//订单id
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost("/tradeCenter/healthCardOrder/v1/cancelOrder", params, platform);
		}
		else if (platform.equals("h5")) {
			
		}
		else if (platform.equals("pc")) {
			
		}
		else {
			System.out.println(platform+"平台没有康养卡取消下单接口");
		}
		
		return result;
	}
	
	/*再次购买*/
	public JSONObject bookHealthCardOrderAgain(JSONObject userdata,String orderId,String platform) {
		JSONObject params = new JSONObject();
		params.put("orderId", orderId);//订单id
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost("/tradeCenter/healthCardOrder/v1/bookOrderAgain", params, platform);
		}
		else if (platform.equals("h5")) {
			
		}
		else if (platform.equals("pc")) {
			
		}
		else {
			System.out.println(platform+"平台没有康养卡再次购买接口");
		}
		
		return result;
	}
}
