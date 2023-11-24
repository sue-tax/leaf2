package application;

public class Message {
	public static String NOT_FOUND_NODE =
			"#NOT_FOUND_NODE#指定されたノードが見つかりません";
	public static String NOT_FOUND_NODE_ =
			"#NOT_FOUND_NODE#指定されたノード%sが見つかりません";
	public static String NOT_SINGLE =
			"#NOT_SINGLE#１つだけのデータが必要";
	public static String CIRCULATE_ERROR =
			"#CIRCULATE_ERROR#循環参照になっています";

	public static String MUST_LOGICAL =
			"#MUST_LOGICAL#論理値・数値が必要";

	public static String MUST_NUMERIC =
			"#MUST_NUMERIC#数値・論理値が必要";

	public static String UNDEFINE_MARK_ =
			"#UNDEFINE_MARK#マーク%sは未定義です";

	public static String NOT_TABLE =
			"#NOT_TABLE#表のノードは指定できません";
	public static String MUST_TABLE =
			"#MUST_TABLE#インデックス指定は表のノードだけです";
	public static String MUST_TABLE_ARG =
			"#MUST_TABLE_ARG#表のノードが必要";
	public static String NOT_STRING_HEADER =
			"#NOT_STRING_HEADER#表のヘッダーが文字列でない";
	public static String NOT_FOUND_HEADER =
			"#NOT_FOUND_HEADER#表のヘッダーが見つかりません";

	public static String NOT_FOUND_DATA =
			"#NOT_FOUND_DATA#該当するデータが見つかりません";

	public static String TOO_LITTLE =
			"#TOO_LITTLE#値が小さすぎます";
	public static String TOO_BIG =
			"#TOO_BIG#値が大きすぎます";

	public static String INDEX_MUST_INTEGER =
			"#INDEX_MUST_INTEGER#表のインデックスは正の整数が必要";

	public static String IF_COND_MUST_LOGICAL =
			"#IF_COND_MUST_LOGICAL#IFの条件式は論理値・数値が必要";
	public static String IFS_INVALID_ARGUMENT_SIZE =
			"#IFS_INVALID_ARGUMENT_SIZE#IFSの引数の数は3以上";
	public static String IFS_INVALID_LAST_COND =
			"#IFS_INVALID_LAST_COND#IFSの最後の条件が不適切";

	public static String INVALID_EXPRESSION_ =
			"#INVALID_EXPRESSION#%s";

	public static String UNSUPPORT =
			"#UNSUPPORT#サポート外";

	public static String SYSTEM_ERROR_ =
			"#SYSTEM_ERROR#システムエラー%s";
}
