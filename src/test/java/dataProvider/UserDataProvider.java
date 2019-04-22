/**
 * @author helen
 * @date 2018年10月18日
 */
package dataProvider;

import org.testng.annotations.DataProvider;

import common.BaseData;
import common.MyExcel;

/**
 * @Description:用户相关接口API(user)  数据源
 */
public class UserDataProvider {
	MyExcel myExcel = new MyExcel();
	BaseData baseData = new BaseData();
	String filePath = baseData.getFilePath("ExcelData//userApiData.xlsx");
	
	@DataProvider(name="236sellers")
	public Object[][] get_236sellers(){
		return myExcel.readExcel(filePath, "236sellers");
	}
	
	@DataProvider(name="237sellers")
	public Object[][] get_237sellers(){
		return myExcel.readExcel(filePath, "237sellers");
	}
	
	@DataProvider(name="oneGoodsShare")
	public Object[][] get_oneGoodsShare(){
		return myExcel.readExcel(filePath, "oneGoodsShare");//主要用于购买一个商品时，优惠券福豆分摊测试
	}
	
	@DataProvider(name="autoRealNameData")
	public Object[][] get_autoData(){
		return myExcel.readExcel(filePath, "autoRealNameData");
	}
	
	@DataProvider(name="applyCreditCard")
	public Object[][] get_applyCreditCard(){
		return myExcel.readExcel(filePath, "applyCreditCard");
	}
	
	@DataProvider(name="noPCPlatform")
	public Object[][] get_noPCPlatform(){
		return myExcel.readExcel(filePath, "noPCPlatform");
	}
	
	@DataProvider(name="apiPlatform")
	public Object[][] get_apiPlatform(){
		return myExcel.readExcel(filePath, "apiPlatform");
	}
	
	@DataProvider(name="telephoneId")
	public Object[][] get_telephoneId(){
		return myExcel.readExcel(filePath, "telephoneId");
	}
	
	@DataProvider(name="platform")
	public Object[][] get_platform(){
		return myExcel.readExcel(filePath, "platform");
	}
}
