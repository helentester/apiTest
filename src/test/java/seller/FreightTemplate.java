/**
 * @author helen
 * @date 2018年11月27日
 */
package seller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import common.BaseData;
import common.MyAPI;

/**
 * @Description:商家中心的运费模板
 */
public class FreightTemplate {
	BaseData baseData = new BaseData();
	MyAPI myAPI = new MyAPI();
	
	/*添加运费模板*/
	public JSONObject templateAdd(JSONObject sellerUserdata,JSONArray templateDetailList) {
		JSONObject params = new JSONObject();
		params.put("name", "全国模板"+baseData.getNum(0, 99999));//模板名称,必填
		params.put("billingMethod", "1");//计费方式 1:按件数 2:按重量,必填 .备注：现在系统只能按件数
		params.put("templateDetailList", templateDetailList);//配送区域列表信息，必填,设置为全国
		return myAPI.sellerPost(sellerUserdata, "/goodsCenter/admin/template/add", params);
	}
	
	/*添加运费模板,默认添加一个全国模板*/
	public JSONObject templateAdd(JSONObject sellerUserdata) {
		JSONArray templateDetailList = new JSONArray();
		JSONObject detail = new JSONObject();
		detail.put("distributionTitle", "全国");
		detail.put("distributionArea", "000000");
		detail.put("firstFee", "10");//首件
		detail.put("firstPiece", "1");
		detail.put("renewFee", "5");
		detail.put("renewPiece", "1");
		templateDetailList.add(detail);
		return this.templateAdd(sellerUserdata, templateDetailList);
	}
	
	/*查询运费模板*/
	public JSONObject templateDetail(JSONObject sellerUserdata,String templateId) {
		JSONObject params = new JSONObject();
		params.put("templateId", templateId);//运费模板ID
		return myAPI.sellerGet(sellerUserdata, "/goodsCenter/admin/template/get", params);
	}
	
	/*修改运费模板*/
	public JSONObject templateUpdate(JSONObject sellerUserdata,String templateId,JSONArray templateDetailList) {
		JSONObject params = new JSONObject();
		params.put("templateId", templateId);//运费模板ID
		params.put("name", "全国+海外模板"+baseData.getNum(0, 99999));//模板名称,必填
		params.put("billingMethod", "1");//计费方式 1:按件数 2:按重量,必填 .备注：现在系统只能按件数
		params.put("templateDetailList", templateDetailList);//配送区域列表信息，必填,设置为全国
		return myAPI.sellerPost(sellerUserdata, "/goodsCenter/admin/template/update", params);
	}
	
	/*查询商家的运费模板列表*/
	public JSONObject templateList(JSONObject sellerUserdata) {
		return myAPI.sellerGet(sellerUserdata, "/goodsCenter/admin/template/list");
	}
	
	/*禁用、启用模板
	 * @status 1启用，2禁用
	 * */
	public JSONObject templateStatus(JSONObject sellerUserdata,String templateId,String status) {
		JSONObject params = new JSONObject();
		params.put("templateId", templateId);
		params.put("status", status);
		return myAPI.sellerPost(sellerUserdata, "/goodsCenter/admin/template/isStatus", params);
	}
	
	/*删除模板*/
	public JSONObject templateDelete(JSONObject sellerUserdata,String templateId) {
		JSONObject params = new JSONObject();
		params.put("templateId", templateId);
		return myAPI.sellerDelete(sellerUserdata, "/goodsCenter/admin/template/delete?templateId="+templateId, params);
	}
	
	/*运费模板下拉列表*/
	public JSONObject getTemplateList(JSONObject sellerUserdata) {
		JSONObject params = new JSONObject();
		params.put("supplierId", sellerUserdata.getString("supplierId"));
		return myAPI.sellerGet(sellerUserdata, "/goodsCenter/admin/template/getTemplateList");
	}

}
