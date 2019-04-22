/**
 * @author helen
 * @date 2019年4月9日
 */
package seller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import common.BaseData;
import common.MyAPI;

/**
 * @Description:商家中心，房间管理
 */
public class RoomManage {
	BaseData baseData = new BaseData();
	MyAPI myAPI = new MyAPI();
	
	/*添加房间*/
	public JSONObject addRoom(JSONObject sellerUserdata,String roomType,int peopleNumber,int bedType,Number commonPrice,Number commonBean,
			int setType,Number weekendPrice,Number weekendBean) {
		//基本信息
		JSONObject hotelRoomAddBean = new JSONObject();
		hotelRoomAddBean.put("roomType", roomType);//(string, optional): 房间类型 ,自定义
		hotelRoomAddBean.put("goodsTypeId", 380);//(integer, optional): 商品分类ID ,国内酒店
		hotelRoomAddBean.put("roomAmount", 321);// (integer, optional): 房间数量 ,
		hotelRoomAddBean.put("introduction", roomType+"测试");//(string, optional): 房间介绍 ,
		hotelRoomAddBean.put("peopleNumber", peopleNumber);//(integer, optional): 适宜入住人数 ,
		hotelRoomAddBean.put("bedType", bedType);//(integer, optional): 床铺类型:1标准单人床,2加大单人床,3标准双人床,4加大双人床 ,
		hotelRoomAddBean.put("bedAmount", peopleNumber);//(integer, optional): 床铺数量 ,(此处测试设计与人员一至)
		hotelRoomAddBean.put("installation", "0,1,2,3,4,5,6");//便利设施:,(0无线网络,1电视,2空调,3暖气,4吹风机,5厨房,6洗衣机)格式:,0,1,2,3,4,5,6, ,
		hotelRoomAddBean.put("commonPrice", commonPrice);// (number, optional): 平日房价 ,
		hotelRoomAddBean.put("commonBean", commonBean);// (number, optional): 平日豆价 ,
		hotelRoomAddBean.put("setType", setType);//(integer, optional): 状态(0:上调金额,1:上调比例,2:下调金额,3:下调比例) ,周末调价
		hotelRoomAddBean.put("weekendPrice", weekendPrice);// 周末房价,周末调价
		hotelRoomAddBean.put("weekendBean", weekendBean);// 周末豆价 ,周末调价
		hotelRoomAddBean.put("startTime", baseData.getTime(0, 0, 0, "yyyy-MM-dd'T'HH:mm:ss'Z'"));//(string, optional): 有效开始时间 ,
		hotelRoomAddBean.put("endTime", baseData.getTime(0, 30, 0, "yyyy-MM-dd'T'HH:mm:ss'Z'"));//(string, optional): 有效结束时间 ,
		hotelRoomAddBean.put("roomPic", "/sku/1555037941538.jpg");//(string, optional): 房间主图 ,
		hotelRoomAddBean.put("setUp", 0);//(integer, optional): 状态(0:审核通过自动上架,1:手动上架) ,
		hotelRoomAddBean.put("description", "房间详情");//
		hotelRoomAddBean.put("status", 1);//(integer, optional): 状态(0:待提交,1:待审核,2:审核通过,3:审核不通过,4:已上架,5:已下架,6:已失效) ,
		//房间图册
		JSONArray picUrl = new JSONArray();
		picUrl.add("/sku/1555037941538.jpg");
		picUrl.add("/sku/1555037969217.jpg");
		picUrl.add("/1555037971630.jpg");
		picUrl.add("/sku/1555037982791.jpg");
		picUrl.add("/sku/1555037989176.jpg");
		picUrl.add("/sku/1555037993138.jpg");
		//指定房价
		JSONArray hotelRoomSalePriceList = new JSONArray();
		JSONObject hotelRoomSalePrice = new JSONObject();
		hotelRoomSalePrice.put("price", 100);
		hotelRoomSalePrice.put("bean", 80);
		hotelRoomSalePrice.put("startTime", baseData.getTime(0, 28, 0, "yyyy-MM-dd'T'HH:mm:ss'Z'"));
		hotelRoomSalePrice.put("endTime", baseData.getTime(0, 30, 0, "yyyy-MM-dd'T'HH:mm:ss'Z'"));
		hotelRoomSalePriceList.add(hotelRoomSalePrice);
		
		JSONObject params = new JSONObject();
		params.put("hotelRoomAddBean", hotelRoomAddBean);//基本信息
		params.put("hotelRoomSalePrice", hotelRoomSalePriceList);//指定房价
		params.put("picUrl", picUrl);//房间图册
		
		JSONObject result = new JSONObject();
		result = myAPI.KYSellerPost(sellerUserdata, "/hotelCenter/room/admin/add", params);
		
		return result;
	}

	/*康养公寓房间列表*/
	public JSONObject roomList(JSONObject sellerUserdata) {
		JSONObject params = new JSONObject();
		//params.put("id", id);//房间ID
		
		JSONObject result = new JSONObject();
		result = myAPI.KYSellerGet(sellerUserdata, "/hotelCenter/room/admin/supplier/list", params);
		return result;
	}
}
