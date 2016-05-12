package pippin;

/**
 * @author Eric W. Li
 */
public class CodeAccessException extends RuntimeException {
	private String message;
	
	public CodeAccessException() {
		super();
	}
	
	public CodeAccessException(String msg) {
		super(msg);
		this.message = msg;
	}
}
