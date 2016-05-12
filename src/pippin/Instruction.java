package pippin;

/**
 * @author Eric W. Li
 */
public interface Instruction {
	public void execute(int arg, int flags);
	
	public static int numOnes(int i) {
		/*
		int count = 0;
		String str = Integer.toUnsignedString(k, 2);
		
		for (int i = 0; i < str.length(); ++i)
			if (str.charAt(i) == '1')
				count++;

		return count;
		*/
		
		i = i - ((i >>> 1) & 0x55555555);
		i = (i & 0x33333333) + ((i >>> 2) & 0x33333333);
		return (((i + (i >>> 4)) & 0x0F0F0F0F) * 0x01010101) >>> 24;
	}
	
	public static void checkParity(int k) {
		if (numOnes(k) % 2 == 1)
			throw new ParityCheckException("This instruction is corrupted.");
	}
}
