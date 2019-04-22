/**
 * @author helen
 * @date 2018年8月28日
 */
package data;

import org.testng.annotations.DataProvider;

import common.BaseData;
import common.MyExcel;

/**
 * @Description:
 */
public class PC_dataProvider {
	MyExcel myExcel = new MyExcel();
	BaseData baseData = new BaseData();
	String filePath = baseData.getFilePath("testdata.xlsx");

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	/*对应登录接口login*/
	@DataProvider(name="login")
	public Object[][] getLoginData(){
		return myExcel.readExcel(filePath, "login");
	}
	
	/*对应会员中心－个人信息:memberInfo*/
	@DataProvider(name="memberInfo")
	public Object[][] getMemberInfoData(){
		return myExcel.readExcel(filePath, "memberInfo");
	}
	
	/*电话区号*/
	@DataProvider(name="telephoneCode")
	public Object[][] get_telephoneCode(){
		return myExcel.readExcel(filePath, "telephoneCode");
	}

}
