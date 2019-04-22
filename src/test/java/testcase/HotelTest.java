/**
 * @author helen
 * @date 2019年4月9日
 */
package testcase;

import static org.testng.Assert.assertEquals;

import org.testng.Reporter;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import operationCenter.RoomsAdmin;
import seller.RoomManage;
import user.Login;

/**
 * @Description:康养公寓相关测试
 */
public class HotelTest {
	Login login = new Login();
	RoomsAdmin roomsAdmin = new RoomsAdmin();
	RoomManage roomManage = new RoomManage();
	
	@Test
	public void test_() {
		Reporter.log("添加房间-审核－上架");
		
		String sellerName = "17702236515";
		Reporter.log("[康养商家中心]－－－－");
		Reporter.log("1）商家登录："+sellerName);
		JSONObject sellerLoginResult = login.KYSellerLogin(sellerName, "123456li");
		assertEquals(sellerLoginResult.getString("code"), "10000","商家登录"+sellerLoginResult);
		JSONObject sellerUserdata = sellerLoginResult.getJSONObject("data");
		
		Reporter.log("2）添加房间");
		JSONObject addRoomsResult = roomManage.addRoom(sellerUserdata, "单人房", 1, 1, 200, 180, 1, 0, 0);
		assertEquals(addRoomsResult.getString("code"), "10000","添加房间"+addRoomsResult);
		
		Reporter.log("3）查询房间列表：查所有");
		JSONObject sellerRoomListResult = roomManage.roomList(sellerUserdata);
		assertEquals(sellerRoomListResult.getString("code"), "10000","查询房间列表"+sellerRoomListResult);
		JSONArray roomIdList = new JSONArray();//房间列表
		roomIdList.add(sellerRoomListResult.getJSONObject("data").getJSONArray("list").getJSONObject(0).getIntValue("id"));
		
		Reporter.log("[运营中心]－－－－－－");
		JSONObject sysLoginResult = login.sysLogin("admin_helen", "123456li");
		assertEquals(sysLoginResult.getString("code"), "10000","运营中心登录");
		JSONObject sysUserdata = sysLoginResult.getJSONObject("data");
		
		Reporter.log("1）审核房间：审核通过");
		JSONObject roomAuditResult = roomsAdmin.roomAudit(sysUserdata, roomIdList, 2);
		assertEquals(roomAuditResult.getString("code"), "10000","房间审核校验"+roomAuditResult );
		
		Reporter.log("2）房间上架");
		JSONObject roodSellSetResult = roomsAdmin.roomSellSet(sysUserdata, roomIdList, 4);
		assertEquals(roodSellSetResult.getString("code"), "10000","房间上架校验"+roodSellSetResult);
	}

}
