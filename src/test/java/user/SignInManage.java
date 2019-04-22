/**
 * @author helen
 * @date 2018年10月22日
 */
package user;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import common.BaseData;
import common.MyAPI;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import order.TradeInvoiceAdmin;

/**
 * @Description:运营后台->签到管理接口(新版) : Sign In Manage Controller
 */
public class SignInManage {
	BaseData baseData = new BaseData();
	MyAPI myAPI = new MyAPI();
	TradeInvoiceAdmin tradeInvoiceAdmin = new TradeInvoiceAdmin();

	/**/
	
	/*
	 * 新增抽奖模版
	 * 
	 * @modelName 模版名称
	 */
	public JSONObject prizeModelAdd(JSONObject userdata, String modelName, String drawRate) {
		JSONObject params = new JSONObject();
		params.put("modelName", modelName);

		// 获取虚拟商品列表
		JSONObject getVirtualPrizeListResult = this.virtualPrizeList(userdata);
		assertEquals(getVirtualPrizeListResult.getString("code"), "10000","获取虚拟商品列表校验"+getVirtualPrizeListResult);
		JSONArray virtualPrizeList = getVirtualPrizeListResult.getJSONArray("data");
		
		//生成六个奖项
		List<JSONObject> prizeItems = new ArrayList<JSONObject>();
		for (int i = 1; i < 7; i++) {
			JSONObject prize = new JSONObject();
			prize.put("itemType", String.valueOf(i));// 奖项类型 1.奖项1、2.奖项2、3.奖项3、4.奖项4、5.奖项5、6.奖项6
			prize.put("itemName", "奖项" + baseData.getNum(0, 99999));// 奖项名称
			prize.put("drawRate", drawRate);// "15.1501");//中奖几率，4位小数。注意：第六个奖项中奖几率＝1－前5个奖项的中奖几率之和，传任何值过去，后台会自动计算
			prize.put("quantity", "8");// 奖品数量
			if (i < 3) {// 设置2个虚拟奖品
				JSONObject virtualPrize = virtualPrizeList.getJSONObject(i - 1);
				prize.put("type", "2");// 奖品类型(1.实物、2.虚拟商品)
				prize.put("prizeId", virtualPrize.getString("id"));// 奖品主键
				prize.put("prizeName", virtualPrize.getString("name"));// 奖品名称
				prize.put("cost", "3");// 每次奖品数量(奖品数量单个的价值)
			} else {// 4个实物商品
				prize.put("type", "1");// 奖品类型(1.实物、2.虚拟商品)
				prize.put("prizeName", "奖品名" + i);// 奖品名称
				prize.put("salePrice", "8.88");// 售价 虚拟商品默认为0
				prize.put("costPrice", "8.89");// 成本价 虚拟商品默认为0
			}
			
			prizeItems.add(prize);
		}
		params.put("prizeItems", prizeItems);
		JSONObject result = myAPI.sysPost(userdata,"/userCenter/member/sign/prizeModel/add", params);
		return result;
	}

	/*
	 * 审核抽奖模板
	 * 
	 * @auditStatus 1.审核通过、2.审核不通过
	 * 
	 * @templateId 抽奖模板id
	 */
	public JSONObject prizeModelAudit(JSONObject userdata, String auditStatus, String templateId) {
		JSONObject params = new JSONObject();
		params.put("auditStatus", auditStatus);// 1.审核通过、2.审核不通过 ,
		params.put("templateId", templateId);// 抽奖模板id
		JSONObject result = myAPI.sysPost(userdata, "/userCenter/member/sign/prizeModel/audit", params);
		// System.out.println(result);
		return result;
	}

	/*
	 * 删除抽奖模板
	 * 
	 * @templateId
	 */
	public JSONObject prizeModelDelete(JSONObject userdata, String templateId) {
		JSONObject params = new JSONObject();
		params.put("templateId", templateId);
		JSONObject result = myAPI.sysPost(userdata, "/userCenter/member/sign/prizeModel/delete", params);
		return result;
	}

	/*
	 * 抽奖模板详情
	 * 
	 * @templateId 抽奖模板ID
	 */
	public JSONObject prizeModelDetail(JSONObject userdata, String templateId) {
		JSONObject params = new JSONObject();
		params.put("templateId", templateId);
		JSONObject result = myAPI.sysGet(userdata, "/userCenter/member/sign/prizeModel/detail", params);
		return result;
	}

	/*
	 * 抽奖模板列表
	 * 
	 * @status '使用状态(-1.所有、0.未使用、1.使用中、2.禁用、3.已过期)',
	 * 
	 * @auditStatus '审核状态(-1.所有、0.未审核、1.审核通过、2.审核不通过)'
	 */
	public JSONObject prizeModelList(JSONObject userdata,String templateName, int status, int auditStatus) {
		JSONObject params = new JSONObject();
		params.put("templateName", templateName);// 模板名称
		params.put("status", status);// 使用状态；
		params.put("auditStatus", auditStatus);// 审核状态
		params.put("page", "1");// 第一页
		params.put("pageSize", "20");// 每页显示条数
		JSONObject result = myAPI.sysGet(userdata,"/userCenter/member/sign/prizeModel/list", params);
		return result;
	}

	/*
	 * 启用、禁用抽奖模板
	 * 
	 * @status 1.使用中、2.禁用
	 */
	public JSONObject prizeModelStart(JSONObject userdata, String templateId, String status, long beginTime,
			long endTime) {
		JSONObject params = new JSONObject();
		params.put("templateId", templateId);
		params.put("status", status);
		params.put("beginTime", beginTime);
		params.put("endTime", endTime);
		JSONObject result = myAPI.sysPost(userdata, "/userCenter/member/sign/prizeModel/start", params);
		return result;
	}

	/*
	 * 签到记录列表查询
	 * 
	 * @account 会员名或手机号或邮箱
	 * 
	 * @recordType 签到类型:1.普通签到、2.补签(补签券)、3.补签(福豆)
	 */
	public void recordList(String account, String beginTime, String endTime, String recordType) {
		JSONObject params = new JSONObject();
		params.put("account", account);
		params.put("beginTime", beginTime);
		params.put("endTime", endTime);
		params.put("recordType", recordType);
		params.put("page", "1");// 第一页
		params.put("pageSize", "20");// 每页显示条数

		JSONObject result = myAPI.sysPost("/userCenter/member/sign/record/list", params);
		System.out.println(result);
	}

	/* 导出签到记录列表 */
	public void recordListExport(String account, String beginTime, String endTime, String recordType) {
		JSONObject params = new JSONObject();
		params.put("account", account);
		params.put("beginTime", beginTime);
		params.put("endTime", endTime);
		params.put("recordType", recordType);
		JSONObject result = myAPI.sysPost("/userCenter/member/sign/record/list/export", params);
		System.out.println(result);
	}

	/* 虚拟奖品下拉列表 */
	public JSONObject virtualPrizeList(JSONObject sysUserdata) {
		JSONObject result = myAPI.sysGet(sysUserdata,"/userCenter/member/sign/virtualPrize/list");
		return result;
	}

	/*
	 * 中奖列表导出
	 * 
	 * @name 会员名/手机号/邮箱 ,
	 * 
	 * @drawStatus 奖品状态(-1.所有 0.未领取、1.待发货、2.已发货、3.已收货、4.已失效)
	 * 
	 * @drawType 奖品性质(-1.所有 1.实物、2.虚拟商品) ,
	 * 
	 * @itemType 奖项类型(-1.所有 1.奖项1、2.奖项2、3.奖项3、4.奖项4、5.奖项5、6.奖项6) ,
	 */
	public void drawExporter(JSONObject userdata,String name, String beginTime, String endTime, String itemType, String templateId,
			String drawStatus, String drawType) {
		JSONObject params = new JSONObject();
		params.put("name", name);
		params.put("beginTime", beginTime);
		params.put("endTime", endTime);
		params.put("itemType", itemType);
		params.put("templateId", templateId);
		params.put("drawStatus", drawStatus);
		params.put("drawType", drawType);
		params.put("page", "1");// 第一页
		params.put("pageSize", "20");// 每页显示条数

		JSONObject result = myAPI.sysGet(userdata,"/userCenter/member/sign/draw/exporter", params);
		System.out.println(result);
	}

	/*
	 * 中奖列表
	 * 
	 * @name 会员名/手机号/邮箱 ,
	 * 
	 * @drawStatus 奖品状态(-1.所有 0.未领取、1.待发货、2.已发货、3.已收货、4.已失效)
	 * 
	 * @drawType 奖品性质(-1.所有 1.实物、2.虚拟商品) ,
	 * 
	 * @itemType 奖项类型(-1.所有 1.奖项1、2.奖项2、3.奖项3、4.奖项4、5.奖项5、6.奖项6) ,
	 */
	public void drawList(JSONObject userdata,String name, String beginTime, String endTime, String itemType, String templateId,
			String drawStatus, String drawType) {
		JSONObject params = new JSONObject();
		params.put("name", name);
		params.put("beginTime", beginTime);
		params.put("endTime", endTime);
		params.put("itemType", itemType);
		params.put("templateId", templateId);
		params.put("drawStatus", drawStatus);
		params.put("drawType", drawType);
		params.put("page", "1");// 第一页
		params.put("pageSize", "20");// 每页显示条数

		JSONObject result = myAPI.sysGet(userdata,"/userCenter/member/sign/draw/list", params);
		System.out.println(result);
	}

	/* 百日奖品 / 普通奖品立即发货 */
	public JSONObject drawSend(JSONObject userdata, String drawId) {
		//获取后台物流信息列表
		JSONObject deliverResult = tradeInvoiceAdmin.deliveryList(userdata).getJSONObject("data");
		JSONArray deliverList = deliverResult.getJSONArray("list");
		JSONObject delivery = deliverList.getJSONObject(baseData.getNum(1, 9));
		
		JSONObject params = new JSONObject();
		params.put("deliveryId", delivery.getString("id"));//物流公司ID
		params.put("deliveryName", delivery.getString("name"));//物流公司名称
		params.put("deliverySn", String.valueOf(baseData.getNum(0, 99999999)));//物流单号
		params.put("drawId", drawId);//奖品记录ID
		JSONObject result = myAPI.sysPost(userdata,"/userCenter/member/sign/draw/send", params);
		return result;
	}

	/* 中奖统计列表 */
	public void drawStatistic(JSONObject userdata,String templateId) {
		JSONObject params = new JSONObject();
		params.put("templateId", templateId);
		params.put("page", "1");// 第一页
		params.put("pageSize", "20");// 每页显示条数
		JSONObject result = myAPI.sysGet(userdata,"/userCenter/member/sign/draw/statistic", params);
		System.out.println(result);
	}

	/* 中奖统计列表导出 */
	public void drawStatisticExporter(JSONObject userdata,String templateId) {
		JSONObject params = new JSONObject();
		params.put("templateId", templateId);
		params.put("page", "1");// 第一页
		params.put("pageSize", "20");// 每页显示条数
		JSONObject result = myAPI.sysGet(userdata,"/userCenter/member/sign/drawStatistic/exporter", params);
		System.out.println(result);
	}

	/* 百日中奖详情 */
	public void hundredDrawDetail(JSONObject userdata,String userId) {
		JSONObject params = new JSONObject();
		params.put("userId", userId);
		JSONObject result = myAPI.sysGet(userdata,"/userCenter/member/sign/hundredDraw/detail", params);
		System.out.println(result);
	}

	/*
	 * 百日中奖列表
	 * 
	 * @name 会员名/手机号/邮箱
	 */
	public void hundredDrawList(JSONObject userdata,String name) {
		JSONObject params = new JSONObject();
		params.put("name", name);
		params.put("page", "1");// 第一页
		params.put("pageSize", "20");// 每页显示条数
		JSONObject result = myAPI.sysGet(userdata,"/userCenter/member/sign/hundredDraw/list", params);
		System.out.println(result);
	}

	/* 百日中奖列表导出 */
	public void hundredDrawExporter(JSONObject userdata,String name) {
		JSONObject params = new JSONObject();
		params.put("name", name);
		params.put("page", "1");// 第一页
		params.put("pageSize", "20");// 每页显示条数
		JSONObject result = myAPI.sysGet(userdata,"/userCenter/member/sign/hundredDraw/exporter", params);
		System.out.println(result);
	}

}
