/**
 * @author helen
 * @date 2019年4月1日
 */
package order;

import com.alibaba.fastjson.JSONObject;

import common.MyAPI;

/**
 * @Description:运营中心－康养卡业务
 */
public class HealthCardAdmin {
	MyAPI myAPI = new MyAPI();
	
	/*获取康养卡配置列表*/
	public JSONObject healthCardConfigList(JSONObject sysUserdata) {
		JSONObject result = new JSONObject();
		result = myAPI.sysGet(sysUserdata, "/goodsCenter/admin/healthCard/configList");
		return result;
	}
	
	/*购买对账*/
	public JSONObject financeCheck(JSONObject sysUserdata,int checkStatus,int orderId,String remark) {
		JSONObject params = new JSONObject();
		params.put("checkStatus", checkStatus);//@checkStatus (integer): 对账状态: 0.添加备注、1.确认未到账、2.确认到账 ,
		params.put("orderId", orderId);//订单号
		params.put("remark", remark);//备注
		
		JSONObject result = new JSONObject();
		result = myAPI.sysPost(sysUserdata, "/tradeCenter/healthCardOrder/admin/finance/check", params);
		return result;
	}
	
	/*升级对账*/
	public JSONObject upFinanceCheck(JSONObject sysUserdata,int checkStatus,int orderId,String remark) {
		JSONObject params = new JSONObject();
		params.put("checkStatus", checkStatus);//@checkStatus (integer): 对账状态: 0.添加备注、1.确认未到账、2.确认到账 ,
		params.put("orderId", orderId);//订单号
		params.put("remark", remark);//备注
		
		JSONObject result = new JSONObject();
		result = myAPI.sysPost(sysUserdata, "/tradeCenter/healthCardUpOrder/admin/finance/check", params);
		return result;
	}
	
	/*康养卡列表查询*/
	public JSONObject healthCardList(JSONObject sysUserdata,String cardNo) {
		JSONObject params = new JSONObject();
		params.put("cardNo", cardNo);//卡号
		params.put("page", 1);
		params.put("pageSize", 20);
		
		JSONObject result = new JSONObject();
		result = myAPI.sysPost(sysUserdata, "/goodsCenter/admin/healthCard/goods/list", params);
		return result;
	}
}
