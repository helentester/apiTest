/**
 * @author helen
 * @date 2018年11月30日
 */
package user;

import com.alibaba.fastjson.JSONObject;

import common.BaseData;
import common.MyAPI;

/**
 * @Description:提现相关接口
 */
public class FundApply {
	BaseData baseData = new BaseData();
	MyAPI myAPI = new MyAPI();
	
	/*获取商家信息（获取可提现金额等信息）*/
	public JSONObject fundApplyInfo(JSONObject userdata) {
		JSONObject params = new JSONObject();
		params.put("supplierId", userdata.getString("supplierId"));
		return myAPI.sellerPost(userdata, "/userCenter/fund/supplier/apply/list", params);
	}
	
	/*获取商家提现结单时间*/
	public JSONObject getStatementTime(JSONObject userdata) {
		JSONObject params = new JSONObject();
		params.put("supplierId", userdata.getString("supplierId"));
		
		return myAPI.sellerGet(userdata, "/userCenter/fund/supplier/apply/getStatementTime", params);
	}
	
	/*商家提现申请*/
	public JSONObject fundApplyAdd(JSONObject userdata,String statementBeginTime,String statementEndTime) {
		JSONObject params = new JSONObject();
		params.put("supplierId", userdata.getString("supplierId"));
		params.put("statementBeginTime", statementBeginTime);//结单开始时间
		params.put("statementEndTime", statementEndTime);//结单结束时间
		params.put("voucher", "/user/idcard/1550824167394.png");//提取凭证
		
		return myAPI.sellerPost(userdata, "/userCenter/fund/supplier/apply/add", params);
	}
	
	/*商家提现申请列表*/
	public JSONObject fundApplyList(JSONObject userdata,String supplierId,int status) {
		JSONObject params = new JSONObject();
		params.put("supplierId", supplierId);//商家ID
		params.put("status", 1);//状态：1.待审核、2.审核不通过、3.审核通过、4.转账失败、5.转账成功 
		
		JSONObject result = new JSONObject();
		result = myAPI.sellerPost(userdata, "/userCenter/fund/apply/supplier/list", params);
		return result;
	}
	
	/*商家不可用余额查询*/
	public JSONObject fundUnavailableList(JSONObject userdata) {
		JSONObject params = new JSONObject();
		params.put("id", userdata.getString("supplierId"));
		return myAPI.sellerPost(userdata, "/userCenter/fund/supplier/unavailable/list", params);
	}
	
	/*运营中心审核提现*/
	public JSONObject applyAudit(JSONObject sysUserdata,String applyId,boolean isAduditPass) {
		JSONObject params = new JSONObject();
		params.put("applyId", applyId);//提现申请ID；
		params.put("isAduditPass", isAduditPass);//是否审核通过 ,true 、false
		params.put("remark", "提现申请审核（接口提交）");//备注(审核不通过原因) ,
		params.put("userId", sysUserdata.getString("uid")); //审核人ID
		return myAPI.sysPost(sysUserdata, "/userCenter/fund/apply/audit", params);
	}
	
	/*运营中心转账*/
	public JSONObject applyTransfer(JSONObject sysUserdata,String applyId,boolean isSuccess) {
		JSONObject params = new JSONObject();
		params.put("applyId", applyId);//提现申请ID
		params.put("isSuccess", isSuccess);//是否转账成功，true、false
		params.put("remark", "转账操作（接口提交）");//备注(转账失败原因) 
		params.put("userId", sysUserdata.getString("uid"));
		return myAPI.sysPost(sysUserdata, "/userCenter/fund/apply/transfer", params);
	}
	
	/*资金流水列表*/
	public JSONObject journalList(JSONObject userdata,String incomeType,String type) {
		JSONObject params = new JSONObject();
		params.put("incomeType", incomeType);//(string, optional): 收支类型：0.收入、1.支出 ,
		params.put("type", type);//(string, optional): 商家业务类型:3.订单入账、4.退款、5.线下扫码支付、6.提现
		params.put("supplierId", userdata.getString("supplierId"));
		return myAPI.sellerPost(userdata, "/userCenter/fund/journal/supplier/list", params);
	}
	
	/*资金流水详情*/
	public JSONObject journalDetail(JSONObject userdata,String journalId) {
		JSONObject params = new JSONObject();
		params.put("id", journalId);
		return myAPI.sellerPost(userdata, "/userCenter/fund/journal/supplier/detail", params);
	}

}
