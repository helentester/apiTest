/**
 * @author helen
 * @date 2018年11月22日
 */
package order;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import common.MyAPI;
import data.CouponData;
import data.MemberData;

/**
 * @Description:->订单相关接口 : Trade Order Controller
 * 商家中心-管理列表 : Supplier Center Controller
 * 订单管理后台接口 : Trade Order Admin Controller
 */
public class TradeOrder {
	MemberData memberData = new MemberData();
	CouponData couponData = new CouponData();
	MyAPI myAPI = new MyAPI();
	
	/*支付订单*/
	public JSONObject payOrder(JSONObject userdata,String orderId,String payPrice,String payType,String platform) {
		JSONObject params = new JSONObject();
		params.put("orderId", orderId);//订单主键 多个以,号隔开 ,
		params.put("payPrice", payPrice);//支付金额 
		params.put("payType", payType);//支付类型：app支付宝-alipay，支付宝web支付-webAlipay，支付宝H5支付-wapAlipay，app微信支付-wxin，扫码微信支付-wxinNative，微信H5支付-wxinMWEB，微信公众号支付-JSAPI ,
		params.put("userId", userdata.getString("userId"));
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/tradeCenter/order/v1/payOrder", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/tradeCenter/order/h5/payOrder", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/tradeCenter/web/order/payOrder", params);
		}
		else {
			System.out.println(platform+"平台没有[支付订单]接口");
		}
		return result;
	}
	
	//下单接口
	public JSONObject generateOrder(JSONObject userdata,String addressId,int useBalance,String balancePrice,String cartId,boolean isBuyNow,JSONArray supplierOrders,int useCoupon,String couponId,String platform) {
		JSONObject params = new JSONObject();
		params.put("addressId", addressId);//收货地址ID，如果订单为实物类订单，此参数必传 
		params.put("balancePrice", balancePrice);//(number, optional): 使用的余额总额 ,
		params.put("cartId", cartId);//购物车ID
		params.put("isBuyNow", isBuyNow);//(boolean, optional): 是否立即购买 默认:false ,
		params.put("mobile", userdata.getString("loginAccount"));//收货人
		params.put("supplierOrders", supplierOrders.toString());//(string, optional): 商家订单信息 json字符串集合 ,
		params.put("useCoupon", useCoupon); // 是否使用优惠券，0 使用，1 不使用 ,
		params.put("useBalance", useBalance);//(integer): 是否使用余额 0使用 1不使用 ,
		params.put("userId", userdata.getString("userId"));
		//如果使用优惠券，则需要传以下参数
		if (useCoupon==0) {
			params.put("couponId", couponId);//优惠券ID
			params.put("couponNumber", couponData.getCouponItem_ById(userdata.getString("userId"), couponId, "coupon_number"));//优惠券编号
		}
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/tradeCenter/order/v3/generateOrder", params, platform);
		}
		else if (platform.equals("h5")) {
			if (isBuyNow) {
				params.put("source", "cart");
			}
			else {
				params.put("source", "buy_now");
			}
			
			result = myAPI.h5Post(userdata, "/api/tradeCenter/generateOrder", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/tradeCenter/web/order/generateOrder", params);
		}
		return result;
	}
	
	// 下单接口20190116 更改为以商家纬度生成订单
	public JSONObject generateOrderNew(JSONObject userdata, String addressId, String cartId, int useCoupon,
			int useIntegration, String couponId, String platform) {
		JSONObject params = new JSONObject();
		params.put("addressId", addressId);// 收货地址ID，如果订单为实物类订单，此参数必传 ,
		params.put("cartId", cartId);// 购物车ID
		params.put("mobile", "13825464584");// 收货人
		params.put("useCoupon", useCoupon); // 是否使用优惠券，0 使用，1 不使用 ,
		params.put("useIntegration", useIntegration);// 是否使用福豆，0 使用，1 不使用 ,
		params.put("userId", userdata.getString("userId"));
		params.put("isVirtual", "0");// 是否虚拟商品 0实体 1虚拟
		params.put("remark", "接口测试生成订单");
		// 如果使用优惠券，则需要传以下参数
		if (useCoupon == 0) {
			params.put("couponId", couponId);// 优惠券ID
			params.put("couponNumber",
					couponData.getCouponItem_ById(userdata.getString("userId"), couponId, "coupon_number"));// 优惠券编号
			params.put("couponPrice", couponData.getCouponValue_ById(couponId, "discounts"));// 优惠金额
		}
		JSONObject result = new JSONObject();
		if (platform.equals("android") || platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/tradeCenter/order/v1/generateOrder", params, platform);
		} else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/api/tradeCenter/generateOrder", params);
		} else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/tradeCenter/web/order/generateOrder", params);
		}
		return result;
	}
		
	
	/*确认收货*/
	public JSONObject enterReceiveGoods(JSONObject userdata,String deliveryId,String orderId,String platform) {
		JSONObject params = new JSONObject();
		params.put("deliveryId", deliveryId);//发货单ID
		params.put("orderId", orderId);
		params.put("userId", userdata.getString("userId"));
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/tradeCenter/order/v1/enterReceiveGoods", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/tradeCenter/order/h5/enterReceiveGoods", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/tradeCenter/web/order/enterReceiveGoods", params);
		}
		else {
			System.out.println(platform+"平台没有[确认收货]接口");
		}
		return result;
	}

	/*商城、运营中心、商家中心订单详情*/
	public JSONObject orderDetail(JSONObject userdata,String orderId,String platform) {
		JSONObject params = new JSONObject();
		params.put("orderId", orderId);
		JSONObject result = new JSONObject();
		if (userdata.getString("userType").equals("member")) {//会员查看商品详情
			if (platform.equals("android")||platform.equals("ios")) {
				result = myAPI.apiGet(userdata, "/tradeCenter/order/v1/orderDetail", params, platform);
			}
			else if (platform.equals("h5")) {
				result = myAPI.h5Get(userdata, "/tradeCenter/order/h5/orderDetail", params);
			}
			else if (platform.equals("pc")) {
				result = myAPI.pcGet(userdata, "/tradeCenter/web/order/orderCenterDetail", params);
			}
			else {
				System.out.println(platform+"平台没有[订单详情]接口");
			}
		}
		else if (userdata.getString("userType").equals("supplier")) {
			result = myAPI.sellerGet(userdata, "/userCenter/supplier/order/detail", params);
		}
		else if (userdata.getString("code").equals("backend")) {
			result = myAPI.sysPost(userdata, "/tradeCenter/order/admin/detail", params);
		}
		
		return result;
	}

}
