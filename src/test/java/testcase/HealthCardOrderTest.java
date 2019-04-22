/**
 * @author helen
 * @date 2019年3月21日
 */
package testcase;

import static org.testng.Assert.assertEquals;

import java.math.BigDecimal;

import org.testng.Reporter;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONObject;

import agent.AgentFund;
import data.AgentData;
import data.HealthCardData;
import data.MemberData;
import order.HealthCardAdmin;
import order.HealthCardOrder;
import user.HealthCard;
import user.Login;
import user.Medicalcard;
import user.MemberCenter;
import user.MemberDeliveryAddress;
import user.VerificationCode;

/**
 * @Description:康养卡分润
 */
public class HealthCardOrderTest {
	AgentData agentData = new AgentData();
	MemberData memberData = new MemberData();
	HealthCardData healthCardData = new HealthCardData();
	Login login = new Login();
	MemberDeliveryAddress deliveryAddress = new MemberDeliveryAddress();
	HealthCardAdmin healthCardAdmin = new HealthCardAdmin();
	HealthCard healthCard = new HealthCard();
	HealthCardOrder order = new HealthCardOrder();
	AgentFund agentFund = new AgentFund();
	MemberCenter memberCenter = new MemberCenter();
	VerificationCode verificationCode = new VerificationCode();
	Medicalcard medicalcard = new Medicalcard();
	
	@Test
	public void test_scene3() {
		String platform = "android";
		String agentAccount = agentData.getLastHeahthAgent(1);//"18502477268";
		Reporter.log("场景三：购买福卡，并顺序升级到VIP私董会卡");
		try {
			Reporter.log("[运营中心操作]-------");	
			JSONObject sysLoginResult = login.sysLogin("helen", "123456li");
			assertEquals(sysLoginResult.getString("code"), "10000","运营中心登录校验"+sysLoginResult);
			JSONObject sysUserdata  = sysLoginResult.getJSONObject("data");
			
			Reporter.log("1）查询康养卡配置表");
			JSONObject healthCardConfigReslut = healthCardAdmin.healthCardConfigList(sysUserdata);
			assertEquals(healthCardConfigReslut.getString("code"), "10000","康养卡配置查询"+healthCardConfigReslut);
			JSONObject card1Config = healthCardConfigReslut.getJSONObject("data").getJSONArray("list").getJSONObject(0);
			Reporter.log("福卡配置信息："+card1Config);
			
			Reporter.log("[会员操作]--------");
			String username = memberData.get_account("phone");
			Reporter.log("1）会员登录："+username);
			JSONObject loginResult = login.login(username, "123456li", platform);
			assertEquals(loginResult.getString("code"), "10000","登录校验"+loginResult);
			JSONObject userdata = loginResult.getJSONObject("data");
			
			Reporter.log("2）进入康养卡专题页");
			JSONObject healthCardListResult = healthCard.healthCardList(userdata, platform);
			assertEquals(healthCardListResult.getString("code"), "10000","康养卡专题页查询"+healthCardListResult);
			
			String addressId = deliveryAddress.getAddrId(userdata, platform);
			Reporter.log("3）获取会员地址ID:"+addressId);
			
			JSONObject card1 = healthCardListResult.getJSONObject("data").getJSONArray("list").getJSONObject(0);
			Reporter.log("4）购买："+card1.getString("name"));//福卡
			JSONObject generateOrderResult = order.generateHealthCardOrder(userdata, addressId, agentAccount, card1.getIntValue("id"), card1.getString("skuNumber"), 1,card1.getIntValue("supplierId"), platform);
			assertEquals(generateOrderResult.getString("code"), "10000","下订单校验"+generateOrderResult);
			
			Reporter.log("5）上传回执，订单号："+generateOrderResult.getJSONObject("data").getString("orderNo"));
			JSONObject uploadReceiptResult = order.uploadReceipt(userdata, generateOrderResult.getJSONObject("data").getString("orderId"), platform);
			assertEquals(uploadReceiptResult.getString("code"), "10000","上传回执校验"+uploadReceiptResult);
			
			Reporter.log("6）记录会员账户信息");
			JSONObject memberAccountInfoResult1 = memberCenter.getMemberAccountInfo(userdata, platform);
			assertEquals(memberAccountInfoResult1.getString("code"), "10000","会员账户信息查询"+memberAccountInfoResult1);
			JSONObject beanInfo1 = memberAccountInfoResult1.getJSONObject("data");
			Reporter.log("消费福豆＝"+beanInfo1.getString("score"));
			Reporter.log("养老福豆＝"+beanInfo1.getString("retirementScore"));
			Reporter.log("健康福豆="+beanInfo1.getString("hscore"));
			Reporter.log("学习福豆＝"+beanInfo1.getString("registerScore"));
			
			Reporter.log("7）记录会员的体检卡个数");
			JSONObject medicalcardListResult1 = medicalcard.MedicalcardList(userdata, 0, platform);
			assertEquals(medicalcardListResult1.getString("code"), "10000","体检卡列表查询");
			Reporter.log("体检次数＝"+medicalcardListResult1.getJSONObject("data").getString("listCount"));

			Reporter.log("[代理中心操作]-------");
			JSONObject agentLoginResult = login.agentLogin(agentAccount, "123456li");
			assertEquals(agentLoginResult.getString("code"), "10000","代理中心登录校验"+agentLoginResult);
			JSONObject agentUserData = agentLoginResult.getJSONObject("data");
			
			Reporter.log("1）查询代理商的康养卡津贴");
			JSONObject agentFundResult1 = agentFund.agentFundInfo(agentUserData);
			assertEquals(agentFundResult1.getString("code"), "10000","代理商账户信息查询"+agentFundResult1);
			JSONObject agentFund1 = agentFundResult1.getJSONObject("data");
			Reporter.log("总余额＝"+agentFund1.getString("hcBalance"));
			Reporter.log("可用余额＝"+agentFund1.getString("hcAbledBalance"));
			
			Reporter.log("[运营中心操作]-------");		
			Reporter.log("1）财务对账：通过");
			JSONObject financeCheckResult = healthCardAdmin.financeCheck(sysUserdata, 2, generateOrderResult.getJSONObject("data").getIntValue("orderId"), "接口测试，对账通过");
			assertEquals(financeCheckResult.getString("code"), "10000","对账校验"+financeCheckResult);
			
			Thread.sleep(30000);
			String card1No = healthCardData.getValue_orderId(generateOrderResult.getJSONObject("data").getString("orderId"), "card_no");//数据库中获取卡号
			Reporter.log("2）在康养卡列表查询该卡"+card1No);
			JSONObject seachCardListResult = healthCardAdmin.healthCardList(sysUserdata, card1No);
			assertEquals(seachCardListResult.getString("code"), "10000","按卡号查询康养卡");
			int card1Id = seachCardListResult.getJSONObject("data").getJSONArray("list").getJSONObject(0).getInteger("id");

			Reporter.log("[代理中心操作]------");
			Reporter.log("1）查询代理商的康养卡津贴");
			JSONObject agentFundResult2 = agentFund.agentFundInfo(agentUserData);
			assertEquals(agentFundResult2.getString("code"), "10000","代理商账户信息查询"+agentFundResult2);
			JSONObject agentFund2 = agentFundResult2.getJSONObject("data");
			Reporter.log("总余额＝"+agentFund2.getString("hcBalance"));
			Reporter.log("可用余额＝"+agentFund2.getString("hcAbledBalance"));
			
			Reporter.log("2）检查代理商的康养卡津贴是否增加");
			BigDecimal price1 = card1Config.getBigDecimal("healthCardMoney");
			BigDecimal ratio1 = card1Config.getBigDecimal("profitPercentage");
			BigDecimal hcBalance1 = agentFund1.getBigDecimal("hcBalance");
			BigDecimal hcAbledBalance1 = agentFund1.getBigDecimal("hcAbledBalance");
			BigDecimal hcBalance2 = agentFund2.getBigDecimal("hcBalance");
			BigDecimal hcAbledBalance2 = agentFund2.getBigDecimal("hcAbledBalance");
			BigDecimal addMoney1 = price1.multiply(ratio1).setScale(2,BigDecimal.ROUND_HALF_DOWN);
			assertEquals(hcBalance2.subtract(hcBalance1), addMoney1,"总余额校验");
			assertEquals(hcAbledBalance2.subtract(hcAbledBalance1), addMoney1,"可用余额较验");
			
			Reporter.log("[会员操作]------");
			Reporter.log("1）会员激活康养卡");
			JSONObject sendCodeResult = verificationCode.sendCode(username, "", 9, platform);
			assertEquals(sendCodeResult.getString("code"), "10000","验证码发送");
			String messageCode = memberData.getVerificationCode(username, "phone");
			JSONObject healthCardActiveResult = healthCard.healthCardActive(userdata, card1Id,messageCode, username, platform);
			assertEquals(healthCardActiveResult.getString("code"), "10000","激活康养卡校验"+healthCardActiveResult);
			
			Reporter.log("2）查询会员账户信息");
			JSONObject memberAccountInfoResult2 = memberCenter.getMemberAccountInfo(userdata, platform);
			assertEquals(memberAccountInfoResult2.getString("code"), "10000","会员账户信息查询"+memberAccountInfoResult2);
			JSONObject beanInfo2 = memberAccountInfoResult2.getJSONObject("data");
			Reporter.log("消费福豆＝"+beanInfo2.getString("score"));
			Reporter.log("养老福豆＝"+beanInfo2.getString("retirementScore"));
			Reporter.log("健康福豆="+beanInfo2.getString("hscore"));
			Reporter.log("学习福豆＝"+beanInfo2.getString("registerScore"));
			
			Reporter.log("3）查询会员的体检卡个数");
			JSONObject medicalcardListResult2 = medicalcard.MedicalcardList(userdata, 0, platform);
			assertEquals(medicalcardListResult2.getString("code"), "10000","体检卡列表查询");
			Reporter.log("体检次数＝"+medicalcardListResult2.getJSONObject("data").getString("listCount"));
			
			Reporter.log("4）检查会员的4种豆值和体检卡是否增加");
			//激活前会员豆值和体检次数
			BigDecimal score1 = beanInfo1.getBigDecimal("score");
			BigDecimal retirementScore1 = beanInfo1.getBigDecimal("retirementScore");
			BigDecimal hscore1 = beanInfo1.getBigDecimal("hscore");
			BigDecimal registerScore1 = beanInfo1.getBigDecimal("registerScore");
			int medicalcard1 = medicalcardListResult1.getJSONObject("data").getIntValue("listCount");
			//激活后会员的豆值和体检次数
			BigDecimal score2 = beanInfo2.getBigDecimal("score");
			BigDecimal retirementScore2 = beanInfo2.getBigDecimal("retirementScore");
			BigDecimal hscore2 = beanInfo2.getBigDecimal("hscore");
			BigDecimal registerScore2 = beanInfo2.getBigDecimal("registerScore");
			int medicalcard2 = medicalcardListResult2.getJSONObject("data").getIntValue("listCount");
			//会员应当增加的豆值和体检次数
			BigDecimal addblessingBean1 = card1Config.getBigDecimal("blessingBean");
			BigDecimal addenjoyBean1 = card1Config.getBigDecimal("enjoyBean");
			BigDecimal addhbean1 = card1Config.getBigDecimal("hbean");
			BigDecimal addStudyBean1 = card1Config.getBigDecimal("studyBean");
			int addmedicalcard1 = card1Config.getIntValue("examinationsCount");
			assertEquals(score1.subtract(score2), addblessingBean1,"消费福豆校验");
			assertEquals(retirementScore2.subtract(retirementScore1),addenjoyBean1 ,"养老福豆校验");
			assertEquals(hscore2.subtract(hscore1),addhbean1 ,"健康福豆校验");
			assertEquals(registerScore2.subtract(registerScore1),addStudyBean1 ,"学习福豆校验");
			assertEquals(medicalcard2-medicalcard1,addmedicalcard1 ,"体检次数校验");
			
			JSONObject card2Config = healthCardConfigReslut.getJSONObject("data").getJSONArray("list").getJSONObject(1);
			Reporter.log("5）升级到福禄卡:"+card2Config);
			JSONObject sendCodeResult1 = verificationCode.sendCode(username, "member", 8, platform);
			assertEquals(sendCodeResult1.getString("code"), "10000","发送验证码");
			String messageCode1 = memberData.getVerificationCode(username, "phone");
			JSONObject upgradeResult1 = order.healthCardUpgrade(userdata, card2Config.getIntValue("goodsId"), card1Id, messageCode1, username, platform);
			assertEquals(upgradeResult1.getString("code"), "10000","升级提交校验");
			
			Reporter.log("6）上传升级回执");
			JSONObject uploadUpOrderReceiptResult1 = order.uploadUpOrderReceipt(userdata, upgradeResult1.getJSONObject("data").getIntValue("id"), platform);
			assertEquals(uploadUpOrderReceiptResult1.getString("code"), "10000","上传升级回执"+uploadUpOrderReceiptResult1);
			
			Reporter.log("[运营中心操作]------");
			Reporter.log("1）升级对账：通过");
			JSONObject upFinanceCheckResult = healthCardAdmin.upFinanceCheck(sysUserdata, 2, upgradeResult1.getJSONObject("data").getIntValue("id"), "接口升级对账");
			assertEquals(upFinanceCheckResult.getString("code"), "10000","升级对账校验"+upFinanceCheckResult);
			
			Thread.sleep(30000);
			String card2No = healthCardData.getValeu_upOrderId(upgradeResult1.getJSONObject("data").getString("id"), "new_card_no");//数据库中获取卡号
			Reporter.log("2）在康养卡列表查询该卡"+card2No);
			JSONObject seachCardListResult2 = healthCardAdmin.healthCardList(sysUserdata, card2No);
			assertEquals(seachCardListResult2.getString("code"), "10000","按卡号查询康养卡"+seachCardListResult2);
			int card2Id = seachCardListResult2.getJSONObject("data").getJSONArray("list").getJSONObject(0).getInteger("id");
			
			Reporter.log("[会员操作]------");
			Reporter.log("1）查询会员账户信息");
			JSONObject memberAccountInfoResult3 = memberCenter.getMemberAccountInfo(userdata, platform);
			assertEquals(memberAccountInfoResult3.getString("code"), "10000","会员账户信息查询"+memberAccountInfoResult3);
			JSONObject beanInfo3 = memberAccountInfoResult3.getJSONObject("data");
			Reporter.log("消费福豆＝"+beanInfo3.getString("score"));
			Reporter.log("养老福豆＝"+beanInfo3.getString("retirementScore"));
			Reporter.log("健康福豆="+beanInfo3.getString("hscore"));
			Reporter.log("学习福豆＝"+beanInfo3.getString("registerScore"));
			
			Reporter.log("2）查询会员的体检卡个数");
			JSONObject medicalcardListResult3 = medicalcard.MedicalcardList(userdata, 0, platform);
			assertEquals(medicalcardListResult3.getString("code"), "10000","体检卡列表查询");
			Reporter.log("体检次数＝"+medicalcardListResult3.getJSONObject("data").getString("listCount"));
			
			Reporter.log("3）检查会员的4种豆值和体检卡是否增加");
			BigDecimal score3 = beanInfo3.getBigDecimal("score");
			BigDecimal retirementScore3 = beanInfo3.getBigDecimal("retirementScore");
			BigDecimal hscore3 = beanInfo3.getBigDecimal("hscore");
			BigDecimal registerScore3 = beanInfo3.getBigDecimal("registerScore");
			int medicalcard3 = medicalcardListResult3.getJSONObject("data").getIntValue("listCount");
			//会员应当增加的豆值和体检次数
			BigDecimal addblessingBean2 = card2Config.getBigDecimal("blessingBean").subtract(card1Config.getBigDecimal("blessingBean"));
			BigDecimal addenjoyBean2 = card2Config.getBigDecimal("enjoyBean").subtract(card1Config.getBigDecimal("enjoyBean"));
			BigDecimal addhbean2 = card2Config.getBigDecimal("hbean").subtract(card1Config.getBigDecimal("hbean"));
			BigDecimal addStudyBean2 = card2Config.getBigDecimal("studyBean").subtract(card1Config.getBigDecimal("studyBean"));
			int addmedicalcard2 = card2Config.getIntValue("examinationsCount")-card1Config.getIntValue("examinationsCount");
			assertEquals(score3.subtract(score2), addblessingBean2,"消费福豆校验");
			assertEquals(retirementScore3.subtract(retirementScore2), addenjoyBean2,"养老福豆校验");
			assertEquals(hscore3.subtract(hscore2), addhbean2,"健康福豆校验");
			assertEquals(registerScore3.subtract(registerScore2), addStudyBean2,"学习福豆校验");
			assertEquals(medicalcard3-medicalcard2, addmedicalcard2,"体检次数校验");
			
			Reporter.log("[代理中心操作]------");
			Reporter.log("1）查询代理商的康养卡津贴");
			JSONObject agentFundResult3 = agentFund.agentFundInfo(agentUserData);
			assertEquals(agentFundResult3.getString("code"), "10000","代理商账户信息查询"+agentFundResult3);
			JSONObject agentFund3 = agentFundResult3.getJSONObject("data");
			Reporter.log("总余额＝"+agentFund3.getString("hcBalance"));
			Reporter.log("可用余额＝"+agentFund3.getString("hcAbledBalance"));
			
			Reporter.log("2）检查代理商的康养卡津贴是否增加");
			BigDecimal price2 = card2Config.getBigDecimal("healthCardMoney");
			BigDecimal ratio2 = card2Config.getBigDecimal("profitPercentage");
			BigDecimal hcBalance3 = agentFund3.getBigDecimal("hcBalance");
			BigDecimal hcAbledBalance3 = agentFund3.getBigDecimal("hcAbledBalance");
			BigDecimal addMoney2 = price2.multiply(ratio2).subtract(addMoney1).setScale(2,BigDecimal.ROUND_HALF_DOWN);
			assertEquals(hcBalance3.subtract(hcBalance2), addMoney2,"总余额校验");
			assertEquals(hcAbledBalance3.subtract(hcAbledBalance2), addMoney2,"可用余额较验");
			
			Reporter.log("[会员操作]-------");
			JSONObject card3Config = healthCardConfigReslut.getJSONObject("data").getJSONArray("list").getJSONObject(2);
			Reporter.log("1）升级到福禄寿卡:"+card3Config);
			JSONObject sendCodeResult2 = verificationCode.sendCode(username, "member", 8, platform);
			assertEquals(sendCodeResult2.getString("code"), "10000","发送验证码");
			String messageCode2 = memberData.getVerificationCode(username, "phone");
			JSONObject upgradeResult2 = order.healthCardUpgrade(userdata, card3Config.getIntValue("goodsId"), card2Id, messageCode2, username, platform);
			assertEquals(upgradeResult2.getString("code"), "10000","升级提交校验");
			
			Reporter.log("2）上传升级回执");
			JSONObject uploadUpOrderReceiptResult2 = order.uploadUpOrderReceipt(userdata, upgradeResult2.getJSONObject("data").getIntValue("id"), platform);
			assertEquals(uploadUpOrderReceiptResult2.getString("code"), "10000","上传升级回执"+uploadUpOrderReceiptResult1);
			
			Reporter.log("[运营中心操作]------");
			Reporter.log("1）升级对账：通过");
			JSONObject upFinanceCheckResult2 = healthCardAdmin.upFinanceCheck(sysUserdata, 2, upgradeResult2.getJSONObject("data").getIntValue("id"), "接口升级对账");
			assertEquals(upFinanceCheckResult2.getString("code"), "10000","升级对账校验"+upFinanceCheckResult2);
			
			Thread.sleep(30000);
			String card3No = healthCardData.getValeu_upOrderId(upgradeResult2.getJSONObject("data").getString("id"), "new_card_no");//数据库中获取卡号
			Reporter.log("2）在康养卡列表查询该卡"+card3No);
			JSONObject seachCardListResult3 = healthCardAdmin.healthCardList(sysUserdata, card3No);
			assertEquals(seachCardListResult3.getString("code"), "10000","按卡号查询康养卡"+seachCardListResult3);
			int card3Id = seachCardListResult3.getJSONObject("data").getJSONArray("list").getJSONObject(0).getInteger("id");
			
			Reporter.log("[会员操作]------");
			Reporter.log("1）查询会员账户信息");
			JSONObject memberAccountInfoResult4 = memberCenter.getMemberAccountInfo(userdata, platform);
			assertEquals(memberAccountInfoResult4.getString("code"), "10000","会员账户信息查询"+memberAccountInfoResult4);
			JSONObject beanInfo4 = memberAccountInfoResult4.getJSONObject("data");
			Reporter.log("消费福豆＝"+beanInfo4.getString("score"));
			Reporter.log("养老福豆＝"+beanInfo4.getString("retirementScore"));
			Reporter.log("健康福豆="+beanInfo4.getString("hscore"));
			Reporter.log("学习福豆＝"+beanInfo4.getString("registerScore"));
			
			Reporter.log("2）查询会员的体检卡个数");
			JSONObject medicalcardListResult4 = medicalcard.MedicalcardList(userdata, 0, platform);
			assertEquals(medicalcardListResult4.getString("code"), "10000","体检卡列表查询");
			Reporter.log("体检次数＝"+medicalcardListResult4.getJSONObject("data").getString("listCount"));
			
			Reporter.log("3）检查会员的4种豆值和体检卡是否增加");
			BigDecimal score4 = beanInfo4.getBigDecimal("score");
			BigDecimal retirementScore4 = beanInfo4.getBigDecimal("retirementScore");
			BigDecimal hscore4 = beanInfo4.getBigDecimal("hscore");
			BigDecimal registerScore4 = beanInfo4.getBigDecimal("registerScore");
			int medicalcard4 = medicalcardListResult4.getJSONObject("data").getIntValue("listCount");
			//会员应当增加的豆值和体检次数
			BigDecimal addblessingBean3 = card3Config.getBigDecimal("blessingBean").subtract(card2Config.getBigDecimal("blessingBean"));
			BigDecimal addenjoyBean3 = card3Config.getBigDecimal("enjoyBean").subtract(card2Config.getBigDecimal("enjoyBean"));
			BigDecimal addhbean3= card3Config.getBigDecimal("hbean").subtract(card2Config.getBigDecimal("hbean"));
			BigDecimal addStudyBean3 = card3Config.getBigDecimal("studyBean").subtract(card2Config.getBigDecimal("studyBean"));
			int addmedicalcard3= card3Config.getIntValue("examinationsCount")-card2Config.getIntValue("examinationsCount");
			assertEquals(score4.subtract(score3), addblessingBean3,"消费福豆校验");
			assertEquals(retirementScore4.subtract(retirementScore3), addenjoyBean3,"养老福豆校验");
			assertEquals(hscore4.subtract(hscore3), addhbean3,"健康福豆校验");
			assertEquals(registerScore4.subtract(registerScore3), addStudyBean3,"学习福豆校验");
			assertEquals(medicalcard4-medicalcard3, addmedicalcard3,"体检次数校验");
			
			Reporter.log("[代理中心操作]------");
			Reporter.log("1）查询代理商的康养卡津贴");
			JSONObject agentFundResult4 = agentFund.agentFundInfo(agentUserData);
			assertEquals(agentFundResult4.getString("code"), "10000","代理商账户信息查询"+agentFundResult4);
			JSONObject agentFund4 = agentFundResult4.getJSONObject("data");
			Reporter.log("总余额＝"+agentFund4.getString("hcBalance"));
			Reporter.log("可用余额＝"+agentFund4.getString("hcAbledBalance"));
			
			Reporter.log("2）检查代理商的康养卡津贴是否增加");
			BigDecimal price3 = card3Config.getBigDecimal("healthCardMoney");
			BigDecimal ratio3 = card3Config.getBigDecimal("profitPercentage");
			BigDecimal hcBalance4 = agentFund4.getBigDecimal("hcBalance");
			BigDecimal hcAbledBalance4 = agentFund4.getBigDecimal("hcAbledBalance");
			BigDecimal addMoney3 = price3.multiply(ratio3).subtract(addMoney1).subtract(addMoney2).setScale(2,BigDecimal.ROUND_HALF_DOWN);
			assertEquals(hcBalance4.subtract(hcBalance3), addMoney3,"总余额校验");
			assertEquals(hcAbledBalance4.subtract(hcAbledBalance3), addMoney3,"可用余额较验");
			
			Reporter.log("[会员操作]-------");
			JSONObject card4Config = healthCardConfigReslut.getJSONObject("data").getJSONArray("list").getJSONObject(3);
			Reporter.log("1）升级到福禄寿喜卡:"+card4Config);
			JSONObject sendCodeResult3 = verificationCode.sendCode(username, "member", 8, platform);
			assertEquals(sendCodeResult3.getString("code"), "10000","发送验证码");
			String messageCode3 = memberData.getVerificationCode(username, "phone");
			JSONObject upgradeResult3 = order.healthCardUpgrade(userdata, card4Config.getIntValue("goodsId"), card3Id, messageCode3, username, platform);
			assertEquals(upgradeResult3.getString("code"), "10000","升级提交校验");
			
			Reporter.log("2）上传升级回执");
			JSONObject uploadUpOrderReceiptResult3 = order.uploadUpOrderReceipt(userdata, upgradeResult3.getJSONObject("data").getIntValue("id"), platform);
			assertEquals(uploadUpOrderReceiptResult2.getString("code"), "10000","上传升级回执"+uploadUpOrderReceiptResult3);
			
			Reporter.log("[运营中心操作]------");
			Reporter.log("1）升级对账：通过");
			JSONObject upFinanceCheckResult3 = healthCardAdmin.upFinanceCheck(sysUserdata, 2, upgradeResult3.getJSONObject("data").getIntValue("id"), "接口升级对账");
			assertEquals(upFinanceCheckResult2.getString("code"), "10000","升级对账校验"+upFinanceCheckResult3);
			
			Thread.sleep(30000);
			String card4No = healthCardData.getValeu_upOrderId(upgradeResult3.getJSONObject("data").getString("id"), "new_card_no");//数据库中获取卡号
			Reporter.log("2）在康养卡列表查询该卡"+card4No);
			JSONObject seachCardListResult4 = healthCardAdmin.healthCardList(sysUserdata, card4No);
			assertEquals(seachCardListResult4.getString("code"), "10000","按卡号查询康养卡"+seachCardListResult4);
			int card4Id = seachCardListResult4.getJSONObject("data").getJSONArray("list").getJSONObject(0).getInteger("id");
			
			Reporter.log("[会员操作]------");
			Reporter.log("1）查询会员账户信息");
			JSONObject memberAccountInfoResult5 = memberCenter.getMemberAccountInfo(userdata, platform);
			assertEquals(memberAccountInfoResult5.getString("code"), "10000","会员账户信息查询"+memberAccountInfoResult5);
			JSONObject beanInfo5 = memberAccountInfoResult5.getJSONObject("data");
			Reporter.log("消费福豆＝"+beanInfo5.getString("score"));
			Reporter.log("养老福豆＝"+beanInfo5.getString("retirementScore"));
			Reporter.log("健康福豆="+beanInfo5.getString("hscore"));
			Reporter.log("学习福豆＝"+beanInfo5.getString("registerScore"));
			
			Reporter.log("2）查询会员的体检卡个数");
			JSONObject medicalcardListResult5 = medicalcard.MedicalcardList(userdata, 0, platform);
			assertEquals(medicalcardListResult4.getString("code"), "10000","体检卡列表查询");
			Reporter.log("体检次数＝"+medicalcardListResult5.getJSONObject("data").getString("listCount"));
			
			Reporter.log("3）检查会员的4种豆值和体检卡是否增加");
			BigDecimal score5 = beanInfo5.getBigDecimal("score");
			BigDecimal retirementScore5 = beanInfo5.getBigDecimal("retirementScore");
			BigDecimal hscore5 = beanInfo5.getBigDecimal("hscore");
			BigDecimal registerScore5 = beanInfo5.getBigDecimal("registerScore");
			int medicalcard5 = medicalcardListResult5.getJSONObject("data").getIntValue("listCount");
			//会员应当增加的豆值和体检次数
			BigDecimal addblessingBean4 = card4Config.getBigDecimal("blessingBean").subtract(card3Config.getBigDecimal("blessingBean"));
			BigDecimal addenjoyBean4 = card4Config.getBigDecimal("enjoyBean").subtract(card3Config.getBigDecimal("enjoyBean"));
			BigDecimal addhbean4= card4Config.getBigDecimal("hbean").subtract(card3Config.getBigDecimal("hbean"));
			BigDecimal addStudyBean4 = card4Config.getBigDecimal("studyBean").subtract(card3Config.getBigDecimal("studyBean"));
			int addmedicalcard4= card4Config.getIntValue("examinationsCount")-card3Config.getIntValue("examinationsCount");
			assertEquals(score5.subtract(score4), addblessingBean4,"消费福豆校验");
			assertEquals(retirementScore5.subtract(retirementScore4), addenjoyBean4,"养老福豆校验");
			assertEquals(hscore5.subtract(hscore4), addhbean4,"健康福豆校验");
			assertEquals(registerScore5.subtract(registerScore4), addStudyBean4,"学习福豆校验");
			assertEquals(medicalcard5-medicalcard4, addmedicalcard4,"体检次数校验");
			
			Reporter.log("[代理中心操作]------");
			Reporter.log("1）查询代理商的康养卡津贴");
			JSONObject agentFundResult5 = agentFund.agentFundInfo(agentUserData);
			assertEquals(agentFundResult5.getString("code"), "10000","代理商账户信息查询"+agentFundResult5);
			JSONObject agentFund5 = agentFundResult5.getJSONObject("data");
			Reporter.log("总余额＝"+agentFund5.getString("hcBalance"));
			Reporter.log("可用余额＝"+agentFund5.getString("hcAbledBalance"));
			
			Reporter.log("2）检查代理商的康养卡津贴是否增加");
			BigDecimal price4 = card4Config.getBigDecimal("healthCardMoney");
			BigDecimal ratio4 = card4Config.getBigDecimal("profitPercentage");
			BigDecimal hcBalance5 = agentFund5.getBigDecimal("hcBalance");
			BigDecimal hcAbledBalance5 = agentFund5.getBigDecimal("hcAbledBalance");
			BigDecimal addMoney4 = price4.multiply(ratio4).subtract(addMoney1).subtract(addMoney2).subtract(addMoney3).setScale(2,BigDecimal.ROUND_HALF_DOWN);
			assertEquals(hcBalance5.subtract(hcBalance4), addMoney4,"总余额校验");
			assertEquals(hcAbledBalance5.subtract(hcAbledBalance4), addMoney4,"可用余额较验");
			
			Reporter.log("[会员操作]-------");
			JSONObject card5Config = healthCardConfigReslut.getJSONObject("data").getJSONArray("list").getJSONObject(4);
			Reporter.log("1）升级到福禄寿喜卡:"+card5Config);
			JSONObject sendCodeResult4 = verificationCode.sendCode(username, "member", 8, platform);
			assertEquals(sendCodeResult4.getString("code"), "10000","发送验证码");
			String messageCode4 = memberData.getVerificationCode(username, "phone");
			JSONObject upgradeResult4 = order.healthCardUpgrade(userdata, card5Config.getIntValue("goodsId"), card4Id, messageCode4, username, platform);
			assertEquals(upgradeResult4.getString("code"), "10000","升级提交校验");
			
			Reporter.log("2）上传升级回执");
			JSONObject uploadUpOrderReceiptResult4 = order.uploadUpOrderReceipt(userdata, upgradeResult4.getJSONObject("data").getIntValue("id"), platform);
			assertEquals(uploadUpOrderReceiptResult4.getString("code"), "10000","上传升级回执"+uploadUpOrderReceiptResult4);
			
			Reporter.log("[运营中心操作]------");
			Reporter.log("1）升级对账：通过");
			JSONObject upFinanceCheckResult4 = healthCardAdmin.upFinanceCheck(sysUserdata, 2, upgradeResult4.getJSONObject("data").getIntValue("id"), "接口升级对账");
			assertEquals(upFinanceCheckResult4.getString("code"), "10000","升级对账校验"+upFinanceCheckResult4);
			
			Thread.sleep(30000);
			String card5No = healthCardData.getValeu_upOrderId(upgradeResult4.getJSONObject("data").getString("id"), "new_card_no");//数据库中获取卡号
			Reporter.log("2）在康养卡列表查询该卡"+card5No);
			JSONObject seachCardListResult5 = healthCardAdmin.healthCardList(sysUserdata, card5No);
			assertEquals(seachCardListResult5.getString("code"), "10000","按卡号查询康养卡"+seachCardListResult5);
			int card5Id = seachCardListResult5.getJSONObject("data").getJSONArray("list").getJSONObject(0).getInteger("id");
			
			Reporter.log("[会员操作]------");
			Reporter.log("1）查询会员账户信息");
			JSONObject memberAccountInfoResult6 = memberCenter.getMemberAccountInfo(userdata, platform);
			assertEquals(memberAccountInfoResult6.getString("code"), "10000","会员账户信息查询"+memberAccountInfoResult6);
			JSONObject beanInfo6 = memberAccountInfoResult6.getJSONObject("data");
			Reporter.log("消费福豆＝"+beanInfo6.getString("score"));
			Reporter.log("养老福豆＝"+beanInfo6.getString("retirementScore"));
			Reporter.log("健康福豆="+beanInfo6.getString("hscore"));
			Reporter.log("学习福豆＝"+beanInfo6.getString("registerScore"));
			
			Reporter.log("2）查询会员的体检卡个数");
			JSONObject medicalcardListResult6 = medicalcard.MedicalcardList(userdata, 0, platform);
			assertEquals(medicalcardListResult6.getString("code"), "10000","体检卡列表查询");
			Reporter.log("体检次数＝"+medicalcardListResult6.getJSONObject("data").getString("listCount"));
			
			Reporter.log("3）检查会员的4种豆值和体检卡是否增加");
			BigDecimal score6 = beanInfo6.getBigDecimal("score");
			BigDecimal retirementScore6 = beanInfo6.getBigDecimal("retirementScore");
			BigDecimal hscore6 = beanInfo6.getBigDecimal("hscore");
			BigDecimal registerScore6 = beanInfo6.getBigDecimal("registerScore");
			int medicalcard6 = medicalcardListResult6.getJSONObject("data").getIntValue("listCount");
			//会员应当增加的豆值和体检次数
			BigDecimal addblessingBean5 = card5Config.getBigDecimal("blessingBean").subtract(card4Config.getBigDecimal("blessingBean"));
			BigDecimal addenjoyBean5 = card5Config.getBigDecimal("enjoyBean").subtract(card4Config.getBigDecimal("enjoyBean"));
			BigDecimal addhbean5= card5Config.getBigDecimal("hbean").subtract(card4Config.getBigDecimal("hbean"));
			BigDecimal addStudyBean5 = card5Config.getBigDecimal("studyBean").subtract(card4Config.getBigDecimal("studyBean"));
			int addmedicalcard5= card5Config.getIntValue("examinationsCount")-card4Config.getIntValue("examinationsCount");
			assertEquals(score6.subtract(score5), addblessingBean5,"消费福豆校验");
			assertEquals(retirementScore6.subtract(retirementScore5), addenjoyBean5,"养老福豆校验");
			assertEquals(hscore6.subtract(hscore5), addhbean5,"健康福豆校验");
			assertEquals(registerScore6.subtract(registerScore5), addStudyBean5,"学习福豆校验");
			assertEquals(medicalcard6-medicalcard5, addmedicalcard5,"体检次数校验");
			
			Reporter.log("[代理中心操作]------");
			Reporter.log("1）查询代理商的康养卡津贴");
			JSONObject agentFundResult6 = agentFund.agentFundInfo(agentUserData);
			assertEquals(agentFundResult6.getString("code"), "10000","代理商账户信息查询"+agentFundResult6);
			JSONObject agentFund6 = agentFundResult6.getJSONObject("data");
			Reporter.log("总余额＝"+agentFund6.getString("hcBalance"));
			Reporter.log("可用余额＝"+agentFund6.getString("hcAbledBalance"));
			
			Reporter.log("2）检查代理商的康养卡津贴是否增加");
			BigDecimal price5 = card5Config.getBigDecimal("healthCardMoney");
			BigDecimal ratio5 = card5Config.getBigDecimal("profitPercentage");
			BigDecimal hcBalance6 = agentFund6.getBigDecimal("hcBalance");
			BigDecimal hcAbledBalance6= agentFund6.getBigDecimal("hcAbledBalance");
			BigDecimal addMoney5 = price5.multiply(ratio5).subtract(addMoney1).subtract(addMoney2).subtract(addMoney3).subtract(addMoney4).setScale(2,BigDecimal.ROUND_HALF_DOWN);
			assertEquals(hcBalance6.subtract(hcBalance5), addMoney5,"总余额校验");
			assertEquals(hcAbledBalance6.subtract(hcAbledBalance5), addMoney5,"可用余额较验");
			
			Reporter.log("[会员操作]-------");
			JSONObject card6Config = healthCardConfigReslut.getJSONObject("data").getJSONArray("list").getJSONObject(5);
			Reporter.log("1）升级到VIP私董会卡:"+card6Config);
			JSONObject sendCodeResult5 = verificationCode.sendCode(username, "member", 8, platform);
			assertEquals(sendCodeResult5.getString("code"), "10000","发送验证码");
			String messageCode5 = memberData.getVerificationCode(username, "phone");
			JSONObject upgradeResult5 = order.healthCardUpgrade(userdata, card6Config.getIntValue("goodsId"), card5Id, messageCode5, username, platform);
			assertEquals(upgradeResult5.getString("code"), "10000","升级提交校验");
			
			Reporter.log("2）上传升级回执");
			JSONObject uploadUpOrderReceiptResult5= order.uploadUpOrderReceipt(userdata, upgradeResult5.getJSONObject("data").getIntValue("id"), platform);
			assertEquals(uploadUpOrderReceiptResult5.getString("code"), "10000","上传升级回执"+uploadUpOrderReceiptResult5);
			
			Reporter.log("[运营中心操作]------");
			Reporter.log("1）升级对账：通过");
			JSONObject upFinanceCheckResult5 = healthCardAdmin.upFinanceCheck(sysUserdata, 2, upgradeResult5.getJSONObject("data").getIntValue("id"), "接口升级对账");
			assertEquals(upFinanceCheckResult5.getString("code"), "10000","升级对账校验"+upFinanceCheckResult5);
			
			Thread.sleep(30000);
			String card6No = healthCardData.getValeu_upOrderId(upgradeResult5.getJSONObject("data").getString("id"), "new_card_no");//数据库中获取卡号
			Reporter.log("2）在康养卡列表查询该卡"+card6No);
			JSONObject seachCardListResult6 = healthCardAdmin.healthCardList(sysUserdata, card6No);
			assertEquals(seachCardListResult6.getString("code"), "10000","按卡号查询康养卡"+seachCardListResult6);
			int card6Id = seachCardListResult6.getJSONObject("data").getJSONArray("list").getJSONObject(0).getInteger("id");
			
			Reporter.log("[会员操作]------");
			Reporter.log("1）查询会员账户信息");
			JSONObject memberAccountInfoResult7 = memberCenter.getMemberAccountInfo(userdata, platform);
			assertEquals(memberAccountInfoResult7.getString("code"), "10000","会员账户信息查询"+memberAccountInfoResult7);
			JSONObject beanInfo7 = memberAccountInfoResult7.getJSONObject("data");
			Reporter.log("消费福豆＝"+beanInfo7.getString("score"));
			Reporter.log("养老福豆＝"+beanInfo7.getString("retirementScore"));
			Reporter.log("健康福豆="+beanInfo7.getString("hscore"));
			Reporter.log("学习福豆＝"+beanInfo7.getString("registerScore"));
			
			Reporter.log("2）查询会员的体检卡个数");
			JSONObject medicalcardListResult7 = medicalcard.MedicalcardList(userdata, 0, platform);
			assertEquals(medicalcardListResult7.getString("code"), "10000","体检卡列表查询");
			Reporter.log("体检次数＝"+medicalcardListResult7.getJSONObject("data").getString("listCount"));
			
			Reporter.log("3）检查会员的4种豆值和体检卡是否增加");
			BigDecimal score7 = beanInfo7.getBigDecimal("score");
			BigDecimal retirementScore7 = beanInfo7.getBigDecimal("retirementScore");
			BigDecimal hscore7 = beanInfo7.getBigDecimal("hscore");
			BigDecimal registerScore7 = beanInfo7.getBigDecimal("registerScore");
			int medicalcard7 = medicalcardListResult7.getJSONObject("data").getIntValue("listCount");
			//会员应当增加的豆值和体检次数
			BigDecimal addblessingBean6 = card6Config.getBigDecimal("blessingBean").subtract(card5Config.getBigDecimal("blessingBean"));
			BigDecimal addenjoyBean6 = card6Config.getBigDecimal("enjoyBean").subtract(card5Config.getBigDecimal("enjoyBean"));
			BigDecimal addhbean6= card6Config.getBigDecimal("hbean").subtract(card5Config.getBigDecimal("hbean"));
			BigDecimal addStudyBean6 = card6Config.getBigDecimal("studyBean").subtract(card5Config.getBigDecimal("studyBean"));
			int addmedicalcard6= card6Config.getIntValue("examinationsCount")-card5Config.getIntValue("examinationsCount");
			assertEquals(score7.subtract(score6), addblessingBean6,"消费福豆校验");
			assertEquals(retirementScore7.subtract(retirementScore6), addenjoyBean6,"养老福豆校验");
			assertEquals(hscore7.subtract(hscore6), addhbean6,"健康福豆校验");
			assertEquals(registerScore7.subtract(registerScore6), addStudyBean6,"学习福豆校验");
			assertEquals(medicalcard7-medicalcard6, addmedicalcard6,"体检次数校验");
			
			Reporter.log("[代理中心操作]------");
			Reporter.log("1）查询代理商的康养卡津贴");
			JSONObject agentFundResult7 = agentFund.agentFundInfo(agentUserData);
			assertEquals(agentFundResult7.getString("code"), "10000","代理商账户信息查询"+agentFundResult7);
			JSONObject agentFund7 = agentFundResult7.getJSONObject("data");
			Reporter.log("总余额＝"+agentFund7.getString("hcBalance"));
			Reporter.log("可用余额＝"+agentFund7.getString("hcAbledBalance"));
			
			Reporter.log("2）检查代理商的康养卡津贴是否增加");
			BigDecimal price6 = card6Config.getBigDecimal("healthCardMoney");
			BigDecimal ratio6 = card6Config.getBigDecimal("profitPercentage");
			BigDecimal hcBalance7 = agentFund7.getBigDecimal("hcBalance");
			BigDecimal hcAbledBalance7= agentFund7.getBigDecimal("hcAbledBalance");
			BigDecimal addMoney6 = price6.multiply(ratio6).subtract(addMoney1).subtract(addMoney2).subtract(addMoney3).subtract(addMoney4).subtract(addMoney5).setScale(2,BigDecimal.ROUND_HALF_DOWN);
			assertEquals(hcBalance7.subtract(hcBalance6), addMoney6,"总余额校验");
			assertEquals(hcAbledBalance7.subtract(hcAbledBalance6), addMoney6,"可用余额较验");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test_scene2() {
		String platform = "h5";
		String agentAccount = agentData.getLastHeahthAgent(1);//"18502477268";
		Reporter.log("场景二：跨卡购买－顺序升级－激活－顺序升级");
		
		try {
			Reporter.log("[运营中心操作]-------");	
			JSONObject sysLoginResult = login.sysLogin("helen", "123456li");
			assertEquals(sysLoginResult.getString("code"), "10000","运营中心登录校验"+sysLoginResult);
			JSONObject sysUserdata  = sysLoginResult.getJSONObject("data");
			
			Reporter.log("1）查询康养卡配置表");
			JSONObject healthCardConfigReslut = healthCardAdmin.healthCardConfigList(sysUserdata);
			assertEquals(healthCardConfigReslut.getString("code"), "10000","康养卡配置查询"+healthCardConfigReslut);
			JSONObject card4Config = healthCardConfigReslut.getJSONObject("data").getJSONArray("list").getJSONObject(3);
			Reporter.log("福禄寿喜卡配置信息："+card4Config);
			
			Reporter.log("[会员操作]--------");
			String username = memberData.get_account("phone");
			Reporter.log("1）会员登录："+username);
			JSONObject loginResult = login.login(username, "123456li", platform);
			assertEquals(loginResult.getString("code"), "10000","登录校验"+loginResult);
			JSONObject userdata = loginResult.getJSONObject("data");
			
			Reporter.log("2）进入康养卡专题页");
			JSONObject healthCardListResult = healthCard.healthCardList(userdata, platform);
			assertEquals(healthCardListResult.getString("code"), "10000","康养卡专题页查询"+healthCardListResult);
			
			String addressId = deliveryAddress.getAddrId(userdata, platform);
			Reporter.log("3）获取会员地址ID:"+addressId);
			
			JSONObject card1 = healthCardListResult.getJSONObject("data").getJSONArray("list").getJSONObject(3);
			Reporter.log("4）购买："+card1.getString("name"));//福卡
			JSONObject generateOrderResult = order.generateHealthCardOrder(userdata, addressId, agentAccount, card1.getIntValue("id"), card1.getString("skuNumber"), 1,card1.getIntValue("supplierId"), platform);
			assertEquals(generateOrderResult.getString("code"), "10000","下订单校验"+generateOrderResult);
			
			Reporter.log("5）上传回执，订单号："+generateOrderResult.getJSONObject("data").getString("orderNo"));
			JSONObject uploadReceiptResult = order.uploadReceipt(userdata, generateOrderResult.getJSONObject("data").getString("orderId"), platform);
			assertEquals(uploadReceiptResult.getString("code"), "10000","上传回执校验"+uploadReceiptResult);
			
			Reporter.log("6）记录会员账户信息");
			JSONObject memberAccountInfoResult1 = memberCenter.getMemberAccountInfo(userdata, platform);
			assertEquals(memberAccountInfoResult1.getString("code"), "10000","会员账户信息查询"+memberAccountInfoResult1);
			JSONObject beanInfo1 = memberAccountInfoResult1.getJSONObject("data");
			Reporter.log("消费福豆＝"+beanInfo1.getString("score"));
			Reporter.log("养老福豆＝"+beanInfo1.getString("retirementScore"));
			Reporter.log("健康福豆="+beanInfo1.getString("hscore"));
			Reporter.log("学习福豆＝"+beanInfo1.getString("registerScore"));
			
			Reporter.log("7）记录会员的体检卡个数");
			JSONObject medicalcardListResult1 = medicalcard.MedicalcardList(userdata, 0, platform);
			assertEquals(medicalcardListResult1.getString("code"), "10000","体检卡列表查询");
			Reporter.log("体检次数＝"+medicalcardListResult1.getJSONObject("data").getString("listCount"));

			Reporter.log("[代理中心操作]-------");
			JSONObject agentLoginResult = login.agentLogin(agentAccount, "123456li");
			assertEquals(agentLoginResult.getString("code"), "10000","代理中心登录校验"+agentLoginResult);
			JSONObject agentUserData = agentLoginResult.getJSONObject("data");
			
			Reporter.log("1）查询代理商的康养卡津贴");
			JSONObject agentFundResult1 = agentFund.agentFundInfo(agentUserData);
			assertEquals(agentFundResult1.getString("code"), "10000","代理商账户信息查询"+agentFundResult1);
			JSONObject agentFund1 = agentFundResult1.getJSONObject("data");
			Reporter.log("总余额＝"+agentFund1.getString("hcBalance"));
			Reporter.log("可用余额＝"+agentFund1.getString("hcAbledBalance"));
			
			Reporter.log("[运营中心操作]-------");		
			Reporter.log("1）财务对账：通过");
			JSONObject financeCheckResult = healthCardAdmin.financeCheck(sysUserdata, 2, generateOrderResult.getJSONObject("data").getIntValue("orderId"), "接口测试，对账通过");
			assertEquals(financeCheckResult.getString("code"), "10000","对账校验"+financeCheckResult);
			
			Thread.sleep(30000);
			String card1No = healthCardData.getValue_orderId(generateOrderResult.getJSONObject("data").getString("orderId"), "card_no");//数据库中获取卡号
			Reporter.log("2）在康养卡列表查询该卡"+card1No);
			JSONObject seachCardListResult = healthCardAdmin.healthCardList(sysUserdata, card1No);
			assertEquals(seachCardListResult.getString("code"), "10000","按卡号查询康养卡");
			int card1Id = seachCardListResult.getJSONObject("data").getJSONArray("list").getJSONObject(0).getInteger("id");

			Reporter.log("[代理中心操作]------");
			Reporter.log("1）查询代理商的康养卡津贴");
			JSONObject agentFundResult2 = agentFund.agentFundInfo(agentUserData);
			assertEquals(agentFundResult2.getString("code"), "10000","代理商账户信息查询"+agentFundResult2);
			JSONObject agentFund2 = agentFundResult2.getJSONObject("data");
			Reporter.log("总余额＝"+agentFund2.getString("hcBalance"));
			Reporter.log("可用余额＝"+agentFund2.getString("hcAbledBalance"));
			
			Reporter.log("2）检查代理商的康养卡津贴是否增加");
			BigDecimal price4 = card4Config.getBigDecimal("healthCardMoney");
			BigDecimal ratio4 = card4Config.getBigDecimal("profitPercentage");
			BigDecimal hcBalance1 = agentFund1.getBigDecimal("hcBalance");
			BigDecimal hcAbledBalance1 = agentFund1.getBigDecimal("hcAbledBalance");
			BigDecimal hcBalance2 = agentFund2.getBigDecimal("hcBalance");
			BigDecimal hcAbledBalance2 = agentFund2.getBigDecimal("hcAbledBalance");
			BigDecimal addMoney1 = price4.multiply(ratio4).setScale(2,BigDecimal.ROUND_HALF_DOWN);
			assertEquals(hcBalance2.subtract(hcBalance1), addMoney1,"总余额校验");
			assertEquals(hcAbledBalance2.subtract(hcAbledBalance1), addMoney1,"可用余额较验");
			
			Reporter.log("[会员操作]------");
			JSONObject card5Config = healthCardConfigReslut.getJSONObject("data").getJSONArray("list").getJSONObject(4);
			Reporter.log("1）升级到福禄寿喜财卡:"+card5Config);
			JSONObject sendCodeResult1 = verificationCode.sendCode(username, "member", 8, platform);
			assertEquals(sendCodeResult1.getString("code"), "10000","发送验证码");
			String messageCode1 = memberData.getVerificationCode(username, "phone");
			JSONObject upgradeResult1 = order.healthCardUpgrade(userdata, card5Config.getIntValue("goodsId"), card1Id, messageCode1, username, platform);
			assertEquals(upgradeResult1.getString("code"), "10000","升级提交校验");
			
			Reporter.log("2）上传升级回执");
			JSONObject uploadUpOrderReceiptResult1 = order.uploadUpOrderReceipt(userdata, upgradeResult1.getJSONObject("data").getIntValue("id"), platform);
			assertEquals(uploadUpOrderReceiptResult1.getString("code"), "10000","上传升级回执"+uploadUpOrderReceiptResult1);
			
			Reporter.log("[运营中心操作]------");
			Reporter.log("1）升级对账：通过");
			JSONObject upFinanceCheckResult1 = healthCardAdmin.upFinanceCheck(sysUserdata, 2, upgradeResult1.getJSONObject("data").getIntValue("id"), "接口升级对账");
			assertEquals(upFinanceCheckResult1.getString("code"), "10000","升级对账校验"+upFinanceCheckResult1);
			
			Thread.sleep(30000);
			String card2No = healthCardData.getValeu_upOrderId(upgradeResult1.getJSONObject("data").getString("id"), "new_card_no");//数据库中获取卡号
			Reporter.log("2）在康养卡列表查询该卡"+card1No);
			JSONObject seachCardListResult2 = healthCardAdmin.healthCardList(sysUserdata, card2No);
			assertEquals(seachCardListResult2.getString("code"), "10000","按卡号查询康养卡"+seachCardListResult2);
			int card2Id = seachCardListResult2.getJSONObject("data").getJSONArray("list").getJSONObject(0).getInteger("id");
			
			Reporter.log("[代理中心操作]------");
			Reporter.log("1）查询代理商的康养卡津贴");
			JSONObject agentFundResult3 = agentFund.agentFundInfo(agentUserData);
			assertEquals(agentFundResult3.getString("code"), "10000","代理商账户信息查询"+agentFundResult3);
			JSONObject agentFund3 = agentFundResult3.getJSONObject("data");
			Reporter.log("总余额＝"+agentFund3.getString("hcBalance"));
			Reporter.log("可用余额＝"+agentFund3.getString("hcAbledBalance"));
			
			Reporter.log("2）检查代理商的康养卡津贴是否增加");
			BigDecimal price5 = card5Config.getBigDecimal("healthCardMoney");
			BigDecimal ratio5 = card5Config.getBigDecimal("profitPercentage");
			BigDecimal hcBalance3 = agentFund3.getBigDecimal("hcBalance");
			BigDecimal hcAbledBalance3 = agentFund3.getBigDecimal("hcAbledBalance");
			BigDecimal addMoney2 = price5.multiply(ratio5).subtract(addMoney1).setScale(2,BigDecimal.ROUND_HALF_DOWN);
			assertEquals(hcBalance3.subtract(hcBalance2), addMoney2,"总余额校验");
			assertEquals(hcAbledBalance3.subtract(hcAbledBalance2), addMoney2,"可用余额较验");
			
			Reporter.log("[会员操作]------");
			Reporter.log("1）查询会员账户信息");
			JSONObject memberAccountInfoResult2 = memberCenter.getMemberAccountInfo(userdata, platform);
			assertEquals(memberAccountInfoResult2.getString("code"), "10000","会员账户信息查询"+memberAccountInfoResult2);
			JSONObject beanInfo2 = memberAccountInfoResult2.getJSONObject("data");
			
			Reporter.log("2）查询会员的体检卡个数");
			JSONObject medicalcardListResult2 = medicalcard.MedicalcardList(userdata, 0, platform);
			assertEquals(medicalcardListResult2.getString("code"), "10000","体检卡列表查询");
			
			Reporter.log("3）检查会员的4种豆值和体检卡是否增加（未激活，应当不增加）");
			assertEquals(beanInfo2.getBigDecimal("score"), beanInfo1.getBigDecimal("score"),"豆校验");
			assertEquals(beanInfo2.getBigDecimal("retirementScore"), beanInfo1.getBigDecimal("retirementScore"),"养老福豆校验");
			assertEquals(beanInfo2.getBigDecimal("hscore"), beanInfo1.getBigDecimal("hscore"),"健康福豆校验");
			assertEquals(beanInfo2.getBigDecimal("registerScore"), beanInfo1.getBigDecimal("registerScore"),"学习福豆校验");
			assertEquals(medicalcardListResult2.getJSONObject("data").getIntValue("listCount"), medicalcardListResult1.getJSONObject("data").getIntValue("listCount"),"体检次数校验");
			
			Reporter.log("4）会员激活康养卡");
			JSONObject sendCodeResult = verificationCode.sendCode(username, "", 9, platform);
			assertEquals(sendCodeResult.getString("code"), "10000","验证码发送");
			String messageCode = memberData.getVerificationCode(username, "phone");
			JSONObject healthCardActiveResult = healthCard.healthCardActive(userdata, card2Id,messageCode, username, platform);
			assertEquals(healthCardActiveResult.getString("code"), "10000","激活康养卡校验"+healthCardActiveResult);
			
			Reporter.log("5）再次查询会员账户信息");
			JSONObject memberAccountInfoResult3 = memberCenter.getMemberAccountInfo(userdata, platform);
			assertEquals(memberAccountInfoResult3.getString("code"), "10000","会员账户信息查询"+memberAccountInfoResult3);
			JSONObject beanInfo3 = memberAccountInfoResult3.getJSONObject("data");
			Reporter.log("消费福豆＝"+beanInfo3.getString("score"));
			Reporter.log("养老福豆＝"+beanInfo3.getString("retirementScore"));
			Reporter.log("健康福豆="+beanInfo3.getString("hscore"));
			Reporter.log("学习福豆＝"+beanInfo3.getString("registerScore"));
			
			Reporter.log("6）查询会员的体检卡个数");
			JSONObject medicalcardListResult3 = medicalcard.MedicalcardList(userdata, 0, platform);
			assertEquals(medicalcardListResult3.getString("code"), "10000","体检卡列表查询");
			Reporter.log("体检次数＝"+medicalcardListResult3.getJSONObject("data").getString("listCount"));
			
			Reporter.log("7）检查会员的4种豆值和体检卡是否增加");
			//激活前会员豆值和体检次数
			BigDecimal score1 = beanInfo1.getBigDecimal("score");
			BigDecimal retirementScore1 = beanInfo1.getBigDecimal("retirementScore");
			BigDecimal hscore1 = beanInfo1.getBigDecimal("hscore");
			BigDecimal registerScore1 = beanInfo1.getBigDecimal("registerScore");
			int medicalcard1 = medicalcardListResult1.getJSONObject("data").getIntValue("listCount");
			//激活后会员的豆值和体检次数
			BigDecimal score3 = beanInfo3.getBigDecimal("score");
			BigDecimal retirementScore3 = beanInfo3.getBigDecimal("retirementScore");
			BigDecimal hscore3 = beanInfo3.getBigDecimal("hscore");
			BigDecimal registerScore3 = beanInfo3.getBigDecimal("registerScore");
			int medicalcard3 = medicalcardListResult3.getJSONObject("data").getIntValue("listCount");
			//会员应当增加的豆值和体检次数
			BigDecimal addblessingBean1 = card5Config.getBigDecimal("blessingBean");
			BigDecimal addenjoyBean1 = card5Config.getBigDecimal("enjoyBean");
			BigDecimal addhbean1 = card5Config.getBigDecimal("hbean");
			BigDecimal addStudyBean1 = card5Config.getBigDecimal("studyBean");
			int addmedicalcard1 = card5Config.getIntValue("examinationsCount");
			assertEquals(score3.subtract(score1), addblessingBean1,"消费福豆校验");
			assertEquals(retirementScore3.subtract(retirementScore1),addenjoyBean1 ,"养老福豆校验");
			assertEquals(hscore3.subtract(hscore1),addhbean1 ,"健康福豆校验");
			assertEquals(registerScore3.subtract(registerScore1),addStudyBean1 ,"学习福豆校验");
			assertEquals(medicalcard3-medicalcard1,addmedicalcard1 ,"体检次数校验");
			
			JSONObject card6Config = healthCardConfigReslut.getJSONObject("data").getJSONArray("list").getJSONObject(5);
			Reporter.log("1）升级到VIP私董会卡:"+card6Config);
			JSONObject sendCodeResult2 = verificationCode.sendCode(username, "member", 8, platform);
			assertEquals(sendCodeResult2.getString("code"), "10000","发送验证码");
			String messageCode2 = memberData.getVerificationCode(username, "phone");
			JSONObject upgradeResult2 = order.healthCardUpgrade(userdata, card6Config.getIntValue("goodsId"), card2Id, messageCode2, username, platform);
			assertEquals(upgradeResult2.getString("code"), "10000","升级提交校验");
			
			Reporter.log("2）上传升级回执");
			JSONObject uploadUpOrderReceiptResult2 = order.uploadUpOrderReceipt(userdata, upgradeResult2.getJSONObject("data").getIntValue("id"), platform);
			assertEquals(uploadUpOrderReceiptResult2.getString("code"), "10000","上传升级回执"+uploadUpOrderReceiptResult2);
			
			Reporter.log("[运营中心操作]------");
			Reporter.log("1）升级对账：通过");
			JSONObject upFinanceCheckResult2 = healthCardAdmin.upFinanceCheck(sysUserdata, 2, upgradeResult2.getJSONObject("data").getIntValue("id"), "接口升级对账");
			assertEquals(upFinanceCheckResult2.getString("code"), "10000","升级对账校验"+upFinanceCheckResult2);
			
			Thread.sleep(30000);
			Reporter.log("[会员操作]------");
			Reporter.log("1）查询会员账户信息");
			JSONObject memberAccountInfoResult4 = memberCenter.getMemberAccountInfo(userdata, platform);
			assertEquals(memberAccountInfoResult4.getString("code"), "10000","会员账户信息查询"+memberAccountInfoResult4);
			JSONObject beanInfo4 = memberAccountInfoResult4.getJSONObject("data");
			Reporter.log("消费福豆＝"+beanInfo4.getString("score"));
			Reporter.log("养老福豆＝"+beanInfo4.getString("retirementScore"));
			Reporter.log("健康福豆="+beanInfo4.getString("hscore"));
			Reporter.log("学习福豆＝"+beanInfo4.getString("registerScore"));
			
			Reporter.log("2）查询会员的体检卡个数");
			JSONObject medicalcardListResult4 = medicalcard.MedicalcardList(userdata, 0, platform);
			assertEquals(medicalcardListResult4.getString("code"), "10000","体检卡列表查询");
			Reporter.log("体检次数＝"+medicalcardListResult4.getJSONObject("data").getString("listCount"));
			
			Reporter.log("3）检查会员的4种豆值和体检卡是否增加");
			BigDecimal score4 = beanInfo4.getBigDecimal("score");
			BigDecimal retirementScore4 = beanInfo4.getBigDecimal("retirementScore");
			BigDecimal hscore4 = beanInfo4.getBigDecimal("hscore");
			BigDecimal registerScore4 = beanInfo4.getBigDecimal("registerScore");
			int medicalcard4 = medicalcardListResult4.getJSONObject("data").getIntValue("listCount");
			//会员应当增加的豆值和体检次数
			BigDecimal addblessingBean3 = card6Config.getBigDecimal("blessingBean").subtract(card5Config.getBigDecimal("blessingBean"));
			BigDecimal addenjoyBean3 = card6Config.getBigDecimal("enjoyBean").subtract(card5Config.getBigDecimal("enjoyBean"));
			BigDecimal addhbean3 = card6Config.getBigDecimal("hbean").subtract(card5Config.getBigDecimal("hbean"));
			BigDecimal addStudyBean3 = card6Config.getBigDecimal("studyBean").subtract(card5Config.getBigDecimal("studyBean"));
			int addmedicalcard3 = card6Config.getIntValue("examinationsCount")-card5Config.getIntValue("examinationsCount");
			assertEquals(score4.subtract(score3), addblessingBean3,"消费福豆校验");
			assertEquals(retirementScore4.subtract(retirementScore3), addenjoyBean3,"养老福豆校验");
			assertEquals(hscore4.subtract(hscore3), addhbean3,"健康福豆校验");
			assertEquals(registerScore4.subtract(registerScore3), addStudyBean3,"学习福豆校验");
			assertEquals(medicalcard4-medicalcard3, addmedicalcard3,"体检次数校验");
			
			Reporter.log("[代理中心操作]------");
			Reporter.log("1）查询代理商的康养卡津贴");
			JSONObject agentFundResult4 = agentFund.agentFundInfo(agentUserData);
			assertEquals(agentFundResult4.getString("code"), "10000","代理商账户信息查询"+agentFundResult4);
			JSONObject agentFund4 = agentFundResult4.getJSONObject("data");
			Reporter.log("总余额＝"+agentFund4.getString("hcBalance"));
			Reporter.log("可用余额＝"+agentFund4.getString("hcAbledBalance"));
			
			Reporter.log("2）检查代理商的康养卡津贴是否增加");
			BigDecimal price6 = card6Config.getBigDecimal("healthCardMoney");
			BigDecimal ratio6 = card6Config.getBigDecimal("profitPercentage");
			BigDecimal hcBalance4 = agentFund4.getBigDecimal("hcBalance");
			BigDecimal hcAbledBalance4 = agentFund4.getBigDecimal("hcAbledBalance");
			BigDecimal addMoney3 = price6.multiply(ratio6).subtract(addMoney2).subtract(addMoney1).setScale(2,BigDecimal.ROUND_HALF_DOWN);
			assertEquals(hcBalance4.subtract(hcBalance3), addMoney3,"总余额校验");
			assertEquals(hcAbledBalance4.subtract(hcAbledBalance3), addMoney3,"可用余额较验");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test_scene1() {
		String platform = "pc";
		String agentAccount = agentData.getLastHeahthAgent(1);//"18502477268";
		Reporter.log("场景一：购买(福卡)－激活－跨级升级（福禄寿喜卡）－顺序升级（福禄寿喜财卡），代理商账号："+agentAccount);
		
		try {
			Reporter.log("[运营中心操作]-------");	
			JSONObject sysLoginResult = login.sysLogin("helen", "123456li");
			assertEquals(sysLoginResult.getString("code"), "10000","运营中心登录校验"+sysLoginResult);
			JSONObject sysUserdata  = sysLoginResult.getJSONObject("data");
			
			Reporter.log("1）查询康养卡配置表");
			JSONObject healthCardConfigReslut = healthCardAdmin.healthCardConfigList(sysUserdata);
			assertEquals(healthCardConfigReslut.getString("code"), "10000","康养卡配置查询"+healthCardConfigReslut);
			JSONObject card1Config = healthCardConfigReslut.getJSONObject("data").getJSONArray("list").getJSONObject(0);
			Reporter.log("福卡配置信息："+card1Config);
			
			Reporter.log("[会员操作]--------");
			String username = memberData.get_account("phone");
			Reporter.log("1）会员登录："+username);
			JSONObject loginResult = login.login(username, "123456li", platform);
			assertEquals(loginResult.getString("code"), "10000","登录校验"+loginResult);
			JSONObject userdata = loginResult.getJSONObject("data");
			
			Reporter.log("2）进入康养卡专题页");
			JSONObject healthCardListResult = healthCard.healthCardList(userdata, platform);
			assertEquals(healthCardListResult.getString("code"), "10000","康养卡专题页查询"+healthCardListResult);
			
			String addressId = deliveryAddress.getAddrId(userdata, platform);
			Reporter.log("3）获取会员地址ID:"+addressId);
			
			JSONObject card1 = healthCardListResult.getJSONObject("data").getJSONArray("list").getJSONObject(0);
			Reporter.log("4）购买："+card1.getString("name"));//福卡
			JSONObject generateOrderResult = order.generateHealthCardOrder(userdata, addressId, agentAccount, card1.getIntValue("id"), card1.getString("skuNumber"), 1,card1.getIntValue("supplierId"), platform);
			assertEquals(generateOrderResult.getString("code"), "10000","下订单校验"+generateOrderResult);
			
			Reporter.log("5）上传回执，订单号："+generateOrderResult.getJSONObject("data").getString("orderNo"));
			JSONObject uploadReceiptResult = order.uploadReceipt(userdata, generateOrderResult.getJSONObject("data").getString("orderId"), platform);
			assertEquals(uploadReceiptResult.getString("code"), "10000","上传回执校验"+uploadReceiptResult);
			
			Reporter.log("6）记录会员账户信息");
			JSONObject memberAccountInfoResult1 = memberCenter.getMemberAccountInfo(userdata, platform);
			assertEquals(memberAccountInfoResult1.getString("code"), "10000","会员账户信息查询"+memberAccountInfoResult1);
			JSONObject beanInfo1 = memberAccountInfoResult1.getJSONObject("data");
			Reporter.log("消费福豆＝"+beanInfo1.getString("score"));
			Reporter.log("养老福豆＝"+beanInfo1.getString("retirementScore"));
			Reporter.log("健康福豆="+beanInfo1.getString("hscore"));
			Reporter.log("学习福豆＝"+beanInfo1.getString("registerScore"));
			
			Reporter.log("7）记录会员的体检卡个数");
			JSONObject medicalcardListResult1 = medicalcard.MedicalcardList(userdata, 0, platform);
			assertEquals(medicalcardListResult1.getString("code"), "10000","体检卡列表查询");
			Reporter.log("体检次数＝"+medicalcardListResult1.getJSONObject("data").getString("listCount"));

			Reporter.log("[代理中心操作]-------");
			JSONObject agentLoginResult = login.agentLogin(agentAccount, "123456li");
			assertEquals(agentLoginResult.getString("code"), "10000","代理中心登录校验"+agentLoginResult);
			JSONObject agentUserData = agentLoginResult.getJSONObject("data");
			
			Reporter.log("1）查询代理商的康养卡津贴");
			JSONObject agentFundResult1 = agentFund.agentFundInfo(agentUserData);
			assertEquals(agentFundResult1.getString("code"), "10000","代理商账户信息查询"+agentFundResult1);
			JSONObject agentFund1 = agentFundResult1.getJSONObject("data");
			Reporter.log("总余额＝"+agentFund1.getString("hcBalance"));
			Reporter.log("可用余额＝"+agentFund1.getString("hcAbledBalance"));
			
			Reporter.log("[运营中心操作]-------");		
			Reporter.log("1）财务对账：通过");
			JSONObject financeCheckResult = healthCardAdmin.financeCheck(sysUserdata, 2, generateOrderResult.getJSONObject("data").getIntValue("orderId"), "接口测试，对账通过");
			assertEquals(financeCheckResult.getString("code"), "10000","对账校验"+financeCheckResult);
			
			Thread.sleep(30000);
			String card1No = healthCardData.getValue_orderId(generateOrderResult.getJSONObject("data").getString("orderId"), "card_no");//数据库中获取卡号
			Reporter.log("2）在康养卡列表查询该卡"+card1No);
			JSONObject seachCardListResult = healthCardAdmin.healthCardList(sysUserdata, card1No);
			assertEquals(seachCardListResult.getString("code"), "10000","按卡号查询康养卡");
			int card1Id = seachCardListResult.getJSONObject("data").getJSONArray("list").getJSONObject(0).getInteger("id");

			Reporter.log("[代理中心操作]------");
			Reporter.log("1）查询代理商的康养卡津贴");
			JSONObject agentFundResult2 = agentFund.agentFundInfo(agentUserData);
			assertEquals(agentFundResult2.getString("code"), "10000","代理商账户信息查询"+agentFundResult2);
			JSONObject agentFund2 = agentFundResult2.getJSONObject("data");
			Reporter.log("总余额＝"+agentFund2.getString("hcBalance"));
			Reporter.log("可用余额＝"+agentFund2.getString("hcAbledBalance"));
			
			Reporter.log("2）检查代理商的康养卡津贴是否增加");
			BigDecimal price1 = card1Config.getBigDecimal("healthCardMoney");
			BigDecimal ratio1 = card1Config.getBigDecimal("profitPercentage");
			BigDecimal hcBalance1 = agentFund1.getBigDecimal("hcBalance");
			BigDecimal hcAbledBalance1 = agentFund1.getBigDecimal("hcAbledBalance");
			BigDecimal hcBalance2 = agentFund2.getBigDecimal("hcBalance");
			BigDecimal hcAbledBalance2 = agentFund2.getBigDecimal("hcAbledBalance");
			BigDecimal addMoney1 = price1.multiply(ratio1).setScale(2,BigDecimal.ROUND_HALF_DOWN);
			assertEquals(hcBalance2.subtract(hcBalance1), addMoney1,"总余额校验");
			assertEquals(hcAbledBalance2.subtract(hcAbledBalance1), addMoney1,"可用余额较验");
			
			Reporter.log("[会员操作]------");
			Reporter.log("1）会员激活康养卡");
			JSONObject sendCodeResult = verificationCode.sendCode(username, "", 9, platform);
			assertEquals(sendCodeResult.getString("code"), "10000","验证码发送");
			String messageCode = memberData.getVerificationCode(username, "phone");
			JSONObject healthCardActiveResult = healthCard.healthCardActive(userdata, card1Id,messageCode, username, platform);
			assertEquals(healthCardActiveResult.getString("code"), "10000","激活康养卡校验"+healthCardActiveResult);
			
			Reporter.log("2）查询会员账户信息");
			JSONObject memberAccountInfoResult2 = memberCenter.getMemberAccountInfo(userdata, platform);
			assertEquals(memberAccountInfoResult2.getString("code"), "10000","会员账户信息查询"+memberAccountInfoResult2);
			JSONObject beanInfo2 = memberAccountInfoResult2.getJSONObject("data");
			Reporter.log("消费福豆＝"+beanInfo2.getString("score"));
			Reporter.log("养老福豆＝"+beanInfo2.getString("retirementScore"));
			Reporter.log("健康福豆="+beanInfo2.getString("hscore"));
			Reporter.log("学习福豆＝"+beanInfo2.getString("registerScore"));
			
			Reporter.log("3）查询会员的体检卡个数");
			JSONObject medicalcardListResult2 = medicalcard.MedicalcardList(userdata, 0, platform);
			assertEquals(medicalcardListResult2.getString("code"), "10000","体检卡列表查询");
			Reporter.log("体检次数＝"+medicalcardListResult2.getJSONObject("data").getString("listCount"));
			
			Reporter.log("4）检查会员的4种豆值和体检卡是否增加");
			//激活前会员豆值和体检次数
			BigDecimal score1 = beanInfo1.getBigDecimal("score");
			BigDecimal retirementScore1 = beanInfo1.getBigDecimal("retirementScore");
			BigDecimal hscore1 = beanInfo1.getBigDecimal("hscore");
			BigDecimal registerScore1 = beanInfo1.getBigDecimal("registerScore");
			int medicalcard1 = medicalcardListResult1.getJSONObject("data").getIntValue("listCount");
			//激活后会员的豆值和体检次数
			BigDecimal score2 = beanInfo2.getBigDecimal("score");
			BigDecimal retirementScore2 = beanInfo2.getBigDecimal("retirementScore");
			BigDecimal hscore2 = beanInfo2.getBigDecimal("hscore");
			BigDecimal registerScore2 = beanInfo2.getBigDecimal("registerScore");
			int medicalcard2 = medicalcardListResult2.getJSONObject("data").getIntValue("listCount");
			//会员应当增加的豆值和体检次数
			BigDecimal addblessingBean1 = card1Config.getBigDecimal("blessingBean");
			BigDecimal addenjoyBean1 = card1Config.getBigDecimal("enjoyBean");
			BigDecimal addhbean1 = card1Config.getBigDecimal("hbean");
			BigDecimal addStudyBean1 = card1Config.getBigDecimal("studyBean");
			int addmedicalcard1 = card1Config.getIntValue("examinationsCount");
			assertEquals(score1.subtract(score2), addblessingBean1,"消费福豆校验");
			assertEquals(retirementScore2.subtract(retirementScore1),addenjoyBean1 ,"养老福豆校验");
			assertEquals(hscore2.subtract(hscore1),addhbean1 ,"健康福豆校验");
			assertEquals(registerScore2.subtract(registerScore1),addStudyBean1 ,"学习福豆校验");
			assertEquals(medicalcard2-medicalcard1,addmedicalcard1 ,"体检次数校验");
			
			JSONObject card4Config = healthCardConfigReslut.getJSONObject("data").getJSONArray("list").getJSONObject(3);
			Reporter.log("5）升级到福禄寿喜卡:"+card4Config);
			JSONObject sendCodeResult2 = verificationCode.sendCode(username, "member", 8, platform);
			assertEquals(sendCodeResult2.getString("code"), "10000","发送验证码");
			String messageCode2 = memberData.getVerificationCode(username, "phone");
			JSONObject upgradeResult = order.healthCardUpgrade(userdata, card4Config.getIntValue("goodsId"), card1Id, messageCode2, username, platform);
			assertEquals(upgradeResult.getString("code"), "10000","升级提交校验");
			
			Reporter.log("6）上传升级回执");
			JSONObject uploadUpOrderReceiptResult = order.uploadUpOrderReceipt(userdata, upgradeResult.getJSONObject("data").getIntValue("id"), platform);
			assertEquals(uploadUpOrderReceiptResult.getString("code"), "10000","上传升级回执"+uploadUpOrderReceiptResult);
			
			Reporter.log("[运营中心操作]------");
			Reporter.log("1）升级对账：通过");
			JSONObject upFinanceCheckResult = healthCardAdmin.upFinanceCheck(sysUserdata, 2, upgradeResult.getJSONObject("data").getIntValue("id"), "接口升级对账");
			assertEquals(upFinanceCheckResult.getString("code"), "10000","升级对账校验"+upFinanceCheckResult);
			
			Thread.sleep(30000);
			String card2No = healthCardData.getValeu_upOrderId(upgradeResult.getJSONObject("data").getString("id"), "new_card_no");//数据库中获取卡号
			Reporter.log("2）在康养卡列表查询该卡"+card1No);
			JSONObject seachCardListResult2 = healthCardAdmin.healthCardList(sysUserdata, card2No);
			assertEquals(seachCardListResult2.getString("code"), "10000","按卡号查询康养卡"+seachCardListResult2);
			int card2Id = seachCardListResult2.getJSONObject("data").getJSONArray("list").getJSONObject(0).getInteger("id");
			
			Reporter.log("[会员操作]------");
			Reporter.log("1）查询会员账户信息");
			JSONObject memberAccountInfoResult3 = memberCenter.getMemberAccountInfo(userdata, platform);
			assertEquals(memberAccountInfoResult3.getString("code"), "10000","会员账户信息查询"+memberAccountInfoResult3);
			JSONObject beanInfo3 = memberAccountInfoResult3.getJSONObject("data");
			Reporter.log("消费福豆＝"+beanInfo3.getString("score"));
			Reporter.log("养老福豆＝"+beanInfo3.getString("retirementScore"));
			Reporter.log("健康福豆="+beanInfo3.getString("hscore"));
			Reporter.log("学习福豆＝"+beanInfo3.getString("registerScore"));
			
			Reporter.log("2）查询会员的体检卡个数");
			JSONObject medicalcardListResult3 = medicalcard.MedicalcardList(userdata, 0, platform);
			assertEquals(medicalcardListResult3.getString("code"), "10000","体检卡列表查询");
			Reporter.log("体检次数＝"+medicalcardListResult3.getJSONObject("data").getString("listCount"));
			
			Reporter.log("3）检查会员的4种豆值和体检卡是否增加");
			BigDecimal score3 = beanInfo3.getBigDecimal("score");
			BigDecimal retirementScore3 = beanInfo3.getBigDecimal("retirementScore");
			BigDecimal hscore3 = beanInfo3.getBigDecimal("hscore");
			BigDecimal registerScore3 = beanInfo3.getBigDecimal("registerScore");
			int medicalcard3 = medicalcardListResult3.getJSONObject("data").getIntValue("listCount");
			//会员应当增加的豆值和体检次数
			BigDecimal addblessingBean2 = card4Config.getBigDecimal("blessingBean").subtract(card1Config.getBigDecimal("blessingBean"));
			BigDecimal addenjoyBean2 = card4Config.getBigDecimal("enjoyBean").subtract(card1Config.getBigDecimal("enjoyBean"));
			BigDecimal addhbean2 = card4Config.getBigDecimal("hbean").subtract(card1Config.getBigDecimal("hbean"));
			BigDecimal addStudyBean2 = card4Config.getBigDecimal("studyBean").subtract(card1Config.getBigDecimal("studyBean"));
			int addmedicalcard2 = card4Config.getIntValue("examinationsCount")-card1Config.getIntValue("examinationsCount");
			assertEquals(score3.subtract(score2), addblessingBean2,"消费福豆校验");
			assertEquals(retirementScore3.subtract(retirementScore2), addenjoyBean2,"养老福豆校验");
			assertEquals(hscore3.subtract(hscore2), addhbean2,"健康福豆校验");
			assertEquals(registerScore3.subtract(registerScore2), addStudyBean2,"学习福豆校验");
			assertEquals(medicalcard3-medicalcard2, addmedicalcard2,"体检次数校验");
			
			Reporter.log("[代理中心操作]------");
			Reporter.log("1）查询代理商的康养卡津贴");
			JSONObject agentFundResult3 = agentFund.agentFundInfo(agentUserData);
			assertEquals(agentFundResult3.getString("code"), "10000","代理商账户信息查询"+agentFundResult3);
			JSONObject agentFund3 = agentFundResult3.getJSONObject("data");
			Reporter.log("总余额＝"+agentFund3.getString("hcBalance"));
			Reporter.log("可用余额＝"+agentFund3.getString("hcAbledBalance"));
			
			Reporter.log("2）检查代理商的康养卡津贴是否增加");
			BigDecimal price4 = card4Config.getBigDecimal("healthCardMoney");
			BigDecimal ratio4 = card4Config.getBigDecimal("profitPercentage");
			BigDecimal hcBalance3 = agentFund3.getBigDecimal("hcBalance");
			BigDecimal hcAbledBalance3 = agentFund3.getBigDecimal("hcAbledBalance");
			BigDecimal addMoney2 = price4.multiply(ratio4).subtract(addMoney1).setScale(2,BigDecimal.ROUND_HALF_DOWN);
			assertEquals(hcBalance3.subtract(hcBalance2), addMoney2,"总余额校验");
			assertEquals(hcAbledBalance3.subtract(hcAbledBalance2), addMoney2,"可用余额较验");
			
			Reporter.log("[会员操作]------");
			JSONObject card5Config = healthCardConfigReslut.getJSONObject("data").getJSONArray("list").getJSONObject(4);
			Reporter.log("1）升级到福禄寿喜财卡:"+card5Config);
			JSONObject sendCodeResult3 = verificationCode.sendCode(username, "member", 8, platform);
			assertEquals(sendCodeResult3.getString("code"), "10000","发送验证码");
			String messageCode3 = memberData.getVerificationCode(username, "phone");
			JSONObject upgradeResult2 = order.healthCardUpgrade(userdata, card5Config.getIntValue("goodsId"), card2Id, messageCode3, username, platform);
			assertEquals(upgradeResult2.getString("code"), "10000","升级提交校验");
			
			Reporter.log("2）上传升级回执");
			JSONObject uploadUpOrderReceiptResult2 = order.uploadUpOrderReceipt(userdata, upgradeResult2.getJSONObject("data").getIntValue("id"), platform);
			assertEquals(uploadUpOrderReceiptResult2.getString("code"), "10000","上传升级回执"+uploadUpOrderReceiptResult2);
			
			Reporter.log("[运营中心操作]------");
			Reporter.log("1）升级对账：通过");
			JSONObject upFinanceCheckResult2 = healthCardAdmin.upFinanceCheck(sysUserdata, 2, upgradeResult2.getJSONObject("data").getIntValue("id"), "接口升级对账");
			assertEquals(upFinanceCheckResult2.getString("code"), "10000","升级对账校验"+upFinanceCheckResult2);
			
			Thread.sleep(30000);
			Reporter.log("[会员操作]------");
			Reporter.log("1）查询会员账户信息");
			JSONObject memberAccountInfoResult4 = memberCenter.getMemberAccountInfo(userdata, platform);
			assertEquals(memberAccountInfoResult4.getString("code"), "10000","会员账户信息查询"+memberAccountInfoResult4);
			JSONObject beanInfo4 = memberAccountInfoResult4.getJSONObject("data");
			Reporter.log("消费福豆＝"+beanInfo4.getString("score"));
			Reporter.log("养老福豆＝"+beanInfo4.getString("retirementScore"));
			Reporter.log("健康福豆="+beanInfo4.getString("hscore"));
			Reporter.log("学习福豆＝"+beanInfo4.getString("registerScore"));
			
			Reporter.log("2）查询会员的体检卡个数");
			JSONObject medicalcardListResult4 = medicalcard.MedicalcardList(userdata, 0, platform);
			assertEquals(medicalcardListResult4.getString("code"), "10000","体检卡列表查询");
			Reporter.log("体检次数＝"+medicalcardListResult4.getJSONObject("data").getString("listCount"));
			
			Reporter.log("3）检查会员的4种豆值和体检卡是否增加");
			BigDecimal score4 = beanInfo4.getBigDecimal("score");
			BigDecimal retirementScore4 = beanInfo4.getBigDecimal("retirementScore");
			BigDecimal hscore4 = beanInfo4.getBigDecimal("hscore");
			BigDecimal registerScore4 = beanInfo4.getBigDecimal("registerScore");
			int medicalcard4 = medicalcardListResult4.getJSONObject("data").getIntValue("listCount");
			//会员应当增加的豆值和体检次数
			BigDecimal addblessingBean3 = card5Config.getBigDecimal("blessingBean").subtract(card4Config.getBigDecimal("blessingBean"));
			BigDecimal addenjoyBean3 = card5Config.getBigDecimal("enjoyBean").subtract(card4Config.getBigDecimal("enjoyBean"));
			BigDecimal addhbean3 = card5Config.getBigDecimal("hbean").subtract(card4Config.getBigDecimal("hbean"));
			BigDecimal addStudyBean3 = card5Config.getBigDecimal("studyBean").subtract(card4Config.getBigDecimal("studyBean"));
			int addmedicalcard3 = card5Config.getIntValue("examinationsCount")-card4Config.getIntValue("examinationsCount");
			assertEquals(score4.subtract(score3), addblessingBean3,"消费福豆校验");
			assertEquals(retirementScore4.subtract(retirementScore3), addenjoyBean3,"养老福豆校验");
			assertEquals(hscore4.subtract(hscore3), addhbean3,"健康福豆校验");
			assertEquals(registerScore4.subtract(registerScore3), addStudyBean3,"学习福豆校验");
			assertEquals(medicalcard4-medicalcard3, addmedicalcard3,"体检次数校验");
			
			Reporter.log("[代理中心操作]------");
			Reporter.log("1）查询代理商的康养卡津贴");
			JSONObject agentFundResult4 = agentFund.agentFundInfo(agentUserData);
			assertEquals(agentFundResult4.getString("code"), "10000","代理商账户信息查询"+agentFundResult4);
			JSONObject agentFund4 = agentFundResult4.getJSONObject("data");
			Reporter.log("总余额＝"+agentFund4.getString("hcBalance"));
			Reporter.log("可用余额＝"+agentFund4.getString("hcAbledBalance"));
			
			Reporter.log("2）检查代理商的康养卡津贴是否增加");
			BigDecimal price5 = card5Config.getBigDecimal("healthCardMoney");
			BigDecimal ratio5 = card5Config.getBigDecimal("profitPercentage");
			BigDecimal hcBalance4 = agentFund4.getBigDecimal("hcBalance");
			BigDecimal hcAbledBalance4 = agentFund4.getBigDecimal("hcAbledBalance");
			BigDecimal addMoney3 = price5.multiply(ratio5).subtract(addMoney2).subtract(addMoney1).setScale(2,BigDecimal.ROUND_HALF_DOWN);
			assertEquals(hcBalance4.subtract(hcBalance3), addMoney3,"总余额校验");
			assertEquals(hcAbledBalance4.subtract(hcAbledBalance3), addMoney3,"可用余额较验");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
