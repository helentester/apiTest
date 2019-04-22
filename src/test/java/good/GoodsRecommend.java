/**
 * @author helen
 * @date 2018年12月5日
 */
package good;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import common.MyAPI;

/**
 * @Description:goods-recommend-controller : 商品推荐
 */
public class GoodsRecommend {
	MyAPI myAPI = new MyAPI();
	
	/*运营中心添加商品的推荐商品*/
	public JSONObject addRecommendGoods(JSONObject sysUserdata,String goodsId,String channels,JSONArray recommendGoodList) {
		String recommendGoodIds = recommendGoodList.getJSONObject(0).getString("id");
		String recommendGoodNames = recommendGoodList.getJSONObject(0).getString("name");
		for (int i = 1; i < recommendGoodList.size(); i++) {
			recommendGoodIds = recommendGoodIds+","+recommendGoodList.getJSONObject(i).getString("id");
			recommendGoodNames = recommendGoodNames+","+recommendGoodList.getJSONObject(i).getString("name");
		}

		JSONObject params = new JSONObject();
		params.put("goodsId", goodsId);
		params.put("channels", channels); //渠道类型1:app 、h5 2:pc商城，多个用逗号分隔 ,
		params.put("recommendGoodIds", recommendGoodIds);//推荐商品id,多个用逗号拼接 ,
		params.put("recommendGoodNames", recommendGoodNames);//推荐商品Name,多个用逗号拼接
		
		return myAPI.sysPost(sysUserdata, "/goodsCenter/admin/goods/recommend/add", params);
	}
	
	/*运营中心修改商品的推荐商品*/
	public JSONObject updateRecommendGoods(JSONObject sysUserdata,String goodsId,String channels,String recommendGoodIds,String recommendGoodNames) {
		JSONObject params = new JSONObject();
		params.put("goodsId", goodsId);
		params.put("channels", channels); //渠道类型1:app 、h5 2:pc商城，多个用逗号分隔 ,
		params.put("recommendGoodIds", recommendGoodIds);//推荐商品id,多个用逗号拼接 ,
		params.put("recommendGoodNames", recommendGoodNames);//推荐商品Name,多个用逗号拼接
		
		return myAPI.sysPost(sysUserdata, "/goodsCenter/admin/goods/recommend/update", params);
	}
	
	/*运营中心删除商品的推荐商品*/
	public JSONObject deleteRecommendGoods(JSONObject sysUserdata,String goodsId,JSONArray recommendGoodList) {
		String recommendGoodIds = recommendGoodList.getJSONObject(0).getString("id");
		String recommendGoodNames = recommendGoodList.getJSONObject(0).getString("name");
		for (int i = 1; i < recommendGoodList.size(); i++) {
			recommendGoodIds = recommendGoodIds+","+recommendGoodList.getJSONObject(i).getString("id");
			recommendGoodNames = recommendGoodNames+","+recommendGoodList.getJSONObject(i).getString("name");
		}
		
		JSONObject params = new JSONObject();
		params.put("goodsId", goodsId);
		params.put("recommendGoodIds", recommendGoodIds);//推荐商品id,多个用逗号拼接 ,
		params.put("recommendGoodNames", recommendGoodNames);//推荐商品Name,多个用逗号拼接
		
		return myAPI.sysPost(sysUserdata, "/goodsCenter/admin/goods/recommend/delete", params);
	}
	
	/*运营中心：查询可推荐的商品*/
	public JSONObject recommendList(JSONObject sysUserdata,String searchGoodId) {
		JSONObject params = new JSONObject();
		params.put("searchGoodId", searchGoodId);
		return myAPI.sysGet(sysUserdata, "/goodsCenter/admin/goods/recommend/list", params);
	}
	
	/*商城查询推荐商品列表*/
	public JSONObject getRecommendList(JSONObject userdata,String goodId,String platform) {
		JSONObject params = new JSONObject();
		params.put("goodsId", goodId);//宿主商品ID
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiGet(userdata, "/goodsCenter/app/goods/v1/getRecommendList", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Get(userdata, "/goodsCenter/h5/goods/getRecommendList", params);
		}
		else {
			System.out.println(platform+"端没有[查询推荐商品列表]接口");
		}
		return result;
	}
	
	/*商城查询推荐商品列表*/
	public JSONObject getRecommendList(String goodId,String platform) {
		JSONObject params = new JSONObject();
		params.put("goodsId", goodId);//宿主商品ID
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiGet("/goodsCenter/app/goods/v1/getRecommendList", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Get("/goodsCenter/h5/goods/getRecommendList", params);
		}
		else {
			System.out.println(platform+"端没有[查询推荐商品列表]接口");
		}
		return result;
	}

}
