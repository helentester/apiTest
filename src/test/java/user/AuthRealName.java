/**
 * @author helen
 * @date 2018年12月4日
 */
package user;

import com.alibaba.fastjson.JSONObject;

import common.BaseData;
import common.MyAPI;

/**
 * @Description:注意 实名认证业务不做输入校验，保正正常业务即可
 * 实名认证管理 : Auth Real Name Controller
 * H5->会员实名认证 : Auth Real Name App H 5 Controller
 * APP->会员实名认证 : Auth Real Name App Controller
 */
public class AuthRealName {
	BaseData baseData = new BaseData();
	MyAPI myAPI = new MyAPI();
	
	/*添加身份证信息（该接口弃用）*/
	public JSONObject addIdCard1(JSONObject userdata,String platform) {
		JSONObject params = new JSONObject();
		params.put("realName", "张三");//真实姓名
		params.put("idCard", baseData.getIdCard());//身份证号
		params.put("idCardEndDate", baseData.getTimeStamp13(365));
		params.put("idCardFront", "/member/real/%E8%BA%AB%E4%BB%BD%E8%AF%81.jpg");//身份证正面
		params.put("idCardReverse", "/member/real/%E8%BA%AB%E4%BB%BD%E8%AF%81%E5%8F%8D%E9%9D%A2.jpg");//身份证反面
		params.put("uid", userdata.getString("uid"));
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/userCenter/member/v1/addIdCard", params, platform);//放弃使用这个接口，直接用idCardEdit接口

		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/userCenter/member/h5/addIdCard", params); //h5放弃使用这个接口，直接用idCardEdit接口
		}
		else {
			System.out.println(platform+"端没有[添加身份证信息]接口");
		}
		
		return result;
	}
	
	/*编辑会员身份证信息*/
	public JSONObject idCardEdit(JSONObject userdata,String idCard,String platform) {
		JSONObject params = new JSONObject();
		params.put("realName", "张三");//真实姓名
		params.put("idCard", idCard);//身份证号
		params.put("idCardEndDate", baseData.getTimeStamp13(365));
		params.put("idCardFront", "/member/real/%E8%BA%AB%E4%BB%BD%E8%AF%81.jpg");//身份证正面
		params.put("idCardReverse", "/member/real/%E8%BA%AB%E4%BB%BD%E8%AF%81%E5%8F%8D%E9%9D%A2.jpg");//身份证反面
		params.put("uid", userdata.getString("uid"));
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/userCenter/member/v2/idCardEdit", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/api/userCenter/idCardEdit", params);
		}
		else {
			System.out.println(platform+"端没有[编辑会员身份证信息]接口");
		}
		
		return result;
	}
	
	/*获取会员身份证信息*/
	public JSONObject idcard(JSONObject userdata,String platform) {
		JSONObject params = new JSONObject();
		params.put("uid", userdata.getString("uid"));
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/userCenter/member/v1/idcard", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/userCenter/member/h5/idcard", params);
		}
		else {
			System.out.println(platform+"端没有[获取会员身份证信息]接口");
		}
		
		return result;
	}
	
	/*删除会员身份证信息（该接口已弃用）*/
	public JSONObject idCardDel(JSONObject userdata,String platform) {
		JSONObject params = new JSONObject();
		params.put("uid", userdata.getString("uid"));
		
		JSONObject result = new JSONObject();
		if (platform.equals("android")||platform.equals("ios")) {
			result = myAPI.apiPost(userdata, "/userCenter/member/v1/idCardDel", params, platform);
		}
		else if (platform.equals("h5")) {
			result = myAPI.h5Post(userdata, "/userCenter/member/h5/idCardDel", params);
		}
		else {
			System.out.println(platform+"端没有[删除会员身份证信息]接口");
		}
		
		return result;
	}
	
	/*修改实名认证审核状态*/
	public JSONObject updateRealNameAuditStatus(JSONObject sysUserdata,String userId,int auditStatus) {
		JSONObject params = new JSONObject();
		params.put("realNameAuditStatus", auditStatus);//审核状态：1未实名认证、2已实名认证、3实名审核中、4实名不通过、5实名已撤消
		params.put("realNameAuditReason", "实认证审核（接口提交）");//原因
		params.put("userId", userId);//会员ID
		
		return myAPI.sysPost(sysUserdata, "/userCenter/member/realNameAuth/updateRealNameAuditStatus", params);
	}
	
	/*实名认证列表*/
	public JSONObject realNameAuthList(JSONObject sysUserdata) {
		JSONObject params = new JSONObject();
		return myAPI.sysGet(sysUserdata, "/userCenter/member/realNameAuth/realNameAuthList", params);
	}
	
	/*实名认证详情*/
	public JSONObject realNameDetail(JSONObject sysUserdata,String userId) {
		JSONObject params = new JSONObject();
		params.put("userId", userId);
		return myAPI.sysGet(sysUserdata, "/userCenter/member/realNameAuth/realNameDetail", params);
	}

}
