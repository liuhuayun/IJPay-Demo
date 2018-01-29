package com.ijpay.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import net.sf.json.JSON;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;

public class XMLUtil {

	/**
	 * 解析xml文件流,返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据。
	 * 
	 * @param strxml
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> getMapStrFromXML(String strxml) throws Exception {
		InputStream in = new ByteArrayInputStream(strxml.getBytes("UTF-8"));
		return getMapStrFromXML(in);
	}

	/**
	 * 解析xml字符串,返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据。
	 * 
	 * @param strxml
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> getMapStrFromXML(InputStream in) throws Exception {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(in);
		Element rootEle = doc.getRootElement();
		@SuppressWarnings("unchecked")
		List<Element> list = rootEle.elements();
		Map<String, String> dataMap = new TreeMap<String, String>();
		for (Element ele : list) {
			if (ele.elements().size() == 0) {
				dataMap.put(ele.getName(), ele.getText());
			} else if (ele.elements().size() > 0) {
				dataMap.put(ele.getName(), ele.asXML().toString());
			}
		}
		return dataMap;
	}

	/**
	 * 解析xml字符串,返回Map<String, Object> 如果第一级元素有子节点，则此节点的值是子节点的Map<String,
	 * Object>数据
	 * 
	 * @param strxml
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> getMapObjFromXML(String strxml) {
		try {
			InputStream in = new ByteArrayInputStream(strxml.getBytes("UTF-8"));
			SAXReader reader = new SAXReader();
			Document  doc = reader.read(in);
			return Dom2Map(doc);
		} catch (DocumentException | UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	/**
	 * 将xml String<STRONG>转换</STRONG>为JSON<STRONG>对象</STRONG>
	 * 
	 * @param xmlString
	 * @return
	 */
	public static String xml2json(String xmlString) {
		XMLSerializer xmlSerializer = new XMLSerializer();
		JSON json = xmlSerializer.read(xmlString);
		return json.toString(1);
	}

	/**
	 * 将xmlDocument<STRONG>转换</STRONG>为JSON<STRONG>对象</STRONG>
	 * 
	 * @param xmlDocument
	 *            XML Document
	 * @return JSON<STRONG>对象</STRONG>
	 */
	public static String xml2json(Document xmlDocument) {
		return xml2json(xmlDocument.toString());
	}

	/**
	 * JSON(数组)字符串<STRONG>转换</STRONG>成XML字符串
	 * 
	 */
	public static String json2xml(String jsonString) {
		XMLSerializer xmlSerializer = new XMLSerializer();
		return xmlSerializer.write(JSONSerializer.toJSON(jsonString));
	}

	@SuppressWarnings("rawtypes")
	private static Map<String, Object> Dom2Map(Document doc) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (doc == null)
			return map;
		Element root = doc.getRootElement();
		for (Iterator iterator = root.elementIterator(); iterator.hasNext();) {
			Element e = (Element) iterator.next();
			List list = e.elements();
			if (list.size() > 0) {
				map.put(e.getName(), Dom2Map(e));
			} else
				map.put(e.getName(), e.getText());
		}
		return map;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Map Dom2Map(Element e) {
		Map map = new HashMap();
		List list = e.elements();
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Element iter = (Element) list.get(i);
				List mapList = new ArrayList();

				if (iter.elements().size() > 0) {
					Map m = Dom2Map(iter);
					if (map.get(iter.getName()) != null) {
						Object obj = map.get(iter.getName());
						if (!obj.getClass().getName().equals("java.util.ArrayList")) {
							mapList = new ArrayList();
							mapList.add(obj);
							mapList.add(m);
						}
						if (obj.getClass().getName().equals("java.util.ArrayList")) {
							mapList = (List) obj;
							mapList.add(m);
						}
						map.put(iter.getName(), mapList);
					} else
						map.put(iter.getName(), m);
				} else {
					if (map.get(iter.getName()) != null) {
						Object obj = map.get(iter.getName());
						if (!obj.getClass().getName().equals("java.util.ArrayList")) {
							mapList = new ArrayList();
							mapList.add(obj);
							mapList.add(iter.getText());
						}
						if (obj.getClass().getName().equals("java.util.ArrayList")) {
							mapList = (List) obj;
							mapList.add(iter.getText());
						}
						map.put(iter.getName(), mapList);
					} else
						map.put(iter.getName(), iter.getText());
				}
			}
		} else
			map.put(e.getName(), e.getText());
		return map;
	}

	/**
	 * 将输入流转换为字符串
	 * 
	 * @param input
	 *            输入流
	 * @return
	 */
	private static String InputStreamToStr(InputStream input, String charset) {
		String result = "";
		int len = 0;
		byte[] array = new byte[1024];
		StringBuffer buffer = new StringBuffer();
		if (input != null) {
			try {
				while ((len = input.read(array)) != -1) {
					buffer.append(new String(array, 0, len, charset));
				}
				result = buffer.toString();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 将输入流转换为utf-8码的字符串
	 * 
	 * @param input
	 * @return
	 */
	public static String InputStreamToUTF8(InputStream input) {
		return InputStreamToStr(input, "UTF-8");
	}

	/**
	 * 将输入流转换为gbk码的字符串
	 * 
	 * @param input
	 * @return
	 */
	public static String InputStreamToGBK(InputStream input) {
		return InputStreamToStr(input, "GBK");
	}

	public static void main(String[] args) throws Exception {
		String xmlStr = "<xml><ToUserName><![CDATA[toUser]]></ToUserName><FromUserName><![CDATA[fromUser]]></FromUserName><CreateTime>12345678</CreateTime><MsgType><![CDATA[news]]></MsgType><ArticleCount>2</ArticleCount><Articles><item><Title><![CDATA[title1]]></Title><Description><![CDATA[description1]]></Description><PicUrl><![CDATA[picurl]]></PicUrl><Url><![CDATA[url]]></Url></item><item><Title><![CDATA[title]]></Title><Description><![CDATA[description]]></Description><PicUrl><![CDATA[picurl]]></PicUrl><Url><![CDATA[url]]></Url></item></Articles></xml>";
		InputStream in = new ByteArrayInputStream(xmlStr.getBytes("UTF-8"));
		Map<String, String> test = getMapStrFromXML(in);
		System.out.print(test.toString());
	}
	
    public static String winxinResultXML(String return_code, String return_msg) {
        return "<xml><return_code><![CDATA[" + return_code
                + "]]></return_code><return_msg><![CDATA[" + return_msg
                + "]]></return_msg></xml>";

}
}