package pippin;

/**
 * @author Eric W. Li
 */
public class Test {
	public static void main(String[] args) {
		System.out.print("Testing checkParity for 3, 5, and 6: ");
		Instruction.checkParity(3);
		Instruction.checkParity(5);
		Instruction.checkParity(6);
		Instruction.checkParity(9);
		Instruction.checkParity(10);
		Instruction.checkParity(12);
		System.out.println("PASSED");
		
		
		System.out.print("Testing checkParity for 8: ");
		Instruction.checkParity(8);
		System.out.println("PASSED");
	}
}
