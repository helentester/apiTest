/**
 * @author helen
 * @date 2018年11月22日
 */
package order;

import com.alibaba.fastjson.JSONObject;

import common.BaseData;
import common.MyAPI;
import data.OrderData;

/**
 * @Description:包含所有评论接口：会员、运营中心、商家
 * ->订单商品评价接口 : Trade Evaluate Controller
 * 订单商品评价管理后台接口 : Goods Evaluate Admin Controller
 * 商家中心-管理列表 : Supplier Center Controller
 */
public class TradeEvaluate {
	BaseData baseData = new BaseData();
	OrderData orderData = new OrderData();
	MyAPI myAPI = new MyAPI();
	
	/*订单提交评价*/
	public JSONObject evaluateOrderItem(JSONObject userdata,String orderSn,String orderItemId,String urlType,String platform) {
		//商品评价等级 :1至5
		int level = baseData.getNum(1, 5);
		//评论的图片或视频
		String imgVideoUrl = "";
		if (urlType.equals("0")) {//图片
			imgVideoUrl = "upload/file-1542870203871.jpg,upload/file-1542870209324.jpg";
		}
		else if (urlType.equals("1")) {//视频
			imgVideoUrl = "upload/file-1543396076229.mp4";
		}
		//获取SKU
		String skuNumber = orderData.getOrderItem_orderItemId(orderItemId, "sku_number");
		
		JSONObject params = new JSONObject();
		params.put("orderSn", orderSn);//订单号 
		params.put("orderItemId", orderItemId);//订单商品id 
		params.put("skuNumber", skuNumber);
		params.put("level", String.valueOf(level));//评论等级
		params.put("urlType", urlType);//URL类型：0 图片 1视频
		params.put("content", "接口提交评价,等级为："+level);//
		params.put("imgVideoUrl", imgVideoUrl);//评论的图片或视频
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/tradeCenter/evaluation/v1/evaluateOrderItem", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/tradeCenter/h5/evaluation/evaluateOrderItem", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/tradeCenter/web/evaluation/evaluateOrderItem", params);
		}
		else {
			System.out.println(platform+"平台没有[提交订单评论接口]");
		}
		
		return result;
	}
	
	/*订单提交追评*/
	public JSONObject evaluateAppendOrderItem(JSONObject userdata,String orderItemId,String urlType,String platform) {
		// 评论的图片或视频
		String imgVideoUrl = "";
		if (urlType.equals("0")) {// 图片
			imgVideoUrl = "upload/file-1542870203871.jpg,upload/file-1542870209324.jpg";
		} else if (urlType.equals("1")) {// 视频
			imgVideoUrl = "upload/file-1542871793049.mp4";
		}

		JSONObject params = new JSONObject();
		params.put("orderItemId", orderItemId);// 订单商品id
		params.put("urlType", urlType);// URL类型：0 图片 1视频
		params.put("content", "接口提交追加评价");//
		params.put("imgVideoUrl", imgVideoUrl);// 评论的图片或视频
		JSONObject result = new JSONObject();
		if (platform.equals("android") || platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/tradeCenter/evaluation/v1/evaluateAppendOrderItem", params, platform);
		} else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/tradeCenter/h5/evaluation/evaluateAppendOrderItem", params);
		} else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/tradeCenter/web/evaluation/evaluateAppendOrderItem", params);
		} else {
			System.out.println(platform + "平台没有[追加评论]接口");
		}
		return result;
	}
	
	/*商品评价信息列表*/
	public JSONObject goodsEvaluationList(JSONObject userdata,String goodsId,String level,String platform) {
		JSONObject params = new JSONObject();
		params.put("goodsId", goodsId);
		params.put("level", level);//评价等级 0.全部 1.非常不满意、2.不满意、3.一般、4.满意、5.非常满意
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiGet(userdata, "/tradeCenter/evaluation/v1/goodsEvaluationList", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Get(userdata, "/tradeCenter/h5/evaluation/goodsEvaluationList", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.pcGet(userdata, "/tradeCenter/web/evaluation/goodsEvaluationList", params);
		}
		else {
			System.out.println(platform+"平台没有[商品评论信息列表]接口");
		}
		return result;
	}

	/*运营中心、商家中心查看商品评价列表(未包含所有查询条件)*/
	public JSONObject orderEvaluationList(JSONObject userdata,String orderSn) {
		JSONObject params = new JSONObject();
		params.put("orderSn", orderSn);//订单号
		JSONObject result = new JSONObject();
		if (userdata.getString("userType").equals("backend")) {
			result = myAPI.sysPost(userdata, "/tradeCenter/order/admin/evaluate/list", params);
		}
		else if (userdata.getString("userType").equals("supplier")) {
			params.put("supplierId", userdata.getString("supplierId"));
			result = myAPI.sellerGet(userdata, "/userCenter/supplier/order/orderEvaluateList", params);
		}
		else {
			System.out.println(userdata.getString("userType")+"的用户没有[查看商品评论列表]接口");
		}
		return result;
	}
	
	/*商家中心回复评论*/
	public JSONObject orderEvaluateReply(JSONObject sellerUserdata,String evaluateId,String evaluateDetailId) {
		JSONObject params = new JSONObject();
		params.put("evaluateId", evaluateId);//评价ID
		params.put("evaluateDetailId", evaluateDetailId);//评价详情ID
		params.put("context", "商家回复评论（接口提交）");//回复内容
		return myAPI.sellerPost(sellerUserdata, "/userCenter/supplier/order/orderEvaluateReply", params);
	}
}
