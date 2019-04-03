package decaf.dataflow;

import java.io.PrintWriter;
import java.util.*;

import decaf.machdesc.Asm;
import decaf.machdesc.Register;
import decaf.tac.Label;
import decaf.tac.Tac;
import decaf.tac.Temp;

public class BasicBlock {
    public int bbNum;

    public enum EndKind {
        BY_BRANCH, BY_BEQZ, BY_BNEZ, BY_RETURN
    }

    public EndKind endKind;

    public int endId; // last TAC's id for this basic block

    public int inDegree;

    public Tac tacList;

    public Label label;

    public Temp var;

    public Register varReg;

    public int[] next;

    public boolean cancelled;

    public boolean mark;

    public Set<Temp> def;
    
    public Set<Temp> bdef;
    
    public Map<Temp,Integer> lastdef;
    
    public Set<Pair> DUdef;

    public Set<Temp> liveUse;
    
    public Set<Pair> liveDUUse;

    public Set<Temp> liveIn;
    
    public Set<Pair> liveDUIn;

    public Set<Temp> liveOut;
    
    public Set<Pair> liveDUOut;

    public Set<Temp> saves;

    private List<Asm> asms;

    /**
     * DUChain.
     *
     * 表中的每一项 `Pair(p, A) -> ds` 表示 变量 `A` 在定值点 `p` 的 DU 链为 `ds`.
     * 这里 `p` 和 `ds` 中的每一项均指的定值点或引用点对应的那一条 TAC 的 `id`.
     */
    private Map<Pair, Set<Integer>> DUChain;

    public BasicBlock() {
        def = new TreeSet<Temp>(Temp.ID_COMPARATOR);
        liveUse = new TreeSet<Temp>(Temp.ID_COMPARATOR);
        liveIn = new TreeSet<Temp>(Temp.ID_COMPARATOR);
        liveOut = new TreeSet<Temp>(Temp.ID_COMPARATOR);
        next = new int[2];
        asms = new ArrayList<Asm>();

        DUChain = new TreeMap<Pair, Set<Integer>>(Pair.COMPARATOR);
        
        bdef = new TreeSet<Temp>(Temp.ID_COMPARATOR);
        
        lastdef = new TreeMap<Temp,Integer>(Temp.ID_COMPARATOR);
        DUdef = new TreeSet<Pair>(Pair.COMPARATOR);
        
        liveDUUse = new TreeSet<Pair>(Pair.COMPARATOR);
        liveDUIn = new TreeSet<Pair>(Pair.COMPARATOR);
        liveDUOut = new TreeSet<Pair>(Pair.COMPARATOR);
        
    }

    public void allocateTacIds() {
        for (Tac tac = tacList; tac != null; tac = tac.next) {
            tac.id = IDAllocator.apply();
        }
        endId = IDAllocator.apply();
    }

    public void computeDefAndLiveUse() {
        for (Tac tac = tacList; tac != null; tac = tac.next) {
            switch (tac.opc) {
                case ADD:
                case SUB:
                case MUL:
                case DIV:
                case MOD:
                case LAND:
                case LOR:
                case GTR:
                case GEQ:
                case EQU:
                case NEQ:
                case LEQ:
                case LES:
                /* use op1 and op2, def op0 */
                    if (tac.op1.lastVisitedBB != bbNum) {
                        liveUse.add(tac.op1);
                        tac.op1.lastVisitedBB = bbNum;
                    }
                    if (tac.op2.lastVisitedBB != bbNum) {
                        liveUse.add(tac.op2);
                        tac.op2.lastVisitedBB = bbNum;
                    }
                    if (tac.op0.lastVisitedBB != bbNum) {
                        def.add(tac.op0);
                        tac.op0.lastVisitedBB = bbNum;
                    }
                    break;
                case NEG:
                case LNOT:
                case ASSIGN:
                case INDIRECT_CALL:
                case LOAD:
				/* use op1, def op0 */
                    if (tac.op1.lastVisitedBB != bbNum) {
                        liveUse.add(tac.op1);
                        tac.op1.lastVisitedBB = bbNum;
                    }
                    if (tac.op0 != null && tac.op0.lastVisitedBB != bbNum) {  // in INDIRECT_CALL with return type VOID,
                        // tac.op0 is null
                        def.add(tac.op0);
                        tac.op0.lastVisitedBB = bbNum;
                    }
                    break;
                case LOAD_VTBL:
                case DIRECT_CALL:
                case RETURN:
                case LOAD_STR_CONST:
                case LOAD_IMM4:
				/* def op0 */
                    if (tac.op0 != null && tac.op0.lastVisitedBB != bbNum) {  // in DIRECT_CALL with return type VOID,
                        // tac.op0 is null
                        def.add(tac.op0);
                        tac.op0.lastVisitedBB = bbNum;
                    }
                    break;
                case STORE:
				/* use op0 and op1*/
                    if (tac.op0.lastVisitedBB != bbNum) {
                        liveUse.add(tac.op0);
                        tac.op0.lastVisitedBB = bbNum;
                    }
                    if (tac.op1.lastVisitedBB != bbNum) {
                        liveUse.add(tac.op1);
                        tac.op1.lastVisitedBB = bbNum;
                    }
                    break;
                case PARM:
				/* use op0 */
                    if (tac.op0.lastVisitedBB != bbNum) {
                        liveUse.add(tac.op0);
                        tac.op0.lastVisitedBB = bbNum;
                    }
                    break;
                default:
				/* BRANCH MEMO MARK PARM*/
                    break;
            }
        }
        if (var != null && var.lastVisitedBB != bbNum) {
            liveUse.add(var);
            var.lastVisitedBB = bbNum;
        }
        liveIn.addAll(liveUse);
    }
    
    public void computeDUDefAndLiveUse( Map<Temp,Set<BasicBlock>> Defed ) {//在这个地方需要给 
    	bdef.clear();
        for (Tac tac = tacList; tac != null; tac = tac.next) {
            switch (tac.opc) {
                case ADD:
                case SUB:
                case MUL:
                case DIV:
                case MOD:
                case LAND:
                case LOR:
                case GTR:
                case GEQ:
                case EQU:
                case NEQ:
                case LEQ:
                case LES:
                /* use op1 and op2, def op0 */
                    if ( !bdef.contains(tac.op1) ) {

                    	Pair pair = new Pair(tac.id,tac.op1);
                    	liveDUUse.add(pair);
                    }
                    else{
                    	Pair pair = new Pair(lastdef.get(tac.op1),tac.op1);
                    	if(!DUChain.containsKey(pair)) {
            				DUChain.put(pair,new TreeSet<Integer>());
            			}
            			DUChain.get(pair).add(tac.id);
                    }

                    if ( !bdef.contains(tac.op2)) {
                    	
                    	Pair pair = new Pair(tac.id,tac.op2);
                    	liveDUUse.add(pair);
                    	bdef.remove(tac.op2);
                    }                    
                    else{
                    	Pair pair = new Pair(lastdef.get(tac.op2),tac.op2);
                    	if(!DUChain.containsKey(pair)) {
            				DUChain.put(pair,new TreeSet<Integer>());
            			}
            			DUChain.get(pair).add(tac.id);//一段里面先给加进去
                    }
                    
                    if(tac.op0 != null) {//从头put到尾，一直覆盖
                    	if(!bdef.contains(tac.op0))
                    		bdef.add(tac.op0);
                    	lastdef.put(tac.op0,tac.id);
                    	if(Defed.get(tac.op0)==null) {
                        	Defed.put(tac.op0,new HashSet<BasicBlock>());
                        }//在这个地方开始干活儿
                        Defed.get(tac.op0).add(this);//某变量在哪些地方被修改过
                    }
                    break;
                case NEG:
                case LNOT:
                case ASSIGN:
                case INDIRECT_CALL:
                case LOAD:
				/* use op1, def op0 */
                    if ( !bdef.contains(tac.op1) ) {
                    	Pair pair = new Pair(tac.id,tac.op1);
                    	liveDUUse.add(pair);
                    	bdef.remove(tac.op1);
                    }
                    else{
                    	Pair pair = new Pair(lastdef.get(tac.op1),tac.op1);
                    	if(!DUChain.containsKey(pair)) {
            				DUChain.put(pair,new TreeSet<Integer>());
            			}
            			DUChain.get(pair).add(tac.id);
                    }
                    
                    if (tac.op0 != null) {//从头put到尾，一直覆盖
                    	if(!bdef.contains(tac.op0))//不是在块内define过
                    		bdef.add(tac.op0);//如果这个地方没有的话
                    	lastdef.put(tac.op0,tac.id);
                    	if(Defed.get(tac.op0)==null) {//
                        	Defed.put(tac.op0,new HashSet<BasicBlock>());
                        	
                        }
                    	/*Pair pair = new Pair(tac.id,tac.op0);
                    	if(!DUChain.containsKey(pair)) {
            				DUChain.put(pair,new TreeSet<Integer>());
            			}
                    	DUChain.get(pair).add(1000);*/
                        Defed.get(tac.op0).add(this);//某变量在哪些块内被修改过
                    }
                    break;
                case LOAD_VTBL:
                case DIRECT_CALL:
                case RETURN:
                case LOAD_STR_CONST:
                case LOAD_IMM4:
				/* def op0 */
                    if(tac.op0 != null) {//从头put到尾，一直覆盖有的
                    	if(!bdef.contains(tac.op0))
                    		bdef.add(tac.op0);
                    	lastdef.put(tac.op0,tac.id);//最后一次的put出来
                    	if(Defed.get(tac.op0)==null) {
                        	Defed.put(tac.op0,new HashSet<BasicBlock>());
                        }
                        Defed.get(tac.op0).add(this);//某变量在哪些地方被修改过
                    }
                    break;
                case STORE:
				/* use op0 and op1*/

                    if ( !bdef.contains(tac.op0)) {
                    	Pair pair = new Pair(tac.id,tac.op0);
                    	liveDUUse.add(pair);
                    }
                    else {
                    	Pair pair = new Pair(lastdef.get(tac.op0),tac.op0);
                    	if(!DUChain.containsKey(pair)) {
            				DUChain.put(pair,new TreeSet<Integer>());
            			}
            			DUChain.get(pair).add(tac.id);
                    }
                    if ( !bdef.contains(tac.op1)) {
                    	Pair pair = new Pair(tac.id,tac.op1);
                    	liveDUUse.add(pair);
                    }
                    else {
                    	Pair pair = new Pair(lastdef.get(tac.op1),tac.op1);
                    	if(!DUChain.containsKey(pair)) {
            				DUChain.put(pair,new TreeSet<Integer>());
            			}
            			DUChain.get(pair).add(tac.id);
                    }
                    break;
                case PARM://时候用shenme
				/* use op0 */

                    if ( !bdef.contains(tac.op0)) {//如果不是在这一段前面定值的
                    	Pair pair = new Pair(tac.id,tac.op0);
                    	liveDUUse.add(pair);//在liveUse上面加上一个东西
                    }
                    else {
                    	Pair pair = new Pair(lastdef.get(tac.op0),tac.op0);
                    	if(!DUChain.containsKey(pair)) {
            				DUChain.put(pair,new TreeSet<Integer>());
            			}
            			DUChain.get(pair).add(tac.id);
                    }
                    break;
                default:
				/* BRANCH MEMO MARK PARM*/
                    break;
            }
        }

        if( var != null) {
        	if ( !bdef.contains(var)) {//如果不是在这一段前面定值的
        		Pair pair = new Pair(endId,var);
        		liveDUUse.add(pair);//liveUse存在var，在xx地方引用了var
        	}
        	else {
        		Pair pair = new Pair(lastdef.get(var),var);
        		if(!DUChain.containsKey(pair)) {
        			DUChain.put(pair,new TreeSet<Integer>());
        		}
        		DUChain.get(pair).add(endId);
        	}
        }
    }
    
    public void computeDEFED( Map<Temp,Set<BasicBlock>> Defed ) {
    	for (Tac tac = tacList; tac != null; tac = tac.next) {
            switch (tac.opc) {
                case ADD:
                case SUB:
                case MUL:
                case DIV:
                case MOD:
                case LAND:
                case LOR:
                case GTR:
                case GEQ:
                case EQU:
                case NEQ:
                case LEQ:
                case LES:
                /* use op1 and op2, def op0 */
                	if(Defed.containsKey(tac.op1)) {
                		for( BasicBlock bb : Defed.get(tac.op1) ) {
                			if( bb != this ) {
                				Pair pair = new Pair(tac.id,tac.op1);
                        		bb.DUdef.add(pair);
                			}
                		}
                	}
                	if(Defed.containsKey(tac.op2)) {
                		for( BasicBlock bb : Defed.get(tac.op2) ) {
                			if( bb != this ) {
                				Pair pair = new Pair(tac.id,tac.op2);
                        		bb.DUdef.add(pair);
                			}
                		}
                	}
                    break;
                case NEG:
                case LNOT:
                case ASSIGN:
                case INDIRECT_CALL:
                case LOAD:
				/* use op1, def op0 */
                	if(Defed.containsKey(tac.op1)) {
                		for( BasicBlock bb : Defed.get(tac.op1) ) {
                			if( bb != this ) {
                				Pair pair = new Pair(tac.id,tac.op1);
                        		bb.DUdef.add(pair);
                			}
                		}
                	}
                    break;
                case LOAD_VTBL:
                case DIRECT_CALL:
                case RETURN:
                case LOAD_STR_CONST:
                case LOAD_IMM4:
				/* def op0 */
                    break;
                case STORE:
				/* use op0 and op1*/
                	if(Defed.containsKey(tac.op0)) {
                		for( BasicBlock bb : Defed.get(tac.op0) ) {
                			if( bb != this ) {
                				Pair pair = new Pair(tac.id,tac.op0);
                        		bb.DUdef.add(pair);
                			}
                		}
                	}
                	if(Defed.containsKey(tac.op1)) {
                		for( BasicBlock bb : Defed.get(tac.op1) ) {
                			if( bb != this ) {
                				Pair pair = new Pair(tac.id,tac.op1);
                        		bb.DUdef.add(pair);
                			}
                		}
                	}
                    break;
                case PARM://时候用shenme
				/* use op0 */
                	if(Defed.containsKey(tac.op0)) {
                		for( BasicBlock bb : Defed.get(tac.op0) ) {
                			if( bb != this ) {
                				Pair pair = new Pair(tac.id,tac.op0);
                        		bb.DUdef.add(pair);
                			}
                		}
                	}
                    break;
                default:
				/* BRANCH MEMO MARK PARM*/
                    break;
            }
        }
    	if ( var != null) {
    		if(Defed.containsKey(var)) {
    			for(BasicBlock bb : Defed.get(var)) {
    				if(bb != this) {
    					Pair pair = new Pair(endId,var);
    					bb.DUdef.add(pair);
    				}
    			}
    		}
    	}
    	liveDUIn.addAll(liveDUUse);//这一部分直接加上去
    }
    
    public void analyzeDUChain() {//每一个块与块之间的变量都这样开始先进来然后如是做
    	for(Pair du: liveDUOut) {
    		if(lastdef.containsKey(du.tmp)) {
    			Pair p = new Pair(lastdef.get(du.tmp),du.tmp);//位置
    			if(!DUChain.containsKey(p)) {
    				DUChain.put(p,new TreeSet<Integer>());
    			}
    			DUChain.get(p).add(du.pos);
    		}
    	}
    }


    public void analyzeLiveness() {
        if (tacList == null)
            return;
        Tac tac = tacList;
        for (; tac.next != null; tac = tac.next) ;

        tac.liveOut = new HashSet<Temp>(liveOut);
        if (var != null)
            tac.liveOut.add(var);
        for (; tac != tacList; tac = tac.prev) {
            tac.prev.liveOut = new HashSet<Temp>(tac.liveOut);
            switch (tac.opc) {
                case ADD:
                case SUB:
                case MUL:
                case DIV:
                case MOD:
                case LAND:
                case LOR:
                case GTR:
                case GEQ:
                case EQU:
                case NEQ:
                case LEQ:
                case LES:
				/* use op1 and op2, def op0 */
                    tac.prev.liveOut.remove(tac.op0);
                    tac.prev.liveOut.add(tac.op1);
                    tac.prev.liveOut.add(tac.op2);
                    break;
                case NEG:
                case LNOT:
                case ASSIGN:
                case INDIRECT_CALL:
                case LOAD:
				/* use op1, def op0 */
                    tac.prev.liveOut.remove(tac.op0);
                    tac.prev.liveOut.add(tac.op1);
                    break;
                case LOAD_VTBL:
                case DIRECT_CALL:
                case RETURN:
                case LOAD_STR_CONST:
                case LOAD_IMM4:
				/* def op0 */
                    tac.prev.liveOut.remove(tac.op0);
                    break;
                case STORE:
				/* use op0 and op1*/
                    tac.prev.liveOut.add(tac.op0);
                    tac.prev.liveOut.add(tac.op1);
                    break;
                case BEQZ:
                case BNEZ:
                case PARM:
				/* use op0 */
                    tac.prev.liveOut.add(tac.op0);
                    break;
                default:
				/* BRANCH MEMO MARK PARM*/
                    break;
            }
        }
    }

    public void printTo(PrintWriter pw) {
        pw.println("BASIC BLOCK " + bbNum + " : ");
        for (Tac t = tacList; t != null; t = t.next) {
            pw.println("    " + t);
        }
        switch (endKind) {
            case BY_BRANCH:
                pw.println("END BY BRANCH, goto " + next[0]);
                break;
            case BY_BEQZ:
                pw.println("END BY BEQZ, if " + var.name + " = ");
                pw.println("    0 : goto " + next[0] + "; 1 : goto " + next[1]);
                break;
            case BY_BNEZ:
                pw.println("END BY BGTZ, if " + var.name + " = ");
                pw.println("    1 : goto " + next[0] + "; 0 : goto " + next[1]);
                break;
            case BY_RETURN:
                if (var != null) {
                    pw.println("END BY RETURN, result = " + var.name);
                } else {
                    pw.println("END BY RETURN, void result");
                }
                break;
        }
    }

    public void printLivenessTo(PrintWriter pw) {
        pw.println("BASIC BLOCK " + bbNum + " : ");
        pw.println("  Def     = " + toString(def));
        pw.println("  liveUse = " + toString(liveUse));
        pw.println("  liveIn  = " + toString(liveIn));
        pw.println("  liveOut = " + toString(liveOut));

        for (Tac t = tacList; t != null; t = t.next) {
            pw.println("    " + t + " " + toString(t.liveOut));
        }

        switch (endKind) {
            case BY_BRANCH:
                pw.println("END BY BRANCH, goto " + next[0]);
                break;
            case BY_BEQZ:
                pw.println("END BY BEQZ, if " + var.name + " = ");
                pw.println("    0 : goto " + next[0] + "; 1 : goto " + next[1]);
                break;
            case BY_BNEZ:
                pw.println("END BY BGTZ, if " + var.name + " = ");
                pw.println("    1 : goto " + next[0] + "; 0 : goto " + next[1]);
                break;
            case BY_RETURN:
                if (var != null) {
                    pw.println("END BY RETURN, result = " + var.name);
                } else {
                    pw.println("END BY RETURN, void result");
                }
                break;
        }
    }

    public void printDUChainTo(PrintWriter pw) {
        pw.println("BASIC BLOCK " + bbNum + " : ");

        for (Tac t = tacList; t != null; t = t.next) {
            pw.print(t.id + "\t" + t);

            Pair pair = null;
            switch (t.opc) {
                case ADD:
                case SUB:
                case MUL:
                case DIV:
                case MOD:
                case LAND:
                case LOR:
                case GTR:
                case GEQ:
                case EQU:
                case NEQ:
                case LEQ:
                case LES:
                case NEG:
                case LNOT:
                case ASSIGN:
                case INDIRECT_CALL:
                case LOAD:
                case LOAD_VTBL:
                case DIRECT_CALL:
                case RETURN:
                case LOAD_STR_CONST:
                case LOAD_IMM4:
                    if (t.op0 != null) {
                        pair = new Pair(t.id, t.op0);
                    }
                    break;
                case STORE:
                case BEQZ:
                case BNEZ:
                case PARM:
                    break;
                default:
				/* BRANCH MEMO MARK PARM */
                    break;
            }

            if (pair == null) {
                pw.println();
            } else {
                pw.print(" [ ");
                if (pair != null) {
                    Set<Integer> locations = DUChain.get(pair);
                    if (locations != null) {
                        for (Integer loc : locations) {
                            pw.print(loc + " ");
                        }
                    }
                }
                pw.println("]");
            }
        }

        pw.print(endId + "\t");
        switch (endKind) {
            case BY_BRANCH:
                pw.println("END BY BRANCH, goto " + next[0]);
                break;
            case BY_BEQZ:
                pw.println("END BY BEQZ, if " + var.name + " = ");
                pw.println("\t    0 : goto " + next[0] + "; 1 : goto " + next[1]);
                break;
            case BY_BNEZ:
                pw.println("END BY BGTZ, if " + var.name + " = ");
                pw.println("\t    1 : goto " + next[0] + "; 0 : goto " + next[1]);
                break;
            case BY_RETURN:
                if (var != null) {
                    pw.println("END BY RETURN, result = " + var.name);
                } else {
                    pw.println("END BY RETURN, void result");
                }
                break;
        }
    }

    public String toString(Set<Temp> set) {
        StringBuilder sb = new StringBuilder("[ ");
        for (Temp t : set) {
            sb.append(t.name + " ");
        }
        sb.append(']');
        return sb.toString();
    }

    public void insertBefore(Tac insert, Tac base) {
        if (base == tacList) {
            tacList = insert;
        } else {
            base.prev.next = insert;
        }
        insert.prev = base.prev;
        base.prev = insert;
        insert.next = base;
    }

    public void insertAfter(Tac insert, Tac base) {
        if (tacList == null) {
            tacList = insert;
            insert.next = null;
            return;
        }
        if (base.next != null) {
            base.next.prev = insert;
        }
        insert.prev = base;
        insert.next = base.next;
        base.next = insert;
    }

    public void appendAsm(Asm asm) {
        asms.add(asm);
    }

    public List<Asm> getAsms() {
        return asms;
    }
}
