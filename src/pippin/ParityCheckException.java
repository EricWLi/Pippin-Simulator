package pippin;

/**
 * @author Eric W. Li
 */
public class ParityCheckException extends RuntimeException {
	private String message;
	
	public ParityCheckException() {
		super();
	}
	
	public ParityCheckException(String msg) {
		super(msg);
		this.message = msg;
	}
}
