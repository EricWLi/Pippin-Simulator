package pippin;

/**
 * @author Eric W. Li
 */
public class DivideByZeroException extends RuntimeException {
	private String message;
	
	public DivideByZeroException() {
		super();
	}
	
	public DivideByZeroException(String msg) {
		super(msg);
		this.message = msg;
	}
}
