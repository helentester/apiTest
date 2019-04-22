/**
 * @author helen
 * @date 2018年10月10日
 */
package data;

import java.util.HashMap;
import java.util.List;

import common.BaseData;
import common.MysqlConnect;

/**
 * @Description:公共数据类
 */
public class PubData {
	MysqlConnect mysqlConnect = new MysqlConnect();
	BaseData baseData = new BaseData();
	List<HashMap<String, String>> resultSet;
	
	/*获取手机区号
	 * @telephoneId 地区ID
	 * @keysName 字段名称
	 * */
	public String get_telephoneCode(String telephoneCodeId,String keysName) {
		String telephoneCode ="";
		resultSet = mysqlConnect.selectSql("SELECT "+keysName+" from pub_telephone_code where id="+telephoneCodeId);
		if (resultSet.size()>0) {
			telephoneCode = resultSet.get(0).get(keysName);
		}
		
		return telephoneCode;
	}
}
