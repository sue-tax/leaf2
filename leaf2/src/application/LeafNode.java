/**
 *
 */
package application;

import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Leafの各ノード
 * 実態は、XMLのelement
 * ~~さらに言えば、freeMindのMMファイルのnode~~
 * @author sue-t
 *
 */
public class LeafNode /*implements Element*/ {

	private static XPath xpath = XPathFactory.
			newInstance().	newXPath();

	private static final String XPATH_VALUE_NODE =
			"./attribute[@NAME='値']";

	private static final String XPATH_VALUE =
			"./attribute[@NAME='値']/@VALUE";

	private static final String XPATH_TABLE =
			"./richcontent/html/body/table";

	private static final String XPATH_CELL =
			"./richcontent/html/body/table/tr[%d]/td[%d]/p";

	private static final String XPATH_TABLE_ALL_CELL =
			"./richcontent/html/body/table/tr/td/p";

	/** 式でのノードの循環をチェックするため */
	private static Set<Element> setCirculate =
			new HashSet<Element>();

	public static void calcStart() {
		setCirculate.clear();
	}

	public static boolean checkCirculate(Element element) {
		boolean flag = setCirculate.contains(element);
		if (flag) {
			D.dprint(setCirculate);
		}
		return flag;
	}

	public static String getValue(Element element) {
		String strValue = null;
		try {
			strValue = xpath.evaluate(
					XPATH_VALUE, element);
		} catch (XPathExpressionException e) {
			strValue = String.format(
					Message.SYSTEM_ERROR_, e.toString());
			D.dprint(strValue);
			D.dprint_method_end();
			return strValue;
		}
//		D.dprint(strValue);
//		D.dprint_method_end();
		return strValue;
	}


	private Document document;
	private Element element;

	private String strError = null;

	/** マーク記法の管理 */
	private Map<Integer, Boolean>mapFlag =
			new HashMap<>();

	private Map<Integer, String>mapMark =
			new HashMap<>();

	private Map<Integer, List<NodeExpr>>mapMarkMulti =
			new HashMap<>();

	LeafNode( Document document, Element element) {
		this.document = document;
		this.element = element;
	}


	public boolean isTable() {
		Node node = null;
		try {
			node = (Node)xpath.evaluate(
					XPATH_TABLE, element,
					XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			return false;
		}
		if (node == null) {
			return false;
		}
//		D.dprint(node.getAttributes());
		return true;
	}


	/** 値が未設定で、式があれば、
	 * 式を計算し、値に設定する。
	 * 循環計算のチェックは、ここではしていない
	 * @param document
	 * @param element
	 */
	public void calcNode() {
		D.dprint_method_start();
		D.dprint(element.getAttribute(LeafMM.DATA_ATTR));

		initMark();
		Element elementExpr = null;
		NodeList nodeList = element.getChildNodes();
		for (int i=0; i<nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeName().equals(LeafMM.ATTR_NAME)) {
				Element elementAttr = (Element)node;
				if (elementAttr.getAttribute(
						LeafMM.ATTR_NAME_ATTR).equals(
						LeafMM.ATTR_VALUE)) {
					if (! elementAttr.getAttribute(
							LeafMM.ATTR_DATA_ATTR).equals("")) {
						D.dprint_method_end();
						return;
					}
				}
				if (elementAttr.getAttribute(
						LeafMM.ATTR_NAME_ATTR).equals(
						LeafMM.ATTR_EXPR)) {
					elementExpr  = elementAttr;
				}
			}
		}
		if (elementExpr == null) {
			D.dprint_method_end();
			return;
		}
		String strShiki = elementExpr.getAttribute(
				LeafMM.ATTR_DATA_ATTR);
//		D.dprint(strShiki);
		setCirculate.add(element);
		String strValue = calcNodeSub(strShiki);
		setCirculate.remove(element);
		setValue(strValue);
		D.dprint_method_end();
		return;
	}

	private void setValue(String strValue) {
//		D.dprint_method_start();
//		D.dprint(element.getAttribute(LeafMM.DATA_ATTR));
		NodeList nodeList = null;
		try {
			nodeList = (NodeList)xpath.evaluate(
					XPATH_VALUE_NODE, element,
					XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			strError = String.format(
					Message.SYSTEM_ERROR_, e.toString());
			D.dprint(strError);
			D.dprint_method_end();
			return;
		}
		if (nodeList.getLength() == 0) {
			Element elementValue =
					document.createElement("attribute");
			elementValue.setAttribute("NAME", "値");
			elementValue.setAttribute("VALUE", strValue);
			element.appendChild(elementValue);
		} else /*if (nodeList.getLength() == 1)*/ {
			Element elementValue = (Element)nodeList.item(0);
			elementValue.setAttribute("VALUE", strValue);
		}
//		D.dprint_method_end();
		return;
	}


	/** 文字列の式を解析し、式を計算する */
	private String calcNodeSub(String strShiki) {
//		D.dprint_method_start();
//		D.dprint(strShiki);
		if (strShiki.isEmpty()) {
			// 式がないので、計算不要
			D.dprint("式なし");
			D.dprint_method_end();
			return null;
		}
		Reader in = new StringReader(strShiki);
        LeafLexer lexer = new LeafLexer(in);
        LeafParser parser = new LeafParser(lexer);
        lexer.nextToken();
        boolean flag = true;
        try {
        	flag = parser.parse();    // parse the input
        } catch(Exception e) {
        	D.dprint(e);
        	D.dprint(flag);
        	D.dprint_method_end();
        	return null;
        }
        D.dprint(flag);
        if (! flag) {
        	String strMessage = parser.getErrorMessage();
        	String strError = String.format(
        			Message.INVALID_EXPRESSION_, strMessage);
        	D.dprint(strError);
        	D.dprint_method_end();
        	return strError;
        }
        Expr expr = parser.BuildAs();
//        D.dprint(expr);
        String strValue = calcExpr(expr);
//        D.dprint(strValue);
//        D.dprint_method_end();
        return strValue;
	}


	/** 式の解析結果(Expr)を元に計算する */
	public String calcExpr(Expr expr) {
//		D.dprint_method_start();
//		D.dprint(expr);
		String strValue;

		strError = expr.checkError(this, element);
//		D.dprint("calcExpr"+strError);
		if (strError != null) {
			D.dprint(strError);
			D.dprint_method_end();
			return strError;
		}
//		D.dprint(expr.eval());
//		D.dprint(expr.isNumeric(this, element));
		if (expr.isNumeric(this, element)) {
			BigDecimal value = expr.eval();
//			D.dprint(value);


//			strValue = String.valueOf(value);

			strValue = value.toPlainString();
			D.dprint(strValue);



		} else if (expr.isString(this, element)) {
			strValue = expr.evalStr();
		} else {
			boolean logi = expr.evalLogi();
			strValue = logi?"TRUE":"FALSE";
		}
//		D.dprint(strValue);
//		D.dprint_method_end();
		return strValue;
	}


	public String calcCell(int row, int column) {
//		D.dprint_method_start();
		String strXpath = String.format(XPATH_CELL, row, column);
//		D.dprint(strXpath);
		//xpathでノード特定
		Node node = null;
		try {
			node = (Node)xpath.evaluate(
					strXpath, element,
					XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			strError = String.format(
					Message.SYSTEM_ERROR_, e.toString());
			D.dprint(strError);
			D.dprint_method_end();
			return strError;
		}
//		D.dprint(node);
		String strValue = node.getTextContent();
		strValue = strValue.trim();
//		D.dprint(strValue);
//		D.dprint_method_end();
		return strValue;
	}


	public String calcCell() {
//		D.dprint_method_start();
		String strValue = element.getTextContent();
//		D.dprint(strValue);
//		D.dprint_method_end();
		return strValue;
	}


	public NodeList getTableAll() {
//		D.dprint_method_start();
		NodeList nodeList = null;
		try {
			nodeList = (NodeList)xpath.evaluate(
					XPATH_TABLE_ALL_CELL, element,
					XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			strError = String.format(
					Message.SYSTEM_ERROR_, e.toString());
//			D.dprint(strError);
//			D.dprint_method_end();
			return null;
		}
//		D.dprint_method_end();
		return nodeList;

	}


//	"./richcontent/html/body/table/tr[1]/td/p";

	public NodeList getRowHeader() {
		D.dprint_method_start();
		String strHeader =
				"./richcontent/html/body/table/tr[1]/td/p";
		NodeList nodeList = null;
		D.dprint(element.getAttribute(LeafMM.DATA_ATTR));
		try {
			nodeList = (NodeList)xpath.evaluate(
					strHeader, element,
					XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			strError = String.format(
					Message.SYSTEM_ERROR_, e.toString());
//			D.dprint(strError);
//			D.dprint_method_end();
			return null;
		}
		D.dprint(nodeList);
		D.dprint_method_end();
		return nodeList;
	}


	public NodeList getColumnHeader() {
//		D.dprint_method_start();
		String strHeader =
				"./richcontent/html/body/table/tr/td[1]/p";
		NodeList nodeList = null;
		try {
			nodeList = (NodeList)xpath.evaluate(
					strHeader, element,
					XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			strError = String.format(
					Message.SYSTEM_ERROR_, e.toString());
//			D.dprint(strError);
//			D.dprint_method_end();
			return null;
		}
//		D.dprint_method_end();
		return nodeList;
	}


//	"./richcontent/html/body/table/tr[%d]/td[%d]/p";

	public NodeList getCell(int[] range) {
//		D.dprint_method_start();
		String strRow;
		String strColumn;
		if (range[1] != -1) {
			strRow = String.format(
					"tr[position()>=%d and position()<=%d]",
					range[0], range[1]);
		} else {
			strRow = String.format(
					"tr[position()>=%d]",
					range[0]);
		}
		if (range[3] != -1) {
			strColumn = String.format(
					"td[position()>=%d and position()<=%d]",
					range[2], range[3]);
		} else {
			strColumn = String.format(
					"td[position()>=%d]",
					range[2]);
		}
		String strXpath = "./richcontent/html/body/table/"
				+ strRow + "/" + strColumn + "/p";
//		D.dprint(strXpath);
		//xpathでノード特定
		NodeList nodeList = null;
		try {
			nodeList = (NodeList)xpath.evaluate(
					strXpath, element,
					XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			strError = String.format(
					Message.SYSTEM_ERROR_, e.toString());
			D.dprint(strError);
			D.dprint_method_end();
			return null;
		}
//		D.dprint(nodeList.getLength());
//		D.dprint(nodeList);
//		D.dprint_method_end();
		return nodeList;
	}


	public String getError() {
		return strError;
	}

	public void initMark() {
		mapFlag.clear();
		mapMark.clear();
		mapMarkMulti.clear();
	}


	public boolean setMark( Integer mark, String strValue) {
		if (mapFlag.containsKey(mark)) {
			return false;
		}
		mapMark.put(mark, strValue);
		mapFlag.put(mark, Boolean.TRUE);
		return true;
	}

	public boolean setMarkMulti( Integer mark,
			List<NodeExpr> list) {
		if (mapFlag.containsKey(mark)) {
			return false;
		}
		mapMarkMulti.put(mark, list);
		mapFlag.put(mark, Boolean.FALSE);
		return true;
	}


	/** 登録されていないマークの場合はNULLが返る */
	public String getMark( Integer mark ) {
		return mapMark.get(mark);
	}

	public List<NodeExpr> getMarkMulti( Integer mark ) {
		return mapMarkMulti.get(mark);
	}

	public String getMarkDouble( Integer mark) {
		Boolean flag = mapFlag.get(mark);
		if (flag == null) {
			strError = String.format(
					Message.UNDEFINE_MARK_,
					mark.toString());
			return null;
		}
		if (flag) {
			return mapMark.get(mark);
		} else {
			List<NodeExpr> listNode = mapMarkMulti.get(mark);
			if (listNode.size() == 0) {
				strError = Message.NOT_FOUND_NODE;
				return null;
			} else if (listNode.size() != 1) {
				strError = Message.NOT_SINGLE;
				return null;
			}
			NodeExpr nodeExpr = listNode.get(0);
			strError = nodeExpr.checkError(this, element);
			if (strError != null) {
				return null;
			}
			String str = nodeExpr.evalStr();
			return str;
		}
	}

	public List<NodeExpr> getMarkMultiDouble( Integer mark) {
		Boolean flag = mapFlag.get(mark);
		if (flag == null) {
			strError = String.format(
					Message.UNDEFINE_MARK_,
					mark.toString());
			return null;
		}
		if (flag) {
			String strValue = mapMark.get(mark);
			StringNodeExpr stringNodeExpr =
					new StringNodeExpr(document, element,
							strValue);
			List<NodeExpr> listNode = new ArrayList<NodeExpr>();
			listNode.add(stringNodeExpr);
			return listNode;
		} else {
			List<NodeExpr> listNode = mapMarkMulti.get(mark);
			return listNode;
		}
	}


	/**
	 * フォーマットが指定されている場合に、
	 * フォーマットに従い値を
	 * ノードのデータに設定する。
	 * 式の計算が終了後に呼ばれる前提
	 * @param leafNode
	 */
//	public static void setDataFromValueWithFormat(
//			Element leafNode ) {
//		String[] astr = getAttribute(leafNode);
////		Sy
//		if ((astr[INDEX_FORMAT] != null) &&
//				(! astr[INDEX_FORMAT].equals(""))) {
////			System.out.println(astr[INDEX_VALUE]);
//			String str = String.format(astr[INDEX_FORMAT],
//					astr[INDEX_VALUE]);
////			System.out.println(str);
//			leafNode.setAttribute(DATA_ATTR, str);
//		}
//	}

	public static void main(String[] args) throws Exception {

		String strTarget = "../../*[4+2]/abc/def[3]";
		String strRegex = "(\\.|/|\\*|\\[[^]]*\\])";

		Pattern pattern =
				Pattern.compile(strRegex);
		Matcher matcher = pattern.matcher(
				strTarget);
		while (matcher.find()) {
			D.dprint(matcher.group(0));
		}

	}
}
