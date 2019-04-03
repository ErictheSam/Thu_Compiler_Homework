package decaf.scope;

import java.util.ListIterator;
import java.util.Stack;

import decaf.Location;
import decaf.scope.Scope.Kind;
import decaf.symbol.Class;
import decaf.symbol.Symbol;

public class ScopeStack {
	private Stack<Scope> scopeStack = new Stack<Scope>();
	
	private GlobalScope globalScope;

	public Symbol lookup(String name, boolean through) {
		if (through) {
			ListIterator<Scope> iter = scopeStack.listIterator(scopeStack
					.size());
			while (iter.hasPrevious()) {
				Symbol symbol = iter.previous().lookup(name);
				if (symbol != null) {
					return symbol;
				}
			}
			return null;
		} else {
			return scopeStack.peek().lookup(name);
		}
	}

	public Symbol lookupBeforeLocation(String name, Location loc) {
		ListIterator<Scope> iter = scopeStack.listIterator(scopeStack.size());
		while (iter.hasPrevious()) {
			Scope scope = iter.previous();
			Symbol symbol = scope.lookup(name);
			if (symbol != null) {
				if (scope.isLocalScope()
						&& symbol.getLocation().compareTo(loc) > 0) {
					continue;
				}
				return symbol;
			}
		}
		return null;
	}

	public void declare(Symbol symbol) {
		scopeStack.peek().declare(symbol);//如果有的话，那么就在最后面declare掉它每一个类都是需要的
	}

	public void open(Scope scope) {//进入一个新的scope
		switch (scope.getKind()) {
		case GLOBAL:
			globalScope = (GlobalScope)scope;
			break;
		case CLASS:
			ClassScope cs = ((ClassScope) scope).getParentScope();//父类中间的不同内容!
			if (cs != null) {
				open(cs);
			}
			break;
		}
		scopeStack.push(scope);
	}

	public void close() {
		Scope scope = scopeStack.pop();
		if (scope.isClassScope()) {//如果是class
			for (int n = scopeStack.size() - 1; n > 0; n--) {//如果是class的话,那么就都给pop出来
				scopeStack.pop();
			}
		}
	}

	public Scope lookForScope(Kind kind) {//对于现在的一群scope,其需要
		ListIterator<Scope> iter = scopeStack.listIterator(scopeStack.size());
		while (iter.hasPrevious()) {
			Scope scope = iter.previous();
			if (scope.getKind() == kind) {
				return scope;
			}
		}
		return null;
	}

	public Scope getCurrentScope() {
		return scopeStack.peek();
	}

	public Class lookupClass(String name) {
		return (Class) globalScope.lookup(name);
	}
}
