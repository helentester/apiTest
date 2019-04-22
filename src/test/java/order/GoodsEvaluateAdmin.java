/**
 * @author helen
 * @date 2018年11月22日
 */
package order;

import com.alibaba.fastjson.JSONObject;

import common.MyAPI;

/**
 * @Description:订单商品评价管理后台接口 : Goods Evaluate Admin Controller
 */
public class GoodsEvaluateAdmin {
	MyAPI myAPI = new MyAPI();
	
	/*查询订单商品评价详细信息*/
	public JSONObject evaluateDetail(JSONObject sysUserdata,String orderId,String orderItemId) {
		JSONObject params = new JSONObject();
		params.put("orderId", orderId);//订单ID
		params.put("orderItemId", orderItemId);//订单明细ID
		return myAPI.sysPost(sysUserdata, "/tradeCenter/order/admin/evaluate/detail", params);
	}
	
	/*查询订单商品评价列表*/
	public JSONObject evaluateList(JSONObject sysUserdata,String orderSn) {
		JSONObject params = new JSONObject();
		params.put("orderSn", orderSn);
		return myAPI.sysPost(sysUserdata, "/tradeCenter/order/admin/evaluate/list", params);
	}
	
	/*审核/审核通过，审核拒绝订单商品评价和回复 */
	public JSONObject auditShow(JSONObject sysUserdata,String evaluateDetailId,String auditStatus) {		
		JSONObject params = new JSONObject();
		params.put("evaluateDetailId", evaluateDetailId);//评价详情ID
		params.put("auditStatus", auditStatus);//审核: 0.拒绝、1.通过 ,

		return myAPI.sysPost(sysUserdata, "/tradeCenter/order/admin/evaluate/auditShow/update", params);
	}

}
