/**
 * @author helen
 * @date 2018年9月20日
 */
package data;

import java.util.HashMap;
import java.util.List;
import common.BaseData;
import common.MyRedis;
import common.MysqlConnect;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @Description:会员相关数据
 */
public class MemberData {
	MysqlConnect mysqlConnect = new MysqlConnect();
	BaseData baseData = new BaseData();
	MyRedis myRedis = new MyRedis();
	List<HashMap<String, String>> resultSet;
	JSONArray arrayResult = new JSONArray();
	
	/*查询会员积分表 m_member_score
	 * @key 字段名称
	 * */
	public String getScore_userId(String userId,String key) {
		String value = "";
		arrayResult = mysqlConnect.getData("SELECT "+key+" from m_member_score where user_id="+userId);
		if (arrayResult.size()>0) {
			value = arrayResult.getJSONObject(0).getString(key);
		}
		return value;
	}
	
	/*查询可用豆豆大于M的会员m_member_score
	 * @type 类型：1.福豆 2.乐豆 3.享豆 4.学习豆 ,
	 * @score 比较的豆值
	 * */
	public String getAccount_score(int type,String score) {
		String account = "";
		if (type==1) {
			arrayResult = mysqlConnect.getData("SELECT account from m_member_score where abled_score>"+score+" and account is not NULL ORDER BY score_id DESC LIMIT 1");
		}
		else if (type==2) {
			arrayResult = mysqlConnect.getData("SELECT account from m_member_score where h_score>"+score+" and account is not NULL ORDER BY score_id DESC LIMIT 1");
		}
		else if (type==3) {
			arrayResult = mysqlConnect.getData("SELECT account from m_member_score where abled_retirement_score>"+score+" and account is not NULL ORDER BY score_id DESC LIMIT 1");
		}
		else if (type==4) {
			arrayResult = mysqlConnect.getData("SELECT account from m_member_score where register_score>"+score+" and account is not NULL ORDER BY score_id DESC LIMIT 1");
		}
		else {
			System.out.println("无该类型");
		}
		account = arrayResult.getJSONObject(0).getString("account");
		return account;
	}

	/*根据userId查询签到统计信息，表ms_sign_in_statistics
	 * @userId 用户ID
	 * @key 字段名称
	 * */
	public String getSignStatisticsValue_ByUserId(String userId,String key) {
		String value = "";
		arrayResult = mysqlConnect.getData("SELECT "+key+" from ms_sign_in_statistics where user_id="+userId);
		if (arrayResult.size()>0) {
			value = arrayResult.getJSONObject(0).getString(key);
		}
		return value;
	}
	
	/*根据会员ID和抽奖类型获取抽奖信息，表ms_lucky_draw_record
	 * @userId
	 * @drawType 抽奖类型
	 * @key 字段名称
	 * */
	public String getLuckyDrawValue_ByUserIdAndDrawType(String userId,String drawType,String key) {
		String value ="";
		try {
			arrayResult = mysqlConnect.getData("SELECT "+key+" from ms_lucky_draw_record where user_id="+userId+" and draw_type="+drawType);
			if (arrayResult.size()>0) {
				value = arrayResult.getJSONObject(0).getString(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	/*获取会员当天普通签到获取的福豆数 ，ms_sign_in_record
	 * @userId 用户Id
	 * */
	public Double get_signScore(String userId) {
		Double signScore = 0.0;
		arrayResult = mysqlConnect.getData("SELECT score from ms_sign_in_record where TO_DAYS(sign_time)=TO_DAYS(NOW()) and user_id="+userId);
		if (arrayResult.size()>0) {
			signScore = Double.parseDouble(arrayResult.getJSONObject(0).getString("score"));
		}
		return signScore;
	}

	/*修改会员的补签卡数量  ms_sign_in_statistics
	 * @userId 会员Id
	 * @signCard  补签卡数量
	 * */
	public void update_signCard(String userId,String signCard) {
		mysqlConnect.updateData("UPDATE ms_sign_in_statistics set sign_card="+signCard+" where user_id="+userId);
	}
	
	/*获取最新一个注册时间超过100天的用户   m_member*/
	public JSONArray getAccount_createDay(String days) {
		arrayResult = mysqlConnect.getData("SELECT account from m_member WHERE password='2d572ee9061abe31bb1e81fa2f654067' and status=0 AND (TO_DAYS(NOW())-TO_DAYS(create_date))>"+days+"  ORDER BY user_id desc LIMIT 4");
		return arrayResult;
	}
	
	/*根据实名认证状态，获取会员账号  m_member
	 * @real_name_audit_status 实名审核状态 1.未实名认证  2.已实名认证 3.实名审核中 4.实名不通过 5.实名已撤销
	 * */
	public String getAccount_realNameAuditStatus(String status) {
		arrayResult = mysqlConnect.getData("SELECT account from m_member where real_name_audit_status="+status+" and status=0 and password='2d572ee9061abe31bb1e81fa2f654067' ORDER BY user_id DESC LIMIT 1");
		return arrayResult.getJSONObject(0).getString("account");
	}
	
	/*获取会员信息 m_member
	 * @uid  会员ID
	 * */
	public JSONObject get_userInfo(String uid) {
		JSONObject userInfo = new JSONObject();
		arrayResult = mysqlConnect.getData("SELECT account,nick_name as nickName,sex,mobile,header_img_url as headerImgUrl from m_member where user_id="+uid);
		if (arrayResult.size()>0) {
			userInfo = arrayResult.getJSONObject(0);
		}
		//System.out.println(userInfo);
		return userInfo;
	}
	
	/*获取一个不存在的身份证号：实名认证已审核通过 m_member*/
	public String getIdCardNotExit() {
		String idCard = "";
		for (int i = 0; i < 10; i++) {
			idCard = baseData.getIdCard();
			arrayResult = mysqlConnect.getData("SELECT * from m_member where real_name_audit_status=2 and id_card='"+idCard+"';");
			if (arrayResult.size()==0) {
				break;
			}
		}
		return idCard;
	}
	
	/*获取前number个最新的用户ID m_member*/
	public JSONArray getUserId_LastRegistry(String number) {
		return mysqlConnect.getData("SELECT user_id from m_member ORDER BY user_id DESC LIMIT "+number);
	}
	
	/*获取密码为123456li的用户名,表：m_member
	 * @accountType  根据用户是否存手机号或邮箱：phone、email
	 * */
	public String get_account(String accountType) {
		if (accountType.equals("phone")) {
			arrayResult = mysqlConnect.getData("SELECT account from m_member WHERE password='2d572ee9061abe31bb1e81fa2f654067' and status=0 and email IS NULL ORDER BY user_id desc LIMIT 1");
		}
		else {
			arrayResult = mysqlConnect.getData("SELECT account from m_member WHERE password='2d572ee9061abe31bb1e81fa2f654067' and status=0 and mobile IS NULL ORDER BY user_id desc LIMIT 1 ");
		}
		
		return arrayResult.getJSONObject(0).getString("account");
		
	}

	/*获取一个不存在的用户名，表：m_member
	 * @accountType 会员账号类型：email、phone
	 * */
	public String get_unexitUsername(String accountType) {
		String username = "";
		for (int i = 0; i < 5; i++) {
			if (accountType.equals("phone")) {
				username = baseData.getPhoneNumber();
			}
			else if (accountType.equals("email")) {
				username = String.valueOf(baseData.getNum(1, 99999))+"@foisonagel.com";
			}
			else {
				System.out.println(accountType+"账号类型不存在");
			}
			String field = this.get_fieldByAccount(username, "*");
			if (field=="") {
				break;
			}
		}
		return username;
	}
	
	/*根据会员状态，获取账号(密码为123456li)，（表m_member）
	 * @status  0:正常 1:禁用 2:删除
	 * */
	public String get_accountByStatus(String status) {
		String account = "";
		resultSet = mysqlConnect.selectSql("SELECT account from m_member where password='2d572ee9061abe31bb1e81fa2f654067' and status = "+status+" ORDER BY user_id desc LIMIT 1");
		if (resultSet.size()>0) {
			account = resultSet.get(0).get("account");
		}
		return account;
	}
	
	/*获取未绑定邀请人,并且密码为123456li的会员账号，表：m_member*/
	public String getAccount_noInviter() {
		String account="";
		arrayResult = mysqlConnect.getData("SELECT account from m_member WHERE `password`='2d572ee9061abe31bb1e81fa2f654067' and status=0 and recommend_id is null ORDER BY user_id desc LIMIT 1;");
		if (arrayResult.size()>0) {
			account = arrayResult.getJSONObject(0).getString("account");
		}
		return account;
	}
	
	/*设置会员邀请关系，m_member
	 * @member  会员账号
	 * @inviter 邀请人账号
	 * 备注：会员体系改版后，m_member中的邀请类型只有会员邀请会员
	 * */
	public void update_Inviter(String member,String inviter) {
		resultSet = mysqlConnect.selectSql("SELECT recommend_name from m_member where account='"+member+"'");
		if (resultSet.size()==0) {//如果消费者不存在返回false
			System.out.println("消费者账号不存在");
		}
		else {//消费者存在，检查邀请人
			String recommend_name=resultSet.get(0).get("recommend_name");
			if (recommend_name!=inviter) {
				String inviterId = this.get_fieldByAccount(inviter,"user_id");
				mysqlConnect.updateData("UPDATE m_member SET recommend_id="+inviterId+",recommend_name='"+inviter+"',recommend_type='member' where account='"+member+"'");
				
			}
		}
	}
	
	/*根据账号获取相应的字段，表：m_member
	 * @account  用户账号
	 * @fieldName  字段名称
	 * */
	public String get_fieldByAccount(String account,String fieldName) {
		String field = "";
		arrayResult = mysqlConnect.getData("SELECT "+fieldName+" from m_member WHERE account='"+account+"'");
		if (arrayResult.size()>0) {
			field = arrayResult.getJSONObject(0).getString(fieldName);
		}
		return field;
	}
	
	/*获取验证码，表pub_email_send_record、pub_sms_send_record
	 * @msgType email,phone
	 * */
	public String getVerificationCode(String account,String msgType) {
		String code = "";
		
		try {
			//根据消息类型，查不同的表
			Thread.sleep(1000);
			if (msgType.equals("email")) {
				arrayResult=mysqlConnect.getData("SELECT message FROM `pub_email_send_record` where email='"+account+"' ORDER BY id DESC LIMIT 1");
			}
			else if (msgType.equals("phone")) {
				arrayResult=mysqlConnect.getData("SELECT message FROM `pub_sms_send_record` where mobile like'%"+account+"%' ORDER BY id DESC LIMIT 1");
			}
			//正则式获取验证码
			if (arrayResult.size()>0) {
				code = baseData.getTargetList(arrayResult.getJSONObject(0).get("message").toString(), "\\d+").get(0);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return code;
	}

	/*判定会员是否领取了优惠券,表m_coupons_get_use_record
	 * @account 户账号
	 * @couponId 优惠券Id
	 * */
	public boolean couponExit(String account,String couponId) {
		boolean exit=true;
		String userId = this.get_fieldByAccount(account, "user_id");
		resultSet = mysqlConnect.selectSql("SELECT * from m_coupons_get_use_record where user_id="+userId+" and coupon_id="+couponId);
		if (resultSet.size()==0) {
			exit=false;
		}
		return exit;
	}
	
	/*修改用户的可用福豆
	 * @account 用户账号
	 * @ableScore  可用福豆
	 * */
	public void update_ableScore(String account,String ableScore) {
		mysqlConnect.updateData("UPDATE m_member_score SET abled_score="+ableScore+" where account='"+account+"'");
	}
	
	/*通过存储过程生成用户一段时间内的签到记录
	 * @userId 用户ID
	 * @beginTime 开始时间
	 * @endTime  结束时间
	 * */
	public void insert_sign(String userId,String beginTime,String endTime) {
		mysqlConnect.updateData("CALL Pro_RepairSignIn("+userId+",STR_TO_DATE('"+beginTime+"','%Y-%m-%d'),STR_TO_DATE('"+endTime+"','%Y-%m-%d'));");
	}
	
	/*根据用户ID、签到时间，修改[昨天签到标志]
	 * @userId 
	 * @signTime 签到时间
	 * */
	public void update_signTag(String userId,String signTime) {
		mysqlConnect.updateData("UPDATE ms_sign_in_record set sign_tag=0 where user_id="+userId+" AND sign_time='"+signTime+"'");
	}
	
	/*删除用户的签到数据
	 * @userId  会员ID
	 * */
	public void delete_signRecord(String userId) {
		//删除签到详细记录
		mysqlConnect.updateData("DELETE from ms_sign_in_record where user_id="+userId);
		//删除签到统计记录
		mysqlConnect.updateData("DELETE from ms_sign_in_statistics where user_id="+userId);
		//删除中奖记录
		mysqlConnect.updateData("DELETE from ms_lucky_draw_record WHERE user_id="+userId);
	}

	/*获了登录密码*/
	public String get_password(String username) {
		arrayResult = mysqlConnect.getData("SELECT `password` from m_member_login where account='"+username+"'");
		return arrayResult.getJSONObject(0).getString("password");
	}
}
