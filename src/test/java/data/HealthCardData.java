/**
 * @author helen
 * @date 2019年4月1日
 */
package data;

import com.alibaba.fastjson.JSONArray;

import common.MysqlConnect;

/**
 * @Description:康养卡业务查询
 */
public class HealthCardData {
	MysqlConnect mysqlConnect = new MysqlConnect();
	JSONArray arrayResult = new JSONArray();
	
	/*从康养卡订单表t_health_card_order中获取信息*/
	public String getValue_orderId(String orderId,String key) {
		String value = "";
		arrayResult = mysqlConnect.getData("SELECT "+key+" FROM t_health_card_order where id="+orderId);
		if (arrayResult.size()>0) {
			value = arrayResult.getJSONObject(0).getString(key);
		}
		return value;
	}
	
	/*在康养卡升级订单表t_health_card_uporder中获取信息*/
	public String getValeu_upOrderId(String upOrderId,String key) {
		String value = "";
		arrayResult = mysqlConnect.getData("SELECT "+key+" FROM t_health_card_uporder where id="+upOrderId);
		if (arrayResult.size()>0) {
			value = arrayResult.getJSONObject(0).getString(key);
		}
		return value;
	}

}
