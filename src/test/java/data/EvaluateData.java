/**
 * @author helen
 * @date 2018年11月22日
 */
package data;

import com.alibaba.fastjson.JSONArray;

import common.MysqlConnect;

/**
 * @Description:评论数据
 */
public class EvaluateData {
	MysqlConnect mysqlConnect = new MysqlConnect();
	JSONArray arrayResult = new JSONArray();
	
	/*根据订单ID查询最新一条评论
	 * @evaluateId
	 * @key
	 * */
	public String getEvaluateDetail_evaluateId(String evaluateId,String key) {
		String value="";
		arrayResult = mysqlConnect.getData("SELECT "+key+" from t_order_item_evaluate_detail where evaluate_id="+evaluateId+" order by evaluate_detail_id desc");
		if (arrayResult.size()>0) {
			value = arrayResult.getJSONObject(0).getString(key);
		}
		return value;
	}
	

}
