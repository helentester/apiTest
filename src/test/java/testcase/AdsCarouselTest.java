/**
 * @author helen
 * @date 2018年12月7日
 */
package testcase;

import static org.testng.Assert.assertEquals;

import org.testng.Reporter;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONObject;

import data.AdsData;
import site.AdsCarousel;
import user.Login;

/**
 * @Description:站点管理业务测试
 */
public class AdsCarouselTest {
	AdsData adsData = new AdsData();
	Login login = new Login();
	AdsCarousel adsCarousel = new AdsCarousel();
	
	@Test
	public void test_AdsManage() {
		Reporter.log("首页轮播图管理测试：新增");
		
		JSONObject loginResult = login.sysLogin("helen", "123456li");
		assertEquals(loginResult.getString("code"), "10000","后台登录校验"+loginResult);
		JSONObject userdata = loginResult.getJSONObject("data");
		
		Reporter.log("新增首页轮播图");
		JSONObject addResult = adsCarousel.appAdsCarouselWebAdd(userdata);
		assertEquals(addResult.getString("code"), "10000","新增首页轮播图检验"+addResult);
		//获取最新的首页轮播图
		JSONObject adsInfo = adsData.getLastAds();
		String url = "http://wftest.oss-cn-shenzhen.aliyuncs.com"+adsInfo.getString("image");
		adsInfo.put("image", url);//
		
		Reporter.log("返回最新的首页轮播图详情");
		JSONObject detailResult = adsCarousel.appAdsCarouselWebDetail(userdata, adsInfo.getString("id"));
		assertEquals(detailResult.getString("code"), "10000","最新的首页轮播图详情"+detailResult);
		JSONObject detailInfo = detailResult.getJSONObject("data");
		detailInfo.remove("isDelete");//不需要校验删除状态
		detailInfo.remove("type");
		assertEquals(detailInfo.toString(), adsInfo.toString());
		
		Reporter.log("修改首页轮播图");
		JSONObject updateResult = adsCarousel.appAdsCarouselWebUpdate(userdata, adsInfo.getString("id"));
		assertEquals(updateResult.getString("code"), "10000","修改首页轮播图"+updateResult);
		
		Reporter.log("删除首页轮播图");
		JSONObject deleteResult = adsCarousel.appAdsCarouselWebDelete(userdata, adsInfo.getString("id"));
		assertEquals(deleteResult.getString("code"), "10000","删除首页轮播图"+deleteResult);
		
		Reporter.log("查询首页轮播图列表");
		JSONObject listResult = adsCarousel.appAdsCarouselWebList(userdata);
		assertEquals(listResult.getString("code"), "10000","获取首页轮播图列表（第1页）"+listResult);
	}

}
