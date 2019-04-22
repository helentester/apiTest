/**
 * @author helen
 * @date 2018年11月9日
 */
package data;
import common.MyRedis;
import common.MysqlConnect;
import com.alibaba.fastjson.JSONArray;

/**
 * @Description:购物车相关的数据库操作
 */
public class ShoppingCartData {
	MysqlConnect mysqlConnect = new MysqlConnect();
	MyRedis myRedis = new MyRedis();
	JSONArray arrayResult = new JSONArray();
	
	/*根据cartId,查询购物车信息  t_cart*/
	public String getCartValue_byId(String cartId,String key) {
		String value = "";
		arrayResult = mysqlConnect.getData("SELECT "+key+" from t_cart WHERE id="+cartId);
		if (arrayResult.size()>0) {
			value = arrayResult.getJSONObject(0).getString(key);
		}
		return value;
	}

}
