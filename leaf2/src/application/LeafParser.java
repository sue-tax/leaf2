// Output created by jacc on Thu Nov 23 22:26:53 JST 2023


package application;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

abstract class Expr {
        String strError = null;
        boolean flagDone = false;

        // 事前処理（実際は、ここで計算する）
        abstract void preProc(LeafNode node, Element element);
        // ChildAtとTagは上書き
        void preProcMulti(LeafNode node, Element element) {
                preProc(node, element);
        }

        String checkError(LeafNode node, Element element) {
                if (! flagDone) {
                        preProc(node, element);
                }
                return strError;
        }
        String checkErrorMulti(LeafNode node, Element element) {
//                D.dprint_method_start();
                if (! flagDone) {
                        preProcMulti(node, element);
                }
//                D.dprint(strError);
                //D.dprint_method_end();
                return strError;
        }
        boolean isMulti() { return false; }
        List<NodeExpr> getListNode() { return null; }

        BigDecimal eval() { return BigDecimal.ZERO;}
        boolean evalLogi() { return false; }
        String evalStr() { return ""; }
        abstract boolean isNumeric(LeafNode node, Element element);
        abstract boolean isString(LeafNode node, Element element);
        abstract boolean isLogical(LeafNode node, Element element);
}

class NumberExpr extends Expr {
        private BigDecimal value;
    NumberExpr(String value) {
                String rep_value = value.replace("_", "");
                this.value = new BigDecimal(rep_value); }
        void preProc(LeafNode node, Element element) {}
        BigDecimal eval() { return value; }
        boolean evalLogi() { return value.compareTo(BigDecimal.ZERO) == 0; }
        boolean isNumeric(LeafNode node, Element element) { return true; }
        boolean isString(LeafNode node, Element element) { return false; }
        boolean isLogical(LeafNode node, Element element) { return true; }
}

class StrExpr extends Expr {
        private String str;
        StrExpr(String value) { this.str = value; }
        void preProc(LeafNode node, Element element) {}
    String evalStr() { return str; }
        boolean isNumeric(LeafNode node, Element element) { return false; }
        boolean isString(LeafNode node, Element element) { return true; }
        boolean isLogical(LeafNode node, Element element) { return false; }
}

class ChildAtExpr extends Expr {
        boolean flagNumeric;
        // boolean flagString;
        boolean flagLogi;
        BigDecimal value;
        String strValue;
        boolean logiValue;

        private static XPath xpath = XPathFactory.
                        newInstance().  newXPath();

        ChildAtExpr() {}
        void preProc(LeafNode node, Element element) {
                if (flagDone) {
                        return;
                }
                flagDone = true;
                String strXpath = "./node[1]";
                NodeList nodeList = null;
                try {
                        nodeList = (NodeList)xpath.evaluate(
                                        strXpath, element,
                                        XPathConstants.NODESET);
                } catch (XPathExpressionException e) {
                        //e.printStackTrace();
                        strError = Message.SYSTEM_ERROR_
                                        + e.toString();
        //              D.dprint(strError);
                //      D.dprint_method_end();
                        return;
                }
//              D.dprint(nodeList);
                if (nodeList.getLength() == 0) {
                        strError = Message.NOT_FOUND_NODE;
//                      D.dprint(strError);
                        //D.dprint_method_end();
                        return;
                }
                String strNodeName = ((Element)nodeList.item(0)).
                                getAttribute("TEXT");
        String strNumber = strNodeName.replaceAll("[_,]", "");
//        D.dprint(strNumber);
                try {
                        value = new BigDecimal(strNumber);
                        flagNumeric = true;
                logiValue = (value.compareTo(BigDecimal.ZERO)==0)?true:false;
                        flagLogi = true;
        } catch(Exception e) {
                        flagNumeric = false;
                        flagLogi = false;
                }
                strValue = strNumber;
        }
        // TODO preProcMulti, isMulti

        BigDecimal eval() { return value; }
        boolean evalLogi() { return logiValue; }
        String evalStr() { return strValue; }
        boolean isNumeric(LeafNode node, Element element) {
                if (! flagDone) {
                        preProc(node, element);
                }
                return flagNumeric;
        }
        boolean isString(LeafNode node, Element element) {
                if (! flagDone) {
                        preProc(node, element); // 後で実行しても良いが
                }
                return true;
        }
        boolean isLogical(LeafNode node, Element element) {
                if (! flagDone) {
                        preProc(node, element);
                }
                return flagLogi;
        }
}

class OpExpr extends Expr {
        private String operator;
        private int token;
        OpExpr(String value, int token) {
                this.operator = value;
                this.token = token; }
        int getToken() { return token; }
        @Override
        void preProc(LeafNode node, Element element) {}
        @Override
        boolean isNumeric(LeafNode node, Element element) {
                return false;
        }
        @Override
        boolean isString(LeafNode node, Element element) {
                return false;
        }
        @Override
        boolean isLogical(LeafNode node, Element element) {
                return false;
                        }
}

abstract class SingleExpr extends Expr {
    protected Expr single;
    SingleExpr(Expr single) {
        this.single = single;
    }
}

class NotExpr extends SingleExpr {
        NotExpr(Expr single) { super(single); }
        boolean flagDone = false;
        BigDecimal value;
        boolean logiValue;
        String strValue;

        void preProc(LeafNode node, Element element) {
                if (flagDone) {
                        return;
                }
                flagDone = true;
                strError = single.checkError(node, element);
                if (strError != null) {
                        return;
                }
                if (! single.isLogical(node, element)) {
                        strError = Message.MUST_LOGICAL;
                        return;
                }
                logiValue = ! single.evalLogi();
                value = logiValue?BigDecimal.ONE:BigDecimal.ZERO;
                strValue = logiValue?"TRUE":"FALSE";
        }

        BigDecimal eval() { return value; }
        boolean evalLogi() { return logiValue; }
        boolean isNumeric(LeafNode node, Element element) {
                if (! flagDone) {
                        preProc(node, element);
                }
                return true;
        }
        boolean isString(LeafNode node, Element element) {
                if (! flagDone) {
                        preProc(node, element); // 後で実行しても良いが
                }
                return true;
        }
        boolean isLogical(LeafNode node, Element element) {
                if (! flagDone) {
                        preProc(node, element);
                }
                return true;
        }
}

abstract class BinExpr extends Expr {
        protected Expr left, right;
        BinExpr(Expr left, Expr right) {
                this.left = left;  this.right = right;
        }
}

abstract class LogiExpr extends BinExpr {
        LogiExpr(Expr left, Expr right) {
                super(left, right); }
        boolean flagDone = false;
        BigDecimal value;
        boolean logiValue;

        void preProc(LeafNode node, Element element) {
                if (flagDone) {
                        return;
                }
                flagDone = true;
                strError = left.checkError(node, element);
                if (strError != null) {
                        return;
                }
                strError = right.checkError(node, element);
                if (strError != null) {
                        return;
                }

                if (! left.isLogical(node, element)) {
                        strError = Message.MUST_LOGICAL;
                        return;
                }
                if (! right.isLogical(node, element)) {
                        strError = Message.MUST_LOGICAL;
                        return;
                }
                logiValue = proc();
                value = logiValue?BigDecimal.ONE:BigDecimal.ZERO;
        }
        abstract boolean proc();

        BigDecimal eval() { return value; }
        boolean evalLogi() { return logiValue; }
        boolean isNumeric(LeafNode node, Element element) {
                if (! flagDone) {
                        preProc(node, element);
                }
                return true;
        }
        boolean isString(LeafNode node, Element element) {
                if (! flagDone) {
                        preProc(node, element); // 後で実行しても良いが
                }
                return false;
        }
        boolean isLogical(LeafNode node, Element element) {
                if (! flagDone) {
                        preProc(node, element);
                }
                return true;
        }

}

class OrExpr extends LogiExpr {
        OrExpr(Expr left, Expr right) {
                super(left, right); }
        boolean proc() {
                return left.evalLogi() || right.evalLogi(); }
}

class AndExpr extends LogiExpr {
        AndExpr(Expr left, Expr right) {
                super(left, right); }
        boolean proc() {
                return left.evalLogi() && right.evalLogi(); }
}

abstract class HikakuExpr extends BinExpr {
        HikakuExpr(Expr left, Expr right) {
                super(left, right); }
        boolean flagDone = false;
        BigDecimal value;
        boolean logiValue;

        void preProc(LeafNode node, Element element) {
                if (flagDone) {
                        return;
                }
                flagDone = true;
                strError = left.checkError(node, element);
                if (strError != null) {
                        return;
                }
                strError = right.checkError(node, element);
                if (strError != null) {
                        return;
                }
                if (! left.isNumeric(node, element)) {
                        strError = Message.MUST_NUMERIC;
                        return;
                }
                if (! right.isNumeric(node, element)) {
                        strError = Message.MUST_NUMERIC;
                        return;
                }
                logiValue = proc();
                value = logiValue?BigDecimal.ONE:BigDecimal.ZERO;
        }
        abstract boolean proc();

        BigDecimal eval() { return value; }
        boolean evalLogi() { return logiValue; }
        boolean isNumeric(LeafNode node, Element element) {
                if (! flagDone) {
                        preProc(node, element);
                }
                return true;
        }
        boolean isString(LeafNode node, Element element) {
                if (! flagDone) {
                        preProc(node, element); // 後で実行しても良いが
                }
                return false;
        }
        boolean isLogical(LeafNode node, Element element) {
                if (! flagDone) {
                        preProc(node, element);
                }
                return true;
        }
}

class EqualExpr extends HikakuExpr {
        EqualExpr(Expr left, Expr right) {
                super(left, right); }
        boolean proc() {
                return (left.eval().compareTo(right.eval())==0);
        }
}

class NotEqualExpr extends HikakuExpr {
        NotEqualExpr(Expr left, Expr right) {
                super(left, right); }
        boolean proc() {
                return (left.eval().compareTo(right.eval())!=0);
        }
}

class DainariExpr extends HikakuExpr {
        DainariExpr(Expr left, Expr right) {
                super(left, right); }
        boolean proc() {
                return (left.eval().compareTo(right.eval())==1);
        }
}

class DainariEqualExpr extends HikakuExpr {
        DainariEqualExpr(Expr left, Expr right) {
                super(left, right); }
        boolean proc() {
                return (left.eval().compareTo(right.eval())>=0);
//              return (left.eval() >= right.eval());
        }
}

class ShounariExpr extends HikakuExpr {
        ShounariExpr(Expr left, Expr right) {
                super(left, right); }
        boolean proc() {
                return (left.eval().compareTo(right.eval())<0);
//              return (left.eval() < right.eval());
        }
}

class ShounariEqualExpr extends HikakuExpr {
        ShounariEqualExpr(Expr left, Expr right) {
                super(left, right); }
        boolean proc() {
                return (left.eval().compareTo(right.eval())<=0);
//              return (left.eval() <= right.eval());
        }
}

abstract class AccExpr extends BinExpr {
        AccExpr(Expr left, Expr right) {
                super(left, right); }
        boolean flagDone = false;
        BigDecimal value;
        boolean logiValue;

        void preProc(LeafNode node, Element element) {
                if (flagDone) {
                        return;
                }
                flagDone = true;
                strError = left.checkError(node, element);
                if (strError != null) {
                        return;
                }
                strError = right.checkError(node, element);
                if (strError != null) {
                        return;
                }

                if (! left.isNumeric(node, element)) {
                        strError = Message.MUST_NUMERIC;
                        return;
                }
                if (! right.isNumeric(node, element)) {
                        strError = Message.MUST_NUMERIC;
                        return;
                }
                value = proc();
        logiValue = (value.compareTo(BigDecimal.ZERO)==0)?true:false;
        }
        abstract BigDecimal proc();

        BigDecimal eval() { return value; }
        boolean evalLogi() { return logiValue; }
        boolean isNumeric(LeafNode node, Element element) {
                if (! flagDone) {
                        preProc(node, element);
                }
                return true;
        }
        boolean isString(LeafNode node, Element element) {
                if (! flagDone) {
                        preProc(node, element); // 後で実行しても良いが
                }
                return false;
        }
        boolean isLogical(LeafNode node, Element element) {
                if (! flagDone) {
                        preProc(node, element);
                }
                return true;
        }
}

class AddExpr extends AccExpr {
        AddExpr(Expr left, Expr right) {
                super(left, right); }
        BigDecimal proc() {
        return left.eval().add(right.eval()); }
}

class SubExpr extends AccExpr {
        SubExpr(Expr left, Expr right) {
                super(left, right); }
        BigDecimal proc() {
                return left.eval().subtract(right.eval()); }
}

class MultExpr extends AccExpr {
        MultExpr(Expr left, Expr right) {
                super(left, right); }
        BigDecimal proc() {
                return left.eval().multiply(right.eval()); }
}

class DivExpr extends AccExpr {
        DivExpr(Expr left, Expr right) {
                super(left, right); }
        BigDecimal proc() {
                return left.eval().divide(right.eval()); }
}

class RuijouExpr extends AccExpr {
        RuijouExpr(Expr left, Expr right) {
                super(left, right); }
        BigDecimal proc() {
                return new BigDecimal(Math.pow(
                        left.eval().doubleValue(),
                        right.eval().doubleValue())); }
}

class TagExpr extends Expr {
        StrExpr strMarkDef;
        StrExpr strLpath;
        StrExpr strMarkRef;
        IndexExpr index1;
        IndexExpr index2;

        boolean flagNumeric;
        // boolean flagString;
        boolean flagLogi;
        BigDecimal value;
        String strValue;
        boolean logiValue;

        List<NodeExpr>listNode = null;
        boolean flagMulti = false;

        private static XPath xpath = XPathFactory.
                        newInstance().  newXPath();

        TagExpr(StrExpr markdef, StrExpr lpath,
                        StrExpr markref) {
                strMarkDef = markdef;
                strLpath = lpath;
                strMarkRef = markref;
        }
        public void setIndex(IndexExpr index1, IndexExpr index2) {
                this.index1 = index1;
                this.index2 = index2;
        }

        void setError(String strError) {
                this.strError = strError;
        }


        void preProc(LeafNode node, Element element) {
                if (flagDone) {
                        return;
                }
                flagDone = true;

                Document document = element.
                                getOwnerDocument();
                CalcTag calcTag = new CalcTag(
                                node, document, this, element);

                // TODO 指定されたTagのノードが
                // 表だった場合の処理

                String strCalc = calcTag.calcExprTag();
                if (strCalc == null) {
                        strError = calcTag.getError();
                        return;
                }
                try {
                        value = new BigDecimal(strCalc);
                        flagNumeric = true;
                logiValue = (value.compareTo(BigDecimal.ZERO)==0)?true:false;
                        flagLogi = true;
        } catch(Exception e) {
                        flagNumeric = false;
                        flagLogi = false;
                }
                strValue = strCalc;
        }

        // 実際のタグの評価は、ここではしない
        void preProcMulti(LeafNode node, Element element) {
                if (flagDone) {
                        return;
                }
                flagDone = true;

                Document document = element.
                                getOwnerDocument();
                CalcTag calcTag = new CalcTag(
                                node, document, this, element);
        listNode = calcTag.calcExprTagMulti();
        if (listNode == null) {
                strError = calcTag.getError();
            return;
        }

                // TODO 指定されたTagのノードが
                // 表だった場合の処理

                flagMulti = true;
        }
        boolean isMulti() { return flagMulti; }

        BigDecimal eval() { return value; }
        boolean evalLogi() { return logiValue; }
        String evalStr() { return strValue; }
        List<NodeExpr> getListNode() { return listNode; }

        boolean isNumeric(LeafNode node, Element element) {
                if (! flagDone) {
                        preProc(node, element);
                }
                return flagNumeric;
        }
        boolean isString(LeafNode node, Element element) {
                if (! flagDone) {
                        preProc(node, element); // 後で実行しても良いが
                }
                return true;
        }
        boolean isLogical(LeafNode node, Element element) {
                if (! flagDone) {
                        preProc(node, element);
                }
                return flagLogi;
        }
}

class NodeExpr extends Expr {
        // Tagを評価した際に複数のノードが該当する場合の
        // 個々のElement
        //LeafNode node;
        Document document;
        Element nodeElement;
        boolean flagDone = false;

        boolean flagNumeric;
        // boolean flagString;
        boolean flagLogi;
        BigDecimal value;
        String strValue;
        boolean logiValue;

        NodeExpr(Document document, Element element) {
                //this.node = node;
                this.document = document;
                this.nodeElement = element;
        }

        public Document getDocument() {
                return document;
        }

        void preProc(LeafNode node, Element element) {
                if (flagDone) {
                        return;
                }
                flagDone = true;

                Document document = nodeElement.
                                getOwnerDocument();
                LeafNode leafNode = new LeafNode(document,
                                nodeElement);
                leafNode.calcNode();
        String strCalc = LeafNode.getValue(nodeElement);
                try {
                        value = new BigDecimal(strCalc);
                        flagNumeric = true;
                logiValue = (value.compareTo(BigDecimal.ZERO)==0)?true:false;
                        flagLogi = true;
        } catch(Exception e) {
                        flagNumeric = false;
                        flagLogi = false;
                }
                strValue = strCalc;
        }

        BigDecimal eval() { return value; }
        boolean evalLogi() { return logiValue; }
        String evalStr() { return strValue; }

        boolean isNumeric(LeafNode node, Element element) {
                if (! flagDone) {
                        preProc(node, element);
                }
                return flagNumeric;
        }
        boolean isString(LeafNode node, Element element) {
                if (! flagDone) {
                        preProc(node, element); // 後で実行しても良いが
                }
                return true;
        }
        boolean isLogical(LeafNode node, Element element) {
                if (! flagDone) {
                        preProc(node, element);
                }
                return flagLogi;
        }
}


class StringNodeExpr extends NodeExpr {
        // 文字列から作るダミー的なNodeExpr
        StringNodeExpr(Document document, Element element,
                        String strValue) {
        super(document, element);
                this.strValue = strValue;
                try {
                        value = new BigDecimal(strValue);
                        flagNumeric = true;
                logiValue = (value.compareTo(BigDecimal.ZERO)
                                ==0)?true:false;
                        flagLogi = true;
        } catch(Exception e) {
                        flagNumeric = false;
                        flagLogi = false;
                }
        }

        void preProc(LeafNode node, Element element) {
                return;
        }
}


class IndexShikiExpr extends Expr {
        // Tagを評価した際に表だった際の
        // 表の行・列を指定するインデックス

        // 数式、`#`、`#+数式`、`#-数式`、文字列
        Expr expr;
        boolean sharp;
        boolean plus;

        IndexShikiExpr(
                        Expr expr, boolean sharp, boolean plus ) {
                this.expr = expr;
                this.sharp = sharp;
                this.plus = plus;
        }

                @Override
                void preProc(LeafNode node, Element element) {
                        // TODO 自動生成されたメソッド・スタブ

                }

                @Override
                boolean isNumeric(LeafNode node, Element element) {
                        // TODO 自動生成されたメソッド・スタブ
                        return false;
                }

                @Override
                boolean isString(LeafNode node, Element element) {
                        // TODO 自動生成されたメソッド・スタブ
                        return false;
                }

                @Override
                boolean isLogical(LeafNode node, Element element) {
                        // TODO 自動生成されたメソッド・スタブ
                        return false;
                }

}

class IndexExpr extends Expr {
        // `[インデックス式]`　１行、１列の指定
        // `[:]`　行全体、列全体の指定
        // `[インデックス式:]`　インデックス式から最後までの指定（インデックス式で指定した行、列を含む）
        // `[:インデックス式]` 　最初からインデックス式までの指定（インデックス式で指定した行、列を含む）
        IndexShikiExpr single;
        boolean all;
        IndexShikiExpr start;
        IndexShikiExpr end;

        IndexExpr( IndexShikiExpr single, boolean all,
                        IndexShikiExpr start, IndexShikiExpr end ) {
                this.single = single;
                this.all = all;
                this.start = start;
                this.end = end;
        }
                        @Override
                void preProc(LeafNode node, Element element) {
                        // TODO 自動生成されたメソッド・スタブ

                }

                @Override
                boolean isNumeric(LeafNode node, Element element) {
                        // TODO 自動生成されたメソッド・スタブ
                        return false;
                }

                @Override
                boolean isString(LeafNode node, Element element) {
                        // TODO 自動生成されたメソッド・スタブ
                        return false;
                }

                @Override
                boolean isLogical(LeafNode node, Element element) {
                        // TODO 自動生成されたメソッド・スタブ
                        return false;
                }

}

class CellExpr extends NodeExpr {
        // Tagを評価した際に表のセルが指定された場合の
        // 個々のElement

        CellExpr(Document document, Element element) {
                //this.node = node;
                super(document, element);
        }

        void preProc(LeafNode node, Element element) {
                if (flagDone) {
                        return;
                }
                flagDone = true;

                Document document = nodeElement.
                                getOwnerDocument();
                LeafNode leafNode = new LeafNode(document,
                                nodeElement);
                String strCalc = leafNode.calcCell();
        strCalc = strCalc.trim();
        		D.dprint(strCalc);
                try {
                        value = new BigDecimal(strCalc);
                        flagNumeric = true;
                logiValue = (value.compareTo(BigDecimal.ZERO)==0)?true:false;
                        flagLogi = true;
        } catch(Exception e) {
                        flagNumeric = false;
                        flagLogi = false;
                }
                strValue = strCalc;
        }
}

class ListExpr extends Expr {
        List<Expr> listExpr;
        ListExpr() {
                listExpr = new ArrayList<Expr>();
        }
        ListExpr(Expr expr) {
                listExpr = new ArrayList<Expr>();
                listExpr.add(expr);
        }
        void add(Expr expr) {
                listExpr.add(expr);
        }
        List<Expr> getListExpr() {
                return listExpr;
        }
                @Override
                void preProc(LeafNode node, Element element) {
                }
                @Override
                boolean isNumeric(LeafNode node, Element element) {
                        return false;
                }
                @Override
                boolean isString(LeafNode node, Element element) {
                        return false;
                }
                @Override
                boolean isLogical(LeafNode node, Element element) {
                        return false;
                }
}


class FuncExpr extends Expr {
        int token;
        List<Expr> listExpr;

        boolean flagNumeric;
        boolean flagString;
        boolean flagLogi;
        BigDecimal value;
        String strValue;
        boolean logiValue;

        FuncExpr(Expr token, Expr listExpr) {
                this.token = ((OpExpr)token).getToken();
                this.listExpr = ((ListExpr)listExpr).getListExpr();
        }

        void preProc(LeafNode node, Element element) {
                if (flagDone) {
                        return;
                }
                flagDone = true;

                LeafFunc leafFunc = new LeafFunc(
                                node, token, element, listExpr);
                strError = leafFunc.checkError();
                if (strError != null) {
                        flagNumeric = false;
                        flagLogi = false;
                        flagString = false;
                        return;
                }
                value = leafFunc.value;
                logiValue = leafFunc.logiValue;
                strValue = leafFunc.strValue;
                flagNumeric = leafFunc.flagNumeric;
                flagLogi = leafFunc.flagLogi;
        }

        BigDecimal eval() { return value; }
        boolean evalLogi() { return logiValue; }
        String evalStr() { return strValue; }
        boolean isNumeric(LeafNode node, Element element) {
                if (! flagDone) {
                        preProc(node, element);
                }
                return flagNumeric;
        }
        boolean isString(LeafNode node, Element element) {
                if (! flagDone) {
                        preProc(node, element); // 後で実行しても良いが
                }
                return true;
        }
        boolean isLogical(LeafNode node, Element element) {
                if (! flagDone) {
                        preProc(node, element);
                }
                return flagLogi;
        }
}


class LeafParser implements LeafTokens {
    private int yyss = 100;
    private int yytok;
    private int yysp = 0;
    private int[] yyst;
    protected int yyerrno = (-1);
    private Expr[] yysv;
    private Expr yyrv;

    public boolean parse() {
        int yyn = 0;
        yysp = 0;
        yyst = new int[yyss];
        yysv = new Expr[yyss];
        yytok = (lexer.getToken()
                 );
    loop:
        for (;;) {
            switch (yyn) {
                case 0:
                    yyst[yysp] = 0;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 144:
                    yyn = yys0();
                    continue;

                case 1:
                    yyst[yysp] = 1;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 145:
                    switch (yytok) {
                        case ENDINPUT:
                            yyn = 288;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 2:
                    yyst[yysp] = 2;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 146:
                    yyn = yys2();
                    continue;

                case 3:
                    yyst[yysp] = 3;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 147:
                    yyn = yys3();
                    continue;

                case 4:
                    yyst[yysp] = 4;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 148:
                    yyn = yys4();
                    continue;

                case 5:
                    yyst[yysp] = 5;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 149:
                    yyn = yys5();
                    continue;

                case 6:
                    yyst[yysp] = 6;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 150:
                    yyn = yys6();
                    continue;

                case 7:
                    yyst[yysp] = 7;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 151:
                    yyn = yys7();
                    continue;

                case 8:
                    yyst[yysp] = 8;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 152:
                    yyn = yys8();
                    continue;

                case 9:
                    yyst[yysp] = 9;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 153:
                    yyn = yys9();
                    continue;

                case 10:
                    yyst[yysp] = 10;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 154:
                    yyn = yys10();
                    continue;

                case 11:
                    yyst[yysp] = 11;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 155:
                    yyn = yys11();
                    continue;

                case 12:
                    yyst[yysp] = 12;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 156:
                    yyn = yys12();
                    continue;

                case 13:
                    yyst[yysp] = 13;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 157:
                    switch (yytok) {
                        case ENDINPUT:
                            yyn = yyr1();
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 14:
                    yyst[yysp] = 14;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 158:
                    yyn = yys14();
                    continue;

                case 15:
                    yyst[yysp] = 15;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 159:
                    yyn = yys15();
                    continue;

                case 16:
                    yyst[yysp] = 16;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 160:
                    yyn = yys16();
                    continue;

                case 17:
                    yyst[yysp] = 17;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 161:
                    switch (yytok) {
                        case L_KAKKO:
                            yyn = 49;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 18:
                    yyst[yysp] = 18;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 162:
                    switch (yytok) {
                        case L_KAKKO:
                            yyn = 50;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 19:
                    yyst[yysp] = 19;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 163:
                    switch (yytok) {
                        case L_KAKKO:
                            yyn = 51;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 20:
                    yyst[yysp] = 20;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 164:
                    switch (yytok) {
                        case L_KAKKO:
                            yyn = 52;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 21:
                    yyst[yysp] = 21;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 165:
                    switch (yytok) {
                        case L_KAKKO:
                            yyn = 53;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 22:
                    yyst[yysp] = 22;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 166:
                    yyn = yys22();
                    continue;

                case 23:
                    yyst[yysp] = 23;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 167:
                    switch (yytok) {
                        case LPATH:
                            yyn = 55;
                            continue;
                        case MARKDEF:
                            yyn = 56;
                            continue;
                        case MARKREF:
                            yyn = 57;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 24:
                    yyst[yysp] = 24;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 168:
                    switch (yytok) {
                        case L_KAKKO:
                            yyn = 58;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 25:
                    yyst[yysp] = 25;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 169:
                    switch (yytok) {
                        case L_KAKKO:
                            yyn = 59;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 26:
                    yyst[yysp] = 26;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 170:
                    yyn = yys26();
                    continue;

                case 27:
                    yyst[yysp] = 27;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 171:
                    yyn = yys27();
                    continue;

                case 28:
                    yyst[yysp] = 28;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 172:
                    yyn = yys28();
                    continue;

                case 29:
                    yyst[yysp] = 29;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 173:
                    yyn = yys29();
                    continue;

                case 30:
                    yyst[yysp] = 30;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 174:
                    switch (yytok) {
                        case L_KAKKO:
                            yyn = 63;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 31:
                    yyst[yysp] = 31;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 175:
                    switch (yytok) {
                        case L_KAKKO:
                            yyn = 64;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 32:
                    yyst[yysp] = 32;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 176:
                    switch (yytok) {
                        case L_KAKKO:
                            yyn = 65;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 33:
                    yyst[yysp] = 33;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 177:
                    switch (yytok) {
                        case L_KAKKO:
                            yyn = 66;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 34:
                    yyst[yysp] = 34;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 178:
                    yyn = yys34();
                    continue;

                case 35:
                    yyst[yysp] = 35;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 179:
                    yyn = yys35();
                    continue;

                case 36:
                    yyst[yysp] = 36;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 180:
                    yyn = yys36();
                    continue;

                case 37:
                    yyst[yysp] = 37;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 181:
                    yyn = yys37();
                    continue;

                case 38:
                    yyst[yysp] = 38;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 182:
                    yyn = yys38();
                    continue;

                case 39:
                    yyst[yysp] = 39;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 183:
                    yyn = yys39();
                    continue;

                case 40:
                    yyst[yysp] = 40;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 184:
                    yyn = yys40();
                    continue;

                case 41:
                    yyst[yysp] = 41;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 185:
                    yyn = yys41();
                    continue;

                case 42:
                    yyst[yysp] = 42;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 186:
                    yyn = yys42();
                    continue;

                case 43:
                    yyst[yysp] = 43;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 187:
                    yyn = yys43();
                    continue;

                case 44:
                    yyst[yysp] = 44;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 188:
                    yyn = yys44();
                    continue;

                case 45:
                    yyst[yysp] = 45;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 189:
                    yyn = yys45();
                    continue;

                case 46:
                    yyst[yysp] = 46;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 190:
                    yyn = yys46();
                    continue;

                case 47:
                    yyst[yysp] = 47;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 191:
                    switch (yytok) {
                        case L_KAGI:
                            yyn = 48;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 48:
                    yyst[yysp] = 48;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 192:
                    yyn = yys48();
                    continue;

                case 49:
                    yyst[yysp] = 49;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 193:
                    switch (yytok) {
                        case R_KAKKO:
                            yyn = 87;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 50:
                    yyst[yysp] = 50;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 194:
                    yyn = yys50();
                    continue;

                case 51:
                    yyst[yysp] = 51;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 195:
                    yyn = yys51();
                    continue;

                case 52:
                    yyst[yysp] = 52;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 196:
                    yyn = yys52();
                    continue;

                case 53:
                    yyst[yysp] = 53;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 197:
                    yyn = yys53();
                    continue;

                case 54:
                    yyst[yysp] = 54;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 198:
                    switch (yytok) {
                        case R_KAKKO:
                            yyn = 93;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 55:
                    yyst[yysp] = 55;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 199:
                    switch (yytok) {
                        case R_NAMI:
                            yyn = 94;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 56:
                    yyst[yysp] = 56;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 200:
                    switch (yytok) {
                        case LPATH:
                            yyn = 95;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 57:
                    yyst[yysp] = 57;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 201:
                    switch (yytok) {
                        case R_NAMI:
                            yyn = 96;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 58:
                    yyst[yysp] = 58;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 202:
                    yyn = yys58();
                    continue;

                case 59:
                    yyst[yysp] = 59;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 203:
                    yyn = yys59();
                    continue;

                case 60:
                    yyst[yysp] = 60;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 204:
                    yyn = yys60();
                    continue;

                case 61:
                    yyst[yysp] = 61;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 205:
                    yyn = yys61();
                    continue;

                case 62:
                    yyst[yysp] = 62;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 206:
                    yyn = yys62();
                    continue;

                case 63:
                    yyst[yysp] = 63;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 207:
                    yyn = yys63();
                    continue;

                case 64:
                    yyst[yysp] = 64;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 208:
                    yyn = yys64();
                    continue;

                case 65:
                    yyst[yysp] = 65;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 209:
                    yyn = yys65();
                    continue;

                case 66:
                    yyst[yysp] = 66;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 210:
                    switch (yytok) {
                        case R_KAKKO:
                            yyn = 102;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 67:
                    yyst[yysp] = 67;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 211:
                    yyn = yys67();
                    continue;

                case 68:
                    yyst[yysp] = 68;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 212:
                    yyn = yys68();
                    continue;

                case 69:
                    yyst[yysp] = 69;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 213:
                    yyn = yys69();
                    continue;

                case 70:
                    yyst[yysp] = 70;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 214:
                    yyn = yys70();
                    continue;

                case 71:
                    yyst[yysp] = 71;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 215:
                    yyn = yys71();
                    continue;

                case 72:
                    yyst[yysp] = 72;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 216:
                    yyn = yys72();
                    continue;

                case 73:
                    yyst[yysp] = 73;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 217:
                    yyn = yys73();
                    continue;

                case 74:
                    yyst[yysp] = 74;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 218:
                    yyn = yys74();
                    continue;

                case 75:
                    yyst[yysp] = 75;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 219:
                    yyn = yys75();
                    continue;

                case 76:
                    yyst[yysp] = 76;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 220:
                    yyn = yys76();
                    continue;

                case 77:
                    yyst[yysp] = 77;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 221:
                    yyn = yys77();
                    continue;

                case 78:
                    yyst[yysp] = 78;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 222:
                    yyn = yys78();
                    continue;

                case 79:
                    yyst[yysp] = 79;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 223:
                    yyn = yys79();
                    continue;

                case 80:
                    yyst[yysp] = 80;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 224:
                    yyn = yys80();
                    continue;

                case 81:
                    yyst[yysp] = 81;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 225:
                    switch (yytok) {
                        case COLON:
                            yyn = 103;
                            continue;
                        case R_KAGI:
                            yyn = 104;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 82:
                    yyst[yysp] = 82;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 226:
                    switch (yytok) {
                        case COLON:
                        case R_KAGI:
                            yyn = yyr44();
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 83:
                    yyst[yysp] = 83;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 227:
                    switch (yytok) {
                        case COLON:
                        case R_KAGI:
                            yyn = yyr48();
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 84:
                    yyst[yysp] = 84;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 228:
                    yyn = yys84();
                    continue;

                case 85:
                    yyst[yysp] = 85;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 229:
                    switch (yytok) {
                        case MINUS:
                            yyn = 107;
                            continue;
                        case PLUS:
                            yyn = 108;
                            continue;
                        case COLON:
                        case R_KAGI:
                            yyn = yyr45();
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 86:
                    yyst[yysp] = 86;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 230:
                    switch (yytok) {
                        case COLON:
                        case R_KAGI:
                            yyn = yyr33();
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 87:
                    yyst[yysp] = 87;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 231:
                    yyn = yys87();
                    continue;

                case 88:
                    yyst[yysp] = 88;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 232:
                    switch (yytok) {
                        case COMMA:
                            yyn = 109;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 89:
                    yyst[yysp] = 89;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 233:
                    switch (yytok) {
                        case R_KAKKO:
                        case COMMA:
                            yyn = yyr63();
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 90:
                    yyst[yysp] = 90;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 234:
                    switch (yytok) {
                        case COMMA:
                            yyn = 110;
                            continue;
                        case R_KAKKO:
                            yyn = 111;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 91:
                    yyst[yysp] = 91;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 235:
                    switch (yytok) {
                        case R_KAKKO:
                            yyn = 112;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 92:
                    yyst[yysp] = 92;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 236:
                    switch (yytok) {
                        case COMMA:
                            yyn = 113;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 93:
                    yyst[yysp] = 93;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 237:
                    yyn = yys93();
                    continue;

                case 94:
                    yyst[yysp] = 94;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 238:
                    yyn = yys94();
                    continue;

                case 95:
                    yyst[yysp] = 95;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 239:
                    switch (yytok) {
                        case R_NAMI:
                            yyn = 114;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 96:
                    yyst[yysp] = 96;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 240:
                    yyn = yys96();
                    continue;

                case 97:
                    yyst[yysp] = 97;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 241:
                    switch (yytok) {
                        case COMMA:
                            yyn = 110;
                            continue;
                        case R_KAKKO:
                            yyn = 115;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 98:
                    yyst[yysp] = 98;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 242:
                    switch (yytok) {
                        case COMMA:
                            yyn = 110;
                            continue;
                        case R_KAKKO:
                            yyn = 116;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 99:
                    yyst[yysp] = 99;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 243:
                    switch (yytok) {
                        case COMMA:
                            yyn = 117;
                            continue;
                        case R_KAKKO:
                            yyn = 118;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 100:
                    yyst[yysp] = 100;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 244:
                    switch (yytok) {
                        case COMMA:
                            yyn = 119;
                            continue;
                        case R_KAKKO:
                            yyn = 120;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 101:
                    yyst[yysp] = 101;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 245:
                    switch (yytok) {
                        case COMMA:
                            yyn = 110;
                            continue;
                        case R_KAKKO:
                            yyn = 121;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 102:
                    yyst[yysp] = 102;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 246:
                    yyn = yys102();
                    continue;

                case 103:
                    yyst[yysp] = 103;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 247:
                    yyn = yys103();
                    continue;

                case 104:
                    yyst[yysp] = 104;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 248:
                    yyn = yys104();
                    continue;

                case 105:
                    yyst[yysp] = 105;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 249:
                    switch (yytok) {
                        case R_KAGI:
                            yyn = 124;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 106:
                    yyst[yysp] = 106;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 250:
                    yyn = yys106();
                    continue;

                case 107:
                    yyst[yysp] = 107;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 251:
                    yyn = yys107();
                    continue;

                case 108:
                    yyst[yysp] = 108;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 252:
                    yyn = yys108();
                    continue;

                case 109:
                    yyst[yysp] = 109;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 253:
                    yyn = yys109();
                    continue;

                case 110:
                    yyst[yysp] = 110;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 254:
                    yyn = yys110();
                    continue;

                case 111:
                    yyst[yysp] = 111;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 255:
                    yyn = yys111();
                    continue;

                case 112:
                    yyst[yysp] = 112;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 256:
                    yyn = yys112();
                    continue;

                case 113:
                    yyst[yysp] = 113;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 257:
                    yyn = yys113();
                    continue;

                case 114:
                    yyst[yysp] = 114;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 258:
                    yyn = yys114();
                    continue;

                case 115:
                    yyst[yysp] = 115;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 259:
                    yyn = yys115();
                    continue;

                case 116:
                    yyst[yysp] = 116;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 260:
                    yyn = yys116();
                    continue;

                case 117:
                    yyst[yysp] = 117;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 261:
                    yyn = yys117();
                    continue;

                case 118:
                    yyst[yysp] = 118;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 262:
                    yyn = yys118();
                    continue;

                case 119:
                    yyst[yysp] = 119;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 263:
                    yyn = yys119();
                    continue;

                case 120:
                    yyst[yysp] = 120;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 264:
                    yyn = yys120();
                    continue;

                case 121:
                    yyst[yysp] = 121;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 265:
                    yyn = yys121();
                    continue;

                case 122:
                    yyst[yysp] = 122;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 266:
                    switch (yytok) {
                        case R_KAGI:
                            yyn = 132;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 123:
                    yyst[yysp] = 123;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 267:
                    yyn = yys123();
                    continue;

                case 124:
                    yyst[yysp] = 124;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 268:
                    yyn = yys124();
                    continue;

                case 125:
                    yyst[yysp] = 125;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 269:
                    switch (yytok) {
                        case COLON:
                        case R_KAGI:
                            yyn = yyr47();
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 126:
                    yyst[yysp] = 126;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 270:
                    switch (yytok) {
                        case COLON:
                        case R_KAGI:
                            yyn = yyr46();
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 127:
                    yyst[yysp] = 127;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 271:
                    switch (yytok) {
                        case COMMA:
                            yyn = 133;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 128:
                    yyst[yysp] = 128;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 272:
                    switch (yytok) {
                        case R_KAKKO:
                        case COMMA:
                            yyn = yyr64();
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 129:
                    yyst[yysp] = 129;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 273:
                    switch (yytok) {
                        case COMMA:
                            yyn = 134;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 130:
                    yyst[yysp] = 130;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 274:
                    switch (yytok) {
                        case R_KAKKO:
                            yyn = 135;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 131:
                    yyst[yysp] = 131;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 275:
                    switch (yytok) {
                        case R_KAKKO:
                            yyn = 136;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 132:
                    yyst[yysp] = 132;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 276:
                    yyn = yys132();
                    continue;

                case 133:
                    yyst[yysp] = 133;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 277:
                    yyn = yys133();
                    continue;

                case 134:
                    yyst[yysp] = 134;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 278:
                    yyn = yys134();
                    continue;

                case 135:
                    yyst[yysp] = 135;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 279:
                    yyn = yys135();
                    continue;

                case 136:
                    yyst[yysp] = 136;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 280:
                    yyn = yys136();
                    continue;

                case 137:
                    yyst[yysp] = 137;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 281:
                    switch (yytok) {
                        case R_KAKKO:
                            yyn = 139;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 138:
                    yyst[yysp] = 138;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 282:
                    switch (yytok) {
                        case COMMA:
                            yyn = 140;
                            continue;
                        case R_KAKKO:
                            yyn = 141;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 139:
                    yyst[yysp] = 139;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 283:
                    yyn = yys139();
                    continue;

                case 140:
                    yyst[yysp] = 140;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 284:
                    yyn = yys140();
                    continue;

                case 141:
                    yyst[yysp] = 141;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 285:
                    yyn = yys141();
                    continue;

                case 142:
                    yyst[yysp] = 142;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 286:
                    switch (yytok) {
                        case R_KAKKO:
                            yyn = 143;
                            continue;
                    }
                    yyn = 291;
                    continue;

                case 143:
                    yyst[yysp] = 143;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 287:
                    yyn = yys143();
                    continue;

                case 288:
                    return true;
                case 289:
                    yyerror("stack overflow");
                case 290:
                    return false;
                case 291:
                    yyerror("syntax error");
                    return false;
            }
        }
    }

    protected void yyexpand() {
        int[] newyyst = new int[2*yyst.length];
        Expr[] newyysv = new Expr[2*yyst.length];
        for (int i=0; i<yyst.length; i++) {
            newyyst[i] = yyst[i];
            newyysv[i] = yysv[i];
        }
        yyst = newyyst;
        yysv = newyysv;
    }

    private int yys0() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NOT:
                return 27;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys2() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case PLUS:
            case COMMA:
            case AND:
                return yyr31();
        }
        return 291;
    }

    private int yys3() {
        switch (yytok) {
            case OR:
            case ENDINPUT:
            case COLON:
            case R_KAKKO:
            case R_KAGI:
            case COMMA:
            case AND:
                return yyr7();
        }
        return 291;
    }

    private int yys4() {
        switch (yytok) {
            case OR:
            case ENDINPUT:
            case COLON:
            case R_KAKKO:
            case R_KAGI:
            case COMMA:
            case AND:
                return yyr5();
        }
        return 291;
    }

    private int yys5() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case PLUS:
            case COMMA:
            case AND:
                return yyr24();
        }
        return 291;
    }

    private int yys6() {
        switch (yytok) {
            case AND:
                return 34;
            case OR:
            case ENDINPUT:
            case COLON:
            case R_KAKKO:
            case R_KAGI:
            case COMMA:
                return yyr3();
        }
        return 291;
    }

    private int yys7() {
        switch (yytok) {
            case RUIJOU:
                return 35;
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case PLUS:
            case COMMA:
            case AND:
                return yyr19();
        }
        return 291;
    }

    private int yys8() {
        switch (yytok) {
            case DAINARI:
                return 36;
            case DAINARI_EQUAL:
                return 37;
            case EQUAL:
                return 38;
            case MINUS:
                return 39;
            case NOT_EQUAL:
                return 40;
            case PLUS:
                return 41;
            case SHOUNARI:
                return 42;
            case SHOUNARI_EQUAL:
                return 43;
            case OR:
            case ENDINPUT:
            case COLON:
            case R_KAKKO:
            case R_KAGI:
            case COMMA:
            case AND:
                return yyr9();
        }
        return 291;
    }

    private int yys9() {
        switch (yytok) {
            case DIV:
                return 44;
            case MULT:
                return 45;
            case OR:
            case NOT_EQUAL:
            case ENDINPUT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case PLUS:
            case COMMA:
            case AND:
                return yyr16();
        }
        return 291;
    }

    private int yys10() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case PLUS:
            case COMMA:
            case AND:
                return yyr22();
        }
        return 291;
    }

    private int yys11() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case PLUS:
            case COMMA:
            case AND:
                return yyr27();
        }
        return 291;
    }

    private int yys12() {
        switch (yytok) {
            case OR:
                return 46;
            case ENDINPUT:
            case COLON:
            case R_KAKKO:
            case R_KAGI:
            case COMMA:
                return yyr2();
        }
        return 291;
    }

    private int yys14() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case PLUS:
            case COMMA:
            case AND:
                return yyr30();
        }
        return 291;
    }

    private int yys15() {
        switch (yytok) {
            case L_KAGI:
                return 48;
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case PLUS:
            case COMMA:
            case AND:
                return yyr34();
        }
        return 291;
    }

    private int yys16() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case PLUS:
            case COMMA:
            case AND:
                return yyr29();
        }
        return 291;
    }

    private int yys22() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NOT:
                return 27;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys26() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case NUMBER:
                return 28;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys27() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys28() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case PLUS:
            case COMMA:
            case AND:
                return yyr32();
        }
        return 291;
    }

    private int yys29() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case NUMBER:
                return 28;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys34() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NOT:
                return 27;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys35() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys36() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys37() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys38() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys39() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys40() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys41() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys42() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys43() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys44() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys45() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys46() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NOT:
                return 27;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys48() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NOT:
                return 27;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
            case COLON:
                return 84;
            case SHARP:
                return 85;
            case STRING:
                return 86;
        }
        return 291;
    }

    private int yys50() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NOT:
                return 27;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys51() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NOT:
                return 27;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys52() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NOT:
                return 27;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys53() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NOT:
                return 27;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys58() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NOT:
                return 27;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys59() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NOT:
                return 27;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys60() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case PLUS:
            case COMMA:
            case AND:
                return yyr26();
        }
        return 291;
    }

    private int yys61() {
        switch (yytok) {
            case OR:
            case ENDINPUT:
            case COLON:
            case R_KAKKO:
            case R_KAGI:
            case COMMA:
            case AND:
                return yyr8();
        }
        return 291;
    }

    private int yys62() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case PLUS:
            case COMMA:
            case AND:
                return yyr25();
        }
        return 291;
    }

    private int yys63() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NOT:
                return 27;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys64() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NOT:
                return 27;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys65() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NOT:
                return 27;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys67() {
        switch (yytok) {
            case OR:
            case ENDINPUT:
            case COLON:
            case R_KAKKO:
            case R_KAGI:
            case COMMA:
            case AND:
                return yyr6();
        }
        return 291;
    }

    private int yys68() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case PLUS:
            case COMMA:
            case AND:
                return yyr23();
        }
        return 291;
    }

    private int yys69() {
        switch (yytok) {
            case OR:
            case ENDINPUT:
            case COLON:
            case R_KAKKO:
            case R_KAGI:
            case COMMA:
            case AND:
                return yyr12();
        }
        return 291;
    }

    private int yys70() {
        switch (yytok) {
            case OR:
            case ENDINPUT:
            case COLON:
            case R_KAKKO:
            case R_KAGI:
            case COMMA:
            case AND:
                return yyr13();
        }
        return 291;
    }

    private int yys71() {
        switch (yytok) {
            case OR:
            case ENDINPUT:
            case COLON:
            case R_KAKKO:
            case R_KAGI:
            case COMMA:
            case AND:
                return yyr10();
        }
        return 291;
    }

    private int yys72() {
        switch (yytok) {
            case DIV:
                return 44;
            case MULT:
                return 45;
            case OR:
            case NOT_EQUAL:
            case ENDINPUT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case PLUS:
            case COMMA:
            case AND:
                return yyr18();
        }
        return 291;
    }

    private int yys73() {
        switch (yytok) {
            case OR:
            case ENDINPUT:
            case COLON:
            case R_KAKKO:
            case R_KAGI:
            case COMMA:
            case AND:
                return yyr11();
        }
        return 291;
    }

    private int yys74() {
        switch (yytok) {
            case DIV:
                return 44;
            case MULT:
                return 45;
            case OR:
            case NOT_EQUAL:
            case ENDINPUT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case PLUS:
            case COMMA:
            case AND:
                return yyr17();
        }
        return 291;
    }

    private int yys75() {
        switch (yytok) {
            case OR:
            case ENDINPUT:
            case COLON:
            case R_KAKKO:
            case R_KAGI:
            case COMMA:
            case AND:
                return yyr14();
        }
        return 291;
    }

    private int yys76() {
        switch (yytok) {
            case OR:
            case ENDINPUT:
            case COLON:
            case R_KAKKO:
            case R_KAGI:
            case COMMA:
            case AND:
                return yyr15();
        }
        return 291;
    }

    private int yys77() {
        switch (yytok) {
            case RUIJOU:
                return 35;
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case PLUS:
            case COMMA:
            case AND:
                return yyr21();
        }
        return 291;
    }

    private int yys78() {
        switch (yytok) {
            case RUIJOU:
                return 35;
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case PLUS:
            case COMMA:
            case AND:
                return yyr20();
        }
        return 291;
    }

    private int yys79() {
        switch (yytok) {
            case AND:
                return 34;
            case OR:
            case ENDINPUT:
            case COLON:
            case R_KAKKO:
            case R_KAGI:
            case COMMA:
                return yyr4();
        }
        return 291;
    }

    private int yys80() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case PLUS:
            case COMMA:
            case AND:
                return yyr35();
        }
        return 291;
    }

    private int yys84() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NOT:
                return 27;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
            case SHARP:
                return 85;
            case STRING:
                return 86;
            case R_KAGI:
                return 106;
        }
        return 291;
    }

    private int yys87() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case PLUS:
            case COMMA:
            case AND:
                return yyr53();
        }
        return 291;
    }

    private int yys93() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case PLUS:
            case COMMA:
            case AND:
                return yyr28();
        }
        return 291;
    }

    private int yys94() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case L_KAGI:
            case PLUS:
            case COMMA:
            case AND:
                return yyr36();
        }
        return 291;
    }

    private int yys96() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case L_KAGI:
            case PLUS:
            case COMMA:
            case AND:
                return yyr38();
        }
        return 291;
    }

    private int yys102() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case PLUS:
            case COMMA:
            case AND:
                return yyr52();
        }
        return 291;
    }

    private int yys103() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NOT:
                return 27;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
            case SHARP:
                return 85;
            case STRING:
                return 86;
            case R_KAGI:
                return 123;
        }
        return 291;
    }

    private int yys104() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case L_KAGI:
            case PLUS:
            case COMMA:
            case AND:
                return yyr39();
        }
        return 291;
    }

    private int yys106() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case L_KAGI:
            case PLUS:
            case COMMA:
            case AND:
                return yyr40();
        }
        return 291;
    }

    private int yys107() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NOT:
                return 27;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys108() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NOT:
                return 27;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys109() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NOT:
                return 27;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys110() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NOT:
                return 27;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys111() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case PLUS:
            case COMMA:
            case AND:
                return yyr55();
        }
        return 291;
    }

    private int yys112() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case PLUS:
            case COMMA:
            case AND:
                return yyr56();
        }
        return 291;
    }

    private int yys113() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NOT:
                return 27;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys114() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case L_KAGI:
            case PLUS:
            case COMMA:
            case AND:
                return yyr37();
        }
        return 291;
    }

    private int yys115() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case PLUS:
            case COMMA:
            case AND:
                return yyr51();
        }
        return 291;
    }

    private int yys116() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case PLUS:
            case COMMA:
            case AND:
                return yyr50();
        }
        return 291;
    }

    private int yys117() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NOT:
                return 27;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys118() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case PLUS:
            case COMMA:
            case AND:
                return yyr57();
        }
        return 291;
    }

    private int yys119() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NOT:
                return 27;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys120() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case PLUS:
            case COMMA:
            case AND:
                return yyr59();
        }
        return 291;
    }

    private int yys121() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case PLUS:
            case COMMA:
            case AND:
                return yyr49();
        }
        return 291;
    }

    private int yys123() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case L_KAGI:
            case PLUS:
            case COMMA:
            case AND:
                return yyr41();
        }
        return 291;
    }

    private int yys124() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case L_KAGI:
            case PLUS:
            case COMMA:
            case AND:
                return yyr42();
        }
        return 291;
    }

    private int yys132() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case L_KAGI:
            case PLUS:
            case COMMA:
            case AND:
                return yyr43();
        }
        return 291;
    }

    private int yys133() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NOT:
                return 27;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys134() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NOT:
                return 27;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys135() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case PLUS:
            case COMMA:
            case AND:
                return yyr58();
        }
        return 291;
    }

    private int yys136() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case PLUS:
            case COMMA:
            case AND:
                return yyr60();
        }
        return 291;
    }

    private int yys139() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case PLUS:
            case COMMA:
            case AND:
                return yyr54();
        }
        return 291;
    }

    private int yys140() {
        switch (yytok) {
            case CHILD_AT:
                return 16;
            case FALSE:
                return 17;
            case IF:
                return 18;
            case IFS:
                return 19;
            case INT:
                return 20;
            case LOOKUP:
                return 21;
            case L_KAKKO:
                return 22;
            case L_NAMI:
                return 23;
            case MAX:
                return 24;
            case MIN:
                return 25;
            case MINUS:
                return 26;
            case NOT:
                return 27;
            case NUMBER:
                return 28;
            case PLUS:
                return 29;
            case ROUNDDOWN:
                return 30;
            case ROUNDUP:
                return 31;
            case SUM:
                return 32;
            case TRUE:
                return 33;
        }
        return 291;
    }

    private int yys141() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case PLUS:
            case COMMA:
            case AND:
                return yyr62();
        }
        return 291;
    }

    private int yys143() {
        switch (yytok) {
            case OR:
            case NOT_EQUAL:
            case DIV:
            case ENDINPUT:
            case MULT:
            case MINUS:
            case DAINARI_EQUAL:
            case COLON:
            case SHOUNARI_EQUAL:
            case SHOUNARI:
            case EQUAL:
            case DAINARI:
            case R_KAKKO:
            case R_KAGI:
            case RUIJOU:
            case PLUS:
            case COMMA:
            case AND:
                return yyr61();
        }
        return 291;
    }

    private int yyr1() { // input : shiki
        {
                yyrv = yysv[yysp-1]; }
        yysv[yysp-=1] = yyrv;
        return 1;
    }

    private int yyr49() { // function : SUM L_KAKKO shiki_list R_KAKKO
        { yyrv = new FuncExpr(yysv[yysp-4], yysv[yysp-2]); }
        yysv[yysp-=4] = yyrv;
        return 2;
    }

    private int yyr50() { // function : MIN L_KAKKO shiki_list R_KAKKO
        { yyrv = new FuncExpr(yysv[yysp-4], yysv[yysp-2]); }
        yysv[yysp-=4] = yyrv;
        return 2;
    }

    private int yyr51() { // function : MAX L_KAKKO shiki_list R_KAKKO
        { yyrv = new FuncExpr(yysv[yysp-4], yysv[yysp-2]); }
        yysv[yysp-=4] = yyrv;
        return 2;
    }

    private int yyr52() { // function : TRUE L_KAKKO R_KAKKO
        {
                ListExpr listExpr = new ListExpr();
                yyrv = new FuncExpr(yysv[yysp-3], listExpr); }
        yysv[yysp-=3] = yyrv;
        return 2;
    }

    private int yyr53() { // function : FALSE L_KAKKO R_KAKKO
        {
                ListExpr listExpr = new ListExpr();
                yyrv = new FuncExpr(yysv[yysp-3], listExpr); }
        yysv[yysp-=3] = yyrv;
        return 2;
    }

    private int yyr54() { // function : IF L_KAKKO shiki COMMA shiki COMMA shiki R_KAKKO
        {
                        ListExpr listExpr = new ListExpr(yysv[yysp-6]);
                        listExpr.add(yysv[yysp-4]);
                        listExpr.add(yysv[yysp-2]);
                        yyrv = new FuncExpr(yysv[yysp-8], listExpr);
                }
        yysv[yysp-=8] = yyrv;
        return 2;
    }

    private int yyr55() { // function : IFS L_KAKKO shiki_list R_KAKKO
        {
                yyrv = new FuncExpr(yysv[yysp-4], yysv[yysp-2]); }
        yysv[yysp-=4] = yyrv;
        return 2;
    }

    private int yyr56() { // function : INT L_KAKKO shiki R_KAKKO
        {
                ListExpr listExpr = new ListExpr(yysv[yysp-2]);
                yyrv = new FuncExpr(yysv[yysp-4], listExpr); }
        yysv[yysp-=4] = yyrv;
        return 2;
    }

    private int yyr57() { // function : ROUNDDOWN L_KAKKO shiki R_KAKKO
        {
                ListExpr listExpr = new ListExpr(yysv[yysp-2]);
                yyrv = new FuncExpr(yysv[yysp-4], listExpr); }
        yysv[yysp-=4] = yyrv;
        return 2;
    }

    private int yyr58() { // function : ROUNDDOWN L_KAKKO shiki COMMA shiki R_KAKKO
        {
                ListExpr listExpr = new ListExpr(yysv[yysp-4]);
                listExpr.add(yysv[yysp-2]);
                yyrv = new FuncExpr(yysv[yysp-6], listExpr); }
        yysv[yysp-=6] = yyrv;
        return 2;
    }

    private int yyr59() { // function : ROUNDUP L_KAKKO shiki R_KAKKO
        {
                ListExpr listExpr = new ListExpr(yysv[yysp-2]);
                yyrv = new FuncExpr(yysv[yysp-4], listExpr); }
        yysv[yysp-=4] = yyrv;
        return 2;
    }

    private int yyr60() { // function : ROUNDUP L_KAKKO shiki COMMA shiki R_KAKKO
        {
                ListExpr listExpr = new ListExpr(yysv[yysp-4]);
                listExpr.add(yysv[yysp-2]);
                yyrv = new FuncExpr(yysv[yysp-6], listExpr); }
        yysv[yysp-=6] = yyrv;
        return 2;
    }

    private int yyr61() { // function : LOOKUP L_KAKKO shiki COMMA shiki COMMA shiki COMMA shiki R_KAKKO
        {
                ListExpr listExpr = new ListExpr(yysv[yysp-8]);
                listExpr.add(yysv[yysp-6]);
                listExpr.add(yysv[yysp-4]);
                listExpr.add(yysv[yysp-2]);
                yyrv = new FuncExpr(yysv[yysp-10], listExpr); }
        yysv[yysp-=10] = yyrv;
        return 2;
    }

    private int yyr62() { // function : LOOKUP L_KAKKO shiki COMMA shiki COMMA shiki R_KAKKO
        {
                ListExpr listExpr = new ListExpr(yysv[yysp-6]);
                listExpr.add(yysv[yysp-4]);
                listExpr.add(yysv[yysp-2]);
                listExpr.add(null);
                yyrv = new FuncExpr(yysv[yysp-8], listExpr); }
        yysv[yysp-=8] = yyrv;
        return 2;
    }

    private int yyr9() { // hikaku_shiki : kagen_shiki
        { yyrv = yysv[yysp-1]; }
        yysv[yysp-=1] = yyrv;
        return yyphikaku_shiki();
    }

    private int yyr10() { // hikaku_shiki : kagen_shiki EQUAL hikaku_shiki
        {
                yyrv = new EqualExpr(yysv[yysp-3], yysv[yysp-1]); }
        yysv[yysp-=3] = yyrv;
        return yyphikaku_shiki();
    }

    private int yyr11() { // hikaku_shiki : kagen_shiki NOT_EQUAL hikaku_shiki
        {
                yyrv = new NotEqualExpr(yysv[yysp-3], yysv[yysp-1]); }
        yysv[yysp-=3] = yyrv;
        return yyphikaku_shiki();
    }

    private int yyr12() { // hikaku_shiki : kagen_shiki DAINARI hikaku_shiki
        {
                yyrv = new DainariExpr(yysv[yysp-3], yysv[yysp-1]); }
        yysv[yysp-=3] = yyrv;
        return yyphikaku_shiki();
    }

    private int yyr13() { // hikaku_shiki : kagen_shiki DAINARI_EQUAL hikaku_shiki
        {
                yyrv = new DainariEqualExpr(yysv[yysp-3], yysv[yysp-1]); }
        yysv[yysp-=3] = yyrv;
        return yyphikaku_shiki();
    }

    private int yyr14() { // hikaku_shiki : kagen_shiki SHOUNARI hikaku_shiki
        {
                yyrv = new ShounariExpr(yysv[yysp-3], yysv[yysp-1]); }
        yysv[yysp-=3] = yyrv;
        return yyphikaku_shiki();
    }

    private int yyr15() { // hikaku_shiki : kagen_shiki SHOUNARI_EQUAL hikaku_shiki
        {
                yyrv = new ShounariEqualExpr(yysv[yysp-3], yysv[yysp-1]); }
        yysv[yysp-=3] = yyrv;
        return yyphikaku_shiki();
    }

    private int yyphikaku_shiki() {
        switch (yyst[yysp-1]) {
            case 43: return 76;
            case 42: return 75;
            case 40: return 73;
            case 38: return 71;
            case 37: return 70;
            case 36: return 69;
            case 27: return 61;
            default: return 3;
        }
    }

    private int yyr7() { // hitei_shiki : hikaku_shiki
        { yyrv = yysv[yysp-1]; }
        yysv[yysp-=1] = yyrv;
        return yyphitei_shiki();
    }

    private int yyr8() { // hitei_shiki : NOT hikaku_shiki
        { yyrv = new NotExpr(yysv[yysp-1]); }
        yysv[yysp-=2] = yyrv;
        return yyphitei_shiki();
    }

    private int yyphitei_shiki() {
        switch (yyst[yysp-1]) {
            case 34: return 67;
            default: return 4;
        }
    }

    private int yyr27() { // ichijishi : number
        { yyrv = yysv[yysp-1]; }
        yysv[yysp-=1] = yyrv;
        return yypichijishi();
    }

    private int yyr28() { // ichijishi : L_KAKKO shiki R_KAKKO
        { yyrv = yysv[yysp-2]; }
        yysv[yysp-=3] = yyrv;
        return yypichijishi();
    }

    private int yyr29() { // ichijishi : CHILD_AT
        { yyrv = yysv[yysp-1]; }
        yysv[yysp-=1] = yyrv;
        return yypichijishi();
    }

    private int yyr30() { // ichijishi : tag
        { yyrv = yysv[yysp-1]; }
        yysv[yysp-=1] = yyrv;
        return yypichijishi();
    }

    private int yyr31() { // ichijishi : function
        { yyrv = yysv[yysp-1]; }
        yysv[yysp-=1] = yyrv;
        return yypichijishi();
    }

    private int yypichijishi() {
        switch (yyst[yysp-1]) {
            case 29: return 62;
            case 26: return 60;
            default: return 5;
        }
    }

    private int yyr44() { // index_shiki : shiki
        { yyrv = new IndexShikiExpr(yysv[yysp-1], false, false); }
        yysv[yysp-=1] = yyrv;
        return yypindex_shiki();
    }

    private int yyr45() { // index_shiki : SHARP
        { yyrv = new IndexShikiExpr(null, true, true); }
        yysv[yysp-=1] = yyrv;
        return yypindex_shiki();
    }

    private int yyr46() { // index_shiki : SHARP PLUS shiki
        {
                        yyrv = new IndexShikiExpr(yysv[yysp-1], true, true); }
        yysv[yysp-=3] = yyrv;
        return yypindex_shiki();
    }

    private int yyr47() { // index_shiki : SHARP MINUS shiki
        {
                        yyrv = new IndexShikiExpr(yysv[yysp-1], true, false); }
        yysv[yysp-=3] = yyrv;
        return yypindex_shiki();
    }

    private int yyr48() { // index_shiki : string
        { yyrv = new IndexShikiExpr(yysv[yysp-1], false, false); }
        yysv[yysp-=1] = yyrv;
        return yypindex_shiki();
    }

    private int yypindex_shiki() {
        switch (yyst[yysp-1]) {
            case 84: return 105;
            case 48: return 81;
            default: return 122;
        }
    }

    private int yyr5() { // and_shiki : hitei_shiki
        { yyrv = yysv[yysp-1]; }
        yysv[yysp-=1] = yyrv;
        return yypand_shiki();
    }

    private int yyr6() { // and_shiki : and_shiki AND hitei_shiki
        { yyrv = new AndExpr(yysv[yysp-3], yysv[yysp-1]); }
        yysv[yysp-=3] = yyrv;
        return yypand_shiki();
    }

    private int yypand_shiki() {
        switch (yyst[yysp-1]) {
            case 46: return 79;
            default: return 6;
        }
    }

    private int yyr22() { // inshi : nijishi
        { yyrv = yysv[yysp-1]; }
        yysv[yysp-=1] = yyrv;
        return yypinshi();
    }

    private int yyr23() { // inshi : inshi RUIJOU nijishi
        { yyrv = new RuijouExpr(yysv[yysp-3], yysv[yysp-1]); }
        yysv[yysp-=3] = yyrv;
        return yypinshi();
    }

    private int yypinshi() {
        switch (yyst[yysp-1]) {
            case 45: return 78;
            case 44: return 77;
            default: return 7;
        }
    }

    private int yyr16() { // kagen_shiki : kou
        { yyrv = yysv[yysp-1];}
        yysv[yysp-=1] = yyrv;
        return 8;
    }

    private int yyr17() { // kagen_shiki : kagen_shiki PLUS kou
        { yyrv = new AddExpr(yysv[yysp-3], yysv[yysp-1]); }
        yysv[yysp-=3] = yyrv;
        return 8;
    }

    private int yyr18() { // kagen_shiki : kagen_shiki MINUS kou
        { yyrv = new SubExpr(yysv[yysp-3], yysv[yysp-1]); }
        yysv[yysp-=3] = yyrv;
        return 8;
    }

    private int yyr19() { // kou : inshi
        { yyrv = yysv[yysp-1]; }
        yysv[yysp-=1] = yyrv;
        return yypkou();
    }

    private int yyr20() { // kou : kou MULT inshi
        { yyrv = new MultExpr(yysv[yysp-3], yysv[yysp-1]); }
        yysv[yysp-=3] = yyrv;
        return yypkou();
    }

    private int yyr21() { // kou : kou DIV inshi
        { yyrv = new DivExpr(yysv[yysp-3], yysv[yysp-1]); }
        yysv[yysp-=3] = yyrv;
        return yypkou();
    }

    private int yypkou() {
        switch (yyst[yysp-1]) {
            case 41: return 74;
            case 39: return 72;
            default: return 9;
        }
    }

    private int yyr24() { // nijishi : ichijishi
        { yyrv = yysv[yysp-1]; }
        yysv[yysp-=1] = yyrv;
        return yypnijishi();
    }

    private int yyr25() { // nijishi : PLUS ichijishi
        { yyrv = yysv[yysp-1]; }
        yysv[yysp-=2] = yyrv;
        return yypnijishi();
    }

    private int yyr26() { // nijishi : MINUS ichijishi
        { yyrv = new SubExpr(new NumberExpr("0"), yysv[yysp-1]); }
        yysv[yysp-=2] = yyrv;
        return yypnijishi();
    }

    private int yypnijishi() {
        switch (yyst[yysp-1]) {
            case 35: return 68;
            default: return 10;
        }
    }

    private int yyr32() { // number : NUMBER
        { yyrv = yysv[yysp-1]; }
        yysv[yysp-=1] = yyrv;
        return 11;
    }

    private int yyr3() { // or_shiki : and_shiki
        { yyrv = yysv[yysp-1]; }
        yysv[yysp-=1] = yyrv;
        return 12;
    }

    private int yyr4() { // or_shiki : or_shiki OR and_shiki
        { yyrv = new OrExpr(yysv[yysp-3], yysv[yysp-1]); }
        yysv[yysp-=3] = yyrv;
        return 12;
    }

    private int yyr2() { // shiki : or_shiki
        {
                yyrv = yysv[yysp-1]; }
        yysv[yysp-=1] = yyrv;
        switch (yyst[yysp-1]) {
            case 140: return 142;
            case 134: return 138;
            case 133: return 137;
            case 119: return 131;
            case 117: return 130;
            case 113: return 129;
            case 110: return 128;
            case 109: return 127;
            case 108: return 126;
            case 107: return 125;
            case 103: return 82;
            case 84: return 82;
            case 64: return 100;
            case 63: return 99;
            case 53: return 92;
            case 52: return 91;
            case 50: return 88;
            case 48: return 82;
            case 22: return 54;
            case 0: return 13;
            default: return 89;
        }
    }

    private int yyr63() { // shiki_list : shiki
        {
                        ListExpr listExpr = new ListExpr(yysv[yysp-1]);
                        yyrv = listExpr;
                }
        yysv[yysp-=1] = yyrv;
        return yypshiki_list();
    }

    private int yyr64() { // shiki_list : shiki_list COMMA shiki
        {
                        ((ListExpr)yysv[yysp-3]).add(yysv[yysp-1]);
                        yyrv = yysv[yysp-3];
                }
        yysv[yysp-=3] = yyrv;
        return yypshiki_list();
    }

    private int yypshiki_list() {
        switch (yyst[yysp-1]) {
            case 59: return 98;
            case 58: return 97;
            case 51: return 90;
            default: return 101;
        }
    }

    private int yyr33() { // string : STRING
        { yyrv = yysv[yysp-1]; }
        yysv[yysp-=1] = yyrv;
        return 83;
    }

    private int yyr34() { // tag : tag_main
        { yyrv = yysv[yysp-1];}
        yysv[yysp-=1] = yyrv;
        return 14;
    }

    private int yyr35() { // tag : tag_main tag_index tag_index
        {
                        ((TagExpr)yysv[yysp-3]).setIndex((IndexExpr)yysv[yysp-2], (IndexExpr)yysv[yysp-1]);
                        yyrv = yysv[yysp-3]; }
        yysv[yysp-=3] = yyrv;
        return 14;
    }

    private int yyr39() { // tag_index : L_KAGI index_shiki R_KAGI
        {
                        yyrv = new IndexExpr((IndexShikiExpr)yysv[yysp-2], false, null, null); }
        yysv[yysp-=3] = yyrv;
        return yyptag_index();
    }

    private int yyr40() { // tag_index : L_KAGI COLON R_KAGI
        {
                        yyrv = new IndexExpr(null, true, null, null); }
        yysv[yysp-=3] = yyrv;
        return yyptag_index();
    }

    private int yyr41() { // tag_index : L_KAGI index_shiki COLON R_KAGI
        {
                        yyrv = new IndexExpr(null, false, (IndexShikiExpr)yysv[yysp-3], null); }
        yysv[yysp-=4] = yyrv;
        return yyptag_index();
    }

    private int yyr42() { // tag_index : L_KAGI COLON index_shiki R_KAGI
        {
                        yyrv = new IndexExpr(null, false, null, (IndexShikiExpr)yysv[yysp-2]); }
        yysv[yysp-=4] = yyrv;
        return yyptag_index();
    }

    private int yyr43() { // tag_index : L_KAGI index_shiki COLON index_shiki R_KAGI
        {
                        yyrv = new IndexExpr(null, false, (IndexShikiExpr)yysv[yysp-4], (IndexShikiExpr)yysv[yysp-2]); }
        yysv[yysp-=5] = yyrv;
        return yyptag_index();
    }

    private int yyptag_index() {
        switch (yyst[yysp-1]) {
            case 15: return 47;
            default: return 80;
        }
    }

    private int yyr36() { // tag_main : L_NAMI LPATH R_NAMI
        { yyrv = new TagExpr(null, (StrExpr)yysv[yysp-2], null); }
        yysv[yysp-=3] = yyrv;
        return 15;
    }

    private int yyr37() { // tag_main : L_NAMI MARKDEF LPATH R_NAMI
        {
                        yyrv = new TagExpr((StrExpr)yysv[yysp-3], (StrExpr)yysv[yysp-2], null); }
        yysv[yysp-=4] = yyrv;
        return 15;
    }

    private int yyr38() { // tag_main : L_NAMI MARKREF R_NAMI
        {
                        yyrv = new TagExpr(null, null,(StrExpr)yysv[yysp-2]); }
        yysv[yysp-=3] = yyrv;
        return 15;
    }

    protected String[] yyerrmsgs = {
    };


  /* フィールドやメソッドの定義 */

  LeafParser(LeafLexer l) {
      lexer = l;
  }

  private LeafLexer lexer;

        private String strMessage;

  private void yyerror(String msg) {
                D.dprint((Expr)yyrv);
                if (lexer.yyatEOF() ) {
                        return;
                }

                // TODO msgの保存
                strMessage = msg;
                return;
//      System.out.println("エラー: " + msg);
  //    System.exit(1);
  }

public String getErrorMessage() {
        return strMessage;
}

public Expr BuildAs(){
return ( Expr )yyrv;
}
  public static void main(String[] args) {
//        InputStreamReader in = new InputStreamReader(System.in);
                StringReader in = new StringReader("MIN({../寄附金額},ROUNDUP({ancestor::寄附金税額控除//合計所得金額}*0.3))");
          LeafLexer lexer = new LeafLexer(in);
          D.dprint("a");
      LeafParser calc = new LeafParser(lexer);
          D.dprint("b");
      lexer.nextToken();
      D.dprint("c");
      boolean flag = calc.parse();    // parse the input
      D.dprint(flag);
      if (! flag) {
    	  String str = calc.getErrorMessage();
    	  D.dprint(str);
      }
      D.dprint("d");
          Expr expr = calc.BuildAs();
          D.dprint(expr);
  }


}
