/**
 * @author helen
 * @date 2018年10月24日
 */
package data;

import java.util.HashMap;
import java.util.List;
import common.BaseData;
import common.MysqlConnect;
import com.alibaba.fastjson.JSONArray;

/**
 * @Description:签到抽奖数据查询
 */
public class PrizeData {
	MysqlConnect mysqlConnect = new MysqlConnect();
	BaseData baseData = new BaseData();
	List<HashMap<String, String>> resultSet;
	JSONArray arrayResult = new JSONArray();
	
	/*获取有效结束时间大于当前时候，且是启用状态的模板  ms_prize_template
	 * @templateId 模板Id
	 * */
	public String get_enabelModel(String templateId) {
		String count = "";
		arrayResult = mysqlConnect.getData("SELECT count(*) as c from ms_prize_template where end_time>NOW() and status=1 and id="+templateId);
		if (arrayResult.size()>0) {
			count = arrayResult.getJSONObject(0).getString("c");
		}
		return count;
	}
	
	/*获取当前正在使用的模板ID  ms_prize_template*/
	public String get_inUseTemplatID() {
		String id = "";
		arrayResult = mysqlConnect.getData("SELECT id from ms_prize_template where begin_time<NOW()<end_time and `status`=1 ORDER BY id DESC");
		if (arrayResult.size()>0) {
			id = arrayResult.getJSONObject(0).getString("id");
		}
		
		return id;
	}
	
	/*根据模板Id,获取模板详细信息  ms_prize_template
	 * @templateId 模板id
	 * */
	public JSONArray get_itemList(String templateId) {
		return mysqlConnect.getData("SELECT t.name as templateName,i.template_id as templateId,i.id,i.type,i.item_type as itemType,i.item_name as itemName,i.prize_name as prizeName,i.draw_rate as drawRate,i.quantity,i.cost,i.sale_price as salePrice,i.cost_price as costPrice from ms_prize_item i LEFT JOIN ms_prize_template t on i.template_id=t.id where i.template_id="+templateId);
	}
	
	/*根据模板Id，获取相应字段
	 * @templateId 模板id
	 * @keyName 字段名称
	 * */
	public String get_keyValue(String templateId,String keyName) {
		String keyValue = "";
		resultSet = mysqlConnect.selectSql("SELECT "+keyName+" from ms_prize_template where id="+templateId);
		if (resultSet.size()>0) {
			keyValue = resultSet.get(0).get(keyName);
		}
		return keyValue;
	}
	
	/*根据奖项ID，获取信息
	 * @prizeItemId  奖项ID
	 * @key  字段名称
	 * */
	public String get_itemInfoById(String prizeItemId,String key) {
		String value = "";
		arrayResult = mysqlConnect.getData("SELECT "+key+" from ms_prize_item  WHERE id="+prizeItemId);
		if (arrayResult.size()>0) {
			value = arrayResult.getJSONObject(0).getString(key);
		}
		return value;
	}
	
	/*查询一个模板的中奖几率之和,表ms_prize_item
	 * @templateId 模板ID
	 * */
	public String get_templateDrawRate(String templateId) {
		String templateDrawRate = "";
		resultSet = mysqlConnect.selectSql("SELECT SUM(draw_rate) as sumRate from ms_prize_item where template_id="+templateId);
		if (resultSet.size()>0) {
			templateDrawRate = resultSet.get(0).get("sumRate");
		}
		return templateDrawRate;
	}
	
	/*根据模板ID，修改模板的有效使用时间
	 * @templateId 模板名称
	 * @beginTime 开始时间
	 * @endTime 结束时间
	 * */
	public void update_enableTime(String templateId,String beginTime,String endTime) {
		mysqlConnect.updateData("UPDATE ms_prize_template SET begin_time='"+beginTime+"',end_time='"+endTime+"' where id="+templateId);
	}

}
