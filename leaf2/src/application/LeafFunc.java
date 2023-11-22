package application;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;

public class LeafFunc {
//	private static final BigDecimal zero =
//			new BigDecimal(0);

	LeafNode node;
	int token;
	Element element;
	List<Expr> listExpr;

    boolean flagDone = false;
	String strError = null;

	BigDecimal value;
	boolean logiValue;
	String strValue;

	boolean flagNumeric;
	boolean flagLogi;
	boolean flagString;

	LeafFunc(LeafNode node, int token, Element element, List<Expr> listExpr) {
		this.node =node;
		this.token = token;
		this.element = element;
		this.listExpr = listExpr;
	}

	void preProc() {
//		D.dprint_method_start();
		switch (token) {
		case LeafTokens.MIN:
			min_func();
			break;
		case LeafTokens.MAX:
			max_func();
			break;
		case LeafTokens.SUM:
			sum_func();
			break;
		case LeafTokens.TRUE:
			true_func();
			break;
		case LeafTokens.FALSE:
			false_func();
			break;
		case LeafTokens.IF:
			if_func();
			break;
		case LeafTokens.IFS:
			ifs_func();
			break;
		case LeafTokens.INT:
			int_func();
			break;
		case LeafTokens.ROUNDDOWN:
			rounddown_func();
			break;
		case LeafTokens.ROUNDUP:
			roundup_func();
			break;
		case LeafTokens.LOOKUP:
			lookup_func();
			break;
		}
		flagDone = true;
//		D.dprint(value);
//		D.dprint_method_end();
	}

	String checkError() {
		if (! flagDone) {
			preProc();
		}
		return strError;
	}

	BigDecimal eval() { return value; }
    boolean evalLogi() { return logiValue; }
    String evalStr() { return strValue; }
    boolean isNumeric() {
            if (! flagDone) {
                    preProc();
            }
            return flagNumeric;
    }
    boolean isString() {
            if (! flagDone) {
                    preProc();       // 後で実行しても良いが
            }
            return true;
    }
    boolean isLogical() {
            if (! flagDone) {
                    preProc();
            }
            return flagLogi;
    }

    void min_func() {
//    	D.dprint_method_start();
    	BigDecimal val = null;
    	Iterator<Expr> list_iterated =
        		listExpr.iterator();
        while (list_iterated.hasNext()) {
            Expr expr = list_iterated.next();
            strError = expr.checkErrorMulti(node, element);
            if (strError != null) {
            	flagNumeric = false;
            	flagLogi = false;
            	flagString = false;
//            	D.dprint_method_end();
            	return;
            }
            if (expr.isMulti()) {
            	List<NodeExpr> listNode =
            			expr.getListNode();
            	Iterator<NodeExpr> iter = listNode.iterator();
            	while (iter.hasNext()) {
            		NodeExpr nodeExpr = iter.next();
                    strError = nodeExpr.checkError(
                    		node, element);
                    if (strError != null) {
                    	flagNumeric = false;
                    	flagLogi = false;
                    	flagString = false;
//                    	D.dprint_method_end();
                    	return;
                    }
    	            if (! nodeExpr.isNumeric(node, element)) {
    	            	strError = Message.MUST_NUMERIC;
    	            	flagNumeric = false;
    	            	flagLogi = false;
    	            	flagString = false;
//    	            	D.dprint_method_end();
    	            	return;
    	            }
    	            BigDecimal val2 = nodeExpr.eval();
//    	            D.dprint(val2);
    	            if (val == null) {
    	            	val = val2;
    	            } else {
    	            	val = val.min(val2);
    	            }
            	}
            } else {
	            if (! expr.isNumeric(node, element)) {
	            	strError = Message.MUST_NUMERIC;
	            	flagNumeric = false;
	            	flagLogi = false;
	            	flagString = false;
//	            	D.dprint_method_end();
	            	return;
	            }
	            BigDecimal val2 = expr.eval();
	            D.dprint(val2);
	            if (val == null) {
	            	val = val2;
	            } else {
	            	val = val.min(val2);
	            }
            }
        }
//        D.dprint(val);
        value = val;
        logiValue = (value.compareTo(BigDecimal.ZERO)==0)?true:false;
        strValue = null;
    	flagNumeric = true;
    	flagLogi = true;
    	flagString = false;
//    	D.dprint(this.flagNumeric);
//    	D.dprint_method_end();
    	return;
    }

    void max_func() {
    	D.dprint_method_start();
    	BigDecimal val = null;
    	Iterator<Expr> list_iterated =
        		listExpr.iterator();
        while (list_iterated.hasNext()) {
            Expr expr = list_iterated.next();
            strError = expr.checkErrorMulti(node, element);
            if (strError != null) {
            	flagNumeric = false;
            	flagLogi = false;
            	flagString = false;
            	D.dprint_method_end();
            	return;
            }
            D.dprint(expr.isMulti());
            if (expr.isMulti()) {
//            	D.dprint("max_func while if isMulti");
            	List<NodeExpr> listNode =
            			expr.getListNode();
            	Iterator<NodeExpr> iter = listNode.iterator();
            	while (iter.hasNext()) {
            		NodeExpr nodeExpr = iter.next();
                    strError = nodeExpr.checkError(
                    		node, element);
                    if (strError != null) {
                    	flagNumeric = false;
                    	flagLogi = false;
                    	flagString = false;
                    	D.dprint_method_end();
                    	return;
                    }
    	            if (! nodeExpr.isNumeric(node, element)) {
    	            	strError = Message.MUST_NUMERIC;
    	            	flagNumeric = false;
    	            	flagLogi = false;
    	            	flagString = false;
    	            	D.dprint_method_end();
    	            	return;
    	            }
    	            BigDecimal val2 = nodeExpr.eval();
//    	            D.dprint(val2);
    	            if (val == null) {
    	            	val = val2;
    	            } else {
    	            	val = val.max(val2);
    	            }
            	}
            } else {
//            	D.dprint(expr);
//            	D.dprint(expr.isNumeric(node, element));
	            if (! expr.isNumeric(node, element)) {
	            	strError = Message.MUST_NUMERIC;
	            	flagNumeric = false;
	            	flagLogi = false;
	            	flagString = false;
	            	D.dprint_method_end();
	            	return;
	            }
	            BigDecimal val2 = expr.eval();
//	            D.dprint(val2);
	            if (val == null) {
	            	val = val2;
	            } else {
	            	val = val.max(val2);
	            }
            }
        }
//        D.dprint(val);
        value = val;
        logiValue = (value.compareTo(BigDecimal.ZERO)==0)?true:false;
        strValue = null;
    	flagNumeric = true;
    	flagLogi = true;
    	flagString = false;
    	D.dprint_method_end();
    	return;
    }

    void sum_func() {
    	D.dprint_method_start();
    	BigDecimal val = new BigDecimal(0);
    	Iterator<Expr> list_iterated =
        		listExpr.iterator();
        while (list_iterated.hasNext()) {
            Expr expr = list_iterated.next();
            strError = expr.checkErrorMulti(node, element);
            D.dprint(strError);
            if (strError != null) {
            	flagNumeric = false;
            	flagLogi = false;
            	flagString = false;
            	D.dprint_method_end();
            	return;
            }
            if (expr.isMulti()) {
            	List<NodeExpr> listNode =
            			expr.getListNode();
            	Iterator<NodeExpr> iter = listNode.iterator();
            	while (iter.hasNext()) {
            		NodeExpr nodeExpr = iter.next();
                    strError = nodeExpr.checkError(
                    		node, element);
                    if (strError != null) {
                    	flagNumeric = false;
                    	flagLogi = false;
                    	flagString = false;
                    	D.dprint_method_end();
                    	return;
                    }
                    D.dprint(nodeExpr.isNumeric(node, element));
    	            if (! nodeExpr.isNumeric(node, element)) {
    	            	strError = Message.MUST_NUMERIC;
    	            	flagNumeric = false;
    	            	flagLogi = false;
    	            	flagString = false;
    	            	D.dprint_method_end();
    	            	return;
    	            }
    	            BigDecimal val2 = nodeExpr.eval();
    				val = val.add(val2);
            	}
            } else {
	            if (! expr.isNumeric(node, element)) {
	            	strError = Message.MUST_NUMERIC;
	            	flagNumeric = false;
	            	flagLogi = false;
	            	flagString = false;
	            	D.dprint_method_end();
	            	return;
	            }
	            BigDecimal val2 = expr.eval();
				val = val.add(val2);
            }
        }
//        D.dprint(val);
        value = val;
        logiValue = (value.compareTo(BigDecimal.ZERO)==0)?true:false;
        strValue = null;
    	flagNumeric = true;
    	flagLogi = true;
    	flagString = false;
    	D.dprint_method_end();
    	return;
    }

    void true_func() {
    	D.dprint_method_start();
        flagNumeric = true;
        flagLogi = true;
        flagString = false;
        value = BigDecimal.ONE;
        logiValue = true;
        strValue = "TRUE";
    	D.dprint_method_end();
    	return;
    }

    void false_func() {
    	D.dprint_method_start();
        flagNumeric = true;
        flagLogi = true;
        flagString = false;
        value = BigDecimal.ZERO;
        logiValue = false;
        strValue = "FALSE";
    	D.dprint_method_end();
    	return;
    }

    void if_func() {
    	D.dprint_method_start();
    	Expr condExpr = listExpr.get(0);
        strError = condExpr.checkError(node, element);
        if (strError != null) {
        	flagNumeric = false;
        	flagLogi = false;
        	flagString = false;
        	D.dprint_method_end();
        	return;
        }
        if (! condExpr.isLogical(node, element)) {
        	strError = Message.IF_COND_MUST_LOGICAL;
        	flagNumeric = false;
        	flagLogi = false;
        	flagString = false;
        	D.dprint_method_end();
        	return;
        }
    	Expr resultExpr;
        if (condExpr.evalLogi()) {
//        	D.dprint("true");
        	resultExpr = listExpr.get(1);
        } else {
//        	D.dprint("false");
        	resultExpr = listExpr.get(2);
        }
//        D.dprint(resultExpr.getClass().getCanonicalName());
        strError = resultExpr.checkError(node, element);
        if (strError != null) {
        	flagNumeric = false;
        	flagLogi = false;
        	flagString = false;
        	D.dprint_method_end();
        	return;
        }
//        D.dprint("if_func 途中");
//        D.dprint(resultExpr.isNumeric(node, element));
        flagNumeric = resultExpr.isNumeric(node, element);
//        D.dprint(flagNumeric);
        flagLogi = resultExpr.isLogical(node, element);
        flagString = resultExpr.isString(node, element);
        value = resultExpr.eval();
//        D.dprint(value);
        logiValue = resultExpr.evalLogi();
        strValue = resultExpr.evalStr();
    	D.dprint_method_end();
    	return;
    }

    /**
     * IFS(条件１、条件１が真の場合、
     * 		条件２、条件２が真の場合、
     * 		条件２が偽の場合)
     * IFS(条件１、条件１が真の場合、
     * 		条件２、条件２が真の場合、
     * 		条件３，条件３が真の場合)　条件３が偽ならエラー
     * 奇数と偶数で最後の処理が異なる？
     */
    void ifs_func() {
    	D.dprint_method_start();
    	int length = listExpr.size();
    	if (length < 3) {
        	strError = Message.IFS_INVALID_ARGUMENT_SIZE;
        	flagNumeric = false;
        	flagLogi = false;
        	flagString = false;
        	D.dprint_method_end();
        	return;
    	}
    	int index = 0;
    	Expr resultExpr;
    	while (true) {
	    	Expr condExpr = listExpr.get(index);
	        strError = condExpr.checkError(node, element);
	        if (strError != null) {
	        	flagNumeric = false;
	        	flagLogi = false;
	        	flagString = false;
	        	D.dprint_method_end();
	        	return;
	        }
	        if (! condExpr.isLogical(node, element)) {
	        	strError = Message.IF_COND_MUST_LOGICAL;
	        	flagNumeric = false;
	        	flagLogi = false;
	        	flagString = false;
	        	D.dprint_method_end();
	        	return;
	        }
	        if (condExpr.evalLogi()) {
	        	resultExpr = listExpr.get(index + 1);
	        } else {
	        	if (index + 1 + 1 == length) {
	        		strError = Message.IFS_INVALID_LAST_COND;
	            	flagNumeric = false;
	            	flagLogi = false;
	            	flagString = false;
	            	D.dprint_method_end();
	            	return;

	        	}
	        	if (index + 1 + 2 < length) {
	        		index += 2;
	        		continue;
	        	}
	        	resultExpr = listExpr.get(index + 2);
	        }
	        strError = resultExpr.checkError(node, element);
	        if (strError != null) {
	        	flagNumeric = false;
	        	flagLogi = false;
	        	flagString = false;
	        	D.dprint_method_end();
	        	return;
	        }
	        break;
    	}
        flagNumeric = resultExpr.isNumeric(node, element);
        flagLogi = resultExpr.isLogical(node, element);
        flagString = resultExpr.isString(node, element);
        value = resultExpr.eval();
        logiValue = resultExpr.evalLogi();
        strValue = resultExpr.evalStr();
    	D.dprint_method_end();
    	return;
    }

    void int_func() {
    	D.dprint_method_start();
    	Expr expr = listExpr.get(0);
        strError = expr.checkError(node, element);
        if (strError != null) {
        	flagNumeric = false;
        	flagLogi = false;
        	flagString = false;
        	D.dprint_method_end();
        	return;
        }
        if (! expr.isNumeric(node, element)) {
        	strError = Message.MUST_NUMERIC;
        	flagNumeric = false;
        	flagLogi = false;
        	flagString = false;
        	D.dprint_method_end();
        	return;
        }
        BigDecimal src = expr.eval();
        value = src.setScale(0, RoundingMode.DOWN);
        logiValue = (value.compareTo(BigDecimal.ZERO)==0)?true:false;
        flagNumeric = true;
        flagLogi = true;
        flagString = false;
    	D.dprint_method_end();
    	return;
    }

    void rounddown_func() {
    	D.dprint_method_start();
    	Expr expr = listExpr.get(0);
        strError = expr.checkError(node, element);
        if (strError != null) {
        	flagNumeric = false;
        	flagLogi = false;
        	flagString = false;
        	D.dprint_method_end();
        	return;
        }
        if (! expr.isNumeric(node, element)) {
        	strError = Message.MUST_NUMERIC;
        	flagNumeric = false;
        	flagLogi = false;
        	flagString = false;
        	D.dprint_method_end();
        	return;
        }
        int keta;
        if (listExpr.size() == 1) {
        	keta = 0;
        } else {
        	Expr ketaExpr = listExpr.get(1);
            strError = ketaExpr.checkError(node, element);
            if (strError != null) {
            	flagNumeric = false;
            	flagLogi = false;
            	flagString = false;
            	D.dprint_method_end();
            	return;
            }
            if (! ketaExpr.isNumeric(node, element)) {
            	strError = Message.MUST_NUMERIC;
            	flagNumeric = false;
            	flagLogi = false;
            	flagString = false;
            	D.dprint_method_end();
            	return;
            }
            BigDecimal ketaDouble = ketaExpr.eval();
            keta = ketaDouble.intValue();
        }
        BigDecimal src = expr.eval();
        value = src.setScale(keta, RoundingMode.DOWN).setScale(0);
        logiValue = (value.compareTo(BigDecimal.ZERO)==0)?true:false;
        flagNumeric = true;
        flagLogi = true;
        flagString = false;
    	D.dprint_method_end();
    	return;
    }

    void roundup_func() {
    	D.dprint_method_start();
    	Expr expr = listExpr.get(0);
        strError = expr.checkError(node, element);
        if (strError != null) {
        	flagNumeric = false;
        	flagLogi = false;
        	flagString = false;
        	D.dprint_method_end();
        	return;
        }
        if (! expr.isNumeric(node, element)) {
        	strError = Message.MUST_NUMERIC;
        	flagNumeric = false;
        	flagLogi = false;
        	flagString = false;
        	D.dprint_method_end();
        	return;
        }
        int keta;
        if (listExpr.size() == 1) {
        	keta = 0;
        } else {
        	Expr ketaExpr = listExpr.get(1);
            strError = ketaExpr.checkError(node, element);
            if (strError != null) {
            	flagNumeric = false;
            	flagLogi = false;
            	flagString = false;
            	D.dprint_method_end();
            	return;
            }
            if (! ketaExpr.isNumeric(node, element)) {
            	strError = Message.MUST_NUMERIC;
            	flagNumeric = false;
            	flagLogi = false;
            	flagString = false;
            	D.dprint_method_end();
            	return;
            }
            BigDecimal ketaDouble = ketaExpr.eval();
            keta = ketaDouble.intValue();
        }
        BigDecimal src = expr.eval();
        value = src.setScale(keta, RoundingMode.UP).setScale(0);
        logiValue = (value.compareTo(BigDecimal.ZERO)==0)?true:false;
        flagNumeric = true;
        flagLogi = true;
        flagString = false;
    	D.dprint_method_end();
    	return;
    }

    /**
     * LOOKUP(検索値, 検索対象, 取得データ, 検索方法)
     *  検索対象は複数列は不可だが、チェックできていない
     *  検索方法が、falseなら完全一致、trueなら下限値
     * @param args
     */
    void lookup_func() {
    	D.dprint_method_start();
    	Expr findExpr = listExpr.get(0);
        strError = findExpr.checkError(node, element);
        if (strError != null) {
        	flagNumeric = false;
        	flagLogi = false;
        	flagString = false;
        	D.dprint_method_end();
        	return;
        }
//        if (! findExpr.isLogical(node, element)) {
//        	strError = Message.IF_COND_MUST_LOGICAL;
//        	flagNumeric = false;
//        	flagLogi = false;
//        	flagString = false;
//        	D.dprint_method_end();
//        	return;
//        }
        Expr targetExpr = listExpr.get(1);
        strError = targetExpr.checkErrorMulti(node, element);
        if (strError != null) {
        	flagNumeric = false;
        	flagLogi = false;
        	flagString = false;
        	D.dprint_method_end();
        	return;
        }
        if (! targetExpr.isMulti()) {
        	strError = Message.MUST_TABLE_ARG;
        	flagNumeric = false;
        	flagLogi = false;
        	flagString = false;
        	D.dprint_method_end();
        	return;
        }
    	List<NodeExpr> listNode2 =
    			targetExpr.getListNode();

    	Expr condExpr = listExpr.get(3);
    	boolean cond = true;
    	if (condExpr != null) {
	    	strError = condExpr.checkError(node, element);
	        if (strError != null) {
	        	flagNumeric = false;
	        	flagLogi = false;
	        	flagString = false;
	        	D.dprint_method_end();
	        	return;
	        }
	        if (! condExpr.isLogical(node, element)) {
	        	strError = Message.MUST_LOGICAL;
	        	flagNumeric = false;
	        	flagLogi = false;
	        	flagString = false;
	        	D.dprint_method_end();
	        	return;
	        }
	        cond = condExpr.evalLogi();
    	}
        if (! cond) {
        	// 完全一致
        	if (findExpr.isNumeric(node, element)) {
        		// 数値
        		BigDecimal value1 = findExpr.eval();
        		for (int i=0; i<listNode2.size(); i++) {
        			NodeExpr expr2 = listNode2.get(i);
        			strError = expr2.checkError(node, element);
        			if (strError != null) {
        	        	flagNumeric = false;
        	        	flagLogi = false;
        	        	flagString = false;
        	        	D.dprint_method_end();
        	        	return;
        			}
        			if (! expr2.isNumeric(node, element)) {
        				strError = Message.MUST_NUMERIC;
        	        	flagNumeric = false;
        	        	flagLogi = false;
        	        	flagString = false;
        	        	D.dprint_method_end();
        	        	return;
        			}
        			BigDecimal value2 = expr2.eval();
        			if (value1.compareTo(value2)==0) {
        		        Expr dataExpr = listExpr.get(2);
        		        strError = dataExpr.checkErrorMulti(node, element);
        		        if (strError != null) {
        		        	flagNumeric = false;
        		        	flagLogi = false;
        		        	flagString = false;
        		        	D.dprint_method_end();
        		        	return;
        		        }
        		        if (! dataExpr.isMulti()) {
        		        	strError = Message.MUST_TABLE_ARG;
        		        	flagNumeric = false;
        		        	flagLogi = false;
        		        	flagString = false;
        		        	D.dprint_method_end();
        		        	return;
        		        }
        		    	List<NodeExpr> listNode3 =
        		    			dataExpr.getListNode();
        		    	// TODO 範囲オーバー
        		    	NodeExpr expr3 = listNode3.get(i);
            			strError = expr3.checkError(node, element);
            			if (strError != null) {
            	        	flagNumeric = false;
            	        	flagLogi = false;
            	        	flagString = false;
            	        	D.dprint_method_end();
            	        	return;
            			}
            	        value = expr3.eval();
            	        D.dprint(value);
            	        logiValue = expr3.evalLogi();
            	        strValue = expr3.evalStr();
            	        flagNumeric = expr3.flagNumeric;
            	        flagLogi = expr3.flagLogi;
            	    	D.dprint_method_end();
            	    	return;
        			}
        		}
    			strError = Message.NOT_FOUND_DATA;
    			strValue = strError;
    	        flagNumeric = false;
    	        flagLogi = false;
    	    	D.dprint_method_end();
    	    	return;
        	} else {
        		// 文字列
        		String value1 = findExpr.evalStr();
        		for (int i=0; i<listNode2.size(); i++) {
        			NodeExpr expr2 = listNode2.get(i);
        			strError = expr2.checkError(node, element);
        			if (strError != null) {
        	        	flagNumeric = false;
        	        	flagLogi = false;
        	        	flagString = false;
        	        	D.dprint_method_end();
        	        	return;
        			}
        			if (! expr2.isNumeric(node, element)) {
        				strError = Message.MUST_NUMERIC;
        	        	flagNumeric = false;
        	        	flagLogi = false;
        	        	flagString = false;
        	        	D.dprint_method_end();
        	        	return;
        			}
        			String value2 = expr2.evalStr();
        			if (value1.compareTo(value2)==0) {
        		        Expr dataExpr = listExpr.get(2);
        		        strError = dataExpr.checkErrorMulti(node, element);
        		        if (strError != null) {
        		        	flagNumeric = false;
        		        	flagLogi = false;
        		        	flagString = false;
        		        	D.dprint_method_end();
        		        	return;
        		        }
        		        if (! dataExpr.isMulti()) {
        		        	strError = Message.MUST_TABLE_ARG;
        		        	flagNumeric = false;
        		        	flagLogi = false;
        		        	flagString = false;
        		        	D.dprint_method_end();
        		        	return;
        		        }
        		    	List<NodeExpr> listNode3 =
        		    			dataExpr.getListNode();
        		    	if (i>= listNode3.size()) {
        		    		strError = Message.TOO_BIG;
            	        	flagNumeric = false;
            	        	flagLogi = false;
            	        	flagString = false;
            	        	D.dprint_method_end();
            	        	return;

        		    	}
        		    	NodeExpr expr3 = listNode3.get(i);
            			strError = expr3.checkError(node, element);
            			if (strError != null) {
            	        	flagNumeric = false;
            	        	flagLogi = false;
            	        	flagString = false;
            	        	D.dprint_method_end();
            	        	return;
            			}
            	        value = expr3.eval();
            	        D.dprint(value);
            	        logiValue = expr3.evalLogi();
            	        strValue = expr3.evalStr();
            	        flagNumeric = expr3.flagNumeric;
            	        flagLogi = expr3.flagLogi;
            	    	D.dprint_method_end();
            	    	return;
        			}
        		}
    			strError = Message.NOT_FOUND_DATA;
    			strValue = strError;
    	        flagNumeric = false;
    	        flagLogi = false;
    	    	D.dprint_method_end();
    	    	return;
        	}
        } else {
        	// 下限値
        	if (! findExpr.isNumeric(node, element)) {
				strError = Message.MUST_NUMERIC;
	        	flagNumeric = false;
	        	flagLogi = false;
	        	flagString = false;
	        	D.dprint_method_end();
	        	return;
        	}
       		// 数値
    		BigDecimal value1 = findExpr.eval();
//    		D.dprint("listNode size");
//    		D.dprint(listNode2.size());
    		int i;
    		for (i=0; i<listNode2.size(); i++) {
    			NodeExpr expr2 = listNode2.get(i);
    			strError = expr2.checkError(node, element);
    			if (strError != null) {
    	        	flagNumeric = false;
    	        	flagLogi = false;
    	        	flagString = false;
    	        	D.dprint_method_end();
    	        	return;
    			}
    			if (! expr2.isNumeric(node, element)) {
    				strError = Message.MUST_NUMERIC;
    	        	flagNumeric = false;
    	        	flagLogi = false;
    	        	flagString = false;
    	        	D.dprint_method_end();
    	        	return;
    			}
    			BigDecimal value2 = expr2.eval();
    			if (value1.compareTo(value2)<0) {
    				// OKとする
//    				if (i == 0) {
//    					strError = Message.TOO_LITTLE;
//    		        	flagNumeric = false;
//    		        	flagLogi = false;
//    		        	flagString = false;
//    		        	D.dprint_method_end();
//    		        	return;
//    				}
    				break;
    			}
    		}
			i -= 1;
	        Expr dataExpr = listExpr.get(2);
	        strError = dataExpr.checkErrorMulti(node, element);
	        if (strError != null) {
	        	flagNumeric = false;
	        	flagLogi = false;
	        	flagString = false;
	        	D.dprint_method_end();
	        	return;
	        }
	        if (! dataExpr.isMulti()) {
	        	strError = Message.MUST_TABLE_ARG;
	        	flagNumeric = false;
	        	flagLogi = false;
	        	flagString = false;
	        	D.dprint_method_end();
	        	return;
	        }
	    	List<NodeExpr> listNode3 =
	    			dataExpr.getListNode();
	    	if (i>= listNode3.size()) {
	    		strError = Message.TOO_BIG;
	        	flagNumeric = false;
	        	flagLogi = false;
	        	flagString = false;
	        	D.dprint_method_end();
	        	return;
	    	}
//	    	D.dprint(i);
	    	NodeExpr expr3 = listNode3.get(i);
			strError = expr3.checkError(node, element);
			if (strError != null) {
	        	flagNumeric = false;
	        	flagLogi = false;
	        	flagString = false;
	        	D.dprint_method_end();
	        	return;
			}
	        value = expr3.eval();
//	        D.dprint(value);
	        logiValue = expr3.evalLogi();
	        strValue = expr3.evalStr();
	        flagNumeric = expr3.flagNumeric;
	        flagLogi = expr3.flagLogi;
	    	D.dprint_method_end();
	    	return;
        }
    }

public static void main(String[] args) {
	BigDecimal value = new BigDecimal("123.456");
	D.dprint(value.setScale(0,RoundingMode.DOWN));
	D.dprint(value.setScale(1,RoundingMode.DOWN));
	D.dprint(value.setScale(-1,RoundingMode.DOWN).setScale(0));
}

}
