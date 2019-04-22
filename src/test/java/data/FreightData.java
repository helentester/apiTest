/**
 * @author helen
 * @date 2018年11月27日
 */
package data;

import com.alibaba.fastjson.JSONArray;

import common.MysqlConnect;

/**
 * @Description:商家的运费模板相关数据
 */
public class FreightData {
	MysqlConnect mysqlConnect = new MysqlConnect();
	JSONArray arrayResult = new JSONArray();
	
	/*根据模板ID获取模板信息 p_freight_template
	 * @templateId 模板ID
	 * @key 字段名称
	 * */
	public String getTemplateValue(String templateId,String key) {
		String value = "";
		arrayResult = mysqlConnect.getData("SELECT "+key+" from p_freight_template where template_id="+templateId);
		if (arrayResult.size()>0) {
			value = arrayResult.getJSONObject(0).getString(key);
		}
		return value;
	}
	
	/*查询商家下的运费模板
	 * @sellerId 商家ID
	 * @TemplateName  模板名称
	 * */
	public String getTemplateId(String sellerId,String TemplateName) {
		String templateId = "";
		arrayResult = mysqlConnect.getData("SELECT template_id from p_freight_template where supplier_id="+sellerId+" and name like '%"+TemplateName+"%'");
		if (arrayResult.size()>0) {
			templateId = arrayResult.getJSONObject(0).getString("template_id");
		}
		
		return templateId;
	}

}
