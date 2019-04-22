/**
 * @author helen
 * @date 2018年9月20日
 */
package data;
import common.MysqlConnect;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @Description:优惠券相关数据
 */
public class CouponData {
	MysqlConnect mysqlConnect = new MysqlConnect();
	JSONArray arrayResult = new JSONArray();
	
	/*获取最新的优惠券*/
	public JSONObject getLastCoupon() {
		arrayResult = mysqlConnect.getData("SELECT * from m_marketing_coupon ORDER BY id desc LIMIT 1;");
		return arrayResult.getJSONObject(0);
	}
	
	/*根据优惠券ID和用户ID，获取信息，m_coupons_get_use_record
	 * @userId 用户Id
	 * @couponId 优惠券ID
	 * @key 字段名称
	 * */
	public String getCouponItem_ById(String userId,String couponId,String key) {
		String value = "";
		arrayResult = mysqlConnect.getData("SELECT "+key+" from m_coupons_get_use_record WHERE coupon_id="+couponId+" and user_id="+userId);
		if (arrayResult.size()>0) {
			value = arrayResult.getJSONObject(0).getString(key);
		}
		return value;
	}
	
	/*获取优惠券ID
	 * @couponName 优惠券名称
	 * */
	public String get_id(String couponName) {
		String id = "";
		arrayResult = mysqlConnect.getData("SELECT id from m_marketing_coupon where name='"+couponName+"'");
		if (arrayResult.size()==0) {
			id="0";
		}
		else {
			id = arrayResult.getJSONObject(0).getString("id");
		}
		return id;
	}
	
	/*根据优惠券ID，获取优惠券某个字段信息  m_marketing_coupon
	 * @couponId 优惠券ID
	 * @key 字段名称
	 * */
	public String getCouponValue_ById(String couponId,String key) {
		String value = "";
		arrayResult = mysqlConnect.getData("SELECT "+key+" from m_marketing_coupon where id="+couponId);
		if (arrayResult.size()==0) {
			value="-1";
		}
		else {
			value = arrayResult.getJSONObject(0).getString(key);
		}
		
		return value;
	}

	/*获取用户未领取，并且还在有效期内的优惠券
	 * @userId 用户ID
	 * */
	public String getCouponId(String userId) {
		String couponId = "";
		arrayResult = mysqlConnect.getData("SELECT c.id from m_marketing_coupon c LEFT JOIN m_coupons_get_use_record r on c.id=r.coupon_id where coupon_use_scop=1 and end_time>NOW() and sent_status=2 and residue_num>0 and r.user_id !="+userId+" ORDER BY c.id DESC LIMIT 1");
		if (arrayResult.size()>0) {
			couponId = arrayResult.getJSONObject(0).getString("id");
		}
		return couponId;
	}
}
