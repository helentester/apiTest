/**
 * @author helen
 * @date 2018年11月2日
 */
package marketing;

import static org.testng.Assert.assertEquals;

import common.MyAPI;
import data.CouponData;
import data.GoodData;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import user.Login;

/**
 * @Description:前台优惠券使用：营销模块优惠券相关接口
 */
public class CouponUse {
	GoodData goodData = new GoodData();
	MyAPI myAPI = new MyAPI();
	CouponManager couponManager = new CouponManager();
	CouponData couponData = new CouponData();

	
	/*获取优惠券
	 * @account 用户账号
	 * @couponId 优惠券ID
	 * */
	public JSONObject getCoupon(JSONObject userdata,String couponId,String platform) {
		JSONObject params = new JSONObject();
		params.put("couponId", couponId);
		params.put("userAccount", userdata.getString("loginAccount"));
		params.put("userId", userdata.getString("userId"));
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/marketing/appApi/v1/coupon/get", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/marketing/appApi/h5/coupon/get", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/marketing/pcApi/coupon/get", params);
		}
		else {
			System.out.println(platform+"平台没有[获取优惠券]接口");
		}
		return result;
	}
	
	/*判断是否有优惠券可以使用返回可以使用优惠券列表接口*/
	public JSONObject get_whetherUseCouponList(JSONObject userdata,JSONArray goodsArray,String orderTotalAmount,String platform) {
		JSONObject params = new JSONObject();
		params.put("goodsList", goodsArray.toString());//商品集合json 必填 ,
		params.put("orderTotalAmount", orderTotalAmount);//订单总金额 ,
		params.put("userId", userdata.getString("userId"));
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/marketing/appApi/v1/whetherUseCoupon/list", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/marketing/appApi/h5/whetherUseCoupon/list", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/marketing/pcApi/whetherUseCoupon/list", params);
		}
		else {
			System.out.println(platform+"平台没有[判断是否有优惠券可以使用返回可以使用优惠券列表]接口");
		}
		System.out.println(params);
		System.out.println(result);
		return result;
	}
	
	/*我的优惠券列表接口
	 * @useType (integer, optional): 使用状态 必填 0表示未使用，1表示使用，2表示已经过期,3表示全部 
	 * */
	public JSONObject myCouponList(JSONObject userdata,int useType,String platform) {
		JSONObject params = new JSONObject();
		params.put("useType", useType);
		params.put("userId", userdata.getString("userId"));
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/marketing/appApi/v1/myCoupon/list", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/marketing/appApi/h5/myCoupon/list", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/marketing/pcApi/myCoupon/list", params);
		}
		else {
			System.out.println(platform+"平台没有[我的优惠券列表]接口");
		}
		
		return result;
	}
	
	/*领券入口，商品详情判断是否有优惠券可以领取,如果有返回所有优惠券信息*/
	public JSONObject whetherGetCoupon(JSONObject userdata,String goodsId,String platform) {
		String goodsTypeId = goodData.getGood_byId(goodsId, "goods_type_id");
		JSONObject params = new JSONObject();
		params.put("goodsTypeId", goodsTypeId);
		params.put("goodsId", goodsId);
		params.put("userId", userdata.getString("userId"));
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/marketing/appApi/v1/whetherGetCoupon", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/marketing/appApi/h5/whetherGetCoupon", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/marketing/pcApi/whetherGetCoupon", params);
		}
		else {
			System.out.println(platform+"平台没有[领券入口]接口");
		}
		
		return result;
	}
	
	/*
	 * 获取优惠券：运营后台新增优惠券－审核－发放－领取
	 * 
	 * @account 用户账号
	 */
	public String getCoupon(JSONObject userData, int discounts,String platform) {
		Login login = new Login();
		// 运营中心操作-----------------
		// 登录
		JSONObject sysLoginResult = login.sysLogin("helen", "123456li");
		assertEquals(sysLoginResult.getString("code"), "10000", "运营中心登录" + sysLoginResult);
		JSONObject sysUser = sysLoginResult.getJSONObject("data");
		// 新增
		JSONObject addCouponResult = couponManager.addFullSubCoupon(sysUser,"商城满减券的使用","1", String.valueOf(discounts),"3", "0","", "1", "", "1", "");
		assertEquals(addCouponResult.getString("code"), "10000", "新增优惠券" + addCouponResult);
		String couponId = couponData.getLastCoupon().getString("id");// 优惠券ID
		// 审核
		JSONObject auditCouponResult = couponManager.auditCoupon(sysUser, couponId,"3");
		assertEquals(auditCouponResult.getString("code"), "10000", "审核优惠券" + auditCouponResult);
		// 发放
		JSONObject sendCouponResult = couponManager.sendCoupon(sysUser, couponId);
		assertEquals(sendCouponResult.getString("code"), "10000", "发放优惠券" + sendCouponResult);
		
		// 前台用户操作-----------------
		// 领取
		JSONObject getCouponResult = this.getCoupon(userData, couponId,platform);
		assertEquals(getCouponResult.getString("code"), "10000", "获取优惠券校验" + getCouponResult);
		
		return couponId;
	}

}
