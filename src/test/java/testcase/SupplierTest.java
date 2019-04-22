/**
 * @author helen
 * @date 2018年11月26日
 */
package testcase;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

import java.util.ArrayList;

import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import common.BaseData;
import common.MyConfig;
import data.EvaluateData;
import data.FreightData;
import data.GoodData;
import data.SellerData;
import data.SellerPermissionData;
import dataProvider.UserDataProvider;
import good.GoodsAdmin;
import good.GoodsSKU;
import order.ShoppingCart;
import order.TradeEvaluate;
import order.TradeInvoiceAdmin;
import order.TradeOrder;
import order.TradeRefund;
import seller.FreightTemplate;
import seller.SupplierAddress;
import seller.SupplierRole;
import seller.SupplierStaff;
import user.FundApply;
import user.Login;
import user.MemberDeliveryAddress;


/**
 * @Description:商家中心测试
 */
public class SupplierTest {
	MyConfig myConfig = new MyConfig();
	BaseData baseData = new BaseData();
	SellerData sellerData = new SellerData();
	GoodData goodData = new GoodData();
	SellerPermissionData sellerPermissionData = new SellerPermissionData();
	FreightData freightData = new FreightData();
	Login login = new Login();
	SupplierRole supplierRole = new SupplierRole();
	SupplierStaff supplierStaff = new SupplierStaff();
	FreightTemplate freightTemplate = new FreightTemplate();
	MemberDeliveryAddress memberDeliveryAddress = new MemberDeliveryAddress();
	ShoppingCart shoppingCart = new ShoppingCart();
	String platform = "android";
	String username = myConfig.getKeys("member1");//会员账号
	String seller;//商家账号
	String staff;
	String templateId;
	String goodsId;//用于购买的商品ID
	
	@BeforeClass
	public void test_getData() {
		seller = sellerData.getLastAccount();//"18907753412"
	}
	
	@Test(enabled=false,dataProvider="236sellers",dataProviderClass=UserDataProvider.class)
	public void test_236insertFreightTemplate(String account) {
		Reporter.log("为分润业务中的商家添加全国模板");
		Reporter.log("1）登录商家中心："+account);
		JSONObject loginResult = login.sellerLogin(account, "123456li");
		assertEquals(loginResult.getString("code"), "10000","商家中心登录校验"+loginResult);
		JSONObject sellerUserdata = loginResult.getJSONObject("data");
		Reporter.log("添加全国模板");
		JSONObject templateAddResult = freightTemplate.templateAdd(sellerUserdata);//添加一个全国模板
		assertEquals(templateAddResult.getString("code"), "10000","添加全国模板校验"+templateAddResult);
	}
	
	@Test(enabled=false,dataProvider="237sellers",dataProviderClass=UserDataProvider.class)
	public void test_237insertFreightTemplate(String account) {
		Reporter.log("为分润业务中的商家添加全国模板");
		Reporter.log("1）登录商家中心："+account);
		JSONObject loginResult = login.sellerLogin(account, "123456li");
		assertEquals(loginResult.getString("code"), "10000","商家中心登录校验"+loginResult);
		JSONObject sellerUserdata = loginResult.getJSONObject("data");
		Reporter.log("添加全国模板");
		JSONObject templateAddResult = freightTemplate.templateAdd(sellerUserdata);//添加一个全国模板
		assertEquals(templateAddResult.getString("code"), "10000","添加全国模板校验"+templateAddResult);
	}
	
	@Test(priority=1)
	public void test_roleAndStaff() {
		Reporter.log("角色及员工管理业务测试");

		Reporter.log("1）登录商家中心："+seller);
		JSONObject loginResult = login.sellerLogin(seller, "123456li");
		assertEquals(loginResult.getString("code"), "10000","商家中心登录校验"+loginResult);
		JSONObject sellerUserdata = loginResult.getJSONObject("data");

		String roleName = "超级角色"+baseData.getNum(0, 99999);//角色名称
		Reporter.log("2）新增一个含有所有权限的角色："+roleName);
		//数据库中获取所有权限
		JSONArray permissionIdsArray = sellerPermissionData.getAllPermission();
		ArrayList<Integer> permissionIds = new ArrayList<Integer>();
		for (int i = 0; i < permissionIdsArray.size(); i++) {
			permissionIds.add(permissionIdsArray.getJSONObject(i).getInteger("permission_id"));
		}
		JSONObject addSupplierRoleResult = supplierRole.addSupplierRole(sellerUserdata, roleName, permissionIds);
		assertEquals(addSupplierRoleResult.getString("code"), "10000","新增角色校验"+addSupplierRoleResult);
		
		Reporter.log("3）查询商家下的所有角色");
		JSONObject supplierRoleListResult = supplierRole.supplierRoleList(sellerUserdata);
		assertEquals(supplierRoleListResult.getString("code"), "10000","检查商家角色列表查询"+supplierRoleListResult);
		String roleId = supplierRoleListResult.getJSONObject("data").getJSONArray("list").getJSONObject(0).getString("roleId");//最添加的角色
		
		String mobile = baseData.getPhoneNumber();
		Reporter.log("4）新增这角色下在的员工："+mobile);
		JSONObject addStaffResult = supplierStaff.addStaff(sellerUserdata, mobile, roleId);
		assertEquals(addStaffResult.getString("code"), "10000","校验新增员工"+addStaffResult);
	}
	
	@Test(priority=2)
	public void test_FreightTemplate() {
		Reporter.log("运费模板设置");
		
		Reporter.log("1）登录商家中心："+seller);
		JSONObject loginResult = login.sellerLogin(seller, "123456li");
		assertEquals(loginResult.getString("code"), "10000","登录校验"+loginResult);
		JSONObject sellerUserdata = loginResult.getJSONObject("data");

		Reporter.log("添加全国模板");
		JSONObject templateAddResult = freightTemplate.templateAdd(sellerUserdata);//添加一个全国模板
		assertEquals(templateAddResult.getString("code"), "10000","添加全国模板校验"+templateAddResult);
		
		Reporter.log("2）查询商家的所有运费模板");
		JSONObject templateListResult = freightTemplate.templateList(sellerUserdata);
		assertEquals(templateListResult.getString("code"), "10000","商家运费模板列表查询校验"+templateListResult);
		JSONArray templateList = templateListResult.getJSONObject("data").getJSONArray("list");
		templateId = templateList.getJSONObject(templateList.size()-1).getString("templateId");
		
		Reporter.log("3）查询模板详情："+templateId);
		JSONObject templateDetailResult = freightTemplate.templateDetail(sellerUserdata, templateId);
		assertEquals(templateDetailResult.getString("code"), "10000","运费详情校验"+templateDetailResult);
		JSONArray templateDetailList = templateDetailResult.getJSONObject("data").getJSONArray("templateDetailList");
		
		Reporter.log("4）修改运费模板（加入海外）"+templateId);
		JSONObject overseas = new JSONObject();
		overseas.put("distributionTitle", "海外");
		overseas.put("distributionArea", "999999");
		overseas.put("firstFee", "30");//首件
		overseas.put("firstPiece", "1");
		overseas.put("renewFee", "15");
		overseas.put("renewPiece", "1");
		templateDetailList.add(overseas);
		
		Reporter.log("5）禁用模板"+templateId);
		JSONObject disableTemplateResult = freightTemplate.templateStatus(sellerUserdata, templateId, "2");
		assertEquals(disableTemplateResult.getString("code"), "10000","禁用模板校验"+disableTemplateResult);
		assertEquals(freightData.getTemplateValue(templateId, "status"), "2","数据库中校验模板状态");
		
		Reporter.log("6）启用模板"+templateId);
		JSONObject enableTemplateResult = freightTemplate.templateStatus(sellerUserdata, templateId, "1");
		assertEquals(enableTemplateResult.getString("code"), "10000","启用模板校验"+enableTemplateResult);
		assertEquals(freightData.getTemplateValue(templateId, "status"), "1","数据库中校验模板状态");
		
/*		Reporter.log("7）删除模板"+templateId);
		JSONObject templateDeleteResult = freightTemplate.templateDelete(sellerUserdata, templateId);
		assertEquals(templateDeleteResult.getString("code"), "10000","删除模板校验"+templateDeleteResult);
		assertEquals(freightData.getTemplateValue(templateId, "*"), "","数据库中查询该模板应当不存在（硬删）");*/
	}

	@Test(priority=3,dependsOnMethods="test_FreightTemplate")
	public void test_goods_nomal() {
		Reporter.log("商品管理测试（普通商品）");
		
		GoodsAdmin goodsAdmin = new GoodsAdmin();
		GoodsSKU goodsSKU = new GoodsSKU();

		Reporter.log("1）登录商家中心："+seller);
		JSONObject loginResult = login.sellerLogin(seller, "123456li");
		assertEquals(loginResult.getString("code"), "10000","商家中心登录校验"+loginResult);
		JSONObject sellerUserdata = loginResult.getJSONObject("data");

		Reporter.log("2）新增商品，运营费模板ID＝"+templateId);
		JSONObject goodsAddResult = goodsAdmin.goodsAdd(sellerUserdata, sellerUserdata.getString("supplierId"), "普通商品", templateId, "0", "0", "0", "0", "1");
		assertEquals(goodsAddResult.getString("code"), "10000","新增商品校验"+goodsAddResult);
		goodsId = goodsAddResult.getJSONObject("data").getString("id");//商品ID 7893
		
		Reporter.log("3）添加商品("+goodsId+")的SKU");
		JSONArray goodsSkuList = goodsSKU.skuAdd(sellerUserdata, goodsId,"0");
		
		Reporter.log("4）查看商品详情"+goodsId);
		JSONObject goodsDetailResult = goodsAdmin.goodsDetail(sellerUserdata, goodsId);
		assertEquals(goodsDetailResult.getString("code"), "10000","查询商品详情校验"+goodsDetailResult);
		JSONObject goodsDetail = goodsDetailResult.getJSONObject("data");
		
		Reporter.log("5）修改商品，加入SKU的数据,并保存草稿");
		JSONObject goodsUpdateSkuResult = goodsAdmin.goodsUpdate_sku(sellerUserdata, goodsDetail, goodsSkuList,"0");
		assertEquals(goodsUpdateSkuResult.getString("code"), "10000","修改商品（SKU）校验"+goodsUpdateSkuResult);
		
		Reporter.log("6）商品提交到待审核");
		JSONObject goodsPublishWaitResult1 = goodsAdmin.goodsPublishWait(sellerUserdata, goodsId, 0);
		assertEquals(goodsPublishWaitResult1.getString("code"), "10000","商品提交审核校验"+goodsPublishWaitResult1);
		
		JSONObject sysLoginResult = login.sysLogin("helen", "123456li");
		assertEquals(sysLoginResult.getString("code"), "10000","校验运营中心是否登录成功"+sysLoginResult);
		JSONObject sysUserData = sysLoginResult.getJSONObject("data");
		
		Reporter.log("7）运营中心，审核并上架商品"+goodsId);
		JSONObject IsOnliveAndPublishWaitResult = goodsAdmin.IsOnliveAndPublishWait(sysUserData, goodsId, 1, 1);
		assertEquals(IsOnliveAndPublishWaitResult.getString("code"), "10000","审核并上架商品校验"+IsOnliveAndPublishWaitResult);
		
		Reporter.log("8）商家下架商品");
		JSONObject isOnliveResult_off = goodsAdmin.isOnlive(sellerUserdata, goodsId, 0);
		assertEquals(isOnliveResult_off.getString("code"), "10000","上架商品校验");
		
		Reporter.log("9）商家上架商品");
		JSONObject isOnliveResult_on = goodsAdmin.isOnlive(sellerUserdata, goodsId, 1);
		assertEquals(isOnliveResult_on.getString("code"), "10000","上架商品校验");
	}
	
	@Test(priority=4,dependsOnMethods="test_FreightTemplate")
	public void test_goods_Health() {
		Reporter.log("商品管理测试（乐豆商品）");
		
		GoodsAdmin goodsAdmin = new GoodsAdmin();
		GoodsSKU goodsSKU = new GoodsSKU();
		
		Reporter.log("1）登录商家中心："+seller);
		JSONObject loginResult = login.sellerLogin(seller, "123456li");
		assertEquals(loginResult.getString("code"), "10000","商家中心登录校验"+loginResult);
		JSONObject sellerUserdata = loginResult.getJSONObject("data");

		Reporter.log("2）新增商品");
		JSONObject goodsAddResult = goodsAdmin.goodsAdd(sellerUserdata, sellerUserdata.getString("supplierId"), "乐豆商品", templateId, "0", "0", "1", "0", "1");
		assertEquals(goodsAddResult.getString("code"), "10000","新增商品校验"+goodsAddResult);
		String goodsId = goodsAddResult.getJSONObject("data").getString("id");//商品ID 7893
		
		Reporter.log("3）添加商品("+goodsId+")的SKU");
		JSONArray goodsSkuList = goodsSKU.skuAdd(sellerUserdata, goodsId,"1");
		
		Reporter.log("4）查看商品详情"+goodsId);
		JSONObject goodsDetailResult = goodsAdmin.goodsDetail(sellerUserdata, goodsId);
		assertEquals(goodsDetailResult.getString("code"), "10000","查询商品详情校验"+goodsDetailResult);
		JSONObject goodsDetail = goodsDetailResult.getJSONObject("data");
		
		Reporter.log("5）修改商品，加入SKU的数据,并提交审核");
		JSONObject goodsUpdateSkuResult = goodsAdmin.goodsUpdate_sku(sellerUserdata, goodsDetail, goodsSkuList,"1");
		assertEquals(goodsUpdateSkuResult.getString("code"), "10000","修改商品（SKU）校验"+goodsUpdateSkuResult);
		
		JSONObject sysLoginResult = login.sysLogin("helen", "123456li");
		assertEquals(sysLoginResult.getString("code"), "10000","校验运营中心是否登录成功"+sysLoginResult);
		JSONObject sysUserData = sysLoginResult.getJSONObject("data");
		
		Reporter.log("6）运营中心，审核并上架商品"+goodsId);
		JSONObject IsOnliveAndPublishWaitResult = goodsAdmin.IsOnliveAndPublishWait(sysUserData, goodsId, 1, 1);
		assertEquals(IsOnliveAndPublishWaitResult.getString("code"), "10000","审核并上架商品校验");
		
		Reporter.log("7）商家下架商品");
		JSONObject isOnliveResult_off = goodsAdmin.isOnlive(sellerUserdata, goodsId, 0);
		assertEquals(isOnliveResult_off.getString("code"), "10000","上架商品校验");
		
		Reporter.log("8）商家上架商品");
		JSONObject isOnliveResult_on = goodsAdmin.isOnlive(sellerUserdata, goodsId, 1);
		assertEquals(isOnliveResult_on.getString("code"), "10000","上架商品校验");
	}

	@Test(priority=5,dependsOnMethods="test_goods_nomal")
	public void test_orders() {
		Reporter.log("订单发货+商品评论回复");
		EvaluateData evaluateData = new EvaluateData();
		ShoppingCart shoppingCartTest = new ShoppingCart();
		TradeOrder tradeOrder = new TradeOrder();
		TradeInvoiceAdmin tradeInvoiceAdmin = new TradeInvoiceAdmin();
		TradeEvaluate tradeEvaluate = new TradeEvaluate();
		String skuNumber = goodData.getSKU_goodId(goodsId).getJSONObject(0).getString("sku_number");
		
		Reporter.log("[会员操作]-----");
		Reporter.log("1）会员登录PC商城"+username);
		JSONObject pcLoginResult = login.login(username, "123456li", platform);
		assertEquals(pcLoginResult.getString("code"), "10000","PC商城登录校验"+pcLoginResult);
		JSONObject userdata = pcLoginResult.getJSONObject("data");
		
		Reporter.log("2）会员把商品（"+goodsId+"）的SKU（"+skuNumber+"）加入购物车");
		JSONObject addCartResult = shoppingCartTest.addShoppingCart(userdata, skuNumber, "2", platform);
		assertEquals(addCartResult.getString("code"), "10000","加入购物车校验");
		String cartId = addCartResult.getJSONObject("data").getString("cartId");
		
		String addressId = memberDeliveryAddress.getAddrId(userdata, platform);
		Reporter.log("获取会员的收货地址ID："+addressId);
		
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
		
		Reporter.log("3）会员生成订单");
		JSONObject generateOrderResult = tradeOrder.generateOrder(userdata, addressId,1, "0", cartId, false, supplierOrderList, 1, "", platform);
		assertEquals(generateOrderResult.getString("code"), "10000","生成订单"+generateOrderResult);
		JSONObject orderData = generateOrderResult.getJSONObject("data");
		
		Reporter.log("4）会员模拟使用[微信]支付订单："+orderData.getString("orderNos"));
		JSONObject payResult = tradeOrder.payOrder(userdata, orderData.getJSONArray("orderIds").getString(0), orderData.getString("payTotal"), "wxin", platform);
		assertEquals(payResult.getString("code"), "95270","校验模拟支付是否成功"+payResult);
		
		Reporter.log("[商家操做]-----");
		Reporter.log("1）登录商家中心："+seller);
		JSONObject loginResult = login.sellerLogin(seller, "123456li");
		assertEquals(loginResult.getString("code"), "10000","商家中心登录校验"+loginResult);
		JSONObject sellerUserdata = loginResult.getJSONObject("data");
		
		Reporter.log("2）商家中心查看订单详情");
		JSONObject orderDetailResult = tradeOrder.orderDetail(sellerUserdata, orderData.getJSONArray("orderIds").getString(0), "");
		assertEquals(orderDetailResult.getString("code"), "10000","查看订单详情接口"+orderDetailResult);
		String orderItemId = orderDetailResult.getJSONObject("data").getJSONArray("goodsItem").getJSONObject(0).getString("id");
		
		Reporter.log("3）商家中心确认发货"+orderData.getJSONArray("orderNos").getString(0));
		JSONObject sendResult = tradeInvoiceAdmin.send(sellerUserdata, orderData.getJSONArray("orderIds").getString(0));
		assertEquals(sendResult.getString("code"), "10000","发货校验"+sendResult);
		
		Reporter.log("4）商家中心按[订单号]查询运单");
		JSONObject invoiceListResult = tradeInvoiceAdmin.invoiceList(sellerUserdata, orderData.getJSONArray("orderNos").getString(0));
		assertEquals(invoiceListResult.getString("code"), "10000","校验查询运单号是否成功"+invoiceListResult);
		String invoiceId = invoiceListResult.getJSONObject("data").getJSONArray("list").getJSONObject(0).getString("id");
		
		Reporter.log("[会员操作]-----");
		Reporter.log("1）会员确认收货");
		JSONObject enterReceiveGoodsResult = tradeOrder.enterReceiveGoods(userdata, invoiceId, orderData.getJSONArray("orderIds").getString(0), platform);
		assertEquals(enterReceiveGoodsResult.getString("code"), "10000","校验确认收货是否成功"+enterReceiveGoodsResult);
		
		Reporter.log("2）会员评论商品:视频评论");
		JSONObject evaluateResult = tradeEvaluate.evaluateOrderItem(userdata, orderData.getJSONArray("orderNos").getString(0),orderItemId, "1", platform);
		assertEquals(evaluateResult.getString("code"), "10000","校验是否评论成功"+evaluateResult);
		
		Reporter.log("[商家中心]------");
		Reporter.log("1）商家中心按[订单号]查询评论");
		JSONObject orderEvaluationListResult = tradeEvaluate.orderEvaluationList(sellerUserdata, orderData.getJSONArray("orderNos").getString(0));
		assertEquals(orderEvaluationListResult.getString("code"), "10000","查询评论列表校验"+orderEvaluationListResult);
		JSONObject Evaluation = orderEvaluationListResult.getJSONObject("data").getJSONArray("list").getJSONObject(0);
		
		Reporter.log("2）商家回复评论");
		JSONObject orderEvaluateReplyResult = tradeEvaluate.orderEvaluateReply(sellerUserdata, Evaluation.getString("evaluateId"), Evaluation.getString("evaluateDetailId"));
		assertEquals(orderEvaluateReplyResult.getString("code"), "10000","商家回复评论校验"+orderEvaluateReplyResult);
		
		Reporter.log("[会员操作]-----");
		Reporter.log("1）会员提交追评:图片评论");
		JSONObject evaluateAppendOrderItemResult = tradeEvaluate.evaluateAppendOrderItem(userdata, orderItemId, "0", platform);
		assertEquals(evaluateAppendOrderItemResult.getString("code"), "10000","用户追评校验"+evaluateAppendOrderItemResult);
		
		Reporter.log("[商家中心]------");
		//查询追评ID
		String evaluateDetailId2 = evaluateData.getEvaluateDetail_evaluateId(Evaluation.getString("evaluateId"), "evaluate_detail_id");
		Reporter.log("1）商家回复追评");
		JSONObject orderEvaluateReplyResult2 = tradeEvaluate.orderEvaluateReply(sellerUserdata, Evaluation.getString("evaluateId"), evaluateDetailId2);
		assertEquals(orderEvaluateReplyResult2.getString("code"), "10000","商家回复评论校验"+orderEvaluateReplyResult2);
	}

	@Test(priority=6,dependsOnMethods="test_goods_nomal")
	public void test_moneyRefund() throws InterruptedException {
		Reporter.log("退款处理：审核通过");
		ShoppingCart shoppingCartTest = new ShoppingCart();
		TradeOrder tradeOrder = new TradeOrder();
		TradeRefund tradeRefund = new TradeRefund();
		String skuNumber = goodData.getSKU_goodId(goodsId).getJSONObject(0).getString("sku_number");
		
		Reporter.log("[会员操作]-----");
		Reporter.log("1）会员登录PC商城"+username);
		JSONObject pcLoginResult = login.login(username, "123456li", platform);
		assertEquals(pcLoginResult.getString("code"), "10000","PC商城登录校验"+pcLoginResult);
		JSONObject userdata = pcLoginResult.getJSONObject("data");
		
		Reporter.log("清空用户当前的购物车");
		shoppingCart.clearShoppingCart(userdata, platform);
		
		Reporter.log("2）会员把商品（"+goodsId+"）的SKU（"+skuNumber+"）加入购物车");
		JSONObject addCartResult = shoppingCartTest.addShoppingCart(userdata, skuNumber, "2", platform);
		assertEquals(addCartResult.getString("code"), "10000","加入购物车校验");
		String cartId = addCartResult.getJSONObject("data").getString("cartId");
		
		String addressId = memberDeliveryAddress.getAddrId(userdata, platform);
		Reporter.log("获取会员的收货地址ID："+addressId);
		
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
		
		Reporter.log("3）会员生成订单");
		JSONObject generateOrderResult = tradeOrder.generateOrder(userdata, addressId,1, "0", cartId, false, supplierOrderList, 1, "", platform);
		assertEquals(generateOrderResult.getString("code"), "10000","生成订单"+generateOrderResult);
		JSONObject orderData = generateOrderResult.getJSONObject("data");
		
		Reporter.log("4）会员模拟使用[微信]支付订单："+orderData.getString("orderNos"));
		JSONObject payResult = tradeOrder.payOrder(userdata, orderData.getJSONArray("orderIds").getString(0), orderData.getString("payTotal"), "wxin", platform);
		assertEquals(payResult.getString("code"), "95270","校验模拟支付是否成功"+payResult);
		
		Thread.sleep(3000);
		Reporter.log("5）会员申请退款");
		JSONObject moneyRefundResult = tradeRefund.moneyRefund(userdata, orderData.getJSONArray("orderIds").getString(0), platform);
		assertEquals(moneyRefundResult.getString("code"), "10000","退款申请校验"+moneyRefundResult);
		
		Reporter.log("[商家操作]-----");
		Reporter.log("1）登录商家中心："+seller);
		JSONObject loginResult = login.sellerLogin(seller, "123456li");
		assertEquals(loginResult.getString("code"), "10000","商家中心登录校验"+loginResult);
		JSONObject sellerUserdata = loginResult.getJSONObject("data");
		
		Reporter.log("2）商家根据订单号查询退款信息");
		JSONObject refundListResult = tradeRefund.refundList(sellerUserdata, orderData.getJSONArray("orderNos").getString(0));
		assertEquals(refundListResult.getString("code"), "10000","退货退款列表"+refundListResult);
		String refundId = refundListResult.getJSONObject("data").getJSONArray("list").getJSONObject(0).getString("id");
		
		Reporter.log("3）商家审核退款：审核通过"+refundId);
		JSONObject refundAuditResult = tradeRefund.RefundAudit(sellerUserdata, refundId, 2);
		assertEquals(refundAuditResult.getString("code"), "10000","审核退款校验"+refundAuditResult);		
	}
	
	@Test(priority=7,dependsOnMethods="test_goods_nomal")
	public void test_goodsRefund() throws InterruptedException {
		Reporter.log("退款处理：拒绝退款");
		ShoppingCart shoppingCartTest = new ShoppingCart();
		TradeOrder tradeOrder = new TradeOrder();
		TradeRefund tradeRefund = new TradeRefund();
		String skuNumber = goodData.getSKU_goodId(goodsId).getJSONObject(0).getString("sku_number");
		
		Reporter.log("[会员操作]-----");
		Reporter.log("1）会员登录PC商城"+username);
		JSONObject pcLoginResult = login.login(username, "123456li", platform);
		assertEquals(pcLoginResult.getString("code"), "10000","PC商城登录校验"+pcLoginResult);
		JSONObject userdata = pcLoginResult.getJSONObject("data");
		
		Reporter.log("清空用户当前的购物车");
		shoppingCart.clearShoppingCart(userdata, platform);
		
		Reporter.log("2）会员把商品（"+goodsId+"）的SKU（"+skuNumber+"）加入购物车");
		JSONObject addCartResult = shoppingCartTest.addShoppingCart(userdata, skuNumber, "2", platform);
		assertEquals(addCartResult.getString("code"), "10000","加入购物车校验");
		String cartId = addCartResult.getJSONObject("data").getString("cartId");
		
		String addressId = memberDeliveryAddress.getAddrId(userdata, platform);
		Reporter.log("3）获取会员的收货地址ID："+addressId);
		
		Reporter.log("4）查询购物车列表");
		JSONObject selectShoppingCartResult = shoppingCart.selectShoppingCart(userdata, platform);
		assertEquals(selectShoppingCartResult.getString("code"), "10000","查询购物车"+selectShoppingCartResult);
		JSONArray cartList = selectShoppingCartResult.getJSONObject("data").getJSONArray("cartList");
		
		Reporter.log("5）提交购物车");
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
		
		Reporter.log("3）会员生成订单");
		JSONObject generateOrderResult = tradeOrder.generateOrder(userdata, addressId,1, "0", cartId, false, supplierOrderList, 1, "", platform);
		assertEquals(generateOrderResult.getString("code"), "10000","生成订单"+generateOrderResult);
		JSONObject orderData = generateOrderResult.getJSONObject("data");
		
		Reporter.log("4）会员模拟使用[微信]支付订单："+orderData.getString("orderNos"));
		JSONObject payResult = tradeOrder.payOrder(userdata, orderData.getJSONArray("orderIds").getString(0), orderData.getString("payTotal"), "wxin", platform);
		assertEquals(payResult.getString("code"), "95270","校验模拟支付是否成功"+payResult);
		
		Thread.sleep(3000);
		Reporter.log("5）会员申请退款");
		JSONObject moneyRefundResult = tradeRefund.moneyRefund(userdata, orderData.getJSONArray("orderIds").getString(0), platform);
		assertEquals(moneyRefundResult.getString("code"), "10000","退款申请校验"+moneyRefundResult);
		
		Reporter.log("[商家操作]-----");
		Reporter.log("1）登录商家中心："+seller);
		JSONObject loginResult = login.sellerLogin(seller, "123456li");
		assertEquals(loginResult.getString("code"), "10000","商家中心登录校验"+loginResult);
		JSONObject sellerUserdata = loginResult.getJSONObject("data");
		
		Reporter.log("2）商家根据订单号查询退款信息");
		JSONObject refundListResult = tradeRefund.refundList(sellerUserdata, orderData.getJSONArray("orderNos").getString(0));
		assertEquals(refundListResult.getString("code"), "10000","退货退款列表"+refundListResult);
		String refundId = refundListResult.getJSONObject("data").getJSONArray("list").getJSONObject(0).getString("id");
		
		Reporter.log("3）商家审核退款：拒绝退款"+refundId);
		JSONObject refundAuditResult = tradeRefund.RefundAudit(sellerUserdata, refundId, -2);
		assertEquals(refundAuditResult.getString("code"), "10000","审核退款校验"+refundAuditResult);		
	}
	
	@Test(priority=8)
	public void test_fundApplier_auditFalse() {
		Reporter.log("提现管理：审核不通过");
		FundApply fundApply = new FundApply();
		seller = sellerData.getAccount_abledBalance();
		Reporter.log("商家账号："+seller);
		assertNotEquals(seller, "","校验是否存在[可用余额>1000]的商家");
		
		Reporter.log("[清理数据]------");
		String sellerId = sellerData.getValue_account(seller, "supplier_id");
		JSONObject sysLoginResult = login.sysLogin("helen", "123456li");
		assertEquals(sysLoginResult.getString("code"), "10000","运营中心登录校验"+sysLoginResult);
		JSONObject sysUserdata = sysLoginResult.getJSONObject("data");
		try {
			Reporter.log("1）查询是否存在[待审核]的记录");
			JSONObject applierPassRestul = fundApply.fundApplyList(sysUserdata, sellerId, 1);
			assertEquals(applierPassRestul.getString("code"), "10000");
			JSONArray applierPassList = applierPassRestul.getJSONObject("data").getJSONArray("list");
			Reporter.log("如果该商家存在[待审核]的记录，则把该记录设置为[审核不通过]");
			for (int i = 0; i < applierPassList.size(); i++) {
				JSONObject applyAuditResult = fundApply.applyAudit(sysUserdata, applierPassList.getJSONObject(i).getString("id"), false);
				assertEquals(applyAuditResult.getString("code"), "10000","提现申请审核校验"+applyAuditResult);
			}
			
			Reporter.log("2）查询是否存在[审核通过]的记录");
			JSONObject applierPassRestul2 = fundApply.fundApplyList(sysUserdata, sellerId, 3);
			assertEquals(applierPassRestul2.getString("code"), "10000");
			JSONArray applierPassList2 = applierPassRestul2.getJSONObject("data").getJSONArray("list");
			Reporter.log("如果该商家存在[审核通过]的记录，则把该记录设置为[转账失败]");
			for (int i = 0; i < applierPassList2.size(); i++) {
				JSONObject applyTransferResult = fundApply.applyTransfer(sysUserdata, applierPassList2.getJSONObject(i).getString("id"), false);
				assertEquals(applyTransferResult.getString("code"), "10000","转账校验"+applyTransferResult);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Reporter.log("[商家中心操作]-----");
		Reporter.log("1）登录商家中心："+seller);
		JSONObject loginResult = login.sellerLogin(seller, "123456li");
		assertEquals(loginResult.getString("code"), "10000","商家中心登录校验"+loginResult);
		JSONObject sellerUserdata = loginResult.getJSONObject("data");
		
		Reporter.log("2）获取商家的可提现信息");
		JSONObject applyInfoResult = fundApply.fundApplyInfo(sellerUserdata);
		assertEquals(applyInfoResult.getString("code"), "10000","查询商家可反现金额等信息校验"+applyInfoResult);
		String abledBalance = applyInfoResult.getJSONObject("data").getString("abledBalance");
		
		Reporter.log("3）获取商家提现结单时间");
		JSONObject getStatementTimeResult = fundApply.getStatementTime(sellerUserdata);
		assertEquals(getStatementTimeResult.getString("code"), "10000","获取商家提现结单时间"+getStatementTimeResult);
		JSONObject StatementTime = getStatementTimeResult.getJSONObject("data");
		
		Reporter.log("3）商家提交提现申请，提现金额："+abledBalance);
		JSONObject fundApplyAddResult = fundApply.fundApplyAdd(sellerUserdata, StatementTime.getString("statementBeginTime"),StatementTime.getString("statementEndTime"));
		assertEquals(fundApplyAddResult.getString("code"), "10000","提现申请校验"+fundApplyAddResult);
		
		Reporter.log("4）商家查询提现申请列表");
		JSONObject fundApplyListResult = fundApply.fundApplyList(sellerUserdata,sellerUserdata.getString("supplier_id"),1);
		assertEquals(fundApplyListResult.getString("code"), "10000","提现申请列表校验"+fundApplyListResult);
		String applyId = fundApplyListResult.getJSONObject("data").getJSONArray("list").getJSONObject(0).getString("id");
		assertEquals(fundApplyListResult.getJSONObject("data").getJSONArray("list").getJSONObject(0).getString("amount"), abledBalance,"校验");
		
		Reporter.log("[运营中心操作]-----");		
		Reporter.log("1）运营中心审核提现申请：审核不通过");
		JSONObject applyAuditResult = fundApply.applyAudit(sysUserdata, applyId, false);
		assertEquals(applyAuditResult.getString("code"), "10000","提现申请审核校验"+applyAuditResult);
		
		Reporter.log("[商家中心操作]-----");
		Reporter.log("1）获取商家的可提现信息");
		JSONObject applyInfoResult2 = fundApply.fundApplyInfo(sellerUserdata);
		assertEquals(applyInfoResult2.getString("code"), "10000","查询商家可反现金额等信息校验"+applyInfoResult2);
		assertEquals(applyInfoResult.getJSONObject("data").getString("abledBalance"), abledBalance,"可提现余额应当不变");
	}
	
	@Test(priority=9,dependsOnMethods="test_fundApplier_auditFalse")
	public void test_fundApplier_transferFalse() {
		Reporter.log("提现管理：转账不成功");
		FundApply fundApply = new FundApply();
		
		Reporter.log("[商家中心操作]-----");
		Reporter.log("1）登录商家中心："+seller);
		JSONObject loginResult = login.sellerLogin(seller, "123456li");
		assertEquals(loginResult.getString("code"), "10000","商家中心登录校验"+loginResult);
		JSONObject sellerUserdata = loginResult.getJSONObject("data");
		
		Reporter.log("2）获取商家的可提现信息");
		JSONObject applyInfoResult = fundApply.fundApplyInfo(sellerUserdata);
		assertEquals(applyInfoResult.getString("code"), "10000","查询商家可反现金额等信息校验"+applyInfoResult);
		String abledBalance = applyInfoResult.getJSONObject("data").getString("abledBalance");
		
		Reporter.log("3）获取商家提现结单时间");
		JSONObject getStatementTimeResult = fundApply.getStatementTime(sellerUserdata);
		assertEquals(getStatementTimeResult.getString("code"), "10000","获取商家提现结单时间"+getStatementTimeResult);
		JSONObject StatementTime = getStatementTimeResult.getJSONObject("data");
		
		Reporter.log("3）商家提交提现申请，提现金额："+abledBalance);
		JSONObject fundApplyAddResult = fundApply.fundApplyAdd(sellerUserdata, StatementTime.getString("statementBeginTime"),StatementTime.getString("statementEndTime"));
		assertEquals(fundApplyAddResult.getString("code"), "10000","提现申请校验");
		
		Reporter.log("4）商家查询提现申请列表:待审核");
		JSONObject fundApplyListResult = fundApply.fundApplyList(sellerUserdata,sellerUserdata.getString("supplier_id"),1);
		assertEquals(fundApplyListResult.getString("code"), "10000","提现申请列表校验"+fundApplyListResult);
		String applyId = fundApplyListResult.getJSONObject("data").getJSONArray("list").getJSONObject(0).getString("id");
		
		Reporter.log("[运营中心操作]-----");
		JSONObject sysLoginResult = login.sysLogin("helen", "123456li");
		assertEquals(sysLoginResult.getString("code"), "10000","运营中心登录校验"+sysLoginResult);
		JSONObject sysUserdata = sysLoginResult.getJSONObject("data");
		
		Reporter.log("1）运营中心审核提现申请：审核通过");
		JSONObject applyAuditResult = fundApply.applyAudit(sysUserdata, applyId, true);
		assertEquals(applyAuditResult.getString("code"), "10000","提现申请审核校验"+applyAuditResult);
		
		Reporter.log("2）运营中心进行转账：转账不成功");
		JSONObject applyTransferResult = fundApply.applyTransfer(sysUserdata, applyId, false);
		assertEquals(applyTransferResult.getString("code"), "10000","转账校验"+applyTransferResult);
		
		Reporter.log("[商家中心操作]-----");
		Reporter.log("1）获取商家的可提现信息");
		JSONObject applyInfoResult2 = fundApply.fundApplyInfo(sellerUserdata);
		assertEquals(applyInfoResult2.getString("code"), "10000","查询商家可反现金额等信息校验"+applyInfoResult2);
		assertEquals(applyInfoResult.getJSONObject("data").getString("abledBalance"), abledBalance,"可提现余额应当不变");
	}
	
	@Test(priority=10,dependsOnMethods="test_fundApplier_transferFalse")
	public void test_fundApplier() {
		Reporter.log("提现管理:审核通过，并且转账成功");
		FundApply fundApply = new FundApply();
		
		Reporter.log("[商家中心操作]-----");
		Reporter.log("1）登录商家中心："+seller);
		JSONObject loginResult = login.sellerLogin(seller, "123456li");
		assertEquals(loginResult.getString("code"), "10000","商家中心登录校验"+loginResult);
		JSONObject sellerUserdata = loginResult.getJSONObject("data");
		
		Reporter.log("2）获取商家的可提现信息");
		JSONObject applyInfoResult = fundApply.fundApplyInfo(sellerUserdata);
		assertEquals(applyInfoResult.getString("code"), "10000","查询商家可反现金额等信息校验"+applyInfoResult);
		String abledBalance = applyInfoResult.getJSONObject("data").getString("abledBalance");
		
		Reporter.log("3）获取商家提现结单时间");
		JSONObject getStatementTimeResult = fundApply.getStatementTime(sellerUserdata);
		assertEquals(getStatementTimeResult.getString("code"), "10000","获取商家提现结单时间"+getStatementTimeResult);
		JSONObject StatementTime = getStatementTimeResult.getJSONObject("data");
		
		Reporter.log("3）商家提交提现申请，提现金额："+abledBalance);
		JSONObject fundApplyAddResult = fundApply.fundApplyAdd(sellerUserdata, StatementTime.getString("statementBeginTime"),StatementTime.getString("statementEndTime"));
		assertEquals(fundApplyAddResult.getString("code"), "10000","提现申请校验");
		
		Reporter.log("4）商家查询提现申请列表");
		JSONObject fundApplyListResult = fundApply.fundApplyList(sellerUserdata,sellerUserdata.getString("supplier_id"),1);
		assertEquals(fundApplyListResult.getString("code"), "10000","提现申请列表校验"+fundApplyListResult);
		String applyId = fundApplyListResult.getJSONObject("data").getJSONArray("list").getJSONObject(0).getString("id");
		
		Reporter.log("[运营中心操作]-----");
		JSONObject sysLoginResult = login.sysLogin("helen", "123456li");
		assertEquals(sysLoginResult.getString("code"), "10000","运营中心登录校验"+sysLoginResult);
		JSONObject sysUserdata = sysLoginResult.getJSONObject("data");
		
		Reporter.log("1）运营中心审核提现申请：审核通过");
		JSONObject applyAuditResult = fundApply.applyAudit(sysUserdata, applyId, true);
		assertEquals(applyAuditResult.getString("code"), "10000","提现申请审核校验"+applyAuditResult);
		
		Reporter.log("2）运营中心进行转账：转账成功");
		JSONObject applyTransferResult = fundApply.applyTransfer(sysUserdata, applyId, true);
		assertEquals(applyTransferResult.getString("code"), "10000","转账校验"+applyTransferResult);
		
		Reporter.log("[商家中心操作]-----");
		Reporter.log("1）商家中心查看资金流水列表：支出、提现");
		JSONObject journalListResult = fundApply.journalList(sellerUserdata, "1", "6");
		assertEquals(journalListResult.getString("code"), "10000","资金流水列表校验"+journalListResult);
		String journalId = journalListResult.getJSONObject("data").getJSONArray("list").getJSONObject(0).getString("id");
		
		Reporter.log("2）商家查看提现资金流水详情:"+journalId);
		JSONObject journalDetailResult = fundApply.journalDetail(sellerUserdata, journalId);
		assertEquals(journalDetailResult.getString("code"), "10000","资金流水详情校验"+journalDetailResult);
		assertEquals(Double.valueOf(journalDetailResult.getJSONObject("data").getString("dealBalance")), Double.valueOf("-"+abledBalance),"提现金额校验");
		
	}
	
	@Test(priority=11)
	public void test_address() {
		Reporter.log("地址管理");
		SupplierAddress supplierAddress = new SupplierAddress();
		
		Reporter.log("[商家中心操作]-----");
		Reporter.log("1）登录商家中心："+seller);
		JSONObject loginResult = login.sellerLogin(seller, "123456li");
		assertEquals(loginResult.getString("code"), "10000","商家中心登录校验"+loginResult);
		JSONObject sellerUserdata = loginResult.getJSONObject("data");
		
		Reporter.log("2）查看省份下拉列表");
		JSONObject getTopAddressResult = supplierAddress.getTopAddress(sellerUserdata);
		assertEquals(getTopAddressResult.getString("code"), "10000","获取省份列表校验"+getTopAddressResult);
		JSONObject provinceData = getTopAddressResult.getJSONArray("data").getJSONObject(baseData.getNum(1, (getTopAddressResult.getJSONArray("data").size())-1));
		String provinceCode = provinceData.getString("areaCode");
		
		Reporter.log("3）查看市级下拉列表");
		JSONObject getSubAddressResult1 = supplierAddress.getSubAddress(sellerUserdata, provinceCode);
		assertEquals(getSubAddressResult1.getString("code"), "10000","获取市级列表校验"+getSubAddressResult1);
		JSONObject cityData = getSubAddressResult1.getJSONArray("data").getJSONObject(baseData.getNum(0, (getSubAddressResult1.getJSONArray("data").size())-1));
		String cityCode = cityData.getString("areaCode");
		
		Reporter.log("4）查看区级下拉列表");
		JSONObject getSubAddressResult2 = supplierAddress.getSubAddress(sellerUserdata, cityCode);
		assertEquals(getSubAddressResult2.getString("code"), "10000","获取市级列表校验"+getSubAddressResult2);
		JSONObject areaData = getSubAddressResult2.getJSONArray("data").getJSONObject(baseData.getNum(0, (getSubAddressResult2.getJSONArray("data").size())-1));
		String areaCode = areaData.getString("areaCode");
		
		Reporter.log("5）新增地址");
		String areaName = provinceData.getString("areaName")+cityData.getString("areaName")+areaData.getString("areaName");
		JSONObject addressAddResult = supplierAddress.addAddress(sellerUserdata, areaCode, areaName);
		assertEquals(addressAddResult.getString("code"), "10000","新增地址校验"+addressAddResult);
		
		Reporter.log("6）查看地址列表");
		JSONObject addressListResult = supplierAddress.addressList(sellerUserdata);
		assertEquals(addressListResult.getString("code"), "10000","商家的地址列表校验"+addressListResult);
		String addressId = addressListResult.getJSONArray("data").getJSONObject(0).getString("addressId");
		
		Reporter.log("7）查看地址详情:addressId="+addressId);
		JSONObject addressDetailResult = supplierAddress.addressDetail(sellerUserdata, addressId);
		assertEquals(addressDetailResult.getString("code"), "10000","地址详情校验"+addressDetailResult);

	}

}
