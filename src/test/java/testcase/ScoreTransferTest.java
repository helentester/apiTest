/**
 * @author helen
 * @date 2019年3月21日
 */
package testcase;

import static org.testng.Assert.assertEquals;

import org.testng.Reporter;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONObject;

import data.MemberData;
import user.Login;
import user.MemberTransferScore;
import user.VerificationCode;

/**
 * @Description:豆豆转赠业务测试
 */
public class ScoreTransferTest {
	MemberData memberData = new MemberData();
	Login login = new Login();
	VerificationCode verificationCode = new VerificationCode();
	MemberTransferScore transferScore = new MemberTransferScore();
	
	/*转赠福豆*/
	@Test
	public void test_TransferFScore() {
		String platform = "ios";
		String username = memberData.getAccount_score(1, "80");
		
		Reporter.log("1）会员登录："+username);
		JSONObject loginResult = login.login(username, "123456li", platform);
		assertEquals(loginResult.getString("code"), "10000","登录校验"+loginResult);
		JSONObject userdata = loginResult.getJSONObject("data");
		
		String FScore = memberData.getScore_userId(userdata.getString("userId"), "abled_score");//会员当前可用福豆
		Reporter.log("2）数据库中获取会员当前可用福豆："+FScore);
		
		Reporter.log("3）可转赠福豆校验");
		JSONObject ableTransferAmountResult = transferScore.ableTransferAmount(userdata, 1, platform);
		assertEquals(ableTransferAmountResult.getString("code"), "10000","可转账总额接口执行校验"+ableTransferAmountResult);
		assertEquals(Float.valueOf(ableTransferAmountResult.getJSONObject("data").getString("amount")), Float.valueOf(FScore),"可用转账福豆查询校验");
		
		Reporter.log("4）发送验证码");
		JSONObject sendCodeResult = verificationCode.sendCode(username, "", 5, platform);
		assertEquals(sendCodeResult.getString("code"), "10000","发送验证码校验"+sendCodeResult);
		String code = memberData.getVerificationCode(username, "phone");
		
		String receiver = memberData.get_account("phone");
		Reporter.log("5）数据库获取豆豆接收者："+receiver);
		
		Reporter.log("6）福豆正常转赠校验");
		JSONObject transferResult = transferScore.transfer(userdata, 1, 1.33, "18620277817", code, platform);
		System.out.println(transferResult);
	}

}
