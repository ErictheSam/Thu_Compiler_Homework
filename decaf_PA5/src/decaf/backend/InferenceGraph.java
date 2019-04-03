package decaf.backend;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;

import decaf.Driver;
import decaf.dataflow.BasicBlock;
import decaf.machdesc.Register;
import decaf.tac.Tac;
import decaf.tac.Temp;


class InferenceGraph {
	public Set<Temp> nodes = new HashSet<>();
	public Map<Temp, Set<Temp>> neighbours = new HashMap<>();
	public Map<Temp, Integer> nodeDeg = new HashMap<>();
	public BasicBlock bb;
	public Register[] regs;
	public Register fp;//
	public Set<Temp> liveUseLoad = new HashSet<>();


	private void clear() {
		nodes.clear();
		neighbours.clear();
		nodeDeg.clear();
		liveUseLoad.clear();
	}


	public void alloc(BasicBlock bb, Register[] regs, Register fp) {
		this.regs = regs;
		this.bb = bb;
		this.fp = fp;
		while (true) {
			clear();
			makeGraph();
			if (color())
				break;
			// For simplicity, omit handling for spilling.
		}
	}


	private void addNode(Temp node) {//如果这个fp被采用的话
		if (nodes.contains(node)) return;//
		if (node.reg != null && node.reg.equals(fp)) return;//如果没有寄存器后者在别的上面的话，那么滚蛋
		nodes.add(node);
		neighbours.put(node, new HashSet<Temp>());
		nodeDeg.put(node, 0);
	}


	private void removeNode(Temp n) {
		nodes.remove(n);
		for (Temp m : neighbours.get(n))
			if (nodes.contains(m))
				nodeDeg.put(m, nodeDeg.get(m) - 1);
	}


	private void addEdge(Temp a, Temp b) {
		neighbours.get(a).add(b);
		neighbours.get(b).add(a);
		nodeDeg.put(a, nodeDeg.get(a) + 1);
		nodeDeg.put(b, nodeDeg.get(b) + 1);
	}


	private boolean color() {
		if (nodes.isEmpty())
			return true;

		// Try to find a node with less than K neighbours
		Temp n = null;
		for (Temp t : nodes) {
			if (nodeDeg.get(t) < regs.length) {
				n = t;
				break;
			}
		}

		if (n != null) {
			// We've found such a node.
			removeNode(n);
			boolean subColor = color();
			n.reg = chooseAvailableRegister(n);//直接和自己的reg属性结合
			return subColor;//首先，这个地方指的是
		} else {
			throw new IllegalArgumentException(
					"Coloring with spilling is not yet supported");
		}
	}


	Register chooseAvailableRegister(Temp n) {
		Set<Register> usedRegs = new HashSet<>();
		for (Temp m : neighbours.get(n)) {
			if (m.reg == null) continue;
			usedRegs.add(m.reg);
		}
		for (Register r : regs)
			if (!usedRegs.contains(r))
				return r;
		return null;
	}


	void makeGraph() {
		// First identify all nodes. 
		// Each value is a node.
		makeNodes();
		// Then build inference edges:
		// It's your job to decide what values should be linked.
		makeEdges();
	}


	void makeNodes() {
		for (Tac tac = bb.tacList; tac != null; tac = tac.next) {
			switch (tac.opc) {
				case ADD: case SUB: case MUL: case DIV: case MOD:
				case LAND: case LOR: case GTR: case GEQ: case EQU:
				case NEQ: case LEQ: case LES://是否每一步都是和Node相同?
					addNode(tac.op0); addNode(tac.op1); addNode(tac.op2);
					break;

				case NEG: case LNOT: case ASSIGN:
					addNode(tac.op0); addNode(tac.op1);
					break;

				case LOAD_VTBL: case LOAD_IMM4: case LOAD_STR_CONST:
					addNode(tac.op0);
					break;

				case INDIRECT_CALL:
					addNode(tac.op1);
				case DIRECT_CALL:
					// tac.op0 is used to hold the return value.
					// If we are calling a function with void type, then tac.op0 is null.
					if (tac.op0 != null) addNode(tac.op0);
					break;

				case PARM:
					addNode(tac.op0);
					break;

				case LOAD:
				case STORE:
					addNode(tac.op0); addNode(tac.op1);
					break;

				case BRANCH: case BEQZ: case BNEZ: case RETURN:
					throw new IllegalArgumentException();
			}
		}
	}


	// With your definition of inference graphs, build the edges.
	void makeEdges() {
		for( Temp i : bb.liveUse ) {
			if(!nodes.contains(i)) {
				nodes.add(i);
				nodeDeg.put(i,0);
				neighbours.put(i, new HashSet<Temp>());
			}
			for( Temp j : bb.liveUse ) {
				if(!nodes.contains(j)) {
					nodes.add(j);
					nodeDeg.put(j,0);
					neighbours.put(j, new HashSet<Temp>());
				}
				if(i != j && !(neighbours.get(i).contains(j))) {
					addEdge(i,j);
				}
			}
		}
		for (Tac tac = bb.tacList; tac != null; tac = tac.next) {

			switch (tac.opc) {
				case ADD: case SUB: case MUL: case DIV: case MOD:
				case LAND: case LOR: case GTR: case GEQ: case EQU:
				case NEQ: case LEQ: case LES:
				case NEG: case LNOT: case ASSIGN:			
				case LOAD_VTBL: case LOAD_IMM4: case LOAD_STR_CONST:
				case INDIRECT_CALL:
				case DIRECT_CALL:
				case PARM:
				case LOAD:
					if(tac.op0 != null) {
						for(Temp t : tac.liveOut) {
							if(!nodes.contains(t)) {
								nodes.add(t);
								nodeDeg.put(t,0);
								neighbours.put(t, new HashSet<Temp>());
							}
							if(t != tac.op0 && !(neighbours.get(tac.op0).contains(t))) {//不会重联
								addEdge(tac.op0,t);
							}
						}
						if(tac.prev!=null) {
						for(Temp t : tac.prev.liveOut) {
							if(!nodes.contains(t)) {
								nodes.add(t);
								nodeDeg.put(t,0);
								neighbours.put(t, new HashSet<Temp>());
							}
							if(t != tac.op0 && !(neighbours.get(tac.op0).contains(t))) {//不会重联
								addEdge(tac.op0,t);
							}
						}
						}
					}
					break;
				case STORE:
					break;
				case BRANCH: case BEQZ: case BNEZ: case RETURN:
					throw new IllegalArgumentException();
			}
		}
	}
}

