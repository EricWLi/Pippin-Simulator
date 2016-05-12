package pippin;

/**
 * @author Eric W. Li
 */
public class Test1 {
	public static String make32Bits (String binaryForm) {
		binaryForm = "00000000000000000000000000000000" + binaryForm;
		return binaryForm.substring(binaryForm.length() - 32);		
	}
	
	public static String make64Bits (String binaryForm) {
		binaryForm = "00000000000000000000000000000000" + 
					 "00000000000000000000000000000000" + binaryForm;
		return binaryForm.substring(binaryForm.length() - 64);		
	}
	
	public static void main(String[] args) {
		int op = 7*8 + 4;
		int arg = 9;

		System.out.println(make32Bits(Integer.toUnsignedString(op,2)));
		System.out.println(make32Bits(Integer.toUnsignedString(arg,2)));
		long k = Code.join(op, arg);
		System.out.println(make64Bits(Long.toUnsignedString(k, 2)));

		op = 5*8 + 3;
		arg = -9;

		System.out.println(make32Bits(Integer.toUnsignedString(op,2)));
		System.out.println(make32Bits(Integer.toUnsignedString(arg,2)));
		k = Code.join(op, arg);
		System.out.println(make64Bits(Long.toUnsignedString(k, 2)));		
	}
}