package decaf.backend;

public final class OffsetCounter {
	public enum Kind {///几种符号表的分类
		LOCAL, PARAMETER, VARFIELD
	}

	public static final int POINTER_SIZE = 4;

	public static final int WORD_SIZE = 4;

	public static final int DOUBLE_SIZE = 8;

	private static final int[] initValue = new int[] { -2 * WORD_SIZE,
			WORD_SIZE, POINTER_SIZE };///一个size

	private static final int[] direction = new int[] { -1, 1, 1 };///区域方向

	public static final OffsetCounter LOCAL_OFFSET_COUNTER = new OffsetCounter(
			Kind.LOCAL);///local

	public static final OffsetCounter PARAMETER_OFFSET_COUNTER = new OffsetCounter(
			Kind.PARAMETER);

	public static final OffsetCounter VARFIELD_OFFSET_COUNTER = new OffsetCounter(
			Kind.VARFIELD);

	private Kind kind;

	private int value;

	private OffsetCounter(Kind kind) {
		this.kind = kind;
		reset();
	}

	public void reset() {
		value = initValue[kind.ordinal()];
	}

	public int next(int value) {
		int ret = this.value;
		this.value += direction[kind.ordinal()] * value;
		return ret;
	}

	public void set(int offset) {
		value = offset;
	}
}
