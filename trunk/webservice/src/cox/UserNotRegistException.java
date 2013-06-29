package cox;

public class UserNotRegistException extends Exception {
	public UserNotRegistException()  {}
	public UserNotRegistException(String message) {
		super(message);
	}
}
