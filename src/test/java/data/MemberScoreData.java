/**
 * @author helen
 * @date 2018年11月20日
 */
package data;

import common.MysqlConnect;
import com.alibaba.fastjson.JSONArray;

/**
 * @Description:会员积分等信息查询
 */
public class MemberScoreData {
	MysqlConnect mysqlConnect = new MysqlConnect();
	JSONArray arrayResult = new JSONArray();
	
	/*days天内消费过的所有用户（以支付时间为准）*/
	public JSONArray getUserId_PayTime(String days) {
		return mysqlConnect.getData("SELECT user_id from m_member_score where last_order_time>DATE_SUB(NOW(), INTERVAL "+days+" DAY) ");
	}
	
	/*累计订单数大于orderCount的所有用户*/
	public JSONArray getUserId_OrderCount(String orderCount) {
		return mysqlConnect.getData("SELECT user_id from m_member_score where orders_number>"+orderCount);
	}
	
	/*累计消费金额大于orderSum的所有用户（不考虑退款，支付就算）*/
	public JSONArray getUserId_orderSum(String orderSum) {
		return mysqlConnect.getData("SELECT user_id from m_member_score where orders_money_total>"+orderSum);
	}
	
	/*days天没有消费记录，且累计订单数小于orderCount，且累计消费金额小于orderSum的20个用户*/
	public JSONArray getUserId_noMatch(String days,String orderCount,String orderSum) {
		return mysqlConnect.getData("SELECT user_id from m_member_score where last_order_time<DATE_SUB(NOW(), INTERVAL "+days+" DAY) AND orders_number<"+orderCount+" and orders_money_total<"+orderSum+" ORDER BY user_id desc LIMIT 20");
	}
	
	/*获取用户的信息,表m_member_score
	 * @userId 账号ID
	 * @keyName 字段名
	 * */
	public double getMemberScore_ByUserId(String userId,String keyName) {
		double keyValue = 0;
		arrayResult = mysqlConnect.getData("SELECT "+keyName+" from m_member_score WHERE user_id="+userId);
		if (arrayResult.size()>0) {
			keyValue = Double.parseDouble(arrayResult.getJSONObject(0).getString(keyName)) ;
		}
		return keyValue;
	}

}
