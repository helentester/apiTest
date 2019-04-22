/**
 * @author helen
 * @date 2019年4月8日
 */
package seller;

import com.alibaba.fastjson.JSONObject;

import common.MyAPI;

/**
 * @Description:商家注册
 */
public class SupplierRegister {
	MyAPI myAPI = new MyAPI();
	
	/*商家完善信息*/
	public JSONObject add(JSONObject sellerUserdata,int supplierTag,String legalMobile,String supplierName,String agentAccount) {
		JSONObject params = new JSONObject();
		//基本信息
		params.put("supplierId", sellerUserdata.getInteger("supplierId"));//商家ID
		params.put("supplierTag", supplierTag);//(string, optional): 主营类目 ,
		params.put("legalMobile", legalMobile);//(string, optional): 法人电话 ,
		params.put("personMobile", legalMobile);//(string, optional): 联系人电话 ,
		params.put("supplierPerson", "接口商家");
		params.put("companyCharacter", "1");//(string, optional): 企业性质 1企业法人 2个体工商户 ,
		params.put("supplierName", supplierName+"商家");// (string, optional): 商户名称 ,
		params.put("supplierOrgName", supplierName+"公司");//(string, optional): 公司名称 ,
		params.put("province", "440000");//省级区域码 ,广东省
		params.put("city", "440100");//市级区域码 ,广州
		params.put("supplierAreaCode", "440104");// (string, optional): 所在地区区域码 ,越秀区
		params.put("supplierAreaName", "广东省广州市越秀区");//(string, optional): 商家区域名称 
		//资质信息
		params.put("idCardCopiesFront", "http://wftest.oss-cn-shenzhen.aliyuncs.com/user/idcard/1554052672932.jpg");//(string, optional): 法人证件复印正面 
		params.put("idCardCopiesReverse", "http://wftest.oss-cn-shenzhen.aliyuncs.com/user/idcard/1554052686624.jpg");//(string, optional): 法人证件复印反面 ,
		params.put("supplierOrgLicense", "http://wftest.oss-cn-shenzhen.aliyuncs.com/user/businesslicense/1554052703197.jpg");//(string, optional): 公司营业执照 ,
		params.put("bankLicense", "http://wftest.oss-cn-shenzhen.aliyuncs.com/user/bankcard/1554052714879.jpg");//(string, optional): 银行开户许可 ,
		//商标信息上传
		params.put("brandType", 1);//(integer, optional): 商标类型 1=自有商标 2=授权商标 ,
		params.put("brandNumber", "sellerTrademarkNB178563");//(string, optional): 商标注册号 ,
		params.put("brandCertifying", "http://wftest.oss-cn-shenzhen.aliyuncs.com/user/leaseContract/1554709853548.jpg");// (string, optional): 商标注册证明 ,
		//店铺信息
		params.put("supplierLogo", "http://wftest.oss-cn-shenzhen.aliyuncs.com/user/logo/1554709861100.jpg");//(string, optional): 店铺logo ,
		params.put("supplierDesc", "接口完善商家信息");// (string, optional): 店铺描述 ,
		//银行卡信息
		params.put("bankAccountName", supplierName);//(string, optional): 银行户名 ,
		params.put("bankName", "中国银行");//(string, optional): 开户银行 ,
		params.put("bankBranch", "番禺支行");//(string, optional): 银行支行 ,
		params.put("bankAccount", "78954545454545");//银行卡号
		//邀请信息
		params.put("recommendCode", agentAccount);//(string, optional): 推荐代理商账号(手机号) ,
		
		JSONObject result = new JSONObject();
		result = myAPI.sellerPost(sellerUserdata, "/userCenter/supplierBack/add", params);
		
		return result;
	}
	
	
}
