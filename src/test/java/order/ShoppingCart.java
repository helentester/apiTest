/**
 * @author helen
 * @date 2018年10月23日
 */
package order;

import common.MyAPI;
import data.CouponData;
import data.GoodData;
import data.MemberData;
import data.ShoppingCartData;

import static org.testng.Assert.assertEquals;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @Description:shopping-cart-controller : 购物车相关接口
 */
public class ShoppingCart {
	MyAPI myAPI = new MyAPI();
	GoodData goodData = new GoodData();
	MemberData memberData = new MemberData();
	ShoppingCartData shoppingCartData = new ShoppingCartData();	
	
	/*清空购物车（综合接口）*/
	public void clearShoppingCart(JSONObject userdata,String platform) {
		//取得所有购物车ID
		JSONArray cartIdList = this.getShoppingCartIdList(userdata, platform);

		// 删除购物车
		for (int i = 0; i < cartIdList.size(); i++) {
			JSONObject deleteResult = this.deleteShoppingCart(userdata, cartIdList.getString(i), platform);
			assertEquals(deleteResult.getString("code"), "10000", "删除购物车"+deleteResult);
		}
	}
	
	/*获取所有购物车的ID*/
	public JSONArray getShoppingCartIdList(JSONObject userdata,String platform) {
		JSONObject selectResult = this.selectShoppingCart(userdata, platform);
		assertEquals(selectResult.getString("code"), "10000", "查询购物车" + selectResult);
		JSONArray cartList = selectResult.getJSONObject("data").getJSONArray("cartList");// 所有购物车列表
		JSONArray cartIdList = new JSONArray();
		for (int i = 0; i < cartList.size(); i++) {
			JSONArray supplier_cartList = cartList.getJSONObject(i).getJSONArray("cart");// 获取商家相关的购物车
			for (int j = 0; j < supplier_cartList.size(); j++) {
				cartIdList.add(supplier_cartList.getJSONObject(j).getString("cartId"));
			}
		}
		return cartIdList;
	}

	/*提交购物车
	 * @source 提交购物车类型：cart 通过购物车提交，buy_now 立即购买
	 * @useCoupon 是否使用优惠券，0 使用，1 不使用
	 * @useIntegration 是否使用福豆，0 使用，1 不使用 ,
	 */
	public JSONObject submitShoppingCart(JSONObject userdata,String addressId, String cartId, JSONArray supplierOrders,
			int useCoupon,String couponId,int useBalance,String balancePrice, String platform) {
		JSONObject params = new JSONObject();
		params.put("cartId", cartId);// 购物车ID 多个用“,”隔开
		params.put("supplierOrders", supplierOrders.toString());// 商家订单信息
		params.put("useCoupon", useCoupon); // 是否使用优惠券，0 使用，1 不使用 ,
		params.put("useBalance", useBalance);// 是否使用余额支付 0使用 1不使用 ,
		params.put("addressId", addressId);// 收货地址
		params.put("balancePrice", balancePrice);// 使用支付的余额
		
		if (useCoupon==0) {
			CouponData couponData = new CouponData();
			String couponNumber = couponData.getCouponItem_ById(userdata.getString("userId"), couponId, "coupon_number");
			params.put("couponId", couponId);// 优惠券ID
			params.put("couponNumber", couponNumber);// 优惠券编码
		}

		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/tradeCenter/order/v3/submitShoppingCart", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/api/tradeCenter/submitShoppingCart", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/tradeCenter/web/order/submitShoppingCart", params);
		}
		else {
			System.out.println(platform+"平台没有[提交购物车]接口");
		}
		
		return result;
	}

	/* 删除购物车 */
	public JSONObject deleteShoppingCart(JSONObject userdata, String cartId,String platform) {
		JSONObject params = new JSONObject();
		params.put("id", cartId);
		params.put("userId", userdata.getString("userId"));
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiDelete(userdata, "/tradeCenter/order/v1/deleteShoppingCart", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Delete(userdata, "/api/tradeCenter/deleteShoppingCart",params);
		}
		else if (platform.equals("pc")) {
			params.remove("userId");//PC接口不需要传userId
			result = myAPI.pcDelete(userdata, "/tradeCenter/web/order/deleteShoppingCart", params);
		}
		else {
			System.out.println(platform+"平台没有[删除购物车]接口");
		}
		
		return result;
	}

	/*
	 * 修改购物车
	 * 
	 * @type plus 增加 minus 减少
	 */
	public JSONObject updateShoppingCart(JSONObject userdata, String cartId, String number, String type,String platform) {
		JSONObject params = new JSONObject();
		params.put("cartId", cartId);
		params.put("goodsId", shoppingCartData.getCartValue_byId(cartId, "goods_id"));
		params.put("number", number);
		params.put("type", type);// plus 增加 minus 减少
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/tradeCenter/order/v1/updateShoppingCart", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/api/tradeCenter/updateShoppingCart", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/tradeCenter/web/order/updateShoppingCart", params);
		}
		else {
			System.out.println(platform+"平台没有[修改购物车]接口");
		}
		
		return result;
	}

	/* 查询购物车 */
	public JSONObject selectShoppingCart(JSONObject userdata,String platform) {
		JSONObject params = new JSONObject();
		params.put("userId", userdata.getString("userId"));
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiGet(userdata, "/tradeCenter/order/v1/selectShoppingCart", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Get(userdata, "/api/tradeCenter/selectShoppingCart",params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.pcGet(userdata, "/tradeCenter/web/order/selectShoppingCart", params);
		}
		else {
			System.out.println(platform+"平台没有[查询购物车]接口");
		}
		return result;
	}

	/* 加入购物车 */
	public JSONObject addShoppingCart(JSONObject userdata, String skuNumber, String number,String platform) {
		JSONObject params = new JSONObject();
		params.put("goodsId", goodData.getSKUValue_bySkuNb(skuNumber, "goods_id"));
		params.put("number", number);
		params.put("skuNumber", skuNumber);
		params.put("userId", userdata.getString("userId"));
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/tradeCenter/order/v1/addShoppingCart", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/api/tradeCenter/addShoppingCart", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/tradeCenter/web/order/addShoppingCart", params);
		}
		else {
			System.out.println(platform+"平台没有[加入购物车]接口");
		}
		
		return result;
	}
}
