/**
 * @author helen
 * @date 2018年11月27日
 */
package good;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import common.BaseData;
import common.MyAPI;
import data.GoodData;

/**
 * @Description:
 */
public class GoodsAdmin {
	BaseData baseData = new BaseData();
	GoodData goodData = new GoodData();
	MyAPI myAPI = new MyAPI();
	
	/*新增商品*/
	public JSONObject goodsAdd(JSONObject userdata, String supplierId, String name, String freightTemplateId,
			String isDelivery, String isRealName, String isHealth, String maxBuyCount, String shelvesType) {
		//随机获取一个商品分类
		JSONArray goodsTypeList = goodData.getGoodsTypeList("3");
		String goodsTypeId = goodsTypeList.getJSONObject(baseData.getNum(0, goodsTypeList.size()-1)).getString("id");
		
		JSONObject params = new JSONObject();
		params.put("supplierId", supplierId);//商家ID
		params.put("name", name+baseData.getNum(0, 9999));//商品名称
		params.put("adsMessage", "养老界的阿里巴巴");//商品介绍广告语 ,
		params.put("goodsTypeId", goodsTypeId);//商品分类
		params.put("brandId", "0");//商品品牌ID,必填 ,0表示没有
		params.put("boughtWarning", "10");//库存警告，默认：10 
		params.put("freightTemplateId", freightTemplateId);//运费模板
		params.put("isNew", "0");//是否首页推荐：0不是，1是,必填 
		params.put("isDelivery", isDelivery);//商品类型： 0实体商品 ，1虚拟商品,必填 
		params.put("isRealName", isRealName);//是否需要实名认证： 0为否 ，1为是,必填 
		params.put("isOwn", "0");// 是否自营商品： 0为否 ，1为是,必填 
		params.put("isHealth", isHealth);//是否合作商2乐豆商品，0，否，1，是，默认：0 ,
		params.put("maxBuyCount", maxBuyCount);//最大购买数,必填 ,0表示没有控制
		params.put("shelvesType", shelvesType);//上架类型：1，立即上架，2，定时上架,必填 。
		params.put("img", "/goods/1543304953579.jpg");//商品主图,必填 ,
		params.put("description", "接口添加商品");
		
		JSONObject result = new JSONObject();
		if (userdata.getString("userType").equals("backend")) {
			result = myAPI.sysPost(userdata, "/goodsCenter/admin/goods/add", params);
		}
		else if (userdata.getString("userType").equals("supplier")) {
			result = myAPI.sellerPost(userdata, "/goodsCenter/admin/goods/add", params);
		}
		else {
			System.out.println(userdata.getString("userType")+"的用户没有[新增商品]接口");
		}
		return result;
	}
	
	/*商品详情*/
	public JSONObject goodsDetail(JSONObject userdata,String goodsId) {
		JSONObject params = new JSONObject();
		params.put("id", goodsId);//商品ID
		
		JSONObject result = new JSONObject();
		if (userdata.getString("userType").equals("backend")) {
			result = myAPI.sysGet(userdata, "/goodsCenter/admin/goods/get/"+goodsId, params);
		}
		else if (userdata.getString("userType").equals("supplier")) {
			result = myAPI.sellerGet(userdata, "/goodsCenter/admin/goods/get/"+goodsId, params);
		}
		else {
			System.out.println(userdata.getString("userType")+"的用户没有[商品详情]接口");
		}
		return result;
	}
	
	/*修改商品*/
	public JSONObject goodsUpdate_sku(JSONObject userdata,JSONObject goods,JSONArray skuList,String isSubmit) {
		goods.remove("goodsSkuList");//去掉原有的SKU数据
		goods.remove("goodsSkuModelList");
		goods.put("skuList", skuList);//加入新的SKU数据
		goods.put("isSubmit", isSubmit);//是否提交审核：0，否，1，是，2，审核不通过，3，审核通过并上架 非必填，默认：0 ,
		JSONObject result = new JSONObject();
		if (userdata.getString("userType").equals("backend")) {
			result = myAPI.sysPost(userdata, "/goodsCenter/admin/goods/update", goods);
		}
		else if (userdata.getString("userType").equals("supplier")) {
			result = myAPI.sellerPost(userdata, "/goodsCenter/admin/goods/update", goods);
		}
		else {
			System.out.println(userdata.getString("userType")+"的用户没有[修改商品]接口");
		}
		return result;
	}

	/*更新商品审核状态*/
	public JSONObject goodsPublishWait(JSONObject userdata,String goodsId,int publishWait) {
		JSONObject params = new JSONObject();
		params.put("id", goodsId);
		params.put("publishWait", publishWait);// 商品审核状态： 0:等待审核 1:审核通过 2:审核不通过 3草稿发状态(没提交到审核)
		if (publishWait==2) {
			params.put("reviewerMeseage", "接口审核商品不通过");
		}
		
		JSONObject result = new JSONObject();
		if (userdata.getString("userType").equals("backend")) {
			result = myAPI.sysPost(userdata, "/goodsCenter/admin/goods/publishWait/update", params);
		}
		else if (userdata.getString("userType").equals("supplier")) {
			result = myAPI.sellerPost(userdata, "/goodsCenter/admin/goods/publishWait/update", params);
		}
		else {
			System.out.println(userdata.getString("userType")+"的用户没有[更新商品审核状态]接口");
		}
		return result;
	}
	
	/*更新商品审核状态并上下架*/
	public JSONObject IsOnliveAndPublishWait(JSONObject userdata,String goodsId,int publishWait,int isOnlive) {
		JSONObject params = new JSONObject();
		params.put("id", goodsId);
		params.put("isOnlive", isOnlive);//商品上下架状态：0为已下架 1为已上架 2为未上架 ,
		params.put("publishWait", publishWait);// 商品审核状态： 0:等待审核 1:审核通过 2:审核不通过 3草稿发状态(没提交到审核)
		if (publishWait==2) {
			params.put("reviewerMeseage", "接口审核商品不通过");
		}
		
		JSONObject result = new JSONObject();
		if (userdata.getString("userType").equals("backend")) {
			result = myAPI.sysPost(userdata, "/goodsCenter/admin/goods/IsOnliveAndPublishWait/update", params);
		}
		else if (userdata.getString("userType").equals("supplier")) {
			result = myAPI.sellerPost(userdata, "/goodsCenter/admin/goods/IsOnliveAndPublishWait/update", params);
		}
		else {
			System.out.println(userdata.getString("userType")+"的用户没有[更新商品审核状态并上下架]接口");
		}
		return result;
	}
	
	/*更新商品上架状态*/
	public JSONObject isOnlive(JSONObject userdata,String goodsId,int isOnlive) {
		JSONObject params = new JSONObject();
		params.put("id", goodsId);
		params.put("isOnlive", isOnlive);//商品上下架状态：0为已下架 1为已上架 2为未上架 
		
		JSONObject result = new JSONObject();
		if (userdata.getString("userType").equals("backend")) {
			result = myAPI.sysPost(userdata, "/goodsCenter/admin/goods/isOnlive/update", params);
		}
		else if (userdata.getString("userType").equals("supplier")) {
			result = myAPI.sellerPost(userdata, "/goodsCenter/admin/goods/isOnlive/update", params);
		}
		else {
			System.out.println(userdata.getString("userType")+"的用户没有[更新商品审核状态并上下架]接口");
		}
		return result;
	}

	/*查询商品列表*/
	public JSONObject goodsList(JSONObject userdata,int isOnlive) {
		JSONObject params = new JSONObject();
		params.put("isOnlive", isOnlive);//上架状态 0为已下架 1为已上架 2为未上架
		return myAPI.sysGet(userdata, "/goodsCenter/admin/goods/list", params);
	}
}
