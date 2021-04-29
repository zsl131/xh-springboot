package com.zslin.business.mini.utils;

import com.qq.weixin.mp.aes.WXBizMsgCrypt;
import com.zslin.business.mini.model.MiniConfig;
import com.zslin.business.mini.tools.AccessTokenTools;
import com.zslin.business.mini.tools.MiniConfigTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

/**
 * 接收消息的事件处理工具类
 * @author 钟述林 20150729
 *
 */
@Component
public class MiniEventTools {

	@Autowired
	private MiniConfigTools miniConfigTools;

	@Autowired
	private AccessTokenTools accessTokenTools;

//	@Autowired
//	private IEventRecordService eventRecordService;

//	@Autowired
//	private ITemplateMessageService templateMessageService;

	@Autowired
	private MiniSignTools miniSignTools;

	/**
	 * 获取事件消息的元素对象
	 * @param request
	 * @return
	 */
	public Element getMessageEle(HttpServletRequest request) {
//		System.out.println("====config:"+wxConfigTools);
		MiniConfig config = miniConfigTools.getMiniConfig();
		//System.out.println("====config:"+config);
		Element root = null;
		try {
			String signature = request.getParameter("signature"); //微信加密签名
			String timestamp = request.getParameter("timestamp"); //时间戳
			String nonce = request.getParameter("nonce"); //随机数

//			System.out.println("signature--->"+signature);
//			System.out.println("timestamp--->"+timestamp);
//			System.out.println("nonce--->"+nonce);

			boolean check = miniSignTools.checkSignature(signature, timestamp, nonce);
			if(!check) {return null;} //如果验证不通过
			WXBizMsgCrypt pc = new WXBizMsgCrypt(config.getMsgToken(), config.getMsgAesKey(), config.getAppid());
			
			InputStream in =  request.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(in, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);
			StringBuffer buffer = new StringBuffer();
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = null;
			Document document = null;
			String resultStr = buffer.toString();
			if(resultStr.indexOf("Encrypt")>=0) { 
				StringReader sr = new StringReader(resultStr);
				is = new InputSource(sr);
				document = db.parse(is);
				
				root = document.getDocumentElement();
				NodeList nodelist1 = root.getElementsByTagName("Encrypt");

				String encrypt = nodelist1.item(0).getTextContent();
				
				String format = "<xml><ToUserName><![CDATA[toUser]]></ToUserName><Encrypt><![CDATA[%1$s]]></Encrypt></xml>";
				String fromXML = String.format(format, encrypt);
				//System.out.println("formXML --> "+fromXML);
				try {
					resultStr = pc.decryptMsg(signature, timestamp, nonce, fromXML);
					//System.out.println("resultStr --> "+resultStr);
				} catch (Exception e) {
				e.printStackTrace();
				}
			}
			
			is = new InputSource(new StringReader(resultStr));
			document = db.parse(is);
			root = document.getDocumentElement();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return root;
	}
}
