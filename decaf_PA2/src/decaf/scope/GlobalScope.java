package decaf.scope;

import decaf.symbol.Class;
import decaf.symbol.Symbol;
import decaf.utils.IndentPrintWriter;

public class GlobalScope extends Scope {

	@Override
	public boolean isGlobalScope() {
		return true;
	}

	@Override
	public Kind getKind() {
		return Kind.GLOBAL;
	}

	@Override
	public void printTo(IndentPrintWriter pw) {
		pw.println("GLOBAL SCOPE:");
		pw.incIndent();
		for (Symbol symbol : symbols.values()) {//下面每一个symbol都需要print
			pw.println(symbol);
		}
		for (Symbol symbol : symbols.values()) {//每一个symbol都需要print
			((Class) symbol).getAssociatedScope().printTo(pw);//如果有内部的,那么就在每一个地点print出来
		}
		pw.decIndent();
	}

}
