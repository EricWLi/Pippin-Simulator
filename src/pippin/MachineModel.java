package pippin;

import java.util.Observable;

/**
 * @author Eric W. Li
 */
public class MachineModel extends Observable {
	public Instruction[] INSTRUCTIONS = new Instruction[0x10];
	private CPU cpu = new CPU();
	private Memory memory = new Memory();
	private boolean withGUI = false;
	private Code code = new Code();
	private boolean running = false;
	
	public MachineModel(boolean withGUI) {
		this.withGUI = withGUI;
		
		//INSTRUCTION_MAP entry for "NOP"
		INSTRUCTIONS[0] = (arg, flags) -> {
			flags = flags & 0x6; // remove parity bit that will have been verified
			if(flags != 0) {
				String fString = "(" + (flags%8 > 3?"1":"0") + 
							(flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException(
					"Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;			
		};
		
		//INSTRUCTION entry for LOD (Load Accumulator)
		INSTRUCTIONS[0x1] = (arg, flags) -> {
			flags = flags & 0x6; // remove parity bit that will have been verified
			if (flags == 0) { // direct addressing
				cpu.accum = memory.getData(arg);
			} else if (flags == 2) { // immediate addressing
				cpu.accum = arg;
			} else if (flags == 4) { // indirect addressing
				cpu.accum = memory.getData(memory.getData(arg));
			} else {
				String fString = "(" + (flags%8 > 3?"1":"0") 
						+ (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException(
				"Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;
		};
		
		//INSTRUCTION entry for STO (Store Accumulator in Memory)
		INSTRUCTIONS[0x2] = (arg, flags) -> {
			flags = flags & 0x6; // remove parity bit that will have been verified
			if (flags == 0) { // direct addressing
				memory.setData(arg, cpu.accum);
			} else if (flags == 4) { // indirect addressing
				memory.setData(memory.getData(arg), cpu.accum);
			} else {
				String fString = "(" + (flags%8 > 3?"1":"0") 
						+ (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException(
				"Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;
		};

		//INSTRUCTION entry for JUMP
		INSTRUCTIONS[0x3] = (arg, flags) -> {
			flags = flags & 0x6; // remove parity bit that will have been verified
			if (flags == 0) { // direct addressing
				cpu.pc += arg;
			} else if (flags == 2) { // immediate addressing
				cpu.pc = arg;
			} else if (flags == 4) { // indirect addressing
				cpu.pc += memory.getData(arg);
			} else if (flags == 6) {
				cpu.pc = memory.getData(arg);
			}
		};
		
		//INSTRUCTION entry for JMPZ
		INSTRUCTIONS[0x4] = (arg, flags) -> {
			flags = flags & 0x6; // remove parity bit that will have been verified
			if (cpu.accum == 0) {
				if (flags == 0) { // direct addressing
					cpu.pc += arg;
				} else if (flags == 2) { // immediate addressing
					cpu.pc = arg;
				} else if (flags == 4) { // indirect addressing
					cpu.pc += memory.getData(arg);
				} else if (flags == 6) {
					cpu.pc = memory.getData(arg);
				}
			} else {
				cpu.pc++;
			}
		};
		
		//INSTRUCTION entry for ADD (add)
		INSTRUCTIONS[0x5] = (arg, flags) -> {
			flags = flags & 0x6; // remove parity bit that will have been verified
			if(flags == 0) { // direct addressing
				cpu.accum += memory.getData(arg);
			} else if(flags == 2) { // immediate addressing
				cpu.accum += arg;
			} else if(flags == 4) { // indirect addressing
				cpu.accum += memory.getData(memory.getData(arg));				
			} else { // here the illegal case is "11"
				String fString = "(" + (flags%8 > 3?"1":"0") 
							+ (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException(
					"Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;			
		};
		
		//INSTRUCTION entry for SUB (subtraction)
		INSTRUCTIONS[0x6] = (arg, flags) -> {
			flags = flags & 0x6; // remove parity bit that will have been verified
			if(flags == 0) { // direct addressing
				cpu.accum -= memory.getData(arg);
			} else if(flags == 2) { // immediate addressing
				cpu.accum -= arg;
			} else if(flags == 4) { // indirect addressing
				cpu.accum -= memory.getData(memory.getData(arg));				
			} else { // here the illegal case is "11"
				String fString = "(" + (flags%8 > 3?"1":"0") 
							+ (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException(
					"Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;			
		};
		
		//INSTRUCTION entry for MUL (multiplication)
		INSTRUCTIONS[0x7] = (arg, flags) -> {
			flags = flags & 0x6; // remove parity bit that will have been verified
			if(flags == 0) { // direct addressing
				cpu.accum *= memory.getData(arg);
			} else if(flags == 2) { // immediate addressing
				cpu.accum *= arg;
			} else if(flags == 4) { // indirect addressing
				cpu.accum *= memory.getData(memory.getData(arg));				
			} else { // here the illegal case is "11"
				String fString = "(" + (flags%8 > 3?"1":"0") 
							+ (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException(
					"Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;			
		};
		
		//INSTRUCTION entry for DIV (division)
		INSTRUCTIONS[0x8] = (arg, flags) -> {
			flags = flags & 0x6; // remove parity bit that will have been verified
			if(flags == 0) { // direct addressing
				if (memory.getData(arg) == 0)
					throw new DivideByZeroException();
				cpu.accum /= memory.getData(arg);
			} else if(flags == 2) { // immediate addressing
				if (arg == 0)
					throw new DivideByZeroException();
				cpu.accum /= arg;
			} else if(flags == 4) { // indirect addressing
				if (memory.getData(memory.getData(arg)) == 0)
					throw new DivideByZeroException();
				cpu.accum /= memory.getData(memory.getData(arg));				
			} else { // here the illegal case is "11"
				String fString = "(" + (flags%8 > 3?"1":"0") 
							+ (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException(
					"Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;			
		};
		
		//INSTRUCTION entry for AND
		INSTRUCTIONS[0x9] = (arg, flags) -> {
			flags = flags & 0x6; // remove parity bit that will have been verified
			if(flags == 0) { // direct addressing
				if (cpu.accum != 0 && memory.getData(arg) != 0)
					cpu.accum = 1;
				else
					cpu.accum = 0;
			} else if(flags == 2) { // immediate addressing
				if (cpu.accum != 0 && arg != 0)
					cpu.accum = 1;
				else
					cpu.accum = 0;
			} else {
				String fString = "(" + (flags%8 > 3?"1":"0") + 
						(flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException(
				"Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;			
		};
		
		//INSTRUCTION_MAP entry for "NOT"
		INSTRUCTIONS[0xA] = (arg, flags) -> {
			flags = flags & 0x6; // remove parity bit that will have been verified
			if(flags != 0) {
				String fString = "(" + (flags%8 > 3?"1":"0") + 
							(flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException(
					"Illegal flags for this instruction: " + fString);
			} else {
				if (cpu.accum == 0)
					cpu.accum = 1;
				else
					cpu.accum = 0;
			}
			cpu.pc++;			
		};
		
		//INSTRUCTION_MAP entry for "CMPL" (Compare less than 0)
		INSTRUCTIONS[0xB] = (arg, flags) -> {
			flags = flags & 0x6; // remove parity bit that will have been verified
			if(flags != 0) {
				String fString = "(" + (flags%8 > 3?"1":"0") + 
							(flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException(
					"Illegal flags for this instruction: " + fString);
			} else {
				if (memory.getData(arg) < 0)
					cpu.accum = 1;
				else
					cpu.accum = 0;
			}
			cpu.pc++;			
		};
		
		//INSTRUCTION_MAP entry for "CMPZ" (Compare to zero)
		INSTRUCTIONS[0xC] = (arg, flags) -> {
			flags = flags & 0x6; // remove parity bit that will have been verified
			if(flags != 0) {
				String fString = "(" + (flags%8 > 3?"1":"0") + 
							(flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException(
					"Illegal flags for this instruction: " + fString);
			} else {
				if (memory.getData(arg) == 0)
					cpu.accum = 1;
				else
					cpu.accum = 0;
			}
			cpu.pc++;			
		};
		
		//INSTRUCTION_MAP entry for "FOR" (For loop)
		INSTRUCTIONS[0xD] = (arg, flags) -> {
			flags = flags & 0x6; // remove parity bit that will have been verified
			int instr;
			int iterations;
			if(flags == 0) { // direct addressing
				instr = getData(arg) / 0x1000;
				iterations = getData(arg) % 0x1000;
			} else if(flags == 2) { // immediate addressing
				instr = arg / 0x1000;
				iterations = arg % 0x1000;
			} else {
				String fString = "(" + (flags%8 > 3?"1":"0") + 
						(flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException(
				"Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;
			int forPC = cpu.pc;
			for (int i = 0; i < iterations; i++) {
				cpu.pc = forPC;
				for (int j = 0; j < instr; j++) {
					step();
				}
			}
			
		};
		
		//INSTRUCTION_MAP entry for "HALT" (Halt execution)
		INSTRUCTIONS[0xF] = (arg, flags) -> {
			flags = flags & 0x6; // remove parity bit that will have been verified
			if(flags != 0) {
				String fString = "(" + (flags%8 > 3?"1":"0") + 
							(flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException(
					"Illegal flags for this instruction: " + fString);
			} else {
				halt();
			}		
		};
	}
	
	public MachineModel() {
		this(false);
	}
	
	public void halt() {
		if (!withGUI)
			System.exit(0);
		else
			running = false;
	}
	
	public void setData(int i, int j) {
		memory.setData(i, j);		
	}
	public Instruction get(int i) {
		return INSTRUCTIONS[i];
	}
	int[] getData() {
		return memory.getData();
	}
	public int getPC() {
		return cpu.pc;
	}
	public int getAccum() {
		return cpu.accum;
	}
	public void setAccum(int i) {
		cpu.accum = i;
	}
	public void setPC(int i) {
		cpu.pc = i;
	}
	public boolean isRunning() {
		return running;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public int getData(int i) {
		return memory.getData(i);
	}
	
	public Code getCode() {
		return code;
	}
	
	public int getChangedIndex() {
		return memory.getChangedIndex();
	}
	
	public void setCode(int op, int arg) {
		code.setCode(op, arg);
	}
	
	public void clear() {
		memory.clear();
		code.clear();
		cpu.accum = 0;
		cpu.pc = 0;
	}
	
	public void step() {
		try {
			int opPart = code.getOpPart(cpu.pc);
			int arg = code.getArg(cpu.pc);
			Instruction.checkParity(opPart);
			INSTRUCTIONS[opPart/8].execute(arg, opPart%8);
		} catch (Exception e) {
			halt();
			throw e;
		}
	}
	
	private class CPU {
		private int accum;
		private int pc;
	}
}

