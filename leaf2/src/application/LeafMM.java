/**
 *
 */
package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Leaf(python)との相違
 * Leaf(p)は各ノードのデータに、source_setなどがある
 * leaf(j)は、XMLデータのまま、処理するので、これができない
 *
 * データの型　指定をどうするか？
 * 数値変換可能なものは、数値とする　暫定
 */

/**
 * MindMapとLeafを連携させる
 * 表示・編集などをMindMapに依存する
 * FreeMindのMM形式のファイルはXMLファイル
 * @author sue-t
 *
 */
public class LeafMM {

	/* FeeMindのファイル形式内のXMLの設定 */
	public static final String NODE_NAME = "node";
	public static final String ATTR_NAME = "attribute";
	public static final String RICH_NAME = "richcontent";

	public static final String DATA_ATTR = "TEXT";
	public static final String ATTR_NAME_ATTR = "NAME";
	public static final String ATTR_DATA_ATTR = "VALUE";

	public static final String ATTR_VALUE = "値";
	public static final String ATTR_EXPR = "式";
	public static final String ATTR_FORMAT = "フォーマット";

	public static final int INDEX_VALUE = 0;
	public static final int INDEX_EXPR = 1;
	public static final int INDEX_FORMAT = 2;

	/* LeafのXMLの設定 */
	public static final String LEAF_ATTR_FORMULA = "formula";
	public static final String LEAF_ATTR_VALUE = "value";


	private static XPath xpath;
	private static XPathExpression exprNode;

	static {
		LeafMM.xpath = XPathFactory.newInstance().
				newXPath();
		try {
			exprNode = xpath.compile("//node");
		} catch (XPathExpressionException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}


	public static Document load( String strFileName ) {
		File file = new File(strFileName);
		FileInputStream is;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return null;
		}
		DocumentBuilderFactory factory =
				DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return null;
		}
		Document document;
		try {
			document = builder.parse(is);
		} catch (SAXException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return null;
		}
		return document;
	}

	public static void save( Document document, String strFileName )
			throws TransformerException {
		D.dprint_method_start();
		File file2 = new File(strFileName);
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();
		transformer.transform(new DOMSource(document), new StreamResult(file2));
		D.dprint_method_end();
		return;
	}

	/**
	 * 使わない方向に、方針転換
	 *
	 * FreeMindのMM形式のXMLから、leaf用のXMLを作成する
	 * @param docMM
	 * @return
	 * @throws ParserConfigurationException
	 */
	public static Document createLeafXML( Document docMM ) throws ParserConfigurationException {
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    DOMImplementation dom = builder.getDOMImplementation();
	    Document docLeaf = dom.createDocument("", "leaf", null);
	    Element rootMM = docMM.getDocumentElement();
	    D.dprint(rootMM);
//	    Element topMM = (Element) nodeTopMM;
	    Element rootLeaf = docLeaf.getDocumentElement();
	    createLeafXMLsub(docLeaf, rootMM, rootLeaf);
		return docLeaf;

//		NodeList nodeList = rootMM.getChildNodes();
//	    for (int i=0; i<nodeList.getLength(); i++) {
//		    Node nodeTopMM = nodeList.item(i);
//		    D.dprint(nodeTopMM);
//		    D.dprint(nodeTopMM.getNodeName());
//		    if (nodeTopMM.getNodeName().equals(NODE_NAME)) {
//			    Element topMM = (Element) nodeTopMM;
//			    Element rootLeaf = docLeaf.getDocumentElement();
//			    createLeafXMLsub(docLeaf, topMM, rootLeaf);
//				return docLeaf;
//		    }
//	    }
//	    return null;
	}

	private static void createLeafXMLsub(Document docLeaf,
			Element nodeMM, Element nodeLeaf) {
		D.dprint_method_start();
		D.dprint(nodeMM.getAttribute(DATA_ATTR));
		NodeList nodeList = nodeMM.getChildNodes();
		for (int i=0; i<nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			D.dprint(node.getNodeName());
			if (node.getNodeName().equals(NODE_NAME)) {
				Element elementMM = (Element)node;
				String strAtai = null;
				String strShiki = null;
				String strTag = elementMM.getAttribute(DATA_ATTR);
				D.dprint(strTag);
				// TODO ===========================
				// strTag が　"123456"のような値の場合がある
				Element element = null;
				char topTag = strTag.charAt(0);
				if (Character.isJavaIdentifierStart(topTag)) {
					element = docLeaf.createElement(strTag);
				} else {
					String strNewTag = strTag.replace(",", "_");
					element = docLeaf.createElement("_"+strNewTag);
					strAtai = strTag;
				}
				// TODO 暫定
				NodeList attrList = elementMM.getChildNodes();
				for (int j=0; j<attrList.getLength(); j++) {
					Node nodeAttr = attrList.item(j);
					String strAttrName = nodeAttr.getNodeName();
					if (strAttrName.equals(ATTR_NAME)) {
						Element elementAttr = (Element)nodeAttr;
						String strName = elementAttr.getAttribute(
								ATTR_NAME_ATTR);
						if (strName == null) {
							continue;
						}
						D.dprint("strName"+strName);
						if (strName.equals(ATTR_VALUE)) {
							strAtai = elementAttr.getAttribute(
									ATTR_DATA_ATTR);
						} else if (strName.equals(ATTR_EXPR)) {
							strShiki = elementAttr.getAttribute(
									ATTR_DATA_ATTR);
						}
					} else if (strAttrName.equals(RICH_NAME)) {
						// TODO 表のデータ

						// Xpath に変更する

//						Node node_html = nodeAttr.getFirstChild();
//						D.dprint(node_html.getNodeName());
//						Node node_head = node_html.getFirstChild();
//						D.dprint(node_head.getNodeName());
////						Node node_body = node_html.getLastChild();
//						Node node_body = node_head.getNextSibling();
//						D.dprint(node_body.getNodeName());
//						Node nodeTableMM = node_body.getFirstChild();
//						D.dprint(nodeTableMM.getNodeName());


						break;
					}
				}
				D.dprint(strShiki);
				D.dprint(strAtai);
				if (strShiki != null) {
					element.setAttribute(LEAF_ATTR_FORMULA,
							strShiki);
				} else if ((strShiki == null)
						&& (strAtai != null)) {
					element.setAttribute(LEAF_ATTR_VALUE,
							strAtai);
				}
				D.dprint("append node");
				D.dprint(element.getNodeName());
				D.dprint(element.getAttribute(LEAF_ATTR_FORMULA));
				D.dprint(element.getAttribute(LEAF_ATTR_VALUE));
				nodeLeaf.appendChild(element);
				createLeafXMLsub(docLeaf, elementMM, element);
			}
		}
		D.dprint_method_end();
	}

	/**
	 * フォーマットが指定されている場合に、
	 * フォーマットに従い値を
	 * ノードのデータに設定する。
	 * 式の計算が終了後に呼ばれる前提
	 * @param leafNode
	 */
	public static void setDataFromValueWithFormat(
			Element leafNode ) {
		String[] astr = getAttribute(leafNode);
		if ((astr[INDEX_FORMAT] != null) &&
				(! astr[INDEX_FORMAT].equals(""))) {
			D.dprint(astr[INDEX_VALUE]);
			D.dprint(astr[INDEX_FORMAT]);
			double doubleValue = Double.parseDouble(astr[INDEX_VALUE]);
			String str = String.format(astr[INDEX_FORMAT],
					doubleValue);
//			System.out.println(str);
			leafNode.setAttribute(DATA_ATTR, str);
		}
	}

	/**
	 * 式が設定されているノードの
	 * データ内の値をクリアする（""を設定する）
	 * @param document
	 * @throws XPathExpressionException
	 */
	public static void clearData( Document document )
			throws XPathExpressionException {
		D.dprint_method_start();
		NodeList nodeList =
				(NodeList)exprNode.evaluate(document,
						XPathConstants.NODESET);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element node = (Element)nodeList.item(i);
//			System.out.println(node.getNodeName());
//			System.out.println(node.getAttribute("TEXT"));
			String[] astr = LeafMM.getAttribute(node);
			if ((astr[INDEX_EXPR] == null) ||
					(astr[INDEX_EXPR].equals(""))) {
				continue;
			}
//			System.out.println(astr[INDEX_EXPR]);
			NodeList attrList = node.getChildNodes();
			for (int j=0; j<attrList.getLength(); j++) {
				if (attrList.item(j).getNodeType()
						!= Node.ELEMENT_NODE) {
					continue;
				}
				Element child = (Element)attrList.item(j);
				if (child.getNodeName().equals(ATTR_NAME)) {
					String strName = child.getAttribute(
							ATTR_NAME_ATTR);
					if (strName == null) {
						continue;
					}
//					System.out.println(strName);
					if (strName.equals(ATTR_VALUE)) {
						child.setAttribute(
								ATTR_DATA_ATTR, "");
//						System.out.println("DATA CLEARED");
						break;
					}
					//TODO ""
				}
			}
		}
		D.dprint_method_end();
	}


	/**
	 * ノードの属性の値、式、フォーマットを取得する
	 * @param leafNode
	 * @return
	 */
	public static String[] getAttribute(
			Element leafNode ) {
		String[] astr = new String[3];
		NodeList childList = leafNode.getChildNodes();
		if (childList.getLength() == 0) {
			return astr;
		}
		for (int i=0; i<childList.getLength(); i++) {
			if (childList.item(i).getNodeType()
					== Node.ELEMENT_NODE) {
				Element child = (Element)childList.item(i);
				if (child.getNodeName().equals(ATTR_NAME)) {
					String strName = child.getAttribute(
							ATTR_NAME_ATTR);
					if (strName == null) {
						continue;
					}
//					System.out.println(strName);
					if (strName.equals(ATTR_VALUE)) {
						astr[INDEX_VALUE] =
								child.getAttribute(
								ATTR_DATA_ATTR);
					} else if (strName.equals(ATTR_EXPR)) {
						astr[INDEX_EXPR] =
								child.getAttribute(
								ATTR_DATA_ATTR);
					} else if (strName.equals(ATTR_FORMAT)) {
						astr[INDEX_FORMAT] =
								child.getAttribute(
								ATTR_DATA_ATTR);
					}
				}
			}
		}
		return astr;
	}

	public static void main(String[] args) throws Exception {
		Document document = LeafMM.load(
				"生命保険料控除.mm");
//		Document document = LeafMM.load(
//				"ふるさと納税.mm");

		//		Document docLeaf = LeafMM.createLeafXML(document);
//		System.exit(0);

		LeafMM.clearData(document);
		Document docLeaf = LeafMM.createLeafXML(document);

		System.exit(0);

		LeafMM.save(document, "生命保険料控除2.mm");

		// 1.Documentを作るまでの流れはDOMと同じ
//		File file = new File("生命保険料控除.mm");
//		FileInputStream is = new FileInputStream(
//				file);
//		DocumentBuilderFactory factory =
//				DocumentBuilderFactory.newInstance();
//		DocumentBuilder builder =
//				factory.newDocumentBuilder();
//		Document document = builder.parse(is);

//
//
//		// 2.XPathの処理を実行するXPathのインスタンスを取得する
//		XPath xpath = XPathFactory.newInstance().
//				newXPath();
//		XPathExpression exprRoot =
//				xpath.compile("/map/node");
//		NodeList rootList =
//				(NodeList) exprRoot.evaluate(document,
//						XPathConstants.NODESET);
//		System.out.println(rootList.getLength());
//		System.out.println(rootList.item(0));
//		XPathExpression exprAttribute =
//				xpath.compile("./attribute");
//		XPathExpression exprChild =
//				xpath.compile("./node");
//		if (rootList.getLength()>0) {
//			Element root = (Element)rootList.item(0);
//			System.out.println(root.getAttribute("TEXT"));
//			NodeList attrList = (NodeList)exprAttribute.
//					evaluate(root, XPathConstants.NODESET);
//			System.out.println(attrList);
//			System.out.println(attrList.item(0));
//			for (int i = 0; i < attrList.getLength(); i++) {
//				Element attr = (Element) attrList.item(i);
//				System.out.println(attr.getAttribute("NAME"));
//				System.out.println(attr.getAttribute("VALUE"));
//			}
//			NodeList childList = (NodeList)exprChild.
//					evaluate(root, XPathConstants.NODESET);
//			System.out.println(childList);
//			System.out.println(childList.item(0));
//			for (int i = 0; i < childList.getLength(); i++) {
//				Element child = (Element) childList.item(i);
//				System.out.println(child.getAttribute("TEXT"));
//				NodeList attrList2 = (NodeList)exprAttribute.
//						evaluate(child, XPathConstants.NODESET);
//				System.out.println(attrList2);
//				System.out.println(attrList2.item(0));
//				for (int j = 0; j < attrList2.getLength(); j++) {
//					Element attr2 = (Element) attrList2.item(j);
//					System.out.println(attr2.getAttribute("NAME"));
//					System.out.println(attr2.getAttribute("VALUE"));
//				}
//				if (child.getAttribute("TEXT").equals("TEST")) {
//					System.out.println("change");
//					child.setAttribute("TEXT", "test");
//
//				}
//			}
//		}
//		File file2 = new File("生命保険料控除2.mm");
//		TransformerFactory transFactory = TransformerFactory.newInstance();
//		Transformer transformer = transFactory.newTransformer();
//		transformer.transform(new DOMSource(document), new StreamResult(file2));
//
////		// 3.XPathでの検索条件を作る
//////		XPathExpression expr =
//////				xpath.compile("/BookList/Book");
////		XPathExpression expr =
////				xpath.compile("//node");
////		XPathExpression exprFont =
////				xpath.compile("./font");
////
////		// 4.DocumentをXPathで検索して、結果をDOMのNodeListで受け取る
////		NodeList nodeList =
////				(NodeList) expr.evaluate(document,
////						XPathConstants.NODESET);
////
////		// 5.XPathでの検索結果を持っているNodeListの内容でループ
////		for (int i = 0; i < nodeList.getLength(); i++) {
////			// 6.要素を検索しているのでNodeの実体はElement。キャストして使う。
////			Element element = (Element) nodeList.item(i);
////
////			// 7.Elementから必要な情報を取得して出力する
//////			System.out.println("isbn = " + element.getAttribute("isbn"));
//////			System.out.println("title = " + element.getAttribute("title"));
//////			System.out.println("author = " + element.getAttribute("author"));
//////			System.out.println("text = " + element.getTextContent());
//////			System.out.println();
////			System.out.println(element.getAttribute("TEXT"));
////			NodeList nodeAttr = (NodeList)exprAttribute.evaluate(
////					element, XPathConstants.NODESET);
////			if (nodeAttr.getLength() > 0) {
////				Element font = (Element)nodeAttr.item(0);
////				System.out.println(font.getAttribute("NAME"));
////			}
////
////
////		}
	}
}