/**
 * @author helen
 * @date 2018年11月14日
 */
package user;

import common.MyAPI;
import common.MyConfig;
import data.MemberData;
import com.alibaba.fastjson.JSONObject;

/**
 * @Description:APP->会员收藏 : Member Collection Controller
 */
public class MemberCollection {
	
	MemberData memberData = new MemberData();
	MyConfig myConfig = new MyConfig();
	MyAPI myAPI = new MyAPI();

	
	/*删除收藏*/
	public JSONObject CollectionDelete(JSONObject userdata,int deleteAll,String goodsIds,String platform) {
		JSONObject params = new JSONObject();
		params.put("userId", userdata.getString("userId"));
		params.put("deleteAll", deleteAll);//(integer, optional): 清空所有（0为否，1为是） 
		if (deleteAll==0) {
			String[] goodsIdList = goodsIds.split(",");
			params.put("goodsIds", goodsIdList);//(Array[integer], optional): 商品id列表 
		}
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/userCenter/member/v1/collect/delete", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/userCenter/member/h5/collect/delete", params);
		}
		else {
			System.out.println(platform+"平台没有[清除收藏]");
		}
		return result;
	}
	
	/*收藏列表
	 * @sort 排序顺序(price:价格,time:时间,升序:asc,降序:desc)(eg:{"price":"asc","time":"desc"}) ,
	 * @types (Array[integer], optional): 分类ID ,
	 * */
	public JSONObject CollectionList(JSONObject userdata,String sort,String types,String platform) {
		JSONObject params = new JSONObject();
		params.put("userId", userdata.getString("userId"));
		params.put("sort ", sort);
		params.put("type", types);
		params.put("page", "1");
		params.put("pageSize", "10");
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/userCenter/member/v1/collect/list", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/api/userCenter/member/h5/collect/list", params);
		}
		else {
			System.out.println(platform+"平台没有[收藏列表]接口");
		}

		return result;
	}
	
	/*收藏或者取消收藏*/
	public JSONObject switchOnCollection(JSONObject userdata,String goodsId,String type,String platform) {
		JSONObject params = new JSONObject();
		params.put("userId", userdata.getString("userId"));
		params.put("goodsId", goodsId);
		params.put("type", type);//类型（0：取消收藏，1：收藏） 
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/userCenter/member/v1/collect/switchOnCollection", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/userCenter/member/h5/collect/switchOnCollection", params);
		}
		else {
			System.out.println(platform+"平台没有[收藏或者取消收藏]接口");
		}
		
		return result;
	}

}
