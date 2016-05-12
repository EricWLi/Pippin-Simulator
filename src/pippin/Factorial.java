package pippin;

/**
 * @author Eric W. Li
 */
public class Factorial {
	public static void main(String[] args) {
		MachineModel test = new MachineModel();
		
		test.setData(0, 8);
		test.setRunning(true);
		int op = 0;
		int arg = 0;
		
		// LOD
		op = 1*8 + 1;
		arg = 0;
		test.setCode(op, arg);
		
		// STO
		op = 2*8 + 1;
		arg = 1;
		test.setCode(op, arg);
		
		// LOD
		op = 1*8 + 1;
		arg = 0;
		test.setCode(op, arg);
		
		// SUB
		op = 6*8 + 1 + 2;
		arg = 1;
		test.setCode(op, arg);
		
		// STO
		op = 2*8 + 1;
		arg = 0;
		test.setCode(op, arg);
		
		// CMPZ
		op = 12*8 + 0 + 0;
		arg = 0;
		test.setCode(op, arg);
		
		// SUB
		op = 6*8 + 2 + 1;
		arg = 1;
		test.setCode(op, arg);
		
		// JMPZ
		op = 4*8 + 1;
		arg = 4;
		test.setCode(op, arg);
		
		// LOD
		op = 1*8 + 1;
		arg = 0;
		test.setCode(op, arg);
		
		// MUL
		op = 7*8 + 1;
		arg = 1;
		test.setCode(op, arg);
		
		// JUMP
		op = 3*8 + 2 +  1;
		arg = 1;
		test.setCode(op, arg);
		
		// HALT
		op = 0xF * 8;
		arg = 0;
		test.setCode(op, arg);
		
		test.setRunning(true);
		int result = 0;
		while(test.isRunning()) {
			if(result != test.getData(1)){
				result = test.getData(1);
				System.out.println("0 => " + test.getData(0) + 
						"; 1 => " + result);
			}
			test.step();
		}
	}
}
