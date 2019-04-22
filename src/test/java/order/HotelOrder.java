/**
 * @author helen
 * @date 2019年4月4日
 */
package order;

import com.alibaba.fastjson.JSONObject;

import common.BaseData;
import common.MyAPI;

/**
 * @Description:
 */
public class HotelOrder {
	BaseData baseData = new BaseData();
	MyAPI myAPI = new MyAPI();
	
	/*提交订单*/
	public JSONObject submit(JSONObject userdata,String liveName,String liveDate,String leaveDate,int roomId,int rooms,String userPhone,String platform) {
		JSONObject params = new JSONObject();
		params.put("liveName", liveName);//(string): 入住人，多个用中文逗号隔开 ,
		params.put("liveDate", liveDate);//(integer): 必填，入住时间(年月日13位时间戳) ,
		params.put("leaveDate", leaveDate);//(integer): 可不填，离店时间(年月日13位时间戳) ,
		params.put("roomId", roomId);// (integer): 房间id ,
		params.put("rooms", rooms);//(integer): 房间数 
		params.put("userPhone", userPhone);// (string): 联系方式
		params.put("notice", "接口提交订单");//(string, optional): 订单留言 ,
		params.put("userId", userdata.getInteger("userId")); //(integer): 会员id 
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			params.put("channel", "APP");
			result = myAPI.apiPost(userdata, "/hotelCenter/order/v1/submit", params, platform);
		}
		else if (platform.equals("h5")) {
			params.put("channel", "H5");
			result = myAPI.h5Post(userdata, "/hotelCenter/order/h5/submit", params);
		}
		else if (platform.equals("pc")) {
			params.put("channel", "PC");
			result = myAPI.PCPost(userdata, "/hotelCenter/order/web/submit", params);
		}
		else {
			System.out.println(platform+"平台没有康养公寓下订单接口");
		}
		return result;
	}
	
	/*支付订单*/
	public JSONObject payOrder(JSONObject userdata,int orderId,String orderSn,int payment,String payType,String payAmount,String platform) {
		JSONObject params = new JSONObject();
		params.put("id", orderId);//订单id
		params.put("callbackType", "1");//(string, optional): 回调类型 1:康养公寓订单 ,
		params.put("orderSn", orderSn);//订单编号
		params.put("payment", payment);//支付方式 1：养老福豆；2：全余额支付；3：支付宝；4：微信；5：支付宝支付+余额支付；6：微信宝支付+余额支付
		params.put("payType", payType);// 支付类型不能为空  支付宝App:alipay；支付宝网站支付:webAlipay；微信App支付:wxin；扫码微信支付:wxinNative；平台支付：wufuPay（养老福豆或余额）
		params.put("payAmount", payAmount);//(string, optional): 支付金额(现金支付需传) ,
		params.put("body", "环球大爱"+orderSn);//(string, optional): 商品描述 ,
		params.put("subject", "环球大爱"+orderSn);//(string, optional): 订单标题 
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/hotelCenter/order/v1/payOrder", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/api/hotelCenter/payOrder", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/hotelCenter/order/web/payOrder", params);
		}
		else {
			System.out.println(platform+"平台没有康养公寓支付订单接口");
		}
		return result;
	}
	
	/*订单详情*/
	public JSONObject detail(JSONObject userdata,int orderId,String platform) {
		JSONObject params = new JSONObject();
		params.put("id",orderId ); //(integer): 订单ID
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiGet(userdata, "/hotelCenter/order/v1/detail", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Get(userdata, "/hotelCenter/order/h5/detail", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.h5Get(userdata, "/hotelCenter/order/web/detail", params);
		}
		else {
			System.out.println(platform+"平台没有康养公寓订单详情接口");
		}
		return result;
	}
	
	/*订单列表*/
	public JSONObject orderList(JSONObject userdata,int orderStatus,String platform) {
		JSONObject params = new JSONObject();
		params.put("orderStatus",orderStatus ); //订单状态 全部订单(不传)；1：待付款；2：待确认；3：待入住；4：退款单
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiGet(userdata, "/hotelCenter/order/v1/list", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Get(userdata, "/hotelCenter/order/h5/list", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.h5Get(userdata, "/hotelCenter/order/web/list", params);
		}
		else {
			System.out.println(platform+"平台没有康养公寓订单详情接口");
		}
		return result;
	}
	
	/*取消订单*/
	public JSONObject cancelOrder(JSONObject userdata,int orderId,String platform) {
		JSONObject params = new JSONObject();
		params.put("id", orderId);//订单id
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/hotelCenter/order/v1/cancel", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/hotelCenter/order/h5/cancel", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/hotelCenter/order/web/cancel", params);
		}
		else {
			System.out.println(platform+"平台没有康养公寓取消订单接口");
		}
		return result;
	}
	
	/*评论订单*/
	public JSONObject evaluate(JSONObject userdata,int orderId,String orderSn,String urlType,String platform) {
		JSONObject params = new JSONObject();
		params.put("orderId", orderId);//(integer): 订单id ,
		params.put("orderSn", orderSn);// (string): 订单号 ,
		params.put("level", baseData.getNum(1, 5));//(string): 商品评价等级 ,
		params.put("content", "接口首评");//(string): 商品评价信息 ,
		params.put("urlType", urlType);//(string, optional): URL类型：0 图片 1视频
		if (urlType.equals("0")) {
			params.put("imgVideoUrl", "upload/file-1542870203871.jpg,upload/file-1542870209324.jpg");//(string, optional): 评价图片URL：多个用逗号分隔/视频URL ,
		}
		else {
			params.put("imgVideoUrl", "upload/file-1543396076229.mp4");//(string, optional): 评价图片URL：多个用逗号分隔/视频URL ,
		}
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/hotelCenter/evaluation/v1/evaluateOrderItem", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/hotelCenter/h5/evaluation/evaluateOrderItem", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/hotelCenter/web/evaluation/evaluateOrderItem", params);
		}
		else {
			System.out.println(platform+"平台没有康养公寓取消订单接口");
		}
		return result;
	}
	
	/*申请退款*/
	public JSONObject refundApply(JSONObject userdata,int orderId,String platform) {
		JSONObject params = new JSONObject();
		params.put("id", orderId);//订单id
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/hotelCenter/order/v1/refund/apply", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/hotelCenter/order/h5/refund/apply", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/hotelCenter/order/web/refund/apply", params);
		}
		else {
			System.out.println(platform+"平台没有康养公寓取消订单接口");
		}
		return result;
	}
	
	/*退款详情*/
	public JSONObject refundDetail(JSONObject userdata,int orderId,String platform) {
		JSONObject params = new JSONObject();
		params.put("id",orderId ); //订单id
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiGet(userdata, "/hotelCenter/order/v1/refund/detail", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Get(userdata, "/hotelCenter/order/h5/refund/detail", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.h5Get(userdata, "/hotelCenter/order/web/refund/detail", params);
		}
		else {
			System.out.println(platform+"平台没有康养公寓订单详情接口");
		}
		return result;
	}
	
	/*取消退款*/
	public JSONObject cancelRefund(JSONObject userdata,int orderId,String platform) {
		JSONObject params = new JSONObject();
		params.put("id", orderId);//订单id
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/hotelCenter/order/v1/refund/cancel", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/hotelCenter/order/h5/refund/cancel", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/hotelCenter/order/web/refund/cancel", params);
		}
		else {
			System.out.println(platform+"平台没有康养公寓取消订单接口");
		}
		return result;
	}

}
