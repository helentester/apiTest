/**
 * @author helen
 * @date 2018年11月15日
 */
package site;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import common.BaseData;
import common.MyAPI;
import com.alibaba.fastjson.JSONObject;
import user.Login;

/**
 * @Description:首页悬浮窗管理类 : Floating Window Controller
 */
public class FloatingWindow {
	BaseData baseData = new BaseData();
	MyAPI myAPI = new MyAPI();
	Login loginTest = new Login();
	
	@Test
	public void test_() {
		JSONObject loginResult = loginTest.sysLogin("helen", "123456li");
		assertEquals(loginResult.getString("code"), "10000","后台登录校验"+loginResult);
		JSONObject userdata = loginResult.getJSONObject("data");
		JSONObject addResult = this.Add(userdata);
		assertEquals(addResult.getString("code"), "10000","新增悬浮窗"+addResult);
	}

	/*轮播图添加*/
	public JSONObject Add(JSONObject userdata) {
		JSONObject params = new JSONObject();
		int sort = baseData.getNum(0, 999);
		params.put("title","新增首页悬浮窗"+sort);//广告标题 ,
		params.put("description", "测试首页轮播图");
		params.put("sort", sort);//排序
		params.put("beginTime", baseData.getTimeStamp(1));//发布时间
		params.put("endTime", baseData.getTimeStamp(3));//结束时间

		//随机设置类型
		int typeChoose = baseData.getNum(0, 4);
		params.put("type", typeChoose);//类型 1为外链 2为活动内页 3为商品
		if (typeChoose==1) {
			params.put("image", "/user/leaseContract/1540457654051.jpg");// 
			params.put("link", "https://c.pingan.com/ca/index?sign=61ea0b84fd87839ec8bc2aad27df358f&versionNo=R10310&scc=386100511&channel=WX&onlineSQFlag=N&ccp=3a1a4a20a15");//外链
		}
		else if (typeChoose==2) {
			params.put("image", "/user/leaseContract/1533717136610.jpeg");
			params.put("link", "http://pctest.wufu360.com:8031/activePage/july?id=118");
		}
		else if (typeChoose==3) {
			params.put("image", "/user/leaseContract/1542101587924.jpg");
			params.put("link", "http://pctest.wufu360.com:8031/goods/goodsInfo?id=7813");
			params.put("targetId", "7813");//目标ID
		}
		
		JSONObject result = myAPI.sysPost(userdata, "/userCenter/site/floatingWindow/web/add", params);
		return result;
	}
}
