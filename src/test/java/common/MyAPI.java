/**
 * @author helen
 * @date 2018年9月20日
 */
package common;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.wufumall.safety.util.ParamSignUtils;
import com.alibaba.fastjson.JSONObject;

/**
 * @Description:项目公共接口处理类
 */
public class MyAPI {
	BaseData baseData = new BaseData();
	MyConfig myConfig = new MyConfig();	
	
	private CloseableHttpClient getHttpCilent() {
		String useFiddler = myConfig.getPropertyValue("fiddler");
		RequestConfig config = RequestConfig.DEFAULT;
		if (useFiddler.equals("on")) {
			HttpHost proxy = new HttpHost("localhost", 8888, "http");  
			config = RequestConfig.custom().setProxy(proxy).build(); 
		}
		CloseableHttpClient httpCilent= HttpClientBuilder.create().setDefaultRequestConfig(config).build();
		return httpCilent;
	}

	/*
	 * 运营中心，不携带登录信息的post请求
	 * 
	 * @uri 接口地址
	 * 
	 * @params json参数
	 */
	public JSONObject sysPost(String uri, JSONObject params) {
		JSONObject jsonResult = new JSONObject();
		try {
			StringEntity entity = new StringEntity(params.toString(), "UTF-8");// 这里需要设置编码，否则会出现中文乱码

			HttpUriRequest httpUriRequest = RequestBuilder.post()
					.setUri(new URI(myConfig.getKeys("YY_domainName") + uri))
					.addHeader("Content-Type", "application/json")// 设置JSON格式
					.addHeader("channelId", "pc") // 渠道来源
					.addHeader("requestId", "0c922fe9-f507-4314-8715-11b9b44c08tr") // 保证每次请求都是唯一
					.addHeader("timestamp", String.valueOf(System.currentTimeMillis() / 1000)) // 当前时间戳,
					.addHeader("userType", "backend").setEntity(entity) // 需要传的参数
					.build();
			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
			jsonResult = JSONObject.parseObject(strResult);

		} catch (Exception e) {
			System.out.println("执行 post接口失败");
			e.printStackTrace();
		}
		return jsonResult;
	}

	/*
	 * 运营中心，不携带登录信息的post请求
	 * 
	 * @uri 接口地址
	 * 
	 * @params json参数
	 */
	public JSONObject sysPost(JSONObject sysUserdata, String uri, JSONObject params) {
		JSONObject jsonResult = new JSONObject();
		try {
			StringEntity entity = new StringEntity(params.toString(), "UTF-8");// 这里需要设置编码，否则会出现中文乱码
			String uuid = UUID.randomUUID().toString();
			//System.out.println(uuid);
			HttpUriRequest httpUriRequest = RequestBuilder.post()
					.setUri(new URI(myConfig.getKeys("YY_domainName") + uri))
					.addHeader("Content-Type", "application/json")// 设置JSON格式
					.addHeader("channelId", "pc") // 渠道来源
					.addHeader("requestId", uuid) // 使用UUID,保证每次请求都是唯一
					.addHeader("timestamp", String.valueOf(System.currentTimeMillis() / 1000)) // 当前时间戳,
					.addHeader("userType", "backend").addHeader("token", sysUserdata.getString("token"))
					.addHeader("loginId", sysUserdata.getString("loginId"))
					.addHeader("Cookie",
							"token=" + sysUserdata.getString("token") + "; loginId=" + sysUserdata.getString("loginId"))
					.setEntity(entity) // 需要传的参数
					.build();
			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
			jsonResult = JSONObject.parseObject(strResult);

		} catch (Exception e) {
			System.out.println("执行 post接口失败");
			e.printStackTrace();
		}
		return jsonResult;
	}
	
	public JSONObject sysPost_(JSONObject sysUserdata, String uri, JSONObject params) {
		JSONObject jsonResult = new JSONObject();
		try {
			StringEntity entity = new StringEntity(params.toString(), "UTF-8");// 这里需要设置编码，否则会出现中文乱码
			String uuid = UUID.randomUUID().toString();
			//System.out.println(uuid);
			HttpUriRequest httpUriRequest = RequestBuilder.post()
					.setUri(new URI(myConfig.getKeys("YY_domainName") + uri))
					.addHeader("Accept", "*/*")// 设置JSON格式
					.addHeader("Content-Type","multipart/form-data")
					.addHeader("channelId", "pc") // 渠道来源
					.addHeader("requestId", uuid) // 使用UUID,保证每次请求都是唯一
					.addHeader("timestamp", String.valueOf(System.currentTimeMillis() / 1000)) // 当前时间戳,
					.addHeader("userType", "backend").addHeader("token", sysUserdata.getString("token"))
					.addHeader("loginId", sysUserdata.getString("loginId"))
					.addHeader("Cookie",
							"token=" + sysUserdata.getString("token") + "; loginId=" + sysUserdata.getString("loginId"))
					.setEntity(entity) // 需要传的参数
					.build();
			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
			jsonResult = JSONObject.parseObject(strResult);

		} catch (Exception e) {
			System.out.println("执行 post接口失败");
			e.printStackTrace();
		}
		return jsonResult;
	}

	/*
	 * 运营中心，get请求
	 * 
	 * @uri 接口地址
	 * 
	 * @params json参数
	 */
	public JSONObject sysGet(JSONObject sysUserdata, String uri, JSONObject params) {
		JSONObject jsonResult = new JSONObject();
		try {
			String uuid = UUID.randomUUID().toString();
			URIBuilder uriBuilder = new URIBuilder(new URI(myConfig.getKeys("YY_domainName") + uri));
			for (String key : params.keySet()) {
				uriBuilder.addParameter(key.toString(), params.getString(key));
			}
			HttpUriRequest httpUriRequest = new HttpGet(uriBuilder.build());
			httpUriRequest.addHeader("Content-Type", "application/json");// 设置JSON格式
			httpUriRequest.addHeader("channelId", "pc"); // 渠道来源
			httpUriRequest.addHeader("requestId", uuid); // 使用UUID,保证每次请求都是唯一
			httpUriRequest.addHeader("timestamp", String.valueOf(System.currentTimeMillis() / 1000)); // 当前时间戳,
			httpUriRequest.addHeader("userType", "backend");
			httpUriRequest.addHeader("token", sysUserdata.getString("token"));
			httpUriRequest.addHeader("loginId", sysUserdata.getString("loginId"));
			httpUriRequest.addHeader("Cookie",
					"token=" + sysUserdata.getString("token") + "; loginId=" + sysUserdata.getString("loginId"));
			
			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
			jsonResult = JSONObject.parseObject(strResult);

		} catch (Exception e) {
			System.out.println("执行 get接口失败");
			e.printStackTrace();
		}
		return jsonResult;
	}

	/* 运营中心，不携带参数的get请求*/
	public JSONObject sysGet(JSONObject sysUserdata, String uri) {
		JSONObject jsonResult = new JSONObject();
		try {
			String uuid = UUID.randomUUID().toString();
			HttpUriRequest httpUriRequest = RequestBuilder.get()
					.setUri(new URI(myConfig.getKeys("YY_domainName") + uri))
					.addHeader("Content-Type", "application/json")// 设置JSON格式
					.addHeader("channelId", "pc") // 渠道来源
					.addHeader("requestId", uuid) // 使用UUID,保证每次请求都是唯一
					.addHeader("timestamp", String.valueOf(System.currentTimeMillis() / 1000)) // 当前时间戳,
					.addHeader("userType", "backend").addHeader("token", sysUserdata.getString("token"))
					.addHeader("loginId", sysUserdata.getString("loginId"))
					.addHeader("Cookie",
							"token=" + sysUserdata.getString("token") + "; loginId=" + sysUserdata.getString("loginId"))
					.build();

			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
			jsonResult = JSONObject.parseObject(strResult);

		} catch (Exception e) {
			System.out.println("执行 post接口失败");
			e.printStackTrace();
		}
		return jsonResult;
	}

	/* 运营中心，不携带登录信息的get请求 */
	public JSONObject sysGet(String uri) {
		JSONObject jsonResult = new JSONObject();
		try {
			HttpUriRequest httpUriRequest = RequestBuilder.get()
					.setUri(new URI(myConfig.getKeys("YY_domainName") + uri))
					.addHeader("Content-Type", "application/json")// 设置JSON格式
					.addHeader("channelId", "pc") // 渠道来源
					.addHeader("requestId", UUID.randomUUID().toString()) // 保证每次请求都是唯一
					.addHeader("timestamp", String.valueOf(System.currentTimeMillis() / 1000)) // 当前时间戳,
					.addHeader("userType", "backend").build();
			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());
			jsonResult = JSONObject.parseObject(strResult);
		} catch (Exception e) {
			System.out.println("执行运营中心的get接口失败");
			e.printStackTrace();
		}

		return jsonResult;
	}
	
	/*代理中心POS请求*/
	public JSONObject agentPost(String uri,JSONObject params) {
		JSONObject jsonResult = new JSONObject();
		try {
			StringEntity entity = new StringEntity(params.toString(), "UTF-8");
			
			HttpUriRequest httpUriRequest = RequestBuilder.post()
					.setUri(new URI(myConfig.getKeys("DL_domainName") + uri))
					.addHeader("Content-Type", "application/json")// 设置JSON格式
					.addHeader("channelId", "pc") // 渠道来源
					.addHeader("requestId", UUID.randomUUID().toString()) // 保证每次请求都是唯一
					.addHeader("timestamp", String.valueOf(System.currentTimeMillis() / 1000)) // 当前时间戳,
					.addHeader("userType","agent")
					.setEntity(entity)
					.build();
			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
			jsonResult = JSONObject.parseObject(strResult);
		} catch (Exception e) {
			System.out.println("执行代理中心 post接口失败");
			e.printStackTrace();
		}
		
		return jsonResult;
	}
	
	/*代理中心POS请求(登录)*/
	public JSONObject agentPost(JSONObject agentUserdata,String uri,JSONObject params) {
		JSONObject jsonResult = new JSONObject();
		try {
			StringEntity entity = new StringEntity(params.toString(), "UTF-8");
			
			HttpUriRequest httpUriRequest = RequestBuilder.post()
					.setUri(new URI(myConfig.getKeys("sj_domainName") + uri))
					.addHeader("Content-Type", "application/json")// 设置JSON格式
					.addHeader("channelId", "pc") // 渠道来源
					.addHeader("requestId", UUID.randomUUID().toString()) // 保证每次请求都是唯一
					.addHeader("timestamp", String.valueOf(System.currentTimeMillis() / 1000)) // 当前时间戳,
					.addHeader("userType",agentUserdata.getString("userType"))
					.addHeader("token", agentUserdata.getString("token"))
					.addHeader("loginId", agentUserdata.getString("loginId"))
					.addHeader("Cookie",
							"token=" + agentUserdata.getString("token") + "; loginId=" + agentUserdata.getString("loginId"))
					.setEntity(entity)
					.build();
			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
			jsonResult = JSONObject.parseObject(strResult);
		} catch (Exception e) {
			System.out.println("执行代理中心 post接口失败");
			e.printStackTrace();
		}
		
		return jsonResult;
	}
	
	/*商家中心 post请求*/
	public JSONObject sellerPost(String uri,JSONObject params) {
		JSONObject jsonResult = new JSONObject();
		try {
			StringEntity entity = new StringEntity(params.toString(), "UTF-8");
			
			HttpUriRequest httpUriRequest = RequestBuilder.post()
					.setUri(new URI(myConfig.getKeys("sj_domainName") + uri))
					.addHeader("Content-Type", "application/json")// 设置JSON格式
					.addHeader("channelId", "pc") // 渠道来源
					.addHeader("requestId", UUID.randomUUID().toString()) // 保证每次请求都是唯一
					.addHeader("timestamp", String.valueOf(System.currentTimeMillis() / 1000)) // 当前时间戳,
					.addHeader("userType","supplier")
					.setEntity(entity)
					.build();
			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
			jsonResult = JSONObject.parseObject(strResult);
		} catch (Exception e) {
			System.out.println("执行商家中心 post接口失败");
			e.printStackTrace();
		}
		
		return jsonResult;
	}
	
	/*商家中心 post请求*/
	public JSONObject sellerPost(JSONObject sellerUserdata,String uri,JSONObject params) {
		JSONObject jsonResult = new JSONObject();
		try {
			StringEntity entity = new StringEntity(params.toString(), "UTF-8");
			
			HttpUriRequest httpUriRequest = RequestBuilder.post()
					.setUri(new URI(myConfig.getKeys("sj_domainName") + uri))
					.addHeader("Content-Type", "application/json")// 设置JSON格式
					.addHeader("channelId", "pc") // 渠道来源
					.addHeader("requestId", UUID.randomUUID().toString()) // 保证每次请求都是唯一
					.addHeader("timestamp", String.valueOf(System.currentTimeMillis() / 1000)) // 当前时间戳,
					.addHeader("userType",sellerUserdata.getString("userType"))
					.addHeader("token", sellerUserdata.getString("token"))
					.addHeader("loginId", sellerUserdata.getString("loginId"))
					.addHeader("Cookie",
							"token=" + sellerUserdata.getString("token") + "; loginId=" + sellerUserdata.getString("loginId"))
					.setEntity(entity)
					.build();
			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
			jsonResult = JSONObject.parseObject(strResult);
		} catch (Exception e) {
			System.out.println("执行商家中心 post接口失败");
			e.printStackTrace();
		}
		
		return jsonResult;
	}
	
	/*商家中心get请求*/
	public JSONObject sellerGet(JSONObject sellerUserdata, String uri) {
		JSONObject jsonResult = new JSONObject();
		try {
			String uuid = UUID.randomUUID().toString();
			HttpUriRequest httpUriRequest = RequestBuilder.get()
					.setUri(new URI(myConfig.getKeys("sj_domainName") + uri))
					.addHeader("Content-Type", "application/json")// 设置JSON格式
					.addHeader("channelId", "pc") // 渠道来源
					.addHeader("requestId", uuid) // 使用UUID,保证每次请求都是唯一
					.addHeader("timestamp", String.valueOf(System.currentTimeMillis() / 1000)) // 当前时间戳,
					.addHeader("userType", sellerUserdata.getString("userType"))
					.addHeader("token", sellerUserdata.getString("token"))
					.addHeader("loginId", sellerUserdata.getString("loginId"))
					.addHeader("Cookie",
							"token=" + sellerUserdata.getString("token") + "; loginId=" + sellerUserdata.getString("loginId"))
					.build();

			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
			jsonResult = JSONObject.parseObject(strResult);

		} catch (Exception e) {
			System.out.println("执行 post接口失败");
			e.printStackTrace();
		}
		return jsonResult;
	}
	
	/*商家中心get请求*/
	public JSONObject sellerGet(JSONObject sellerUserdata, String uri, JSONObject params) {
		JSONObject jsonResult = new JSONObject();
		try {
			String uuid = UUID.randomUUID().toString();
			URIBuilder uriBuilder = new URIBuilder(new URI(myConfig.getKeys("sj_domainName") + uri));
			for (String key : params.keySet()) {
				uriBuilder.addParameter(key.toString(), params.getString(key));
			}
			HttpUriRequest httpUriRequest = new HttpGet(uriBuilder.build());
			httpUriRequest.addHeader("Content-Type", "application/json");// 设置JSON格式
			httpUriRequest.addHeader("channelId", "pc"); // 渠道来源
			httpUriRequest.addHeader("requestId", uuid); // 使用UUID,保证每次请求都是唯一
			httpUriRequest.addHeader("timestamp", String.valueOf(System.currentTimeMillis() / 1000)); // 当前时间戳,
			httpUriRequest.addHeader("userType", sellerUserdata.getString("userType"));
			httpUriRequest.addHeader("token", sellerUserdata.getString("token"));
			httpUriRequest.addHeader("loginId", sellerUserdata.getString("loginId"));
			httpUriRequest.addHeader("Cookie",
					"token=" + sellerUserdata.getString("token") + "; loginId=" + sellerUserdata.getString("loginId"));
			
			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
			jsonResult = JSONObject.parseObject(strResult);

		} catch (Exception e) {
			System.out.println("执行商家中心get接口失败");
			e.printStackTrace();
		}
		return jsonResult;
	}
	
	/*商家中心delete请求*/
	public JSONObject sellerDelete(JSONObject sellerUserdata, String uri,JSONObject params) {
		JSONObject jsonResult = new JSONObject();
		try {
			StringEntity entity = new StringEntity(params.toString(), "UTF-8");
			HttpUriRequest httpUriRequest = RequestBuilder.delete()
					.setUri(new URI(myConfig.getKeys("sj_domainName") + uri))
					.addHeader("Content-Type", "application/json")// 设置JSON格式
					.addHeader("channelId", "pc") // 渠道来源
					.addHeader("requestId", UUID.randomUUID().toString()) // 保证每次请求都是唯一
					.addHeader("timestamp", String.valueOf(System.currentTimeMillis() / 1000)) // 当前时间戳,
					.addHeader("userType",sellerUserdata.getString("userType"))
					.addHeader("token", sellerUserdata.getString("token"))
					.addHeader("loginId", sellerUserdata.getString("loginId"))
					.addHeader("Cookie",
							"token=" + sellerUserdata.getString("token") + "; loginId=" + sellerUserdata.getString("loginId"))
					.setEntity(entity) // 需要传的参数
					.build();
			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
			jsonResult = JSONObject.parseObject(strResult);
		} catch (Exception e) {
			System.out.println("执行 post接口失败");
			e.printStackTrace();
		}
		return jsonResult;
	}
	
	/*康养商家中心 post请求*/
	public JSONObject KYSellerPost(String uri,JSONObject params) {
		JSONObject jsonResult = new JSONObject();
		try {
			StringEntity entity = new StringEntity(params.toString(), "UTF-8");
			
			HttpUriRequest httpUriRequest = RequestBuilder.post()
					.setUri(new URI(myConfig.getKeys("kysj_domainName") + uri))
					.addHeader("Content-Type", "application/json")// 设置JSON格式
					.addHeader("channelId", "pc") // 渠道来源
					.addHeader("requestId", UUID.randomUUID().toString()) // 保证每次请求都是唯一
					.addHeader("timestamp", String.valueOf(System.currentTimeMillis() / 1000)) // 当前时间戳,
					.addHeader("userType","hotelSupplier")
					.setEntity(entity)
					.build();
			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
			jsonResult = JSONObject.parseObject(strResult);
		} catch (Exception e) {
			System.out.println("执行商家中心 post接口失败");
			e.printStackTrace();
		}
		
		return jsonResult;
	}
	
	/*康养商家中心 post请求*/
	public JSONObject KYSellerPost(JSONObject sellerUserdata,String uri,JSONObject params) {
		JSONObject jsonResult = new JSONObject();
		try {
			StringEntity entity = new StringEntity(params.toString(), "UTF-8");
			
			HttpUriRequest httpUriRequest = RequestBuilder.post()
					.setUri(new URI(myConfig.getKeys("kysj_domainName") + uri))
					.addHeader("Content-Type", "application/json")// 设置JSON格式
					.addHeader("channelId", "pc") // 渠道来源
					.addHeader("requestId", UUID.randomUUID().toString()) // 保证每次请求都是唯一
					.addHeader("timestamp", String.valueOf(System.currentTimeMillis() / 1000)) // 当前时间戳,
					.addHeader("userType",sellerUserdata.getString("userType"))
					.addHeader("token", sellerUserdata.getString("token"))
					.addHeader("loginId", sellerUserdata.getString("loginId"))
					.addHeader("Cookie",
							"token=" + sellerUserdata.getString("token") + "; loginId=" + sellerUserdata.getString("loginId"))
					.setEntity(entity)
					.build();
			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
			jsonResult = JSONObject.parseObject(strResult);
		} catch (Exception e) {
			System.out.println("执行商家中心 post接口失败");
			e.printStackTrace();
		}
		
		return jsonResult;
	}
	
	/*康养商家中心get请求*/
	public JSONObject KYSellerGet(JSONObject sellerUserdata, String uri, JSONObject params) {
		JSONObject jsonResult = new JSONObject();
		try {
			String uuid = UUID.randomUUID().toString();
			URIBuilder uriBuilder = new URIBuilder(new URI(myConfig.getKeys("kysj_domainName") + uri));
			for (String key : params.keySet()) {
				uriBuilder.addParameter(key.toString(), params.getString(key));
			}
			HttpUriRequest httpUriRequest = new HttpGet(uriBuilder.build());
			httpUriRequest.addHeader("Content-Type", "application/json");// 设置JSON格式
			httpUriRequest.addHeader("channelId", "pc"); // 渠道来源
			httpUriRequest.addHeader("requestId", uuid); // 使用UUID,保证每次请求都是唯一
			httpUriRequest.addHeader("timestamp", String.valueOf(System.currentTimeMillis() / 1000)); // 当前时间戳,
			httpUriRequest.addHeader("userType", sellerUserdata.getString("userType"));
			httpUriRequest.addHeader("token", sellerUserdata.getString("token"));
			httpUriRequest.addHeader("loginId", sellerUserdata.getString("loginId"));
			httpUriRequest.addHeader("Cookie",
					"token=" + sellerUserdata.getString("token") + "; loginId=" + sellerUserdata.getString("loginId"));
			
			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
			jsonResult = JSONObject.parseObject(strResult);

		} catch (Exception e) {
			System.out.println("执行商家中心get接口失败");
			e.printStackTrace();
		}
		return jsonResult;
	}
	
	/*
	 * PC商城的不携带登录信息的post请求公共方法
	 * 
	 * @uri 接口地址
	 * 
	 * @params json参数
	 * 
	 * @userType:member（会员） supplier（商家） agent(代理)
	 */
	public JSONObject PCPost(String uri, JSONObject params) {
		JSONObject jsonResult = new JSONObject();
		try {
			StringEntity entity = new StringEntity(params.toString(), "UTF-8");

			HttpUriRequest httpUriRequest = RequestBuilder.post()
					.setUri(new URI(myConfig.getKeys("PC_domainName") + uri))
					.addHeader("Content-Type", "application/json")// 设置JSON格式
					.addHeader("channelId", "backend") // 渠道来源
					.addHeader("requestId", "0c922fe9-f507-4314-8715-11b9b44c08tr") // 保证每次请求都是唯一
					.addHeader("timestamp", String.valueOf(System.currentTimeMillis() / 1000)) // 当前时间戳,
					.addHeader("userType", "member").setEntity(entity) // 需要传的参数
					.build();
			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
			jsonResult = JSONObject.parseObject(strResult);
		} catch (Exception e) {
			System.out.println("执行PC post接口失败");
			e.printStackTrace();
		}
		return jsonResult;
	}

	/* PC商城的携带登录信息的post请求公共方法 */
	public JSONObject PCPost(JSONObject userdata, String uri, JSONObject params) {
		JSONObject jsonResult = new JSONObject();
		try {
			
			StringEntity entity = new StringEntity(params.toString(), "UTF-8");
			String uuid = UUID.randomUUID().toString();
			
			HttpUriRequest httpUriRequest = RequestBuilder.post()
					.setUri(new URI(myConfig.getKeys("PC_domainName") + uri))
					.addHeader("Content-Type", "application/json")// 设置JSON格式
					.addHeader("channelId", "pc") // 渠道来源
					.addHeader("requestId", uuid) // 保证每次请求都是唯一
					.addHeader("timestamp", String.valueOf(System.currentTimeMillis() / 1000)) // 当前时间戳,
					.addHeader("loginId", userdata.getString("loginId")) // 用户登录ID
					.addHeader("token", userdata.getString("token")) // 登录校验
					.addHeader("userType", userdata.getString("userType"))
					.setEntity(entity) // 需要传的参数
					.build();

			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
			jsonResult = JSONObject.parseObject(strResult);
		} catch (Exception e) {
			System.out.println("执行 post接口失败");
			e.printStackTrace();
		}
		return jsonResult;
	}
	
	/*
	 * get请求
	 * 
	 * @uri 接口地址
	 * 
	 * @params json参数
	 */
	public JSONObject pcGet(JSONObject userdata, String uri, JSONObject params) {
		JSONObject jsonResult = new JSONObject();
		try {
			String uuid = UUID.randomUUID().toString();
			URIBuilder uriBuilder = new URIBuilder(new URI(myConfig.getKeys("PC_domainName") + uri));
			for (String key : params.keySet()) {
				uriBuilder.addParameter(key.toString(), params.getString(key));
			}
			HttpUriRequest httpUriRequest = new HttpGet(uriBuilder.build());
			httpUriRequest.addHeader("Content-Type", "application/json");// 设置JSON格式
			httpUriRequest.addHeader("channelId", "pc"); // 渠道来源
			httpUriRequest.addHeader("requestId", uuid); // 使用UUID,保证每次请求都是唯一
			httpUriRequest.addHeader("timestamp", String.valueOf(System.currentTimeMillis() / 1000)); // 当前时间戳,
			httpUriRequest.addHeader("userType", userdata.getString("userType"));
			httpUriRequest.addHeader("token", userdata.getString("token"));
			httpUriRequest.addHeader("loginId", userdata.getString("loginId"));

			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
			jsonResult = JSONObject.parseObject(strResult);

		} catch (Exception e) {
			System.out.println("执行 post接口失败");
			e.printStackTrace();
		}
		return jsonResult;
	}
	
	/*
	 * get请求
	 * 
	 * @uri 接口地址
	 * 
	 * @params json参数
	 */
	public JSONObject pcGet(String uri, JSONObject params) {
		JSONObject jsonResult = new JSONObject();
		try {
			String uuid = UUID.randomUUID().toString();
			URIBuilder uriBuilder = new URIBuilder(new URI(myConfig.getKeys("PC_domainName") + uri));
			for (String key : params.keySet()) {
				uriBuilder.addParameter(key.toString(), params.getString(key));
			}
			HttpUriRequest httpUriRequest = new HttpGet(uriBuilder.build());
			httpUriRequest.addHeader("Content-Type", "application/json");// 设置JSON格式
			httpUriRequest.addHeader("channelId", "pc"); // 渠道来源
			httpUriRequest.addHeader("requestId", uuid); // 使用UUID,保证每次请求都是唯一
			httpUriRequest.addHeader("timestamp", String.valueOf(System.currentTimeMillis() / 1000)); // 当前时间戳,
			httpUriRequest.addHeader("usertype","member");
			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
			jsonResult = JSONObject.parseObject(strResult);

		} catch (Exception e) {
			System.out.println("执行 post接口失败");
			e.printStackTrace();
		}
		return jsonResult;
	}
	
	/* PC商城携带登录信息的delete请求 */
	public JSONObject pcDelete(JSONObject userdata, String uri,JSONObject params) {
		JSONObject jsonResult = new JSONObject();
		try {
			StringEntity entity = new StringEntity(params.toString(), "UTF-8");
			HttpUriRequest httpUriRequest = RequestBuilder.delete()
					.setUri(new URI(myConfig.getKeys("PC_domainName") + uri))
					.addHeader("Content-Type", "application/json")// 设置JSON格式
					.addHeader("channelId", "pc") // 渠道来源
					.addHeader("requestId", UUID.randomUUID().toString()) // 保证每次请求都是唯一
					.addHeader("timestamp", String.valueOf(System.currentTimeMillis() / 1000)) // 当前时间戳,
					.addHeader("loginId", userdata.getString("loginId")) // 用户登录ID
					.addHeader("token", userdata.getString("token")) // 登录校验
					.addHeader("userType", userdata.getString("userType"))
					.setEntity(entity) // 需要传的参数
					.build();
			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
			jsonResult = JSONObject.parseObject(strResult);
		} catch (Exception e) {
			System.out.println("执行 post接口失败");
			e.printStackTrace();
		}
		return jsonResult;
	}

	/* H5商城post请求 */
	public JSONObject h5Post(String uri, JSONObject params) {
		JSONObject jsonResult = new JSONObject();
		try {
			StringEntity entity = new StringEntity(params.toString(), "UTF-8");// 这里需要设置编码，否则会出现中文乱码
			HttpUriRequest httpUriRequest = RequestBuilder.post()
					.setUri(new URI(myConfig.getKeys("h5_domainName") + uri))
					.addHeader("Content-Type", "application/json")// 设置JSON格式
					.addHeader("requestId", UUID.randomUUID().toString()) // 保证每次请求都是唯一
					.addHeader("timestamp", String.valueOf(System.currentTimeMillis() / 1000)) // 当前时间戳,
					.addHeader("channelId", "h5") // 渠道来源
					.setEntity(entity) // 需要传的参数
					.build();
			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
			jsonResult = JSONObject.parseObject(strResult);
		} catch (Exception e) {
			System.out.println("接口：" + uri + "；响应＝" + jsonResult);
			e.printStackTrace();
		}
		return jsonResult;
	}

	/* H5商城携带登录信息的post请求 */
	public JSONObject h5Post(JSONObject userdata, String uri, JSONObject params) {
		JSONObject jsonResult = new JSONObject();
		try {
			String uuid = UUID.randomUUID().toString();
			String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
			//获取签名
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("channelid", "h5");
	        map.put("loginid",userdata.getString("loginId"));
	        map.put("usertype", "member");
	        map.put("requestid", uuid);
	        map.put("timestamp", timestamp);
	        map.put("token", userdata.getString("token"));
	        for (String key:params.keySet()) {
				map.put(key.toString(), params.get(key));
			}
			String sign = ParamSignUtils.sign(map, "appPayJson");//获取签名
			
			StringEntity entity = new StringEntity(params.toString(), "UTF-8");// 这里需要设置编码，否则会出现中文乱码
			HttpUriRequest httpUriRequest = RequestBuilder.post()
					.setUri(new URI(myConfig.getKeys("h5_domainName") + uri))
					.addHeader("Content-Type", "application/json")// 设置JSON格式
					.addHeader("requestId", uuid) // 保证每次请求都是唯一
					.addHeader("timestamp", timestamp) // 当前时间戳,
					.addHeader("channelId", "h5") // 渠道来源
					.addHeader("sign",sign)//获取签名
					.addHeader("userType", userdata.getString("userType"))
					.addHeader("loginId", userdata.getString("loginId")) // 用户登录ID
					.addHeader("token", userdata.getString("token")) // 登录校验
					.addHeader("Cookie",
							"token=" + userdata.getString("token") + "; loginId=" + userdata.getString("loginId"))
					.setEntity(entity) // 需要传的参数
					.build();
			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
			jsonResult = JSONObject.parseObject(strResult);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonResult;
	}

	/* H5商城携带登录信息的get请求 */
	public JSONObject h5Get(JSONObject userdata, String uri) {
		JSONObject jsonResult = new JSONObject();
		try {
			HttpUriRequest httpUriRequest = RequestBuilder.get()
					.setUri(new URI(myConfig.getKeys("h5_domainName") + uri))
					.addHeader("Content-Type", "application/json")// 设置JSON格式
					.addHeader("Cookie",
							"token=" + userdata.getString("token") + "; loginId=" + userdata.getString("loginId"))
					.build();
			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
			jsonResult = JSONObject.parseObject(strResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonResult;
	}
	
	/*
	 * 运营中心，post请求
	 * 
	 * @uri 接口地址
	 * 
	 * @params json参数
	 */
	public JSONObject h5Get(JSONObject userdata, String uri, JSONObject params) {
		JSONObject jsonResult = new JSONObject();
		try {
			String uuid = UUID.randomUUID().toString();
			URIBuilder uriBuilder = new URIBuilder(new URI(myConfig.getKeys("h5_domainName") + uri));
			for (String key : params.keySet()) {
				uriBuilder.addParameter(key.toString(), params.getString(key));
			}
			HttpUriRequest httpUriRequest = new HttpGet(uriBuilder.build());
			httpUriRequest.addHeader("Content-Type", "application/json");// 设置JSON格式
			httpUriRequest.addHeader("requestId",uuid);
			httpUriRequest.addHeader("timestamp",String.valueOf(System.currentTimeMillis() / 1000)); // 当前时间戳
			httpUriRequest.addHeader("channelId", "h5"); // 渠道来源
			httpUriRequest.addHeader("usertype", "member");
			httpUriRequest.addHeader("loginId", userdata.getString("loginId")); // 用户登录ID
			httpUriRequest.addHeader("token", userdata.getString("token")); // 登录校验
			httpUriRequest.addHeader("Cookie","token=" + userdata.getString("token") + "; loginId=" + userdata.getString("loginId"));

			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
			jsonResult = JSONObject.parseObject(strResult);

		} catch (Exception e) {
			System.out.println("执行 post接口失败");
			e.printStackTrace();
		}
		return jsonResult;
	}
	
	/*
	 * 运营中心，post请求
	 * 
	 * @uri 接口地址
	 * 
	 * @params json参数
	 */
	public JSONObject h5Get(String uri, JSONObject params) {
		JSONObject jsonResult = new JSONObject();
		try {
			String uuid = UUID.randomUUID().toString();
			URIBuilder uriBuilder = new URIBuilder(new URI(myConfig.getKeys("h5_domainName") + uri));
			for (String key : params.keySet()) {
				uriBuilder.addParameter(key.toString(), params.getString(key));
			}
			HttpUriRequest httpUriRequest = new HttpGet(uriBuilder.build());
			httpUriRequest.addHeader("Content-Type", "application/json");// 设置JSON格式
			httpUriRequest.addHeader("requestId",uuid);
			httpUriRequest.addHeader("timestamp",String.valueOf(System.currentTimeMillis() / 1000)); // 当前时间戳
			httpUriRequest.addHeader("channelId", "h5"); // 渠道来源
			httpUriRequest.addHeader("usertype", "member");

			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
			jsonResult = JSONObject.parseObject(strResult);

		} catch (Exception e) {
			System.out.println("执行 post接口失败");
			e.printStackTrace();
		}
		return jsonResult;
	}

	/* H5商城携带登录信息的delete请求 */
	public JSONObject h5Delete(JSONObject userdata, String uri,JSONObject params) {
		JSONObject jsonResult = new JSONObject();
		try {
			//发送请求
			URIBuilder uriBuilder = new URIBuilder(new URI(myConfig.getKeys("h5_domainName") + uri));
			for (String key : params.keySet()) {
				uriBuilder.addParameter(key.toString(), params.getString(key));
			}
			HttpUriRequest httpUriRequest = new HttpDelete(uriBuilder.build());
			httpUriRequest.addHeader("Content-Type", "application/json");// 设置JSON格式
			httpUriRequest.addHeader("requestId",UUID.randomUUID().toString());
			httpUriRequest.addHeader("Cookie","token=" + userdata.getString("token") + "; loginId=" + userdata.getString("loginId")); 
			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
			jsonResult = JSONObject.parseObject(strResult);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonResult;
	}

	/*
	 * APP商城post请求
	 * 
	 * @channelId :android、ios
	 */
	public JSONObject apiPost(String uri, JSONObject params, String channelId) {
		JSONObject jsonResult = new JSONObject();
		try {
			StringEntity entity = new StringEntity(params.toString(), "UTF-8");// 这里需要设置编码，否则会出现中文乱码
			HttpUriRequest httpUriRequest = RequestBuilder.post()
					.setUri(new URI(myConfig.getKeys("api_domainName") + uri))
					.addHeader("Content-Type", "application/json")// 设置JSON格式
					.addHeader("requestid", "8eaf210f-70bd-4730-aad8-32f03b61d5a8")
					.addHeader("timestamp", String.valueOf(System.currentTimeMillis() / 1000)) // 当前时间戳
					.addHeader("channelId", channelId) // 渠道来源
					.addHeader("usertype", "member").addHeader("sign", "D0F81E4671FE9EA98356785EBBCEFED2")
					.setEntity(entity).build();
			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());
			jsonResult = JSONObject.parseObject(strResult);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonResult;
	}
	/*
	 * APP商城携带登录信息的post请求
	 * 
	 * @channelId :android、ios
	 */
	public JSONObject apiPost(JSONObject userdata, String uri, JSONObject params, String channelId) {
		JSONObject jsonResult = new JSONObject();
		try {
			String uuid = UUID.randomUUID().toString();
			String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
			//获取签名
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("channelid", channelId);
	        map.put("loginid",userdata.getString("loginId"));
	        map.put("usertype", "member");
	        map.put("requestid", uuid);
	        map.put("timestamp", timestamp);
	        map.put("token", userdata.getString("token"));
	        for (String key:params.keySet()) {
				map.put(key.toString(), params.get(key));
			}
			String sign = ParamSignUtils.sign(map, "appPayJson");//获取签名
			
			StringEntity entity = new StringEntity(params.toString(), "UTF-8");// 这里需要设置编码，否则会出现中文乱码
			HttpUriRequest httpUriRequest = RequestBuilder.post()
					.setUri(new URI(myConfig.getKeys("api_domainName") + uri))
					.addHeader("Content-Type", "application/json")// 设置JSON格式
					.addHeader("requestid", uuid)
					.addHeader("timestamp", timestamp) // 当前时间戳
					.addHeader("channelId", channelId) // 渠道来源
					.addHeader("usertype", "member")
					.addHeader("sign", sign)
					.addHeader("loginId", userdata.getString("loginId")) // 用户登录ID
					.addHeader("token", userdata.getString("token")) // 登录校验
					.setEntity(entity).build();
			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());
			//System.out.println(strResult);
			jsonResult = JSONObject.parseObject(strResult);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonResult;
	}
	
	/* APP商城携带登录信息的get请求 */
	public JSONObject apiGet(JSONObject userdata, String uri, String channelId) {
		JSONObject jsonResult = new JSONObject();
		try {
			HttpUriRequest httpUriRequest = RequestBuilder.get()
					.setUri(new URI(myConfig.getKeys("api_domainName") + uri))
					.addHeader("Content-Type", "application/json")// 设置JSON格式
					.addHeader("requestid", "8eaf210f-70bd-4730-aad8-32f03b61d5a8")
					.addHeader("timestamp", String.valueOf(System.currentTimeMillis() / 1000)) // 当前时间戳
					.addHeader("channelId", channelId) // 渠道来源
					.addHeader("usertype", "member").addHeader("sign", "D0F81E4671FE9EA98356785EBBCEFED2")
					.addHeader("loginId", userdata.getString("loginId")) // 用户登录ID
					.addHeader("token", userdata.getString("token")) // 登录校验
					.build();
			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());
			jsonResult = JSONObject.parseObject(strResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonResult;
	}

	/*APP带参数的get请求*/
	public JSONObject apiGet(JSONObject userdata, String uri, JSONObject params, String channelId) {
		JSONObject jsonResult = new JSONObject();
		try {
			String uuid = UUID.randomUUID().toString();
			String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
			//获取签名
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("channelid", channelId);
	        map.put("loginid",userdata.getString("loginId"));
	        map.put("usertype", "member");
	        map.put("requestid", uuid);
	        map.put("timestamp", timestamp);
	        map.put("token", userdata.getString("token"));
	        for (String key:params.keySet()) {
				map.put(key.toString(), params.get(key));
			}
			String sign = ParamSignUtils.sign(map, "appPayJson");//获取签名
			
			//发送请求
			URIBuilder uriBuilder = new URIBuilder(new URI(myConfig.getKeys("api_domainName") + uri));
			for (String name : params.keySet()) {
				uriBuilder.addParameter(name.toString(), params.getString(name.toString()));
			}
			HttpUriRequest httpUriRequest = new HttpGet(uriBuilder.build());
			httpUriRequest.addHeader("Content-Type", "application/json");// 设置JSON格式
			httpUriRequest.addHeader("channelid", channelId); // 渠道来源
			httpUriRequest.addHeader("requestid", uuid); // 使用UUID,保证每次请求都是唯一
			httpUriRequest.addHeader("timestamp", timestamp); // 当前时间戳,
			httpUriRequest.addHeader("usertype", "member");
			httpUriRequest.addHeader("token", userdata.getString("token"));
			httpUriRequest.addHeader("loginid", userdata.getString("loginId"));
			httpUriRequest.addHeader("sign",sign);

			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
			jsonResult = JSONObject.parseObject(strResult);

		} catch (Exception e) {
			System.out.println("执行 post接口失败");
			e.printStackTrace();
		}
		return jsonResult;
	}
	
	/*APP带参数的get请求*/
	public JSONObject apiGet(String uri, JSONObject params, String channelId) {
		JSONObject jsonResult = new JSONObject();
		try {
			String uuid = UUID.randomUUID().toString();
			String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
			
			//发送请求
			URIBuilder uriBuilder = new URIBuilder(new URI(myConfig.getKeys("api_domainName") + uri));
			for (String name : params.keySet()) {
				uriBuilder.addParameter(name.toString(), params.getString(name.toString()));
			}
			HttpUriRequest httpUriRequest = new HttpGet(uriBuilder.build());
			httpUriRequest.addHeader("Content-Type", "application/json");// 设置JSON格式
			httpUriRequest.addHeader("channelid", channelId); // 渠道来源
			httpUriRequest.addHeader("requestid", uuid); // 使用UUID,保证每次请求都是唯一
			httpUriRequest.addHeader("timestamp", timestamp); // 当前时间戳,
			httpUriRequest.addHeader("usertype", "member");

			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
			jsonResult = JSONObject.parseObject(strResult);

		} catch (Exception e) {
			System.out.println("执行 post接口失败");
			e.printStackTrace();
		}
		return jsonResult;
	}
	
	/* api携带登录信息的delete请求 */
	public JSONObject apiDelete(JSONObject userdata, String uri,JSONObject params,String channelId) {
		JSONObject jsonResult = new JSONObject();
		try {
			String uuid = UUID.randomUUID().toString();
			String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
			//获取签名
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("channelid", channelId);
	        map.put("loginid",userdata.getString("loginId"));
	        map.put("usertype", "member");
	        map.put("requestid", uuid);
	        map.put("timestamp", timestamp);
	        map.put("token", userdata.getString("token"));
	        for (String key:params.keySet()) {
				map.put(key.toString(), params.get(key));
			}
			String sign = ParamSignUtils.sign(map, "appPayJson");//获取签名
			
			//发送请求
			URIBuilder uriBuilder = new URIBuilder(new URI(myConfig.getKeys("api_domainName") + uri));
			for (String name : params.keySet()) {
				uriBuilder.addParameter(name.toString(), params.getString(name.toString()));
			}
			HttpUriRequest httpUriRequest = new HttpDelete(uriBuilder.build());
			httpUriRequest.addHeader("Content-Type", "application/json");// 设置JSON格式
			httpUriRequest.addHeader("channelid", channelId); // 渠道来源
			httpUriRequest.addHeader("requestid", uuid); // 使用UUID,保证每次请求都是唯一
			httpUriRequest.addHeader("timestamp", timestamp); // 当前时间戳,
			httpUriRequest.addHeader("usertype", "member");
			httpUriRequest.addHeader("token", userdata.getString("token"));
			httpUriRequest.addHeader("loginid", userdata.getString("loginId"));
			httpUriRequest.addHeader("sign",sign);
			HttpResponse httpResponse = this.getHttpCilent().execute(httpUriRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
			jsonResult = JSONObject.parseObject(strResult);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonResult;
	}
	
	
}
