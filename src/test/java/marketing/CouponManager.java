/**
 * @author helen
 * @date 2018年11月2日
 */
package marketing;

import common.BaseData;
import common.MyAPI;

import com.alibaba.fastjson.JSONObject;

/**
 * @Description:( 营销（优惠券）相关接口API(marketing))coupon-manager-controller : Coupon Manager Controller
 * @备注：当优惠券的[发放对象]是“所有会员”时，需要会员自已去领取，其他的条件的在执行[发放]接口时，送给会员
 */
public class CouponManager {
	BaseData baseData = new BaseData();
	MyAPI myAPI = new MyAPI();
	
	
	/*新增优惠券
	 * @couponType (string, optional): 优惠券类型:1表示注册赠券，2表示商品赠券，3表示消费赠券 4现金券、 5福豆券 
	 * 
	 * @totalNum 发放张数
	 * 
	 * @discounts 面值
	 * 
	 * @deductionType (string, optional): 抵扣类型 1.仅抵扣现金 2.仅抵扣福豆 3.优先抵扣福豆 ,
	 * 
	 * @isUseThreshold 使用门槛：0无门槛,1有门槛
	 * 
	 * @couponGiveScope 消费赠券赠送范围类型：1表示任何消费均赠送 ，2表示购买指定商品赠送，3表示购买指定分类的商品赠送
	 * 
	 * @giveList 消费赠送对象
	 * 
	 * @couponUseScop 可使用商品范围类型：1表示全部商品，2表示指定商品，3表示指定分类的商品 ,
	 * 
	 * @useList 指定商品ID或商品类别ID
	 * 
	 * @objectType 发放对象类型：1.全部会员、2.指定条件、3.指定标签、4.指定会员
	 * 
	 * @objectValue 发放对象的
	 * */
	public JSONObject addCoupon(JSONObject userdata,String couponType,String couponName,String totalNum, String discounts,String deductionType, String isUseThreshold,
			String thresholdValue,String couponGiveScope,String giveList,String couponUseScop, String useList, String objectType, String objectValue) {
		JSONObject params = new JSONObject();
		params.put("couponType", couponType);// 优惠券类型:1表示注册赠券，2表示商品赠券，3表示消费赠券 4现金券、 5福豆券 
		params.put("name", couponName);// 优惠券名称
		params.put("totalNum", totalNum);// 发放总数
		params.put("discounts", discounts);// 面值
		params.put("deductionType", deductionType);//抵扣类型 1.仅抵扣现金 2.仅抵扣福豆 3.优先抵扣福豆 
		params.put("effectTime", baseData.getTimeStamp13(-1));// 有效期开始时间:前一天
		params.put("endTime", baseData.getTimeStamp13(1));// 有效期结束时间：后一天
		params.put("createById", userdata.getString("userId"));// 用户UID
		
		// 设置使用门槛
		params.put("isUseThreshold", isUseThreshold);// 使用门槛：0无门槛,1有门槛
		if (isUseThreshold.equals("1")) {
			params.put("thresholdValue", thresholdValue);// 使用门槛值
		}
		
		//设置消费券赠送范围
		params.put("couponGiveScope", couponGiveScope);// 消费赠券赠送范围类型：1表示任何消费均赠送 ，2表示购买指定商品赠送，3表示购买指定分类的商品赠送
		if (couponGiveScope.equals("2")) {
			params.put("giveGoodsList", giveList);
		} else if (couponGiveScope.equals("3")) {
			params.put("giveGoodsTypeId", giveList);
		}
		
		// 设置可使用商品范围
		params.put("couponUseScop", couponUseScop);// 可使用商品范围类型：1表示全部商品，2表示指定商品，3表示指定分类的商品 ,
		if (couponUseScop.equals("2")) {
			params.put("useGoodsList", useList);// 可使用商品指定商品：商品ID,多个商品ID间用逗号隔开 ,
		} else if (couponUseScop.equals("3")) {
			params.put("useGoodsTypeId", useList);// 可使用商品指定分类：商品分类ID,多个分类ID间用逗号隔开
		}

		// 设置发放对象
		params.put("objectType", objectType);// 发放对象类型：1.全部会员、2.指定条件、3.指定标签、4.指定会员
		if (objectType.equals("2")) {// 指定条件
			params.put("objectValue", objectValue);// 发放对象值，多个值之前用逗号隔开，对象值有标签ID或会员ID,如里是指定条件三个条件一定要按“累计消费金额”、“累计订单数量”、“天之内消费过”的顺序且没有选也要给个空如:,,
													// ,
		} else if (objectType.equals("3")) {// 指定标签
			params.put("objectValue", objectValue);
		} else if (objectType.equals("4")) {// 指定会员
			params.put("objectValue", objectValue);
		}
		
		JSONObject result = myAPI.sysPost(userdata, "/marketingCenter/coupon/manager/add", params);
		return result;
	}
	
	/*新增现金券*/
	public JSONObject addCashCoupon(JSONObject userdata,String couponName,String totalNum, String discounts, String isUseThreshold,String thresholdValue,
			String couponUseScop, String useList, String objectType, String objectValue) {
		
		JSONObject result = this.addCoupon(userdata, "4",couponName,totalNum,  discounts, "1", isUseThreshold, thresholdValue,
				"", "", couponUseScop, useList, objectType, objectValue);
		return result;
	}
	
	/*新增福豆券*/
	public JSONObject addFudouCoupon(JSONObject userdata,String couponName,String totalNum, String discounts, String isUseThreshold,String thresholdValue,
			String couponUseScop, String useList, String objectType, String objectValue) {
		
		JSONObject result = this.addCoupon(userdata,"5", couponName,totalNum,  discounts, "2", isUseThreshold, thresholdValue,
				"", "", couponUseScop, useList, objectType, objectValue);
		return result;
	}
	
	/*新增注册券*/
	public JSONObject addRegistryCoupon(JSONObject userdata,String couponName,String totalNum, String discounts,String deductionType, String isUseThreshold,String thresholdValue,
			String couponUseScop, String useList) {
		
		JSONObject result = this.addCoupon(userdata,"1", couponName,totalNum,discounts, deductionType, isUseThreshold, thresholdValue,
				"", "", couponUseScop, useList, "", "");
		return result;
	}
	
	/*新增满减券*/
	public JSONObject addFullSubCoupon(JSONObject userdata,String couponName,String totalNum, String discounts,String deductionType, String isUseThreshold,
			String thresholdValue,String couponUseScop, String useList, String objectType, String objectValue) {
		JSONObject result = this.addCoupon(userdata,"2", couponName,totalNum,  discounts, deductionType, isUseThreshold, thresholdValue,
				"", "", couponUseScop, useList, objectType, objectValue);
		return result;
	}

	/*新增消费券*/
	public JSONObject addConsumCoupon(JSONObject userdata, String couponName, String totalNum, String discounts,
			String deductionType, String isUseThreshold, String thresholdValue, String couponGiveScope, String giveList,
			String couponUseScop, String useList, String objectType, String objectValue) {

		JSONObject result = this.addCoupon(userdata, "3", couponName, totalNum, discounts, deductionType,
				isUseThreshold, thresholdValue, couponGiveScope, giveList, couponUseScop, useList, objectType,
				objectValue);
		return result;
	}

	/* 审核优惠券
	 * @couponId 优惠券ID
	 * @auditStatus 审核状态： 1表示未审核，2表示审核未通过，3表示审核以通过
	 */
	public JSONObject auditCoupon(JSONObject userdata, String couponId,String auditStatus) {
		JSONObject params = new JSONObject();
		params.put("createById", userdata.getString("userId"));// 审核人ID
		params.put("couponId", couponId);// 优惠券ID
		params.put("auditStatus", auditStatus);// 审核状态－3（通过）
		params.put("auditRemark", "通过接口审核优惠券");
		
		JSONObject result = myAPI.sysPost(userdata, "/marketingCenter/coupon/manager/audit", params);
		return result;
	}

	/*
	 * 发放优惠券
	 * 
	 * @couponId 优惠券ID
	 */
	public JSONObject sendCoupon(JSONObject userdata, String couponId) {
		JSONObject params = new JSONObject();
		params.put("createById", userdata.getString("userId"));
		params.put("couponId", couponId);
		JSONObject result = myAPI.sysPost(userdata, "/marketingCenter/coupon/manager/sent", params);
		return result;
	}

	/*结束发放优惠券*/
	public JSONObject sentover(JSONObject userdata, String couponId) {
		JSONObject params = new JSONObject();
		params.put("couponId", couponId);
		params.put("createById", userdata.getString("userId"));
		JSONObject result = myAPI.sysPost(userdata, "/marketingCenter/coupon/manager/sentover", params);
		return result;
	}
	
	/*查看优惠券明细*/
	public JSONObject Detail(JSONObject userdata,String couponId) {
		JSONObject params = new JSONObject();
		params.put("couponId", couponId);
		params.put("page", "1");
		params.put("pageSize", "20");
		JSONObject result = myAPI.sysPost(userdata, "/marketingCenter/coupon/manager/detail", params);
		return result;
	}
}
