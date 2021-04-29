package com.zslin.business.mini.tools;

import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;

/**
 * 与微信平台进行交互的网络处理工具类
 * @author 钟述林
 *
 */
public class InternetTools {

	public static BufferedInputStream doPost(String url, Map<String, Object> params) {
//		URL url = new URL("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token="+accessToken);
		BufferedInputStream bis = null;
		try {
			URL req = new URL(url);
			HttpURLConnection httpURLConnection = (HttpURLConnection) req.openConnection();
			httpURLConnection.setRequestMethod("POST");// 提交模式

			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
			// 发送请求参数
			JSONObject paramJson = new JSONObject();

			Set<String> keys = params.keySet();
			for(String key : keys) {
				paramJson.put(key, params.get(key));
			}

			printWriter.write(paramJson.toString());
			// flush输出流的缓冲
			printWriter.flush();
			//开始获取数据
			/*BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
			String line ;
			while((line=br.readLine())!=null) {
				sb.append(line);
			}
			br.close();
			*/
			bis = new BufferedInputStream(httpURLConnection.getInputStream());
			//printWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return bis;
	}

	/**
	 * 处理get请求
	 * @param serverName url
	 * @param params 参数
	 * @return 返回结果
	 */
	public static String doGet(String serverName, Map<String, Object> params) {
		String result = null;
		int flag = 0;
		while(result==null && (flag++)<3) {
			try {
				URL url = new URL(rebuildUrl(serverName, params));
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(5000);
				conn.connect();
				BufferedReader reader =new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
				result = reader.readLine();
//				System.out.println("res::"+result);
			} catch (Exception e) {
				System.out.println("InternetTools.toGet 出现异常："+e.getMessage());
			}
		}
		return result;
	}

	/**
	 * 重新生成url
	 * @param serverName
	 * @param params
	 * @return
	 */
	private static String rebuildUrl(String serverName, Map<String, Object> params) {
		StringBuffer sb = new StringBuffer(serverName);
		if(serverName.indexOf("?")<0) {
			sb.append("?1=1");
		}
		if(params!=null) {
			for(String key : params.keySet()) {
				sb.append("&").append(key).append("=").append(params.get(key));
			}
		}
		return sb.toString();
	}
}
