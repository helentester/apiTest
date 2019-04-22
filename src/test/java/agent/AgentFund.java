/**
 * @author helen
 * @date 2019年4月1日
 */
package agent;

import com.alibaba.fastjson.JSONObject;

import common.MyAPI;

/**
 * @Description:代理商账户相关接口
 */
public class AgentFund {
	MyAPI myAPI = new MyAPI();
	
	/*获取代理商账户信息*/
	public JSONObject agentFundInfo(JSONObject agentUserdata) {
		JSONObject params = new JSONObject();
		params.put("agentId", agentUserdata.getInteger("agentId"));
		
		JSONObject result = new JSONObject();
		result = myAPI.agentPost(agentUserdata,"/userCenter/fund/agent/apply/agentInfo", params);
		return result;
	}

}
