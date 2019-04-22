/**
 * @author helen
 * @date 2018年12月5日
 */
package testcase;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import java.util.ArrayList;

import org.testng.Reporter;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import common.BaseData;
import data.GoodData;
import data.MemberData;
import dataProvider.UserDataProvider;
import good.GoodsAdmin;
import good.GoodsRecommend;
import user.FileDo;
import user.Login;

/**
 * @Description:商品业务测试
 */
public class GoodTest {
	BaseData baseData = new BaseData();
	MemberData memberData = new MemberData();
	GoodData goodData = new GoodData();
	Login login = new Login();
	GoodsAdmin goodsAdmin = new GoodsAdmin();
	GoodsRecommend goodsRecommend = new GoodsRecommend();
	
	@Test
	public void test_() {
		JSONObject LoginResult = login.sysLogin("helen", "123456li");
		assertEquals(LoginResult.getString("code"), "10000");
		JSONObject userData = LoginResult.getJSONObject("data");
		
		FileDo fileDo = new FileDo();
		JSONObject result = fileDo.uploadFile(userData);
		System.out.println(result);
	}
	
	@Test(dataProvider="noPCPlatform",dataProviderClass=UserDataProvider.class)
	public void test_recommendGoods(String platform) {
		Reporter.log("推荐商品测试");
		
		String goodId = goodData.getGoodId_IsOnLive();
		Reporter.log("查询出完全没有推荐商品："+goodId);
		
		Reporter.log("[会员操作(无登录)]-----");
		Reporter.log("1）查看商品（"+goodId+"）的推荐商品 列表：自动补全6个");
		JSONObject getRecommendListResult = goodsRecommend.getRecommendList( goodId, platform);
		assertEquals(getRecommendListResult.getString("code"), "10000","商城推荐商品列表查询"+getRecommendListResult);
		assertEquals(getRecommendListResult.getJSONArray("data").size(), 6,"查看推荐商品是否补全到6个");

		Reporter.log("[运营中心操作]-----");
		JSONObject sysLoginResult = login.sysLogin("helen", "123456li");
		assertEquals(sysLoginResult.getString("code"), "10000","运营中心登录校验"+sysLoginResult);
		JSONObject sysUserdata = sysLoginResult.getJSONObject("data");

		Reporter.log("1）查询出已上架商品列表");
		JSONObject onLiveGoodsListResult = goodsAdmin.goodsList(sysUserdata, 1);
		assertEquals(onLiveGoodsListResult.getString("code"), "10000","查询商品列表（已上架）校验"+onLiveGoodsListResult);
		JSONArray onLiveGoodsList = onLiveGoodsListResult.getJSONObject("data").getJSONArray("list");
		//把前面几个商品作为推荐商品
		JSONArray recommendGoodsList = new JSONArray();
		ArrayList<String> goodsIdList = new ArrayList<String>();
		for (int i = 1; i < 7; i++) {
			JSONObject good = new JSONObject();
			good.put("id", onLiveGoodsList.getJSONObject(i).getString("id"));//商品ID
			good.put("name", onLiveGoodsList.getJSONObject(i).getString("name"));//商品名称
			recommendGoodsList.add(good);
			goodsIdList.add(onLiveGoodsList.getJSONObject(i).getString("id"));
		}
		
		Reporter.log("2）添加推荐商品（推荐6个）："+recommendGoodsList);
		JSONObject addRecommendGoodsResult = goodsRecommend.addRecommendGoods(sysUserdata, goodId, "1", recommendGoodsList);
		assertEquals(addRecommendGoodsResult.getString("code"), "10000","添加推荐商品校验"+addRecommendGoodsResult);

		Reporter.log("[会员操作(登录)]-----");
		String username = memberData.get_account("phone");
		Reporter.log("1）登录商城："+username);
		JSONObject loginResult = login.login(username, "123456li", platform);
		assertEquals(loginResult.getString("code"), "10000","商城登录校验"+loginResult);
		JSONObject userdata = loginResult.getJSONObject("data");
		
		Reporter.log("2）查看商品（"+goodId+"）的推荐商品 列表");
		JSONObject getRecommendListResult2 = goodsRecommend.getRecommendList(userdata, goodId, platform);
		assertEquals(getRecommendListResult2.getString("code"), "10000","商城推荐商品列表查询"+getRecommendListResult2);
		JSONArray getRecommendList2 = getRecommendListResult2.getJSONArray("data");
		String recommerndId1 = getRecommendList2.getJSONObject(baseData.getNum(0, getRecommendList2.size()-1)).getString("goodsId");
		assertTrue(goodsIdList.contains(recommerndId1),"随机抽检一个商品进行较验:"+recommerndId1);
		
		Reporter.log("[运营中心操作]-----");
		Reporter.log("1）删除5个推荐商品");
		JSONArray deleteRecommendGoodsList = new JSONArray();
		for (int i = 0; i < 5; i++) {
			deleteRecommendGoodsList.add(recommendGoodsList.getJSONObject(i));
		}
		JSONObject deleteRecommendGoodsResult = goodsRecommend.deleteRecommendGoods(sysUserdata, goodId, deleteRecommendGoodsList);
		assertEquals(deleteRecommendGoodsResult.getString("code"), "10000","删除推荐商品校验"+deleteRecommendGoodsResult);
		
		Reporter.log("[会员操作]-----");
		Reporter.log("1）查看推荐商品列表是否补足6个");
		JSONObject getRecommendListResult3 = goodsRecommend.getRecommendList(userdata, goodId, platform);
		assertEquals(getRecommendListResult3.getString("code"), "10000","商城推荐商品列表查询"+getRecommendListResult3);
		assertEquals(getRecommendListResult3.getJSONArray("data").size(), 6,"查看推荐商品是否补全到6个");
	}
	
}
