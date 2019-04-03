package decaf.typecheck;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import decaf.Driver;
import decaf.tree.Tree;
import decaf.error.BadArrElementError;
import decaf.error.BadScopyArgError;
import decaf.error.BadScopySrcError;
import decaf.error.BadSealedInherError;
import decaf.error.BadInheritanceError;
import decaf.error.BadOverrideError;
import decaf.error.BadVarTypeError;
import decaf.error.ClassNotFoundError;
import decaf.error.DecafError;
import decaf.error.DeclConflictError;
import decaf.error.NoMainClassError;
import decaf.error.OverridingVarError;
import decaf.scope.ClassScope;
import decaf.scope.GlobalScope;
import decaf.scope.LocalScope;
import decaf.scope.ScopeStack;
import decaf.symbol.Class;
import decaf.symbol.Function;
import decaf.symbol.Symbol;
import decaf.symbol.Variable;
import decaf.type.BaseType;
import decaf.type.FuncType;

public class BuildSym extends Tree.Visitor {

	private ScopeStack table;

	private void issueError(DecafError error) {
		Driver.getDriver().issueError(error);
	}

	public BuildSym(ScopeStack table) {
		this.table = table;
	}

	public static void buildSymbol(Tree.TopLevel tree) {
		new BuildSym(Driver.getDriver().getTable()).visitTopLevel(tree);
	}

	// root
	/*
	@Override
	public void visitTopLevel(Tree.TopLevel program) {
		program.globalScope = new GlobalScope();
		table.open(program.globalScope);
		for (Tree.ClassDef cd : program.classes) {
			Class c = new Class(cd.name, cd.parent, cd.getLocation());
			Class earlier = table.lookupClass(cd.name);
			if (earlier != null) {
				issueError(new DeclConflictError(cd.getLocation(), cd.name,
						earlier.getLocation()));
			} else {
				table.declare(c);
			}
			cd.symbol = c;
		}

		for (Tree.ClassDef cd : program.classes) {
			Class c = cd.symbol;
			if (cd.parent != null && c.getParent() == null) {
				issueError(new ClassNotFoundError(cd.getLocation(), cd.parent));
				c.dettachParent();
			}
			if (calcOrder(c) <= calcOrder(c.getParent())) {
				issueError(new BadInheritanceError(cd.getLocation()));
				c.dettachParent();
			}
		}

		for (Tree.ClassDef cd : program.classes) {
			cd.symbol.createType();
		}

		for (Tree.ClassDef cd : program.classes) {
			cd.accept(this);
			if (Driver.getDriver().getOption().getMainClassName().equals(
					cd.name)) {
				program.main = cd.symbol;
			}
		}

		for (Tree.ClassDef cd : program.classes) {
			checkOverride(cd.symbol);
		}

		if (!isMainClass(program.main)) {
			issueError(new NoMainClassError(Driver.getDriver().getOption()
					.getMainClassName()));
		}
		table.close();
	}*/
	
	public void visitTopLevel(Tree.TopLevel program) {
		program.globalScope = new GlobalScope();
		table.open(program.globalScope);//有这些东西在里面

		Set<String> seal = new HashSet<String>();
		for (Tree.ClassDef cd : program.classes) {//开始干活儿!InheritanceError
			Class c = new Class(cd.name, cd.parent, cd.getLocation());//每一个地方是否都有过是否有加入C
			Class earlier = table.lookupClass(cd.name);//这个是什么?
			if (earlier != null) {//
				issueError(new DeclConflictError(cd.getLocation(), cd.name,
						earlier.getLocation()));//首先,它是
			} else {
				table.declare(c);//declare一处的<进入>(有这一些的可以declare的地方)
			}
			cd.symbol = c;
		}

		for (Tree.ClassDef cd : program.classes) {
			Class c = cd.symbol;//每一个的symbol都遍历一遍!
			if (cd.parent != null && c.getParent() == null) {//如果找到null的父母,那么就...
				issueError(new ClassNotFoundError(cd.getLocation(), cd.parent));
				c.dettachParent();//
			}
			if (calcOrder(c) <= calcOrder(c.getParent())) {//calc
				issueError(new BadInheritanceError(cd.getLocation()));
				c.dettachParent();
			}
		}

		for (Tree.ClassDef cd : program.classes) {
			cd.symbol.createType();
			if(cd.sealed)
				seal.add(cd.name);
		}
		
		for(Tree.ClassDef cd : program.classes ) {
			if(cd.parent != null)//如果有爹娘的话，那么...
				if(seal.contains(cd.parent))//如果有这样的话
					issueError(new BadSealedInherError(cd.loc));//没有能够sealed
		}

		for (Tree.ClassDef cd : program.classes) {
			cd.accept(this);
			if (Driver.getDriver().getOption().getMainClassName().equals(
					cd.name)) {
				program.main = cd.symbol;
			}
		}

		for (Tree.ClassDef cd : program.classes) {
			checkOverride(cd.symbol);
		}

		if (!isMainClass(program.main)) {
			issueError(new NoMainClassError(Driver.getDriver().getOption()
					.getMainClassName()));
		}
		table.close();//最终pop出来一个
	}

	// visiting declarations
	@Override
	public void visitClassDef(Tree.ClassDef classDef) {//每一个都需要进入这个symbol
		table.open(classDef.symbol.getAssociatedScope());
		for (Tree f : classDef.fields) {
			f.accept(this);//每一棵树都需要accept
		}
		table.close();
	}
	
	public void visitVarValue(Tree.VarValue varValue) {
		Symbol sym = table.lookupBeforeLocation(varValue.name, varValue
				.getLocation());
		if (sym != null) {//是否和前面的东西冲突继承一个新的
			if (table.getCurrentScope().equals(sym.getScope())) {//这个地方是会和别的地方有冲突的
				issueError(new DeclConflictError(varValue.getLocation(), varValue.name,//名字
						sym.getLocation()));
			} else if ((sym.getScope().isFormalScope() && table.getCurrentScope().isLocalScope() && ((LocalScope)table.getCurrentScope()).isCombinedtoFormal() )) {
				issueError(new DeclConflictError(varValue.getLocation(), varValue.name,
						sym.getLocation()));
			}
			else {
				Variable v = new Variable(varValue.name, BaseType.UNKNOWN, 
						varValue.getLocation());
				table.declare(v);//是否会有不可以的东西
			}
		}
		else {
			Variable v = new Variable(varValue.name, BaseType.UNKNOWN, 
					varValue.getLocation());
			table.declare(v);
		}
	}
	
	public void visitAssign(Tree.Assign assign) {//确认只有在assign的时候才能接触到它
		assign.left.accept(this);//直接判断是否错误
	}
	
	public void visitForeachLoop(Tree.ForeachLoop foreachLoop) {
		
		foreachLoop.ownBlock.associatedScope = new LocalScope(foreachLoop.ownBlock);//对于LocalScope的话,先弹入是比较好的
		table.open(foreachLoop.ownBlock.associatedScope);//得要重写block部分
		if(foreachLoop.var != null)
			foreachLoop.var.accept(this);
		else
			foreachLoop.unkvar.accept(this);
		for (Tree s : foreachLoop.ownBlock.block) {
			s.accept(this);//先检查!
		}
		table.close();
	}

	@Override
	public void visitVarDef(Tree.VarDef varDef) {
		varDef.type.accept(this);
		if (varDef.type.type.equal(BaseType.VOID)) {//如果是别的type的话，那么
			issueError(new BadVarTypeError(varDef.getLocation(), varDef.name));
			// for argList
			varDef.symbol = new Variable(".error", BaseType.ERROR, varDef
					.getLocation());
			return;
		}
		Variable v = new Variable(varDef.name, varDef.type.type, 
				varDef.getLocation());
		Symbol sym = table.lookup(varDef.name, true);
		if (sym != null) {
			if (table.getCurrentScope().equals(sym.getScope())) {//这个地方是会和别的地方有冲突的现在的scope是否是scopy的scope
				issueError(new DeclConflictError(v.getLocation(), v.getName(),//名字
						sym.getLocation()));
			} else if ((sym.getScope().isFormalScope() && table.getCurrentScope().isLocalScope() && ((LocalScope)table.getCurrentScope()).isCombinedtoFormal() )) {
				issueError(new DeclConflictError(v.getLocation(), v.getName(),
						sym.getLocation()));//如果重定义的话
			} else {
				table.declare(v);
			}
		} else {
			table.declare(v);
		}
		varDef.symbol = v;
	}

	@Override
	public void visitMethodDef(Tree.MethodDef funcDef) {
		funcDef.returnType.accept(this);
		Function f = new Function(funcDef.statik, funcDef.name,
				funcDef.returnType.type, funcDef.body, funcDef.getLocation());
		funcDef.symbol = f;
		Symbol sym = table.lookup(funcDef.name, false);
		if (sym != null) {
			issueError(new DeclConflictError(funcDef.getLocation(),
					funcDef.name, sym.getLocation()));
		} else {
			table.declare(f);
		}
		table.open(f.getAssociatedScope());
		for (Tree.VarDef d : funcDef.formals) {
			d.accept(this);
			f.appendParam(d.symbol);
		}

		funcDef.body.associatedScope = new LocalScope(funcDef.body);
		funcDef.body.associatedScope.setCombinedtoFormal(true);
		table.open(funcDef.body.associatedScope);
		for (Tree s : funcDef.body.block) {
			s.accept(this);
		}
		table.close();
		table.close();
	}

	// visiting types
	@Override
	public void visitTypeIdent(Tree.TypeIdent type) {
		switch (type.typeTag) {
		case Tree.VOID:
			type.type = BaseType.VOID;//设定不同的type
			break;
		case Tree.INT:
			type.type = BaseType.INT;
			break;
		case Tree.BOOL:
			type.type = BaseType.BOOL;
			break;
		default:
			type.type = BaseType.STRING;
		}
	}

	@Override
	public void visitTypeClass(Tree.TypeClass typeClass) {
		Class c = table.lookupClass(typeClass.name);
		if (c == null) {
			issueError(new ClassNotFoundError(typeClass.getLocation(),
					typeClass.name));
			typeClass.type = BaseType.ERROR;
		} else {
			typeClass.type = c.getType();
		}
	}

	@Override
	public void visitTypeArray(Tree.TypeArray typeArray) {
		typeArray.elementType.accept(this);
		if (typeArray.elementType.type.equal(BaseType.ERROR)) {
			typeArray.type = BaseType.ERROR;
		} else if (typeArray.elementType.type.equal(BaseType.VOID)) {
			issueError(new BadArrElementError(typeArray.getLocation()));
			typeArray.type = BaseType.ERROR;
		} else {
			typeArray.type = new decaf.type.ArrayType(
					typeArray.elementType.type);
		}
	}

	// for VarDecl in LocalScope
	@Override
	public void visitBlock(Tree.Block block) {//新的Block
		block.associatedScope = new LocalScope(block);//对于LocalScope的话
		table.open(block.associatedScope);
		for (Tree s : block.block) {
			s.accept(this);
		}
		table.close();
	}

	@Override
	public void visitForLoop(Tree.ForLoop forLoop) {
		if (forLoop.loopBody != null) {
			forLoop.loopBody.accept(this);
		}
	}
	

	

	@Override
	public void visitIf(Tree.If ifStmt) {
		if (ifStmt.trueBranch != null) {
			ifStmt.trueBranch.accept(this);
		}
		if (ifStmt.falseBranch != null) {
			ifStmt.falseBranch.accept(this);
		}
	}

	@Override
	public void visitWhileLoop(Tree.WhileLoop whileLoop) {
		if (whileLoop.loopBody != null) {
			whileLoop.loopBody.accept(this);
		}
	}

	public void visitSubStmt(Tree.SubStmt subStmt) {
		subStmt.lexpr.accept(this);
		subStmt.rexpr.accept(this);
	}
	
	public void visitGuardStmt(Tree.GuardStmt guardStmt) {
		for( Tree.SubStmt v :guardStmt.works)
			v.accept(this);
	}
	
	public void visitScopy(Tree.Scopy scopy) {
		scopy.lvalue.accept(this);
		scopy.expr.accept(this);
	}
	
	private int calcOrder(Class c) {
		if (c == null) {
			return -1;
		}
		if (c.getOrder() < 0) {
			c.setOrder(0);
			c.setOrder(calcOrder(c.getParent()) + 1);
		}
		return c.getOrder();
	}

	private void checkOverride(Class c) {
		if (c.isCheck()) {
			return;
		}
		Class parent = c.getParent();
		if (parent == null) {
			return;
		}
		checkOverride(parent);

		ClassScope parentScope = parent.getAssociatedScope();
		ClassScope subScope = c.getAssociatedScope();
		table.open(parentScope);
		Iterator<Symbol> iter = subScope.iterator();
		while (iter.hasNext()) {
			Symbol suspect = iter.next();
			Symbol sym = table.lookup(suspect.getName(), true);
			if (sym != null && !sym.isClass()) {
				if ((suspect.isVariable() && sym.isFunction())
						|| (suspect.isFunction() && sym.isVariable())) {
					issueError(new DeclConflictError(suspect.getLocation(),
							suspect.getName(), sym.getLocation()));
					iter.remove();
				} else if (suspect.isFunction()) {
					if (((Function) suspect).isStatik()
							|| ((Function) sym).isStatik()) {
						issueError(new DeclConflictError(suspect.getLocation(),
								suspect.getName(), sym.getLocation()));
						iter.remove();
					} else if (!suspect.getType().compatible(sym.getType())) {
						issueError(new BadOverrideError(suspect.getLocation(),
								suspect.getName(),
								((ClassScope) sym.getScope()).getOwner()
										.getName()));
						iter.remove();
					}
				} else if (suspect.isVariable()) {
					issueError(new OverridingVarError(suspect.getLocation(),
							suspect.getName()));
					iter.remove();
				}
			}
		}
		table.close();
		c.setCheck(true);
	}

	private boolean isMainClass(Class c) {
		if (c == null) {
			return false;
		}
		table.open(c.getAssociatedScope());
		Symbol main = table.lookup(Driver.getDriver().getOption()
				.getMainFuncName(), false);
		if (main == null || !main.isFunction()) {
			return false;
		}
		((Function) main).setMain(true);
		FuncType type = (FuncType) main.getType();
		return type.getReturnType().equal(BaseType.VOID)
				&& type.numOfParams() == 0 && ((Function) main).isStatik();
	}
}
