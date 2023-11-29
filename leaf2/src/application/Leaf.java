/**
 *
 */
package application;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Leafデータ
 *  leaf(python)のLeafNodeに対応する
 *  leaf(python)のLeafは、別のクラス名LeafSys？
 * @author sue-t
 *
 */
public class Leaf {

	private String strFileName;
	Document documentMM;
	private Document documentLeaf;

	public Leaf( String strFileName ) {
		this.documentMM = LeafMM.load(strFileName);
		this.strFileName = strFileName;
		try {
			LeafMM.clearData(this.documentMM);
		} catch (XPathExpressionException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
//		try {
//			this.documentLeaf = LeafMM.createLeafXML(this.documentMM);
//		} catch (ParserConfigurationException e) {
//			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		}
	}



	/**
	 * documentLeaf を全計算する
	 */
	public void calcAll() {
		D.dprint_method_start();
	    Element rootMM = this.documentMM.getDocumentElement();
//	    D.dprint(rootMM);
		clearAllsub(rootMM);
	    LeafNode.calcStart();
	    LeafNode leafNode = new LeafNode(this.documentMM,
	    		rootMM);
	    leafNode.calcNode();
	    calcAllsub(rootMM);
		setDataFormatValueWithFormat();
	    D.dprint_method_end();
	    return;
	}


	// LeafMM.clearDataとかぶっているので、不要
	private void clearAllsub(Element nodeMM) {
//		D.dprint_method_start();
//		D.dprint(nodeMM.getAttribute(LeafMM.DATA_ATTR));
		// attribute[@NAME='式']


		NodeList nodeList = nodeMM.getChildNodes();
		for (int i=0; i<nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
//			D.dprint(node.getNodeName());
			if (node.getNodeName().equals(LeafMM.ATTR_NAME)) {
				Element element = (Element)node;
				if (element.getAttribute(
						LeafMM.ATTR_NAME_ATTR).equals(
						LeafMM.ATTR_VALUE)) {

					element.setAttribute(
							LeafMM.ATTR_DATA_ATTR, "");
					break;
				}
			}
		}
		for (int i=0; i<nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeName().equals(LeafMM.NODE_NAME)) {
				Element element = (Element)node;
				clearAllsub(element);
			}
		}
//		D.dprint_method_end();
	}


	private boolean calcAllsub(Element nodeMM) {
		D.dprint_method_start();
		D.dprint(nodeMM.getAttribute(LeafMM.DATA_ATTR));
		NodeList nodeList = nodeMM.getChildNodes();
		for (int i=0; i<nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
//			D.dprint(node.getNodeName());
			if (node.getNodeName().equals(LeafMM.NODE_NAME)) {
				Element elementMM = (Element)node;
				if (elementMM.getAttribute(LeafMM.DATA_ATTR).
						equals("!STOP!")) {
					D.dprint("!STOP!");
					D.dprint_method_end();
					return false;
				}
				LeafNode.calcStart();
			    LeafNode leafNode = new LeafNode(this.documentMM,
			    		elementMM);
			    leafNode.calcNode();
				boolean flag = calcAllsub(elementMM);
				if (! flag) {
					D.dprint_method_end();
					return false;
				}
			}
		}
		D.dprint_method_end();
		return true;
	}

	public void save() {
		D.dprint_method_start();
		try {
			LeafMM.save(this.documentMM, this.strFileName);
		} catch (TransformerException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			D.dprint(e.toString());
		}
		D.dprint_method_end();
	}


	public void setDataFormatValueWithFormat() {
		final XPath xpath = XPathFactory.
				newInstance().	newXPath();
		String strXpath = "//node";
		NodeList nodeList = null;
		try {
			nodeList = (NodeList)xpath.evaluate(
					strXpath, this.documentMM,
					XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		for (int i=0; i<nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			Element element = (Element)node;
			setDataFromValueWithFormat(element);
		}
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
		LeafMM.setDataFromValueWithFormat(leafNode);
	}

	public static void main(String[] args) throws Exception {
//		Leaf leaf = new Leaf("ふるさと納税.mm");
//		leaf.calcAll();
//		leaf.setDataFormatValueWithFormat();
//		LeafMM.save(leaf.documentMM, "ふるさと納税結果.mm");

		Leaf leaf = new Leaf("外形標準課税.mm");
		leaf.calcAll();
		leaf.setDataFormatValueWithFormat();
		LeafMM.save(leaf.documentMM, "外形標準課税結果.mm");

//		Leaf leaf = new Leaf("leafテスト用.mm");
//		leaf.calcAll();
//		leaf.setDataFormatValueWithFormat();
//		LeafMM.save(leaf.documentMM, "leafテスト結果.mm");


//		Document document = LeafMM.load("生命保険料控除.mm");


		// 2.XPathの処理を実行するXPathのインスタンスを取得する
//		XPath xpath = XPathFactory.newInstance().
//				newXPath();
//		XPathExpression exprRoot =
//				xpath.compile("/map/node");
//		NodeList rootList =
//				(NodeList) exprRoot.evaluate(document,
//						XPathConstants.NODESET);
//		System.out.println(rootList.getLength());
////		System.out.println(rootList.item(0));
//		XPathExpression exprAttribute =
//				xpath.compile("./attribute");
//		XPathExpression exprChild =
//				xpath.compile("./node");
//		if (rootList.getLength()>0) {
//			Element root = (Element)rootList.item(0);
//			System.out.println(root.getAttribute("TEXT"));
//			String[] astr = LeafMM.getAttribute(root);
//			System.out.println(astr[0]);
//			System.out.println(astr[1]);
//			System.out.println(astr[2]);

//
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
////			System.out.println(childList);
////			System.out.println(childList.item(0));
//			for (int i = 0; i < childList.getLength(); i++) {
//				Element child = (Element) childList.item(i);
//				astr = LeafMM.getAttribute(child);
//				System.out.println("値"+astr[0]);
//				System.out.println("式"+astr[1]);
//				System.out.println("フォーマット"+astr[2]);
//
//				setDataFromValueWithFormat(child);
//
//				//				System.out.println(child.getAttribute("TEXT"));
////				NodeList attrList2 = (NodeList)exprAttribute.
////						evaluate(child, XPathConstants.NODESET);
////				System.out.println(attrList2);
////				System.out.println(attrList2.item(0));
////				for (int j = 0; j < attrList2.getLength(); j++) {
////					Element attr2 = (Element) attrList2.item(j);
////					System.out.println(attr2.getAttribute("NAME"));
////					System.out.println(attr2.getAttribute("VALUE"));
////				}
////				if (child.getAttribute("TEXT").equals("TEST")) {
////					System.out.println("change");
////					child.setAttribute("TEXT", "test");
////
////				}
//			}
//		}
//		LeafMM.save(document, "生命保険料控除2.mm");
	}
}
