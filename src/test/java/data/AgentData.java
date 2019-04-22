/**
 * @author helen
 * @date 2018年11月12日
 */
package data;

import common.MysqlConnect;
import com.alibaba.fastjson.JSONArray;

/**
 * @Description:代理商数据
 */
public class AgentData {
	MysqlConnect mysqlConnect = new MysqlConnect();
	JSONArray arrayResult = new JSONArray();
	
	/*设置代理商的邀请人,u_supermarket_agent_user
	 * @account  代理商账号
	 * @inviter  邀请人账号,只能是代理商
	 * */
	public void update_inviter(String inviter,String account) {
		arrayResult = mysqlConnect.getData("SELECT * from u_supermarket_agent_user where account='"+account+"' AND recommend_name='"+inviter+"'");
		if (arrayResult.size()==0) {
			String inviterId = this.getAgentValue(inviter,"agent_id");
			mysqlConnect.updateData("UPDATE u_supermarket_agent_user set invite='"+inviter+"',recommend_id="+inviterId+" ,recommend_name='"+inviter+"',recommend_code='"+inviter+"',recommend_type='agent' where account='"+account+"'");
		}
		
	}
	
	/*根据代理商账号获取代理商信息,u_supermarket_agent_user
	 * @account 代理商账号
	 * @key 字段名称
	 * */
	public String getAgentValue(String account,String key) {
		String value = "";
		arrayResult = mysqlConnect.getData("SELECT "+key+" from u_supermarket_agent_user where account='"+account+"'");
		if (arrayResult.size()>0) {
			value = arrayResult.getJSONObject(0).getString(key);
		}
		return value;
	}
	
	/*根据信用卡审核状态，获取最近一个代理商账号 u_supermarket_agent_user
	 * @card_auditor_status  '信用卡代理状态：0.未申请；1.审核通过；2.待审核；3.审核不通过；4.已撤销；5.启用；6.禁用',
	 * */
	public String getLastCardAgent(String status) {
		String account = "";
		arrayResult = mysqlConnect.getData("SELECT account from u_supermarket_agent_user where card_auditor_status="+status+" ORDER BY agent_id DESC LIMIT 1");
		if (arrayResult.size()>0) {
			account = arrayResult.getJSONObject(0).getString("account");
		}
		return account;
	}
	
	/*根据康养卡代理审核状态，获取最近一个代理账号
	 *   `safe_auditor_status` tinyint(4) DEFAULT '0' COMMENT '保险代理状态：0.未申请；1.审核通过；2.待审核；3.审核不通过；4.已撤销；5.启用；6.禁用',
	 * */
	public String getLastHeahthAgent(int status) {
		String account = "";
		arrayResult = mysqlConnect.getData("SELECT account from u_supermarket_agent_user where safe_auditor_status="+status+" ORDER BY agent_id DESC LIMIT 1");
		if (arrayResult.size()>0) {
			account = arrayResult.getJSONObject(0).getString("account");
		}
		return account;
	}
	
	/*查找最近一个代理商账号   u_supermarket_agent_user*/
	public String getLastAccount() {
		String account = "";
		arrayResult = mysqlConnect.getData("SELECT account from u_supermarket_agent_user ORDER BY agent_id DESC LIMIT 1;");
		if (arrayResult.size()>0) {
			account = arrayResult.getJSONObject(0).getString("account");
		}
		return account;
	}

}
