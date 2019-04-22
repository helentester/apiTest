/**
 * @author helen
 * @date 2019年4月8日
 */
package testcase;

import static org.testng.Assert.assertEquals;

import java.math.BigDecimal;

import org.testng.Reporter;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import agent.AgentFund;
import common.BaseData;
import data.MemberData;
import data.OrderData;
import good.Rooms;
import order.HotelOrder;
import order.HotelOrderadmin;
import seller.SupplierInfo;
import user.Login;
import user.MemberCenter;

/**
 * @Description:康养公寓订单分润
 */
public class HotelProfitTest {
	BaseData baseData = new  BaseData();
	MemberData memberData = new MemberData();
	OrderData orderData = new OrderData();
	Rooms rooms = new Rooms();
	Login login = new Login();
	HotelOrder hotelOrder = new HotelOrder();
	HotelOrderadmin hotelOrderadmin = new HotelOrderadmin();
	MemberCenter memberCenter = new MemberCenter();
	SupplierInfo supplierInfo = new SupplierInfo();
	AgentFund agentFund = new AgentFund();
	
	@Test
	public void test_order() {
		Reporter.log("康养公寓分润（未完成）");
		String platform = "h5";
		String username = "15106297284";
		String userInviter = "13208624938";
		String sellerName = "17702236515";
		String agentNameA = "17105040795";
		
		try {
			Reporter.log("[会员操作]-------");
			Reporter.log("1）会员登录："+username);
			JSONObject loginResult = login.login(username, "123456li", platform);
			assertEquals(loginResult.getString("code"), "10000","登录校验"+loginResult);
			JSONObject userdata = loginResult.getJSONObject("data");
			
			Reporter.log("2）获取康养公寓专题列表");
			JSONObject roomListResult = rooms.roomList("广州", "2019-04-01", "2019-05-01", platform);
			assertEquals(roomListResult.getString("code"), "10000","获取康养公寓专题列表"+roomListResult);
			
			Reporter.log("3）下订单");
			JSONObject orderSubmitResult = hotelOrder.submit(userdata, "张三", String.valueOf(baseData.getTimeStamp13(0)), String.valueOf(baseData.getTimeStamp13(2)), 95, 1, "13822289691", platform);
			assertEquals(orderSubmitResult.getString("code"), "10000","提交订单"+orderSubmitResult);
			JSONObject orderSubmitData = orderSubmitResult.getJSONObject("data");
			
			Reporter.log("4）支付订单:"+orderSubmitData.getString("orderNo"));
			JSONObject payOrderResult = hotelOrder.payOrder(userdata, orderSubmitData.getIntValue("orderId"), orderSubmitData.getString("orderNo"), 3, "alipay",orderSubmitData.getString("payTotalPrice"), platform);
			assertEquals(payOrderResult.getString("code"), "95270","支付订单:msg=使用模拟支付");
			
			Thread.sleep(5000);
			Reporter.log("5）查看订单详情：检查是否支付成功");
			JSONObject orderDetailResult = hotelOrder.detail(userdata, orderSubmitData.getIntValue("orderId"), platform);
			assertEquals(orderDetailResult.getString("code"), "10000","查看订单详情"+orderDetailResult);
			assertEquals(orderDetailResult.getJSONObject("data").getString("orderStatus"), "2","检查订单是否是已支付待确认状态");
			JSONObject orderDetail = orderDetailResult.getJSONObject("data");
			
			Reporter.log("[商家中心]-----");
			Reporter.log("1）商家登录："+sellerName);
			JSONObject sellerLoginResult = login.KYSellerLogin(sellerName, "");
			assertEquals(sellerLoginResult.getString("code"), "10000","商家登录"+sellerLoginResult);
			JSONObject sellerUserdata = sellerLoginResult.getJSONObject("data");
			
			Reporter.log("2）查看订单详情,并填写入住参数");
			JSONObject orderDetailInSellerResult =hotelOrderadmin.OrderDetail(sellerUserdata, orderDetail.getIntValue("id"));
			assertEquals(orderDetailInSellerResult.getString("code"), "10000","商家查看订单详情"+orderDetailInSellerResult);
			JSONObject orderDetailInSeller = orderDetailInSellerResult.getJSONObject("data");
			JSONArray liverInfosDetail = orderDetailInSeller.getJSONArray("liveInfos");
			JSONArray liverInfoList = new JSONArray();
			for (int i = 0; i < liverInfosDetail.size(); i++) {
				JSONObject liverInfo = new JSONObject();
				liverInfo.put("id", liverInfosDetail.getJSONObject(i).getInteger("id"));//
				liverInfo.put("liveName", liverInfosDetail.getJSONObject(i).getString("liveName"));// (string, optional): 入住人 ,
				liverInfo.put("roomNo", "room_"+baseData.getNum(0, 10000));//roomNo (string, optional): 房间编号
				liverInfoList.add(liverInfo);
			}
			
			Reporter.log("3）分配房间");
			JSONObject distributionRoomResult = hotelOrderadmin.distributionRoom(sellerUserdata, orderDetail.getIntValue("id"), liverInfoList) ;
			assertEquals(distributionRoomResult.getString("code"), "10000","分配房间"+distributionRoomResult);
			
			Reporter.log("[会员操作]------");
			Reporter.log("1）评论订单");//orderData.getJSONArray("orderItemList").getJSONObject(0).getIntValue("id")
			JSONObject evaluateResult = hotelOrder.evaluate(userdata, orderDetail.getIntValue("id"), orderDetail.getString("orderSn"), "0", platform);
			assertEquals(evaluateResult.getString("code"), "10000","评论订单"+evaluateResult);
			
			Reporter.log("2）在数据库把订单状态改为：4已入住");
			orderData.updateHOrderStatus(orderDetail.getString("id"), "4",baseData.getTimeByDays(-1, "yyyy-MM-dd HH:mm:ss"));
			
			Reporter.log("3）等待订单完结");
			Thread.sleep(360000);//定时任务5分钟执行一次
			Reporter.log("4）查看订单是否完结");
			JSONObject orderDetailResult2 =hotelOrder.detail(userdata, orderDetail.getIntValue("id"),platform);
			assertEquals(orderDetailResult2.getString("code"), "10000","查看订单详情"+orderDetailResult2);
			assertEquals(orderDetailResult2.getJSONObject("data").getString("orderStatus"), "5","检查订单是否已经完结");
			
			Reporter.log("[检查各账户]----------");
			Reporter.log("1）会员账户校验："+username);
			JSONObject userInfoResult = memberCenter.getMemberAccountInfo(userdata, platform);
			assertEquals(userInfoResult.getString("code"), "10000","会员账户"+userInfoResult);
			BigDecimal userScore = userInfoResult.getJSONObject("data").getBigDecimal("score");//消费福豆
			BigDecimal UserRetirementScore = userInfoResult.getJSONObject("data").getBigDecimal("retirementScore");//养老福豆
			Reporter.log("会员消费福豆＝"+userScore);
			Reporter.log("会员养老福豆＝"+UserRetirementScore);
			
			Reporter.log("2）邀请人账户校验:"+userInviter);
			JSONObject userInviterLoginResult = login.login(userInviter, "", platform);
			assertEquals(userInviterLoginResult.getString("code"), "10000","会员登录"+userInviterLoginResult);
			JSONObject userInviterData = userInviterLoginResult.getJSONObject("data");
			
			JSONObject userInviterInfoResult = memberCenter.getMemberAccountInfo(userInviterData, platform);
			BigDecimal userInviterBalance = userInviterInfoResult.getJSONObject("data").getBigDecimal("balance");//邀请人余额
			Reporter.log("邀请人余额＝"+userInviterBalance);

			Reporter.log("3）商家账户检校验");
			JSONObject sellerInfoResult = supplierInfo.homePageInfo(sellerUserdata);
			assertEquals(sellerInfoResult.getString("code"), "10000","商家账户信息"+sellerInfoResult);
			BigDecimal sellerBean = sellerInfoResult.getJSONObject("data").getBigDecimal("totalBean");//商家总养老福豆
			BigDecimal sellerTotalBalance = sellerInfoResult.getJSONObject("data").getBigDecimal("totalBalance");//商家总余额
			BigDecimal sellerAvailableBalance = sellerInfoResult.getJSONObject("data").getBigDecimal("availableBalance");//商家可用余额
			Reporter.log("商家总养老福豆＝"+sellerBean);
			Reporter.log("商家总余额＝"+sellerTotalBalance);
			Reporter.log("商家可用余额＝"+sellerAvailableBalance);
			
			Reporter.log("4）商家的代理A账户校验");
			JSONObject agentLoginReulst = login.agentLogin(agentNameA, "");
			assertEquals(agentLoginReulst.getString("code"), "10000","代理商登录"+agentLoginReulst);
			JSONObject agentUserdata = agentLoginReulst.getJSONObject("data");
			
			JSONObject agentInfoResult = agentFund.agentFundInfo(agentUserdata);
			assertEquals(agentInfoResult.getString("code"), "10000","代理信息"+agentInfoResult);
			BigDecimal agentBalance = agentInfoResult.getJSONObject("data").getBigDecimal("balance");//代理商余额
			BigDecimal agentAbledBalance = agentInfoResult.getJSONObject("data").getBigDecimal("abledBalance");//代理商可用余额
			Reporter.log("代理商A余额＝"+agentBalance);
			Reporter.log("代理商A可用余额＝"+agentAbledBalance);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
