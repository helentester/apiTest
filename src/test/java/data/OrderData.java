/**
 * @author helen
 * @date 2018年11月15日
 */
package data;

import common.MysqlConnect;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @Description:订单数据
 */
public class OrderData {
	MysqlConnect mysqlConnect = new MysqlConnect();
	JSONArray arrayResult = new JSONArray();
	
	/*修改康养订单的订单状态h_orders*/
	public void updateHOrderStatus(String orderId,String status,String leave_date) {
		mysqlConnect.updateData("UPDATE h_orders set order_status="+status+",leave_date='"+leave_date+"' where id="+orderId);
	}
	
	/*订单统计表（t_order_statistics）获取订单的优惠券、积分、现金的分摊
	 * @orderSN  订单号
	 * */
	public JSONObject get_statisticsPayShare(String orderSN) {
		JSONObject statisticsPayShare = new JSONObject();
		arrayResult =mysqlConnect.getData("SELECT score,coupon,pay_price from t_order_statistics where order_no='"+orderSN+"'");
		statisticsPayShare.put("resulSize", String.valueOf(arrayResult.size()));
		if (arrayResult.size()>0) {
			statisticsPayShare.put("score", arrayResult.getJSONObject(0).getString("score"));
			statisticsPayShare.put("coupon", arrayResult.getJSONObject(0).getString("coupon"));
			statisticsPayShare.put("pay_price", arrayResult.getJSONObject(0).getString("pay_price"));
		}
		return statisticsPayShare;
	}
	
	/*（t_orders）订单维度的优惠券、福豆、现金的分摊
	 * @orderSN 订单号
	 * */
	public JSONObject get_PayShare(String orderSN) {
		JSONObject payShare = new JSONObject();
		try {
			Thread.sleep(1000);
			arrayResult = mysqlConnect.getData("SELECT coupon_discount_money,discount_price,pay_price from t_orders where order_sn='"+orderSN+"'");
			payShare.put("resulSize", String.valueOf(arrayResult.size()));
			if (arrayResult.size()>0) {
				payShare.put("score", arrayResult.getJSONObject(0).getString("discount_price"));
				payShare.put("coupon", arrayResult.getJSONObject(0).getString("coupon_discount_money"));
				payShare.put("pay_price", arrayResult.getJSONObject(0).getString("pay_price"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return payShare;
	}
	
	/*根据订单ID获取所有订单商品ID  t_order_items
	 * @orderId 订单ID
	 * */
	public JSONArray getOrderItemsId_orderId(String orderId) {
		return mysqlConnect.getData("SELECT id FROM t_order_items where order_id="+orderId);
	}
	
	/*根据订单商品ID获取订单商品信息  t_order_items
	 * @orderItemId
	 * @key
	 * */
	public String getOrderItem_orderItemId(String orderItemId,String key) {
		String value = "";
		arrayResult = mysqlConnect.getData("SELECT "+key+" from t_order_items where order_id="+orderItemId);
		if (arrayResult.size()>0) {
			value = arrayResult.getJSONObject(0).getString(key);
		}
		return value;
	}
	
	/*根据[订单号]查询订单信息 t_orders
	 * @orderSn 订单号
	 * @key 
	 * */
	public String getOrder_orderSn(String orderSn,String key) {
		String value = "";
		arrayResult = mysqlConnect.getData("SELECT "+key+" from t_orders where order_sn='"+orderSn+"'");
		if (arrayResult.size()>0) {
			value = arrayResult.getJSONObject(0).getString(key);
		}
		return value;
	}
	
	
	/*累计消费总额大于于orderSum的所有用户*/
	public JSONArray getUserId_orderSumPlu(String orderSum) {
		return arrayResult = mysqlConnect.getData("SELECT user_id,SUM(goods_total_price) as s from t_orders where order_status>2 GROUP BY user_id HAVING s>"+orderSum);
	}
	
	/*累计订单数大于orderCount的所有用户ID*/
	public JSONArray getUserId_orderCountPlu(String orderCount) {
		return arrayResult = mysqlConnect.getData("SELECT user_id,COUNT(*) as c from t_orders where order_status>2 GROUP BY user_id HAVING c>"+orderCount);
	}
	
	/*查询days天内消费过的所有用户ID
	 * @days
	 * */
	public JSONArray getUserId_hadOrderPay(String days) {
		return arrayResult = mysqlConnect.getData("SELECT user_id from t_orders where order_status>2 and FROM_UNIXTIME(update_time)>DATE_SUB(CURDATE(), INTERVAL "+days+" DAY) GROUP BY user_id");
	}
	
	/*查询days天内没有消费过,且累计订单数少于orderCount,且累计订单数少于orderSum的20个用户ID
	 * @days 天数
	 * */
	public JSONArray getUserId_noMatchRule(String days,String orderCount,String orderSum) {
		arrayResult = mysqlConnect.getData("SELECT user_id,COUNT(*) as c ,SUM(goods_total_price) as s from t_orders where order_status>2 AND FROM_UNIXTIME(update_time)<DATE_SUB(CURDATE(), INTERVAL "+days+" DAY)  GROUP BY user_id HAVING c<"+orderCount+" and s<"+orderSum+" ORDER BY c DESC LIMIT 20");
		return arrayResult;
	}

}
