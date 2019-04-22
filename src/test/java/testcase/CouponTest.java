/**
 * @author helen
 * @date 2018年11月15日
 */
package testcase;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.List;

import org.testng.Reporter;
import org.testng.annotations.Test;

import common.BaseData;
import common.MyAPI;
import common.MyConfig;
import data.CouponData;
import data.GoodData;
import data.MemberData;
import data.MemberScoreData;
import data.OrderData;
import marketing.CouponManager;
import marketing.CouponUse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import order.ShoppingCart;
import order.TradeOrder;
import user.Login;
import user.MemberDeliveryAddress;
import user.Registry;

/**
 * @Description:优惠券业务测试（未完成）
 */
public class CouponTest {
	MyConfig myConfig = new MyConfig();
	BaseData baseData = new BaseData();
	MemberData memberData = new MemberData();
	MemberScoreData memberScoreData = new MemberScoreData();
	GoodData goodData = new GoodData();
	CouponData couponData = new CouponData();
	OrderData orderData = new OrderData();
	MyAPI myAPI = new MyAPI();
	Registry registryTest = new Registry();
	Login login = new Login();
	ShoppingCart shoppingCart = new ShoppingCart();
	TradeOrder tradeOrder = new TradeOrder();
	CouponManager couponManager = new CouponManager();
	CouponUse couponUse = new CouponUse();
	MemberDeliveryAddress memberDeliveryAddress = new MemberDeliveryAddress();
		
	@Test
	public void test_cashCoupon1() {
		Reporter.log("现金券测试1");
		
		Reporter.log("[运营中心操作]");
		Reporter.log("1）登录运营中心");
		JSONObject sysLoginResult = login.sysLogin("helen", "123456li");
		assertEquals(sysLoginResult.getString("code"), "10000","运营中心登录");
		JSONObject sysUserdata = sysLoginResult.getJSONObject("data");
		
		String couponName = "现金券"+baseData.getNum(0, 9999);
		Reporter.log("2）新增现金券[满199减29_全部商品可用_全部会员可领取],优惠券名称："+couponName);
		JSONObject addCashCouponResult = couponManager.addCashCoupon(sysUserdata, couponName,"1","29", "1","199", "1", "", "1", "");
		assertEquals(addCashCouponResult.getString("code"), "10000","新增现金券"+addCashCouponResult);
		String couponId = couponData.get_id(couponName);//获得优惠券Id
		
		Reporter.log("3）优惠券审核通过，优惠券ID="+couponId);
		JSONObject auditResult = couponManager.auditCoupon(sysUserdata, couponId, "3");
		assertEquals(auditResult.getString("code"), "10000","审核优惠券"+auditResult);
		
		Reporter.log("4）发放优惠券，优惠券ID="+couponId);
		JSONObject sendResult = couponManager.sendCoupon(sysUserdata, couponId);
		assertEquals(sendResult.getString("code"), "10000","发放优惠券"+sendResult);
		
		Reporter.log("[android App商城操作]");
		String platform = "android";
		String username = memberData.get_account("phone");
		Reporter.log("1）会员登录商城："+username);
		JSONObject loginResult = login.login(username, "123456li", platform);
		assertEquals(loginResult.getString("code"), "10000","会员登录android商城"+loginResult);
		JSONObject userdata = loginResult.getJSONObject("data");
		
		Reporter.log("2）获取优惠券，优惠券ID="+couponId);
		JSONObject getCouponResult = couponUse.getCoupon(userdata, couponId, platform);
		assertEquals(getCouponResult.getString("code"), "10000","会员获取优惠券"+getCouponResult);
		
		
	}
	
	@Test
	public void test_FudouCoupon1() {
		Reporter.log("新增优惠券：福豆券_满198减28_全部商品可用_全部会员可领取");
		JSONObject sysLoginResult = login.sysLogin("helen", "123456li");
		assertEquals(sysLoginResult.getString("code"), "10000","运营中心登录");
		JSONObject sysUserdata = sysLoginResult.getJSONObject("data");
		JSONObject addCashCouponResult = couponManager.addFudouCoupon(sysUserdata, "福豆券_满198减28_全部商品可用_全部会员可领取","1","28", "1","198", "1", "", "1", "");
		assertEquals(addCashCouponResult.getString("code"), "10000","新增福豆券"+addCashCouponResult);
	}
	
	@Test
	public void test_RegistryCoupon1() {
		Reporter.log("新增优惠券：注册券_优先抵扣福豆_满197减27_全部商品可用_全部会员可领");
		JSONObject sysLoginResult = login.sysLogin("helen", "123456li");
		assertEquals(sysLoginResult.getString("code"), "10000","运营中心登录");
		JSONObject sysUserdata = sysLoginResult.getJSONObject("data");
		JSONObject addCashCouponResult = couponManager.addRegistryCoupon(sysUserdata, "注册券_优先抵扣福豆_满197减27_全部商品可用_全部会员可领", "1", "27", "3", "1", "197", "1", "");
		assertEquals(addCashCouponResult.getString("code"), "10000","新增注册券"+addCashCouponResult);
	}
	
	@Test
	public void test_addFullSubCoupon1() {
		Reporter.log("新增优惠券：满减券_优先抵扣福豆_满196减26_全部商品可用_全部会员可领");
		JSONObject sysLoginResult = login.sysLogin("helen", "123456li");
		assertEquals(sysLoginResult.getString("code"), "10000","运营中心登录");
		JSONObject sysUserdata = sysLoginResult.getJSONObject("data");
		JSONObject addCashCouponResult = couponManager.addFullSubCoupon(sysUserdata, "满减券_优先抵扣福豆_满196减26_全部商品可用", "1", "26", "3", "1", "196", "1", "", "1", "");
		assertEquals(addCashCouponResult.getString("code"), "10000","新增满减券"+addCashCouponResult);
	}
	
	@Test
	public void test_addConsumCoupon1() {
		Reporter.log("新增优惠券：消费券_优先抵扣福豆_满195减25_任何消费都赠送_全部商品可用_全部会员可领");
		JSONObject sysLoginResult = login.sysLogin("helen", "123456li");
		assertEquals(sysLoginResult.getString("code"), "10000","运营中心登录");
		JSONObject sysUserdata = sysLoginResult.getJSONObject("data");
		JSONObject addCashCouponResult = couponManager.addConsumCoupon(sysUserdata, "消费券_优先抵扣福豆_无门槛_任何消费都赠送_全部商品可用_全部会员可领", "1", "25", "3", "1", "195", "1", "", "1", "", "1", "");
		assertEquals(addCashCouponResult.getString("code"), "10000","新增消费券"+addCashCouponResult);
	}
	
	@Test
	public void test_couponSend4() {
		Reporter.log("检查注册券的赠送情况:在注册业务中校验");
	}
	
	@Test
	public void test_couponSend3() {
		Reporter.log("新增满减券－检查发放对象（向指定会员发放）");
		JSONArray userIdArray = memberData.getUserId_LastRegistry("20");
		String userIds = userIdArray.getJSONObject(0).getString("user_id");
		for (int i = 1; i < userIdArray.size(); i++) {
			userIds = userIds+","+userIdArray.getJSONObject(i).getString("user_id");
		}
		String couponId = this.couponSend("4", userIds);
		
		//检查各个账号送优惠券的情况
		String[] userIdList = userIds.split(",");
		for (String userId:userIdList) {
			assertNotEquals(couponData.getCouponItem_ById(userId, couponId, "id"), "","校验会员领到优惠券");
		}
	}
	
	@Test
	public void test_couponSend2() {
		Reporter.log("新增满减券－检查发放对象：指定条件");
		
		String days = "5";//M天内有消费
		String orderCount = "30";//累计订单数
		String orderSum = "10000";//累计消费金额
		Reporter.log("1、发放满减券，"+days+"天内有消费，或累计订单数达"+orderCount+"，或累计消费满"+orderSum);
		String couponId = this.couponSend("2", orderSum+","+orderCount+","+days);
		
		Reporter.log("校验"+days+"内消费过的用户，应当收到优惠券");
		JSONArray user_idList1 = memberScoreData.getUserId_PayTime(days);
		for (int i = 0; i < user_idList1.size(); i++) {
			String userId = user_idList1.getJSONObject(i).getString("user_id");
			assertNotEquals(couponData.getCouponItem_ById(userId, couponId, "id"), "",days+"天内下订单的会员应当有收到优惠券，优惠券（"+couponId+"）会员("+userId+")");
		}

		Reporter.log("累计订单数大于"+orderCount+"的所有用户,应当收到优惠券");
		JSONArray user_idList2 = memberScoreData.getUserId_OrderCount(orderCount);
		for (int i = 0; i < user_idList2.size(); i++) {
			String userId = user_idList2.getJSONObject(i).getString("user_id");
			assertNotEquals(couponData.getCouponItem_ById(userId, couponId, "id"), "","累计订单数达到"+orderCount+"条应当有优惠券，优惠券（"+couponId+"）会员("+userId+")");
		}
		
		Reporter.log("累计消费大于"+orderSum+"的所有用户,应当收到优惠券");
		JSONArray user_idList3 = memberScoreData.getUserId_orderSum(orderSum);
		for (int i = 0; i < user_idList3.size(); i++) {
			String userId = user_idList3.getJSONObject(i).getString("user_id");
			assertNotEquals(couponData.getCouponItem_ById(userId, couponId, "id"), "","累计消费总额达到"+orderSum+"元应当有优惠券，优惠券（"+couponId+"）会员("+userId+")");
		}

		Reporter.log(days+"天没有消费，且累计订单笔数<"+orderCount+",且累计消费金额<"+orderSum+"的用户（取前20个校验）应当没有优惠券");
		JSONArray user_idList4 = memberScoreData.getUserId_noMatch(days, orderCount, orderSum);
		for (int i = 0; i < user_idList4.size(); i++) {
			String userId = user_idList4.getJSONObject(i).getString("user_id");
			assertEquals(couponData.getCouponItem_ById(userId, couponId, "id"), "","未达到条件的用户应当没有优惠券，优惠券（" + couponId + "）会员(" + userId + ")");
		}
	}
	
	@Test
	public void test_couponSend1() {
		Reporter.log("新增满减券-检查发放对象：向所有会员发放（需要会员自己去领取）");
		
		String couponId = this.couponSend("1", "");
		
		String username = memberData.get_account("phone");
		Reporter.log("用户登录ios:"+username);
		JSONObject loginResult = login.login(username, "123456li", "ios");
		assertEquals(loginResult.getString("code"), "10000","登录校验");
		JSONObject userdata = loginResult.getJSONObject("data");

		Reporter.log("用户领取优惠券："+couponId);
		JSONObject getCouponResult = couponUse.getCoupon(userdata, couponId, "ios");
		assertEquals(getCouponResult.getString("code"), "10000","获取优惠券校验"+getCouponResult);
		assertNotEquals(couponData.getCouponItem_ById(userdata.getString("userId"), couponId, "id"), "","校验会员领到优惠券");
	}
	
	@Test
	public void test_CouponUse1() {
		Reporter.log("检查可使用商品范围（以满减券为准）：全部商品可用");
		String platform = "ios";
		
		String username = memberData.get_account("phone");//最新一个用户
		Reporter.log("1）获取最新注册的一个用户："+username);
		JSONObject loginResult = login.login(username, "123456li", platform);
		assertEquals(loginResult.getString("code"), "10000","校验登录"+loginResult);
		JSONObject userdata = loginResult.getJSONObject("data");
		
		Reporter.log("2）新增并发放优惠券：全部商品可用");
		String couponId = this.CouponUseGoods("1", "","4", userdata.getString("userId"));
		assertNotEquals(couponData.getCouponItem_ById(userdata.getString("userId"), couponId, "id"), "","校验会员领到优惠券。优惠券("+couponId+")用户("+userdata.getString("userId")+")");
		
		String sku = myConfig.getKeys("sku5");
		Reporter.log("3）选择[500现金+100福豆]的SKU＝"+sku+"，并下单");
		this.generateOrder(userdata, couponId, sku,platform);
		assertEquals(couponData.getCouponItem_ById(userdata.getString("userId"), couponId, "use_status"), "1","校验优惠券使用。优惠券("+couponId+")用户("+userdata.getString("userId")+")");
	}
	
	@Test
	public void test_couponUse2() {
		Reporter.log("检查可使用商品范围（以满减券为准）：指定商品可用");
		String platform = "ios";
		
		String username = memberData.get_account("phone");//最新一个用户
		Reporter.log("1）获取最新注册的一个用户"+username);
		JSONObject loginResult = login.login(username, "123456li", "ios");
		assertEquals(loginResult.getString("code"), "10000","校验登录"+loginResult);
		JSONObject userdata = loginResult.getJSONObject("data");
		
		JSONArray goodArray = goodData.getGoodId_lastGood("2");
		String goodList = goodArray.getJSONObject(0).getString("id");
		for (int i = 1; i < goodArray.size(); i++) {
			goodList = goodList+","+goodArray.getJSONObject(i).getString("id");
		}
		Reporter.log("2）新增并发放优惠券：指定商品可用，商品ID＝"+goodList);
		String couponId = this.CouponUseGoods("2", goodList,"4", userdata.getString("userId"));
		assertNotEquals(couponData.getCouponItem_ById(userdata.getString("userId"), couponId, "id"), "","校验会员领到优惠券。优惠券("+couponId+")用户("+userdata.getString("userId")+")");
		
		Reporter.log("3）第一个商品SKU下单");
		JSONArray skuArray = goodData.getSKU_goodId(goodArray.getJSONObject(0).getString("id"));
		String sku = skuArray.getJSONObject(baseData.getNum(0, skuArray.size()-1)).getString("sku_number");
		this.generateOrder(userdata, couponId, sku,platform);
		assertEquals(couponData.getCouponItem_ById(userdata.getString("userId"), couponId, "use_status"), "1","校验优惠券使用。优惠券("+couponId+")用户("+userdata.getString("userId")+")");
	
		Reporter.log("4）非指定的商品应当不可用用优惠券");
		JSONArray goodArray2 = goodData.getGoodId_lastGood("5");
		List<JSONObject> goodList2 = new ArrayList<JSONObject>();
		for (int i = 2; i < goodArray2.size(); i++) {
			JSONObject goodsJson = new JSONObject();
			goodsJson.put("goodsId", goodArray2.getJSONObject(i).getString("id"));
			goodsJson.put("goodsTotalAmount", "100");//默认给100，不是准确值，不过无所谓
			goodList2.add(goodsJson);
		}
	/*	JSONObject couponCanUseResult = couponUse.get_whetherUseCouponList(userdata, goodList2, "300", "ios");
		assertEquals(couponCanUseResult.getString("code"), "1000","可用优惠券查询"+couponCanUseResult);*/
	}
	
	@Test
	public void test_couponUse3() {
		Reporter.log("检查可使用商品范围（以满减券为准）：指定商品类别可用");
		String platform = "ios";
		
		String username = memberData.get_account("phone");//最新一个用户
		Reporter.log("1）获取最新注册的一个用户");
		JSONObject loginResult = login.login(username, "123456li", "ios");
		assertEquals(loginResult.getString("code"), "10000","校验登录"+loginResult);
		JSONObject userdata = loginResult.getJSONObject("data");
		
		Reporter.log("2）新增并发放优惠券：指定商品类别可用");
		JSONArray goodTypeArray = goodData.getGoodTypeId("2");
		String goodTypeList = goodTypeArray.getJSONObject(0).getString("goods_type_id");
		for (int i = 1; i < goodTypeArray.size(); i++) {
			goodTypeList = goodTypeList+","+goodTypeArray.getJSONObject(i).getString("goods_type_id");
		}
		String couponId = this.CouponUseGoods("3", goodTypeList,"4", userdata.getString("userId"));
		assertNotEquals(couponData.getCouponItem_ById(userdata.getString("userId"), couponId, "id"), "","校验会员领到优惠券。优惠券("+couponId+")用户("+userdata.getString("userId")+")");
		
		Reporter.log("3）第一个商品SKU下单");
		JSONArray goodIdArray = goodData.getGoodId_typeId(goodTypeArray.getJSONObject(0).getString("goods_type_id"));
		JSONArray skuArray = goodData.getSKU_goodId(goodIdArray.getJSONObject(baseData.getNum(0, goodIdArray.size()-1)).getString("id"));
		String sku = skuArray.getJSONObject(baseData.getNum(0, skuArray.size()-1)).getString("sku_number");
		this.generateOrder(userdata, couponId, sku,platform);
		assertEquals(couponData.getCouponItem_ById(userdata.getString("userId"), couponId, "use_status"), "1","校验优惠券使用。优惠券("+couponId+")用户("+userdata.getString("userId")+")");
	
	}
	
	/*使用优惠券下单*/
	public void generateOrder(JSONObject userdata,String couponId,String sku,String platform) {
		Reporter.log("[使用优惠券下单操作]---");
		Reporter.log("a）SKU加入购物车："+sku);
		JSONObject addCartResult = shoppingCart.addShoppingCart(userdata, sku, "1", platform);
		assertEquals(addCartResult.getString("code"), "10000", "加入购物车校验"+addCartResult);
		String cartId = addCartResult.getJSONObject("data").getString("cartId");
		
		String addressId = memberDeliveryAddress.getAddrId(userdata, platform);
		Reporter.log("b）获取会员的收货地址ID："+addressId);
		
		Reporter.log("c）查询购物车列表");
		JSONObject selectShoppingCartResult = shoppingCart.selectShoppingCart(userdata, platform);
		assertEquals(selectShoppingCartResult.getString("code"), "10000","查询购物车"+selectShoppingCartResult);
		JSONArray cartList = selectShoppingCartResult.getJSONObject("data").getJSONArray("cartList");
		
		Reporter.log("d）提交购物车");
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
		
		JSONObject submitShoppingCartResult = shoppingCart.submitShoppingCart(userdata, addressId, cartId, supplierOrderList, 0, couponId, 1, "", platform);
		assertEquals(submitShoppingCartResult.getString("code"), "10000","提交购物车"+submitShoppingCartResult);
		
		Reporter.log("e）生成订单");
		JSONObject generateOrderResult = tradeOrder.generateOrder(userdata, addressId,1, "0", cartId, false, supplierOrderList, 0, couponId, platform);
		assertEquals(generateOrderResult.getString("code"), "10000", "生成订单" + generateOrderResult);
	}
	
	/*优惠券发放对象（以满减券为准）
	 * @objectType 发放对象类型：1.全部会员、2.指定条件、3.指定标签、4.指定会员
	 * @objectValue 发放对象的
	 * */
	public String couponSend(String objectType, String objectValue) {
		Reporter.log("[运营中心操作]-----");
		Reporter.log("a）登录运营中心");
		JSONObject sysLoginResult = login.sysLogin("helen", "123456li");
		assertEquals(sysLoginResult.getString("code"), "10000", "运营中心登录" + sysLoginResult);
		JSONObject sysUser = sysLoginResult.getJSONObject("data");

		Reporter.log("b）新增满减券：优先抵扣福豆、无门槛、全部商品可用，发放对象类型＝"+objectType+"(1.全部会员、2.指定条件、3.指定标签、4.指定会员)");
		JSONObject addCouponResult = couponManager.addFullSubCoupon(sysUser,"用于校验发放对象的满减券","250", "26","3", "0","", "1", "", objectType, objectValue);
		assertEquals(addCouponResult.getString("code"), "10000", "新增优惠券" + addCouponResult);
		String couponId = couponData.getLastCoupon().getString("id");// 优惠券ID

		Reporter.log("c）优惠券审核通过");
		JSONObject auditCouponResult = couponManager.auditCoupon(sysUser, couponId,"3");
		assertEquals(auditCouponResult.getString("code"), "10000", "审核优惠券" + auditCouponResult);

		Reporter.log("d）发放优惠券");
		JSONObject sendCouponResult = couponManager.sendCoupon(sysUser, couponId);
		assertEquals(sendCouponResult.getString("code"), "10000", "发放优惠券" + sendCouponResult);
		
		return couponId;
	}
	
	/*优惠券可使用商品（以满减券为准）
	 * @couponUseScop 可使用商品范围类型：1表示全部商品，2表示指定商品，3表示指定分类的商品 
	 * @useList 指定商品ID或商品类别ID,多个ID间用逗号隔开
	 * @objectType 发放对象类型：1.全部会员、2.指定条件、3.指定标签、4.指定会员
	 * */
	public String CouponUseGoods(String couponUseScop,String useList,String objectType,String userIds) {
		Reporter.log("[运营中心发放优惠券操作]-----");
		Reporter.log("a）登录运营中心");
		JSONObject sysLoginResult = login.sysLogin("helen", "123456li");
		assertEquals(sysLoginResult.getString("code"), "10000", "运营中心登录" + sysLoginResult);
		JSONObject sysUser = sysLoginResult.getJSONObject("data");

		Reporter.log("b）新增满减券：优先抵扣福豆、无门槛、全部商品可用");
		JSONObject addCouponResult = couponManager.addFullSubCoupon(sysUser,"用于校验可使用商品的满减券","1", "25","3", "0","", couponUseScop, useList, objectType, userIds);
		assertEquals(addCouponResult.getString("code"), "10000", "新增优惠券" + addCouponResult);
		String couponId = couponData.getLastCoupon().getString("id");// 优惠券ID

		Reporter.log("c）优惠券审核通过");
		JSONObject auditCouponResult = couponManager.auditCoupon(sysUser, couponId,"3");
		assertEquals(auditCouponResult.getString("code"), "10000", "审核优惠券" + auditCouponResult);

		Reporter.log("d）发放优惠券");
		JSONObject sendCouponResult = couponManager.sendCoupon(sysUser, couponId);
		assertEquals(sendCouponResult.getString("code"), "10000", "发放优惠券" + sendCouponResult);
		
		return couponId;
	}

}
