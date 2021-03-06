/**
 * @author helen
 * @date 2018年6月21日
 */
package common;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
/**
 * @Description:数据处理基类
 */
public class BaseData {
	
	public BaseData() {
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BaseData baseData = new BaseData();

		System.out.println(baseData.getIdCard());

	}
	
	/* 生成MD5密文
	 * @s	MD5明文
	 * */
	public String getMD5(String s) {
		return DigestUtils.md5Hex(s);
	}

	/* 随机生成身份证号 */
	public String getIdCard() {
		IdCardGenerator idCardGenerator = new IdCardGenerator();
		return idCardGenerator.generate();
	}

	/* 随机生成手机号 */
	public String getPhoneNumber() {
		// 被拿出的号段：166,199,198,149(阿里接口不支持该号段)
		String[] telFirst = "133,153,173,177,180,181,189,130,131,132,145,155,156,171,175,176,185,186,134,135,136,137,138,139,147,150,151,152,157,158,159,178,182,183,184,187,188,170"
				.split(",");
		int index = getNum(0, telFirst.length - 1);
		String first = telFirst[index];
		String second = String.valueOf(getNum(1, 888) + 10000).substring(1);
		String third = String.valueOf(getNum(1, 9100) + 10000).substring(1);
		return first + second + third;
	}

	/* 返回某个范围内的整数 */
	public int getNum(int start, int end) {
		return (int) (Math.random() * (end - start + 1) + start);
	}

	/* 返回某个范围的Double*/
	public double getDouble(final double min, final double max) {
		return min + ((max - min) * new Random().nextDouble());
	}
	
	/* 返回某个范围的float*/
	public float getFloat(float min,float max) {
		float f = min + ((max - min) * new Random().nextFloat());
		return (float)(Math.round(f*100))/100;//返回两位小数，如果要求精确4位就*10000然后/10000
	}

	/* 通过正则表达式匹配，返回结果列表 
	 * baseData.getTargetList("¥24.08", "(\\d+(\\.\\d+)?)")   提取小数，返回[24.08]
	 * baseData.getTargetList("http://pctest.wufu360.com:8031/cart/checkstand?cartId=6933,6932", "\\d+")  提取正整数 [360, 8031, 6933, 6932]
	 * baseData.getTargetList("http://pctest.wufu360.com:8031/cart/checkstand?cartId=6933,6932", "(?<=cartId=)([.\\S\\s]*)(?=)").get(0)   提得cartId=后面的值：6933,6932
	 * */
	public List<String> getTargetList(String matcherStr, String compileStr) {
		List<String> targetList = new ArrayList<String>();
		Pattern p = Pattern.compile(compileStr);// 规则
		Matcher m = p.matcher(matcherStr);
		while (m.find()) {
			targetList.add(m.group());// 把匹配到的结果存到列表中
		}
		return targetList;
	}

	/* 生成时间:根据当前月份加减 
	 * @months	加减的月份
	 * @day	加减的天数
	 * @hour	加减的小时数
	 * @dateFormat	返回的时间格式：yyyy-MM-dd HH:mm:ss	yyyy-MM-dd  yyyy-MM-dd'T'HH:mm:ss'Z'
	 * */
	public String getTime(int months, int day,int hour,String dateFormat) {
		Calendar calendar = this.getTime(months,0, day, hour);
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);//设置时间格式
		//System.out.println(calendar.getTimeInMillis());
		return sdf.format(calendar.getTime());
	}
	
	/*根据day返回时间戳10位
	 * @day  加减天数
	 * */
	public long getTimeStamp(int day) {
		Calendar calendar = this.getTime(0,0, day, 0);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置时间格式
		String time = sdf.format(calendar.getTime());
		try {
			Date date = sdf.parse(time);
			long ts = date.getTime() / 1000;
			return ts;
		} catch (Exception e) {
			return 0;
		}
	}
	
	/*根据day返回时间戳13位
	 * @day  加减天数
	 * */
	public long getTimeStamp13(int day) {
		Calendar calendar = this.getTime(0,0, day, 0);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置时间格式
		String time = sdf.format(calendar.getTime());
		try {
			Date date = sdf.parse(time);
			long ts = date.getTime();
			return ts;
		} catch (Exception e) {
			return 0;
		}
	}
	
	/**
	 * 时间戳转时间(11位时间戳)
	 * @param time
	 * @return
	 */
	public static String timestampToDate(long time) {
	    String dateTime;
	    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    dateTime = simpleDateFormat.format(new Date(time * 1000L));
	    return dateTime;
	}
	
	/*获取时间
	 * @weeks  周数
	 * @dayOfWeek 一周中的第几天
	 * @dateFormat	返回的时间格式：yyyy-MM-dd HH:mm:ss	yyyy-MM-dd  yyyy-MM-dd'T'HH:mm:ss'Z'
	 * */
	public String getTimeByWeek(int weeks,int dayOfWeek,String dateFormat) {
		Calendar date = Calendar.getInstance();// 使用默认时区和语言环境获得一个日历。
		date.add(Calendar.WEEK_OF_YEAR, weeks);
		date.set(Calendar.DAY_OF_WEEK, dayOfWeek);
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);//设置时间格式
		return sdf.format(date.getTime());
	}
	
	/*获取时间
	 * @targetDate  目标时间
	 * @days 相隔目标时间的天数
	 * @dateFormat	返回的时间格式：yyyy-MM-dd HH:mm:ss	yyyy-MM-dd  yyyy-MM-dd'T'HH:mm:ss'Z'
	 * */
	public String getTimeByWeek(int weeks,int dayOfWeek,int days,String dateFormat) {
		Calendar date = Calendar.getInstance();// 使用默认时区和语言环境获得一个日历。
		date.add(Calendar.WEEK_OF_YEAR, weeks);
		date.set(Calendar.DAY_OF_WEEK, dayOfWeek);
		date.add(Calendar.DAY_OF_YEAR, days);
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);//设置时间格式
		return sdf.format(date.getTime());
	}
	
	/*获取时间
	 * @days  天数
	 * @dateFormat	返回的时间格式：yyyy-MM-dd HH:mm:ss	yyyy-MM-dd  yyyy-MM-dd'T'HH:mm:ss'Z'
	 * */
	public String getTimeByDays(int days,String dateFormat) {
		Calendar calendar = this.getTime(0, 0, days, 0);
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);//设置时间格式
		return sdf.format(calendar.getTime());
	}
	
	/*获取时间
	 * @months	加减的月份
	 * @day	加减的天数
	 * @hour	加减的小时数
	 * */
	public Calendar getTime(int months,int weeks,int day,int hour) {
		Calendar calendar = Calendar.getInstance();// 使用默认时区和语言环境获得一个日历。
		calendar.add(Calendar.MONTH, months);// 当前时间加减月份
		calendar.add(Calendar.WEEK_OF_MONTH, weeks);
		calendar.add(Calendar.DAY_OF_MONTH, day);//当前时间加减天数
		calendar.add(Calendar.HOUR_OF_DAY, hour);//当前时间加减小时
		return calendar;
	}

	/* 根据相对路径获取文件的绝对路径 */
	public String getFilePath(String path) {
		File file = new File(path);
		String filePath = "";
		try {
			filePath = file.getCanonicalPath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filePath;
	}

}
