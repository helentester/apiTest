/**
 * @author helen
 * @date 2019年1月14日
 */
package testcase;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import org.testng.Reporter;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import common.BaseData;
import data.MemberData;
import dataProvider.UserDataProvider;
import user.Login;
import user.MemberCollection;
import user.Registry;
import user.VerificationCode;

/**
 * @Description:商品收藏测试
 */
public class MemberCollerctionTest {
	BaseData baseData = new BaseData();
	MemberData memberData = new MemberData();
	Login loginTest= new Login();
	Registry registryTest = new Registry();
	MemberCollection memberCollection = new MemberCollection();
	VerificationCode verificationCode = new VerificationCode();
	
	@Test(dataProvider="noPCPlatform",dataProviderClass=UserDataProvider.class)
	public void test_Collection(String platform) {
		Reporter.log("[收藏商品测试]");
		
		String username = baseData.getPhoneNumber();
		Reporter.log("1）注册新用户："+username);
		//获取验证码
		JSONObject sendCodeResult = verificationCode.sendCode(username, "", 1, platform);
		assertEquals(sendCodeResult.getString("code"), "10000", "发送验证码校验" + sendCodeResult);
		String verifyCode = memberData.getVerificationCode(username, "phone");
		
		JSONObject registryResult = registryTest.register("43", username,verifyCode, "", platform, "phone");
		assertEquals(registryResult.getString("code"), "10000","手机用户注册");
		
		Reporter.log("2）用户登录");
		JSONObject loginResult = loginTest.login(username, "123456li", platform);
		assertEquals(loginResult.getString("code"), "10000", "用户登录校验：" + loginResult);
		JSONObject userdata = loginResult.getJSONObject("data");
		
		Reporter.log("3）收藏大健康商品");
		JSONObject collectHealthGoodResult = memberCollection.switchOnCollection(userdata, "7215", "1", platform);
		assertEquals(collectHealthGoodResult.getString("code"), "10000","收藏大健康商品"+collectHealthGoodResult);

		Reporter.log("4）收藏普通商品");
		JSONObject CollectNomalGoodResult = memberCollection.switchOnCollection(userdata, "7813", "1", platform);
		assertEquals(CollectNomalGoodResult.getString("code"), "10000","收藏普通商品"+CollectNomalGoodResult);
		
		Reporter.log("5）收藏虚拟商品");
		JSONObject CollectServiceGoodResult = memberCollection.switchOnCollection(userdata, "7183", "1", platform);
		assertEquals(CollectServiceGoodResult.getString("code"), "10000","收藏虚拟商品"+CollectServiceGoodResult);
		
		Reporter.log("6）收藏顺道商品");
		JSONObject CollectSundaoGoodResult = memberCollection.switchOnCollection(userdata, "7090", "1", platform);
		assertEquals(CollectSundaoGoodResult.getString("code"), "10000","收藏虚拟商品"+CollectSundaoGoodResult);
		
		Reporter.log("7）查询收藏列表：应当有三个收藏商品");
		JSONObject listResult = memberCollection.CollectionList(userdata, "{\"price\":\"asc\",\"time\":\"desc\"}", "", platform);
		assertEquals(listResult.getString("code"), "10000","收藏列表"+listResult);
		assertEquals(listResult.getJSONObject("data").getString("listCount"), "4","应当有4个收藏");
		JSONArray goodsArray = listResult.getJSONObject("data").getJSONArray("list");
		JSONObject switchGoods = goodsArray.getJSONObject(baseData.getNum(0, 3));
		
		Reporter.log("8）取消收藏商品，商品ID："+switchGoods.getString("goodsId"));
		JSONObject removeCollerctResult = memberCollection.switchOnCollection(userdata, switchGoods.getString("goodsId"), "0", platform);
		assertEquals(removeCollerctResult.getString("code"), "10000","取消收藏大健康商品"+removeCollerctResult);
		
		
		Reporter.log("9）再次查询收藏列表，检查取消收藏后的收藏列表是否正确");
		JSONObject listResult2 = memberCollection.CollectionList(userdata, "{\"price\":\"asc\",\"time\":\"desc\"}", "", platform);
		assertEquals(listResult2.getString("code"), "10000","收藏列表"+listResult2);
		JSONArray goodsArray2 = listResult2.getJSONObject("data").getJSONArray("list");
		assertFalse(goodsArray2.contains(switchGoods),"检查取消收藏的商品是否已经从收藏列表中去除");
		goodsArray.remove(switchGoods);//把取消收藏的商品去掉
		
		JSONObject deleteGoods = goodsArray.getJSONObject(baseData.getNum(0, 2));
		Reporter.log("10）从会员的收藏列表中删除一个收藏的商品:"+deleteGoods.getString("goodsId"));
		JSONObject deleteResult = memberCollection.CollectionDelete(userdata, 0, deleteGoods.getString("goodsId"), platform);
		assertEquals(deleteResult.getString("code"), "10000","清除收藏"+deleteResult);
		
		Reporter.log("11）再次查询收藏列表，检查删除收藏的商品后的收藏列表是否正确");
		JSONObject listResult3 = memberCollection.CollectionList(userdata, "{\"price\":\"asc\",\"time\":\"desc\"}", "", platform);
		assertEquals(listResult3.getString("code"), "10000","收藏列表"+listResult3);
		JSONArray goodsArray3 = listResult3.getJSONObject("data").getJSONArray("list");
		assertFalse(goodsArray3.contains(deleteGoods),"检查取消收藏的商品是否已经从收藏列表中去除");
		
		Reporter.log("12）删除所有收藏");
		JSONObject deleteAllResult = memberCollection.CollectionDelete(userdata, 1, "", platform);
		assertEquals(deleteAllResult.getString("code"), "10000","清除收藏"+deleteResult);
		
		Reporter.log("13）再次查询收藏列表，检查删除收藏的商品后的收藏列表是否正确");
		JSONObject listResult4 = memberCollection.CollectionList(userdata, "{\"price\":\"asc\",\"time\":\"desc\"}", "", platform);
		assertEquals(listResult4.getString("code"), "10000","收藏列表"+listResult4);
		assertEquals(listResult4.getJSONObject("data").getString("listCount"), "0","应当有0个收藏");
	}

}
