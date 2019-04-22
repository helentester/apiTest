/**
 * @author helen
 * @date 2019年1月14日
 */
package testcase;

import java.util.HashMap;

import org.testng.Reporter;
import org.testng.annotations.Test;

import common.BaseData;
import common.MyConfig;
import common.MysqlConnect;
import data.AgentData;
import data.GoodData;
import data.MemberData;
import data.OrderData;
import data.SellerData;

/**
 * @Description:分润体系：包含1.0版本和2.0版本
 */
public class ProfitTest {
	BaseData baseData = new BaseData();
	AgentData agentData = new AgentData();
	SellerData sellerData = new SellerData();
	MemberData memberData = new MemberData();
	GoodData goodData = new GoodData();
	OrderData orderData = new OrderData();
	MysqlConnect mysqlConnect = new MysqlConnect();
	MyConfig myConfig = new MyConfig();

	HashMap<String, String> AS = new HashMap<String, String>();
	String member = "13524569458";//会员
	String inviter = "13825464584";//会员的邀请人

	/*检查测试数据*/
	@Test
	public void test_checkAllData() {
		Reporter.log("检查商代会员体系");
		//会员关系
		memberData.update_Inviter(member, inviter);
		
		//商代关系
		String testService= myConfig.getPropertyValue("testService");//获取当前的测试环境
		if (testService.equals("237")) {
			AS = new HashMap<String, String>(){
			private static final long serialVersionUID = 1L;
			{
				put("A1", "13302723392");
				put("A2", "15308474090");
				put("A4", "13006584441");
				put("A5", "17105040795");
				put("A6", "17008852971");
				put("A6_2", "15807441502");
				put("H3", "18208873865");
				put("H4", "18204983331");
				put("H5", "18507835187");
				put("H7", "18702876997");
				put("S7", "18003901151");
				put("S6", "15700677945");
				put("S5", "17707461396");
				put("S8", "14501365807");
				put("S7_2", "18507042598");
				put("S6_2", "17705764379");
				put("S5_2", "15100662485");
				put("S4", "13205502874");
				put("v2S1", "18808614638");
				put("v2S2", "17707722880");
				put("v2S3", "13904381198");
				put("v2S4", "13604713527");
				put("v2S5", "14502698219");
				put("v2S6", "17307058075");
				put("v2S7", "14506958986");
				put("v2S8", "15606212461");
				put("G1", "7778");//分润G1普通商品
				put("G2", "7779");//分润G2普通商品
				put("G3", "7780");//分润G3普通商品
				put("G4", "7781");//分润G4虚拟商品
				put("G5", "7782");//分润G5虚拟商品
				put("G6", "7785");//分润G6虚拟商品
				put("G7", "7783");//分润G7海外购商品
				put("G8", "7784");//分润G8海外购商品
				//设置多一个普通商品
				put("G11", "7789");//分润G11普通商品
				put("G22", "7790");//分润G22普通商品
				put("G33", "7791");//分润G33普通商品
				//v2版本分润规则所用的商品
				put("v2G1", "8028");//普通商品
				put("v2G2", "8022");//普通商品
				put("v2G3", "8085");//虚拟商品
				put("v2G4", "8073");//虚拟商品
				put("v2G5", "7990");//普通商品
				put("v2G6", "7821");//普通商品
				put("v2G7", "7825");//普通商品
				put("v2G8", "7828");//普通商品
				//设置多个v2版本分润规则所用的商品
				put("v2G11", "8011");//普通商品
				put("v2G22", "8002");//普通商品
				}
			};
		}
		else if (testService.equals("236")) {
			AS = new HashMap<String, String>(){
				private static final long serialVersionUID = 1L;
				{
					put("A1", "15207466927");
					put("A2", "15807482990");
					put("A4", "13908175345");
					put("A5", "17503087859");
					put("A6", "14500199002");
					put("A6_2", "17708286029");
					put("H3", "18801904664");
					put("H4", "17305508966");
					put("H5", "18207486993");
					put("H7", "17708618018");
					put("S7", "17708477898");
					put("S6", "17705716937");
					put("S5", "15708645656");
					put("S8", "13203354586");
					put("S7_2", "15904171935");
					put("S6_2", "17707562547");
					put("S5_2", "17302441929");
					put("S4", "13902183042");
					put("v2S1", "18905339026");
					put("v2S2", "18707403890");
					put("v2S3", "18802838989");
					put("v2S4", "18808100407");
					put("v2S5", "18606982427");
					put("v2S6", "13108857972");
					put("v2S7", "15902020661");
					put("v2S8", "18104526811");
					put("G1", "7778");//分润G1普通商品
					put("G2", "7779");//分润G2普通商品
					put("G3", "7780");//分润G3普通商品
					put("G4", "7781");//分润G4虚拟商品
					put("G5", "7782");//分润G5虚拟商品
					put("G6", "7785");//分润G6虚拟商品
					put("G7", "7783");//分润G7海外购商品
					put("G8", "7784");//分润G8海外购商品
					//设置多一个普通商品
					put("G11", "7789");//分润G11普通商品
					put("G22", "7790");//分润G22普通商品
					put("G33", "7791");//分润G33普通商品
					//v2版本分润规则所用的商品
					put("v2G1", "7991");//普通商品
					put("v2G2", "7995");//普通商品
					put("v2G3", "7998");//虚拟商品
					put("v2G4", "7999");//虚拟商品
					put("v2G5", "8000");//普通商品
					put("v2G6", "8001");//普通商品
					put("v2G7", "8002");//普通商品
					put("v2G8", "8003");//普通商品
					//设置多个v2版本分润规则所用的商品
					put("v2G11", "7994");//普通商品
					put("v2G22", "7996");//普通商品
					}
				};
		}
		else {
			System.out.println(testService+"测试环境没有该商代会员体系");
		}
		//个代与健康家
		agentData.update_inviter(AS.get("A1"), AS.get("A2"));
		agentData.update_inviter(AS.get("A2"), AS.get("H3"));
		agentData.update_inviter(AS.get("H3"), AS.get("A4"));
		agentData.update_inviter(AS.get("A4"), AS.get("A5"));
		agentData.update_inviter(AS.get("A5"), AS.get("A6"));
		agentData.update_inviter(AS.get("H3"), AS.get("H4"));
		agentData.update_inviter(AS.get("H4"), AS.get("H5"));
		agentData.update_inviter(AS.get("H5"), AS.get("A6_2"));
		agentData.update_inviter(AS.get("A6_2"), AS.get("H7"));
		//商家与代理商的关系
		sellerData.update_inviter(AS.get("A6"), AS.get("S7"));
		sellerData.update_inviter(AS.get("A5"), AS.get("S6"));
		sellerData.update_inviter(AS.get("A4"), AS.get("S5"));
		sellerData.update_inviter(AS.get("H7"), AS.get("S8"));
		sellerData.update_inviter(AS.get("A6_2"), AS.get("S7_2"));
		sellerData.update_inviter(AS.get("H5"), AS.get("S6_2"));
		sellerData.update_inviter(AS.get("H4"), AS.get("S5_2"));
		sellerData.update_inviter(AS.get("H3"), AS.get("S4"));
		sellerData.update_inviter(AS.get("H5"),AS.get("v2S1"));
		sellerData.update_inviter(AS.get("H7"),AS.get("v2S2"));
		sellerData.update_inviter(AS.get("A6_2"),AS.get("v2S3"));
		sellerData.update_inviter(AS.get("A5"),AS.get("v2S4") );
		sellerData.update_inviter(AS.get("H5"),AS.get("v2S5"));
		sellerData.update_inviter(AS.get("H7"),AS.get("v2S7"));
		sellerData.update_inviter(AS.get("A6_2"),AS.get("v2S6"));
		sellerData.update_inviter(AS.get("A5"),AS.get("v2S8") );
		//设置商家的分润比例
		sellerData.update_profitPercentage(AS.get("S7"), "0.1");
		sellerData.update_profitPercentage(AS.get("S6"), "0.1");
		sellerData.update_profitPercentage(AS.get("S5"), "0.1");
		sellerData.update_profitPercentage(AS.get("S8"), "0.1");
		sellerData.update_profitPercentage(AS.get("S7_2"), "0.1");
		sellerData.update_profitPercentage(AS.get("S6_2"), "0.1");
		sellerData.update_profitPercentage(AS.get("S5_2"), "0.1");
		sellerData.update_profitPercentage(AS.get("S4"), "0.1");
		sellerData.update_profitPercentage(AS.get("v2S1"), "0.1");
		sellerData.update_profitPercentage(AS.get("v2S2"), "0.1");
		sellerData.update_profitPercentage(AS.get("v2S3"), "0.1");
		sellerData.update_profitPercentage(AS.get("v2S4"), "0.1");
		sellerData.update_profitPercentage(AS.get("v2S5"), "0.0655");
		sellerData.update_profitPercentage(AS.get("v2S6"), "0.8999");
		sellerData.update_profitPercentage(AS.get("v2S7"), "0.0");
		sellerData.update_profitPercentage(AS.get("v2S8"), "1");
		//检查商家下的商品
		this.checkAS_goods(AS.get("S7"), AS.get("G1"));
		this.checkAS_goods(AS.get("S6"), AS.get("G2"));
		this.checkAS_goods(AS.get("S5"), AS.get("G3"));
		this.checkAS_goods(AS.get("S7"), AS.get("G11"));
		this.checkAS_goods(AS.get("S6"), AS.get("G22"));
		this.checkAS_goods(AS.get("S5"), AS.get("G33"));
		this.checkAS_goods(AS.get("S7_2"), AS.get("G4"));
		this.checkAS_goods(AS.get("S4"), AS.get("G5"));
		this.checkAS_goods(AS.get("S8"), AS.get("G6"));
		this.checkAS_goods(AS.get("S5_2"), AS.get("G7"));
		this.checkAS_goods(AS.get("S6_2"), AS.get("G8"));
		this.checkAS_goods(AS.get("v2S1"), AS.get("v2G1"));
		this.checkAS_goods(AS.get("v2S1"), AS.get("v2G11"));
		this.checkAS_goods(AS.get("v2S2"), AS.get("v2G2"));
		this.checkAS_goods(AS.get("v2S2"), AS.get("v2G22"));
		this.checkAS_goods(AS.get("v2S3"), AS.get("v2G3"));
		this.checkAS_goods(AS.get("v2S4"), AS.get("v2G4"));
		this.checkAS_goods(AS.get("v2S5"), AS.get("v2G5"));
		this.checkAS_goods(AS.get("v2S6"), AS.get("v2G6"));
		this.checkAS_goods(AS.get("v2S7"), AS.get("v2G7"));
		this.checkAS_goods(AS.get("v2S8"), AS.get("v2G8"));
		
		Reporter.log("**************商代体系*****************");
		Reporter.log("第一种情况：商品G1（"+AS.get("G1")+"、"+AS.get("G11")+"）－商家S7（"+AS.get("S7")+"）－个代A6（"+AS.get("A6")+"）－个代A5（"+AS.get("A5")+"）－个代A4（"+AS.get("A4")+"）－健康家H3（"+AS.get("H3")+"）－个代A2（"+AS.get("A2")+"）－个代A1（"+AS.get("A1")+"）");
		Reporter.log("第二种情况：商品G2（"+AS.get("G2")+"、"+AS.get("G22")+"）－商家S6（"+AS.get("S6")+"）－个代A5（"+AS.get("A5")+"）－个代A4（"+AS.get("A4")+"）－健康家H3（"+AS.get("H3")+"）－个代A2（"+AS.get("A2")+"）－个代A1（"+AS.get("A1")+"）");
		Reporter.log("第三种情况：商品G3（"+AS.get("G3")+"、"+AS.get("G33")+"）－商家S5（"+AS.get("S5")+"）－个代A4（"+AS.get("A4")+"）－健康家H3（"+AS.get("H3")+"）－个代A2（"+AS.get("A2")+"）－个代A1（"+AS.get("A1")+"）");
		Reporter.log("第四种情况：商品G4（"+AS.get("G4")+"）－商家S7_2（"+AS.get("S7_2")+"）－个代A6_2（"+AS.get("A6_2")+"）－健康家H5（"+AS.get("H5")+"）－健康家H4（"+AS.get("H4")+"）－健康家H3（"+AS.get("H3")+"）－个代A2（"+AS.get("A2")+"）－个代A1（"+AS.get("A1")+"）");
		Reporter.log("第五种情况：商品G5（"+AS.get("G5")+"）－商家S4（"+AS.get("S4")+"）－健康家H3（"+AS.get("H3")+"）－个代A2（"+AS.get("A2")+"）－个代A1（"+AS.get("A1")+"）");
		Reporter.log("第六种情况：商品G6（"+AS.get("G6")+"）－商家S8（"+AS.get("S8")+"）－健康家H7（"+AS.get("H7")+"）－个代A6_2（"+AS.get("A6_2")+"）－健康家H5（"+AS.get("H5")+"）－健康家H4（"+AS.get("H4")+"）－健康家H3（"+AS.get("H3")+"）－个代A2（"+AS.get("A2")+"）－个代A1（"+AS.get("A1")+"）");
		Reporter.log("第七种情况：商品G7（"+AS.get("G7")+"）－商家S5_2（"+AS.get("S5_2")+"）－健康家H4（"+AS.get("H4")+"）－健康家H3（"+AS.get("H3")+"）－个代A2（"+AS.get("A2")+"）－个代A1（"+AS.get("A1")+"）");
		Reporter.log("第八种情况：商品G8（"+AS.get("G8")+"）－商家S6_2（"+AS.get("S6_2")+"）－健康家H5（"+AS.get("H5")+"）－健康家H4（"+AS.get("H4")+"）－健康家H3（"+AS.get("H3")+"）－个代A2（"+AS.get("A2")+"）－个代A1（"+AS.get("A1")+"）");
		Reporter.log("************************************");
	}
	
	/*商家下的商品检查
	 * @seller 商家账号
	 * @goodId 商品Id
	 * return	商品ID
	 * */
	public void checkAS_goods(String seller,String goodId) {
		
		/****通过update的方法保证商品数据的正确性*****/
		// 保存商家与商品的关联
			mysqlConnect.updateData("UPDATE p_goods set supplier_id=(SELECT supplier_id from u_supplier_user WHERE account='"+seller+"') where id="+goodId);
			// 保证商品与商家的“全国模板”关联：非免费模板
			String templateId = sellerData.getTemplateId_name(seller, "全国模板");
			mysqlConnect.updateData("UPDATE p_goods set freight_template_id="+templateId+" where id="+goodId);
			// 设置商品的上架状态
			mysqlConnect.updateData("UPDATE p_goods set is_onlive=1,publish_wait=1 where id="+goodId);
			// 把库存为0 的商品库存增加10个库存
			mysqlConnect.updateData("UPDATE p_goods_sku SET bought=10  where bought=0 and goods_id in(" +goodId+ ")");
			// 设置SKU1(现金100，会员应得养老积分15%、消费积分10%)
			mysqlConnect.updateData("UPDATE p_goods_sku set current_price=100,min_buy_count=1,use_score=0,integral_use_ratio=0,is_delete=0 WHERE goods_id="+goodId+" and sku_title='s1 k1'");
			// 设置SKU2(现金40+福豆10，会员应当养老积分0、消费积分10)
			mysqlConnect.updateData("UPDATE p_goods_sku set current_price=50,min_buy_count=1,use_score=1,integral_use_ratio=10,is_delete=0 WHERE goods_id="+goodId+" and sku_title='s1 k2'");
			// 设置SKU3(现金30+福豆10，会员应当养老积分6、消费积分4)
			mysqlConnect.updateData("UPDATE p_goods_sku set current_price=40,min_buy_count=1,use_score=1,integral_use_ratio=10,is_delete=0 WHERE goods_id="+goodId+" and sku_title='s2 k1'");
			// 设置SKU4（现金10+福豆40）
			mysqlConnect.updateData("UPDATE p_goods_sku set current_price=50,min_buy_count=1,use_score=1,integral_use_ratio=40,is_delete=0 WHERE goods_id="+goodId+" and sku_title='s2 k2'");
	}
	
	/*生成订单*/
	public void generateOrder(String Consumer) {
		Reporter.log("[生成订单]------");
		
		Reporter.log("--------------");
	}

}
