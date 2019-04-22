/**
 * @author helen
 * @date 2018年10月30日
 */
package user;

import static org.testng.Assert.assertEquals;
import common.MyAPI;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @Description:->收货地址 : Member Delivery Address Controller
 */
public class MemberDeliveryAddress {
	MyAPI myAPI = new MyAPI();
	
	/*获取会员的收货地址ID：先查询会员是否有收货地址，如果没有就新增一个，然后返回*/
	public String getAddrId(JSONObject userdata,String platform) {
		String addrId = "";
		JSONObject deliverryListResult = this.deliveryAddrList(userdata, platform);
		assertEquals(deliverryListResult.getString("code"), "10000","获取会员的收货地址"+deliverryListResult);
		JSONArray deliverryList = deliverryListResult.getJSONArray("data");
		if (deliverryList.size()>0) {
			addrId = deliverryList.getJSONObject(0).getString("addressId");
		}
		else {
			JSONObject addAddrResult = this.deliverryAdd(userdata, platform);
			assertEquals(addAddrResult.getString("code"), "10000","新增会员收货地址校验："+addAddrResult);
			addrId = addAddrResult.getJSONObject("data").getString("addressId");
		}
		return addrId;
	}
	
	/*添加会员收货地址*/
	public JSONObject deliverryAdd(JSONObject userdata,String platform) {
		JSONObject params = new JSONObject();
		params.put("address", "小北");//
		params.put("addressTag", "公司");//
		params.put("areaCode", "00000");//
		params.put("cityCode", "440100");//
		params.put("cityName", "广州市");//
		params.put("deliveryName", "helenli");//
		params.put("detailAddress", "广东省广州市越秀区小北");//
		params.put("districtCode", "440104");//
		params.put("districtName", "越秀区");//
		params.put("mobile", userdata.getString("loginAccount"));//
		params.put("provinceCode", "440000");//
		params.put("provinceName", "广东省");//
		params.put("telephone", userdata.getString("loginAccount"));//
		params.put("telephoneCodeId", "0");//
		params.put("userId", userdata.getString("userId"));//
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/userCenter/member/v1/memberDeliveryAddrAdd", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/userCenter/member/h5/memberDeliveryAddrAdd", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.PCPost(userdata, "/userCenter/delivery/addr/add", params);
		}
		else {
			System.out.println(platform+"平台没有[增加收货地址接口]");
		}
		return result;
	}

	/*会员收货地址列表*/
	public JSONObject deliveryAddrList(JSONObject userdata,String platform) {
		JSONObject params = new JSONObject();
		params.put("userId", userdata.getString("userId"));
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/userCenter/member/v1/memberDeliveryAddrList", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/userCenter/member/h5/memberDeliveryAddrList", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.pcGet(userdata, "/userCenter/delivery/addr/list", params);
		}
		else {
			System.out.println(platform+"平台没有[查询会员收货地址接口]");
		}
				
		return result;
	}
	
	/*获取默认收货地址*/
	public JSONObject memberGetDefaultDeliveryAddr(JSONObject userdata,String platform) {
		JSONObject params = new JSONObject();
		params.put("userId", userdata.getString("userId"));
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/userCenter/member/v1/memberGetDefaultDeliveryAddr", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/userCenter/member/h5/memberGetDefaultDeliveryAddr", params);
		}
		else if (platform.equals("pc")) {
			result = myAPI.pcGet(userdata, "/userCenter/delivery/def/addr", params);
		}
		else {
			System.out.println(platform+"平台没有[获取默认收货地址接口]");
		}
		return result;
	}
}
