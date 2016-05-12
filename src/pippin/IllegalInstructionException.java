package pippin;

/**
 * @author Eric W. Li
 */
public class IllegalInstructionException extends RuntimeException {
	private String message;
	
	public IllegalInstructionException() {
		super();
	}
	
	public IllegalInstructionException(String msg) {
		super(msg);
		this.message = msg;
	}
}
