/**
 * @author helen
 * @date 2018年11月12日
 */
package data;

import common.MysqlConnect;
import com.alibaba.fastjson.JSONArray;

/**
 * @Description:商家数据
 */
public class SellerData {
	MysqlConnect mysqlConnect = new MysqlConnect();
	AgentData agentData = new AgentData();
	JSONArray arrayResult = new JSONArray();
	
	/*根据模板名称获取商家的动费模板ID
	 * @seller 商家名称
	 * @TemplateName
	 * */
	public String getTemplateId_name(String seller,String TemplateName) {
		String TemplateId = "";
		String sellerId = this.getValue_account(seller,"supplier_id");
		arrayResult = mysqlConnect.getData("SELECT template_id from p_freight_template WHERE supplier_id="+sellerId+" and name like'%"+TemplateName+"%' ORDER BY template_id DESC LIMIT 1");
		if (arrayResult.size()>0) {
			TemplateId = arrayResult.getJSONObject(0).getString("template_id");
		}
		return TemplateId;
	}
	
	/*根据商家账号，查询字段
	 * @account 商家账号
	 * @key 字段
	 * */
	public String getValue_account(String account,String key) {
		String values = "";
		arrayResult = mysqlConnect.getData("SELECT "+key+" from u_supplier_user where account='"+account+"'");
		if (arrayResult.size()>0) {
			values = arrayResult.getJSONObject(0).getString(key);
		}
		return values;
	}
	
	/*设置商家的分润比例
	 * @seller  商家账号
	 * @profit_percentage  分润比例
	 * */
	public void update_profitPercentage(String seller,String profit_percentage) {
		String sellerId = this.getValue_account(seller,"supplier_id");
		mysqlConnect.updateData("UPDATE u_supplier_user set profit_percentage="+profit_percentage+" where supplier_id="+sellerId);
	}
	
	/*设置商家的邀请人
	 * @account 商家账号
	 * @inviter 邀请人，只能是代理商
	 * */
	public void update_inviter(String inviter,String account) {
		arrayResult = mysqlConnect.getData("SELECT * from u_supplier_user where account='"+account+"' AND recommend_code='"+inviter+"'");
		if (arrayResult.size()==0) {
			String inviterId = agentData.getAgentValue(inviter,"agent_id");
			String inviterName = agentData.getAgentValue(inviter, "real_name");
			mysqlConnect.updateData("UPDATE u_supplier_user SET recommend_id='"+inviterId+"',recommend_code='"+inviter+"',recommend_name='"+inviterName+"' WHERE account='"+account+"'");
		}
	}
	
	/*查询可提现余额大于1000的用户*/
	public String getAccount_abledBalance() {
		String account = "";
		arrayResult = mysqlConnect.getData("SELECT account from u_supplier_user where abled_balance>1000 and password='2d572ee9061abe31bb1e81fa2f654067' ORDER BY supplier_id DESC LIMIT 1");
		if (arrayResult.size()>0) {
			account= arrayResult.getJSONObject(0).getString("account");
		}
		return account;
	}
	
	/*查找最近一个商家账号   u_supplier_user*/
	public String getLastAccount() {
		String account = "";
		arrayResult = mysqlConnect.getData("SELECT account from u_supplier_user where auditor_status=1 and `password`='2d572ee9061abe31bb1e81fa2f654067' ORDER BY supplier_id DESC LIMIT 1;");
		if (arrayResult.size()>0) {
			account = arrayResult.getJSONObject(0).getString("account");
		}
		return account;
	}

}
