/**
 * @author helen
 * @date 2018年11月9日
 */
package testcase;

import static org.testng.Assert.assertEquals;

import org.testng.Reporter;
import org.testng.annotations.Test;
import common.MyConfig;
import data.CouponData;
import data.EvaluateData;
import data.MemberData;
import data.ShoppingCartData;
import dataProvider.UserDataProvider;
import marketing.CouponUse;
import order.GoodsEvaluateAdmin;
import order.ShoppingCart;
import order.TradeEvaluate;
import order.TradeInvoiceAdmin;
import order.TradeOrder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import user.Login;
import user.MemberDeliveryAddress;

/**
 * @Description:-> 订单相关接口 : Web Trade Order Controller
 */
public class OrderTest {
	MyConfig myConfig = new MyConfig();
	CouponData couponData = new CouponData();
	ShoppingCartData shoppingCartData = new ShoppingCartData();
	MemberData memberData = new MemberData();
	EvaluateData evaluateData = new EvaluateData();
	Login login = new Login();
	MemberDeliveryAddress memberDeliveryAddress = new MemberDeliveryAddress();
	CouponUse coupon = new CouponUse();
	ShoppingCart shoppingCart = new ShoppingCart();
	TradeInvoiceAdmin tradeInvoiceAdmin = new TradeInvoiceAdmin();
	TradeEvaluate tradeEvaluate = new TradeEvaluate();
	GoodsEvaluateAdmin goodsEvaluateAdmin = new GoodsEvaluateAdmin();
	TradeOrder tradeOrder = new TradeOrder();

	@Test(dataProvider = "platform", dataProviderClass = UserDataProvider.class)
	public void test_shoppingCart(String platform) {
		Reporter.log("用户登录－加入购物车（多个）－查询购物车－删除购物车（1个）－修改购物车－提交购物车");
		
		String username=memberData.get_account("phone");
		Reporter.log("1、登录");
		JSONObject loginResult = login.login(username, "123456li", platform);
		assertEquals(loginResult.getString("code"), "10000","登录校验");
		JSONObject userdata = loginResult.getJSONObject("data");

		Reporter.log("清空用户当前的购物车");
		shoppingCart.clearShoppingCart(userdata, platform);
		
		Reporter.log("2、SKU1加入购物车");
		JSONObject addShoppingCartResult1 = shoppingCart.addShoppingCart(userdata, myConfig.getKeys("sku1"), "3", platform);
		assertEquals(addShoppingCartResult1.getString("code"), "10000","第一个SKU加入购物车校验"+addShoppingCartResult1);
		String cart1 = addShoppingCartResult1.getJSONObject("data").getString("cartId");
		
		Reporter.log("3、SKU2加入购物车");
		JSONObject addShoppingCartResult2 = shoppingCart.addShoppingCart(userdata, myConfig.getKeys("sku2"), "3", platform);
		assertEquals(addShoppingCartResult2.getString("code"), "10000","第一个SKU加入购物车校验"+addShoppingCartResult2);
		String cart2 = addShoppingCartResult2.getJSONObject("data").getString("cartId");
		
		Reporter.log("4、SKU3加入购物车");
		JSONObject addShoppingCartResult3 = shoppingCart.addShoppingCart(userdata, myConfig.getKeys("sku3"), "3", platform);
		assertEquals(addShoppingCartResult3.getString("code"), "10000","第一个SKU加入购物车校验"+addShoppingCartResult3);
		String cart3 = addShoppingCartResult3.getJSONObject("data").getString("cartId");
		
		Reporter.log("5、SKU4加入购物车");
		JSONObject addShoppingCartResult4 = shoppingCart.addShoppingCart(userdata, myConfig.getKeys("sku4"), "3", platform);
		assertEquals(addShoppingCartResult4.getString("code"), "10000","第一个SKU加入购物车校验"+addShoppingCartResult4);
		String cart4 = addShoppingCartResult4.getJSONObject("data").getString("cartId");
		
		Reporter.log("6、查询购物车");
		JSONObject selectShoppingCartResult = shoppingCart.selectShoppingCart(userdata,platform);
		assertEquals(selectShoppingCartResult.getString("code"), "10000","查询购物车校验"+selectShoppingCartResult);
		assertEquals(selectShoppingCartResult.getJSONObject("data").getString("count"), "4","用户名下购物车数量校验");
		
		Reporter.log("7、删除第一第二个购物车（sku1、sku2）");
		JSONObject deleteShoppingCartResult = shoppingCart.deleteShoppingCart(userdata, cart1+","+cart2,platform);
		assertEquals(deleteShoppingCartResult.getString("code"), "10000","删除购物车"+deleteShoppingCartResult);
		assertEquals(shoppingCartData.getCartValue_byId(cart1, "is_delete"), "true","校验购物车的删除状态");
		assertEquals(shoppingCartData.getCartValue_byId(cart2, "is_delete"), "true","校验购物车的删除状态");
		
		Reporter.log("8、修改购物车（sku3加2，sku4减1）");
		JSONObject updateShoppingCart3Result = shoppingCart.updateShoppingCart(userdata, cart3, "2", "plus", platform);
		assertEquals(updateShoppingCart3Result.getString("code"), "10000","修改购物车（加）"+updateShoppingCart3Result);
		assertEquals(shoppingCartData.getCartValue_byId(cart3, "number"), "5","购物车当前数量较验");
		JSONObject updateShoppingCart4Result = shoppingCart.updateShoppingCart(userdata, cart4, "1", "minus", platform);
		assertEquals(updateShoppingCart4Result.getString("code"), "10000","修改购物车（减）"+updateShoppingCart4Result);
		assertEquals(shoppingCartData.getCartValue_byId(cart4, "number"), "2","购物车当前数量较验");
		
		String addressId = memberDeliveryAddress.getAddrId(userdata, platform);
		Reporter.log("5）获取会员的收货地址ID："+addressId);
		
		/*Reporter.log("9、提交购物车（cart3、cart4）");
		JSONObject submitShoppingCartResult = shoppingCart.submitShoppingCart(userdata,addressId, cart3+","+cart4, "1", "1",platform);
		assertEquals(submitShoppingCartResult.getString("code"), "10000","提交购物车"+submitShoppingCartResult);*/
		
	}
	
	@Test(dataProvider="platform",dataProviderClass=UserDataProvider.class)
	public void test_useCouponAndBean(String platform) {
		Reporter.log("主要业务流测试：使用优惠券+福豆+现金购买普通商品，并且进行评论（图片）与追评（视频）");
		
		//String platform = "h5";

		Reporter.log("[会员操作]-----");
		String username = memberData.get_account("phone");
		Reporter.log("1）登录商城"+username);
		JSONObject loginResult = login.login(username, "123456li", platform);
		assertEquals(loginResult.getString("code"), "10000","登录校验"+loginResult);
		JSONObject userdata = loginResult.getJSONObject("data");
		
		Reporter.log("清空用户当前的购物车");
		shoppingCart.clearShoppingCart(userdata, platform);
		
		Reporter.log("2）获取一张150元的满减券(优先抵扣福豆)");
		String couponId = coupon.getCoupon(userdata, 150, platform);//获得优惠券

		Reporter.log("3）数据库操作：把会员的可用福豆设置为100");
		memberData.update_ableScore(userdata.getString("loginAccount"), "100");
		
		String sku=myConfig.getKeys("sku5");
		Reporter.log("4）把一个[500现金+100福豆]的普通商品SKU（"+sku+"）加入购物车，数量为2");
		JSONObject addCartResult = shoppingCart.addShoppingCart(userdata,sku , "2", platform);
		assertEquals(addCartResult.getString("code"), "10000","加入购物车校验");
		String cartId = addCartResult.getJSONObject("data").getString("cartId");
		
		String addressId = memberDeliveryAddress.getAddrId(userdata, platform);
		Reporter.log("5）获取会员的收货地址ID："+addressId);
		
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
			supplierOrder.put("useIntegration", 0);//(integer, optional): 是否使用福豆，0 使用，1 不使用 默认 0
			supplierOrderList.add(supplierOrder);
		}
		
		JSONObject submitShoppingCartResult = shoppingCart.submitShoppingCart(userdata, addressId, cartId, supplierOrderList, 1, couponId, 1, "0", platform);
		assertEquals(submitShoppingCartResult.getString("code"), "10000","提交购物车"+submitShoppingCartResult);
		
		Reporter.log("6）生成订单");
		JSONObject generateOrderResult = tradeOrder.generateOrder(userdata, addressId,1, "0", cartId, false, supplierOrderList, 0, couponId, platform);
		assertEquals(generateOrderResult.getString("code"), "10000","生成订单"+generateOrderResult);
		JSONObject orderData = generateOrderResult.getJSONObject("data");
		
		Reporter.log("7）查询订单详情");
		JSONObject orderDetailResult = tradeOrder.orderDetail(userdata, orderData.getJSONArray("orderIds").getString(0), platform);
		assertEquals(orderDetailResult.getString("code"), "10000","校验查询订单详情是否成功"+orderDetailResult);
		String item_name = "orderItems";//移动端接口返回的名称
		if (platform.equals("pc")) {
			item_name="goodsItem";//PC端接口返回的名称
		}
		JSONObject orderItem = orderDetailResult.getJSONObject("data").getJSONArray(item_name).getJSONObject(0);
		
		Reporter.log("8）模拟使用[app支付宝]支付订单："+orderData.getString("orderNos"));
		JSONObject payResult = tradeOrder.payOrder(userdata, orderData.getJSONArray("orderIds").getString(0), orderData.getString("payTotal"), "alipay", platform);
		assertEquals(payResult.getString("code"), "95270","校验模拟支付是否成功"+payResult);
		assertEquals(Double.valueOf(memberData.getScore_userId(userdata.getString("uid"), "abled_score")), Double.valueOf("50"),"校验福豆使用");
		assertEquals(couponData.getCouponItem_ById(userdata.getString("uid"), couponId, "use_status"), "1","校验优惠券的使用");
		
		Reporter.log("[运营中心操作]------");
		JSONObject sysLoginResult = login.sysLogin("helen", "123456li");
		assertEquals(sysLoginResult.getString("code"), "10000","校验运营中心是否登录成功"+sysLoginResult);
		JSONObject sysUserData = sysLoginResult.getJSONObject("data");
		
		Reporter.log("1）运营中心确认发货");
		JSONObject deliverySendResult = tradeInvoiceAdmin.send(sysUserData, orderData.getJSONArray("orderIds").getString(0));
		assertEquals(deliverySendResult.getString("code"), "10000","校验后台是否发货成功"+deliverySendResult);
		
		Reporter.log("2）运营中心按[订单号]查询运单");
		JSONObject invoiceListResult = tradeInvoiceAdmin.invoiceList(sysUserData, orderData.getJSONArray("orderNos").getString(0));
		assertEquals(invoiceListResult.getString("code"), "10000","校验查询运单号是否成功"+invoiceListResult);
		String invoiceId = invoiceListResult.getJSONObject("data").getJSONArray("list").getJSONObject(0).getString("id");
		
		Reporter.log("[会员操作]-----");
		Reporter.log("1）确认收货");
		JSONObject enterReceiveGoodsResult = tradeOrder.enterReceiveGoods(userdata, invoiceId, orderData.getJSONArray("orderIds").getString(0), platform);
		assertEquals(enterReceiveGoodsResult.getString("code"), "10000","校验确认收货是否成功"+enterReceiveGoodsResult);
		
		Reporter.log("2）评论商品:图片评论");
		JSONObject evaluateResult = tradeEvaluate.evaluateOrderItem(userdata, orderData.getJSONArray("orderNos").getString(0),orderItem.getString("id"), "0", platform);
		assertEquals(evaluateResult.getString("code"), "10000","校验是否评论成功"+evaluateResult);
		
		Reporter.log("[运营中心操作]------");
		Reporter.log("1）按[订单号]查询该评论");
		JSONObject evaluateListResult = goodsEvaluateAdmin.evaluateList(sysUserData, orderData.getJSONArray("orderNos").getString(0));
		assertEquals(evaluateListResult.getString("code"), "10000","校验查询评论是否成功"+evaluateListResult);
		String evaluateId = evaluateListResult.getJSONObject("data").getJSONArray("list").getJSONObject(0).getString("id");
		String evaluateDetailId = evaluateData.getEvaluateDetail_evaluateId(evaluateId, "evaluate_detail_id");
		
		Reporter.log("2）审核评论：审核通过");
		JSONObject auditShowResult = goodsEvaluateAdmin.auditShow(sysUserData, evaluateDetailId, "1");
		assertEquals(auditShowResult.getString("code"), "10000","验证评论审核是否成功"+auditShowResult);
		
		Reporter.log("[会员操作]-----");
		Reporter.log("1）追加评论:视频评论");
		JSONObject evaluateAppendOrderItemResult = tradeEvaluate.evaluateAppendOrderItem(userdata,orderItem.getString("id"), "1", platform);
		assertEquals(evaluateAppendOrderItemResult.getString("code"), "10000","校验追评是否成功"+evaluateAppendOrderItemResult);

		Reporter.log("[运营中心操作]------");
		Reporter.log("1）查询该订单的评论详情");
		JSONObject evaluateDetailResult = goodsEvaluateAdmin.evaluateDetail(sysUserData, orderData.getJSONArray("orderIds").getString(0), orderItem.getString("id"));
		assertEquals(evaluateDetailResult.getString("code"), "10000","校验运营中心查看订单的评论详情是否成功"+evaluateDetailResult);
		String appendEvaluateId = evaluateDetailResult.getJSONObject("data").getJSONArray("orderItemEvaluateDetail").getJSONObject(1).getString("evaluateDetailId");
		
		Reporter.log("2）审核追评：审核不通过");
		JSONObject auditAppendShowResul = goodsEvaluateAdmin.auditShow(sysUserData, appendEvaluateId, "0");
		assertEquals(auditAppendShowResul.getString("code"), "10000","校验审核追评是否成功"+auditAppendShowResul);
		
		Reporter.log("[会员操作]-----");
		Reporter.log("1）查询商品评论：全部");
		JSONObject goodsEvaluationListResult = tradeEvaluate.goodsEvaluationList(sysUserData, orderItem.getString("goodsId"),"0", platform);
		assertEquals(goodsEvaluationListResult.getString("code"), "10000","校验查看商品的评论列表是否成功");
		
	}
	
	@Test(dataProvider="platform",dataProviderClass=UserDataProvider.class)
	public void test_payCash(String platform) {
		Reporter.log("仅使用现金支付单个[现金+福豆]商品");
		//String platform="h5";

		Reporter.log("[会员操作]-----");
		String username = memberData.get_account("phone");
		Reporter.log("1）登录商城"+username);
		JSONObject loginResult = login.login(username, "123456li", platform);
		assertEquals(loginResult.getString("code"), "10000","登录校验"+loginResult);
		JSONObject userdata = loginResult.getJSONObject("data");
		
		Reporter.log("2）清空用户当前的购物车，以免影响测试");
		shoppingCart.clearShoppingCart(userdata, platform);
		
		String sku=myConfig.getKeys("sku5");
		Reporter.log("3）把一个[500现金+100福豆]的普通商品SKU（"+sku+"）加入购物车，数量为2");
		JSONObject addCartResult = shoppingCart.addShoppingCart(userdata,sku , "2", platform);
		assertEquals(addCartResult.getString("code"), "10000","加入购物车校验");
		String cartId = addCartResult.getJSONObject("data").getString("cartId");
		
		String addressId = "";
		JSONObject defaultAddrResult = memberDeliveryAddress.memberGetDefaultDeliveryAddr(userdata, platform);
		assertEquals(defaultAddrResult.getString("code"), "10000","获取会员默认地址"+defaultAddrResult);
		if (defaultAddrResult.getJSONObject("data")==null) {
			addressId = memberDeliveryAddress.getAddrId(userdata, platform);
		}
		else {
			addressId = defaultAddrResult.getJSONObject("data").getString("addressId");
		}
		Reporter.log("4）获取会员的收货地址ID："+addressId);
		
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
			supplierOrder.put("useIntegration", 1);//(integer, optional): 是否使用福豆，0 使用，1 不使用 默认 0
			supplierOrderList.add(supplierOrder);
		}
		
		JSONObject submitShoppingCartResult = shoppingCart.submitShoppingCart(userdata, addressId, cartId, supplierOrderList, 1, "", 1, "", platform);
		assertEquals(submitShoppingCartResult.getString("code"), "10000","提交购物车"+submitShoppingCartResult);
		
		Reporter.log("7）生成订单");
		JSONObject generateOrderResult = tradeOrder.generateOrder(userdata, addressId,1, "0", cartId, false, supplierOrderList, 1, "", platform);
		assertEquals(generateOrderResult.getString("code"), "10000","生成订单"+generateOrderResult);
		JSONObject orderData = generateOrderResult.getJSONObject("data");
		
		Reporter.log("8）查询订单详情");
		JSONObject orderDetailResult = tradeOrder.orderDetail(userdata, orderData.getJSONArray("orderIds").getString(0), platform);
		assertEquals(orderDetailResult.getString("code"), "10000","校验查询订单详情是否成功"+orderDetailResult);
		JSONObject orderDetail = orderDetailResult.getJSONObject("data");
		assertEquals(orderDetail.getString("goodsTotalPrice"), "1200.00","商品总价校验");
		assertEquals(orderDetail.getString("shipPrice"), "0.02","运费校验");
		assertEquals(orderDetail.getString("payPrice"), "1200.02","应支付金额校验");
		assertEquals(orderDetail.getString("orderStatus"), "0","订单状态校验");//订单状态 -1 已取消（关闭） 0未支付(待付款) 1 已超时 2 支付中 3支付成功（待发货）4已发货（待收货）5确认收货 6完结 ,
		
		Reporter.log("9）模拟使用[app支付宝]支付订单："+orderData.getString("orderNos"));
		JSONObject payResult = tradeOrder.payOrder(userdata, orderData.getJSONArray("orderIds").getString(0), orderData.getString("payTotal"), "alipay", platform);
		assertEquals(payResult.getString("code"), "95270","校验模拟支付是否成功"+payResult);
		
		try {
			Thread.sleep(500);
			Reporter.log("10）再次查询订单详情，检查订单支付状态");
			JSONObject orderDetailResult2 = tradeOrder.orderDetail(userdata, orderData.getJSONArray("orderIds").getString(0), platform);
			assertEquals(orderDetailResult2.getString("code"), "10000","校验查询订单详情是否成功"+orderDetailResult2);
			assertEquals(orderDetailResult2.getJSONObject("data").getString("orderStatus"), "3","订单状态校验");//订单状态 -1 已取消（关闭） 0未支付(待付款) 1 已超时 2 支付中 3支付成功（待发货）4已发货（待收货）5确认收货 6完结 ,
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
}
