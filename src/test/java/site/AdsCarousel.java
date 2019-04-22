/**
 * @author helen
 * @date 2018年11月13日
 */
package site;

import common.BaseData;
import common.MyAPI;
import com.alibaba.fastjson.JSONObject;

/**
 * @Description:首页轮播图管理列表 : App Ads Carousel Controller
 */
public class AdsCarousel {
	BaseData baseData = new BaseData();
	MyAPI myAPI = new MyAPI();

	/*查询首页轮播图列表:查询第一页*/
	public JSONObject appAdsCarouselWebList(JSONObject userdata) {
		JSONObject params = new JSONObject();
		params.put("page", "1");//第一页
		JSONObject result = myAPI.sysPost(userdata, "/userCenter/site/appAdsCarousel/web/list", params);
		return result;
	}
	
	/*删除首页轮播图*/
	public JSONObject appAdsCarouselWebDelete(JSONObject userdata,String adsId) {
		JSONObject params = new JSONObject();
		params.put("id", adsId);
		JSONObject result  = myAPI.sysPost(userdata, "/userCenter/site/appAdsCarousel/web/delete", params);
		return result;
	}
	
	/*轮播图详情*/
	public JSONObject appAdsCarouselWebDetail(JSONObject userdata,String adsId) {
		JSONObject params = new JSONObject();
		params.put("id", adsId);
		JSONObject result  = myAPI.sysPost(userdata, "/userCenter/site/appAdsCarousel/web/detail", params);
		return result;
	}
	
	/*轮播图修改*/
	public JSONObject appAdsCarouselWebUpdate(JSONObject userdata,String adsId) {
		JSONObject params = new JSONObject();
		int sort = baseData.getNum(0, 999);
		params.put("id", adsId);//轮播图Id
		params.put("title","修改首页轮播图"+sort);//广告标题 ,
		params.put("description", "测试首页轮播图");
		params.put("sort", sort);//排序
		params.put("publishTime", baseData.getTimeStamp(1));//发布时间
		params.put("endTime", baseData.getTimeStamp(3));//结束时间
		//随机设置平台
		int platformChoose = baseData.getNum(1, 4);
		params.put("releasePlatform", platformChoose);//投放平台(1:app端;2:pc端;3:h5) ,
		//随机设置类型
		int typeChoose = baseData.getNum(0, 4);
		params.put("type", typeChoose);//类型(0=链接;1=商品;2=商品分类;3=招商)
		if (typeChoose==0) {
			params.put("image", "/user/leaseContract/1540457654051.jpg");// 广告图
			params.put("link", "https://c.pingan.com/ca/index?sign=61ea0b84fd87839ec8bc2aad27df358f&versionNo=R10310&scc=386100511&channel=WX&onlineSQFlag=N&ccp=3a1a4a20a15");//外链
		}
		else if (typeChoose==1) {
			params.put("image", "/user/leaseContract/1542101587924.jpg");
			params.put("link", "http://pctest.wufu360.com:8031/goods/goodsInfo?id=7813");
			params.put("targetId", "7813");//目标ID
		}
		else if (typeChoose==2) {
			params.put("image", "/user/leaseContract/1538289508926.png");
			params.put("targetId", "118");//目标ID
		}
		else if (typeChoose==3) {
			params.put("image", "/user/leaseContract/1533717136610.jpeg");
			params.put("link", "http://pctest.wufu360.com:8031/activePage/july?id=118");
		}

		JSONObject result = myAPI.sysPost(userdata, "/userCenter/site/appAdsCarousel/web/add", params);
		return result;
	}
	
	/*轮播图添加*/
	public JSONObject appAdsCarouselWebAdd(JSONObject userdata) {
		JSONObject params = new JSONObject();
		int sort = baseData.getNum(0, 999);
		params.put("title","新增首页轮播图"+sort);//广告标题 ,
		params.put("description", "测试首页轮播图");
		params.put("sort", sort);//排序
		params.put("publishTime", baseData.getTimeStamp(1));//发布时间
		params.put("endTime", baseData.getTimeStamp(3));//结束时间
		//随机设置平台
		int platformChoose = baseData.getNum(1, 4);
		params.put("releasePlatform", platformChoose);//投放平台(1:app端;2:pc端;3:h5) ,
		//随机设置类型
		int typeChoose = baseData.getNum(0, 4);
		params.put("type", typeChoose);//类型(0=链接;1=商品;2=商品分类;3=招商)
		if (typeChoose==0) {
			params.put("image", "/user/leaseContract/1540457654051.jpg");// 广告图
			params.put("link", "https://c.pingan.com/ca/index?sign=61ea0b84fd87839ec8bc2aad27df358f&versionNo=R10310&scc=386100511&channel=WX&onlineSQFlag=N&ccp=3a1a4a20a15");//外链
		}
		else if (typeChoose==1) {
			params.put("image", "/user/leaseContract/1542101587924.jpg");
			params.put("link", "http://pctest.wufu360.com:8031/goods/goodsInfo?id=7813");
			params.put("targetId", "7813");//目标ID
		}
		else if (typeChoose==2) {
			params.put("image", "/user/leaseContract/1538289508926.png");
			params.put("targetId", "118");//目标ID
		}
		else if (typeChoose==3) {
			params.put("image", "/user/leaseContract/1533717136610.jpeg");
			params.put("link", "http://pctest.wufu360.com:8031/activePage/july?id=118");
		}
		JSONObject result = myAPI.sysPost(userdata, "/userCenter/site/appAdsCarousel/web/add", params);
		return result;
	}
}
