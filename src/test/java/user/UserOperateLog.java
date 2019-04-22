/**
 * @author helen
 * @date 2018年12月7日
 */
package user;

import com.alibaba.fastjson.JSONObject;

import common.MyAPI;

/**
 * @Description:操作日志接口
 */
public class UserOperateLog {
	MyAPI myAPI = new MyAPI();
	
	/*操作日志列表查询*/
	public JSONObject operateLog(JSONObject sysUserdata,String gxId,int logType) {
		JSONObject params = new JSONObject();
		params.put("gxId", gxId);
		params.put("logType", logType);//记录类型 0信用卡代理 1保险代理 2商城代理 3实名认证
		
		return myAPI.sysGet(sysUserdata, "/userCenter/user/operate/log", params);
	}

}
