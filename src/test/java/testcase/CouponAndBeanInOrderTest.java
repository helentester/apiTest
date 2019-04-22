/**
 * @author helen
 * @date 2019年1月15日
 */
package testcase;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

import org.testng.Reporter;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import common.BaseData;
import common.MyConfig;
import data.MemberData;
import data.OrderData;
import dataProvider.UserDataProvider;
import marketing.CouponUse;
import order.ShoppingCart;
import order.TradeOrder;
import user.Login;
import user.MemberDeliveryAddress;

/**
 * @Description:福豆优惠券分摊测试
 */
public class CouponAndBeanInOrderTest {
	MyConfig myConfig = new MyConfig();
	BaseData baseData = new BaseData();
	MemberData memberData = new MemberData();
	OrderData orderData = new OrderData();
	Login login = new Login();
	ShoppingCart shoppingCart = new ShoppingCart();
	MemberDeliveryAddress memberDeliveryAddress = new MemberDeliveryAddress();
	CouponUse couponUse = new CouponUse();
	TradeOrder tradeOrder = new TradeOrder();
	
	@Test(dataProvider="oneGoodsShare",dataProviderClass = UserDataProvider.class)
	public void oneGoodShare(String log, String sku, String price, String coupon, String bean, String usedCoupon,
			String usedBean, String money) {
		Reporter.log("购买一个商品，优惠券（优先抵扣福豆）分摊测试");
		String platform = "ios";
		String username = memberData.get_account("phone");
		Reporter.log("下单平台为："+platform);
				
		//用于测试商品数据（237与236数据一至）
		JSONObject goods = new JSONObject();
		goods.put("goodsId", "7842");
		goods.put("sku1", "201809200000000015");// 11现金额
		goods.put("sku2", "201809200000000016");// 99福豆
		goods.put("sku3", "201809200000000017");// 500现金+100福豆

		Reporter.log("1）会员登录商城："+username);
		JSONObject loginResult = login.login(username, "123456li", platform);
		assertEquals(loginResult.getString("code"), "10000","会员登录商城"+loginResult);
		JSONObject userdata = loginResult.getJSONObject("data");
		
		Reporter.log("2）给会员设置优惠券：面值为："+coupon);
		String couponId = "";
		int hadCoupon = 1;//不使用优惠券
		if (Integer.valueOf(usedCoupon)>0) {//根据设置使用优惠券的值，判断是否给用户设置优惠券。（如果为0则不设置优惠券）
			couponId = couponUse.getCoupon(userdata, Integer.valueOf(coupon), platform);
			hadCoupon = 0;//使用优惠券
		}
		
		Reporter.log("3）修改会员的可用福豆为："+usedBean);// 
		memberData.update_ableScore(username, usedBean);
		int hadBean = 1;//不使用福豆
		if (Double.valueOf(usedBean)>0) {
			hadBean = 0;//使用福豆
		}
		
		Reporter.log("4）清空会员的购物车，以免影响测试结果");
		shoppingCart.clearShoppingCart(userdata, platform);
		
		Reporter.log("5）把sku加入购物车,skuNumber:"+sku);
		JSONObject addCartResult = shoppingCart.addShoppingCart(userdata,goods.getString(sku) , "1", platform);
		assertEquals(addCartResult.getString("code"), "10000","加入购物车校验"+addCartResult);
		String cartId = addCartResult.getJSONObject("data").getString("cartId");
		
		String addressId = memberDeliveryAddress.getAddrId(userdata, platform);
		Reporter.log("6）获取会员的收货地址ID："+addressId);
		
		Reporter.log("5）查询购物车列表");
		JSONObject selectShoppingCartResult = shoppingCart.selectShoppingCart(userdata, platform);
		assertEquals(selectShoppingCartResult.getString("code"), "10000","查询购物车"+selectShoppingCartResult);
		JSONArray cartList = selectShoppingCartResult.getJSONObject("data").getJSONArray("cartList");
		
		Reporter.log("6）提交购物车");
		JSONArray supplierOrderList = new JSONArray();
		for (int i = 0; i < cartList.size(); i++) {
			JSONArray cartDetailList = cartList.getJSONObject(i).getJSONArray("cart");
			JSONObject supplierOrder = new JSONObject();
			supplierOrder.put("carId", cartDetailList.getJSONObject(0).getString("cartId"));
			supplierOrder.put("supplierId", cartList.getJSONObject(i).getString("supplierId"));//商家ID
			supplierOrder.put("isJoinSale", true);//是否参加活动 (满减/择活动时选择禁用=false 其它=true) 默认true ,
			supplierOrder.put("deliveryTime", 0);//送货时间 默认为0 ,
			supplierOrder.put("saleType", "0");//(integer, optional): 活动类型 0 普通 1 闪购 2 满减/折(商家只要有满减/折活动就传2) 默认0 ,
			supplierOrder.put("useIntegration", hadBean);//(integer, optional): 是否使用福豆，0 使用，1 不使用 默认 0
			supplierOrderList.add(supplierOrder);
		}
		
		JSONObject submitShoppingCartResult = shoppingCart.submitShoppingCart(userdata, addressId, cartId, supplierOrderList, hadCoupon, couponId, 1, "", platform);
		assertEquals(submitShoppingCartResult.getString("code"), "10000","提交购物车"+submitShoppingCartResult);
		
		Reporter.log("7）生成订单");
		JSONObject generateOrderResult = tradeOrder.generateOrder(userdata, addressId,1, "0", cartId, false, supplierOrderList, hadCoupon, couponId, platform);
		assertEquals(generateOrderResult.getString("code"), "10000","生成订单"+generateOrderResult);
		JSONObject orderJson = generateOrderResult.getJSONObject("data");
		String orderSN = orderJson.getJSONArray("orderNos").getString(0);
		
		Reporter.log("8）模拟使用[app支付宝]支付订单："+orderSN);
		JSONObject payResult = tradeOrder.payOrder(userdata, orderJson.getJSONArray("orderIds").getString(0), orderJson.getString("payTotal"), "alipay", platform);
		assertEquals(payResult.getString("code"), "95270","校验模拟支付是否成功"+payResult);

		//String orderSN = "1901151557478440";
		Reporter.log("9）校验订单表中分摊");// 
		JSONObject ShareInOrder = orderData.get_PayShare(orderSN);
		assertNotEquals(ShareInOrder.getString("resulSize"), "0", "检查是否生成订单分摊信息"+ShareInOrder);
		assertEquals(Double.valueOf(ShareInOrder.getString("score")), Double.valueOf(usedBean), "福豆分摊校验，订单号：" + orderSN);
		assertEquals(Double.valueOf(ShareInOrder.getString("coupon")), Double.valueOf(usedCoupon), "优惠券分摊校验，订单号：" + orderSN);
		assertEquals(Double.valueOf(ShareInOrder.getString("pay_price")), Double.valueOf(money) + 0.01,
				"现金分摊校验，订单号：" + orderSN);// 现金支付部份包含运费0.01

		Reporter.log("10）校验订单统计表中分摊");// 订单统计表中分摊
		try {
			Thread.sleep(1000);
			JSONObject ShareInStatic = orderData.get_statisticsPayShare(orderSN);
			assertNotEquals(ShareInStatic.get("resulSize"), "0", "检查是否生成订单统计表中的分摊信息");
			assertEquals(Double.valueOf(ShareInStatic.getString("score")), Double.valueOf(usedBean), "福豆，订单号：" + orderSN);
			assertEquals(Double.valueOf(ShareInStatic.getString("coupon")), Double.valueOf(usedCoupon), "优惠券，订单号：" + orderSN);
			assertEquals(Double.valueOf(ShareInStatic.getString("pay_price")), Double.valueOf(money) + 0.01, "现金，订单号：" + orderSN);// 现金支付部份包含运费0.01
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
