/**
 * @author helen
 * @date 2018年9月11日
 */
package data;
import common.MysqlConnect;
import com.alibaba.fastjson.JSONArray;

/**
 * @Description:商品相关数据
 */
public class GoodData {
	MysqlConnect mysqlConnect = new MysqlConnect();
	JSONArray arrayResult = new JSONArray();
	
	/*获取没有推荐商品，且已上架的最近一个商品ID  p_goods*/
	public String getGoodId_IsOnLive() {
		return mysqlConnect.getData("SELECT id from p_goods WHERE is_onlive=1 and id!=7966 and id not in(SELECT good_id from p_goods_recommend) ORDER BY id DESC LIMIT 1").getJSONObject(0).getString("id");
	}
	
	/*获取goodTypeId下的所有上架非乐豆商品  p_goods*/
	public JSONArray getGoodId_typeId(String goodTypeId) {
		return mysqlConnect.getData("SELECT id from p_goods WHERE is_onlive=1 and is_health=0 AND goods_type_id ="+goodTypeId);
	}
	
	/*获取上架状态且是非大健康商品的商品类别（前n个类别）p_goods*/
	public JSONArray getGoodTypeId(String n) {
		return mysqlConnect.getData("SELECT goods_type_id from p_goods WHERE is_onlive=1 and is_health=0 and max_buy_count=0 GROUP BY goods_type_id  ORDER BY id DESC LIMIT "+n);
	}
	
	/*最新number个非乐豆商品的UID p_goods*/
	public JSONArray getGoodId_lastGood(String number) {
		return mysqlConnect.getData("SELECT id from p_goods where is_onlive=1 and is_health=0 ORDER BY id desc LIMIT "+number);
	}
	
	/*根据ID，查询商品信息，p_goods
	 * @goodsId 商品ID
	 * @key 字段名称
	 * */
	public String getGood_byId(String goodId,String key) {
		String value = "";
		arrayResult = mysqlConnect.getData("SELECT "+key+" from p_goods where id="+goodId);
		if (arrayResult.size()>0) {
			value = arrayResult.getJSONObject(0).getString(key);
		}
		return value;
	}

	/*根据SKUNB，获取信息,表：p_goods_sku
	 * @skuNumber SKU编号
	 * @keyName 字段名称
	 * */
	public String getSKUValue_bySkuNb(String skuNumber,String keyName) {
		String keyValue = "";
		arrayResult = mysqlConnect.getData("SELECT "+keyName+" from p_goods_sku where sku_number='"+skuNumber+"'");
		if (arrayResult.size()>0) {
			keyValue = arrayResult.getJSONObject(0).getString(keyName);
		}
		return keyValue;
	}
	
	/*获取某个库存大于1的商品的sku_number ,p_goods_sku*/
	public JSONArray getSKU_goodId(String goodId) {
		return mysqlConnect.getData("SELECT sku_number from p_goods_sku where bought>1 and goods_id="+goodId);
	}
	
	/*最新一个非乐豆商品的SKU   p_goods_sku*/
	public String getSKU_lastGood() {
		arrayResult = mysqlConnect.getData("SELECT sku_number from p_goods_sku where goods_id = (SELECT id from p_goods where is_onlive=1 and is_health=0 ORDER BY id desc LIMIT 1)");
		return arrayResult.getJSONObject(0).getString("sku_number");
	}

	/*根据分类级别，获取商品类别列表
	 * @level  1\2\3级
	 * */
	public JSONArray getGoodsTypeList(String level) {
		return mysqlConnect.getData("SELECT id from p_goods_type where is_delete=0 and level="+level);
	}
}
