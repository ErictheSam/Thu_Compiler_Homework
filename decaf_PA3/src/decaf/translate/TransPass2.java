package decaf.translate;

import java.util.Stack;

import decaf.tree.Tree;
import decaf.backend.OffsetCounter;
import decaf.machdesc.Intrinsic;
import decaf.symbol.Variable;
import decaf.tac.Label;
import decaf.tac.Temp;
import decaf.type.BaseType;
import decaf.type.ClassType;
import decaf.type.Type;
import decaf.scope.ClassScope;

public class TransPass2 extends Tree.Visitor {

	private Translater tr;

	private Temp currentThis;

	private Stack<Label> loopExits;

	public TransPass2(Translater tr) {
		this.tr = tr;
		loopExits = new Stack<Label>();
	}

	@Override
	public void visitClassDef(Tree.ClassDef classDef) {
		for (Tree f : classDef.fields) {
			f.accept(this);
		}
	}

	@Override
	public void visitMethodDef(Tree.MethodDef funcDefn) {
		if (!funcDefn.statik) {
			currentThis = ((Variable) funcDefn.symbol.getAssociatedScope()
					.lookup("this")).getTemp();
		}
		tr.beginFunc(funcDefn.symbol);
		funcDefn.body.accept(this);
		tr.endFunc();
		currentThis = null;
	}

	@Override
	public void visitTopLevel(Tree.TopLevel program) {
		for (Tree.ClassDef cd : program.classes) {
			cd.accept(this);
		}
	}

	@Override
	public void visitVarDef(Tree.VarDef varDef) {
		if (varDef.symbol.isLocalVar()) {
			Temp t = Temp.createTempI4();
			t.sym = varDef.symbol;
			varDef.symbol.setTemp(t);
		}
	}

	@Override
	public void visitBinary(Tree.Binary expr) {
		expr.left.accept(this);
		expr.right.accept(this);
		switch (expr.tag) {
		case Tree.PLUS:
			expr.val = tr.genAdd(expr.left.val, expr.right.val);
			break;
		case Tree.MINUS:
			expr.val = tr.genSub(expr.left.val, expr.right.val);
			break;
		case Tree.MUL:
			expr.val = tr.genMul(expr.left.val, expr.right.val);
			break;
		case Tree.DIV:
			expr.val = tr.genDiv(expr.left.val, expr.right.val);
			break;
		case Tree.MOD:
			expr.val = tr.genMod(expr.left.val, expr.right.val);
			break;
		case Tree.AND:
			expr.val = tr.genLAnd(expr.left.val, expr.right.val);
			break;
		case Tree.OR:
			expr.val = tr.genLOr(expr.left.val, expr.right.val);
			break;
		case Tree.LT:
			expr.val = tr.genLes(expr.left.val, expr.right.val);
			break;
		case Tree.LE:
			expr.val = tr.genLeq(expr.left.val, expr.right.val);
			break;
		case Tree.GT:
			expr.val = tr.genGtr(expr.left.val, expr.right.val);
			break;
		case Tree.GE:
			expr.val = tr.genGeq(expr.left.val, expr.right.val);
			break;
		case Tree.EQ:
		case Tree.NE:
			genEquNeq(expr);
			break;
		}
	}

	private void genEquNeq(Tree.Binary expr) {
		if (expr.left.type.equal(BaseType.STRING)
				|| expr.right.type.equal(BaseType.STRING)) {
			tr.genParm(expr.left.val);
			tr.genParm(expr.right.val);
			expr.val = tr.genDirectCall(Intrinsic.STRING_EQUAL.label,
					BaseType.BOOL);
			if(expr.tag == Tree.NE){
				expr.val = tr.genLNot(expr.val);
			}
		} else {
			if(expr.tag == Tree.EQ)
				expr.val = tr.genEqu(expr.left.val, expr.right.val);
			else
				expr.val = tr.genNeq(expr.left.val, expr.right.val);
		}
	}
	
	@Override
	public void visitAssign(Tree.Assign assign) {
		assign.left.accept(this);
		assign.expr.accept(this);
		switch (assign.left.lvKind) {
		case ARRAY_ELEMENT:
			Tree.Indexed arrayRef = (Tree.Indexed) assign.left;
			Temp esz = tr.genLoadImm4(OffsetCounter.WORD_SIZE);
			Temp t = tr.genMul(arrayRef.index.val, esz);
			Temp base = tr.genAdd(arrayRef.array.val, t);
			tr.genStore(assign.expr.val, base, 0);
			break;
		case MEMBER_VAR:
			Tree.Ident varRef = (Tree.Ident) assign.left;
			tr.genStore(assign.expr.val, varRef.owner.val, varRef.symbol
					.getOffset());
			break;
		case PARAM_VAR:
		case LOCAL_VAR:
			tr.genAssign(((Tree.Ident) assign.left).symbol.getTemp(),
					assign.expr.val);
			break;
		case VAR_IDENT:
			tr.genAssign(assign.left.symbol.getTemp(), assign.expr.val);
		}
	}

	@Override
	public void visitLiteral(Tree.Literal literal) {
		switch (literal.typeTag) {
		case Tree.INT:
			literal.val = tr.genLoadImm4(((Integer)literal.value).intValue());
			break;
		case Tree.BOOL:
			literal.val = tr.genLoadImm4((Boolean)(literal.value) ? 1 : 0);
			break;
		default:
			literal.val = tr.genLoadStrConst((String)literal.value);
		}
	}

	@Override
	public void visitExec(Tree.Exec exec) {
		exec.expr.accept(this);
	}

	@Override
	public void visitUnary(Tree.Unary expr) {
		expr.expr.accept(this);
		switch (expr.tag){
		case Tree.NEG:
			expr.val = tr.genNeg(expr.expr.val);
			break;
		default:
			expr.val = tr.genLNot(expr.expr.val);
		}
	}

	@Override
	public void visitNull(Tree.Null nullExpr) {
		nullExpr.val = tr.genLoadImm4(0);
	}

	@Override
	public void visitBlock(Tree.Block block) {
		for (Tree s : block.block) {
			s.accept(this);
		}
	}

	@Override
	public void visitThisExpr(Tree.ThisExpr thisExpr) {
		thisExpr.val = currentThis;
	}

	@Override
	public void visitReadIntExpr(Tree.ReadIntExpr readIntExpr) {
		readIntExpr.val = tr.genIntrinsicCall(Intrinsic.READ_INT);
	}

	@Override
	public void visitReadLineExpr(Tree.ReadLineExpr readStringExpr) {
		readStringExpr.val = tr.genIntrinsicCall(Intrinsic.READ_LINE);
	}

	@Override
	public void visitReturn(Tree.Return returnStmt) {
		if (returnStmt.expr != null) {
			returnStmt.expr.accept(this);
			tr.genReturn(returnStmt.expr.val);
		} else {
			tr.genReturn(null);
		}

	}

	@Override
	public void visitPrint(Tree.Print printStmt) {
		for (Tree.Expr r : printStmt.exprs) {
			r.accept(this);
			tr.genParm(r.val);
			if (r.type.equal(BaseType.BOOL)) {
				tr.genIntrinsicCall(Intrinsic.PRINT_BOOL);
			} else if (r.type.equal(BaseType.INT)) {
				tr.genIntrinsicCall(Intrinsic.PRINT_INT);
			} else if (r.type.equal(BaseType.STRING)) {
				tr.genIntrinsicCall(Intrinsic.PRINT_STRING);
			}
		}
	}

	@Override
	public void visitIndexed(Tree.Indexed indexed) {///那个index
		indexed.array.accept(this);
		indexed.index.accept(this);
		tr.genCheckArrayIndex(indexed.array.val, indexed.index.val);
		
		Temp esz = tr.genLoadImm4(OffsetCounter.WORD_SIZE);//这个是什么
		Temp t = tr.genMul(indexed.index.val, esz);//
		Temp base = tr.genAdd(indexed.array.val, t);//
		indexed.val = tr.genLoad(base, 0);//
	}
	

	@Override
	public void visitIdent(Tree.Ident ident) {//
		if(ident.lvKind == Tree.LValue.Kind.MEMBER_VAR){
			ident.owner.accept(this);
		}
		switch (ident.lvKind) {
		case MEMBER_VAR:
			ident.val = tr.genLoad(ident.owner.val, ident.symbol.getOffset());//val给存在了owner里面
			break;
		default:
			ident.val = ident.symbol.getTemp();
			break;
		}
	}
	
	@Override
	public void visitBreak(Tree.Break breakStmt) {
		tr.genBranch(loopExits.peek());///新的需要出来的loop，然后在这个地方开始干活儿
	}

	@Override
	public void visitCallExpr(Tree.CallExpr callExpr) {
		if (callExpr.isArrayLength) {
			callExpr.receiver.accept(this);
			callExpr.val = tr.genLoad(callExpr.receiver.val,
					-OffsetCounter.WORD_SIZE);
		} else {
			if (callExpr.receiver != null) {
				callExpr.receiver.accept(this);
			}
			for (Tree.Expr expr : callExpr.actuals) {
				expr.accept(this);
			}
			if (callExpr.receiver != null) {
				tr.genParm(callExpr.receiver.val);
			}
			for (Tree.Expr expr : callExpr.actuals) {
				tr.genParm(expr.val);
			}
			if (callExpr.receiver == null) {
				callExpr.val = tr.genDirectCall(
						callExpr.symbol.getFuncty().label, callExpr.symbol
								.getReturnType());
			} else {
				Temp vt = tr.genLoad(callExpr.receiver.val, 0);
				Temp func = tr.genLoad(vt, callExpr.symbol.getOffset());//getOffset
				callExpr.val = tr.genIndirectCall(func, callExpr.symbol
						.getReturnType());
			}
		}
	}

	@Override
	public void visitForLoop(Tree.ForLoop forLoop) {
		if (forLoop.init != null) {//首先初始化
			forLoop.init.accept(this);
		}
		Label cond = Label.createLabel();//condition
		Label loop = Label.createLabel();//loop
		tr.genBranch(cond);//mein gott...还要进一步的再次干活儿...
		tr.genMark(loop);//进入loop标签循环
		if (forLoop.update != null) {//如果可以继续update
			forLoop.update.accept(this);//
		}
		tr.genMark(cond);
		forLoop.condition.accept(this);
		Label exit = Label.createLabel();//进入出来
		tr.genBeqz(forLoop.condition.val, exit);//进入的话
		loopExits.push(exit);//进入exits
		if (forLoop.loopBody != null) {
			forLoop.loopBody.accept(this);
		}
		tr.genBranch(loop);
		loopExits.pop();
		tr.genMark(exit);
	}
	
	public void visitForeachLoop(Tree.ForeachLoop foreachLoop) {
		Temp vari;
		if(foreachLoop.unkvar != null) {
			foreachLoop.unkvar.accept(this);
			vari = foreachLoop.unkvar.symbol.getTemp();
		}
		else {
			foreachLoop.var.accept(this);
			vari = foreachLoop.var.symbol.getTemp();
		}//找到那个type
		foreachLoop.rexpr.accept(this);
		Temp array = foreachLoop.rexpr.val;
		Label loop = Label.createLabel();
		Label exit = Label.createLabel();
		Temp length = tr.genLoad(array, -OffsetCounter.WORD_SIZE);//先把Array-4给load进来
		Temp index = tr.genLoadImm4(0);
		Temp unit = tr.genLoadImm4(OffsetCounter.WORD_SIZE);
		tr.genMark(loop);
		Temp cond = tr.genLes(index, length);//是否index小于length
		tr.genBeqz(cond, exit);
		Temp offset = tr.genMul(unit, index);
		Temp strant = tr.genAdd(array, offset);
		tr.genAssign(vari,tr.genLoad(strant,0));
		if( foreachLoop.condexpr != null ) {
			foreachLoop.condexpr.accept(this);
			tr.genBeqz(foreachLoop.condexpr.val,exit);
		}
		loopExits.push(exit);//进入exits
		foreachLoop.ownBlock.accept(this);
		Temp tmp = tr.genAdd(index,tr.genLoadImm4(1));//成功的完成了这一切就++
		tr.genAssign(index,tmp);
		tr.genBranch(loop);
		loopExits.pop();
		tr.genMark(exit);//出来!
	}

	@Override
	public void visitIf(Tree.If ifStmt) {
		ifStmt.condition.accept(this);
		if (ifStmt.falseBranch != null) {
			Label falseLabel = Label.createLabel();
			tr.genBeqz(ifStmt.condition.val, falseLabel);
			ifStmt.trueBranch.accept(this);
			Label exit = Label.createLabel();
			tr.genBranch(exit);
			tr.genMark(falseLabel);
			ifStmt.falseBranch.accept(this);
			tr.genMark(exit);
		} else if (ifStmt.trueBranch != null) { //是否会要
			Label exit = Label.createLabel();
			tr.genBeqz(ifStmt.condition.val, exit);
			if (ifStmt.trueBranch != null) {
				ifStmt.trueBranch.accept(this);
			}
			tr.genMark(exit);
		}
	}
	
/*	public void visitScopy( Tree.Scopy scopy ) {
		
	}*/
	
	public void visitVarValue(Tree.VarValue varValue) {
		varValue.val = varValue.symbol.getTemp();
		varValue.lvKind = Tree.LValue.Kind.VAR_IDENT;
		Temp t = Temp.createTempI4();
		t.sym = varValue.symbol;
		varValue.symbol.setTemp(t);
	}
	
	public void visitSubStmt (Tree.SubStmt subStmt ) {
		subStmt.lexpr.accept(this);
		Label exit = Label.createLabel();
		tr.genBeqz(subStmt.lexpr.val,exit);
		if(subStmt.rexpr != null)
			subStmt.rexpr.accept(this);
		tr.genMark(exit);
	}
	
	public void visitGuardStmt( Tree.GuardStmt guardStmt ) {
		for(Tree.SubStmt subStmt : guardStmt.works )
			subStmt.accept(this);
	}

	@Override
	public void visitNewArray(Tree.NewArray newArray) {
		newArray.length.accept(this);
		newArray.val = tr.genNewArray(newArray.length.val);
	}

	@Override
	public void visitNewClass(Tree.NewClass newClass) {///newClass
		newClass.val = tr.genDirectCall(newClass.symbol.getNewFuncLabel(),
				BaseType.INT);
	}

	@Override
	public void visitWhileLoop(Tree.WhileLoop whileLoop) {
		Label loop = Label.createLabel();
		tr.genMark(loop);
		whileLoop.condition.accept(this);
		Label exit = Label.createLabel();
		tr.genBeqz(whileLoop.condition.val, exit);
		loopExits.push(exit);
		if (whileLoop.loopBody != null) {
			whileLoop.loopBody.accept(this);
		}
		tr.genBranch(loop);
		loopExits.pop();
		tr.genMark(exit);
	}

	@Override
	public void visitTypeTest(Tree.TypeTest typeTest) {
		typeTest.instance.accept(this);
		typeTest.val = tr.genInstanceof(typeTest.instance.val,
				typeTest.symbol);
	}

	@Override
	public void visitTypeCast(Tree.TypeCast typeCast) {
		typeCast.expr.accept(this);
		if (!typeCast.expr.type.compatible(typeCast.symbol.getType())) {
			tr.genClassCast(typeCast.expr.val, typeCast.symbol);
		}
		typeCast.val = typeCast.expr.val;
	}
	
	public void visitScopy(Tree.Scopy scopy) {
		scopy.lvalue.accept(this);
		scopy.expr.accept(this);
		ClassType tip = (ClassType)(scopy.lvalue.type);
		tr.genScopy(tip.getClassScope(),scopy.lvalue.val,scopy.expr.val);
	}
	
	public void visitTimeArrayConst(Tree.TimeArrayConst timeArrayConst ) {
		timeArrayConst.lexpr.accept(this);
		timeArrayConst.rexpr.accept(this);
		ClassScope tip;
		if(timeArrayConst.lexpr.type.isClassType()) {
			 tip= ((ClassType)(timeArrayConst.lexpr.type)).getClassScope();
		}
		else
			tip = null;
		timeArrayConst.val = tr.genNewWithArray(tip
				,timeArrayConst.lexpr.val,timeArrayConst.rexpr.val);
	}
	
	public void visitDefaultConst(Tree.DefaultConst defaultConst ) {
		defaultConst.array.accept(this);
		defaultConst.rank.accept(this);
		defaultConst.rexpr.accept(this);
		Label exit = Label.createLabel();
		Label tag = Label.createLabel();
		tr.getCheckDefaultArrayIndex(defaultConst.array.val, defaultConst.rank.val, 
				defaultConst.rexpr.val,exit);
		Temp esz = tr.genLoadImm4(OffsetCounter.WORD_SIZE);//这个是什么
		Temp t = tr.genMul(defaultConst.rank.val, esz);//
		Temp base = tr.genAdd(defaultConst.array.val, t);//
		defaultConst.val = tr.genLoad(base, 0);//
		tr.genBranch(tag);
		tr.genMark(exit);
		tr.genAssign(defaultConst.val,defaultConst.rexpr.val);
		tr.genMark(tag);
	}
	
	
/*	public void visitForeachLoop(Tree.ForeachLoop foreachLoop) {
		if(foreachLoop.var != null) {
			foreachLoop.var.visit(this);
		}
		else
			foreachLoop.unkvar.visit(this);
		Label cond = Label.createLabel();
		Label loop = Label.createLabel();
		Label quit = Label.createLabel();
		tr.genBranch(cond);
		tr.genMark(loop);
		
		tr.genMark(quit);
		if (forLoop.init != null) {//首先初始化
			forLoop.init.accept(this);
		}
		Label cond = Label.createLabel();//condition
		Label loop = Label.createLabel();//loop
		tr.genBranch(cond);//mein gott...
		tr.genMark(loop);//进入loop标签循环
		if (forLoop.update != null) {//如果可以继续update
			forLoop.update.accept(this);//
		}
		tr.genMark(cond);
		forLoop.condition.accept(this);
		Label exit = Label.createLabel();
		tr.genBeqz(forLoop.condition.val, exit);
		loopExits.push(exit);//进入exits
		if (forLoop.loopBody != null) {
			forLoop.loopBody.accept(this);
		}
		tr.genBranch(loop);
		loopExits.pop();
		tr.genMark(exit);
	}*/
	

}
