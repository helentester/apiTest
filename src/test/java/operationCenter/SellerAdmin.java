/**
 * @author helen
 * @date 2019年4月9日
 */
package operationCenter;

import com.alibaba.fastjson.JSONObject;

import common.BaseData;
import common.MyAPI;

/**
 * @Description:商家管理
 */
public class SellerAdmin {
	BaseData baseData = new BaseData();
	MyAPI myAPI = new MyAPI();
	
	public JSONObject sellerAudit(JSONObject sysUserdata,int supplierId,int auditorStatus) {
		JSONObject params = new JSONObject();
		params.put("supplierId",supplierId ); //商家ID
		params.put("auditorStatus", auditorStatus);//(integer, optional): 状态：1通过; 2拒绝
		params.put("contractStartDate", baseData.getTimeStamp13(0));//合同开始时间 13位时间戳
		params.put("contractEndDate",baseData.getTimeStamp13(365) );//合同结束时间 13位时间戳
		
		JSONObject result = new JSONObject();
		result = myAPI.sysPost(sysUserdata, "/userCenter/back/supplier/audit", params);
		return result;
	}


}
