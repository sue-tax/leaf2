package application;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

/* LeafNodeから独立
 * 本来は、TagExprの中にあるメソッドかもしれない
 */
public class CalcTag {
	private static XPath xpath = XPathFactory.
			newInstance().	newXPath();

	// 祖先ノードの省略記法
	// `..4` -> `../../../..`
	private static Pattern patternTransAncestor =
			Pattern.compile("\\.\\.([0-9]+)");

	// ノード名参照記法
	// `$2` -> 親の親のノード名
	private static Pattern patternTransNodeName =
			Pattern.compile("\\$([0-9]*)");

	// 相対位置参照記法
	// `%n-m`
	private static Pattern patternTransOffset =
			Pattern.compile("%([0-9]*)([+-])([0-9]+)");

	// 位置置換記法
	// `#n`
	private static Pattern patternTransReplace =
			Pattern.compile("#([0-9]*)");

	// 子ノード参照記法
	// `@`
	private static Pattern patternTransChild =
			Pattern.compile("@");

	// FreeMind用にXPathへの書き換え
	// `./子/孫` -> `./node[@TEXT='子']/node[@TEXT='孫']`
	private static Pattern patternTransWithout =
			Pattern.compile("(!|\\.|/|\\*|\\[[^]]*\\])");
			// ! ダミー

	private static Pattern patternTransAll =
			Pattern.compile("\\*");

	private LeafNode node;
	private Document document;
	private TagExpr expr;
	private Element element;

	private String strError = null;

	CalcTag( LeafNode node, Document document,
			TagExpr expr,
			Element element) {
		this.node = node;
		this.document = document;
		this.expr = expr;
		this.element = element;
	}

	public String getError() {
		return strError;
	}

	/**
	 * @return
	 */
	public String calcExprTag() {
//		D.dprint_method_start();
//		D.dprint(expr);
//		D.dprint(expr.index1);
//		D.dprint(expr.strMarkDef);
//		D.dprint(expr.strMarkRef);
		String strValue = null;

		// strMarkRef で処理を分ける　ように変更
		if (expr.strMarkRef == null) {
			// マーク指定なし
//			D.dprint(expr.strLpath.evalStr());
			String strXpath = translateLpath(
					expr.strLpath.evalStr(), element,
					document);
//			D.dprint(strXpath);
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
//			D.dprint(nodeList);
			if (nodeList.getLength() == 0) {
				strError = String.format(
						Message.NOT_FOUND_NODE_,
						expr.strLpath.evalStr());
				D.dprint(strError);
				D.dprint_method_end();
				return null;
			}
			if (nodeList.getLength() != 1) {
				strError = Message.NOT_SINGLE;
				D.dprint(strError);
				D.dprint_method_end();
				return null;
			}
			Element elementRef = (Element)nodeList.item(0);
			if (LeafNode.checkCirculate(elementRef)) {
				strError = Message.CIRCULATE_ERROR;
				D.dprint(strError);
				D.dprint_method_end();
				return null;
			}
//			D.dprint(elementRef.getAttribute("TEXT"));
		    LeafNode leafNode = new LeafNode(document,
		    		elementRef);
			if (expr.index1 == null) {
			    if (leafNode.isTable()) {
			    	strError = Message.NOT_TABLE;
			    	D.dprint(strError);
					D.dprint_method_end();
					return null;
			    }
			    leafNode.calcNode();
				strValue = LeafNode.getValue(elementRef);
			} else {
				LeafNode leafNodeBase = new LeafNode(document,
						element);
//				strValue = calcTableTag(leafNodeBase);
				strValue = calcTableTag(leafNode);
			}
			if (expr.strMarkDef != null) {
				String strMark = expr.strMarkDef.
						evalStr();
				strMark = strMark.
						substring(0, strMark.length()-1);
				Integer iMark = Integer.valueOf(strMark);
				node.setMark(iMark, strValue);
			}
		} else {
			// マーク指定の場合
			String strMark = expr.strMarkRef.
					evalStr();
			strMark = strMark.substring(1);
			Integer iMark = Integer.valueOf(strMark);
			strValue = node.getMark(iMark);
			if (strValue == null) {
				strValue = node.getMarkDouble(iMark);
				if (strValue == null) {
					strError = node.getError();
			    	D.dprint(strError);
					D.dprint_method_end();
					return null;
				}
			}
		}
//		D.dprint(strValue);
//		D.dprint_method_end();
		return strValue;
	}


	/**
	 * @return
	 */
	public List<NodeExpr> calcExprTagMulti() {
		D.dprint_method_start();
//		D.dprint(expr);
//		D.dprint(expr.index1);
//		D.dprint(expr.strMarkDef);
//		D.dprint(expr.strMarkRef);
	    LeafNode leafNodeBase = new LeafNode(document,
	    		element);
		String strValue = null;
		List<NodeExpr> listNode =
				new ArrayList<NodeExpr>();
		if (expr.strMarkRef == null) {
			// 参照指定なし
//			D.dprint(expr.strLpath.evalStr());

			String strXpath = translateLpath(
					expr.strLpath.evalStr(), element,
					document);
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
//			D.dprint(nodeList);
			if (nodeList.getLength() == 0) {
				strError = String.format(
						Message.NOT_FOUND_NODE_,
						expr.strLpath.evalStr());
				D.dprint(strError);
				D.dprint_method_end();
				return null;
			}
			if (expr.index1 == null) {
				for (int i=0; i<nodeList.getLength(); i++) {
					Element elementRef = (Element)nodeList.
							item(i);
					if (LeafNode.checkCirculate(elementRef)) {
						strError = Message.CIRCULATE_ERROR;
						D.dprint(strError);
						D.dprint_method_end();
						return null;
					}
				    LeafNode leafNode = new LeafNode(document,
				    		elementRef);
				    if (leafNode.isTable()) {
				    	// TODO テーブルの全セル指定
				    	List<NodeExpr> list =
				    			calcExprTagMultiTableAll(
				    			leafNode, elementRef);
				    	listNode.addAll(list);
				    } else {
						NodeExpr nodeExpr = new NodeExpr(
								document, elementRef);
						listNode.add(nodeExpr);
				    }
				}
			} else {
				// 表のインデックス指定あり
				for (int i=0; i<nodeList.getLength(); i++) {
					Element elementRef = (Element)nodeList.
							item(i);
				    LeafNode leafNode = new LeafNode(document,
				    		elementRef);
				    if (! leafNode.isTable()) {
						strError = Message.MUST_TABLE;
						D.dprint(strError);
						D.dprint_method_end();
						return null;
				    }
					List<List<CellExpr>>llHeader = getTableHeader(
							leafNodeBase);
				    //index1から行の範囲　１～：１～　０は終わりまで
					//index2から列の範囲
//					int[] range = getIndex(leafNode);
					int[] range = getIndex(leafNodeBase,
							llHeader);
					D.dprint(range);
					if (range == null) {
						D.dprint(strError);
						D.dprint_method_end();
						return null;
					}
					// LeafNode セルのノードの取得
					NodeList nodeCellList = leafNode.getCell(range);
					for (int j=0; j<nodeCellList.getLength(); j++) {
						Node node = nodeCellList.item(j);
						CellExpr cellExpr = new CellExpr(document,
								(Element)node);
						listNode.add(cellExpr);
					}
				}
			}
			if (expr.strMarkDef != null) {
//				D.dprint("MARKDEF");
				String strMark = expr.strMarkDef.
						evalStr();
				strMark = strMark.
						substring(0, strMark.length()-1);
				Integer iMark = Integer.valueOf(strMark);
				node.setMarkMulti(iMark, listNode);
//				D.dprint(node);
			}
		} else {
			// マーク指定の場合
//			D.dprint("MARKREF");
			String strMark = expr.strMarkRef.
					evalStr();
			strMark = strMark.substring(1);
			Integer iMark = Integer.valueOf(strMark);
			listNode = node.getMarkMulti(iMark);
			if (listNode == null) {
				listNode = node.getMarkMultiDouble(iMark);
//				D.dprint("node.getMarkMultiDouble");
//				D.dprint(listNode);
				if (listNode == null) {
					strError = node.getError();
			    	D.dprint(strError);
					D.dprint_method_end();
					return null;
				}
			}
		}
//		D.dprint(listNode);
		D.dprint_method_end();
		return listNode;
	}


	private List<NodeExpr> calcExprTagMultiTableAll(
			LeafNode leafNode, Element element) {
		D.dprint_method_start();
		NodeList nodeList = leafNode.getTableAll();
		if (nodeList == null) {
			strError = leafNode.getError();
			D.dprint(strError);
			D.dprint_method_end();
			return null;
		}
		List<NodeExpr> list = new ArrayList<NodeExpr>();
		for (int i=0; i<nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			CellExpr cellExpr = new CellExpr(document,
					(Element)node);
			list.add(cellExpr);
		}
		D.dprint(list);
		D.dprint_method_end();
		return list;
	}


	private int[] getIndex( LeafNode leafNode,
			List<List<CellExpr>> llHeader) {
		// index1, index2 から範囲指定
		D.dprint_method_start();
		int[] range = new int[4];
		if (expr.index1.all) {
			range[0] = 1;
			range[1] = -1;	//最後の行
		} else if (expr.index1.single != null) {
			int row = calcIndex(leafNode, expr.index1.single,
					llHeader.get(1));
			if (row < 0) {
				D.dprint_method_end();
				return null;
			}
			range[0] = row;
			range[1] = row;
		} else {
			if (expr.index1.start != null) {
				int row = calcIndex(leafNode, expr.index1.start,
						llHeader.get(1));
				if (row < 0) {
					D.dprint_method_end();
					return null;
				}
				range[0] = row;
			} else {
				range[0] = 1;
			}
			if (expr.index1.end != null) {
				int row = calcIndex(leafNode, expr.index1.end,
						llHeader.get(1));
				if (row < 0) {
					D.dprint_method_end();
					return null;
				}
				range[1] = row;
			} else {
				range[1] = -1;	// 最後の行
			}
		}
		if (expr.index2.all) {
			range[2] = 1;
			range[3] = -1;	//最後の行
		} else if (expr.index2.single != null) {
			int column = calcIndex(leafNode, expr.index2.single,
					llHeader.get(0));
			if (column < 0) {
				D.dprint_method_end();
				return null;
			}
			range[2] = column;
			range[3] = column;
		} else {
			if (expr.index2.start != null) {
				int column = calcIndex(leafNode, expr.index2.start,
						llHeader.get(0));
				if (column < 0) {
					D.dprint_method_end();
					return null;
				}
				range[2] = column;
			} else {
				range[2] = 1;
			}
			if (expr.index2.end != null) {
				int column = calcIndex(leafNode, expr.index2.end,
						llHeader.get(0));
				if (column < 0) {
					D.dprint_method_end();
					return null;
				}
				range[3] = column;
			} else {
				range[3] = -1;	// 最後の列
			}
		}
		D.dprint(range);
		D.dprint_method_end();
		return range;
	}

	private String calcTableTag( LeafNode leafNode ) {
		D.dprint_method_start();
		// leafNodeが表だった場合の処理

		List<List<CellExpr>>llHeader = getTableHeader(
				leafNode);
		IndexExpr rowIndexExpr = expr.index1;
		IndexExpr columnIndexExpr = expr.index2;
		if ((rowIndexExpr == null) ||
				(columnIndexExpr == null)) {
			strError = Message.NOT_TABLE;
			D.dprint(strError);
			D.dprint_method_end();
			return null;
		}
		if ((rowIndexExpr.single == null) ||
				(columnIndexExpr.single == null)) {
			strError = Message.NOT_TABLE;
			D.dprint(strError);
			D.dprint_method_end();
			return null;
		}
		// １行、１列指定により、セルを特定した場合のみ
	    int rowIndex = calcIndex(leafNode,
	    		rowIndexExpr.single, llHeader.get(1));
	    if (rowIndex < 0) {
			D.dprint(strError);
			D.dprint_method_end();
			return null;
	    }
	    int columnIndex = calcIndex(leafNode,
	    		columnIndexExpr.single, llHeader.get(0));
	    if (columnIndex < 0) {
			D.dprint(strError);
			D.dprint_method_end();
			return null;
	    }
	    String strValue = leafNode.calcCell(rowIndex, columnIndex);

//		String strValue = leafNode.getValue(element);

		D.dprint(strValue);
		D.dprint_method_end();
		return strValue;
	}


	private List<List<CellExpr>> getTableHeader(
			LeafNode leafNode) {
//		D.dprint_method_start();
		NodeList listRowHeader = leafNode.getRowHeader();
		if (listRowHeader == null) {
			strError = leafNode.getError();
			D.dprint_method_end();
			return null;
		}
		NodeList listColumnHeader = leafNode.getColumnHeader();
		if (listColumnHeader == null) {
			strError = leafNode.getError();
//			D.dprint_method_end();
			return null;
		}
		List<CellExpr> listRowExpr = new ArrayList<CellExpr>();
		for (int i=0; i<listRowHeader.getLength(); i++) {
			Element element = (Element)listRowHeader.item(i);
			CellExpr cellExpr = new CellExpr(
					document, element);
			listRowExpr.add(cellExpr);
		}
		List<CellExpr> listColumnExpr = new ArrayList<CellExpr>();
		for (int i=0; i<listColumnHeader.getLength(); i++) {
			Element element = (Element)listColumnHeader.item(i);
			CellExpr cellExpr = new CellExpr(
					document, element);
			listColumnExpr.add(cellExpr);
		}
		List<List<CellExpr>> llHeader = new ArrayList
				<List<CellExpr>>();
		llHeader.add(listRowExpr);
		llHeader.add(listColumnExpr);
//		D.dprint_method_end();
		return llHeader;
	}


	private int calcIndex(LeafNode leafNode,
			IndexShikiExpr indexShiki, List<CellExpr> listHeader) {
		D.dprint_method_start();
		Expr expr = indexShiki.expr;
		int index = 0;
		if (expr != null) {
			String strIndex = leafNode.calcExpr(expr);
			// strIndexはエラーメッセージの可能性あり
			if (! expr.isNumeric(leafNode, element)) {
				if (! expr.isString(leafNode, element)) {
					strError = Message.INDEX_MUST_INTEGER;
					D.dprint(strError);
					D.dprint_method_end();
					return -1;
				}
				// 文字列ならば、ヘッダーを調べる
				String strData = expr.evalStr();
				for (int i=0; i<listHeader.size(); i++) {
					CellExpr cellExpr = listHeader.get(i);
					strError = cellExpr.checkError(
							leafNode, element);
					if (strError != null) {
						D.dprint(strError);
						D.dprint_method_end();
						return -1;
					}
					if (! cellExpr.isString(
							leafNode, element)) {
						strError = Message.NOT_STRING_HEADER;
						D.dprint(strError);
						D.dprint_method_end();
						return -1;
					}
					String strHeader = cellExpr.evalStr();
					strHeader = strHeader.trim();
					D.dprint("strHeader *"+ strHeader +"*");
					D.dprint("strData   *"+ strData +"*");
					if (strHeader.compareTo(strData) == 0) {
						index = i + 1;
						D.dprint(index);
						D.dprint_method_end();
						return index;
					}
				}
				strError = Message.NOT_FOUND_HEADER;
				D.dprint_method_end();
				return -1;
			}
			BigDecimal indexDecimal = expr.eval();
			try {
				index = indexDecimal.intValueExact();
			} catch(Exception e) {
				strError = Message.INDEX_MUST_INTEGER;
				D.dprint(strError);
				D.dprint_method_end();
				return -1;
			}
			D.dprint("index");
			D.dprint(index);
		}
		if (indexShiki.sharp) {
			// TODO
			// 表のセルから、他のセルを指定
			int base = 0;
			if (indexShiki.plus) {
				index = base + index;
			} else {
				index = base - index;
			}
			strError = Message.UNSUPPORT;
			D.dprint(strError);
			D.dprint_method_end();
			return -1;
		}
		D.dprint(index);
		D.dprint_method_end();
		return index;
	}






// {lPath}  |xPath| 以外に、<vPath> ノード名を値として取る
// は、止めにして、
// {@}　を　{./*[0]}
// {@i},{@f},{@s}で整数、浮動小数点数、文字列を示す
	/**
	 * lPath表記をXpathに書き換える
	 * @param strLpath
	 * @return
	 */
//    指定された文字列 xpath の中の
//    ..3 を../../../のように置換し、
//    $n をそのノードの要素名に置換し、
//    #n を各ノードの兄弟番号に置換する。
//    # または #0 は、node の兄弟番号
//    #1 は、tag の親の兄弟番号

	// 上記の#は、%で代用可能
	// #は絶対的な兄弟番号に変更する？　しない

	// &を絶対的な兄弟番号？



//    %n+m は、そのノードのn代上の先祖のm個下の弟のノード、
//    %0-m は、そのノードのm個上の兄のノード
//    %n-m は、そのノードのn代上の先祖のm個上の兄のノード
//    %0+m は、そのノードのm個下の弟のノード
//    　%の後の0は省略可能

	private static String translateLpath(
			String strLpath, Element element,
			Document document) {
//		D.dprint_method_start();
//		D.dprint(strLpath);
		String strXpath = null;
		// 各種置換後に、FreeMind用のXMLのxpathに

		//%,#,$を置換する

		// 祖先ノードの省略記法
		String strLpathReplace = translateAncestor(
				strLpath);
		// ノード名参照記法
		strLpathReplace = translateNodeName(
				strLpathReplace, element);
		// 子ノード参照
		strLpathReplace = translateChild(
				strLpathReplace);
		// 相対位置参照記法
		strLpathReplace = translateOffset(
				strLpathReplace, element);
		// 位置置換記法
		strLpathReplace = translateReplace(
				strLpathReplace, element);


		// ./* [～]以外をnode[@TEXT='xxxx']に置換する
		strXpath = translateMM(strLpathReplace);

		strXpath = strXpath.replaceAll("!", "*");
//		D.dprint(strXpath);
//		D.dprint_method_end();
		return strXpath;
	}

	// 祖先ノードの省略記法
	// `..4` -> `../../../..`
	private static String translateAncestor(
			String strLpath ) {
//		D.dprint_method_start();
//		D.dprint(strLpath);
		StringBuilder sbAnc = new StringBuilder();
		int index = 0;
		Matcher mAnc = patternTransAncestor.matcher(
				strLpath);
		while (mAnc.find()) {
//			D.dprint(mAnc.group(0));
			if (index < mAnc.start(0)) {
				sbAnc.append(strLpath.substring(
						index, mAnc.start(0)));
			}
			int R = Integer.parseInt(mAnc.group(1));
			for (int r=0; r<R; r++) {
				sbAnc.append("..");
				sbAnc.append("/");
			}
			if (R > 0) {
				sbAnc.deleteCharAt(sbAnc.length()-1);
			}
			index = mAnc.end(0);
//			D.dprint(index);
		}
		if (index < strLpath.length()) {
			sbAnc.append(strLpath.substring(
					index));
		}
//		D.dprint(sbAnc);
		String strReplace = sbAnc.toString();
//		D.dprint(strReplace);
//		D.dprint_method_end();
		return strReplace;
	}

	// ノード名参照記法
	// `$2` は親の親のノード名に置き換える
	private static String translateNodeName(
			String strLpath, Element element ) {
//		D.dprint_method_start();
//		D.dprint(strLpath);
		// ノード名参照記法
		// `$2` は親の親のノード名に置き換える
		StringBuilder sbName = new StringBuilder();
		int index = 0;
		Matcher mName = patternTransNodeName.matcher(
				strLpath);
		while (mName.find()) {
//			D.dprint(mName.group(0));
			if (index < mName.start(0)) {
				sbName.append(strLpath.substring(
						index, mName.start(0)));
			}
			String strR = mName.group(1);
			if ((strR == null) || (strR.equals(""))) {
//				D.dprint("$の場合");
				sbName.append(
						element.getAttribute("TEXT"));
			} else {
				String strXpathName = String.format(
						"ancestor::node[%s]/@TEXT", mName.group(1));
//				D.dprint(strXpathName);
				String strText = null;
				try {
					strText = (String)xpath.evaluate(
							strXpathName, element,
							XPathConstants.STRING);
				} catch (XPathExpressionException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
//				D.dprint(strText);
				sbName.append(strText);
			}
			index = mName.end(0);
//			D.dprint(index);
		}
		if (index < strLpath.length()) {
			sbName.append(strLpath.substring(
					index));
		}
		String strReplace = sbName.toString();
//		D.dprint(strReplace);
//		D.dprint_method_end();
		return strReplace;
	}

	// 相対位置参照記法
	// `%n-m` は

	// ! はダミー、最終的には*に
	private static String translateOffset(
			String strLpath, Element element ) {
//		D.dprint_method_start();
//		D.dprint(strLpath);
		StringBuilder sb = new StringBuilder();
		int index = 0;
		Matcher m = patternTransOffset.matcher(
				strLpath);
		while (m.find()) {
//			D.dprint(m.group(0));
			if (index < m.start(0)) {
				sb.append(strLpath.substring(
						index, m.start(0)));
			}
			String strParent = m.group(1);
			NodeList nodeList = null;
			int offsetN = 0;
			if ((strParent == null) ||
					strParent.equals("") || strParent.equals("0")) {
				String strXpathParent = "../node";
				try {
					nodeList = (NodeList)xpath.evaluate(
							strXpathParent, element,
							XPathConstants.NODESET);
				} catch (XPathExpressionException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
				Node elementNode = (Node)element;
				for (int i=0; i<nodeList.getLength(); i++) {
					if (nodeList.item(i).isSameNode(elementNode)) {
						offsetN = i;
						break;
					}
				}
//				sb.append(String.format("*[%d%s%s]",
//						offsetN+1, m.group(2), m.group(3)));
				sb.append(String.format("../![@TEXT][%d%s%s]",
						offsetN+1, m.group(2), m.group(3)));
			} else {
				String strXpathParent = String.format(
						"ancestor::node[%s]", strParent);
//				String strXpathParent = String.format(
//						"ancestor::node[%s+1]", strParent);
				Node parentNode = null;
//				D.dprint(strXpathParent);
				try {
					parentNode = (Node)xpath.evaluate(
							strXpathParent, element,
							XPathConstants.NODE);
				} catch (XPathExpressionException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
//				D.dprint(((Element)parentNode).getAttribute("TEXT"));
//				strXpathParent += "/..";
				strXpathParent += "/../*[@TEXT]";
//				D.dprint(strXpathParent);
				try {
					nodeList = (NodeList)xpath.evaluate(
							strXpathParent, element,
							XPathConstants.NODESET);
				} catch (XPathExpressionException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
				for (int i=0; i<nodeList.getLength(); i++) {
					if (nodeList.item(i).isSameNode(parentNode)) {
						offsetN = i;
						break;
					}
				}
				sb.append("../");
				int iParent = Integer.valueOf(strParent);
				for (int i=0; i<iParent; i++) {
					sb.append("../");
				}
				sb.append(String.format("![@TEXT][%d%s%s]",
						offsetN+1, m.group(2), m.group(3)));
			}
			index = m.end(0);
//			D.dprint(index);
		}
		if (index < strLpath.length()) {
			sb.append(strLpath.substring(
					index));
		}
		String strReplace = sb.toString();
//		D.dprint(strReplace);
//		D.dprint_method_end();
		return strReplace;
	}

	private static String translateReplace(
			String strLpath, Element element ) {
//		D.dprint_method_start();
//		D.dprint(strLpath);
		StringBuilder sb = new StringBuilder();
		int index = 0;
		Matcher m = patternTransReplace.matcher(
				strLpath);
		while (m.find()) {
//			D.dprint(m.group(0));
			if (index < m.start(0)) {
				sb.append(strLpath.substring(
						index, m.start(0)));
			}
			String strParent = m.group(1);
			NodeList nodeList = null;
			int offsetN = 0;
			if ((strParent == null) ||
					strParent.equals("") || strParent.equals("0")) {
				String strXpathParent = "../node";
				try {
					nodeList = (NodeList)xpath.evaluate(
							strXpathParent, element,
							XPathConstants.NODESET);
				} catch (XPathExpressionException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
				Node elementNode = (Node)element;
				for (int i=0; i<nodeList.getLength(); i++) {
					if (nodeList.item(i).isSameNode(elementNode)) {
						offsetN = i;
						break;
					}
				}
			} else {
				String strXpathParent = String.format(
						"ancestor::node[%s]", strParent);
				Node parentNode = null;
				try {
					parentNode = (Node)xpath.evaluate(
							strXpathParent, element,
							XPathConstants.NODE);
				} catch (XPathExpressionException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
//				strXpathParent += "/..";
				strXpathParent += "/../*[@TEXT]";
//				D.dprint(strXpathParent);
				try {
					nodeList = (NodeList)xpath.evaluate(
							strXpathParent, element,
							XPathConstants.NODESET);
				} catch (XPathExpressionException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
//				D.dprint(nodeList.getLength());
				for (int i=0; i<nodeList.getLength(); i++) {
					if (nodeList.item(i).isSameNode(parentNode)) {
						offsetN = i;
						D.dprint(offsetN);
						break;
					}
				}
			}
			sb.append(String.valueOf(offsetN+1));
			index = m.end(0);
//			D.dprint(index);
		}
		if (index < strLpath.length()) {
			sb.append(strLpath.substring(
					index));
		}
		String strReplace = sb.toString();
//		D.dprint(strReplace);
//		D.dprint_method_end();
		return strReplace;
	}

	// {@}　を　{./node[1]}
	static private String translateChild(
			String strLpath ) {
//		D.dprint_method_start();
//		D.dprint(strLpath);
		StringBuilder sb = new StringBuilder();
		int index = 0;
		Matcher m = patternTransChild.matcher(
				strLpath);
		while (m.find()) {
			if (index < m.start(0)) {
				sb.append(strLpath.substring(
						index, m.start(0)));
			}
			sb.append("./node[1]");
			index = m.end(0);
		}
		if (index < strLpath.length()) {
			sb.append(strLpath.substring(
					index));
		}
//		D.dprint(sb);
		String strReplace = sb.toString();
//		D.dprint(strReplace);
//		D.dprint_method_end();
		return strReplace;
	}

	// ./* [～]以外をnode[@TEXT='xxxx']に置換する
	static private String translateMM( String strLpath ) {
//		D.dprint_method_start();
//		D.dprint(strLpath);
		StringBuilder sb = new StringBuilder();
		int index = 0;
		Matcher matcher = patternTransWithout.matcher(
				strLpath);
		while (matcher.find()) {
	//		D.dprint(matcher.group(0));
			if (index < matcher.start(0)) {
				sb.append("node[@TEXT='");
				sb.append(strLpath.substring(
						index, matcher.start(0)));
				sb.append("']");
			}
			sb.append(matcher.group(0));
			index = matcher.end(0);
	//		D.dprint(index);
		}
		if (index < strLpath.length()) {
			sb.append("node[@TEXT='");
			sb.append(strLpath.substring(
					index));
			sb.append("']");
		}
		String strStill = sb.toString();
//		D.dprint(strStill);
		sb = new StringBuilder();
		index = 0;
		matcher = patternTransAll.matcher(
				strStill);
		while (matcher.find()) {
			if (index < matcher.start(0)) {
				sb.append(strStill.substring(
						index, matcher.start(0)));
			}
			sb.append("*[@TEXT]");
			index = matcher.end(0);
	//		D.dprint(index);
		}
		if (index < strStill.length()) {
			sb.append(strStill.substring(
					index));
		}
//		D.dprint(sb);
		String strReplace = sb.toString();
//		D.dprint(strReplace);
//		D.dprint_method_end();
		return strReplace;
	}

}
