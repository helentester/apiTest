/**
 * @author helen
 * @date 2018年11月27日
 */
package good;

import static org.testng.Assert.assertEquals;

import java.math.BigDecimal;

import org.testng.Reporter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import common.BaseData;
import common.MyAPI;

/**
 * @Description:商品SKU相关接口
 */
public class GoodsSKU {
	BaseData baseData = new BaseData();
	MyAPI myAPI = new MyAPI();
	
	public JSONObject skuModelAdd(JSONObject userdata,String goodsId,String name,String pid) {
		JSONObject params = new JSONObject();
		params.put("goodsId", goodsId);//商品ID
		params.put("name", name);//SKU模板ID
		params.put("pid", Integer.valueOf(pid));//父级模板ID，0表示商品规格
		JSONObject result = new JSONObject();
		if (userdata.getString("userType").equals("backend")) {
			result = myAPI.sysPost(userdata, "/goodsCenter/admin/goods/sku/model/add", params);
		}
		else if (userdata.getString("userType").equals("supplier")) {
			result = myAPI.sellerPost(userdata, "/goodsCenter/admin/goods/sku/model/add", params);
		}
		else {
			System.out.println(userdata.getString("userType")+"的用户没有相应公共接口");
		}
		return result;
	}
	
	public JSONArray skuAdd(JSONObject userdata,String goodsId,String isHealth) {
		Reporter.log("添加商品规格：颜色");
		JSONObject modeAdd_colourResult = this.skuModelAdd(userdata, goodsId, "颜色", "0");
		assertEquals(modeAdd_colourResult.getString("code"), "10000","添加商品规格校验"+modeAdd_colourResult);
		String colourId =modeAdd_colourResult.getJSONObject("data").getString("id");
		
		Reporter.log("添加SKU：红");
		JSONObject skuResult1 = this.skuModelAdd(userdata, goodsId, "红", colourId);
		assertEquals(skuResult1.getString("code"), "10000","添加SKU校验"+skuResult1);
		
		Reporter.log("添加SKU：黄");
		JSONObject skuResult2 = this.skuModelAdd(userdata, goodsId, "黄", colourId);
		assertEquals(skuResult2.getString("code"), "10000","添加SKU校验"+skuResult2);
		
		Reporter.log("添加商品规格：尺码");
		JSONObject modeAdd_sizeResult = this.skuModelAdd(userdata, goodsId, "尺码", "0");
		assertEquals(modeAdd_sizeResult.getString("code"), "10000","添加商品规格校验"+modeAdd_sizeResult);
		String sizeId = modeAdd_sizeResult.getJSONObject("data").getString("id");
		
		Reporter.log("添加SKU：大");
		JSONObject skuResult3 = this.skuModelAdd(userdata, goodsId, "大", sizeId);
		assertEquals(skuResult3.getString("code"), "10000","添加SKU校验"+skuResult3);
		
		Reporter.log("添加SKU：小");
		JSONObject skuResult4 = this.skuModelAdd(userdata, goodsId, "小", sizeId);
		assertEquals(skuResult4.getString("code"), "10000","添加SKU校验"+skuResult4);
		JSONArray skuList = skuResult4.getJSONObject("data").getJSONArray("goodsSkuList");//获取返回的SKU列表

		Reporter.log("设置SKU的数据");
		JSONArray goodsSkuList = new JSONArray();
		for (int i = 0; i < skuList.size(); i++) {
			int Price = baseData.getNum(1, 9999);
			JSONObject sku = new JSONObject();
			sku.put("id", skuList.getJSONObject(i).getString("id"));//SKU的ID
			sku.put("bought", baseData.getNum(10, 1000));//库存
			sku.put("minBuyCount", "1");//最小购买数
			sku.put("maxBuyCount", "9999");//最小购买数	
			sku.put("useHscore", isHealth);//能否使用H乐豆：0，否，不能使用，1，能，能使用，默认：0，必填 ,
			if (isHealth.equals("0")) {
				sku.put("currentPrice", Price);//当前前售价
				sku.put("balancePrice", BigDecimal.valueOf(Price).multiply(BigDecimal.valueOf(0.65)));//结算价
				sku.put("integralUseHratio", 0);//乐豆最大可使用点数
				//设置可用福豆
				int useScore = baseData.getNum(0, 1);
				sku.put("useScore", useScore);//能否使用福豆 0为否；1为是，必填
				if (useScore==1) {
					sku.put("integralUseRatio", baseData.getNum(1, Price));//福豆最大使用点数 
				}
				else {
					sku.put("integralUseRatio", 0);//福豆最大使用点数 
				}
			}
			else {
				sku.put("useScore", 0);//能否使用福豆 0为否；1为是，必填
				sku.put("currentPrice", 0);//当前前售价
				sku.put("balancePrice", 0);//结算价
				sku.put("integralUseRatio", 0);//福豆最大使用点数 
				sku.put("integralUseHratio", Price);//乐豆最大可使用点数
			}
					
			goodsSkuList.add(sku);
		}
		return goodsSkuList;
	}

}
