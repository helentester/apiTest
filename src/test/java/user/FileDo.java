/**
 * @author helen
 * @date 2019年2月28日
 */
package user;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.FileBody;

import com.alibaba.fastjson.JSONObject;
import com.google.common.io.FileBackedOutputStream;

import common.BaseData;
import common.MyAPI;

/**
 * @Description:文件处理
 */
public class FileDo {
	BaseData baseData = new BaseData();
	MyAPI myAPI = new MyAPI();
	
	public JSONObject uploadFile(JSONObject userdata) {
		String localFileName = "D:/eclipse-workspace/apiTest/target/test-classes/testFiles/goodMainImage.jpg";
		JSONObject result = new JSONObject();
		try {
			JSONObject params = new JSONObject();
			File file = new File(localFileName);
			params.put("file", new FileBody(file,"image/jpeg"));
			//params.put("filename", "goodMainImage.jpg");
			params.put("Content-Type", "image/jpeg");
			//params.put("name", "file");
			params.put("filePath", "user/leaseContract/");
			//params.put("uploadUUID", "2751220715237");
			
			System.out.println(params);
			result = myAPI.sysPost_(userdata, "/userCenter/user/fileUpload", params);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return result;
	}

}
