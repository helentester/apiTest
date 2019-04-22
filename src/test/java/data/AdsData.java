/**
 * @author helen
 * @date 2018年11月14日
 */
package data;

import common.MysqlConnect;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @Description:轮播图数据库操作
 */
public class AdsData {
	MysqlConnect mysqlConnect = new MysqlConnect();
	JSONArray arrayResult = new JSONArray();
	
	/*获取最新的一个轮播图信息*/
	public JSONObject getLastAds() {
		arrayResult = mysqlConnect.getData("SELECT id,title,description,image,editor_id as editorId,editor_name as editorName,add_time as addTime,publish_time as publishTime,end_time as endTime,target_id as targetId,link,sort,release_platform as releasePlatform from ads ORDER BY id DESC LIMIT 1");
		return arrayResult.getJSONObject(0);
	}

}
